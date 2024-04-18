package com.example.trobamot;

import java.util.Iterator;

public class ullSet<E> {  // Unsorted Linked List Set
    private class Node {
        private Node next;
        private E value;
        public Node(E val, Node n) {
            this.value = val;
            this.next = n;
        }
    }

    private Node head;

    public ullSet() {  // O(1)
        head = null;
    }

    public boolean isEmpty() {  // O(1)
        return (head == null);
    }

    public boolean contains (E val) {  // O(n)
        for (Node i = head; i != null; i = i.next) {
            if (i.value.equals(val))
                return true;
        }
        return false;
    }

    // Devuelve true si la palabra no estaba O(n)
    public boolean add(E val) {
        if (contains(val))
            return false;

        Node newNode = new Node(val, head);
        head = newNode;

        return true;
    }

    // Devuelve true si la palabra estaba O(n)
    public boolean remove(E val){
        // Recorremos la lista enlazada con 2 iteradores
        for (Node prev = null, i = head; i != null; prev = i, i = i.next){
            if (i.value.equals(val)){  // Si hemos encontrado el elemento a eliminar
                if (prev == null)
                    head = i.next;
                else
                    prev = i.next;
                return true;
            }
        }
        return false;
    }

    public Iterator getIterator(){  // O(1)
        return new ullsIterator();
    }

    private class ullsIterator implements Iterator{
        private Node it;

        public ullsIterator(){
            it = head;
        }

        @Override
        public boolean hasNext(){  // O(1)
            return (it != null);
        }

        @Override
        public E next(){  // O(1)
            E val = it.value;
            it = it.next;
            return val;
        }
    }
}
