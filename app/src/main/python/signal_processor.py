import numpy as np
import matplotlib.pyplot as plt
import soundfile as sf
from scipy.signal import find_peaks, firwin
import numba

def voice_segmentation(data,fs):    

    print("RecordingAnalyze voice_segmentation: Started processing")

    # Slow autocorrelation
    # N = 2048
    # x = data[:2*N]
    # lags = np.array(range(0, N-1))
    # r_xx = np.zeros_like(lags).astype(float)
    # for lag in lags:
    #     tmp_sum = 0
    #     for n in range(N):
    #         if ((n+lag)<N) and ((n+lag)>=0):
    #             tmp_sum = tmp_sum + x[n]*x[n+lag]
    #     r_xx[lag] = tmp_sum

    # Fast autocorrelation
    N = 2*2048
    x = data[:N]
    r_xx = np.zeros((2*N)).astype(float)

    x_ = np.zeros((2*N))
    x_[:N] = x
    X = np.fft.fft(x_)
    r_xx = np.real(np.fft.ifft(X*np.conj(X)))
    r_xx = r_xx[:N]

    print("RecordingAnalyze voice_segmentation: Computed r_xx")

    peaks, _ = find_peaks(r_xx)
    
    print("RecordingAnalyze voice_segmentation: Found peaks")

    max_peak = peaks[0]
    for i in range(1, len(peaks)):
        if r_xx[peaks[i]] > r_xx[max_peak]:
            max_peak = peaks[i]
            
    print("RecordingAnalyze voice_segmentation: Found max peaks")

    nyq = 0.5*fs
    T0 = max_peak
    T0 = T0/fs      # seconds
    F0 = 1.0/(T0)
    cutoff = 1.5*F0/nyq
    N_filter = 401

    taps = firwin(N_filter, cutoff, fs=fs, window=('chebwin',120))

    filtered_signal = np.convolve(data, taps, mode='full')
    filtered_signal = filtered_signal[(N_filter-1)//2:]

    zero_crossings = np.where(np.diff(np.sign(filtered_signal[:len(data)])))[0]

    positive_zero_crossings_left = []
    
    print("RecordingAnalyze voice_segmentation: Filtered signal")

    for i in zero_crossings:
        if (filtered_signal[i]<0) and (filtered_signal[i+1]>0):
            positive_zero_crossings_left.append(i)
            
    print("RecordingAnalyze voice_segmentation: Processed segments")

    return positive_zero_crossings_left

def perturbation_L(data, L):
    """ Calculation of period perturbation quotient (PPQ) for data.
    L -- size of sliding window for estimating current averaged value (must be odd, e.g. 1,3,5 and etc.).
    data -- should have type numpy.array
    """
    
    data_ave = np.mean(data)
    N = data.shape[0]
    if L==1:
        deltas = np.zeros((N-1))
        deltas = np.abs(data[0:-1] - data[1:])
    else:
        deltas = np.zeros((N-(L-1)))
        for i in range((L-1)//2, N - (L-1)//2):
            data_curr_ave = np.mean(data[i - (L-1)//2 : i + (L-1)//2 + 1])
            deltas[i - (L-1)//2] = np.abs(data[i] - data_curr_ave)

    deltas_ave = np.mean(deltas)

    PPQ_L = deltas_ave/data_ave * 100

    return PPQ_L

def voice_parameters(data, segments):
    print("RecordingAnalyze voice_parameters: Start processing")

    periods = np.array(segments[1:]) - np.array(segments[:-1])
    amplitudes = np.zeros(len(periods), dtype=np.float64)

    for i in range(len(segments)-1):
        start_idx = segments[i]
        end_idx = segments[i+1]

        # Нахождение минимального и максимального значения амплитуды в периоде
        min_amplitude,max_amplitude = minmax_np(data[start_idx:end_idx])

        # Вычисление амплитуды в периоде
        amplitudes[i] = max_amplitude - min_amplitude          
        

    J1 = perturbation_L(periods, 1)    
    
    J3 = perturbation_L(periods, 3)
    
    J5 = perturbation_L(periods, 5)

    S1 = perturbation_L(amplitudes, 1)
    
    S3 = perturbation_L(amplitudes, 3)
    
    S5 = perturbation_L(amplitudes, 5)
    
    S11 = perturbation_L(amplitudes, 11)    

    print("RecordingAnalyze voice_parameters: Finish processing")
    return J1, J3, J5, S1, S3, S5, S11
  
@numba.jit
def minmax(x):
    maximum = x[0]
    minimum = x[0]
    for i in x[1:]:
        if i > maximum:
            maximum = i
        elif i < minimum:
            minimum = i
    return (minimum, maximum)

def minmax_np(x):
    maximum = np.max(x)
    minimum = np.min(x)            
    return (minimum, maximum)