package com.rikkeibank.service;

import com.rikkeibank.dto.request.TransferRequest;
import com.rikkeibank.dto.response.*;
import org.springframework.data.domain.Page;

public interface TransactionService {
    TransactionResponse transfer(String username, TransferRequest request);

    Page<StatementResponse> getMyStatement(String username, int page, int size);
}