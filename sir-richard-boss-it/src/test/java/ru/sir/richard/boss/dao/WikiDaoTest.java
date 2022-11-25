package ru.sir.richard.boss.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;

import com.ibm.icu.math.BigDecimal;

import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.ProductCategory;
import ru.sir.richard.boss.model.data.SupplierStock;
import ru.sir.richard.boss.model.data.SupplierStockProduct;
import ru.sir.richard.boss.model.data.conditions.ProductConditions;
import ru.sir.richard.boss.model.types.SupplierTypes;

@SpringBootTest
@PropertySource(value="classpath:application-it.properties", encoding="UTF-8")
public class WikiDaoTest {
	
	private final Logger logger = LoggerFactory.getLogger(WikiDaoTest.class);
	
	@Autowired
	private WikiDao wikiDao;
	
	@BeforeEach
	public void init() {
				
	}

	@Test
	public void testGetDbProductById() {
	    wikiDao.init(false);

	    Product product = wikiDao.getDbProductById(32);
	    assertEquals(32, product.getId());
	    assertEquals("Отпугиватель змей ЭкоСнайпер LS-107", product.getName());
	}
	
	@Test
	public void testSupplierStockProductFindById() {
	    wikiDao.init(false);

	    SupplierStockProduct result;
	    result = wikiDao.supplierStockProductFindById(501);
	    assertEquals(501, result.getId());
	    assertEquals(985, result.getProduct().getId());

	    wikiDao.supplierStockProductFindByProductId(985);
	    assertEquals(501, result.getId());
	    assertEquals(985, result.getProduct().getId());

	    wikiDao.getSupplierStock(SupplierTypes.SITITEK, null);
	}
	
	@ParameterizedTest
	@ValueSource(ints = { 985, 986 })
	public void testListProductsByConditions(int productId) {
	    wikiDao.init(false);

	    ProductConditions productConditions = new ProductConditions();
	    productConditions.setProductId(productId);
	    List<Product> products = wikiDao.listProductsByConditions(productConditions);
	    products.forEach(product -> logger.debug("testListProductsByConditions: {}, {}", product.getId(),
		    product.getName()));
	}
	
	@Test
	public void testGetSupplierStocks() {
	    wikiDao.init(false);

	    SupplierStock stock = wikiDao.getSupplierStocks();
	    assertEquals(BigDecimal.valueOf(483788.6000), stock.getTotalSupplierAmount());
	}
	
	@Test
	public void testUpdateDeltaQuantityProduct2_1() {

	    // (1 кейс) заказ оформлен вручную, у нас комплект, списываем товар комплекта
	    // (2 кейс) заказ оформлен вручную, у нас штучный товар, списываем поштучно

	    // (3 кейс) заказ оформлен на я.маркете, у нас комплект, списываем товар
	    // комплекта
	    // (4 кейс) заказ оформлен на я.маркете, у нас штучный товар, списываем поштучно

	    // (5 кейс) заказ оформлен на озоне, у нас комплект, списываем товар комплекта
	    // (6 кейс) заказ оформлен на озоне, у нас штучный товар, списываем поштучно

	    ProductCategory category103 = wikiDao.getCategoryById(103);
	    if (category103 == null) {
		category103 = new ProductCategory(103, "Тестовая категория 103");
		wikiDao.getCategories().add(category103);
	    }
	    ProductCategory category201 = wikiDao.getCategoryById(201);
	    if (category201 == null) {
		category201 = new ProductCategory(201, "Тестовая категория 201");
		wikiDao.getCategories().add(category201);
	    }

	    Product product985 = wikiDao.getDbProductById(985);
	    product985.setCategory(category201);
	    wikiDao.getProducts().add(product985);

	    Product product986 = wikiDao.getDbProductById(986);
	    product985.setCategory(category201);
	    wikiDao.getProducts().add(product986);

	    Product product1029 = wikiDao.getDbProductById(1029);
	    product1029.setCategory(category201);
	    wikiDao.getProducts().add(product1029);
	}	

}
