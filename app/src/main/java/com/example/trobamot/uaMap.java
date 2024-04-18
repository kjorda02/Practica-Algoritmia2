package com.example.trobamot;

import java.util.Iterator;

public class uaMap<K, V> {
    private K[] keys;
    private V[] values;
    private int size;

    public uaMap(int max){
        keys = (K[]) new Object[max];
        values = (V[]) new Object[max];
        size = 0;
    }

    public V get(K key){  // O(n)
        for (int i = 0; i < size; i++){
            if (keys[i].equals(key)){
                return values[i];
            }
        }
        return null;
    }

    public boolean containsKey(K key){
        return (get(key)!=null);
    }

    // Devuelve el valor anterior asociado a la llave. O(n)
    public V put(K key, V value){
        for (int i = 0; i < size; i++){  // Buscamos la llave
            if (keys[i].equals(key)){  // Si la encontramos
                V prevValue = values[i];
                values[i] = value;  // Actualizamos el valor
                return prevValue;  // Y devolvemos el valor anterior
            }
        }

        // Si no la hemos encontrado
        if (size < keys.length){  // Si tenemos espacio libre
            keys[size] = key;
            values[size++] = value;
        }
        return null;
    }

    // Devuelve el valor asociado a la llave antes de eliminarla. O(n)
    public V remove(K key){
        for (int i = 0; i < size; i++){  // Buscamos la llave
            if (keys[i].equals(key)){  // Si la encontramos
                keys[i] = keys[size-1];  // Eliminamos el elemento sustituyendolo por el ultimo
                V prevValue = values[i];
                values[i] = values[size-1];
                size--;
                return prevValue;  // Y devolvemos el valor anterior
            }
        }

        // Si no hemos encontrado la llave
        return null;
    }

    public boolean isEmpty(){
        return (size==0);
    }

    public int size(){
        return size;
    }

    protected class Pair {
        private K key;
        private V value;
        private Pair(K k,V v) {
            key = k;
            value = v;
        }
        public K getKey() {return key;}
        public V getValue() {return value;}
    }

    private class uamIterator implements Iterator {
        private int idx;

        public uamIterator(){
            idx = 0;
        }

        @Override
        public boolean hasNext(){
            return (idx < size);
        }

        @Override
        public Object next(){
            return new Pair(keys[idx], values[idx++]);
        }
    }

    public Iterator iterator(){
        return new uamIterator();
    }
}














