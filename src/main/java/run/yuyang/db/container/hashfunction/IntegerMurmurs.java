package run.yuyang.db.container.hashfunction;

import run.yuyang.db.container.hashfunction.HashFunction;

import static run.yuyang.db.container.hashfunction.Murmurs.hash;
import static run.yuyang.db.util.ConvertUtils.intToBytes;

public class IntegerMurmurs implements HashFunction<Integer> {
    @Override
    public Long gethash(Integer integer) {

        return hash(intToBytes(integer));
    }

}
