package com.rice.msg.msgpush.channel;

import com.rice.msg.common.conf.SendMsgConf;
import com.rice.msg.exception.BusinessException;
import com.rice.msg.exception.ErrorCode;
import com.rice.msg.msgpush.ChannelMsgBase;
import com.rice.msg.msgpush.MsgPushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Component
@Slf4j
public class EmailServiceImpl implements MsgPushService {
    @Autowired
    private SendMsgConf sendMsgConf;


    @Override
    public void pushMsg(ChannelMsgBase channelMsgBase) {
        log.info("发送邮件开始");
        String emailAccount = sendMsgConf.getEmailAccount();
        String emailHost = sendMsgConf.getEmailHost();
        String emailPort = sendMsgConf.getEmailPort();
        String emailAuthCode = sendMsgConf.getEmailAuthCode();

        String channelMsgBaseTo = channelMsgBase.getTo();
        String subject = channelMsgBase.getSubject();
        String content = channelMsgBase.getContent();

        Properties properties = new Properties();
        properties.put("mail.smtp.host", emailHost);
        properties.put("mail.smtp.port", emailPort);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailAccount, emailAuthCode);
            }
        });

        try {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(emailAccount));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(channelMsgBaseTo));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(content);

            Transport.send(mimeMessage);
            log.info("邮件发送成功");
        } catch (MessagingException e) {
            log.info("邮件发送失败");
            throw new BusinessException(ErrorCode.PUSH_MSG_ERROR, "邮件发送失败");
        }


    }
}
