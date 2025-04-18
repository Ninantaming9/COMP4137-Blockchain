package com.miniblockchain;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;

public class Transaction {
    public String transactionId;
    public PublicKey sender;
    public PublicKey recipient;
    public float value;
    public byte[] signature;

    public ArrayList<TransactionInput> inputs = new ArrayList<>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<>();

    private static int sequence = 0;

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    private String calculateHash() {
        sequence++;
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(recipient) +
                        Float.toString(value) + sequence
        );
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) +
                StringUtil.getStringFromKey(recipient) +
                Float.toString(value);
        signature = CryptoUtils.applyECDSASig(privateKey, data);
    }

    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) +
                StringUtil.getStringFromKey(recipient) +
                Float.toString(value);
        return CryptoUtils.verifyECDSASig(sender, data, signature);
    }

    public boolean processTransaction() {
        if (!verifySignature()) {
            System.out.println("Transaction signature failed to verify");
            return false;
        }

        // Gather transaction inputs (Make sure they are unspent)
        for (TransactionInput i : inputs) {
            i.UTXO = Blockchain.UTXOs.get(i.transactionOutputId);
        }

        // Check if transaction is valid
        if (getInputsValue() < Blockchain.minimumTransaction) {
            System.out.println("Transaction inputs too small: " + getInputsValue());
            return false;
        }

        // Generate transaction outputs
        float leftOver = getInputsValue() - value;
        transactionId = calculateHash();

        // Send value to recipient
        outputs.add(new TransactionOutput(this.recipient, value, transactionId));

        // Send the left over 'change' back to sender
        if (leftOver > 0) {
            outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));
        }

        // Add outputs to Unspent list
        for (TransactionOutput o : outputs) {
            Blockchain.UTXOs.put(o.id, o);
        }

        // Remove transaction inputs from UTXO lists as spent
        for (TransactionInput i : inputs) {
            if (i.UTXO == null) continue; // Skip if can't find transaction
            Blockchain.UTXOs.remove(i.UTXO.id);
        }

        return true;
    }
    public float getInputsValue() {
        float total = 0;
        for (TransactionInput i : inputs) {
            if (i.UTXO == null) continue;
            total += i.UTXO.value;
        }
        return total;
    }

    public float getOutputsValue() {
        float total = 0;
        for (TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }
}