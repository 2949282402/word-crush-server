package com.example.hjlblog.util;

import com.example.hjlblog.dto.ApiResponse;
import com.example.hjlblog.entiy.User;
import com.example.hjlblog.service.UserService;
import com.example.hjlblog.service.impl.UserServiceImpl;
import io.jsonwebtoken.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class tokenUtil {

    private static final String SECRET_KEY = "hjl-word-crush-secret-key" ;

    private static String getKey(){
        return SECRET_KEY ;
    }

    private static final long EXPIRATION_TIME = 1000*60*60*24*7; //7天

    public static final int TOKEN_VERITY_SUCCESS = 0; //验证成功
    public static final int TOKEN_VERITY_PARAM_ERROR = 1; //参数错误
    public static final int TOKEN_VERITY_SINGLE_ERROR = 2;
    public static final int TOKEN_VERITY_EXPIRED = 3; //token过期
    public static final int TOKEN_VERITY_UNEXPECTED = 4;

    //生成token
    public static String getToken(User user) {
        return Jwts.builder()
                .claim("uid", String.valueOf(user.getId()))
                .claim("username", user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+ EXPIRATION_TIME) )
                .signWith(SignatureAlgorithm.HS256, getKey())
                .compact();
    }

    //验证token并解析
    public static ApiResponse<Map<String, String>> checkToken(String token){
        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(getKey())
                    .parseClaimsJws(token)
                    .getBody();
            if(claims.get("username") == null || claims.get("uid") == null){
                return new ApiResponse<>(TOKEN_VERITY_PARAM_ERROR, "Token参数错误！", null);
            }
            else{
                Map<String, String> userInfo = new HashMap<>();
                userInfo.put("username", String.valueOf(claims.get("username")));
                userInfo.put("uid",  String.valueOf(claims.get("uid")));
                userInfo.put("token", String.valueOf(token));
                return new ApiResponse<>(TOKEN_VERITY_SUCCESS, "验证成功", userInfo);
            }
        }
        catch(SignatureException e){
            return new ApiResponse<>(TOKEN_VERITY_SINGLE_ERROR, "Token签名错误！", null);
        }
        catch (ExpiredJwtException e){
            return new ApiResponse<>(TOKEN_VERITY_EXPIRED, "Token已过期！", null);
        }
        catch (UnsupportedJwtException e){
            return new ApiResponse<>(TOKEN_VERITY_UNEXPECTED, "Token无效！", null);
        }
    }

}
