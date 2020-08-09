package run.yuyang.db.container.hashtable;

import run.yuyang.db.concurrency.Transaction;

public interface HashTable<K, V> {

    boolean insert(Transaction transaction, K key, V value);

    boolean remove(Transaction transaction, K key, V value);

    V getValue(Transaction transaction, K key);


}
