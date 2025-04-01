package com.miniblockchain;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Mini Blockchain Phase I Implementation ===");

        // 1. Generate accounts (key pairs)
        System.out.println("\nGenerating accounts...");
        KeyPair alice = CryptoUtils.generateKeyPair();
        KeyPair bob = CryptoUtils.generateKeyPair();
        KeyPair charlie = CryptoUtils.generateKeyPair();
        KeyPair dave = CryptoUtils.generateKeyPair();

        String aliceAddress = CryptoUtils.keyToString(alice.getPublic());
        String bobAddress = CryptoUtils.keyToString(bob.getPublic());
        String charlieAddress = CryptoUtils.keyToString(charlie.getPublic());
        String daveAddress = CryptoUtils.keyToString(dave.getPublic());

        System.out.println("Alice: " + aliceAddress);
        System.out.println("Bob: " + bobAddress);
        System.out.println("Charlie: " + charlieAddress);
        System.out.println("Dave: " + daveAddress);

        // 2. Create transactions (must be power of 2 for simple Merkle tree)
        System.out.println("\nCreating transactions...");
        List<Transaction> transactions = new ArrayList<>();

        // Alice sends 10 coins to Bob
        Transaction tx1 = new Transaction(
                aliceAddress,
                bobAddress,
                10.0,
                alice.getPrivate()
        );
        transactions.add(tx1);
        System.out.println("Created: " + tx1);

        // Bob sends 5 coins to Charlie
        Transaction tx2 = new Transaction(
                bobAddress,
                charlieAddress,
                5.0,
                bob.getPrivate()
        );
        transactions.add(tx2);
        System.out.println("Created: " + tx2);

        // Charlie sends 3 coins to Dave
        Transaction tx3 = new Transaction(
                charlieAddress,
                daveAddress,
                3.0,
                charlie.getPrivate()
        );
        transactions.add(tx3);
        System.out.println("Created: " + tx3);

        // Dave sends 1 coin to Alice
        Transaction tx4 = new Transaction(
                daveAddress,
                aliceAddress,
                1.0,
                dave.getPrivate()
        );
        transactions.add(tx4);
        System.out.println("Created: " + tx4);

        // 3. Verify all transactions
        System.out.println("\nVerifying transactions...");
        for (Transaction tx : transactions) {
            boolean isValid = tx.verifySignature();
            System.out.println("Transaction " + tx.getTransactionId() +
                    " valid: " + isValid);
        }

        // 4. Build Merkle Tree
        System.out.println("\nBuilding Merkle Tree...");
        MerkleTree merkleTree = new MerkleTree(transactions);
        merkleTree.printTree();

        System.out.println("\n=== Phase I Implementation Complete ===");
    }
}