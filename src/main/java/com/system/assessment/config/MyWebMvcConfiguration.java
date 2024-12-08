package com.system.assessment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@Configuration
public class MyWebMvcConfiguration implements WebMvcConfigurer {

    @Override
        //处理跨域请求的映射
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**").allowedMethods("GET","POST","PUT","DELETE");
        }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/static/").setCacheControl(
                CacheControl.maxAge(0, TimeUnit.SECONDS)
                        .cachePublic());
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/").setCacheControl(
                CacheControl.maxAge(0, TimeUnit.SECONDS)
                        .cachePublic());
        registry.addResourceHandler("/images/**").addResourceLocations("file:./file/certificate/");

        registry.addResourceHandler("/files/**").addResourceLocations("file:./file/post/");
        registry.addResourceHandler("/materials/**").addResourceLocations("file:./file/material/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
