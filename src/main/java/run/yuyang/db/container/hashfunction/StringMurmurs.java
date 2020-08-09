package run.yuyang.db.container.hashfunction;


import run.yuyang.db.container.hashfunction.HashFunction;

import static run.yuyang.db.container.hashfunction.Murmurs.hash;

public class StringMurmurs implements HashFunction<String> {

    @Override
    public Long gethash(String s) {
        return hash(s.getBytes());
    }



}
