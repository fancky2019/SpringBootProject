package com.example.demo.controller;

import com.example.demo.model.viewModel.MessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.Set;

@RestController
@RequestMapping("/redisTest")
public class RedisTestController {

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("")
    public MessageResult<Void> redisTest() {

        try {

            //region String
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            valueOperations.set("stringKey1", "stringKeyValue1");
            valueOperations.set("stringKey2", "stringKeyValue2");
            valueOperations.set("stringKey3", "stringKeyValue3");
            //取值
            String strVal = valueOperations.get("stringKey1");
            String strVal3 = valueOperations.get("stringKey3", 0, -1);
            //删除
            strVal3 = "stringKey3";
            Boolean delByStringKey = redisTemplate.delete("stringKey1");
            Boolean del = redisTemplate.delete(strVal3);
            Boolean exists = redisTemplate.hasKey(strVal3);
            //endregion

            //region List
            ListOperations<String, String> listOperations = redisTemplate.opsForList();
            listOperations.leftPush("listKey1", "listKeyValue1");
            listOperations.leftPush("listKey2", "listKeyValue2");
            listOperations.leftPush("listKey3", "listKeyValue3");
            listOperations.leftPush("listKey4", "listKeyValue4");
            //取值
            String listVal4 = listOperations.leftPop("listKey1");
            //删除
            String listVal = listOperations.leftPop("listKey1");
            redisTemplate.delete("listKey2");
            //endregion

            //region Hash
            HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
            hashOperations.put("hashKey1", "hashField1", "hashValue1");
            hashOperations.put("hashKey1", "hashField12", "hashValue12");
            hashOperations.put("hashKey1", "hashField13", "hashValue13");
            hashOperations.put("hashKey2", "hashField2", "hashValue2");
            hashOperations.put("hashKey3", "hashField3", "hashValue3");

            //取值
            String hashVal1 = hashOperations.get("hashKey1", "hashField1");
            //删除
            //  hashOperations.delete("hashKey1", new String[]{"hashField1","hashField12"});
            hashOperations.delete("hashKey1", "hashField1", "hashField12");
            redisTemplate.delete("hashKey2");
            //endregion

            //region Set
            SetOperations<String, String> setOperations = redisTemplate.opsForSet();
            setOperations.add("setKey1", "setValue1,setValue2,setValue3,setValue4");
            setOperations.add("setKey2", "setValue2");
            setOperations.add("setKey3", "setValue3");
            setOperations.add("setKey4", "setValue4");
            //取值
            Set<String> setStr = setOperations.members("setKey1");
            Iterator<String> stringIterator = setStr.iterator();
            while (stringIterator.hasNext()) {
                String str = stringIterator.next();
                Integer n = 0;
            }
            //删除
            //可删除
//            setOperations.remove("setKey1", "setValue1,setValue2,setValue3,setValue4");
            //删除不掉
//            setOperations.remove("setKey1", "setValue1,setValue2");
            redisTemplate.delete("setKey2");
            //endregion


            //region SortedSet
            ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
            zSetOperations.add("zSetKey1", "zSetValue1", 1);
            zSetOperations.add("zSetKey1", "zSetValue11", 11);
            zSetOperations.add("zSetKey1", "zSetValue12", 12);
            zSetOperations.add("zSetKey1", "zSetValue13", 13);
            zSetOperations.add("zSetKey2", "zSetValue2", 4);
            zSetOperations.add("zSetKey3", "zSetValue3", 3);
            zSetOperations.add("zSetKey4", "zSetValue4", 2);
            //获取
            //取所有
            Set<String> zSetStr = zSetOperations.range("zSetKey1", 0, -1);
            //取三个
            Set<String> zSetStr1 = zSetOperations.range("zSetKey1", 0, 2);
            Iterator<String> zSetStringIterator = zSetStr.iterator();
            while (zSetStringIterator.hasNext()) {
                String str = zSetStringIterator.next();
                Integer n = 0;
            }
            //删除
            redisTemplate.delete("zSetKey1");
            //endregion
        } catch (Exception ex) {
            String msg = ex.toString();
            Integer m = 0;
        }
        return null;
    }
}
