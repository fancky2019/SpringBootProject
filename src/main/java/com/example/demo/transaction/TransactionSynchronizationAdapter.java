package com.example.demo.transaction;

import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronization;

/**
 * 采用事务方法内监控耗时
 */
public class TransactionSynchronizationAdapter implements TransactionSynchronization {

    private long startTime;

    public TransactionSynchronizationAdapter() {
        this.startTime = System.currentTimeMillis();
        TransactionSynchronizationManager.registerSynchronization(this);
    }

    @Override
    public void afterCommit() {
        long endTime = System.currentTimeMillis();
        System.out.println("Transaction Execution Time: " + (endTime - startTime) + "ms");
    }
}