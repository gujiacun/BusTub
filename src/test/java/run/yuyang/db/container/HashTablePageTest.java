package run.yuyang.db.container;

import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import run.yuyang.db.buffer.BufferPoolManager;
import run.yuyang.db.storage.disk.DiskManager;
import run.yuyang.db.storage.page.HashTableHeaderPage;
import run.yuyang.db.storage.page.Page;

import java.io.File;

@Slf4j
public class HashTablePageTest extends TestCase {

    @Test
    public void test1() {

        DiskManager diskManager = new DiskManager("test.db");
        BufferPoolManager poolManager = new BufferPoolManager(diskManager, null, 5);
        Page page0 = poolManager.newPageImpl();
        HashTableHeaderPage headerPage = new HashTableHeaderPage();
        page0.setData(headerPage);

        // 设置一些字段
        for (int i = 0; i < 11; i++) {
            headerPage.setLsn(i);
            assertEquals(headerPage.getLsn(), i);
            headerPage.setPageId(i);
            assertEquals(headerPage.getPageId(), i);
            headerPage.setSize(i);
            assertEquals(headerPage.getSize(), i);
        }

        //添加一些虚拟的block pages
        for (int i = 0; i < 10; i++) {
            headerPage.addBlockPageIds(i);
            assertEquals(i + 1, headerPage.numBlocks());
        }
        // 使用结束
        log.debug("id: {}",page0.getPageId());
        log.debug("data : {}",page0.getData());
        poolManager.unpinPageImpl(page0.getPageId(), true);
        File file = new File("test.db");
        file.delete();
        file = new File("test.log");
        file.delete();
    }

    @Test
    public void test2() {
        DiskManager diskManager = new DiskManager("test.db");
        BufferPoolManager poolManager = new BufferPoolManager(diskManager, null, 5);
        Page page0 = poolManager.newPageImpl();
        byte[] datas = page0.getData();

    }

}
