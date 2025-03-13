package com.system.assessment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.system.assessment.config.filter.LoginFilter;
import com.system.assessment.config.handler.JWTAccessDeniedHandler;
import com.system.assessment.config.handler.JWTAuthenticationEntryPoint;
import com.system.assessment.filter.SessionIsValuedTokenFilter;
import com.system.assessment.mapper.UserMapper;
import com.system.assessment.pojo.User;
import com.system.assessment.service.Impl.UserDetailServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JWTAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private JWTAccessDeniedHandler accessDeniedHandler;

    @Autowired
    private SessionIsValuedTokenFilter sessionIsValuedTokenFilter;

    @Autowired
    private UserMapper userMapper;

    private final UserDetailServiceImpl myUserDetailService;

    @Autowired
    public SecurityConfig(UserDetailServiceImpl myUserDetailService) {
        this.myUserDetailService = myUserDetailService;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(myUserDetailService);
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    //自定义 filter 交给工厂管理
    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter();
        loginFilter.setFilterProcessesUrl("/api/login");//指定认证 url
        loginFilter.setUsernameParameter("username");//指定接收json 用户名 key
        loginFilter.setPasswordParameter("password");//指定接收 json 密码 key
        loginFilter.setAuthenticationManager(authenticationManagerBean());
        //认证成功处理
        loginFilter.setAuthenticationSuccessHandler((req, resp, authentication) -> {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("msg", "登录成功");
            result.put("code", 200);
            result.put("login_message", authentication.getPrincipal());
            User user = (User)authentication.getPrincipal();
            result.put("role",user.getRole()); //默认一个用户只有一种角色
            result.put("isFirstLogin", user.getIsFirstLogin());
            userMapper.updateFirstLogin(user.getId());
            resp.setContentType("application/json;charset=UTF-8");
            resp.setStatus(HttpStatus.OK.value());
            String s = new ObjectMapper().writeValueAsString(result);
            resp.getWriter().println(s);
        });
        //认证失败处理
        loginFilter.setAuthenticationFailureHandler((req, resp, ex) -> {
            Map<String, Object> result = new HashMap<String, Object>();
            if(ex instanceof InternalAuthenticationServiceException){
                result.put("msg", "登录失败, " + ex.getMessage());
            }else {
                result.put("msg", "登录失败, 用户名或密码错误!");
            }
            result.put("code", 403);
            resp.setStatus(HttpStatus.OK.value());
            resp.setContentType("application/json;charset=UTF-8");
            String s = new ObjectMapper().writeValueAsString(result);
            resp.getWriter().println(s);
        });
        return loginFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .formLogin()
                .and()
                .exceptionHandling()
                .and()
                .logout()
                .logoutRequestMatcher(new OrRequestMatcher(
                        new AntPathRequestMatcher("/api/logout", HttpMethod.DELETE.name()),
                        new AntPathRequestMatcher("/api/logout", HttpMethod.GET.name())
                ))
                .logoutSuccessHandler((req, resp, auth) -> {
                    Map<String, Object> result = new HashMap<String, Object>();
                    result.put("msg", "注销成功");
                    result.put("code", 200);
//                    result.put("用户信息", auth.getPrincipal());
                    resp.setContentType("application/json;charset=UTF-8");
                    resp.setStatus(HttpStatus.OK.value());
                    String s = new ObjectMapper().writeValueAsString(result);
                    resp.getWriter().println(s);
                })
                .and()
                .authorizeRequests()
                .antMatchers(
                        "/v2/api-docs",
                        "/swagger-resources/configuration/ui",
                        "/swagger-resources",
                        "/test",
                        "/swagger-resources/configuration/security",
                        "/","/index.html","/index",
                        "/static/**",
                        "/favicon.ico",
                        "/css/**", "/js/**", "/img/**",
                        "/pdf/**",
                        "/api/export/evaluatedInfo",
                        "/swagger-ui.html", "/webjars/**").permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .maximumSessions(1)  // 设置最大会话数为1，意味着一个用户只能有一个会话
                .expiredSessionStrategy(event->{
                    HttpServletResponse resp = event.getResponse();
                    Map<String, Object> result = new HashMap<>();
                    result.put("msg", "当前会话已过期，请重新登录!");
                    result.put("code", 500);
                    resp.setContentType("application/json;charset=UTF-8");
                    resp.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    String s = new ObjectMapper().writeValueAsString(result);
                    resp.getWriter().println(s);
                    resp.flushBuffer();
                });

        http.exceptionHandling()
                //配置认证失败处理器
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);


        // at: 用来某个 filter 替换过滤器链中哪个 filter
        // before: 放在过滤器链中哪个 filter 之前
        // after: 放在过滤器链中那个 filter 之后
        http.addFilterBefore(sessionIsValuedTokenFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);

    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher(){
        return new HttpSessionEventPublisher();
    }
}
