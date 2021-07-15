package com.example.demo.controller;

import com.example.demo.model.pojo.Student;
import com.example.demo.model.viewModel.MessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    @GetMapping("/object")
    public MessageResult<Void> redisObjectTest() {

        try {

            Student student1 = new Student();
            student1.setName("fancky");
            student1.setAge(25);
            Student student2 = new Student();
            student2.setName("li");
            student2.setAge(26);
            Student student3 = new Student();
            student3.setName("rui");
            student3.setAge(27);

            //region String

            // 数据结构
            //StringRedisKey1  StringValue1
            //StringRedisKey1  StringValue2
            //StringRedisKey1  StringValue3
            //    *                *
            //    *                *
            //    *                *
            ValueOperations<String, Student> valueOperations = redisTemplate.opsForValue();
            valueOperations.set("stringKey1", student1);
            valueOperations.set("stringKey2", student2);
            valueOperations.set("stringKey3", student3);
            //取值
            Student strVal = valueOperations.get("stringKey1");

            //删除
            String strKey3 = "stringKey3";
            Boolean delByStringKey = redisTemplate.delete("stringKey2");
            Boolean del = redisTemplate.delete(strKey3);
            Boolean exists = redisTemplate.hasKey(strKey3);
            //endregion

            //region List

            //数据结构
            //ListRedisKey1   ListValue1
            //                ListValue2
            //                ListValue3
            //                    *
            //                    *
            //                    *
            //ListRedisKey2   ListValue1
            //                ListValue2
            //                ListValue3
            //                    *
            //                    *
            //                    *
            ListOperations<String, Student> listOperations = redisTemplate.opsForList();
            listOperations.leftPush("listKey1", student1);
            listOperations.leftPush("listKey1", student3);
            listOperations.leftPush("listKey2", student1);
            listOperations.leftPush("listKey2", student2);
            listOperations.leftPush("listKey2", student3);
            listOperations.leftPush("listKey3", student3);


            //取值--根据key 取出该key 所有值 list 集合
            List<Student> listVal3 = listOperations.range("listKey1", 0, -1);
            //出队一个并删除  --返回出队的值
            Student listVal = listOperations.leftPop("listKey1");
            //删除key
            redisTemplate.delete("listKey2");
            //endregion

            //region Hash

            // 数据结构
            //HashSetRedisKey1  HashSetKey1 HashSetValue1
            //                  HashSetKey2 HashSetValu2
            //                  HashSetKey3 HashSetValu3
            //                       *            *
            //                       *            *
            //                       *            *
            //HashSetRedisKey2  HashSetKey1 HashSetValue1
            //                  HashSetKey2 HashSetValu2
            //                  HashSetKey3 HashSetValu3
            //                      *             *
            //                      *             *
            //                      *             *
            HashOperations<String, String, Student> hashOperations = redisTemplate.opsForHash();
            hashOperations.put("hashKey1", "hashField1", student1);
            hashOperations.put("hashKey1", "hashField2", student2);
            hashOperations.put("hashKey1", "hashField3", student3);
            hashOperations.put("hashKey2", "hashField1", student1);
            hashOperations.put("hashKey2", "hashField2", student2);
            hashOperations.put("hashKey3", "hashField3", student3);

            //取值
            Student hashVal1 = hashOperations.get("hashKey1", "hashField1");
            //删除
            //  hashOperations.delete("hashKey1", new String[]{"hashField1","hashField12"});
            //返回删除的个数
            Long deleteCount = hashOperations.delete("hashKey1", "hashField1", "hashField12");
            redisTemplate.delete("hashKey2");
            //endregion

            //region Set

            //数据结构
            //SetRedisKey1    SetValue1
            //                SetValue2
            //                SetValue3
            //                    *
            //                    *
            //                    *
            //SetRedisKey2    SetValue1
            //                SetValue2
            //                SetValue3
            //                    *
            //                    *
            //                    *

            SetOperations<String, Student> setOperations = redisTemplate.opsForSet();
            setOperations.add("setKey1", student1,student2,student3);
            setOperations.add("setKey2", student1,student2);
            setOperations.add("setKey3", student3);
            //HashSet--Set
            //取值
            Set<Student> setStr = setOperations.members("setKey1");
            List<Student> studentList=new ArrayList<>(setStr);

            Iterator<Student> stringIterator = setStr.iterator();
            while (stringIterator.hasNext()) {
                Student str = stringIterator.next();
                Integer n = 0;
            }

            //删除

            //删除集合中的一个值
            setOperations.remove("setKey1", student2);

            redisTemplate.delete("setKey2");
            //endregion


            //region SortedSet

            //数据结构
            //                                           Score
            //SortedSetRedisKey1    SortedSetValue1        1
            //                      SortedSetValue2        2
            //                      SortedSetValue3        3
            //                           *                 *
            //                           *
            //                           *
            //SortedSetRedisKey2    SortedSetValue1        1
            //                      SortedSetValue2        2
            //                      SortedSetValue3        3
            //                           *                 *
            //                           *                 *
            //                           *                 *


            ZSetOperations<String, Student> zSetOperations = redisTemplate.opsForZSet();
            zSetOperations.add("zSetKey1", student1, 1);
            zSetOperations.add("zSetKey1", student2, 11);
            zSetOperations.add("zSetKey1", student3, 12);
            zSetOperations.add("zSetKey2", student1, 4);
            zSetOperations.add("zSetKey2", student2, 4);
            zSetOperations.add("zSetKey3", student1, 3);

            //获取
            //取所有
            Set<Student> zSetStr = zSetOperations.range("zSetKey1", 0, -1);
            //取三个
            Set<Student> zSetStr1 = zSetOperations.range("zSetKey1", 0, 2);
            studentList=new ArrayList<>(zSetStr1);
            Iterator<Student> zSetStringIterator = zSetStr.iterator();
            while (zSetStringIterator.hasNext()) {
                Student str = zSetStringIterator.next();
                Integer n = 0;
            };


            //删除
            //删除指定Key中的值
            zSetOperations.remove("zSetKey1", student2,student3);
            //当key中的值都删除，就把key也删除
            zSetOperations.remove("zSetKey2", student1, student2);
            
            redisTemplate.delete("zSetKey1");
            //endregion
        } catch (Exception ex) {
            String msg = ex.toString();
            Integer m = 0;
        }
        return null;
    }
}
