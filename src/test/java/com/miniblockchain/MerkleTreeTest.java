package com.miniblockchain;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

class MerkleTreeTest {
    @Test
    void testMerkleTreeConstruction() {
        List<String> transactions = Arrays.asList("tx1", "tx2", "tx3", "tx4");
        MerkleTree merkleTree = new MerkleTree(transactions);

        assertNotNull(merkleTree.getRoot());
        assertFalse(merkleTree.getRoot().isEmpty());
    }

    @Test
    void testMerkleTreeWithOddTransactions() {
        List<String> transactions = Arrays.asList("tx1", "tx2", "tx3");
        MerkleTree merkleTree = new MerkleTree(transactions);

        assertNotNull(merkleTree.getRoot());
        assertFalse(merkleTree.getRoot().isEmpty());
    }
}
