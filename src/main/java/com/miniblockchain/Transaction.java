package com.miniblockchain;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class Transaction {
    private String transactionId;
    private String sender;
    private String recipient;
    private double amount;
    private byte[] signature;

    public Transaction(String sender, String recipient, double amount, PrivateKey senderPrivateKey) throws Exception {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.signature = CryptoUtils.sign(senderPrivateKey, getDataToSign());
        this.transactionId = calculateHash();
    }

    private String getDataToSign() {
        return sender + recipient + amount;
    }

    public String calculateHash() {
        String dataToHash = getDataToSign() + Base64.getEncoder().encodeToString(signature);
        return CryptoUtils.applySHA256(dataToHash);
    }

    public boolean verifySignature() throws Exception {
        String data = getDataToSign();
        PublicKey publicKey = CryptoUtils.stringToPublicKey(sender);
        return CryptoUtils.verify(publicKey, data, signature);
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public String getSender() { return sender; }
    public String getRecipient() { return recipient; }
    public double getAmount() { return amount; }
    public byte[] getSignature() { return signature; }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + transactionId + '\'' +
                ", sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", amount=" + amount +
                '}';
    }
}