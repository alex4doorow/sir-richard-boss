package ru.sir.richard.boss.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import ru.sir.richard.boss.model.data.report.AggregateProductSalesReportBean;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.Pair;

import java.text.ParseException;
import java.util.Date;


@SpringBootTest
@PropertySource(value="classpath:application-it.properties", encoding="UTF-8")
@Slf4j
public class ReportDaoTest {
    @Autowired
    private ReportDao reportDao;

    @Test
    public void testOne() throws ParseException {
        Pair<Date> period = new Pair(DateTimeUtils.defaultFormatStringToDate("05.12.2022"), DateTimeUtils.defaultFormatStringToDate("11.12.2022"));
        AggregateProductSalesReportBean result = reportDao.aggregateProductSales(period);
        log.info(result.toString());
    }

}
