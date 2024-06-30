package org.example.admin.common.biz.user;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.example.admin.common.convention.exception.ClientException;
import org.example.admin.common.convention.result.Results;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.example.admin.common.enums.UserErrorCodeEnum.USER_TOKEN_FALL;

@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {
    private final StringRedisTemplate stringRedisTemplate;
    private final List<String> ignore_uri = Lists.newArrayList(
            "/api/short-link/admin/v1/user/login",
            "/api/short-link/admin/v1/user/has-username/",
            "/api/short-link/admin/v1/user");

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpServletRequest.getRequestURI();
        if (!ignore_uri.contains(requestURI)) {
            String method = httpServletRequest.getMethod();
            if (!(Objects.equals(requestURI, "/api/short-link/admin/v1/user")&&Objects.equals(method,"POST"))){
                String username = httpServletRequest.getHeader("username");
                String token = httpServletRequest.getHeader("token");
                if (!StrUtil.isAllNotBlank(username, token)) {
                  returnJson((HttpServletResponse)servletResponse,JSON.toJSONString(Results.failure(new ClientException(USER_TOKEN_FALL))));
                  return;
                }
                Object userInfoStr;
                try {
                    userInfoStr = stringRedisTemplate.opsForHash().get("login_" + username, token);
                    if (userInfoStr == null) {
                        throw new ClientException(USER_TOKEN_FALL);
                    }
                } catch (Exception e) {
                    returnJson((HttpServletResponse)servletResponse,JSON.toJSONString(Results.failure(new ClientException(USER_TOKEN_FALL))));
                    return;
                }
                UserInfoDTO userInfoDTO = JSON.parseObject(userInfoStr.toString(), UserInfoDTO.class);
                UserContext.setUser(userInfoDTO);
            }
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }

    private void returnJson(HttpServletResponse response, String json) throws Exception {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(json);

        } catch (IOException e) {
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}