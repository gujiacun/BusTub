package run.yuyang.db.container.hashfunction;

public interface HashFunction<T> {

    Long gethash(T t);

}
