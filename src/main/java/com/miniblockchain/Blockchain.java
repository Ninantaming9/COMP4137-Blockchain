package com.miniblockchain;


import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Blockchain {
    public static List<Block> blockchain = new ArrayList<>();
    public static int difficulty = 5;
    public static float minimumTransaction = 0.1f;
    public static Map<String, TransactionOutput> UTXOs = new HashMap<>();
    public static Wallet walletA;
    public static Wallet walletB;

    public static void main(String[] args) {
        // Setup Bouncy Castle as security provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // Create wallets
        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinbase = new Wallet(); // For genesis block

        // Create genesis transaction
        Transaction genesisTransaction = new Transaction(
                coinbase.getPublicKey(),
                walletA.getPublicKey(),
                100f,
                null
        );
        genesisTransaction.generateSignature(coinbase.getPrivateKey());
        genesisTransaction.transactionId = "0";
        genesisTransaction.outputs.add(new TransactionOutput(
                genesisTransaction.recipient,
                genesisTransaction.value,
                genesisTransaction.transactionId)
        );
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        System.out.println("Creating and mining Genesis block...");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        // Testing
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        System.out.println("\nWalletA is attempting to send funds (40) to WalletB...");
        Block block1 = new Block(genesis.hash);
        block1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 40f));
        addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        System.out.println("\nWalletB is attempting to send funds (20) to WalletA...");
        Block block2 = new Block(block1.hash);
        block2.addTransaction(walletB.sendFunds(walletA.getPublicKey(), 20f));
        addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        isChainValid();
    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        // Temporary working list of unspent transactions
        Map<String, TransactionOutput> tempUTXOs = new HashMap<>();
        tempUTXOs.put(blockchain.get(0).transactions.get(0).outputs.get(0).id,
                blockchain.get(0).transactions.get(0).outputs.get(0));

        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);

            // Compare registered hash and calculated hash
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("Current hashes not equal");
                return false;
            }

            // Compare previous hash and registered previous hash
            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("Previous hashes not equal");
                return false;
            }

            // Check if hash is solved
            if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }

            // Loop through transactions
            for (int t = 0; t < currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if (!currentTransaction.verifySignature()) {
                    System.out.println("Signature on Transaction(" + t + ") is invalid");
                    return false;
                }

                if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("Inputs are not equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for (TransactionInput input : currentTransaction.inputs) {
                    TransactionOutput tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if (tempOutput == null) {
                        System.out.println("Referenced input on Transaction(" + t + ") is missing");
                        return false;
                    }

                    if (input.UTXO.value != tempOutput.value) {
                        System.out.println("Referenced input Transaction(" + t + ") value is invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for (TransactionOutput output : currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if (currentTransaction.outputs.get(0).recipient != currentTransaction.recipient) {
                    System.out.println("Transaction(" + t + ") output recipient is not who it should be");
                    return false;
                }

                if (currentTransaction.outputs.size() > 1 &&
                        currentTransaction.outputs.get(1).recipient != currentTransaction.sender) {
                    System.out.println("Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }
            }
        }

        System.out.println("Blockchain is valid");
        return true;
    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }
}