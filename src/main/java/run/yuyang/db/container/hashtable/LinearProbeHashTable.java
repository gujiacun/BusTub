package run.yuyang.db.container.hashtable;

public interface LinearProbeHashTable<K, V> extends HashTable<K, V> {

    void resize(int size);

    int getSize();

}
