#include <jni.h>

#include <algorithm>
#include <array>
#include <chrono>
#include <cmath>
#include <complex>
#include <limits>
#include <numeric>
#include <stdexcept>
#include <string>
#include <utility>
#include <vector>

namespace {

// These constants mirror the Python implementation so the JNI path stays numerically aligned
// with the reference engine used in the hidden benchmark screen.
constexpr double kPi = 3.141592653589793238462643383279502884;
constexpr int kMetricCount = 9;
constexpr double kWindowBeta = 8.0;
constexpr double kPerc = 0.05;
constexpr int kMaxIterations = 50;

struct StageTiming {
    std::string label;
    jlong nanos;
};

struct SignalProcessorOutput {
    std::array<float, kMetricCount> metrics{};
    std::vector<StageTiming> stage_timings;
    jlong total_nanos = 0;
};

struct VoiceSegmentationOutput {
    std::vector<int> zero_crossings;
    std::vector<double> filtered_signal;
};

int sign(double value) {
    if (value > 0.0) {
        return 1;
    }
    if (value < 0.0) {
        return -1;
    }
    return 0;
}

double i0(double x) {
    const double ax = std::abs(x);
    if (ax < 3.75) {
        const double y = x / 3.75;
        const double y2 = y * y;
        return 1.0 + y2 * (3.5156229 + y2 * (3.0899424 + y2 * (1.2067492 +
                y2 * (0.2659732 + y2 * (0.0360768 + y2 * 0.0045813)))));
    }

    const double y = 3.75 / ax;
    return (std::exp(ax) / std::sqrt(ax)) * (0.39894228 + y * (0.01328592 +
            y * (0.00225319 + y * (-0.00157565 + y * (0.00916281 +
            y * (-0.02057706 + y * (0.02635537 + y * (-0.01647633 +
            y * 0.00392377))))))));
}

// Minimal radix-2 Cooley-Tukey FFT used for the autocorrelation stage.
// We keep it local to avoid pulling a heavier DSP dependency into the JNI library.
void fft(std::vector<std::complex<double>>& values, bool inverse) {
    const std::size_t n = values.size();
    if (n == 0 || (n & (n - 1)) != 0U) {
        throw std::invalid_argument("FFT size must be a power of two");
    }

    for (std::size_t i = 1U, j = 0U; i < n; ++i) {
        std::size_t bit = n >> 1U;
        while ((j & bit) != 0U) {
            j ^= bit;
            bit >>= 1U;
        }
        j ^= bit;
        if (i < j) {
            std::swap(values[i], values[j]);
        }
    }

    for (std::size_t len = 2U; len <= n; len <<= 1U) {
        const double angle = 2.0 * kPi / static_cast<double>(len) * (inverse ? -1.0 : 1.0);
        const std::complex<double> wlen(std::cos(angle), std::sin(angle));

        for (std::size_t i = 0U; i < n; i += len) {
            std::complex<double> w(1.0, 0.0);
            const std::size_t half = len >> 1U;
            for (std::size_t j = 0U; j < half; ++j) {
                const std::complex<double> u = values[i + j];
                const std::complex<double> v = values[i + j + half] * w;
                values[i + j] = u + v;
                values[i + j + half] = u - v;
                w *= wlen;
            }
        }
    }

    if (inverse) {
        const double scale = 1.0 / static_cast<double>(n);
        for (auto& value : values) {
            value *= scale;
        }
    }
}

// The Python code uses FFT-based autocorrelation to estimate the dominant pitch period.
// Zero-padding to 2 * n matches the circular-convolution trick used there.
std::vector<double> autocorrelate(const std::vector<double>& samples, int count) {
    const int n = std::min<int>(count, static_cast<int>(samples.size()));
    if (n <= 1) {
        throw std::invalid_argument("Not enough samples for autocorrelation");
    }

    std::vector<std::complex<double>> padded(static_cast<std::size_t>(n * 2), std::complex<double>(0.0, 0.0));
    for (int index = 0; index < n; ++index) {
        padded[static_cast<std::size_t>(index)] = std::complex<double>(samples[static_cast<std::size_t>(index)], 0.0);
    }

    fft(padded, false);
    for (auto& value : padded) {
        value *= std::conj(value);
    }
    fft(padded, true);

    std::vector<double> result(static_cast<std::size_t>(n));
    for (int index = 0; index < n; ++index) {
        result[static_cast<std::size_t>(index)] = padded[static_cast<std::size_t>(index)].real();
    }

    return result;
}

// Equivalent to scipy.signal.find_peaks for the simple "strict local maximum" behavior
// used by the original script.
std::vector<int> find_peaks(const std::vector<double>& signal) {
    std::vector<int> peaks;
    if (signal.size() < 3U) {
        return peaks;
    }

    for (std::size_t index = 1U; index + 1U < signal.size(); ++index) {
        if (signal[index] > signal[index - 1U] && signal[index] > signal[index + 1U]) {
            peaks.push_back(static_cast<int>(index));
        }
    }

    return peaks;
}

// Reimplementation of scipy.signal.firwin(..., window=("kaiser", beta)).
// This low-pass filter smooths the waveform before the zero-crossing search.
std::vector<double> firwin_lowpass(int num_taps, double cutoff, double sample_rate, double beta) {
    if (num_taps < 2) {
        throw std::invalid_argument("Filter must have at least two taps");
    }

    double normalized_cutoff = cutoff / (sample_rate * 0.5);
    normalized_cutoff = std::clamp(normalized_cutoff, 1e-12, 0.999999);

    const double alpha = 0.5 * static_cast<double>(num_taps - 1);
    const double denominator = i0(beta);
    std::vector<double> taps(static_cast<std::size_t>(num_taps));

    for (int tap = 0; tap < num_taps; ++tap) {
        const double m = static_cast<double>(tap) - alpha;
        const double ratio = alpha == 0.0 ? 0.0 : m / alpha;
        const double window = i0(beta * std::sqrt(std::max(0.0, 1.0 - ratio * ratio))) / denominator;
        const double sinc = normalized_cutoff * (m == 0.0
                ? 1.0
                : std::sin(kPi * normalized_cutoff * m) / (kPi * normalized_cutoff * m));
        taps[static_cast<std::size_t>(tap)] = sinc * window;
    }

    const double scale = std::accumulate(taps.begin(), taps.end(), 0.0);
    if (scale != 0.0) {
        for (auto& tap : taps) {
            tap /= scale;
        }
    }

    return taps;
}

std::vector<double> convolve_full(const std::vector<double>& signal, const std::vector<double>& kernel) {
    std::vector<double> output(signal.size() + kernel.size() - 1U, 0.0);
    for (std::size_t signal_index = 0U; signal_index < signal.size(); ++signal_index) {
        const double sample = signal[signal_index];
        for (std::size_t kernel_index = 0U; kernel_index < kernel.size(); ++kernel_index) {
            output[signal_index + kernel_index] += sample * kernel[kernel_index];
        }
    }
    return output;
}

// Stage 1: derive coarse glottal-cycle boundaries.
// 1. Estimate the fundamental period from the autocorrelation peak.
// 2. Build a low-pass filter tuned to that period.
// 3. Use downward zero crossings of the filtered signal as rough cycle boundaries.
VoiceSegmentationOutput voice_segmentation(const std::vector<double>& samples, int sample_rate) {
    const std::vector<double> rxx = autocorrelate(samples, std::min<int>(4096, static_cast<int>(samples.size())));
    std::vector<int> peaks = find_peaks(rxx);

    int max_peak = 1;
    if (!peaks.empty()) {
        max_peak = peaks.front();
        for (std::size_t index = 1U; index < peaks.size(); ++index) {
            if (rxx[static_cast<std::size_t>(peaks[index])] > rxx[static_cast<std::size_t>(max_peak)]) {
                max_peak = peaks[index];
            }
        }
    } else {
        max_peak = static_cast<int>(std::distance(
            rxx.begin() + 1,
            std::max_element(rxx.begin() + 1, rxx.end())
        ));
        max_peak += 1;
    }

    max_peak = std::max(max_peak, 1);

    const double nyquist = 0.5 * static_cast<double>(sample_rate);
    const double fundamental = static_cast<double>(sample_rate) / static_cast<double>(max_peak);
    // Keep the same cutoff math as the Python code, even though it looks unusual,
    // to preserve output parity between both implementations.
    const double cutoff = 1.8 * fundamental / nyquist;
    int filter_size = static_cast<int>(std::llround(static_cast<double>(max_peak)));
    filter_size -= filter_size % 2;
    if (filter_size < 2) {
        filter_size = 2;
    }

    const std::vector<double> taps = firwin_lowpass(filter_size, cutoff, static_cast<double>(sample_rate), kWindowBeta);
    std::vector<double> filtered = convolve_full(samples, taps);
    const int shift = (filter_size - 1) / 2;
    filtered.erase(filtered.begin(), filtered.begin() + shift);

    std::vector<int> zero_crossings;
    const std::size_t signal_size = samples.size();
    if (filtered.size() < signal_size) {
        filtered.resize(signal_size, 0.0);
    }

    int previous_sign = sign(filtered[0]);
    for (std::size_t index = 1U; index < signal_size; ++index) {
        const int current_sign = sign(filtered[index]);
        if ((current_sign - previous_sign) < 0) {
            zero_crossings.push_back(static_cast<int>(index));
        }
        previous_sign = current_sign;
    }

    VoiceSegmentationOutput output;
    output.zero_crossings = std::move(zero_crossings);
    output.filtered_signal = std::move(filtered);
    return output;
}

// Compare two adjacent cycles by mean squared error. WM refinement searches for the next
// peak location that makes consecutive cycles look as similar as possible.
double err(int candidate, const std::vector<int>& peaks, int peak_index, const std::vector<double>& buffer) {
    if (candidate < 0 || candidate >= static_cast<int>(buffer.size())) {
        return std::numeric_limits<double>::infinity();
    }

    const int start_index = peaks[static_cast<std::size_t>(peak_index - 1)];
    const int length = candidate - start_index;
    if (length <= 0 || candidate + length > static_cast<int>(buffer.size())) {
        return std::numeric_limits<double>::infinity();
    }

    double error_sum = 0.0;
    for (int offset = 0; offset < length; ++offset) {
        const double difference = buffer[static_cast<std::size_t>(candidate + offset)] -
                buffer[static_cast<std::size_t>(start_index + offset)];
        error_sum += difference * difference;
    }

    return error_sum / static_cast<double>(length);
}

// Stage 2: refine cycle peaks with the WM method.
// The rough zero crossings give us approximate cycle lengths; this step slides each next peak
// inside a local window and picks the position that minimizes cycle-to-cycle mismatch.
std::pair<std::vector<double>, std::vector<int>> wm_method(
    const std::vector<double>& samples,
    const std::vector<double>& filtered_signal,
    int sample_rate,
    const std::vector<int>& zero_crossings
) {
    const int cycle_count = static_cast<int>(zero_crossings.size());
    if (cycle_count < 3) {
        return { {}, {} };
    }

    std::vector<double> frequencies(static_cast<std::size_t>(cycle_count - 3), 0.0);
    std::vector<int> peaks(static_cast<std::size_t>(cycle_count - 2), 0);

    const int first_start = zero_crossings[0];
    const int first_end = zero_crossings[1];
    auto min_it = std::min_element(
        filtered_signal.begin() + first_start,
        filtered_signal.begin() + first_end
    );
    peaks[0] = static_cast<int>(std::distance(filtered_signal.begin(), min_it));

    for (int index = 1; index < cycle_count - 2; ++index) {
        peaks[static_cast<std::size_t>(index)] = zero_crossings[static_cast<std::size_t>(index)] +
                (peaks[static_cast<std::size_t>(index - 1)] - zero_crossings[static_cast<std::size_t>(index - 1)]);

        const int radius = static_cast<int>(std::llround(
            kPerc * static_cast<double>(
                peaks[static_cast<std::size_t>(index)] - peaks[static_cast<std::size_t>(index - 1)]
            )
        ));
        int search_start = std::max(0, peaks[static_cast<std::size_t>(index)] - radius);
        int search_end = std::min(static_cast<int>(samples.size()) - 1, peaks[static_cast<std::size_t>(index)] + radius);

        bool search_complete = false;
        int iterations = 0;
        int best_candidate = search_start;

        while (!search_complete && iterations < kMaxIterations) {
            ++iterations;
            double minimum_error = std::numeric_limits<double>::infinity();

            for (int candidate = search_start; candidate <= search_end; ++candidate) {
                const double current_error = err(candidate, peaks, index, samples);
                if (current_error < minimum_error) {
                    minimum_error = current_error;
                    best_candidate = candidate;
                }
            }

            if (best_candidate == search_start) {
                const int expansion = std::max(
                    1,
                    static_cast<int>(std::llround(0.5 * static_cast<double>(search_end - search_start)))
                );
                search_start = std::max(0, search_start - expansion);
            } else if (best_candidate == search_end) {
                const int expansion = std::max(
                    1,
                    static_cast<int>(std::llround(0.5 * static_cast<double>(search_end - search_start)))
                );
                search_end = std::min(static_cast<int>(samples.size()) - 1, search_end + expansion);
            } else {
                search_complete = true;
            }
        }

        if (iterations >= kMaxIterations) {
            best_candidate = (search_start + search_end) / 2;
        }

        peaks[static_cast<std::size_t>(index)] = best_candidate;

        // Quadratic interpolation gives a sub-sample correction around the best discrete candidate.
        const double error_plus = err(best_candidate + 1, peaks, index, samples);
        const double error_minus = err(best_candidate - 1, peaks, index, samples);
        const double error_current = err(best_candidate, peaks, index, samples);
        const double denominator = error_plus - 2.0 * error_current + error_minus;
        const double delta = denominator != 0.0
                ? -0.5 * ((error_plus - error_minus) / denominator)
                : 0.0;

        frequencies[static_cast<std::size_t>(index - 1)] = static_cast<double>(sample_rate) /
                (static_cast<double>(peaks[static_cast<std::size_t>(index)] - peaks[static_cast<std::size_t>(index - 1)]) + delta);
    }

    return { std::move(frequencies), std::move(peaks) };
}

double mean(const std::vector<double>& values) {
    if (values.empty()) {
        return 0.0;
    }
    return std::accumulate(values.begin(), values.end(), 0.0) / static_cast<double>(values.size());
}

double stddev(const std::vector<double>& values, double average) {
    if (values.empty()) {
        return 0.0;
    }

    double variance = 0.0;
    for (const double value : values) {
        const double diff = value - average;
        variance += diff * diff;
    }

    return std::sqrt(variance / static_cast<double>(values.size()));
}

std::pair<double, double> minmax(const std::vector<double>& values, int start_index, int end_index) {
    double minimum = values[static_cast<std::size_t>(start_index)];
    double maximum = minimum;

    for (int index = start_index + 1; index < end_index; ++index) {
        const double value = values[static_cast<std::size_t>(index)];
        minimum = std::min(minimum, value);
        maximum = std::max(maximum, value);
    }

    return { minimum, maximum };
}

// Shared perturbation metric used for both jitter and shimmer families.
// window_size == 1 computes local frame-to-frame deviation; wider windows reproduce the
// RAP/PPQ/APQ variants from the Python implementation.
double perturbation_l(const std::vector<double>& values, int window_size) {
    if (values.empty()) {
        return 0.0;
    }

    const double average = mean(values);
    if (average == 0.0) {
        return 0.0;
    }

    std::vector<double> deltas;
    if (window_size == 1) {
        if (values.size() < 2U) {
            return 0.0;
        }
        deltas.reserve(values.size() - 1U);
        for (std::size_t index = 0U; index + 1U < values.size(); ++index) {
            deltas.push_back(std::abs(values[index] - values[index + 1U]));
        }
    } else {
        const int half_window = (window_size - 1) / 2;
        if (static_cast<int>(values.size()) < window_size) {
            return 0.0;
        }

        deltas.reserve(values.size() - static_cast<std::size_t>(window_size - 1));
        for (int index = half_window; index < static_cast<int>(values.size()) - half_window; ++index) {
            double current_average = 0.0;
            for (int offset = index - half_window; offset <= index + half_window; ++offset) {
                current_average += values[static_cast<std::size_t>(offset)];
            }
            current_average /= static_cast<double>(window_size);
            deltas.push_back(std::abs(values[static_cast<std::size_t>(index)] - current_average));
        }
    }

    return mean(deltas) / average * 100.0;
}

// Stage 3: derive the final voice metrics from refined cycles.
// Period lengths feed the jitter metrics, cycle amplitudes feed the shimmer metrics,
// and the WM frequencies feed mean F0 and F0 standard deviation.
std::array<float, kMetricCount> voice_parameters(
    const std::vector<double>& samples,
    const std::vector<int>& segments,
    const std::vector<double>& frequencies
) {
    if (segments.size() < 2U) {
        throw std::invalid_argument("Not enough segments for parameter calculation");
    }

    std::vector<double> periods;
    periods.reserve(segments.size() - 1U);

    std::vector<double> amplitudes;
    amplitudes.reserve(segments.size() - 1U);

    for (std::size_t index = 0U; index + 1U < segments.size(); ++index) {
        const int start_index = segments[index];
        const int end_index = segments[index + 1U];
        if (start_index < 0 || end_index > static_cast<int>(samples.size()) || end_index <= start_index) {
            continue;
        }

        periods.push_back(static_cast<double>(end_index - start_index));
        const auto [minimum, maximum] = minmax(samples, start_index, end_index);
        amplitudes.push_back(maximum - minimum);
    }

    const double f0_mean = mean(frequencies);
    const double f0_sd = stddev(frequencies, f0_mean);

    return {
        static_cast<float>(perturbation_l(periods, 1)),
        static_cast<float>(perturbation_l(periods, 3)),
        static_cast<float>(perturbation_l(periods, 5)),
        static_cast<float>(perturbation_l(amplitudes, 1)),
        static_cast<float>(perturbation_l(amplitudes, 3)),
        static_cast<float>(perturbation_l(amplitudes, 5)),
        static_cast<float>(perturbation_l(amplitudes, 11)),
        static_cast<float>(f0_mean),
        static_cast<float>(f0_sd),
    };
}

// Top-level native pipeline: segmentation -> WM refinement -> perturbation metrics.
// The benchmark screen calls the timed path, while production uses the same logic without timing.
SignalProcessorOutput analyze_samples(const std::vector<double>& samples, int sample_rate, bool capture_timings) {
    SignalProcessorOutput output;
    const auto total_started_at = std::chrono::steady_clock::now();

    const auto segmentation_started_at = std::chrono::steady_clock::now();
    const VoiceSegmentationOutput segmentation = voice_segmentation(samples, sample_rate);
    const auto segmentation_finished_at = std::chrono::steady_clock::now();

    if (capture_timings) {
        output.stage_timings.push_back({
            "voice_segmentation",
            std::chrono::duration_cast<std::chrono::nanoseconds>(
                segmentation_finished_at - segmentation_started_at
            ).count()
        });
    }

    const auto wm_started_at = std::chrono::steady_clock::now();
    const auto [frequencies, peaks] = wm_method(
        samples,
        segmentation.filtered_signal,
        sample_rate,
        segmentation.zero_crossings
    );
    const auto wm_finished_at = std::chrono::steady_clock::now();

    if (capture_timings) {
        output.stage_timings.push_back({
            "wm_method",
            std::chrono::duration_cast<std::chrono::nanoseconds>(wm_finished_at - wm_started_at).count()
        });
    }

    const auto params_started_at = std::chrono::steady_clock::now();
    output.metrics = voice_parameters(samples, peaks, frequencies);
    const auto params_finished_at = std::chrono::steady_clock::now();

    if (capture_timings) {
        output.stage_timings.push_back({
            "voice_parameters",
            std::chrono::duration_cast<std::chrono::nanoseconds>(
                params_finished_at - params_started_at
            ).count()
        });
    }

    output.total_nanos = std::chrono::duration_cast<std::chrono::nanoseconds>(
        std::chrono::steady_clock::now() - total_started_at
    ).count();

    return output;
}

// JNI boundary helpers keep the algorithm code working with plain std::vector buffers.
std::vector<double> read_samples(JNIEnv* env, jshortArray samples_array) {
    const jsize length = env->GetArrayLength(samples_array);
    std::vector<jshort> shorts(static_cast<std::size_t>(length));
    env->GetShortArrayRegion(samples_array, 0, length, shorts.data());

    std::vector<double> samples(static_cast<std::size_t>(length), 0.0);
    for (jsize index = 0; index < length; ++index) {
        samples[static_cast<std::size_t>(index)] = static_cast<double>(shorts[static_cast<std::size_t>(index)]);
    }

    return samples;
}

jfloatArray make_metrics_array(JNIEnv* env, const std::array<float, kMetricCount>& metrics) {
    jfloatArray result = env->NewFloatArray(kMetricCount);
    env->SetFloatArrayRegion(result, 0, kMetricCount, metrics.data());
    return result;
}

jobject make_payload(JNIEnv* env, const SignalProcessorOutput& output) {
    jclass payload_class = env->FindClass("com/danilovfa/presentation/analysis/processing/NativeAnalysisPayload");
    jmethodID constructor = env->GetMethodID(payload_class, "<init>", "([FJ[Ljava/lang/String;[J)V");

    jfloatArray metrics_array = make_metrics_array(env, output.metrics);
    jobjectArray stage_labels = env->NewObjectArray(
        static_cast<jsize>(output.stage_timings.size()),
        env->FindClass("java/lang/String"),
        nullptr
    );
    jlongArray stage_nanos = env->NewLongArray(static_cast<jsize>(output.stage_timings.size()));

    std::vector<jlong> stage_values(output.stage_timings.size(), 0L);
    for (std::size_t index = 0U; index < output.stage_timings.size(); ++index) {
        env->SetObjectArrayElement(
            stage_labels,
            static_cast<jsize>(index),
            env->NewStringUTF(output.stage_timings[index].label.c_str())
        );
        stage_values[index] = output.stage_timings[index].nanos;
    }
    env->SetLongArrayRegion(
        stage_nanos,
        0,
        static_cast<jsize>(stage_values.size()),
        stage_values.data()
    );

    return env->NewObject(payload_class, constructor, metrics_array, output.total_nanos, stage_labels, stage_nanos);
}

void throw_illegal_argument(JNIEnv* env, const std::string& message) {
    jclass exception_class = env->FindClass("java/lang/IllegalArgumentException");
    env->ThrowNew(exception_class, message.c_str());
}

}  // namespace

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_danilovfa_presentation_analysis_processing_NativeSignalProcessorBridge_nativeAnalyze(
    JNIEnv* env,
    jobject /* this */,
    jshortArray samples_array,
    jint sample_rate
) {
    try {
        const std::vector<double> samples = read_samples(env, samples_array);
        const SignalProcessorOutput output = analyze_samples(samples, sample_rate, false);
        return make_metrics_array(env, output.metrics);
    } catch (const std::exception& exception) {
        throw_illegal_argument(env, exception.what());
        return nullptr;
    }
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_danilovfa_presentation_analysis_processing_NativeSignalProcessorBridge_nativeAnalyzeDetailed(
    JNIEnv* env,
    jobject /* this */,
    jshortArray samples_array,
    jint sample_rate
) {
    try {
        const std::vector<double> samples = read_samples(env, samples_array);
        const SignalProcessorOutput output = analyze_samples(samples, sample_rate, true);
        return make_payload(env, output);
    } catch (const std::exception& exception) {
        throw_illegal_argument(env, exception.what());
        return nullptr;
    }
}
