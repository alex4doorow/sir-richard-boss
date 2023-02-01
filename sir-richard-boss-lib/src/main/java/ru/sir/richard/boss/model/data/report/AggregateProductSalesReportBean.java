package ru.sir.richard.boss.model.data.report;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.sir.richard.boss.model.utils.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Data
@ToString
@NoArgsConstructor
public class AggregateProductSalesReportBean {
    private Pair<Date> period = new Pair<>();
    private int otherGsmSocket;
    private int telemetrikaT60;
    private int telemetrikaT80;
    private int elang;
    private int videoEye;
    private int gsmAlarm;
    private int astroEye;
    private int bugHunter;
    private int birdRepeller;
    private int mouseRepeller;
    private int moleRepeller;
    private int snakeRepeller;
    private int ultrasonicDogRepeller;
    private int antidog;
    private int thermacell;
    private int mosquitoKiller;
    private int knifePoint;
    private int bamboo;
    private int autoFeeder;
    private int carku;
    private int tent;
    private int praktic;
    private int others;
    private BigDecimal advertBudget;


    //isocket	sapsan	sanseit	эланг	эланг реле	IQsocket Mobile	Телеметрика Т80	Телеметрика Т60
    // итого	сититек gsm	сититек i8	сититек eye	остальные	итого	gsm сигнализации	usb микроскоп	планетарий	антижучки
    // отпугиватели птиц	отпугиватели грызунов	отпугиватель кротов	отпугиватель змей	отпугиватель собак	антидог
    // ThermaCELL	уничтожители комаров	экотестеры
    // ножеточки	столики для ноутбука
    // автокормушки	пуско-зарядные устройства CARKU	эхолоты	иные

    public int getTotalGsmSocket() {
        return telemetrikaT60 + telemetrikaT80 + elang + otherGsmSocket;
    }

    public int getTotal() {
        return getTotalGsmSocket()
                + videoEye
                + gsmAlarm
                + astroEye
                + bugHunter
                + birdRepeller
                + mouseRepeller
                + moleRepeller
                + snakeRepeller
                + ultrasonicDogRepeller
                + antidog
                + thermacell
                + mosquitoKiller
                + knifePoint
                + bamboo
                + autoFeeder
                + carku
                + tent
                + praktic
                + others;
    }

    public BigDecimal getAdvertBudgetByOne() {
        if (getTotal() == 0) {
            return BigDecimal.ZERO;
        } else {
            return getAdvertBudget().divide(BigDecimal.valueOf(getTotal()), RoundingMode.CEILING);
        }
    }

}
