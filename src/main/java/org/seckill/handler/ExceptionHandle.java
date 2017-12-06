package org.seckill.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seckill.dto.SeckillResult;
import org.seckill.execption.RepeatKillException;
import org.seckill.execption.SeckillCloseException;
import org.seckill.execption.SeckillException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionHandle {

    private final static Log log = LogFactory.getLog(ExceptionHandle.class);

    @SuppressWarnings("rawtypes")
	@ExceptionHandler(value = Exception.class)
    @ResponseBody
    public SeckillResult handle(Exception e) {
        if (e instanceof RepeatKillException || e instanceof SeckillCloseException) {
            return new SeckillResult(false, e.getMessage());
        } if(e instanceof SeckillException){
            log.error(e.getMessage(), e);
            return new SeckillResult(false, e.getMessage());
        }else {
            log.error(e.getMessage(), e);
            return new SeckillResult(false, "系统异常：" + e.getMessage());
        }
    }
}
