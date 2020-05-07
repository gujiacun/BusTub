package run.yuyang.db.buffer;

import lombok.extern.slf4j.Slf4j;
import run.yuyang.db.recovery.LogManger;
import run.yuyang.db.storage.disk.DiskManager;
import run.yuyang.db.storage.page.Page;

import java.util.*;

import static run.yuyang.db.util.Config.PAGE_SIZE;

/**
 * @author YuYang
 */
@Slf4j
public class BufferPoolManager {

    private final DiskManager diskManager;
    private final LogManger logManger;
    private final int poolSize;
    private final ClockReplacer clockReplacer;
    private final Page[] pages;
    private final LinkedList<Integer> freeList = new LinkedList<>();
    private final Object latch = new Object();
    private final HashMap<Integer, Integer> map = new HashMap<>();

    public BufferPoolManager(DiskManager diskManager, LogManger logManger, int poolSize) {
        this.diskManager = diskManager;
        this.logManger = logManger;
        this.poolSize = poolSize;
        clockReplacer = new ClockReplacer(poolSize);
        pages = new Page[poolSize];
        for (int i = 0; i < poolSize; i++) {
            freeList.add(i);
        }
    }

    /**
     * 从缓冲池中获取请求的页面
     *
     * @param pageId 页面id
     * @return 对应的page
     */
    public Page fetchPageImpl(int pageId) {

        if (map.containsKey(pageId)) {
            clockReplacer.pin(map.get(pageId));
            freeList.remove(map.get(pageId));
            return pages[map.get(pageId)];
        }
        int frameId;
        if (!freeList.isEmpty()) {
            frameId = freeList.pollFirst();
        } else {
            frameId = clockReplacer.victim();

            if (frameId == -1) {
                return null;
            }
            if (pages[frameId].isDirty()) {
                flushPageImpl(pages[frameId].getPageId());
            }
            map.remove(pageId);
        }
        map.put(pageId, frameId);
        Page page = new Page();
        page.setPageId(pageId);
        page.setPinCount(1);
        page.setDirty(false);
        byte[] data = new byte[PAGE_SIZE];
        diskManager.readPage(pageId, data);
        page.setData(data);
        pages[frameId] = page;
        return page;
    }


    /**
     * 取消指定页面page的固定
     *
     * @param pageId  page_id要取消固定的页面的ID
     * @param isDirty is_dirty如果页面应标记为脏，则为true，否则为false
     * @return 如果在此调用之前页面固定计数<= 0 ， 则返回false ， 否则返回true
     */
    public boolean unpinPageImpl(int pageId, boolean isDirty) {

        if (map.containsKey(pageId)) {

            if (pages[map.get(pageId)].getPinCount() <= 0) {
                return false;
            }
            pages[map.get(pageId)].setDirty(pages[map.get(pageId)].isDirty() || isDirty);
            pages[map.get(pageId)].setPinCount(pages[map.get(pageId)].getPinCount() - 1);
            if (pages[map.get(pageId)].getPinCount() == 0) {
                clockReplacer.unpin(map.get(pageId));
            }
            return true;
        }
        return false;
    }

    /**
     * 将目标页面刷新到磁盘。
     *
     * @param pageId page_id要刷新的页面的ID，不能为INVALID_PAGE_ID
     * @return 如果在页面表中找不到该页面，则返回false，否则返回true
     */
    public boolean flushPageImpl(int pageId) {
        if (map.containsKey(pageId) && pages[map.get(pageId)].isDirty()) {
            pages[map.get(pageId)].setDirty(false);
            diskManager.writePage(pageId, pages[map.get(pageId)].getData());
            return true;
        }
        return false;
    }

    /**
     * 在buffer pool中创建一个新的page
     *
     * @return 新的page
     */
    public Page newPageImpl() {

        int frameId;
        if (!freeList.isEmpty()) {
            frameId = freeList.pollFirst();

        } else {
            frameId = clockReplacer.victim();
            if (frameId == -1) {
                return null;
            }
            if (pages[frameId].isDirty()) {
                flushPageImpl(pages[frameId].getPageId());
            }
            map.remove(pages[frameId].getPageId());
        }
        int pageId = diskManager.allocatePage();
        map.put(pageId, frameId);
        Page page = new Page();
        page.setPageId(pageId);
        page.setPinCount(1);
        page.setDirty(false);
        pages[frameId] = page;
        return page;
    }

    /**
     * 从缓冲池中删除页面
     *
     * @param pageId page_id要删除的页面的ID
     * @return 如果该页面存在但无法删除，则返回false；如果该页面不存在或删除成功，则返回true
     */
    public boolean deletePageImpl(int pageId) {
        diskManager.deallocatePage(pageId);
        if (map.containsKey(pageId)) {
            if (pages[map.get(pageId)].getPinCount() > 0) {
                return false;
            }
            pages[pageId] = null;
            freeList.add(map.get(pageId));
            map.remove(pageId);
        }

        return true;
    }

    /**
     * 将缓冲池中的所有页面刷新到磁盘。
     */
    public void flushAllPagesImpl() {
        for (Integer integer : map.keySet()) {
            flushPageImpl(integer);
        }
    }
}
