package org.seckill.enums;

/**
 * 常量数据字典
 * Created by hays on 2017/11/20.
 */
public enum SeckillStatusEnum {
    SUCCESS(1, "秒杀成功"),
    END(0, "秒杀结束"),
    REPEAT_KILL(-1, "重复秒杀"),
    INNER_ERROR(-2, "系统异常"),
    DATA_REWRITE(-3, "数据篡改");

    public static SeckillStatusEnum statusOf(int key){
        for (SeckillStatusEnum status : values()){
            if(status.getStatus() == key){
                return status;
            }
        }
        return null;
    }

    private int status;
    private String statusInfo;

    SeckillStatusEnum(int status, String statusInfo) {
        this.status = status;
        this.statusInfo = statusInfo;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusInfo() {
        return statusInfo;
    }

}
