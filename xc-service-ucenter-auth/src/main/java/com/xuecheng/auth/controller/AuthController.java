package com.xuecheng.auth.controller;

import com.xuecheng.api.auth.AuthControllerApi;
import com.xuecheng.auth.service.AuthService;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.domain.ucenter.response.JwtResult;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author hftang
 * @date 2019-04-08 17:32
 * @desc
 */

@RestController
public class AuthController implements AuthControllerApi {

    @Autowired
    AuthService authService;

    @Value("${auth.clientId}")
    String clientId;
    @Value("${auth.clientSecret}")
    String clientSecret;

    @Value("${auth.cookieDomain}")
    String cookieDomain;
    @Value("${auth.cookieMaxAge}")
    int cookieMaxAge;

    @Override
    @PostMapping("/userlogin")
    public LoginResult login(LoginRequest loginRequest) {

        //校验
        if (loginRequest == null || StringUtils.isEmpty(loginRequest.getUsername())) {
            ExceptionCast.cast(AuthCode.AUTH_USERNAME_NONE);
        }

        if (loginRequest == null || StringUtils.isEmpty(loginRequest.getPassword())) {
            ExceptionCast.cast(AuthCode.AUTH_PASSWORD_NONE);
        }
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        //1 申请令牌

        AuthToken token = authService.apply(username, password, clientId, clientSecret);

        // 用户身份令牌
        String access_token = token.getAccess_token();

        //2 保存到cookie中
        this.saveCookie(access_token);

        return new LoginResult(CommonCode.SUCCESS, access_token);
    }

    //将令牌存入到cookie
    private void saveCookie(String token) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

        CookieUtil.addCookie(response, cookieDomain, "/", "uid", token, cookieMaxAge, false);//false 游览器可以获取token

    }
    //将cookie中存的token删除掉 把有效期改成0

    public void clearCookie(String token) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        CookieUtil.addCookie(response, cookieDomain, "/", "uid", token, 0, false);
    }


    /**
     * 退出登录
     *
     * @return
     */
    @Override
    @PostMapping("/userlogout")
    public ResponseResult logout() {

        //用户的身份令牌
        String tokenFormCookie = getTokenFormCookie();

        //删除redis 中的token
        boolean b = authService.delToken(tokenFormCookie);
        if (!b) {
            ExceptionCast.cast(AuthCode.AUTH_LOGOUT_FAIL);
        }
        //清除 cookie中的token
        this.clearCookie(tokenFormCookie);


        return new ResponseResult(CommonCode.SUCCESS);
    }


    //获取用户jwt令牌
    @Override
    @GetMapping("/userjwt")
    public JwtResult userjwt() {

        //取出cookie中用户身份令牌
        String uid = this.getTokenFormCookie();
        if (uid == null) {
            return new JwtResult(CommonCode.FAIL, null);
        }


        //身份令牌 从redis中获取 jwt令牌
        AuthToken userToken = authService.getUserToken(uid);

        if (userToken != null) {
            String jwt_token = userToken.getJwt_token();

            return new JwtResult(CommonCode.SUCCESS, jwt_token);
        }


        //将jwt令牌返回给用户


        return null;
    }

    //取出cookie中的身份令牌
    private String getTokenFormCookie() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();


        Map<String, String> map = CookieUtil.readCookie(request, "uid");

        if (map != null && map.get("uid") != null) {
            String uid = map.get("uid");
            return uid;
        }

        return null;
    }
}
