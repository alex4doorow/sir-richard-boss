-- p326995_test
/*-------------------------------------------------------------*/
/*                         СИСТЕМНЫЕ СПРАВОЧНИКИ               */
/*-------------------------------------------------------------*/

/* users web security */
CREATE TABLE sr_sys_user (
    id INT NOT NULL AUTO_INCREMENT, /* идентификатор */
	user_name VARCHAR(50) NOT NULL,
	password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NULL,
	enabled TINYINT NOT NULL DEFAULT 1,
    last_login DATETIME NULL,
	PRIMARY KEY (id)
);

CREATE UNIQUE INDEX sr_sys_user_index on sr_sys_user (user_name);

CREATE TABLE sr_sys_role (
	id INT NOT NULL AUTO_INCREMENT, /* идентификатор */
	name VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE sr_sys_user_roles (
	id INT NOT NULL AUTO_INCREMENT, /* идентификатор */
	user_id INT NOT NULL,
	role_id INT NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO sr_sys_user(id, user_name, password, email, last_login, enabled) values (1, 'al', '$2a$10$fCGXaogbOuxCAWgKc7WDCeGFgrebZE0eQyfv5b8PuSvwvOrI94EVi', "alex@doorow@gmail.com", now(), 1);

INSERT INTO sr_sys_role(id, name) VALUES (1, 'ROLE_USER'); 
INSERT INTO sr_sys_role(id, name) VALUES (2, 'ROLE_ADMIN'); 

INSERT INTO sr_sys_user_roles(user_id, role_id) values (1, 1);
INSERT INTO sr_sys_user_roles(user_id, role_id) values (1, 2);

/* конфигуратор */
CREATE TABLE sr_sys_config (
	code VARCHAR(255) NOT NULL, /* параметр */
	value VARCHAR(255) NOT NULL /* значение */,
	annotation VARCHAR(255) NOT NULL /* описание */,
	PRIMARY KEY(code)
);

/* внешние показатели для расчетов */
CREATE TABLE sr_sys_total_amount (
	id SMALLINT NOT NULL, /* идентификатор */
	annotation VARCHAR(255) NOT NULL /* описание */,
	PRIMARY KEY(id)
);

/* системный журнал */
CREATE TABLE sr_sys_message_log (
	id INT NOT NULL AUTO_INCREMENT, /* идентификатор */
    date_added DATETIME DEFAULT NOW() NOT NULL, /* дата создания */	
    module  VARCHAR(30) NOT NULL /* модуль (car - автомобильная сигнализация) */,
    code  VARCHAR(30) NULL /* код сообщения системы */,
	message VARCHAR(255) NULL /* описание */,
	PRIMARY KEY(id)
);

CREATE INDEX sr_sys_message_log_index ON sr_sys_message_log (module, date_added); 

/*-------------------------------------------------------------*/
/*                 ПОЛЬЗОВАТЕЛЬСКИЕ СПРАВОЧНИКИ                */
/*-------------------------------------------------------------*/

/* статусы покупателей */
CREATE TABLE sr_wiki_customer_type (
	id TINYINT NOT NULL, /* идентификатор */
	annotation VARCHAR(255) NOT NULL /* описание */,
	PRIMARY KEY(id)
);

/* статусы покупателей */
CREATE TABLE sr_wiki_customer_status (
	id TINYINT NOT NULL, /* идентификатор */
	annotation VARCHAR(255) NOT NULL /* описание */,
	PRIMARY KEY(id)
);

/* типы адресов */
CREATE TABLE sr_wiki_address_type (
	id TINYINT NOT NULL, /* идентификатор */
	annotation VARCHAR(255) NOT NULL /* описание */,
	PRIMARY KEY(id)
);

-- Город	Код	Адрес	Телефоны	График работы	Индекс

/* ПВЗ СДЭК */
CREATE TABLE sr_wiki_cdek_pvz (	
    code VARCHAR(30) NOT NULL /* код ПВЗ */,
    city VARCHAR(255) NOT NULL /* город */,
    address VARCHAR(255) NOT NULL /* адрес */,
    phones VARCHAR(255) NULL /* телефоны */,
    schedule_work VARCHAR(255) NULL /* расписание работы */,
    post_code VARCHAR(10) NULL /* индекс */,
	PRIMARY KEY(code)
);

-- CREATE INDEX sr_wiki_cdek_pvz_code_index ON sr_wiki_cdek_pvz (code); 
CREATE INDEX sr_wiki_cdek_pvz_city_index ON sr_wiki_cdek_pvz (city); 

/* типы контактов */
CREATE TABLE sr_wiki_contact_type (
	id TINYINT NOT NULL, /* идентификатор */
	annotation VARCHAR(255) NOT NULL /* описание */,
	PRIMARY KEY(id)
);

/* типы источников заказов */
CREATE TABLE sr_wiki_order_source (
	id TINYINT NOT NULL, /* идентификатор */
	annotation VARCHAR(255) NOT NULL /* описание */,
	PRIMARY KEY(id)
);

/* типы привлечения клиентов */
CREATE TABLE sr_wiki_order_advert (
	id TINYINT NOT NULL, /* идентификатор */
	annotation VARCHAR(255) NOT NULL /* описание */,
	PRIMARY KEY(id)
);

/* типы заказов */
CREATE TABLE sr_wiki_order_type (
	id TINYINT NOT NULL, /* идентификатор */
	annotation VARCHAR(255) NOT NULL /* описание */,
	PRIMARY KEY(id)
);

/* типы оплаты заказов */
CREATE TABLE sr_wiki_order_payment (
	id TINYINT NOT NULL, /* идентификатор */
	annotation VARCHAR(255) NOT NULL /* описание */,
	PRIMARY KEY(id)
);

/* типы оплаты доставки заказов */
CREATE TABLE sr_wiki_order_payment_delivery (
	id TINYINT NOT NULL, /* идентификатор */
	annotation VARCHAR(255) NOT NULL /* описание */,
	PRIMARY KEY(id)
);

/* статусы заказов */
CREATE TABLE sr_wiki_order_status (
	id TINYINT NOT NULL, /* идентификатор */
	annotation VARCHAR(255) NOT NULL /* описание */,
	PRIMARY KEY(id)
);

/* типы коментариев */
CREATE TABLE sr_wiki_order_comment_type (
	id TINYINT NOT NULL, /* идентификатор */
	annotation VARCHAR(255) NOT NULL /* описание */,
	PRIMARY KEY(id)
);

/* ниши товаров */
CREATE TABLE sr_wiki_category_product (
	id SMALLINT NOT NULL, /* идентификатор */
	type_group VARCHAR(255) NOT NULL, /* группа ниш */
	annotation VARCHAR(255) NOT NULL /* описание */,
	PRIMARY KEY(id)
);
CREATE INDEX sr_wiki_category_product_annotation_index ON sr_wiki_category_product (annotation);

/* список внешних систем */
CREATE TABLE sr_wiki_crm (
	id SMALLINT NOT NULL, /* идентификатор */
	annotation VARCHAR(255) NOT NULL /* описание */,
	PRIMARY KEY(id)
);

/* список статусов обработки записей из внешних систем */
CREATE TABLE sr_wiki_crm_status (
	id SMALLINT NOT NULL, /* идентификатор */
	annotation VARCHAR(255) NOT NULL /* описание */,
	PRIMARY KEY(id)
);

/*-------------------------------------------------------------*/
/*                      ТАБЛИЦЫ OPENCART                       */
/*-------------------------------------------------------------*/

ALTER TABLE oc_manufacturer
	ADD country_brand VARCHAR(128) NULL; /* страна брэнда */
ALTER TABLE oc_manufacturer
	ADD country_origin VARCHAR(128) NULL; /* страна производства */
                
SET SQL_SAFE_UPDATES = 0;
update oc_manufacturer set country_brand = 'КНР';
update oc_manufacturer set country_brand = 'Россия' where manufacturer_id in (11,12,17,19,5,8,9,27,29,34,43,50,68,69,70,79,80,74,88,89,137);
update oc_manufacturer set country_brand = 'Украина' where manufacturer_id in (78);
update oc_manufacturer set country_brand = 'Нидерланды' where manufacturer_id in (51);
update oc_manufacturer set country_brand = 'Бельгия' where manufacturer_id in (10);
update oc_manufacturer set country_brand = 'Гонконг' where manufacturer_id in (31);
update oc_manufacturer set country_brand = 'США' where manufacturer_id in (54,56,94);
update oc_manufacturer set country_brand = 'Турция' where manufacturer_id in (83);
update oc_manufacturer set country_brand = 'Великобритания' where manufacturer_id in (84);

update oc_manufacturer set country_origin = 'КНР';
update oc_manufacturer set country_origin = 'Россия' where manufacturer_id in (11,12,17,9,27,29,34,43,50,69,79,74,88,89,137);
update oc_manufacturer set country_origin = 'Украина' where manufacturer_id in (78);
update oc_manufacturer set country_origin = 'Нидерланды' where manufacturer_id in (51);
update oc_manufacturer set country_origin = 'Гонконг' where manufacturer_id in (31);
update oc_manufacturer set country_origin = 'Тайвань' where manufacturer_id in (5);
update oc_manufacturer set country_origin = 'США' where manufacturer_id in (54,56);
update oc_manufacturer set country_origin = 'Турция' where manufacturer_id in (83);
          
/**
 * JAN - вариант доставки 
 * 		0, 102 - текущий,
 * 		101 - полный
 * 		103 - бесплатная доставка
 * ISBN 
 * 		101 - домолнительный продукт
 * 		- обычный 
 * MPN
 * 		104 - цена по запросу
 * 
 */

/* новые поля для таблицы oc_product */
ALTER TABLE oc_product
	ADD category_group_id SMALLINT NULL DEFAULT 0; /* ниши товаров */
ALTER TABLE oc_product
	ADD composite SMALLINT NULL DEFAULT 0; /* комплект (0- нет, 1- да) */
ALTER TABLE oc_product
	ADD delivery_name varchar(255) NULL; /* название товара для экспорта в СДЭК, OZON Rocket */
    
    
ALTER TABLE oc_product
    ADD CONSTRAINT fk_oc_product_group_id FOREIGN KEY (category_group_id)   
    REFERENCES sr_wiki_order_category_product (id);
    
CREATE TABLE sr_product_composite (
	id INT(11) NOT NULL AUTO_INCREMENT,
    master_product_id INT(11) NOT NULL, 
	slave_product_id VARCHAR(30) NOT NULL, 
    slave_quantity INT(11) NOT NULL, /* количество товара в комплекте */
    slave_type SMALLINT NOT NULL, /* тип связки */
    CONSTRAINT sr_product_composite_uq UNIQUE (master_product_id, slave_product_id, slave_type),
	PRIMARY KEY(id)
);   

SET SQL_SAFE_UPDATES = 0;

update oc_product set composite = 1 where product_id = 821;
update oc_product set composite = 1 where product_id = 834;
update oc_product set composite = 1 where product_id = 620;
update oc_product set composite = 1 where product_id = 840;
update oc_product set composite = 1 where product_id = 841;

update oc_product set composite = 1 where product_id = 629;
update oc_product set composite = 1 where product_id = 761;
update oc_product set composite = 1 where product_id = 762;

update oc_product set composite = 1 where product_id = 1029;
update oc_product set composite = 1 where product_id = 1028;
update oc_product set composite = 1 where product_id = 1030;

update oc_product set composite = 1 where product_id = 699;
update oc_product set composite = 1 where product_id = 456;

update oc_product set composite = 1 where product_id = 1288;
  
delete from sr_product_composite;

insert into sr_product_composite
(id, master_product_id, slave_product_id, slave_quantity, slave_type)
VALUES
(1, 821, 835, 1, 1),
(2, 821, 843, 1, 1),

(3, 834, 835, 1, 1),
(4, 834, 842, 1, 1),

(5, 620, 839, 1, 1),
(6, 620, 621, 10, 1),

(7, 840, 839, 1, 1),
(8, 840, 621, 2, 1),

(9, 841, 839, 1, 1),
(10, 841, 621, 4, 1),

(11, 629, 632, 1, 1),
(12, 629, 614, 1, 1),

(13, 761, 632, 1, 1),
(14, 761, 614, 2, 1),

(15, 762, 632, 1, 1),
(16, 762, 614, 4, 1),

(17, 1029, 985, 1, 1),
(18, 1029, 986, 1, 1),

(19, 1028, 985, 1, 1),
(20, 1028, 986, 2, 1),

(21, 1030, 985, 1, 1),
(22, 1030, 986, 4, 1),

(23, 699, 700, 1, 1),
(24, 699, 769, 1, 1),

(25, 456, 700, 1, 1),
(26, 456, 769, 1, 1),
(27, 456, 770, 1, 1),

(31, 1288, 687, 2, 1),
(32, 1288, 1287, 2, 1);
       
/* ключи продуктов. с одним продуктом связано один или более ключей */   
/* 
CREATE TABLE sr_product_key (
	id INT(11) NOT NULL AUTO_INCREMENT,
    product_id INT(11) NOT NULL, 
	product_key VARCHAR(30) NOT NULL, 
    CONSTRAINT sr_product_key_uq UNIQUE (product_id, product_key),
	PRIMARY KEY(id)
);    
*/    
    
/*-------------------------------------------------------------*/
/*                ПОЛЬЗОВАТЕЛЬСКИЕ СПРАВОЧНИКИ                 */
/*-------------------------------------------------------------*/   
   
/* способы доставки */
CREATE TABLE sr_wiki_order_delivery (
	id SMALLINT NOT NULL, /* идентификатор */
	category VARCHAR(255) NOT NULL, /* категория */
	annotation VARCHAR(255) NOT NULL /* описание */,
	PRIMARY KEY(id)
);

/* тарифы доставки */
CREATE TABLE sr_wiki_order_delivery_price (
	id SMALLINT NOT NULL, /* идентификатор */
	delivery_id SMALLINT NOT NULL, /* идентификатор sr_wiki_order_delivery.id */
	annotation VARCHAR(255) NULL /* описание */,
	price DECIMAL(15,4) NOT NULL, /* значение тарифа */
	PRIMARY KEY(id)
);

ALTER TABLE sr_wiki_order_delivery_price
    ADD CONSTRAINT fk_sr_wiki_order_delivery_price_delivery_id FOREIGN KEY (delivery_id)    
    REFERENCES sr_wiki_order_delivery (id);
    
CREATE TABLE sr_wiki_stock (
id TINYINT NOT NULL AUTO_INCREMENT, /* идентификатор */ 
annotation VARCHAR(255) NOT NULL /* описание */,
	PRIMARY KEY(id)
);

CREATE TABLE sr_wiki_supplier (
id TINYINT NOT NULL AUTO_INCREMENT, /* идентификатор */ 
annotation VARCHAR(255) NOT NULL /* описание */,
	PRIMARY KEY(id)
);

CREATE TABLE sr_wiki_stock_supplier (
id TINYINT NOT NULL AUTO_INCREMENT, /* идентификатор */ 
stock_id TINYINT NOT NULL,
supplier_id TINYINT NOT NULL,
	CONSTRAINT sr_wiki_stock_supplier_uq UNIQUE (stock_id, supplier_id),
	PRIMARY KEY(id)
);    

ALTER TABLE sr_wiki_stock_supplier
    ADD CONSTRAINT fk_sr_wiki_stock_supplier_stock_id FOREIGN KEY (stock_id)    
    REFERENCES sr_wiki_stock (id);  
    
ALTER TABLE sr_wiki_stock_supplier
    ADD CONSTRAINT fk_sr_wiki_stock_supplier_supplier_id FOREIGN KEY (supplier_id)    
    REFERENCES sr_wiki_supplier (id);      
        
/*-------------------------------------------------------------*/
/*                          ДАННЫЕ                             */
/*-------------------------------------------------------------*/

/* человек */
CREATE TABLE sr_person (
	id INT(11) NOT NULL AUTO_INCREMENT, /* идентификатор */    
    first_name VARCHAR(255) NOT NULL, /* имя */
    last_name VARCHAR(255) NULL, /* фамилия */
    middle_name VARCHAR(255) NULL, /* отчество */
    country_iso_code_2 CHAR(2) DEFAULT 'RU' NOT NULL,
	phone_number VARCHAR(30) NULL, /* (999) 111-11-11 */
    email VARCHAR(255) NULL, /* a@a.ru */	
    CONSTRAINT sr_person_uq UNIQUE (country_iso_code_2, phone_number, email),
	PRIMARY KEY(id)
);

ALTER TABLE sr_person
    ADD CONSTRAINT fk_sr_person_iso_code_2 FOREIGN KEY (country_iso_code_2)    
    REFERENCES oc_country (iso_code_2);  

/* адрес */
CREATE TABLE sr_address (
	id INT(11) NOT NULL AUTO_INCREMENT, /* идентификатор */
    type TINYINT DEFAULT 1 NOT NULL, /* тип */
    country_iso_code_2 CHAR(2) DEFAULT 'RU' NOT NULL,
    post_code VARCHAR(6) NULL, /* индекс */
    city_id INT(11) NULL, /* идентификатор города сдэк */
	city VARCHAR(255) NULL, /* город (контекст) */
	pvz VARCHAR(255) NULL, /* пункт выдачи */
    pvz_id VARCHAR(20) NULL, /* идентификатор пункта выдачи / deliveryVariantId */
    street VARCHAR(255) NULL, /* улица */
    house VARCHAR(255) NULL, /* дом */
    flat VARCHAR(255) NULL, /* квартира/офис */
	address VARCHAR(255) NULL, /* адрес */
	subway_station VARCHAR(255) NULL, /* станция метро */
	annotation VARCHAR(255) NULL /* описание */,    
	PRIMARY KEY(id)
);

ALTER TABLE sr_address
    ADD CONSTRAINT fk_sr_address_type FOREIGN KEY (address_type)    
    REFERENCES sr_wiki_address_type (id); 
    
ALTER TABLE sr_address
    ADD CONSTRAINT fk_sr_address_iso_code_2 FOREIGN KEY (country_iso_code_2)    
    REFERENCES oc_country (iso_code_2);      

/* примеры адресов
доставка курьером: г. Москва, Столешников переулок д.15 кв.15
доставка курьером: МО, Истра, дачный поселок д.15
Почта России: 662971, Красноярский край, Железногорск г. улица Ленина дом 30 кв 33
СДЭК, до ПВЗ: г. Казань, ул. Гвардейская
СДЭК, до адреса: г. Москва, Столешников переулок д.15 кв.15
Деловые Линии: г.Тула, до терминала
самовывоз
*/

/* клиент */	
CREATE TABLE sr_customer (
	id INT(11) NOT NULL AUTO_INCREMENT, /* идентификатор */ 
    type TINYINT DEFAULT 1 NOT NULL, /* тип */
	person_id INT(11) NULL, /* персона */
    status TINYINT DEFAULT 1 NOT NULL, /* статус */    
	date_added DATETIME DEFAULT NOW() NOT NULL, /* дата создания */	
    PRIMARY KEY (id)
);

ALTER TABLE sr_customer
    ADD CONSTRAINT fk_sr_customer_person_id FOREIGN KEY (person_id)    
    REFERENCES sr_person (id);    
  
ALTER TABLE sr_customer
    ADD CONSTRAINT fk_sr_customer_type FOREIGN KEY (type)    
    REFERENCES sr_wiki_customer_type (id);  
    
ALTER TABLE sr_customer
    ADD CONSTRAINT fk_sr_customer_status FOREIGN KEY (status)    
    REFERENCES sr_wiki_customer_status (id);      
    
/* связки адресов и клиентов */    
CREATE TABLE sr_customer_address (
	id INT(11) NOT NULL AUTO_INCREMENT, /* идентификатор */
	address_id INT(11) NOT NULL, /* address.id */ 
    customer_id INT(11) NOT NULL, /* customer_id.id */
    CONSTRAINT sr_customer_address_uq UNIQUE (customer_id, address_Id),
	PRIMARY KEY (id)
); 

ALTER TABLE sr_customer_address
    ADD CONSTRAINT fk_sr_customer_address_address_id FOREIGN KEY (address_id)    
    REFERENCES sr_address (id);  
    
ALTER TABLE sr_customer_address
    ADD CONSTRAINT fk_sr_customer_address_customer_id FOREIGN KEY (customer_id)    
    REFERENCES sr_customer (id); 
    
CREATE INDEX sr_customer_address_customer_id_index ON sr_customer_address (customer_id);    
    
/* организации или ИП */    
CREATE TABLE sr_customer_company (
	id INT(11) NOT NULL AUTO_INCREMENT, /* идентификатор */ 
    customer_id INT(11) NOT NULL,
    country_iso_code_2 CHAR(2) DEFAULT 'RU' NOT NULL,
    inn VARCHAR(30) NULL, /* 123456789012 */   
    short_name VARCHAR(255) NOT NULL, /* краткое наименование */
    long_name VARCHAR(255) NULL, /* полное наименование */
    -- phone_number VARCHAR(30) NOT NULL, /* (999) 111-11-11 */
    -- email VARCHAR(255) NULL, /* a@a.ru */	
    PRIMARY KEY (id)
);  

ALTER TABLE sr_customer_company
    ADD CONSTRAINT fk_sr_customer_company_id FOREIGN KEY (customer_id)    
    REFERENCES sr_customer (id);  
    
ALTER TABLE sr_customer_company
    ADD CONSTRAINT fk_sr_customer_company_iso_code_2 FOREIGN KEY (country_iso_code_2)    
    REFERENCES oc_country (iso_code_2);  
        
/* контакты для организаций */    
CREATE TABLE sr_customer_contact (
	id INT(11) NOT NULL AUTO_INCREMENT, /* идентификатор */ 
    type TINYINT DEFAULT 1 NOT NULL, /* тип */
    customer_id INT(11) NOT NULL,
    person_id INT(11) NOT NULL,      
    CONSTRAINT sr_customer_contact_uq UNIQUE (customer_id, person_id),
    PRIMARY KEY (id)
);  

ALTER TABLE sr_customer_contact
    ADD CONSTRAINT fk_sr_customer_contact_customer_id FOREIGN KEY (customer_id)    
    REFERENCES sr_customer (id);  
    
ALTER TABLE sr_customer_contact
    ADD CONSTRAINT fk_sr_customer_contact_person_id FOREIGN KEY (person_id)    
    REFERENCES sr_person (id); 
    
ALTER TABLE sr_customer_contact
    ADD CONSTRAINT fk_sr_customer_contact_type FOREIGN KEY (type)    
    REFERENCES sr_wiki_contact_type (id);      
    
/* заказы клиентов */
CREATE TABLE sr_order (
	id INT(11) NOT NULL AUTO_INCREMENT, /* идентификатор */
    order_no INT(11) NOT NULL, /* номер заказа */
    order_sub_no INT(11) NULL, /* расширение номера заказа */
    order_year TINYINT DEFAULT 18 NOT NULL, /* год */
    order_date DATETIME NOT NULL, /* дата заказа */
    order_type TINYINT DEFAULT 1 NOT NULL, /* тип заказа */
    source_type TINYINT DEFAULT 4 NOT NULL, /* источник */
	advert_type TINYINT DEFAULT 1 NOT NULL, /* канал привлечения */
    payment_type TINYINT DEFAULT 5 NOT NULL, /* тип оплаты */
    store_id TINYINT DEFAULT 1 NOT NULL, /* магазин (1- sir-richard.ru, 2- pribormaster.ru) */  
    category_product_id SMALLINT DEFAULT 0 NOT NULL,
    customer_id INT(11) NOT NULL, 
    amount_total DECIMAL(15,4) DEFAULT 0 NOT NULL, /* итоговая сумма товара (без учета доставки) */
	amount_total_with_delivery DECIMAL(15,4) DEFAULT 0 NOT NULL, /* итоговая сумма к оплате клиенту (равна итоговой детализации + стоимости доставки)*/
	amount_bill DECIMAL(15,4) DEFAULT 0 NOT NULL, /* сумма чека (наш доход) */
	amount_supplier DECIMAL(15,4) DEFAULT 0 NOT NULL, /* стоимость закупки */
	amount_margin DECIMAL(15,4) DEFAULT 0 NOT NULL, /* прибыль за вычетом налогов (amount_bill - amount_supplier) */	
	amount_postpay DECIMAL(15,4) DEFAULT 0 NOT NULL, /* стоимость постоплаты */
	annotation VARCHAR(255) NULL /* примечание */,
    status INT(11) NOT NULL, /* текущий статус */
    status_email INT(11) DEFAULT 0 NOT NULL, /* текущий статус рассылок */
    offer_count_day TINYINT NULL, /* число дней срока предложения */ 
    offer_date_start DATETIME NULL, /* дата начала срока действия предложения */ 
	date_added DATETIME DEFAULT NOW() NOT NULL, /* дата создания */
	user_added INT(11) DEFAULT 1 NOT NULL, /* 1- al, 2- lara */
    date_modified DATETIME NULL, /* дата изменения */     
    CONSTRAINT sr_order_uq UNIQUE (order_no, order_sub_no, order_year),
	PRIMARY KEY (id)
);
  
ALTER TABLE sr_order
    ADD CONSTRAINT fk_sr_order_type FOREIGN KEY (order_type)    
    REFERENCES sr_wiki_order_type (id);     
    
ALTER TABLE sr_order
    ADD CONSTRAINT fk_sr_order_source_type FOREIGN KEY (source_type)    
    REFERENCES sr_wiki_order_source (id);        
    
ALTER TABLE sr_order
    ADD CONSTRAINT fk_sr_order_advert_type FOREIGN KEY (advert_type)    
    REFERENCES sr_wiki_order_advert (id);
    
ALTER TABLE sr_order
    ADD CONSTRAINT fk_sr_order_payment_type FOREIGN KEY (payment_type)    
    REFERENCES sr_wiki_order_payment (id);       
    
ALTER TABLE sr_order
    ADD CONSTRAINT fk_sr_order_category_product_id FOREIGN KEY (category_product_id)    
    REFERENCES sr_wiki_category_product (id);    
    
ALTER TABLE sr_order
    ADD CONSTRAINT fk_sr_order_customer_id FOREIGN KEY (customer_id)    
    REFERENCES sr_customer (id);     
    
ALTER TABLE sr_order
    ADD CONSTRAINT fk_sr_order_user_added FOREIGN KEY (user_added)    
    REFERENCES oc_user (user_id);      
    
CREATE INDEX sr_order_customer_id_index ON sr_order (customer_id);    
CREATE INDEX sr_order_order_date_index ON sr_order (order_date);
    
/* детализация заказа */    
CREATE TABLE sr_order_item (
	id INT(11) NOT NULL AUTO_INCREMENT, /* идентификатор */
    no INT(11) DEFAULT 1 NOT NULL, /* идентификатор */
    order_id INT(11) NOT NULL,
    product_id INT(11) NOT NULL,
    price DECIMAL(15,4) NOT NULL, /* цена */
    quantity INT(4) DEFAULT 1 NOT NULL, /* количество */
    discount_rate DECIMAL(15,4) NOT NULL, /* скидка */
    amount DECIMAL(15,4) NOT NULL, /* итого */
	amount_supplier DECIMAL(15,4) NOT NULL, /* стоимость закупки */    
    CONSTRAINT sr_order_item_uq UNIQUE (order_id, no),  
	PRIMARY KEY (id)
);   

ALTER TABLE sr_order_item
    ADD CONSTRAINT fk_sr_order_item_order_id FOREIGN KEY (order_id)    
    REFERENCES sr_order (id);     
    
ALTER TABLE sr_order_item
    ADD CONSTRAINT fk_sr_order_item_product_id FOREIGN KEY (product_id)    
    REFERENCES oc_product (product_id);     
    
CREATE INDEX sr_order_item_order_id_index ON sr_order_item (order_id);
	
/* доставка заказа */    
CREATE TABLE sr_order_delivery (
	id INT(11) NOT NULL AUTO_INCREMENT, /* идентификатор */    
    order_id INT(11) NOT NULL, /* ссылка на заказ */
    delivery_type SMALLINT DEFAULT 301, /* способ доставки */
    payment_delivery_type TINYINT DEFAULT 1 NOT NULL, /* тип оплаты доставки (покупатель, продавец) */
    address_id INT(11) NOT NULL, /* адрес доставки */
    recipient_id INT(11) NULL, /* получатель, по умолчанию, это заказчик */
    date_delivery DATETIME NULL,
    time_in TIME NULL,
    time_out TIME NULL,    
    price DECIMAL(15,4) NOT NULL, /* цена доставки */
    customer_price DECIMAL(15,4) NULL, /* стоимость доставки для покупателя */
    seller_price DECIMAL(15,4) NULL, /* стоимость доставки для продавца */    
    annotation VARCHAR(255) NULL /* описание */,
    track_code VARCHAR(255) NULL /* трэк-код для отслеживания */,
	PRIMARY KEY (id)
); 
    
ALTER TABLE sr_order_delivery
    ADD CONSTRAINT fk_sr_order_delivery_order_id FOREIGN KEY (order_id)    
    REFERENCES sr_order (id);     
    
ALTER TABLE sr_order_delivery
    ADD CONSTRAINT fk_sr_order_delivery_type FOREIGN KEY (delivery_type)    
    REFERENCES sr_wiki_order_delivery (id);
    
ALTER TABLE sr_order_delivery
    ADD CONSTRAINT fk_sr_order_address_id FOREIGN KEY (address_id)    
    REFERENCES sr_address (id); 
    
ALTER TABLE sr_order_delivery
    ADD CONSTRAINT fk_sr_order_recipient_id FOREIGN KEY (recipient_id)    
    REFERENCES sr_person (id);         
    
CREATE INDEX sr_order_delivery_order_id_index ON sr_order_delivery (order_id);    
    
/* статусы заказа */    
CREATE TABLE sr_order_status (
	id INT(11) NOT NULL AUTO_INCREMENT, /* идентификатор */    
    order_id INT(11) NOT NULL, /* ссылка на заказ */    
    status INT(11) NOT NULL, /* статус */
    crm_status  VARCHAR(128) NULL, /* статус CRM */                
    crm_sub_status VARCHAR(128) NULL, /* этап обработки CRM */
	date_added DATETIME DEFAULT NOW() NOT NULL, /* дата создания */
	user_added INT(11) DEFAULT 1 NOT NULL, /* 1- al, 2- lara */     
	PRIMARY KEY (id)
);     

ALTER TABLE sr_order_status
    ADD CONSTRAINT fk_sr_order_status_order_id FOREIGN KEY (order_id)    
    REFERENCES sr_order (id);     
    
ALTER TABLE sr_order_status
    ADD CONSTRAINT fk_sr_order_status_status FOREIGN KEY (status)    
    REFERENCES sr_wiki_order_status (id);
    
CREATE INDEX sr_order_status_order_id_index ON sr_order_status (order_id);        
  
/* коментарии к заказу */    
CREATE TABLE sr_order_comment (
	id INT(11) NOT NULL AUTO_INCREMENT, /* идентификатор */    
    order_id INT(11) NOT NULL, /* ссылка на заказ */    
    comment_type INT(11) NOT NULL, /* тип 1- коментарий, 2- конвертер */
    code VARCHAR(30) NOT NULL, /* ключ */
    value VARCHAR(255) NULL, /* описание */	   
	PRIMARY KEY (id)
);    

ALTER TABLE sr_order_comment
    ADD CONSTRAINT fk_sr_order_comment_order_id FOREIGN KEY (order_id)    
    REFERENCES sr_order (id);     
    
ALTER TABLE sr_order_comment
    ADD CONSTRAINT fk_sr_sr_order_comment_comment_type FOREIGN KEY (comment_type)    
    REFERENCES sr_wiki_comment_type (id);
    
CREATE INDEX sr_order_comment_order_id_index ON sr_order_comment (order_id);    

CREATE TABLE sr_order_crm_connect (
	id INT(11) NOT NULL AUTO_INCREMENT, /* идентификатор */    
    order_id INT(11) NOT NULL, /* ссылка на заказ */    
    crm_id INT(11) NOT NULL, /* ссылка на  */
    parent_crm_id BIGINT(30) DEFAULT 0 NOT NULL, /* ссылка на идентификатор заказа из crm */                
    parent_crm_code VARCHAR(30) NULL, /* номер заказа в crm */                    
    crm_status INT(11) NOT NULL, /* статус обработки */                
	PRIMARY KEY (id)
);  

CREATE INDEX sr_order_crm_connect_order_id_index ON sr_order_crm_connect (order_id);         
CREATE INDEX sr_order_crm_connect_parent_crm_id_index ON sr_order_crm_connect (crm_id, parent_crm_id);         
CREATE INDEX sr_order_crm_connect_parent_crm_code_index ON sr_order_crm_connect (parent_crm_code);         
    
    /*
sr_wiki_operate
1 подтверждение заявки
2 постоплата получена    
    */
    
CREATE TABLE sr_order_operate (
	id INT(11) NOT NULL AUTO_INCREMENT, /* идентификатор */    
	order_id INT NOT NULL,
    date_operate DATETIME NOT NULL, /* дата создания */
    operate_id INT NOT NULL, 
	amount_total DECIMAL(15,4) DEFAULT 0 NOT NULL, /* итоговая сумма к оплате клиенту (равна итоговой детализации + стоимости доставки)*/
	amount_bill DECIMAL(15,4) DEFAULT 0 NOT NULL, /* сумма чека (наш доход) */
	amount_supplier DECIMAL(15,4) DEFAULT 0 NOT NULL, /* стоимость закупки */
	amount_margin DECIMAL(15,4) DEFAULT 0 NOT NULL, /* прибыль за вычетом налогов (amount_bill - amount_supplier) */	
	amount_postpay DECIMAL(15,4) DEFAULT 0 NOT NULL, /* стоимость постоплаты */  
    date_added DATETIME DEFAULT NOW() NOT NULL, /* дата создания */
	user_added INT(11) DEFAULT 1 NOT NULL, /* 1- al, 2- lara */ 
    PRIMARY KEY (id)
);   

 /*  итоговые показатели за период */       
 CREATE TABLE sr_period_total_amount (
	id INT(11) NOT NULL AUTO_INCREMENT, /* идентификатор */
    amount_type INT NOT NULL, /* тип показателя */
    period_in DATETIME NOT NULL, /* период с  */
    period_out DATETIME NOT NULL, /* период по */
    amount DECIMAL(15,4) DEFAULT 0 NOT NULL, /* сумма */ 
    PRIMARY KEY (id)
); 

ALTER TABLE sr_period_total_amount
    ADD CONSTRAINT fk_sr_period_total_amount_amount_type FOREIGN KEY (amount_type)    
    REFERENCES sr_sys_total_amount (id);     
    
       
 /* utm метки для заказов по рекламным объявлениям */       
 CREATE TABLE sr_order_utm (
	id INT(11) NOT NULL AUTO_INCREMENT, /* идентификатор */    
    order_id INT(11) NOT NULL, /* ссылка на заказ */    
    utm_medium VARCHAR(30) NULL, /* способ размещения */
    utm_source VARCHAR(30) NULL, /* источник рекламы */
    utm_campaign VARCHAR(30) NULL, /* рекламная кампания */
	utm_content VARCHAR(30) NULL, /* содержимое рекламы */
	utm_term VARCHAR(30) NULL, /* ключевое слово */  
	PRIMARY KEY (id)
); 
    
ALTER TABLE sr_order_utm
    ADD CONSTRAINT fk_sr_order_utm_order_id FOREIGN KEY (order_id)    
    REFERENCES sr_order (id);     

-- http://google.ru/?utm_medium=cpc&utm_source=yandex.{source_type}&utm_campaign={campaign_id}&utm_content={ad_id}&utm_term={keyword}
    
/*
sr_order_annotation
	order_annotation_id
	order_id
	date_added
	annotation
*/
/*
sr_total_amount
	total_amount_id
	type_amount
	date_period_in
	date_period_out
*/

CREATE TABLE sr_stock (
	id INT(11) NOT NULL AUTO_INCREMENT, /* идентификатор */ 
	product_id INT(11) NOT NULL, 
    stock_id TINYINT NOT NULL,
    supplier_id TINYINT NOT NULL,
    supplier_price DECIMAL(15,4) NOT NULL, /* цена опта */    
    quantity INT(4) DEFAULT 1 NOT NULL, /* количество на складе */
    supplier_quantity INT(4) DEFAULT 0 NOT NULL, /* количество на складе поставщика */    
    comment VARCHAR(255) NULL, /* описание */
    PRIMARY KEY (id)
);

ALTER TABLE sr_stock
    ADD CONSTRAINT fk_sr_stock_product_id FOREIGN KEY (product_id)    
    REFERENCES oc_product (product_id); 
    
ALTER TABLE sr_stock
    ADD CONSTRAINT fk_sr_stock_stock_id FOREIGN KEY (stock_id)    
    REFERENCES sr_wiki_stock (id);    
    
 ALTER TABLE sr_stock
    ADD CONSTRAINT fk_sr_stock_supplier_id FOREIGN KEY (supplier_id)    
    REFERENCES sr_wiki_supplier (id);       
    

/*-------------------------------------------------------------*/
/*                        ТАБЛИЦЫ СЛУЖЕБНЫЕ                    */
/*-------------------------------------------------------------*/

/* мапинг продукта на коды в другой таблице */    
CREATE TABLE sr_tmp_product_mapping (	
    product_id INT(11) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    google_table_stock_code VARCHAR(30) NULL,    
	PRIMARY KEY (product_id)
); 	

/*-------------------------------------------------------------*/
/*                        ТАБЛИЦЫ ПОЛЬЗОВАТЕЛЕЙ                */
/*-------------------------------------------------------------*/

/* настройки пользователя */
CREATE TABLE sr_user_query (
	id INT(11) NOT NULL AUTO_INCREMENT, /* идентификатор */ 
    user_id INT(11) NOT NULL, /* пользователь */
    form VARCHAR(30) NOT NULL, /* описание */ 
	code VARCHAR(255) NOT NULL, /* параметр */ 
	value VARCHAR(255) NOT NULL, /* значение */ 
    CONSTRAINT sr_user_query_uq UNIQUE (user_id, form, code),
	PRIMARY KEY(id)
);

/* пример таблицы для отчета */
CREATE TABLE sr_user_qqq (	
    id INT(11) NOT NULL AUTO_INCREMENT, /* идентификатор */ 
    user_id INT(11) NOT NULL,
    session_id INT(11) NOT NULL,
    -- ...
	PRIMARY KEY (id)
); 	

CREATE TABLE sr_wiki_alarm_event_type (	
    id INT(11) NOT NULL AUTO_INCREMENT, /* идентификатор */     
    annotation VARCHAR(255) NOT NULL,
	PRIMARY KEY (id)
); 	

/* alarm gps */
CREATE TABLE sr_alarm_event (	
    id INT(11) NOT NULL AUTO_INCREMENT, /* идентификатор */ 
    event_type TINYINT DEFAULT 1 NOT NULL, /* sr_wiki_alarm_event_type.id */
    annotation VARCHAR(255) NULL, /* */
    lng DECIMAL(15,4) NULL, 
    lat DECIMAL(15,4) NULL,
    temperature DECIMAL(15,4) NULL,
    humidity DECIMAL(15,4) NULL,
    date_added DATETIME DEFAULT NOW() NOT NULL, /* дата создания */
	PRIMARY KEY (id)
); 	
CREATE INDEX sr_alarm_event_date_added_index ON sr_alarm_event(event_type, date_added);  

/*-------------------------------------------------------------*/
/*                        ПРЕДСТАВЛЕНИЯ                        */
/*-------------------------------------------------------------*/

CREATE OR REPLACE VIEW sr_v_category
AS
SELECT c.category_id, c.parent_id, cd.name, c.status
  FROM oc_category c, oc_category_description cd
  WHERE c.category_id = cd.category_id 
    AND cd.language_id = 2;
    
CREATE OR REPLACE VIEW sr_v_product_light
AS
SELECT p.*, pd.name, pd.description product_description, 
       pd.meta_description product_meta_description, pd.meta_title product_meta_title, pd.meta_keyword product_meta_keyword
	FROM oc_product_description pd, oc_product p
    WHERE pd.product_id = p.product_id 
      AND pd.language_id = 2;
     
CREATE OR REPLACE VIEW sr_v_product
AS
SELECT p.product_id, p.model, p.delivery_name, pd.name, p.sku, p.quantity product_quantity, p.price product_price,
       p.image product_image, p.manufacturer_id, p.jan, p.isbn, p.mpn,
       pd.description product_description, 
       pd.meta_description product_meta_description, pd.meta_title product_meta_title, pd.meta_keyword product_meta_keyword,
	   cp.id category_id, cp.type_group category_group, cp.annotation category_annotation, 
       s.supplier_price, s.supplier_quantity supplier_quantity, s.quantity stock_quantity, s.supplier_id, p.status, p.category_group_id,
       weight, weight_class_id, length, width, height, composite
	FROM oc_product_description pd, oc_product p 
    LEFT OUTER JOIN sr_wiki_category_product cp ON (cp.id = p.category_group_id)
    LEFT OUTER JOIN sr_stock s ON (s.product_id = p.product_id)  
    LEFT OUTER JOIN oc_product_special ps ON (ps.product_id = p.product_id)  
    WHERE pd.product_id = p.product_id 
      AND pd.language_id = 2;
      -- AND p.status = 1;    

CREATE OR REPLACE VIEW sr_v_customer 
AS 
SELECT c.id, c.type, c.person_id, NULL inn, p.country_iso_code_2 country_iso_code_2, p.phone_number, p.email, p.first_name short_name, p.last_name long_name, c.status, c.date_added,
p.first_name first_name, p.last_name last_name, p.middle_name middle_name, NULL contact_id 
	FROM sr_customer c, sr_person p
    WHERE c.person_id = p.id 
      AND (c.type in (1, 4))
UNION
SELECT c.id, c.type, cco.person_id person_id, cc.inn, cc.country_iso_code_2 country_iso_code_2, p.phone_number phone_number, p.email, cc.short_name, cc.long_name, c.status, c.date_added,
p.first_name, p.last_name, p.middle_name, cco.id contact_id 
	FROM sr_customer_company cc, sr_customer c
    LEFT OUTER JOIN sr_customer_contact cco ON (c.id = cco.customer_id)
    LEFT OUTER JOIN sr_person p ON (p.id = cco.person_id)
    WHERE cc.customer_id = c.id
      AND (c.type in (2, 3, 5));
      -- AND (cco.type = 1)

/*      
UNION
SELECT c.id, c.type, c.person_id, cc.inn, cc.country_iso_code_2 country_iso_code_2, p.phone_number, p.email, cc.short_name, cc.long_name, c.status, c.date_added,
p.first_name first_name, p.last_name last_name, p.middle_name middle_name  
	FROM sr_customer_company cc, sr_customer c, sr_person p
    WHERE cc.customer_id = c.id
      AND c.person_id = p.id
      AND c.type = 3;   
*/      
      
CREATE OR REPLACE VIEW sr_v_customer_address
AS
SELECT ca.id customer_address_id, ca.customer_id, a.* 
	FROM sr_customer_address ca, sr_address a
	WHERE ca.address_id = a.id;
    
CREATE OR REPLACE VIEW sr_v_order
AS
SELECT o.*, c.person_id, c.last_name person_last_name, c.type customer_type, c.inn, c.short_name, c.long_name, c.phone_number, c.email,
	   od.delivery_type, od.payment_delivery_type, od.track_code, od.price delivery_price, od.time_in delivery_time_in, od.time_out delivery_time_out, od.date_delivery,
       a.id delivery_address_id, a.address, a.country_iso_code_2 country_iso_code_2, a.city, a.post_code, a.pvz, a.type address_type
	FROM sr_order o, sr_v_customer c, sr_order_delivery od, sr_address a
    WHERE o.customer_id = c.id
      AND od.order_id = o.id
      AND a.id = od.address_id; 
 
CREATE OR REPLACE VIEW sr_v_stock
AS
SELECT s.*, p.name product_name, p.sku, p.model product_model, p.product_price, p.product_quantity,
       p.category_annotation, p.category_group, p.category_group_id
	FROM sr_stock s, sr_v_product p
    WHERE s.product_id = p.product_id;
    
CREATE OR REPLACE VIEW sr_v_yb_offers
AS    
SELECT p.product_id product_id, p.sku shopSku, market_sku yandex_sku, 
	   null yandex_category, null status, supplier_stock, marketplace_seller yandex_seller,
       null marketSkuName, null marketCategoryName, 
       0 offer_price, 0 minPriceOnBeru, 0 maxPriceOnBeru, 0 defaultPriceOnBeru, 0 byboxPriceOnBeru, 0 outlierPrice, null `key`, null special_price	   
       from sr_marketpace_offer yb, sr_v_product_light p
  where p.product_id = yb.product_id
  and yb.marketplace_type = 4;    

/*-------------------------------------------------------------*/
/*                        ИНТЕГРАЦИЯ                           */
/*-------------------------------------------------------------*/

/* ЭТО ИНТЕГРАЦИЯ С МАРКЕТПЛЕЙСАМИ (пока только ОЗОН) */   
CREATE TABLE sr_marketpace_offer(
			id INT(11) NOT NULL AUTO_INCREMENT, /* идентификатор */
			product_id int(11) NOT NULL, /* product_id */            
			market_sku varchar(255) NULL, /* market_sku */			
			supplier_stock INT(11) DEFAULT 0 NOT NULL, /* 1 работа от склада поставщика,  0 от нашего склада  */ 
			marketplace_seller INT(11) DEFAULT 0 NOT NULL, /* продажи на площадке (1- разрешено, 0- блокировано) */
            marketplace_type INT(11) NOT NULL, /* 4- yandex.market,  5- ozon */ 
            special_price DECIMAL(15,4) NULL, /* цена на маркетплейсе (может отличаться от РРЦ) */
            cheater_rate DECIMAL(15,4) NULL, /* ставка, на которую уменьшаем при чите */
            cheater_type TINYINT DEFAULT 0 NOT NULL, /* 0- нет, 1- специальная цена по шедалеру читера */
            cheater_price DECIMAL(15,4) NULL, /* цена чита */
            cheater_price_delta DECIMAL(15,4) NULL, /* цена, которую нужно уменьшить при чите */
            etalon_price DECIMAL(15,4) NULL, /* цена маркетплейса без чита */
            CONSTRAINT oc_marketpace_offers_uq UNIQUE (product_id, marketplace_type),
			PRIMARY KEY (id));  
                      
/* must die таблица - нужно перейти на sr_marketpace_offer */            
CREATE TABLE oc_yb_offers(
			product_id int(11) NOT NULL, /* product_id */            
			shopSku varchar(255) NOT NULL, /*sku */
			yandex_sku varchar(255) NULL, /* market_sku */
			yandex_category varchar(255) NULL, 
			status varchar(255) NULL, 
			supplier_stock INT(11) DEFAULT 0 NOT NULL, /* 1 работа от склада поставщика,  0 от нашего склада  */ 
			yandex_seller INT(11) DEFAULT 0 NOT NULL, /* продажи на яндексе (1- разрешено, 0- блокирована) */
            special_price DECIMAL(15,4) NULL, /* цена на маркетплейсе (может отличаться от РРЦ) */
			marketSkuName varchar(255) NULL, 
			marketCategoryName varchar(255) NULL, 
			offer_price float NOT NULL DEFAULT '0', 
			minPriceOnBeru float NOT NULL DEFAULT '0', 
			maxPriceOnBeru float NOT NULL DEFAULT '0', 
			defaultPriceOnBeru float NOT NULL DEFAULT '0', 
			byboxPriceOnBeru float NOT NULL DEFAULT '0', 
			outlierPrice float NOT NULL DEFAULT '0', 
            `key` varchar(255) NULL, 
            CONSTRAINT oc_yb_offers_uq UNIQUE (shopSku),
			PRIMARY KEY (product_id));  
                        
CREATE TABLE oc_yb_history_price(
			offer_id varchar(255) NOT NULL, /* sku */
			offer_name varchar(255) NULL, 
			user int(11) NULL, 
			price float NULL, 
			date_update datetime NULL);            
          
/*            
id
sku
наименование
склад поставщика
яндекс
количество
цена
вкл
*/ 
        
CREATE TABLE oc_yb_product_group( 
				group_id int(11) NOT NULL AUTO_INCREMENT, 
				name varchar(255) NOT NULL, 
				filter_name text NULL DEFAULT NULL, 
				filter_model text NULL DEFAULT NULL, 
				filter_category text NULL DEFAULT NULL, 
				filter_product text NULL DEFAULT NULL, 
				filter_option text NULL DEFAULT NULL, 
				filter_price_from float NULL DEFAULT NULL, 
				filter_price_to float NULL DEFAULT NULL, 
				filter_quantity_from int(11) NULL DEFAULT NULL, 
				filter_quantity_to int(11) NULL DEFAULT NULL, 
				filter_status tinyint(1) NULL DEFAULT NULL, 
				PRIMARY KEY (group_id));  
                
CREATE TABLE oc_yb_product_to_product_group(
				product_id int(11) NOT NULL,
				group_id int(11) NOT NULL,
				PRIMARY KEY (product_id, group_id));
                
CREATE TABLE oc_yb_order_boxes(
				box_id int(11) NOT NULL , 
				order_id int(11) NOT NULL , 
				weight int(64) NOT NULL , 
				width int(64) NOT NULL ,
				height int(64) NOT NULL , 
				depth int(64) NOT NULL , 
				market_box_id int(11) NOT NULL , 
				fulfilmentId varchar(128) NOT NULL , 
				group_id int(11) NOT NULL, 
				PRIMARY KEY (box_id));                
                
ALTER TABLE oc_order ADD COLUMN shipment_id int(11) NOT NULL DEFAULT '0';
ALTER TABLE oc_order ADD COLUMN shipment_date date NULL DEFAULT NULL; /* дата отгрузки */
ALTER TABLE oc_order ADD COLUMN market_order_id int(11) NOT NULL DEFAULT '0'; /* идентификатор ордера маркета */
ALTER TABLE oc_order ADD COLUMN market_status varchar(128) NULL;
ALTER TABLE oc_order ADD COLUMN market_sub_status varchar(128) NULL;

CREATE INDEX oc_order_market_id_index ON oc_order (market_order_id); 

ALTER TABLE oc_order_history ADD COLUMN market_status varchar(128) NULL;
ALTER TABLE oc_order_history ADD COLUMN market_sub_status varchar(128) NULL;

CREATE TABLE `oc_ll_cdek_city_delivery_period` (
  `CityCode` int(11) NOT NULL,
  `period_min` int(11) NULL,
  `period_max` int(11) NULL,
  PRIMARY KEY (`CityCode`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;  

delete from oc_ll_cdek_city_delivery_period;        

-- ///////////////////////////////////////////////////////

/* JPA */
CREATE TABLE sr_test_student (
	id INT(11) NOT NULL AUTO_INCREMENT, /* идентификатор */
    name VARCHAR(255) NOT NULL /* примечание */,
	PRIMARY KEY (id)
);

/*
CREATE TABLE animals (
    grp ENUM('fish','mammal','bird') NOT NULL,
    id MEDIUMINT NOT NULL AUTO_INCREMENT,
    name CHAR(30) NOT NULL,
    PRIMARY KEY (grp,id)
) ENGINE=MyISAM;

INSERT INTO animals (grp,name) VALUES
    ('mammal','dog'),('mammal','cat'),
    ('bird','penguin'),('fish','lax'),('mammal','whale'),
    ('bird','ostrich');
*/

/*
INSERT INTO tbl (auto,text) VALUES(NULL,'text');
SELECT LAST_INSERT_ID();
*/

-- SELECT * FROM animals ORDER BY grp,id;

      