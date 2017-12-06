package org.seckill.execption;

/**
 * Created by hays on 2017/11/16.
 * 秒杀业务异常
 */
public class SeckillException extends RuntimeException{
    public SeckillException(String s) {
        super(s);
    }

    public SeckillException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
