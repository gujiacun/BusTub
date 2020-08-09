package run.yuyang.db.util;

public class ConvertUtils {

    public  static byte[] intToBytes(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static int byteToint(byte[] bytes, int start) {
        return ((bytes[start] & 0xFF) << 24) + ((bytes[start + 1] & 0xFF) << 16) + ((bytes[start + 2] & 0xFF) << 8) + (bytes[start + 3] & 0xFF);
    }

}
