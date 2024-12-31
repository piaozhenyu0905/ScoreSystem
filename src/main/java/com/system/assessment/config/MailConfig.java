package com.system.assessment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@PropertySource("file:${user.dir}/config/email-config.txt")  // 加载外部配置文件
public class MailConfig {

    @Value("${password}")  // 读取外部文件中的 password 键值对
    private String emailPassword;  // 邮箱的授权码

//    @Bean
//    public JavaMailSender javaMailSender() {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//
//        // 手动配置邮件服务器的相关属性
//        mailSender.setHost("smtp.feishu.cn");  // SMTP 服务器地址
//        mailSender.setPort(465);  // 使用 SSL 端口 465
//        mailSender.setUsername("hr_performance@tengwei.com");  // 发件人邮箱地址
//        String password = emailPassword != null ? emailPassword.trim() : null;
//        mailSender.setPassword(password);  // 使用外部文件读取的密码
//        mailSender.setDefaultEncoding("utf-8");  // 默认编码
//
//        // 设置邮件发送的属性
//        Properties mailProperties = new Properties();
//        mailProperties.put("mail.smtp.auth", "true");  // 启用 SMTP 身份验证
//        mailProperties.put("mail.smtp.starttls.enable", "false");  // 禁用 STARTTLS
//        mailProperties.put("mail.smtp.starttls.required", "false");  // 禁用 STARTTLS
//        mailProperties.put("mail.smtp.ssl.enable", "true");  // 启用 SSL
//        mailProperties.put("mail.smtp.ssl.trust", "smtp.feishu.cn");  // 信任 Feishu 的 SMTP 服务器
//        mailProperties.put("mail.smtp.connectiontimeout", "10000");  // 设置连接超时时间
//        mailProperties.put("mail.smtp.timeout", "10000");  // 设置读取超时时间
//        mailProperties.put("mail.smtp.writetimeout", "10000");  // 设置写入超时时间
//
//        mailSender.setJavaMailProperties(mailProperties);
//
//        return mailSender;
//    }

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // 手动配置邮件服务器的相关属性
        mailSender.setHost("smtp.qq.com");  // SMTP 服务器地址（QQ邮箱的SMTP地址）
        mailSender.setPort(587);  // SMTP 端口号（QQ邮箱的 TLS 端口为 587）
        mailSender.setUsername("2339134840@qq.com");  // 发件人邮箱地址（QQ邮箱）

        // 从外部读取的授权码，确保没有空格
//        String password = emailPassword != null ? emailPassword.trim() : null;
        String password = "jsrlalxhrflcdjga";
        mailSender.setPassword(password);  // 使用外部文件读取的授权码（不是QQ的登录密码）

        mailSender.setDefaultEncoding("utf-8");  // 默认编码

        // 设置邮件发送的属性
        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.auth", "true");  // 启用 SMTP 身份验证
        mailProperties.put("mail.smtp.starttls.enable", "true");  // 启用 TLS 加密
        mailProperties.put("mail.smtp.starttls.required", "true");  // 必须启用 STARTTLS
        mailProperties.put("mail.smtp.ssl.enable", "false");  // 禁用 SSL，加密通过 STARTTLS 进行
        mailProperties.put("mail.smtp.connectiontimeout", "10000");  // 设置连接超时时间
        mailProperties.put("mail.smtp.timeout", "10000");  // 设置读取超时时间
        mailProperties.put("mail.smtp.writetimeout", "10000");  // 设置写入超时时间

        // 将配置的属性应用到 mailSender
        mailSender.setJavaMailProperties(mailProperties);

        return mailSender;
    }
}