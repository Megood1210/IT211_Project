package com.rikkeibank.aspect;

import com.rikkeibank.dto.request.TransferRequest;
import com.rikkeibank.dto.response.TransactionResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TransactionAuditAspect {
    @AfterReturning(pointcut = "execution(* com.rikkeibank.service.impl.TransactionServiceImpl" +
            ".transfer(..))", returning = "result")
    public void auditSuccess(Object result) {
        TransactionResponse response = (TransactionResponse) result;

        log.info("[AUDIT SUCCESS] {} transferred {} to {}", response.getFromAccount(), response
                .getAmount(), response.getToAccount());
    }

    @AfterThrowing(pointcut = "execution(* com.rikkeibank.service.impl.TransactionServiceImpl" +
            ".transfer(..))", throwing = "ex")
    public void auditFail(Exception ex) {
        log.error("[AUDIT FAILED] {}", ex.getMessage());
    }
}