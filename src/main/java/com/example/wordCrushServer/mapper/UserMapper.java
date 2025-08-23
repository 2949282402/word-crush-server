package com.example.wordCrushServer.mapper;

import com.example.wordCrushServer.entiy.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.mybatis.spring.annotation.MapperScan;

@Mapper
@MapperScan
public interface UserMapper {
    @Select("SELECT * FROM user WHERE id = #{id}")
    User getUserById(int id);

    @Insert("INSERT INTO user(username, password) VALUES(#{username}, #{password})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertUser(User user);

    @Select("SELECT * FROM user WHERE username = #{name}")
    User getUserByName(String name);
}
