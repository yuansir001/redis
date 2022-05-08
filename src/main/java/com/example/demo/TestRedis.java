package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TestRedis {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    @Qualifier("ooxx")
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    public void test(){
//        stringRedisTemplate.opsForValue().set("hello01", "china");
//        System.out.println(stringRedisTemplate.opsForValue().get("hello01"));

        /*HashOperations<String, Object, Object> hash = stringRedisTemplate.opsForHash();
        hash.put("sean", "name", "zhouzhilei");
        hash.put("sean", "age", "22");
        System.out.println(hash.entries("sean"));*/

        Person person = new Person();
        person.setName("zhangsan");
        person.setAge(11);

        //stringRedisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));

        Jackson2HashMapper jh = new Jackson2HashMapper(objectMapper, false);
        stringRedisTemplate.opsForHash().putAll("sean011", jh.toHash(person));

        Map map = stringRedisTemplate.opsForHash().entries("sean011");
        Person person1 = objectMapper.convertValue(map, Person.class);
        System.out.println(person1.getName());



        RedisConnection connection = stringRedisTemplate.getConnectionFactory().getConnection();
        connection.subscribe(new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] bytes) {
                byte[] body = message.getBody();
                System.out.println(new String(body));
            }
        }, "ooxx".getBytes());

        while (true){
            stringRedisTemplate.convertAndSend("ooxx", "xixi: hello");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
