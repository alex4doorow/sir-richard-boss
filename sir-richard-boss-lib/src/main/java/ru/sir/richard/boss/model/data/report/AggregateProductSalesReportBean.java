package ru.sir.richard.boss.model.data.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.sir.richard.boss.model.utils.Pair;

import java.util.Date;

@Data
@ToString
@NoArgsConstructor
public class AggregateProductSalesReportBean {
    Pair<Date> period;
    int otherGsmSocket;
    int telemetrikaT60;
    int telemetrikaT80;
    int elang;
    int videoEye;
    int gsmAlarm;
    int astroEye;
    int bugHunter;
    int birdRepeller;
    int mouseRepeller;
    int moleRepeller;
    int snakeRepeller;
    int ultrasonicDogRepeller;
    int antidog;
    int thermacell;
    int mosquitoKiller;
    int knifePoint;
    int bamboo;
    int carku;
    int praktic;
    int others;

    //isocket	sapsan	sanseit	эланг	эланг реле	IQsocket Mobile	Телеметрика Т80	Телеметрика Т60
    // итого	сититек gsm	сититек i8	сититек eye	остальные	итого	gsm сигнализации	usb микроскоп	планетарий	антижучки
    // отпугиватели птиц	отпугиватели грызунов	отпугиватель кротов	отпугиватель змей	отпугиватель собак	антидог
    // ThermaCELL	уничтожители комаров	экотестеры
    // ножеточки	столики для ноутбука
    // автокормушки	пуско-зарядные устройства CARKU	эхолоты	иные


}
