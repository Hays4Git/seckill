package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.Seckill;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by hays on 2017/12/6.
 */
public class RedisDao {
    private final JedisPool jedisPool;
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);
    private final String keyPrefix = "seckill:";
    private final int defaultTimeOut = 60*60;

    public RedisDao(String ip, int port){
        jedisPool = new JedisPool(ip, port);
    }

    public Seckill getSeckill(long seckillId){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //使用protostuff序列化，更好的性能
            String key = keyPrefix + seckillId;
            byte[] value = jedis.get(key.getBytes());
            if(value != null){
                Seckill seckill = schema.newMessage();
                ProtostuffIOUtil.mergeFrom(value, seckill, schema);
                return seckill;
            }
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return null;
    }

    public String putSeckill(Seckill seckill){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String key = keyPrefix + seckill.getSeckillId();
            byte[] value = ProtostuffIOUtil.toByteArray(seckill, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
            String result = jedis.setex(key.getBytes(), defaultTimeOut, value);
            return result;
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }
}
