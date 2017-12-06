package org.seckill.execption;

/**
 * Created by hays on 2017/11/16.
 * 重复秒杀异常
 */
public class RepeatKillException extends SeckillException {
    public RepeatKillException(String s) {
        super(s);
    }

    public RepeatKillException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
