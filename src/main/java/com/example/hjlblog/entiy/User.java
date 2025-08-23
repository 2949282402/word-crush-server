package com.example.hjlblog.entiy;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Data                       // getter/setter, toString, equals, hashCode
@NoArgsConstructor          // JPA 要求的无参构造
@AllArgsConstructor         // 全参构造
@Builder                    // 开启 builder 模式
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //用户名
    private String username;

    //密码
    private String password;
}
