package com.rikkeibank.service;

import com.rikkeibank.dto.request.*;

public interface SecurityService {
    void changeTransactionPin(String username, ChangePinRequest request);
    String forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}