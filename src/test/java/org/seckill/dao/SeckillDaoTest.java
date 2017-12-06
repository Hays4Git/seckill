package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.SpringJUnitBase;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by hays on 2017/11/14.
 */
public class SeckillDaoTest extends SpringJUnitBase{

    @Autowired
    private SeckillDao seckillDao;

    @Test
    public void reduceNumber() throws Exception {
        int updateCount = seckillDao.reduceNumber(1000L, new Date());
        System.out.print("updateCount：" + updateCount);
    }

    @Test
    public void queryById() throws Exception {
        long id = 1000;
        Seckill seckill = seckillDao.queryById(id);
        System.out.print(seckill);
    }

    @Test
    public void queryAll() throws Exception {
        List<Seckill> seckillList = seckillDao.queryAll(0, 100);
        seckillList.forEach((seckill -> System.out.print(seckill)));
    }

}