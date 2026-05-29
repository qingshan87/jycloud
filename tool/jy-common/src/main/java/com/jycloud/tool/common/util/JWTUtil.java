package com.jycloud.tool.common.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jycloud.tool.common.constant.TokenConstant;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;

@Slf4j
public class JWTUtil {
    private static final String USER_ID = "userId";
    private static final String USERNAME = "username";
    private static final String TENANT_ID = "tenantId";
    private static final String CREATE_TIME = "createTime";
    private static final long EXPIRE_TIME = 1000*86400;

    /**
     * 校验 token是否正确
     *
     * @param token  密钥
     * @param username 用户的密码
     * @return 是否正确
     */
    public static boolean verify(String token, String username) {
        try {
            username = StringUtils.lowerCase(username);
            String secret = encryptToken(username);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim(USERNAME, username)
                    .build();
            verifier.verify(token);
            log.info("token is valid");
            return true;
        } catch (Exception e) {
            log.error("token is invalid {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从 token中获取用户名
     *
     * @return token中包含的用户名
     */
    public static String getUsername() {
        String authorization = getHeader(TokenConstant.TOKEN);
        String token = JWTUtil.decryptToken(authorization);
        return JWTUtil.getUsername(token);
    }

    public static String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(USERNAME).asString();
        } catch (JWTDecodeException e) {
            log.error("getUsername  error：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 从 token中获取用户ID
     *
     * @return token中包含的用户ID
     */
    public static String getUserId(){
        String authorization = getHeader(TokenConstant.TOKEN);
        String token = JWTUtil.decryptToken(authorization);
        return JWTUtil.getUserId(token);
    }
    public static String getUserId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(USER_ID).asString();
        } catch (JWTDecodeException e) {
            log.error("getUserId  error：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 从 token中获取租户ID
     *
     * @return token中包含的租户ID
     */
    public static String getTenantId(){
        try {
            String authorization = getHeader(TokenConstant.TOKEN);
            String token = JWTUtil.decryptToken(authorization);
            return StringUtils.isNotBlank(token) ? JWTUtil.getTenantId(token) : null;
        } catch (Exception e) {
            log.error("getTenantId error：{}", e.getMessage());
            return null;
        }
    }
    public static String getTenantId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(TENANT_ID).asString();
        } catch (JWTDecodeException e) {
            log.error("getTenantId error：{}", e.getMessage());
            return null;
        }
    }



    /**
     * 从 token中获取过期时间 Expiration
     *
     * @return token中包含的 Expiration参数
     */
    public static Long getCreateTime() {
        try {
            String authorization = getHeader(TokenConstant.TOKEN);
            String token = JWTUtil.decryptToken(authorization);
            return StringUtils.isNotBlank(token) ? JWTUtil.getCreateTime(token) : null;
        } catch (JWTDecodeException e) {
            log.error("getExpiration error：{}", e.getMessage());
            return null;
        }
    }
    public static Long getCreateTime(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(CREATE_TIME).asLong();
        } catch (JWTDecodeException e) {
            log.error("getExpiration error：{}", e.getMessage());
            return null;
        }
    }

    public static String getHeader(String name){
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            if (request == null){
                return null;
            }
            String authorization = request.getHeader(name);
            return authorization;
        } catch (Exception e) {
            log.error("getHeader error：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 从 token中获取过期时间
     *
     * @return token中包含的租户ID
     */
    public static Date getExpires() {
        try {
            String authorization = getHeader(TokenConstant.TOKEN);
            String token = JWTUtil.decryptToken(authorization);
            return StringUtils.isNotBlank(token) ? JWTUtil.getExpires(token) : null;
        } catch (Exception e) {
            log.error("getExpires error：{}", e.getMessage());
            return null;
        }
    }
    public static Date getExpires(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("exp").asDate();
        } catch (JWTDecodeException e) {
            log.error("getExpires error：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 从 token中获取剩余的过期时间
     *
     * @return 单位:秒
     */
    public static Integer getRemainExpires() {
        try {
            String authorization = getHeader(TokenConstant.TOKEN);
            String token = JWTUtil.decryptToken(authorization);
            return StringUtils.isNotBlank(token) ? JWTUtil.getRemainExpires(token) : null;
        } catch (JWTDecodeException e) {
            log.error("getRemainExpires error：{}", e.getMessage());
            return null;
        }
    }
    public static Integer getRemainExpires(String token) {
        try {
            Date expires = JWTUtil.getExpires(token);

            return DateUtil.specTimeStamp(expires) - DateUtil.nowTimeStamp();
        } catch (JWTDecodeException e) {
            log.error("getRemainExpires error：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 生成 token
     * @param username
     * @param userId
     * @param tenantId
     * @return
     */
    public static String createToken(String username, String userId, String tenantId) {
        try {
            username = StringUtils.lowerCase(username);
            String secret = encryptToken(username);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withClaim(USERNAME, username)
                    .withClaim(USER_ID, userId)
                    .withClaim(TENANT_ID, tenantId)
                    .withClaim(CREATE_TIME,System.currentTimeMillis())
                    .sign(algorithm);
        } catch (Exception e) {
            log.error("sign3 error：{}", e);
            return null;
        }
    }


    /**
     * token 加密
     *
     * @param token token
     * @return 加密后的 token
     */
    public static String encryptToken(String token) {
        try {
            return CommonUtil.encryptStr(token,TokenConstant.TOKEN_KEY);
        } catch (Exception e) {
            log.info("token加密失败：", e);
            return null;
        }
    }

    /**
     * token 解密
     *
     * @param encryptToken 加密后的 token
     * @return 解密后的 token
     */
    public static String decryptToken(String encryptToken) {
        try {
            return CommonUtil.decryptStr(encryptToken, TokenConstant.TOKEN_KEY);
        } catch (Exception e) {
            log.info("token解密失败：", e);
            return null;
        }
    }




}
