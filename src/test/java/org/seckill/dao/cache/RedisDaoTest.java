package org.seckill.dao.cache;

import org.junit.Test;
import org.seckill.SpringJUnitBase;
import org.seckill.dao.SeckillDao;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by hays on 2017/12/11.
 */
public class RedisDaoTest extends SpringJUnitBase{

    private long id = 1001;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private SeckillDao seckillDao;

    @Test
    public void testSeckill() throws Exception {
        Seckill seckill = redisDao.getSeckill(id);
        if(seckill == null){
            seckill = seckillDao.queryById(id);
            if(seckill != null){
                String result = redisDao.putSeckill(seckill);
                System.out.println(result);
                seckill = redisDao.getSeckill(id);
                System.out.println(seckill);
            }
        }
    }

}