package com.miniblockchain;
import java.util.ArrayList;
import java.util.List;

public class MerkleTree {
    private List<String> transactions;
    private List<String> tree;

    public MerkleTree(List<String> transactions) {
        this.transactions = transactions;
        this.tree = new ArrayList<>();
        buildTree();
    }

    private void buildTree() {
        // Add all transactions as leaves
        tree.addAll(transactions);

        int levelOffset = 0;
        int levelSize = transactions.size();

        // Build tree level by level
        while (levelSize > 1) {
            for (int i = 0; i < levelSize; i += 2) {
                if (i + 1 < levelSize) {
                    String left = tree.get(levelOffset + i);
                    String right = tree.get(levelOffset + i + 1);
                    tree.add(StringUtil.applySha256(left + right));
                } else {
                    // If odd number of elements, duplicate the last one
                    String last = tree.get(levelOffset + i);
                    tree.add(StringUtil.applySha256(last + last));
                }
            }
            levelOffset += levelSize;
            levelSize = (levelSize + 1) / 2;
        }
    }

    public String getRoot() {
        if (tree.isEmpty()) {
            return "";
        }
        return tree.get(tree.size() - 1);
    }

    public List<String> getTree() {
        return tree;
    }
}