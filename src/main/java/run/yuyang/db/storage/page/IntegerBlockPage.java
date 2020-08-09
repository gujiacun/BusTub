package run.yuyang.db.storage.page;

import javafx.util.Pair;
import run.yuyang.db.util.ConvertUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static run.yuyang.db.util.Config.PAGE_SIZE;
import static run.yuyang.db.util.ConvertUtils.intToBytes;

/**
 * 同时存储key和value在block page，同时支持non-unique keys
 * <p>
 * Block page format (keys are stored in order):
 * ----------------------------------------------------------------
 * | KEY(1) + VALUE(1) | KEY(2) + VALUE(2) | ... | KEY(n) + VALUE(n)
 * ----------------------------------------------------------------
 * <p>
 * 这里的 '+' 意味着一组.
 * <p>
 * 先留一个坑，暂时无法实现Pageable
 */
public class IntegerBlockPage implements Pageable {

    private static final int BLOCK_ARRAY_SIZE = (4 * PAGE_SIZE) / (4 * 16 + 1);

    private final byte[] occupied;
    private final byte[] readable;
    private final Pair<Integer, Integer>[] list;

    public IntegerBlockPage() {
        occupied = new byte[(BLOCK_ARRAY_SIZE - 1) / 8 + 1];
        readable = new byte[(BLOCK_ARRAY_SIZE - 1) / 8 + 1];
        list = new Pair[BLOCK_ARRAY_SIZE];
    }

    public Integer keyAt(int bucket_ind) throws RuntimeException {
        if (!isReadable(bucket_ind)) {
            throw new RuntimeException(bucket_ind + "");
        }
        return list[bucket_ind].getKey();
    }

    public Integer valueAt(int bucket_ind) {
        if (!isReadable(bucket_ind)) {
            throw new RuntimeException(bucket_ind + "");
        }
        return list[bucket_ind].getValue();
    }

    public boolean insert(int bucket_ind, Integer key, Integer value) {
        if (isReadable(bucket_ind)) {
            return false;
        }
        list[bucket_ind] = new Pair<>(key, value);
        int offset = bucket_ind / 8;
        readable[offset] = (byte) (readable[offset] | (1 << (bucket_ind % 8)));
        occupied[offset] = (byte) (occupied[offset] | (1 << (bucket_ind % 8)));
        return true;
    }

    public void remove(int bucket_ind) {
        int offset = bucket_ind / 8;
        readable[offset] = (byte) (readable[offset] & ~(1 << (bucket_ind % 8)));
    }

    public boolean isOccupied(int bucket_ind) {
        return ((occupied[bucket_ind / 8] >> (bucket_ind % 8)) & 1) == 1;
    }

    public boolean isReadable(int bucket_ind) {
        return ((readable[bucket_ind / 8] >> (bucket_ind % 8)) & 1) == 1;
    }

    public int numberOfSlots() {
        return BLOCK_ARRAY_SIZE;
    }

    @Override
    public byte[] convertTo() {
        byte[] bytes = new byte[PAGE_SIZE];
        int index = 0;
        for (byte b : readable) {
            bytes[index++] = b;
        }
        for (byte b : occupied) {
            bytes[index++] = b;
        }
        for (Pair<Integer, Integer> integerIntegerPair : list) {
            if (integerIntegerPair == null) {
                bytes[index++] = 0;
                bytes[index++] = 0;
                bytes[index++] = 0;
                bytes[index++] = 0;
                bytes[index++] = 0;
                bytes[index++] = 0;
                bytes[index++] = 0;
                bytes[index++] = 0;
            }else{
                byte[] temp = intToBytes(integerIntegerPair.getKey());
                bytes[index++] = temp[0];
                bytes[index++] = temp[1];
                bytes[index++] = temp[2];
                bytes[index++] = temp[3];
                temp = intToBytes(integerIntegerPair.getValue());
                bytes[index++] = temp[0];
                bytes[index++] = temp[1];
                bytes[index++] = temp[2];
                bytes[index++] = temp[3];
            }
        }
        return bytes;
    }

    @Override
    public void convertBack(byte[] data) {

    }
}
