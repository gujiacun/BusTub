package run.yuyang.db.container.hashtable;

import run.yuyang.db.buffer.BufferPoolManager;
import run.yuyang.db.concurrency.Transaction;
import run.yuyang.db.container.hashfunction.HashFunction;

public class LinerHashTable<K, V> implements LinearProbeHashTable<K, V> {



    public LinerHashTable(String name, BufferPoolManager bufferPoolManager, int numBuckets, HashFunction hashFunction) {

    }


    @Override
    public void resize(int size) {

    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public boolean insert(Transaction transaction, K key, V value) {
        return false;
    }

    @Override
    public boolean remove(Transaction transaction, K key, V value) {
        return false;
    }

    @Override
    public V getValue(Transaction transaction, K key) {
        return null;
    }
}
