package com.perye.dokit.service;

import com.perye.dokit.security.JwtUser;
import com.perye.dokit.security.OnlineUser;
import com.perye.dokit.utils.EncryptUtils;
import com.perye.dokit.utils.PageUtil;
import com.perye.dokit.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@SuppressWarnings({"unchecked","all"})
public class OnlineUserService {

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.online}")
    private String onlineKey;

    private final RedisTemplate redisTemplate;

    public OnlineUserService(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(JwtUser jwtUser, String token, HttpServletRequest request){
        String job = jwtUser.getDept() + "/" + jwtUser.getJob();
        String ip = StringUtils.getIp(request);
        String browser = StringUtils.getBrowser(request);
        String address = StringUtils.getCityInfo(ip);
        OnlineUser onlineUser = null;
        try {
            onlineUser = new OnlineUser(jwtUser.getUsername(), job, browser , ip, address, EncryptUtils.desEncrypt(token), new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        redisTemplate.opsForValue().set(onlineKey + token, onlineUser);
        redisTemplate.expire(onlineKey + token,expiration, TimeUnit.MILLISECONDS);
    }

    public Page<OnlineUser> getAll(String filter, Pageable pageable){
        List<String> keys = new ArrayList<>(redisTemplate.keys(onlineKey + "*"));
        Collections.reverse(keys);
        List<OnlineUser> onlineUsers = new ArrayList<>();
        for (String key : keys) {
            OnlineUser onlineUser = (OnlineUser) redisTemplate.opsForValue().get(key);
            if(StringUtils.isNotBlank(filter)){
                if(onlineUser.toString().contains(filter)){
                    onlineUsers.add(onlineUser);
                }
            } else {
                onlineUsers.add(onlineUser);
            }
        }
        Collections.sort(onlineUsers, (o1, o2) -> {
            return o2.getLoginTime().compareTo(o1.getLoginTime());
        });
        return new PageImpl<OnlineUser>(
                PageUtil.toPage(pageable.getPageNumber(),pageable.getPageSize(),onlineUsers),
                pageable,
                keys.size());
    }

    public void kickOut(String val) throws Exception {
        String key = onlineKey + EncryptUtils.desDecrypt(val);
        redisTemplate.delete(key);
    }

    public void logout(String token) {
        String key = onlineKey + token;
        redisTemplate.delete(key);
    }
}
