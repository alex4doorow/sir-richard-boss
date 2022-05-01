package ru.sir.richard.boss.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import ru.sir.richard.boss.model.data.Address;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.ProductCategory;
import ru.sir.richard.boss.model.data.SupplierStock;
import ru.sir.richard.boss.model.data.SupplierStockProduct;
import ru.sir.richard.boss.model.data.conditions.ProductConditions;
import ru.sir.richard.boss.model.types.OrderAmountTypes;
import ru.sir.richard.boss.model.types.SupplierTypes;
import ru.sir.richard.boss.model.utils.Pair;

public interface WikiDao {
 
	void init();
	List<ProductCategory> getCategories();
	List<Product> getProducts();
	
	SupplierStock getSupplierStock(SupplierTypes supplier, ProductCategory productCategory);
	SupplierStockProduct supplierStockProductFindById(int id);
	SupplierStockProduct supplierStockProductFindByProductId(int productId);
	
	ProductCategory getCategoryById(int categoryId);
	Product getProductById(int productId);
	Product getDbProductById(int productId);

	Product findProductBySku(String sku);
	List<Product> findProductsByName(String contextString);
	Product findSingleProductByName(String contextString);
	
	String findCdekPvzByAddress(Address address);
			
	Product createProductDescriptionMeta(Product product);
	Product createProductDescriptionMeta(int productId);
	void updatePriceAndQuantityProduct(Product product);
	void updateProductDescriptionMeta(Product product);
	void updateSupplierStockPrice(Product product);
	void updateYmOfferProduct(Product product);
	void updateYmOfferMapping(List<Product> products);
	
	void updateOzonOfferProduct(Product product);
	
	void updateQuantityProduct(int productId, int quantity);	
	//void updateQuantityProductsBySuplierStockProducts(List<Product> products);
	
	void updateDeltaQuantityProduct2(Product product, int deltaQuantity, boolean isProduct, boolean isStock, boolean isSynchronize);
		
	int addSupplierStockProduct(SupplierStockProduct supplierStockProduct);
	void updateSupplierStockProduct(SupplierStockProduct supplierStockProduct);	
	void supplierStockProductSaveOrUpdate(SupplierStockProduct supplierStockProduct);
	void deleteSupplierStockProduct(int supplierStockProductId);
	
	BigDecimal ejectTotalAmountsByConditions(OrderAmountTypes amountType, Pair<Date> period);
	
	List<Product> listProductsByConditions(ProductConditions productConditions);
	List<Product> listYmProductsByConditions(ProductConditions productConditions);
	List<Product> listOzonProductsByConditions(ProductConditions productConditions);
	
}
