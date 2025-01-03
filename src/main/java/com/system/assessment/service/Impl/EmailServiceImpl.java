package com.system.assessment.service.Impl;

import com.system.assessment.service.EmailService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;



@Slf4j
public class EmailServiceImpl implements EmailService {

    private String from = "2339134840@qq.com"; //正式提交时需要修改

    @Autowired(required = false)
    private JavaMailSender mailSender;


    /**
     * 发送纯文本邮件信息
     *
     * @param to      接收方
     * @param subject 邮件主题
     * @param content 邮件内容（发送内容）
     */
    @Async("emailExecutor")
    public void sendMessage(String to, String subject, String content) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // true 表示内容为 HTML
            mailSender.send(mimeMessage);
            log.info("标题为"+ subject +"的邮件已成功发送到" + to);
        } catch (SendFailedException e) {
            log.error("邮件地址不存在或不可送达: " + e.getMessage());
        } catch (MessagingException e) {
            log.error("邮件发送失败，其他原因" + e.getMessage());
        }
    }


    /**
     * 发送html的邮件信息
     *
     * @param to      接收方
     * @param subject 邮件主题
     * @param content 邮件内容（发送内容）
     */
    @Async("emailExecutor")
    public void sendMessageHTML(String to, String subject, String content) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
            // 设置发送发
            helper.setFrom(from);
            // 设置接收方
            helper.setTo(to);
            // 设置邮件主题
            helper.setSubject(subject);
            // 设置邮件内容
            helper.setText(content,true);
            log.info("标题为"+ subject +"的邮件已成功发送到" + to);
        }catch (SendFailedException e) {
            log.error("邮件地址不存在或不可送达: " + e.getMessage());
        } catch (MessagingException e) {
            log.error("邮件发送失败，其他原因" + e.getMessage());
        }
        // 发送邮件
        mailSender.send(mimeMessage);
    }


    /**
     * 发送带附件的邮件信息
     *
     * @param to      接收方
     * @param subject 邮件主题
     * @param content 邮件内容（发送内容）
     * @param files 文件数组 // 可发送多个附件
     */
    @Async("emailExecutor")
    public void sendMessageCarryFiles(String to, String subject, String content, File[] files) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
            // 设置发送发
            helper.setFrom(from);
            // 设置接收方
            helper.setTo(to);
            // 设置邮件主题
            helper.setSubject(subject);
            // 设置邮件内容
            helper.setText(content);
            // 添加附件（多个）
            if (files != null && files.length > 0) {
                for (File file : files) {
                    helper.addAttachment(file.getName(), file);
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        // 发送邮件
        mailSender.send(mimeMessage);
    }
    /**
     * 发送带附件的邮件信息
     *
     * @param to      接收方
     * @param subject 邮件主题
     * @param content 邮件内容（发送内容）
     * @param file 单个文件
     */
    @Async("emailExecutor")
    public void sendMessageCarryFile(String to, String subject, String content, File file) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
            // 设置发送发
            helper.setFrom(from);
            // 设置接收方
            helper.setTo(to);
            // 设置邮件主题
            helper.setSubject(subject);
            // 设置邮件内容
            helper.setText(content);
            // 单个附件
            helper.addAttachment(file.getName(), file);
            log.info("标题为"+ subject +"的邮件已成功发送到" + to);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        // 发送邮件
        mailSender.send(mimeMessage);
    }



    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

}
