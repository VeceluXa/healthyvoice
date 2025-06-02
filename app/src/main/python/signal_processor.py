import numpy as np
import matplotlib.pyplot as plt
import soundfile as sf
from scipy.signal import find_peaks, firwin
import numba

def voice_segmentation(data,fs):    

    # print("RecordingAnalyze voice_segmentation: Started processing")

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

    # print("RecordingAnalyze voice_segmentation: Computed r_xx")

    peaks, _ = find_peaks(r_xx)
    
    # print("RecordingAnalyze voice_segmentation: Found peaks")

    max_peak = peaks[0]
    for i in range(1, len(peaks)):
        if r_xx[peaks[i]] > r_xx[max_peak]:
            max_peak = peaks[i]
            
    # print("RecordingAnalyze voice_segmentation: Found max peaks")

    nyq = 0.5*fs
    T0 = max_peak
    T0_s = T0/fs      # seconds
    F0 = 1.0/(T0_s)   
    cutoff = 1.8*F0/nyq 
    N_filter = np.round(1.0*T0).astype(np.int32)
    N_filter = N_filter - (N_filter%2)

    taps = firwin(N_filter, cutoff, fs=fs, window=('kaiser',8))

    filtered_signal = np.convolve(data, taps, mode='full')
    filtered_signal = filtered_signal[(N_filter-1)//2:]

    # Zero-crossing
    signal_sign = np.sign(filtered_signal[:len(data)])
    I_N = np.where(np.diff(signal_sign)<0)[0] + 1

    return I_N,filtered_signal

def WM_method(x, x_filtered, fs, I_N):
    """
    x – input signal
    fs – sampling frequency
    I_N – rough marking of the input signal
    """
    x = np.array(x)
    PERC = 0.05
    Nc = len(I_N)
    F0 = np.zeros(Nc - 3)
    P = np.zeros(Nc - 2, dtype=int)

    # Find the minimum point and set P[0]
    P[0] = np.argmin(x_filtered[I_N[0]:I_N[1]]) + I_N[0]

    for i in range(1, Nc - 2):
        P[i] = I_N[i] + (P[i - 1] - I_N[i - 1])
        J1 = P[i] - round(PERC * (P[i] - P[i - 1]))
        J2 = P[i] + round(PERC * (P[i] - P[i - 1]))
        search_complete = False

        while not search_complete:
            err_min = np.inf
            Jm = None

            for j in range(J1, J2 + 1):
                err_cur = ERR(j, P, i, x)
                if err_cur < err_min:
                    err_min = err_cur
                    Jm = j

            if Jm == J1:
                J1 = J1 - round(0.5 * (J2 - J1))
            elif Jm == J2:
                J2 = J2 + round(0.5 * (J2 - J1))
            else:
                search_complete = True

        P[i] = Jm

        # Calculate the error at Jm+1, Jm-1, and Jm
        err_Jm_plus1, err_Jm_minus1, err_Jm = ERR(Jm+1, P, i, x), ERR(Jm-1, P, i, x), ERR(Jm, P, i, x)
        delta = -0.5 * ((err_Jm_plus1 - err_Jm_minus1) / (err_Jm_plus1 - 2 * err_Jm + err_Jm_minus1))

        # Frequency calculation
        F0[i - 1] = fs / (P[i] - P[i - 1] + delta)

    return F0,P

def ERR(j, P, i, buff):
    """
    Calculate error for current j, given P and i.
    """
    # sum_error = 0
    err_cur = np.mean((buff[j:j + (j - P[i - 1])] - buff[P[i - 1]:j])**2)
    # for k in range(P[i - 1], j):
    #     sum_error += (buff[k + (j - P[i - 1])] - buff[k]) ** 2
    # err_cur = (1 / (j - P[i - 1])) * sum_error
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
    print("RecordingAnalyze voice_parameters: Start processing")
    data = np.array(data)

    periods = np.array(segments[1:]) - np.array(segments[:-1])
    amplitudes = np.zeros_like(periods, dtype=data.dtype)

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

    F0_mean = np.mean(F0)
    F0_sd = np.std(F0)    

    print("RecordingAnalyze voice_parameters: Finish processing")
    return J1, J3, J5, S1, S3, S5, S11, F0_mean, F0_sd
  
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