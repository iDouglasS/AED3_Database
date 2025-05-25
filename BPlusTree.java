import java.util.*;

class BPlusTree {
    private static final int ORDER = 4; // Ordem da arvore

    public BPlusTree(){
        
    }

    abstract class Node {
        List<String> keys = new ArrayList<>();
        abstract boolean isLeaf();
    }

    class InternalNode extends Node {
        List<Node> children = new ArrayList<>();
        boolean isLeaf() { return false; }
    }

    class LeafNode extends Node {
        List<Long> pointers = new ArrayList<>();
        LeafNode next;
        boolean isLeaf() { return true; }
    }

    private Node root = new LeafNode();

    private LeafNode findLeaf(String key) {
        Node current = root;
        while (!current.isLeaf()) {
            InternalNode internal = (InternalNode) current;
            int i = 0;
            while (i < internal.keys.size() && key.compareTo(internal.keys.get(i)) >= 0) i++;
            current = internal.children.get(i);
        }
        return (LeafNode) current;
    }

    //insere o ponteiro na chave titulo
    public void insert(String key, long pointer) {
        LeafNode leaf = findLeaf(key);
        int pos = Collections.binarySearch(leaf.keys, key);
        if (pos >= 0) {
            leaf.pointers.set(pos, pointer);
            return;
        }
        int insertPos = -pos - 1;
        leaf.keys.add(insertPos, key);
        leaf.pointers.add(insertPos, pointer);
        if (leaf.keys.size() >= ORDER) splitLeaf(leaf);
    }

    private void splitLeaf(LeafNode leaf) {
        LeafNode newLeaf = new LeafNode();
        int mid = (ORDER + 1) / 2;
        newLeaf.keys.addAll(leaf.keys.subList(mid, leaf.keys.size()));
        newLeaf.pointers.addAll(leaf.pointers.subList(mid, leaf.pointers.size()));
        leaf.keys.subList(mid, leaf.keys.size()).clear();
        leaf.pointers.subList(mid, leaf.pointers.size()).clear();
        newLeaf.next = leaf.next;
        leaf.next = newLeaf;
        insertIntoParent(leaf, newLeaf.keys.get(0), newLeaf);
    }

    private void insertIntoParent(Node left, String key, Node right) {
        if (left == root) {
            InternalNode newRoot = new InternalNode();
            newRoot.keys.add(key);
            newRoot.children.add(left);
            newRoot.children.add(right);
            root = newRoot;
            return;
        }
        InternalNode parent = findParent(root, left);
        int pos = Collections.binarySearch(parent.keys, key);
        int insertPos = -pos - 1;
        parent.keys.add(insertPos, key);
        parent.children.add(insertPos + 1, right);
        if (parent.keys.size() >= ORDER) splitInternal(parent);
    }

    private void splitInternal(InternalNode node) {
        int mid = node.keys.size() / 2;
        String midKey = node.keys.get(mid);
        InternalNode newNode = new InternalNode();
        newNode.keys.addAll(node.keys.subList(mid + 1, node.keys.size()));
        newNode.children.addAll(node.children.subList(mid + 1, node.children.size()));
        node.keys.subList(mid, node.keys.size()).clear();
        node.children.subList(mid + 1, node.children.size()).clear();
        insertIntoParent(node, midKey, newNode);
    }

    private InternalNode findParent(Node current, Node child) {
        if (current.isLeaf()) return null;
        InternalNode internal = (InternalNode) current;
        for (Node node : internal.children) {
            if (node == child) return internal;
            InternalNode deeper = findParent(node, child);
            if (deeper != null) return deeper;
        }
        return null;
    }

    public Long search(String key) {
        LeafNode leaf = findLeaf(key);
        int pos = Collections.binarySearch(leaf.keys, key);
        if (pos >= 0) return leaf.pointers.get(pos);
        return null;
    }

    public boolean update(String key, long newPointer) {
        LeafNode leaf = findLeaf(key);
        int pos = Collections.binarySearch(leaf.keys, key);
        if (pos >= 0) {
            leaf.pointers.set(pos, newPointer);
            return true;
        }
        return false;
    }

