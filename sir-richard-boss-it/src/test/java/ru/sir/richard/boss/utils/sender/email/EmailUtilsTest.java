package ru.sir.richard.boss.utils.sender.email;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import ru.sir.richard.boss.model.types.StoreTypes;
import ru.sir.richard.boss.model.utils.sender.email.EmailUtils;

@SpringBootTest
@PropertySource(value="classpath:application-it.properties", encoding="UTF-8")
@Slf4j
public class EmailUtilsTest {

    @Autowired
    private Environment environment;

    @Test
    public void testSendEmail() {
        String messageSubject = "Alex" + ", оставьте отзыв на товары из Вашего заказа на \""+ StoreTypes.PM.getAnnotation() + "\"";
        String emailText = "testing test";
        String messageFooter = "\r\n"
                + "-- \r\n"
                + "С уважением, интернет-компания \"ПРИБОРМАСТЕР\"\r\n"
                + "ИП Федоров Алексей Анатольевич\r\n"
                + "ИНН 771872248140\r\n"
                + "https://" + StoreTypes.PM.getSite() + "\r\n"
                + "+7 (499) 490-59-43\r\n"
                + "+7 (916) 596-90-59\r\n"
                + StoreTypes.PM.getEmail();

        boolean emailResult = EmailUtils.sendEmail(environment,
                StoreTypes.PM,
                "alex4doorow@gmail.com", messageSubject, emailText + messageFooter);
    }
}
