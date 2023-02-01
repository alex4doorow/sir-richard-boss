SET SQL_SAFE_UPDATES = 0;

-- total_amounts
DELETE FROM sr_period_total_amount;

-- 101 рекламный бюджет (неделя)
INSERT INTO sr_period_total_amount
(amount_type, period_in, period_out, amount)
VALUES
(132, '2023-01-02', '2023-01-08', 0),
(132, '2023-01-09', '2023-01-15', 9176),
(132, '2023-01-16', '2023-01-22', 13064),
(132, '2023-01-23', '2023-01-29', 12111);


-- 101 рекламный бюджет (месяц)
INSERT INTO sr_period_total_amount
(amount_type, period_in, period_out, amount)
VALUES
(101, '2016-01-01', '2017-01-31', 19500),
(101, '2016-02-01', '2017-02-28', 27900),
(101, '2016-03-01', '2017-03-31', 35800),
(101, '2016-04-01', '2017-04-30', 34032),
(101, '2016-05-01', '2017-05-31', 40037),
(101, '2016-06-01', '2017-06-30', 41000),
(101, '2016-07-01', '2017-07-31', 41200),
(101, '2016-08-01', '2017-08-31', 44047),
(101, '2016-09-01', '2017-09-30', 30200),
(101, '2016-10-01', '2017-10-31', 45530),
(101, '2016-11-01', '2017-11-30', 37500),
(101, '2016-12-01', '2016-12-31', 38064),

(101, '2017-01-01', '2017-01-31', 30000),
(101, '2017-02-01', '2017-02-28', 30000),
(101, '2017-03-01', '2017-03-31', 44000),
(101, '2017-04-01', '2017-04-30', 36000),
(101, '2017-05-01', '2017-05-31', 51500),
(101, '2017-06-01', '2017-06-30', 49500),
(101, '2017-07-01', '2017-07-31', 30000),
(101, '2017-08-01', '2017-08-31', 38500),
(101, '2017-09-01', '2017-09-30', 43000),
(101, '2017-10-01', '2017-10-31', 67000),
(101, '2017-11-01', '2017-11-30', 50000),
(101, '2017-12-01', '2017-12-31', 53000),

(101, '2018-01-01', '2018-01-31', 30000),
(101, '2018-02-01', '2018-02-28', 51200),
(101, '2018-03-01', '2018-03-31', 45000),
(101, '2018-04-01', '2018-04-30', 60000),
(101, '2018-05-01', '2018-05-31', 81000),
(101, '2018-06-01', '2018-06-30', 80000),
(101, '2018-07-01', '2018-07-31', 55000),
(101, '2018-08-01', '2018-08-31', 40000),
(101, '2018-09-01', '2018-09-30', 54550),
(101, '2018-10-01', '2018-10-31', 86000),
(101, '2018-11-01', '2018-11-30', 90000),
(101, '2018-12-01', '2018-12-31', 92000),

(101, '2019-01-01', '2019-01-31', 82000),
(101, '2019-02-01', '2019-02-28', 83000),
(101, '2019-03-01', '2019-03-31', 80000),
(101, '2019-04-01', '2019-04-30', 100000),
(101, '2019-05-01', '2019-05-31', 103000),
(101, '2019-06-01', '2019-06-30', 123000),
(101, '2019-07-01', '2019-07-31', 100029),
(101, '2019-08-01', '2019-08-31', 60000),
(101, '2019-09-01', '2019-09-30', 75000),
(101, '2019-10-01', '2019-10-31', 130000),
(101, '2019-11-01', '2019-11-30', 100000),
(101, '2019-12-01', '2019-12-31', 100000),

(101, '2020-01-01', '2020-01-31', 65000),
(101, '2020-02-01', '2020-02-29', 85000),
(101, '2020-03-01', '2020-03-31', 95000),
(101, '2020-04-01', '2020-04-30', 80000),
(101, '2020-05-01', '2020-05-31', 90000),
(101, '2020-06-01', '2020-06-30', 105000),
(101, '2020-07-01', '2020-07-31', 80000),
(101, '2020-08-01', '2020-08-31', 95000),
(101, '2020-09-01', '2020-09-30', 70000),
(101, '2020-10-01', '2020-10-31', 75000),
(101, '2020-11-01', '2020-11-30', 90000),
(101, '2020-12-01', '2020-12-31', 90000),

(101, '2021-01-01', '2021-01-31', 60000),
(101, '2021-02-01', '2021-02-28', 55000),
(101, '2021-03-01', '2021-03-31', 88000),
(101, '2021-04-01', '2021-04-30', 80000),
(101, '2021-05-01', '2021-05-31', 80000),
(101, '2021-06-01', '2021-06-30', 100000),
(101, '2021-07-01', '2021-07-31', 50000),
(101, '2021-08-01', '2021-08-31', 41000),
(101, '2021-09-01', '2021-09-30', 99058),
(101, '2021-10-01', '2021-10-31', 71200),
(101, '2021-11-01', '2021-11-30', 60000),
(101, '2021-12-01', '2021-12-31', 0),

