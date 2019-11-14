package com.example.demo.dao.rabc;

import com.example.demo.model.entity.rabc.Users;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

/*
Mapper加@Repository注解Service层依赖注入ProductMapper就不会报错红丝波浪线（其实没有错）。
如果Mapper找不到看：application.yml文件MyBatis是否配置。
                    Mapper.xml文件包名称，函数名称对应是否对应，Mapper.xml内容是否错误。
 */
/*
 @Repository需要在Spring中配置扫描地址，然后生成Dao层的Bean才能被注入到Service层中。

 @Mapper不需要配置扫描地址，通过xml里面的namespace里面的接口地址，生成了Bean后注入到Service层中。
 */
//@Repository  //@MapperScan("com.example.demo.dao")
@Mapper
public interface MybatisAnnotationMapper {
    @Insert("INSERT INTO [RABC].[dbo].[Users] ([Account],[Password] ,[Status],[CreateTime],[ModityTime])\n" +
            "     VALUES (#{account}, #{password}, #{status}, #{createtime}, #{moditytime})")
    int insert(Users user);

//    @InsertProvider(type = UsersProvider.class, method = "batchInsert")
//    int batchInsert(List<Users> list);

    //采用脚本的注解方式
    //双引号转义:\"
    @Insert("<script>" +
            "INSERT INTO [RABC].[dbo].[Users] ([Account],[Password] ,[Status],[CreateTime],[ModityTime])" +
            " VALUES" +
            "<foreach collection=\"list\" item=\"item\" index=\"index\" separator=\",\" >" +
            " (#{item.account},#{item.password},#{item.status},#{item.createtime},#{item.moditytime})" +
            "</foreach>" +
            " </script>")
    //参数名称和foreach collection="list" 对应  "list"
    int batchInsert(List<Users> list);

    @Select("SELECT * FROM [RABC].[dbo].[Users] WHERE id = #{id}")
    Users selectById(@Param("id") Integer id);

    @DeleteProvider(type = UsersProvider.class, method = "batchDelete")
    int batchDelete(List<Integer> list);

    @Select("SELECT * FROM [RABC].[dbo].[Users]")
    List<Users> selectAll();


    class UsersProvider {
        /* 批量插入 */
        public String batchInsert(List<Users> list) {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO [RABC].[dbo].[Users]   ([Account],[Password],[Status],[CreateTime],[ModityTime]) VALUES ");
            Integer i = 0;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Users user : list) {
                //(#{account}, #{password}, #{status}), #{createTime}, #{modityTime}
                //单引号用双单引号转义：''
                sb.append(MessageFormat.format("(''{0}'',''{1}'',{2},''{3}'',''{4}'')",
                        user.getAccount(), user.getPassword(), user.getStatus(), simpleDateFormat.format(user.getCreatetime()), simpleDateFormat.format(user.getModitytime())));
                if (i < list.size() - 1) {
                    sb.append(",");
                }
                i++;
            }
            return sb.toString();
        }

        /* 批量删除 */
        public String batchDelete(List<Integer> list) {
            StringBuilder sb = new StringBuilder();
            sb.append("DELETE FROM [RABC].[dbo].[Users] WHERE id IN (");
            for (int i = 0; i < list.size(); i++) {
                sb.append(list.get(i));
                if (i < list.size() - 1)
                    sb.append(",");
            }
            sb.append(")");
            return sb.toString();
        }
    }
}
