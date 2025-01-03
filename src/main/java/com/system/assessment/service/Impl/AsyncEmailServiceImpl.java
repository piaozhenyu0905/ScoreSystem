package com.system.assessment.service.Impl;

import com.system.assessment.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Slf4j
public class AsyncEmailServiceImpl implements EmailService {
    private String from = "2339134840@qq.com"; //正式提交时需要修改

    @Autowired(required = false)
    private JavaMailSender mailSender;

    // 邮件发送队列
    private final BlockingQueue<Email> emailQueue = new LinkedBlockingQueue<>();

    // 最大重试次数
    private final int MAX_RETRY_COUNT = 3;

    // 初始发送延迟，单位毫秒
    private final long INITIAL_DELAY = 500; // 0.5秒


    @PostConstruct
    public void init() {
        emailProcessorThread = new Thread(new EmailProcessor());
        emailProcessorThread.setDaemon(true);
        emailProcessorThread.start();
    }

    // 邮件处理线程
    private Thread emailProcessorThread;

    /**
     * 将邮件加入发送队列
     *
     * @param to      接收方
     * @param subject 邮件主题
     * @param content 邮件内容（发送内容）
     */
    @Override
    public void sendMessage(String to, String subject, String content) {
        emailQueue.offer(new Email(to, subject, content, null, EmailType.PLAIN, 0));

    }

    /**
     * 将 HTML 邮件加入发送队列
     *
     * @param to      接收方
     * @param subject 邮件主题
     * @param content 邮件内容（发送内容）
     */
    @Override
    public void sendMessageHTML(String to, String subject, String content) {
        emailQueue.offer(new Email(to, subject, content, null, EmailType.HTML, 0));

    }

    /**
     * 将带附件的邮件加入发送队列
     *
     * @param to      接收方
     * @param subject 邮件主题
     * @param content 邮件内容（发送内容）
     * @param files   文件数组 // 可发送多个附件
     */

    public void sendMessageCarryFiles(String to, String subject, String content, File[] files) {
        emailQueue.offer(new Email(to, subject, content, files, EmailType.MULTI_ATTACHMENT, 0));

    }

    /**
     * 将带单个附件的邮件加入发送队列
     *
     * @param to      接收方
     * @param subject 邮件主题
     * @param content 邮件内容（发送内容）
     * @param file    单个文件
     */
    @Override
    public void sendMessageCarryFile(String to, String subject, String content, File file) {
        emailQueue.offer(new Email(to, subject, content, new File[]{file}, EmailType.SINGLE_ATTACHMENT, 0));

    }


    /**
     * 邮件处理器，单线程处理邮件队列
     */
    private class EmailProcessor implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    Email email = emailQueue.take(); // 获取队列中的邮件
                    Integer success = processEmail(email); // 发送邮件
                    if (success.equals(1)) {
                        log.info("邮件发送成功: to={}, subject={}", email.getTo(), email.getSubject());
                        Thread.sleep(INITIAL_DELAY); // 成功发送后等待0.5秒
                    }else if(success.equals(2)){
                        //邮件地址不存在，不需要重新发送!
                    }
                    else {
                        int retryCount = email.getRetryCount();
                        if (retryCount < MAX_RETRY_COUNT) {
                            emailQueue.offer(new Email(
                                    email.getTo(),
                                    email.getSubject(),
                                    email.getContent(),
                                    email.getFiles(),
                                    email.getEmailType(),
                                    retryCount + 1
                            ));
                            log.info("邮件发送失败，已重新加入队列: to={}, subject={}, retryCount={}",
                                    email.getTo(), email.getSubject(), email.getRetryCount() + 1);

                            Thread.sleep(INITIAL_DELAY * 2 * (retryCount + 1)); // 失败后稍微增加等待时间
                        } else {
                            log.error("邮件发送失败，达到最大重试次数，放弃发送: to={}, subject={}",
                                    email.getTo(), email.getSubject());
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("邮件处理线程被中断", e);
                    break;
                } catch (Exception e) {
                    log.error("邮件发送过程中发生异常", e);
                }
            }
        }

        private Integer processEmail(Email email) {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.setFrom(from);
                helper.setTo(email.getTo());
                helper.setSubject(email.getSubject());

                if (email.getEmailType() == EmailType.HTML) {
                    helper.setText(email.getContent(), true);
                } else {
                    helper.setText(email.getContent(), false);
                }

                if (email.getFiles() != null && email.getFiles().length > 0) {
                    for (File file : email.getFiles()) {
                        helper.addAttachment(file.getName(), file);
                    }
                }

                mailSender.send(mimeMessage);
                return 1;
            } catch (SendFailedException e) {
                log.error("邮件地址不存在或不可送达: {}", e.getMessage());
                return 2;
            } catch (MessagingException e) {
                log.error("邮件发送失败，其他原因: {}", e.getMessage());
            } catch (Exception e) {
                log.error("发送邮件时发生未知错误: {}", e.getMessage());
            }
            return 0;
        }
    }



    private static class Email {
        private final String to;
        private final String subject;
        private final String content;
        private final File[] files;
        private final EmailType emailType;
        private final int retryCount;

        public Email(String to, String subject, String content, File[] files, EmailType emailType, int retryCount) {
            this.to = to;
            this.subject = subject;
            this.content = content;
            this.files = files;
            this.emailType = emailType;
            this.retryCount = retryCount;
        }

        public String getTo() {
            return to;
        }

        public String getSubject() {
            return subject;
        }

        public String getContent() {
            return content;
        }

        public File[] getFiles() {
            return files;
        }

        public EmailType getEmailType() {
            return emailType;
        }

        public int getRetryCount() {
            return retryCount;
        }
    }

    /**
     * 邮件类型枚举
     */
    private enum EmailType {
        PLAIN, // 纯文本
        HTML, // HTML 格式
        SINGLE_ATTACHMENT, // 带单个附件
        MULTI_ATTACHMENT // 带多个附件
    }

    // Getter 和 Setter
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
