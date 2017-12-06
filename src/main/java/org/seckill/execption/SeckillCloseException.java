package org.seckill.execption;

/**
 * Created by hays on 2017/11/16.
 * 秒杀关闭异常
 */
public class SeckillCloseException extends SeckillException {
    public SeckillCloseException(String s) {
        super(s);
    }

    public SeckillCloseException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
