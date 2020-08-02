package run.yuyang.db.storage.page;

import static run.yuyang.db.util.Config.PAGE_SIZE;

/*
 * Header format (size in byte, 16 bytes in total):
 * -------------------------------------------------------------
 * | LSN (4) | Size (4) | PageId(4) | NextBlockIndex(4)
 * -------------------------------------------------------------
 */
public class HashTableHeaderPage implements Pageable {

    public static int MAX_SIZE = PAGE_SIZE / 4 - 4;

    private int lsn;
    private int size;
    private int pageId;
    private int nextBlockPage;
    private int[] blockPageIds = new int[MAX_SIZE];
    private int index = 0;


    public int getLsn() {
        return lsn;
    }

    public int getSize() {
        return size;
    }

    public int getPageId() {
        return pageId;
    }

    public int getNextBlockPage() {
        return nextBlockPage;
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

    public void setNextBlockPage(int nextBlockPage) {
        this.nextBlockPage = nextBlockPage;
    }

    public void addBlockPageIds(int pageId) {
        blockPageIds[index++] = pageId;
    }

    public int[] getBlockPageIds() {
        return blockPageIds;
    }

    public void setBlockPageIds(int[] blockPageIds) {
        this.blockPageIds = blockPageIds;
    }

    public int numBlocks() {
        return index;
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
        //转化nextBlockPage
        temp = intToBytes(nextBlockPage);
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
        nextBlockPage = byteToint(data, 12);
        for (int i = 0; i < MAX_SIZE; i++) {
            blockPageIds[i] = byteToint(data, 16 + i * 4);
        }
    }

    private byte[] intToBytes(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    private int byteToint(byte[] bytes, int start) {
        return ((bytes[start] & 0xFF) << 24) + ((bytes[start + 1] & 0xFF) << 16) + ((bytes[start + 2] & 0xFF) << 8) + (bytes[start + 3] & 0xFF);
    }
}
