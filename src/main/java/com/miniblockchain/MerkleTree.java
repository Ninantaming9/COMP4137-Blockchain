package com.miniblockchain;

import java.util.ArrayList;
import java.util.List;

public class MerkleTree {
    private List<String> transactions;
    private List<List<String>> treeLevels;
    private String root;

    public MerkleTree(List<Transaction> transactions) {
        this.transactions = new ArrayList<>();
        for (Transaction tx : transactions) {
            this.transactions.add(tx.getTransactionId());
        }
        this.treeLevels = new ArrayList<>();
        constructTree();
    }

    private void constructTree() {
        if (transactions.isEmpty()) {
            root = "";
            return;
        }

        treeLevels.add(new ArrayList<>(transactions));

        List<String> currentLevel = new ArrayList<>(transactions);

        while (currentLevel.size() > 1) {
            List<String> nextLevel = new ArrayList<>();

            for (int i = 0; i < currentLevel.size(); i += 2) {
                String left = currentLevel.get(i);
                String right = (i + 1 < currentLevel.size()) ? currentLevel.get(i + 1) : left;
                nextLevel.add(CryptoUtils.applySHA256(left + right));
            }

            treeLevels.add(nextLevel);
            currentLevel = nextLevel;
        }

        root = currentLevel.get(0);
    }

    public String getRoot() {
        return root;
    }

    public void printTree() {
        System.out.println("\nMerkle Tree Structure:");
        for (int i = 0; i < treeLevels.size(); i++) {
            System.out.println("Level " + i + ": " + treeLevels.get(i));
        }
        System.out.println("Root: " + root);
    }
}