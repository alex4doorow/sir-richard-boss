# SIR-RICHARD-BOSS 
Проект изначально разрабатывался spring 2.0.1. В июле 2022 года, перенесен со spring 2.0.1 на 2.7.4
Выполнена адаптация проекта под новый SPRING.

Реальные application.properties в проект не заложены из-за соображений безопасности. В них присутствуют параметры интеграций с CDEK, OZON, Яндекс Маркет и т.п.
в git залиты пустые application-etalon.properties, в которых данные вычищены.
Нужно скопипастить application-etalon.properties в application.properties и заполнить данными для настроек.

Настройки для подключения к MySQL сделаны в двух вариантах:

<ol>
<li>
<b>Prodaction</b>.&nbsp;Подключение идет через настроеннный в tomcat jndi. Путь указан в application.properties:
   jdbc.jndi = java:comp/env/jdbc/sirRichardBoss

В моем томкатовском server.xml есть два ресурса priborMasterProduction, priborMasterTest:
<!--
<GlobalNamingResources>
    <Resource name="priborMasterProduction" auth="Container"
              type="javax.sql.DataSource"
              username="********"
              password="********"
              driverClassName="com.mysql.cj.jdbc.Driver"
              url="jdbc:mysql://127.0.0.1:3306/********?characterEncoding=UTF-8&amp;autoReconnect=true&amp;useSSL=false"
              maxTotal="25"
              maxIdle="10"
              defaultTransactionIsolation="READ_COMMITTED"
	          validationQuery="select 1"/>

    <Resource name="priborMasterTest" auth="Container"
              type="javax.sql.DataSource"
              username="********"
              password="********"
              driverClassName="com.mysql.cj.jdbc.Driver"
              url="jdbc:mysql://127.0.0.1:3306/********?characterEncoding=UTF-8&amp;autoReconnect=true&amp;useSSL=false"
              maxTotal="25"
              maxIdle="10"
              defaultTransactionIsolation="READ_COMMITTED"
	          validationQuery="select 1"/>
  </GlobalNamingResources>
-->
Они переключаются в context.xml в зависимости от варианта старта - продакшен или тестовый стенд.

в context.xml ресурс мапится в sirRichardBoss:
<!--
<ResourceLink name="jdbc/sirRichardBoss"
global="priborMasterProduction"
type="javax.sql.DataSource"/>
-->
</li>
<li>
<b>Cтенды</b>&nbsp;Unit-tests и интеграционные тесты.
Поскольку тесты запускаются в embeded tomcat, где никаких jndi нет, то dataSourse инициализируется через url, user, password
в application-test.properties, application-production.properties инициализация тестов sir-richard-boss-web
в application-it.properties инициализация интеграционных тестов sir-richard-boss-it

application-production.properties боевая
application-test.properties тестовая
<!--
#application
application.production = false

#jdbc
jdbc.ds.pm.url = jdbc:mysql://127.0.0.1:3306/************?useUnicode=true&characterEncoding=UTF-8&useSSL=false
jdbc.ds.pm.user = ************
jdbc.ds.pm.password = ************
-->
</li>
</ol>

В application-production.properties находится важный парамер: application.production
Если он в true, то запускаются шедулеры и обращаются через интеграцию во внешние системы: передают данные, обновляют данные.
Для тестовой среды этот параметр сброшен

В каталоге /db находятся файлы построения бд. структура данных разработана для MySQL
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Web приложение "Бэк офис интернет-магазина"

Бэкофис интернет магазина «приборМАСТЕР» является приложением полного цикла.  Приложение обеспечивает ввод заявок, контроль и расход товарных остатков склада. 
Формирование первичных документов. Интеграции с фронтом –сайтом интернет-магазина pribormaster.ru (на нем пользователи просматривают карточки товара, перемещают выбранные товары в корзину и делают покупки), с маркетплейсами, где также размещены товары нашего магазина, интеграция с транспортными компаниями.

Приложение позволяет работать пользователям с несколькими ролями.
