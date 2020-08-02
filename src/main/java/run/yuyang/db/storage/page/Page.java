package run.yuyang.db.storage.page;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static run.yuyang.db.util.Config.INVALID_PAGE_ID;
import static run.yuyang.db.util.Config.PAGE_SIZE;

/**
 * @author YuYang
 */
@Getter
@Setter
public class Page {

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private byte[] data = new byte[PAGE_SIZE];

    private boolean isObj;
    private Pageable objData;

    private int pageId = INVALID_PAGE_ID;
    private int pinCount = 0;
    private boolean isDirty = false;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public void wLatch() {
        readWriteLock.writeLock().lock();
    }

    public void wUnLatch() {
        readWriteLock.writeLock().unlock();
    }

    public void rLatch() {
        readWriteLock.readLock().lock();
    }

    public void rUnLatch() {
        readWriteLock.readLock().unlock();
    }

    public void setData(byte[] data) {
        isObj = false;
        this.data = Arrays.copyOf(data, PAGE_SIZE);
    }

    public void setData(Pageable page) {
        if (!isObj) {
            data = null;
        }
        isObj = true;
        this.objData = page;
    }

    public byte[] getData() {
        if (isObj) {
            return objData.convertTo();
        } else {
            return data;
        }
    }


}
