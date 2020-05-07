package run.yuyang.db.buffer;

import java.util.Arrays;

/**
 * @author YuYang
 */
public class ClockReplacer implements Replacer {

    static final String TYPE_NAME = "CLOCK";

    private final boolean[] isPin;
    private final boolean[] ref;
    private final int capacity;
    private int size;
    private int hand;

     public ClockReplacer(int capacity) {
        this.capacity = capacity;
        this.isPin = new boolean[capacity];
        Arrays.fill(isPin, true);
        this.ref = new boolean[capacity];
        this.size = 0;
        this.hand = 0;
    }

    @Override
    public int victim() {
        synchronized (this) {
            while (size > 0) {
                if (hand == capacity) {
                    hand = 0;
                }
                if (isPin[hand]) {
                    hand++;
                } else if (ref[hand]) {
                    ref[hand++] = false;
                } else {
                    isPin[hand] = true;
                    size--;
                    return hand++;
                }
            }
            return -1;
        }
    }

    @Override
    public void pin(int frameId) {
        synchronized (this) {
            if (!isPin[frameId]) {
                isPin[frameId] = true;
                size--;
            }
        }
    }

    @Override
    public void unpin(int frameId) {
        synchronized (this) {
            if (isPin[frameId]) {
                isPin[frameId] = false;
                ref[frameId] = true;
                size++;
            }
        }
    }

    @Override
    public int size() {
        synchronized (this) {
            return size;
        }
    }

}
