package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.execption.RepeatKillException;
import org.seckill.execption.SeckillCloseException;
import org.seckill.execption.SeckillException;

import java.util.List;

/**
 * Created by hays on 2017/11/16.
 */
public interface SeckillService {
    /***
     * 获取所有秒杀记录
     * @return
     */
    List<Seckill> getSeckillList();

    /***
     * 获取一条秒杀记录
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /***
     * 暴露秒杀地址
     * @param seckillId
     * @return
     */
    Exposer exportSeckillUrl(long seckillId);

    /***
     * 执行秒杀
     * @param seckillId
     * @param userphone
     * @param md5
     * @return
     * @throws SeckillException
     * @throws SeckillCloseException
     * @throws RepeatKillException
     */
    SeckillExecution executeSeckill(long seckillId, long userphone, String md5) throws SeckillException, SeckillCloseException, RepeatKillException;
}
