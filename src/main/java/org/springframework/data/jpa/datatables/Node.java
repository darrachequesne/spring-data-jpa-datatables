package org.springframework.data.jpa.datatables;

import java.util.ArrayList;
import java.util.List;

class Node<T> {
    private final String name;
    private final T data;
    private List<Node<T>> children = new ArrayList<>();

    Node(String name, T data) {
        this.name = name;
        this.data = data;
    }

    Node(String name) {
        this.name = name;
        this.data = null;
    }

    void addChild(Node<T> child) {
        children.add(child);
    }

    Node<T> getOrCreateChild(String name) {
        for (Node<T> child : children) {
            if (child.name.equals(name)) {
                return child;
            }
        }
        Node<T> child = new Node<>(name);
        children.add(child);
        return child;
    }

    boolean isLeaf() {
        return this.children.isEmpty();
    }

    public T getData() {
        return data;
    }

    public String getName() {
        return name;
    }

    List<Node<T>> getChildren() {
        return children;
    }
}