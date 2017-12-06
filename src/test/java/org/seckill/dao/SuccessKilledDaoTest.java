package org.seckill.dao;

import org.junit.Test;
import org.seckill.SpringJUnitBase;
import org.seckill.entity.SuccessKilled;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

/**
 * Created by hays on 2017/11/14.
 */
public class SuccessKilledDaoTest extends SpringJUnitBase{

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() throws Exception {
        int insertCount = successKilledDao.insertSuccessKilled(1000L, 15813312503L, 1);
        System.out.print("insertCount：" + insertCount);
    }

    @Test
    public void queryByIdWithSeckill() throws Exception {
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(1000L, 15813312503L);
        System.out.print(successKilled);
        System.out.print(successKilled.getSeckill());
    }

}