(101, '2022-01-01', '2022-01-31', 40000),
(101, '2022-02-01', '2022-02-28', 70000),
(101, '2022-03-01', '2022-03-31', 68000),
(101, '2022-04-01', '2022-04-30', 48000),
(101, '2022-05-01', '2022-05-31', 83000),
(101, '2022-06-01', '2022-06-30', 92000),
(101, '2022-07-01', '2022-07-31', 98000),
(101, '2022-08-01', '2022-08-31', 67431),
(101, '2022-09-01', '2022-09-30', 91569),
(101, '2022-10-01', '2022-10-31', 96000),
(101, '2022-11-01', '2022-11-30', 110000),
(101, '2022-12-01', '2022-12-31', 82000);

-- 102 число сеансов за период
INSERT INTO sr_period_total_amount
(amount_type, period_in, period_out, amount)
VALUES
(102, '2016-01-01', '2017-01-31', 3551),
(102, '2016-02-01', '2017-02-28', 5001),
(102, '2016-03-01', '2017-03-31', 4249),
(102, '2016-04-01', '2017-04-30', 4252),
(102, '2016-05-01', '2017-05-31', 3993),
(102, '2016-06-01', '2017-06-30', 5634),
(102, '2016-07-01', '2017-07-31', 6616),
(102, '2016-08-01', '2017-08-31', 5546),
(102, '2016-09-01', '2017-09-30', 4537),
(102, '2016-10-01', '2017-10-31', 5771),
(102, '2016-11-01', '2017-11-30', 5467),
(102, '2016-12-01', '2016-12-31', 5047),

(102, '2017-01-01', '2017-01-31', 6406),
(102, '2017-02-01', '2017-02-28', 4615),
(102, '2017-03-01', '2017-03-31', 4336),
(102, '2017-04-01', '2017-04-30', 8070),
(102, '2017-05-01', '2017-05-31', 10523),
(102, '2017-06-01', '2017-06-30', 4718),
(102, '2017-07-01', '2017-07-31', 6254),
(102, '2017-08-01', '2017-08-31', 2884),
(102, '2017-09-01', '2017-09-30', 8437),
(102, '2017-10-01', '2017-10-31', 10081),
(102, '2017-11-01', '2017-11-30', 6498),
(102, '2017-12-01', '2017-12-31', 5667),

(102, '2018-01-01', '2018-01-31', 4046),
(102, '2018-02-01', '2018-02-28', 4538),
(102, '2018-03-01', '2018-03-31', 7042),
(102, '2018-04-01', '2018-04-30', 8804),
(102, '2018-05-01', '2018-05-31', 11370),
(102, '2018-06-01', '2018-06-30', 9823),
(102, '2018-07-01', '2018-07-31', 8300),
(102, '2018-08-01', '2018-08-31', 6657),
(102, '2018-09-01', '2018-09-30', 8705),
(102, '2018-10-01', '2018-10-31', 12697),
(102, '2018-11-01', '2018-11-30', 11227),
(102, '2018-12-01', '2018-12-31', 11209),

(102, '2019-01-01', '2019-01-31', 11289),
(102, '2019-02-01', '2019-02-28', 13241),
(102, '2019-03-01', '2019-03-31', 15617),
(102, '2019-04-01', '2019-04-30', 16114),
(102, '2019-05-01', '2019-05-31', 14180),
(102, '2019-06-01', '2019-06-30', 13908),
(102, '2019-07-01', '2019-07-31', 16464),
(102, '2019-08-01', '2019-08-31', 10229),
(102, '2019-09-01', '2019-09-30', 11915),
(102, '2019-10-01', '2019-10-31', 15842),
(102, '2019-11-01', '2019-11-30', 13899),
(102, '2019-12-01', '2019-12-31', 13120),

(102, '2020-01-01', '2020-01-31', 9776),
(102, '2020-02-01', '2020-02-29', 12253),
(102, '2020-03-01', '2020-03-31', 12762),
(102, '2020-04-01', '2020-04-30', 10293),
(102, '2020-05-01', '2020-05-31', 11682),
(102, '2020-06-01', '2020-06-30', 17163),
(102, '2020-07-01', '2020-07-31', 15619),
(102, '2020-08-01', '2020-08-31', 15892),
(102, '2020-09-01', '2020-09-30', 18214),
(102, '2020-10-01', '2020-10-31', 11100),
(102, '2020-11-01', '2020-11-30', 18060),
(102, '2020-12-01', '2020-12-31', 19731),

