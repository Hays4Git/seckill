package org.seckill.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.seckill.SpringJUnitBase;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.execption.RepeatKillException;
import org.seckill.execption.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by hays on 2017/11/20.
 */
public class SeckillServiceImplTest extends SpringJUnitBase{

    private static Log log = LogFactory.getLog(SeckillServiceImplTest.class);
    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> seckillList = seckillService.getSeckillList();
        log.info(seckillList);
    }

    @Test
    public void getById() throws Exception {
        Seckill seckill = seckillService.getById(1000L);
        log.info(seckill);
    }

    @Test
    public void doKill() throws Exception {
        long seckillId = 1001;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        log.info(exposer);
        if(exposer.isExposed()){
            try {
                long phone = 15813362362L;
                String md5 = exposer.getMd5();
                SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, phone, md5);
                log.info(seckillExecution);
            } catch (RepeatKillException e){
                log.info(e.getMessage());
            } catch (SeckillCloseException e){
                log.info(e.getMessage());
            }
        } else {
            log.warn(exposer);
        }
    }

}