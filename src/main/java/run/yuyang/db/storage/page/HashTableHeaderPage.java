package run.yuyang.db.storage.page;

import run.yuyang.db.util.ConvertUtils;

import static run.yuyang.db.util.Config.PAGE_SIZE;
import static run.yuyang.db.util.ConvertUtils.byteToint;
import static run.yuyang.db.util.ConvertUtils.intToBytes;

/*
 * Header format (size in byte, 16 bytes in total):
 * -------------------------------------------------------------
 * | LSN (4) | Size (4) | PageId(4) | Index(4)
 * -------------------------------------------------------------
 */
public class HashTableHeaderPage implements Pageable {

    public static int MAX_SIZE = PAGE_SIZE / 4 - 4;

    private int lsn;
    private int size;
    private int pageId;
    private int index = 0;
    private final int[] blockPageIds = new int[MAX_SIZE];

    public int getLsn() {
        return lsn;
    }

    public int getSize() {
        return size;
    }

    public int getPageId() {
        return pageId;
    }

    public void setLsn(int lsn) {
        this.lsn = lsn;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public int numBlocks() {
        return index;
    }

    public void resetBlockIndex() {
        this.index = 0;
    }

    public void addBlockPageIds(int pageId) throws RuntimeException {
        if (index == size) {
            throw new RuntimeException("数据库已满");
        }
        blockPageIds[index++] = pageId;
    }

    public int getBlockPageIds(int size) throws RuntimeException {
        if (index > size) {
            throw new RuntimeException("超出数组索引范围");
        }
        return this.blockPageIds[index];
    }

    @Override
    public byte[] convertTo() {
        byte[] bytes = new byte[PAGE_SIZE];
        int index = 0;
        // 转化lsn
        byte[] temp = intToBytes(lsn);
        for (int i = 0; i < 4; i++) {
            bytes[index++] = temp[i];
        }
        //转化size
        temp = intToBytes(size);
        for (int i = 0; i < 4; i++) {
            bytes[index++] = temp[i];
        }
        //转化pageId
        temp = intToBytes(pageId);
        for (int i = 0; i < 4; i++) {
            bytes[index++] = temp[i];
        }
        //转化index
        temp = intToBytes(index);
        for (int i = 0; i < 4; i++) {
            bytes[index++] = temp[i];
        }
        for (int id : blockPageIds) {
            temp = intToBytes(id);
            for (int i = 0; i < 4; i++) {
                bytes[index++] = temp[i];
            }
        }
        return bytes;
    }

    @Override
    public void convertBack(byte[] data) {
        lsn = byteToint(data, 0);
        size = byteToint(data, 4);
        pageId = byteToint(data, 8);
        index = byteToint(data, 12);
        for (int i = 0; i < MAX_SIZE; i++) {
            blockPageIds[i] = byteToint(data, 16 + i * 4);
        }
    }

}