(102, '2021-01-01', '2021-01-31', 14052),
(102, '2021-02-01', '2021-02-28', 10541),
(102, '2021-03-01', '2021-03-31', 12304),
(102, '2021-04-01', '2021-04-30', 10286),
(102, '2021-05-01', '2021-05-31', 10440),
(102, '2021-06-01', '2021-06-30', 15218),
(102, '2021-07-01', '2021-07-31', 6696),
(102, '2021-08-01', '2021-08-31', 5238),
(102, '2021-09-01', '2021-09-30', 8135),
(102, '2021-10-01', '2021-10-31', 9248),
(102, '2021-11-01', '2021-11-30', 6819 + 2607),
(102, '2021-12-01', '2021-12-31', 6566 + 3744),

(102, '2022-01-01', '2022-01-31', 4676 + 2510),
(102, '2022-02-01', '2022-02-28', 5086 + 2335),
(102, '2022-03-01', '2022-03-31', 4211 + 3596),
(102, '2022-04-01', '2022-04-30', 3086 + 1780),
(102, '2022-05-01', '2022-05-31', 3904 + 2132),
(102, '2022-06-01', '2022-06-30', 5343 + 2092),
(102, '2022-07-01', '2022-07-31', 6088 + 2070),
(102, '2022-08-01', '2022-08-31', 3959 + 2124),
(102, '2022-09-01', '2022-09-30', 5311 + 1988),
(102, '2022-10-01', '2022-10-31', 6201 + 2196),
(102, '2022-11-01', '2022-11-30', 5306 + 1579),
(102, '2022-12-01', '2022-12-31', 4258 + 621);

-- 107 число уникальных посетителей за период
INSERT INTO sr_period_total_amount
(amount_type, period_in, period_out, amount)
VALUES
(107, '2018-01-01', '2018-01-31', 2860),
(107, '2018-02-01', '2018-02-28', 3443),
(107, '2018-03-01', '2018-03-31', 5274),
(107, '2018-04-01', '2018-04-30', 6945),
(107, '2018-05-01', '2018-05-31', 8780),
(107, '2018-06-01', '2018-06-30', 7616),
(107, '2018-07-01', '2018-07-31', 6415),
(107, '2018-08-01', '2018-08-31', 5117),
(107, '2018-09-01', '2018-09-30', 6455),
(107, '2018-10-01', '2018-10-31', 9103),
(107, '2018-11-01', '2018-11-30', 7967),
(107, '2018-12-01', '2018-12-31', 7675),

(107, '2019-01-01', '2019-01-31', 8175),
(107, '2019-02-01', '2019-02-28', 10215),
(107, '2019-03-01', '2019-03-31', 12571),
(107, '2019-04-01', '2019-04-30', 12488),
(107, '2019-05-01', '2019-05-31', 11315),
(107, '2019-06-01', '2019-06-30', 10878),
(107, '2019-07-01', '2019-07-31', 13086),
(107, '2019-08-01', '2019-08-31', 8722),
(107, '2019-09-01', '2019-09-30', 8845),
(107, '2019-10-01', '2019-10-31', 12499),
(107, '2019-11-01', '2019-11-30', 9719),
(107, '2019-12-01', '2019-12-31', 9425),

(107, '2020-01-01', '2020-01-31', 7525),
(107, '2020-02-01', '2020-02-29', 9768),
(107, '2020-03-01', '2020-03-31', 10211),
(107, '2020-04-01', '2020-04-30', 8290),
(107, '2020-05-01', '2020-05-31', 9266),
(107, '2020-06-01', '2020-06-30', 13545),
(107, '2020-07-01', '2020-07-31', 12381),
(107, '2020-08-01', '2020-08-31', 13597),
(107, '2020-09-01', '2020-09-30', 15470),
(107, '2020-10-01', '2020-10-31', 8874),
(107, '2020-11-01', '2020-11-30', 14476),
(107, '2020-12-01', '2020-12-31', 15936),

(107, '2021-01-01', '2021-01-31', 11140),
(107, '2021-02-01', '2021-02-28', 8489),
(107, '2021-03-01', '2021-03-31', 9835),
(107, '2021-04-01', '2021-04-30', 8086),
(107, '2021-05-01', '2021-05-31', 8423),
(107, '2021-06-01', '2021-06-30', 12669),
(107, '2021-07-01', '2021-07-31', 5508),
(107, '2021-08-01', '2021-08-31', 4153),
(107, '2021-09-01', '2021-09-30', 6682),
(107, '2021-10-01', '2021-10-31', 7254),
(107, '2021-11-01', '2021-11-30', 5434 + 2607),
(107, '2021-12-01', '2021-12-31', 5259 + 3744),

