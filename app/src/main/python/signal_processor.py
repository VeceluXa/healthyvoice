import numpy as np
from scipy.signal import find_peaks, firwin

def voice_segmentation(data, fs):
    signal = np.asarray(data, dtype=np.float64)
    N = 2 * 2048
    x = signal[:N]
    x_ = np.zeros((2 * N), dtype=np.float64)
    x_[: x.shape[0]] = x
    X = np.fft.fft(x_)
    r_xx = np.real(np.fft.ifft(X * np.conj(X)))
    r_xx = r_xx[:N]

    peaks, _ = find_peaks(r_xx)

    if len(peaks) == 0:
        max_peak = int(np.argmax(r_xx[1:]) + 1)
    else:
        max_peak = int(peaks[np.argmax(r_xx[peaks])])

    nyq = 0.5 * fs
    T0 = max_peak
    T0_s = T0 / fs
    F0 = 1.0 / T0_s
    cutoff = 1.8 * F0 / nyq
    N_filter = np.round(1.0*T0).astype(np.int32)
    N_filter = N_filter - (N_filter%2)

    taps = firwin(N_filter, cutoff, fs=fs, window=("kaiser", 8))

    filtered_signal = np.convolve(signal, taps, mode="full")
    filtered_signal = filtered_signal[(N_filter - 1) // 2 :]

    # Zero-crossing
    signal_sign = np.sign(filtered_signal[:len(data)])
    I_N = np.where(np.diff(signal_sign) < 0)[0] + 1

    return I_N, filtered_signal

def WM_method(x, x_filtered, fs, I_N):
    """
    x – input signal
    x_filtered – filtered signal from voice_segmentation
    fs – sampling frequency
    I_N – rough marking of the input signal
    Returns: F0 (frequencies) and P (refined peaks)
    """
    x = np.array(x)
    PERC = 0.05
    Nc = len(I_N)
    if Nc < 3:
        # Not enough segments to calculate
        return np.array([]), np.array([])

    F0 = np.zeros(Nc - 3)
    P = np.zeros(Nc - 2, dtype=int)

    # Initialize P[0] with min point
    P[0] = np.argmin(x_filtered[I_N[0]:I_N[1]]) + I_N[0]

    for i in range(1, Nc - 2):
        P[i] = I_N[i] + (P[i - 1] - I_N[i - 1])
        J1 = max(0, P[i] - round(PERC * (P[i] - P[i - 1])))
        J2 = min(len(x) - 1, P[i] + round(PERC * (P[i] - P[i - 1])))

        search_complete = False
        max_iter = 50
        iter_count = 0

        while not search_complete and iter_count < max_iter:
            iter_count += 1
            err_min = np.inf
            Jm = None

            for j in range(J1, J2 + 1):
                err_cur = ERR(j, P, i, x)
                if err_cur < err_min:
                    err_min = err_cur
                    Jm = j

            # If boundaries are hit, shrink range conservatively
            if Jm == J1:
                J1 = max(0, J1 - max(1, round(0.5 * (J2 - J1))))
            elif Jm == J2:
                J2 = min(len(x) - 1, J2 + max(1, round(0.5 * (J2 - J1))))
            else:
                search_complete = True

        if iter_count >= max_iter:
            # Safety fallback: pick midpoint
            Jm = (J1 + J2) // 2

        P[i] = Jm

        # Delta correction for frequency
        err_Jm_plus1 = ERR(Jm + 1, P, i, x)
        err_Jm_minus1 = ERR(Jm - 1, P, i, x)
        err_Jm = ERR(Jm, P, i, x)

        denom = err_Jm_plus1 - 2 * err_Jm + err_Jm_minus1
        if denom != 0:
            delta = -0.5 * ((err_Jm_plus1 - err_Jm_minus1) / denom)
        else:
            delta = 0

        F0[i - 1] = fs / (P[i] - P[i - 1] + delta)

    return F0, P

def ERR(j, P, i, buff):
    """
    Calculate error for current j, given P and i.
    Returns np.inf if the slice is invalid.
    """
    start_idx = P[i - 1]
    end_idx = j
    length = end_idx - start_idx

    if length <= 0 or end_idx + length > len(buff):
        return np.inf

    segment = buff[end_idx:end_idx + length]
    reference = buff[start_idx:end_idx]

    if len(segment) != len(reference) or len(segment) == 0:
        return np.inf

    err_cur = np.mean((segment - reference) ** 2)
    return err_cur

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

def voice_parameters(data, segments, F0):
    data = np.array(data)

    periods = np.array(segments[1:]) - np.array(segments[:-1])
    amplitudes = np.zeros_like(periods, dtype=data.dtype)

    for i in range(len(segments)-1):
        start_idx = segments[i]
        end_idx = segments[i+1]

        min_amplitude, max_amplitude = minmax_np(data[start_idx:end_idx])

        amplitudes[i] = max_amplitude - min_amplitude


    J1 = perturbation_L(periods, 1)
    J3 = perturbation_L(periods, 3)    
    J5 = perturbation_L(periods, 5)

    S1 = perturbation_L(amplitudes, 1)    
    S3 = perturbation_L(amplitudes, 3)    
    S5 = perturbation_L(amplitudes, 5)    
    S11 = perturbation_L(amplitudes, 11)    

    F0_mean = np.mean(F0)
    F0_sd = np.std(F0)    

    return J1, J3, J5, S1, S3, S5, S11, F0_mean, F0_sd

def minmax_np(x):
    maximum = np.max(x)
    minimum = np.min(x)
    return (minimum, maximum)
