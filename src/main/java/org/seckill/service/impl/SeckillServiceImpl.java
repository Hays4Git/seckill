package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatusEnum;
import org.seckill.execption.RepeatKillException;
import org.seckill.execption.SeckillCloseException;
import org.seckill.execption.SeckillException;
import org.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hays on 2017/11/16.
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    private Log log = LogFactory.getLog(SeckillServiceImpl.class);
    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private SuccessKilledDao successKilledDao;
    @Autowired
    private RedisDao redisDao;

    private final static String slat = "fdsfasfasfd#^*$(%)_)@";

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = redisDao.getSeckill(seckillId);
        if(seckill == null){
            seckill = seckillDao.queryById(seckillId);
            if (seckill == null) {//没有秒杀记录
                return new Exposer(false, seckillId);
            } else {
                redisDao.putSeckill(seckill);
            }
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date now = new Date();
        if (now.before(startTime) || now.after(endTime)) {//不在秒杀时间内
            return new Exposer(false, seckillId, now.getTime(), startTime.getTime(), endTime.getTime());
        }
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    @Override
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userphone, String md5) {
        try {
            if (md5 == null || !md5.equals(getMD5(seckillId))) {
                throw new SeckillException(SeckillStatusEnum.DATA_REWRITE.getStatusInfo());
            }
            //执行秒杀逻辑
            Date now = new Date();
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userphone, SeckillStatusEnum.SUCCESS.getStatus());
            if (insertCount <= 0) {
                throw new RepeatKillException(SeckillStatusEnum.REPEAT_KILL.getStatusInfo());
            } else {
                //减库存，热点商品竞争
                int updateCount = seckillDao.reduceNumber(seckillId, now);
                if (updateCount <= 0) {//回滚
                    throw new SeckillCloseException(SeckillStatusEnum.END.getStatusInfo());
                } else {//提交
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userphone);
                    return new SeckillExecution(seckillId, SeckillStatusEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseException e) {
            throw e;
        } catch (RepeatKillException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }
    }

    @Override
    public SeckillExecution executeSeckillProcedure(long seckillId, long userphone, String md5) {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            return new SeckillExecution(seckillId, SeckillStatusEnum.DATA_REWRITE);
        }
        Date now = new Date();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("seckillId", seckillId);
        paramMap.put("phone", userphone);
        paramMap.put("killTime", now);
        paramMap.put("result", null);
        //这里缺少一个status状态赋值，数据库默认-1，后续有需要再修改吧
        try {
            //执行秒杀逻辑
            seckillDao.killByProcedure(paramMap);
            int result = MapUtils.getInteger(paramMap, "result", -2);
            if(result == SeckillStatusEnum.SUCCESS.getStatus()){
                SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId, userphone);
                return new SeckillExecution(seckillId, SeckillStatusEnum.SUCCESS, sk);
            }else{
                return new SeckillExecution(seckillId, SeckillStatusEnum.statusOf(result));
            }
        } catch (Exception e){
            log.error(e.getMessage(), e);
            return new SeckillExecution(seckillId, SeckillStatusEnum.INNER_ERROR);
        }
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
}
