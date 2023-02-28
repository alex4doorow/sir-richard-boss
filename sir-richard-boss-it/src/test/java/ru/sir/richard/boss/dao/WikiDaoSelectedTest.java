package ru.sir.richard.boss.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.sir.richard.boss.model.data.Product;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
public class WikiDaoSelectedTest {

    @Autowired
    private WikiDao wikiDao;

    @BeforeEach
    public void beforeEach() {
        wikiDao.init(false);
    }

    @Test
    public void testStream1() {
/*
        List<User> users = generateUsersByFaker(101)
                .stream()
                .filter(u -> u.getAge() < 34)
                .filter(u -> u.getFirstName().startsWith("A"))
                .sorted(Comparator.comparing(User::getAge).thenComparing(User::getFirstName).thenComparing(User::getLastName))
                .collect(Collectors.toList());

 */

        long count1 = wikiDao.getProducts().stream()
                .filter(p -> StringUtils.containsIgnoreCase(p.getName(), "bird gard")).count();
        log.info("count1: {}", count1);

        Comparator<Product> byProductCategoryName1 =
                (Product o1, Product o2) -> o1.getCategory().getName().compareTo(o2.getCategory().getName());
        log.info("{}", byProductCategoryName1);

        Comparator<Product> byProductCategoryName2 =
                Comparator.comparing(o -> o.getCategory().getName());
        log.info("{}", byProductCategoryName2);

        Comparator<Product> byProductCategoryName3 =
                (Product o1, Product o2) -> o1.getCategory().getName().compareTo(o2.getName());
        log.info("{}", byProductCategoryName3);

        //List<Product> products1 =
        //.sorted(Comparator.comparing(User::getAge).thenComparing(User::getFirstName).thenComparing(User::getLastName))

        //wikiDao.getProducts().forEach(p -> log.info("{}", p));

        //Comparator<Product> comparatorByCategoryAndName = Comparator.comparing(Product::getViewName).thenComparing(Product::getName);
        //Comparator<Product> comparatorByCategoryAndName = new ProductCategoryComparator().thenComparing(Product::getViewName).thenComparing(Product::getPrice);

        Comparator<Product> comparatorByCategoryAndName2 = new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o1.getCategory().getName().toUpperCase().compareTo(o2.getCategory().getName().toUpperCase());
            }
        }.thenComparing(Product::getViewName).reversed().thenComparing(Product::getPrice);

        Comparator<Product> comparatorByCategoryAndName3 = Comparator.comparing(
                        (Product p) -> p.getCategory().getName().toUpperCase())
                .thenComparing(Product::getViewName)
                .reversed()
                .thenComparing(Product::getPrice);

        log.info("1:");
        wikiDao.getProducts().stream()
                .filter(p -> StringUtils.containsIgnoreCase(p.getName(), "bird gard"))
                .filter(p -> p.getPrice().compareTo(BigDecimal.valueOf(10000L)) > 0)
                //.sorted(Comparator.comparing(Product::getId))
                //.sorted(comparatorByCategoryAndName2)
                .sorted(Comparator.comparing((Product p) -> p.getCategory().getName().toUpperCase())
                        .thenComparing(Product::getViewName)
                        .thenComparing(Product::getPrice))
                //.sorted((o1, o2) -> o1.getCategory().getName().compareTo(o2.getCategory().getName()))
                //.sorted((o1, o2) -> o1.getViewSKU().compareTo(o2.getViewSKU()))
                //.sorted(Comparator.comparing(Product::getName).thenComparing(Product::getViewSKU))
                //.sorted(Comparator.comparing(Product::getName))
                //.sorted(new ProductCategoryComparator().reversed())
                //.sorted(new ProductCategoryComparator())
                //.sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
                .forEach(p -> log.info("{}, {}, {}, {}, {}", p.getId(),
                        p.getViewSKU(), p.getCategory().getName(), p.getPrice(), p.getName()));

        String productString = wikiDao.getProducts().stream()
                .map(product -> product.getId() + " " + product.getName() + " " + product.getCategory().getName())
                .collect(Collectors.joining(", "));
        log.info("2: {}\n\r", productString);

        productString = wikiDao.getProducts().stream()
                .sorted(Comparator.comparing(Product::getId))
                .map(p -> String.format("%d %s %s", p.getId(), p.getViewSKU(), p.getPrice()))
                .collect(Collectors.joining("\n\r"));
        log.info("3: {}\n\r", productString);

    }

    private void out(Product p, LogOutputer<Product> logOutputer) {
        /*
        logOutputer = new LogOutputer<Product>() {
            @Override
            public void print(Product p) {
                log.info("{}", p);
            }
        };
        logOutputer.print(p);
        */
        logOutputer = product -> log.info("{}", product);
        logOutputer.print(p);
    }

    @FunctionalInterface
    interface LogOutputer<T> {
        void print(T object);
    }

    static class ProductCategoryComparator implements Comparator<Product> {

        public int compare(Product a, Product b) {

            return a.getCategory().getName().toUpperCase().compareTo(b.getCategory().getName().toUpperCase());
        }
    }


}
