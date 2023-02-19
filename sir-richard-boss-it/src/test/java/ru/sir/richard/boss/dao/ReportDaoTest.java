package ru.sir.richard.boss.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import ru.sir.richard.boss.model.data.report.AggregateProductSalesReportBean;
import ru.sir.richard.boss.utils.DateTimeUtils;
import ru.sir.richard.boss.utils.Pair;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;


@SpringBootTest
@PropertySource(value="classpath:application-it.properties", encoding="UTF-8")
@Slf4j
public class ReportDaoTest {
    @Autowired
    private ReportDao reportDao;

    @Test
    public void testOne() throws ParseException, IOException {
        Pair<Date> period = new Pair(DateTimeUtils.defaultFormatStringToDate("05.12.2022"), DateTimeUtils.defaultFormatStringToDate("11.12.2022"));
        AggregateProductSalesReportBean result = reportDao.aggregateProductSales(period);
        log.info(result.toString());

        List<AggregateProductSalesReportBean> beans = Collections.singletonList(result);
        FileOutputStream outputStream = new FileOutputStream("d:\\src\\sir-richard-boss\\--1-save\\aggregate-sales.xls");
        reportDao.aggregateProductSalesWriteIntoExcel(beans, outputStream);
    }

    @Test
    public void testTwo() throws ParseException, IOException {

        final DayOfWeek firstDayOfWeek = DayOfWeek.MONDAY;
        final DayOfWeek lastDayOfWeek = DayOfWeek.SUNDAY;
/*
        Pair<Date> period = new Pair<>(DateTimeUtils.defaultFormatStringToDate("01.01.2022"),
                DateTimeUtils.defaultFormatStringToDate("30.01.2023"));
*/

        Pair<Date> period = new Pair<>(DateTimeUtils.defaultFormatStringToDate("01.01.2016"),
                DateTimeUtils.defaultFormatStringToDate("30.01.2023"));

        Date dateI = period.getStart();
        List<Pair<Date>> weeks = new ArrayList<>();
        while (dateI.compareTo(period.getEnd()) < 0) {
            LocalDateTime swd = dateI.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                    .with(TemporalAdjusters.previousOrSame(firstDayOfWeek));
            LocalDateTime ewd = dateI.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                    .with(TemporalAdjusters.nextOrSame(lastDayOfWeek));
            log.info("week: {}, {}", swd, ewd);

            weeks.add(new Pair<>(java.util.Date.from(swd.atZone(ZoneId.systemDefault()).toInstant()),
                    java.util.Date.from(ewd.atZone(ZoneId.systemDefault()).toInstant())));

            LocalDateTime nextDay = ewd.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
            dateI = java.util.Date.from(nextDay.atZone(ZoneId.systemDefault()).toInstant());
        }

        List<AggregateProductSalesReportBean> beans = new ArrayList<>();
        weeks.forEach(week -> {
            AggregateProductSalesReportBean bean = reportDao.aggregateProductSales(week);
            beans.add(bean);
        });
        FileOutputStream outputStream = new FileOutputStream("d:\\src\\sir-richard-boss\\--1-save\\aggregate-sales.xls");
        reportDao.aggregateProductSalesWriteIntoExcel(beans, outputStream);
    }

}
