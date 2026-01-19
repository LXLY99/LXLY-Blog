package org.lxly.blog.Test;
import org.junit.jupiter.api.Test; // JUnit 5 的注解
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Date;

@SpringBootTest
public class tool {
    @Autowired
    private JavaMailSender javaMailSender;
    /**
     * 密钥加密
     */
    @Test
    void generatePassword() {
        String raw = "123456";
        String encoded = new BCryptPasswordEncoder().encode(raw);

        System.out.println("--- Spring Boot Test 生成结果 ---");
        System.out.println(encoded);
    }

    /**
     * 邮件发送
     */
    @Test
    void testSendSimpleMail() {
        // 1. 构建一个简单的纯文本邮件
        SimpleMailMessage message = new SimpleMailMessage();

        // 【注意】这里必须改成你在 application.yml 中配置的 spring.mail.username
        String fromEmail = "2945706262@qq.com";
        // 接收者的邮箱
        String toEmail = "lxly@bjedu.tech";

        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Spring Boot 邮件测试");
        message.setText("你好！这是一条测试邮件。\n发送时间: " + new Date());

        try {
            // 2. 执行发送
            javaMailSender.send(message);
            System.out.println("------------------------------------");
            System.out.println("邮件发送成功！请检查收件箱。");
            System.out.println("------------------------------------");
        } catch (Exception e) {
            System.err.println("邮件发送失败！原因如下：");
            e.printStackTrace();
        }
    }
}
