package run.yuyang.db.storage.page;

public interface Pageable {

    /**
     * 转化出的byte数组大小需为PAGE_SIZE
     */
    byte[] convertTo();

    void convertBack(byte[] data);


}