(107, '2022-01-01', '2022-01-31', 3753 + 2510),
(107, '2022-02-01', '2022-02-28', 4169 + 2335),
(107, '2022-03-01', '2022-03-31', 3513 + 3596),
(107, '2022-04-01', '2022-04-30', 2494 + 1780),
(107, '2022-05-01', '2022-05-31', 3169 + 2132),
(107, '2022-06-01', '2022-06-30', 4433 + 2092),
(107, '2022-07-01', '2022-07-31', 5078 + 2070),
(107, '2022-08-01', '2022-08-31', 3193 + 2124),
(107, '2022-09-01', '2022-09-30', 4469 + 1988),
(107, '2022-10-01', '2022-10-31', 5188 + 2196),
(107, '2022-11-01', '2022-11-30', 4429 + 1579),
(107, '2022-12-01', '2022-12-31', 3346 + 621);

-- 111 число новых посетителей за период
INSERT INTO sr_period_total_amount
(amount_type, period_in, period_out, amount)
VALUES
(111, '2018-01-01', '2018-01-31', 2715),
(111, '2018-02-01', '2018-02-28', 3270),
(111, '2018-03-01', '2018-03-31', 5070),
(111, '2018-04-01', '2018-04-30', 6706),
(111, '2018-05-01', '2018-05-31', 8480),
(111, '2018-06-01', '2018-06-30', 7277),
(111, '2018-07-01', '2018-07-31', 6144),
(111, '2018-08-01', '2018-08-31', 4869),
(111, '2018-09-01', '2018-09-30', 6262),
(111, '2018-10-01', '2018-10-31', 8758),
(111, '2018-11-01', '2018-11-30', 7454),
(111, '2018-12-01', '2018-12-31', 7194),

(111, '2019-01-01', '2019-01-31', 7738),
(111, '2019-02-01', '2019-02-28', 9716),
(111, '2019-03-01', '2019-03-31', 12189),
(111, '2019-04-01', '2019-04-30', 11954),
(111, '2019-05-01', '2019-05-31', 10472),
(111, '2019-06-01', '2019-06-30', 10327),
(111, '2019-07-01', '2019-07-31', 12401),
(111, '2019-08-01', '2019-08-31', 8283),
(111, '2019-09-01', '2019-09-30', 8563),
(111, '2019-10-01', '2019-10-31', 11933),
(111, '2019-11-01', '2019-11-30', 9244),
(111, '2019-12-01', '2019-12-31', 8898),

(111, '2020-01-01', '2020-01-31', 7226),
(111, '2020-02-01', '2020-02-29', 9294),
(111, '2020-03-01', '2020-03-31', 9786),
(111, '2020-04-01', '2020-04-30', 7922),
(111, '2020-05-01', '2020-05-31', 9056),
(111, '2020-06-01', '2020-06-30', 13185),
(111, '2020-07-01', '2020-07-31', 12047),
(111, '2020-08-01', '2020-08-31', 13313),
(111, '2020-09-01', '2020-09-30', 14961),
(111, '2020-10-01', '2020-10-31', 8712),
(111, '2020-11-01', '2020-11-30', 14044),
(111, '2020-12-01', '2020-12-31', 15223),

(111, '2021-01-01', '2021-01-31', 10279),
(111, '2021-02-01', '2021-02-28', 8038),
(111, '2021-03-01', '2021-03-31', 9216),
(111, '2021-04-01', '2021-04-30', 7606),
(111, '2021-05-01', '2021-05-31', 8165),
(111, '2021-06-01', '2021-06-30', 12144),
(111, '2021-07-01', '2021-07-31', 5135),
(111, '2021-08-01', '2021-08-31', 3950),
(111, '2021-09-01', '2021-09-30', 6547),
(111, '2021-10-01', '2021-10-31', 6872),
(111, '2021-11-01', '2021-11-30', 5137 + 2537),
(111, '2021-12-01', '2021-12-31', 4908 + 3644),

(111, '2022-01-01', '2022-01-31', 3522 + 2408),
(111, '2022-02-01', '2022-02-28', 3919 + 2216),
(111, '2022-03-01', '2022-03-31', 3318 + 3501),
(111, '2022-04-01', '2022-04-30', 2359 + 1698),
(111, '2022-05-01', '2022-05-31', 3045 + 2070),
(111, '2022-06-01', '2022-06-30', 4264 + 2092),
(111, '2022-07-01', '2022-07-31', 4864 + 1999),
(111, '2022-08-01', '2022-08-31', 2938 + 2082),
(111, '2022-09-01', '2022-09-30', 4224 + 1923),
(111, '2022-10-01', '2022-10-31', 4935 + 2125),
(111, '2022-11-01', '2022-11-30', 4157 + 1517),
(111, '2022-12-01', '2022-12-31', 3101 + 557);

