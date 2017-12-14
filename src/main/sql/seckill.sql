-- 秒杀执行存储过程
-- 结束符号 ;转换为$$
DELIMITER $$
-- 参数：in输入参数  out输出参数
-- row_count() 返回上一条修改类型sql的影响行数
-- row_count() 0:未修改  >0:修改的记录数  <0:sql错误/未执行
CREATE PROCEDURE seckill.execute_seckill(
  in v_seckill_id bigint,
  in v_phone bigint,
  in v_kill_time timestamp,
  out r_result int
)
  BEGIN
    DECLARE insert_count int DEFAULT 0;
    START TRANSACTION ;
    insert ignore into success_killed (seckill_id, user_phone, create_time) values (v_seckill_id, v_phone, v_kill_time);
    select row_count() into insert_count;
    if(insert_count = 0) THEN
      ROLLBACK ;
      set r_result = -1;
    elseif(insert_count < 0) THEN
      ROLLBACK ;
      set r_result = -2;
    else
      update seckill set number = number - 1 and end_time > v_kill_time and start_time < v_kill_time and number > 0;
      select row_count() into insert_count;
      if(insert_count = 0) THEN
        ROLLBACK ;
        set r_result = 0;
      elseif(insert_count < 0) THEN
        ROLLBACK ;
        set r_result = -2;
      else
        COMMIT ;
        set r_result = 1;
      end if;
    end if;
  END;
$$
-- 还原
DELIMITER ,,

-- 删除存储过程
-- DROP PROCEDURE seckill.execute_seckill;

-- 执行存储过程
-- set @r_result = -3;
-- call execute_seckill(1001, 15813312502, now(), @r_result);
-- 获取结果
-- select @r_result;