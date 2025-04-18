package com.miniblockchain;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionTest {
    private Wallet walletA;
    private Wallet walletB;

    @BeforeEach
    void setUp() {
        walletA = new Wallet();
        walletB = new Wallet();
    }

    @Test
    void testTransactionCreation() {
        Transaction transaction = new Transaction(
                walletA.getPublicKey(),
                walletB.getPublicKey(),
                5,
                null
        );
        transaction.generateSignature(walletA.getPrivateKey());

        assertNotNull(transaction.transactionId);
        assertTrue(transaction.verifySignature());
    }

    @Test
    void testInvalidTransactionSignature() {
        Transaction transaction = new Transaction(
                walletA.getPublicKey(),
                walletB.getPublicKey(),
                5,
                null
        );
        // Sign with wrong private key
        transaction.generateSignature(walletB.getPrivateKey());

        assertFalse(transaction.verifySignature());
    }
}