    public Long delete(String key) {
        LeafNode leaf = findLeaf(key);
        int pos = Collections.binarySearch(leaf.keys, key);
        if (pos < 0) return null;
        long removedPointer = leaf.pointers.remove(pos);
        leaf.keys.remove(pos);
        if (leaf != root && leaf.keys.size() < (ORDER + 1) / 2) rebalanceLeaf(leaf);
        return removedPointer;
    }

    private void rebalanceLeaf(LeafNode leaf) {
        InternalNode parent = findParent(root, leaf);
        if (parent == null) return;
        int index = parent.children.indexOf(leaf);
        LeafNode leftSibling = (index > 0) ? (LeafNode) parent.children.get(index - 1) : null;
        LeafNode rightSibling = (index < parent.children.size() - 1) ? (LeafNode) parent.children.get(index + 1) : null;

        if (leftSibling != null && leftSibling.keys.size() > (ORDER + 1) / 2) {
            leaf.keys.add(0, leftSibling.keys.remove(leftSibling.keys.size() - 1));
            leaf.pointers.add(0, leftSibling.pointers.remove(leftSibling.pointers.size() - 1));
            parent.keys.set(index - 1, leaf.keys.get(0));
            return;
        }
        if (rightSibling != null && rightSibling.keys.size() > (ORDER + 1) / 2) {
            leaf.keys.add(rightSibling.keys.remove(0));
            leaf.pointers.add(rightSibling.pointers.remove(0));
            parent.keys.set(index, rightSibling.keys.get(0));
            return;
        }
        if (leftSibling != null) {
            leftSibling.keys.addAll(leaf.keys);
            leftSibling.pointers.addAll(leaf.pointers);
            leftSibling.next = leaf.next;
            parent.children.remove(index);
            parent.keys.remove(index - 1);
            rebalanceInternal(parent);
            return;
        }
        if (rightSibling != null) {
            leaf.keys.addAll(rightSibling.keys);
            leaf.pointers.addAll(rightSibling.pointers);
            leaf.next = rightSibling.next;
            parent.children.remove(index + 1);
            parent.keys.remove(index);
            rebalanceInternal(parent);
        }
    }

    private void rebalanceInternal(InternalNode node) {
        if (node == root && node.keys.isEmpty()) {
            root = node.children.get(0);
            return;
        }
        if (node.keys.size() >= (ORDER + 1) / 2) return;
        InternalNode parent = findParent(root, node);
        if (parent == null) return;
        int index = parent.children.indexOf(node);
        InternalNode leftSibling = (index > 0) ? (InternalNode) parent.children.get(index - 1) : null;
        InternalNode rightSibling = (index < parent.children.size() - 1) ? (InternalNode) parent.children.get(index + 1) : null;

        if (leftSibling != null && leftSibling.keys.size() > (ORDER + 1) / 2) {
            node.keys.add(0, parent.keys.get(index - 1));
            parent.keys.set(index - 1, leftSibling.keys.remove(leftSibling.keys.size() - 1));
            node.children.add(0, leftSibling.children.remove(leftSibling.children.size() - 1));
            return;
        }
        if (rightSibling != null && rightSibling.keys.size() > (ORDER + 1) / 2) {
            node.keys.add(parent.keys.get(index));
            parent.keys.set(index, rightSibling.keys.remove(0));
            node.children.add(rightSibling.children.remove(0));
            return;
        }
        if (leftSibling != null) {
            leftSibling.keys.add(parent.keys.remove(index - 1));
            leftSibling.keys.addAll(node.keys);
            leftSibling.children.addAll(node.children);
            parent.children.remove(index);
            rebalanceInternal(parent);
            return;
        }
        if (rightSibling != null) {
            node.keys.add(parent.keys.remove(index));
            node.keys.addAll(rightSibling.keys);
            node.children.addAll(rightSibling.children);
            parent.children.remove(index + 1);
            rebalanceInternal(parent);
        }
    }
}