package com.miniblockchain;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BlockchainTest {
    private Blockchain blockchain;

    @BeforeEach
    void setUp() {
        blockchain = new Blockchain();
    }

    @Test
    void testBlockchainValidity() {
        assertTrue(Blockchain.isChainValid());
    }

    @Test
    void testAddBlock() {
        int initialSize = Blockchain.blockchain.size();
        Block newBlock = new Block(Blockchain.blockchain.get(Blockchain.blockchain.size() - 1).hash);
        Blockchain.addBlock(newBlock);
        assertEquals(initialSize + 1, Blockchain.blockchain.size());
    }
}