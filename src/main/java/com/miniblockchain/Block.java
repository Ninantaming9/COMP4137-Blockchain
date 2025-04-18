package com.miniblockchain;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Block {
    public String hash;
    public String previousHash;
    public String merkleRoot;
    public List<Transaction> transactions = new ArrayList<>();
    public long timeStamp;
    public int nonce;

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return StringUtil.applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        merkleRoot
        );
    }

    public void mineBlock(int difficulty) {
        merkleRoot = getMerkleRoot(transactions);
        String target = new String(new char[difficulty]).replace('\0', '0');

        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }

        System.out.println("Block mined: " + hash);
    }

    public boolean addTransaction(Transaction transaction) {
        if (transaction == null) return false;
        if (!previousHash.equals("0")) {
            if (!transaction.processTransaction()) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction successfully added to Block");
        return true;
    }

    private String getMerkleRoot(List<Transaction> transactions) {
        List<String> transactionHashes = new ArrayList<>();
        for (Transaction transaction : transactions) {
            transactionHashes.add(transaction.transactionId);
        }
        MerkleTree merkleTree = new MerkleTree(transactionHashes);
        return merkleTree.getRoot();
    }
}