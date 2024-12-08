package com.system.assessment.service;

import javax.mail.MessagingException;
import java.io.File;
import java.util.List;

public interface EmailService {
    public void sendMessageCarryFile(String to, String subject, String content, File file);

    public void sendMessageHTML(String to, String subject, String content);

    public void sendMessage(String to, String subject, String content);

}
