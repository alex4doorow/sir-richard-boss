package ru.sir.richard.boss.utils;

import ru.sir.richard.boss.dao.WikiDao;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.ProductCategory;

public class WikiTestHelper {

    public static ProductCategory createProductCategory(int id, WikiDao wikiDao) {
        ProductCategory categoryOne = wikiDao.getCategoryById(id);
        if (categoryOne == null) {
            categoryOne = new ProductCategory(id, "Тестовая категория " + id);
            wikiDao.getCategories().add(categoryOne);
        }
        return categoryOne;
    }

    public static Product createProduct(int id, ProductCategory productCategory, WikiDao wikiDao) {
        Product productOne = new Product(id, "Тестовый " + id);
        productOne.setSku("TST_" + id);
		productOne.setCategory(productCategory);
		wikiDao.getProducts().add(productOne);
        return productOne;
    }
}
