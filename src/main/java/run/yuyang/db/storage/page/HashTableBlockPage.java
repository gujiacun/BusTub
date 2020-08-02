package run.yuyang.db.storage.page;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static run.yuyang.db.util.Config.PAGE_SIZE;

/**
 * 同时存储key和value在block page，同时支持non-unique keys
 * <p>
 * Block page format (keys are stored in order):
 * ----------------------------------------------------------------
 * | KEY(1) + VALUE(1) | KEY(2) + VALUE(2) | ... | KEY(n) + VALUE(n)
 * ----------------------------------------------------------------
 * <p>
 * 这里的 '+' 意味着一组.
 */
public class HashTableBlockPage<K, V> {

    private static final int BLOCK_ARRAY_SIZE = 4 * PAGE_SIZE;

    private AtomicBoolean occupied[];
    private AtomicBoolean readable[];
    private K keys[];
    private V values[];

    public HashTableBlockPage() {
        occupied = new AtomicBoolean[BLOCK_ARRAY_SIZE];
        readable = new AtomicBoolean[BLOCK_ARRAY_SIZE];
        keys = (K[]) new Object[BLOCK_ARRAY_SIZE];
        values = (V[]) new Object[BLOCK_ARRAY_SIZE];
    }

    public K keyAt(int bucket_ind) {

        return null;
    }

    public V valueAt(int bucket_ind) {

        return null;
    }

    public boolean insert(int bucket_ind, K key, V value) {
        return true;
    }

    public void remove(int bucket_ind) {

    }

    public boolean isOccupied(int bucket_ind) {
        return true;
    }

    public boolean IsReadable(int bucket_ind) {
        return true;
    }

}
