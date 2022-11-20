package com.example.L20paymentgatewaydemo.service;

import com.example.L20paymentgatewaydemo.dto.PaymentInitResponse;
import com.example.L20paymentgatewaydemo.dto.PaymentPageRequest;
import com.example.L20paymentgatewaydemo.entity.Merchant;
import com.example.L20paymentgatewaydemo.entity.Transaction;
import com.example.L20paymentgatewaydemo.repo.MerchantRepo;
import com.example.L20paymentgatewaydemo.repo.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private MerchantRepo merchantRepo;

    public String getStatus(String txnId){
        Transaction transaction = transactionRepo.findByTxnId(txnId);
        return transaction.getStatus();
    }

    public Transaction getTransaction(String txnId){
        Transaction transaction = transactionRepo.findByTxnId(txnId);
        return transaction;
    }

    public String doPaymentAndRedirect(String txnId){
        Transaction transaction = transactionRepo.findByTxnId(txnId);
        transaction.setStatus("SUCCESS");
        transactionRepo.save(transaction);
        Merchant merchant = merchantRepo.findById(transaction.getMerchantId()).get();
        String url = merchant.getRedirectionUrl()+txnId;
        return url;
    }

    public PaymentInitResponse generatePaymentPage(PaymentPageRequest request){
        String txnId = UUID.randomUUID().toString();
        Transaction transaction = Transaction.builder()
                .merchantId(request.getMerchantId())
                .amount(request.getAmount())
                .status("PENDING")
                .txnId(txnId)
                .build();
        transactionRepo.save(transaction);
        String url = "http://localhost:9090/payment-page/"+txnId;
        PaymentInitResponse paymentInitResponse = PaymentInitResponse.builder()
                .txnId(txnId)
                .url(url)
                .build();
        return paymentInitResponse;
    }


}
