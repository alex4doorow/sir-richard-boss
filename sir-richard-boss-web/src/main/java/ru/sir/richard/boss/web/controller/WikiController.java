package ru.sir.richard.boss.web.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ru.sir.richard.boss.model.data.Comment;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.ProductCategory;
import ru.sir.richard.boss.model.data.SupplierStock;
import ru.sir.richard.boss.model.data.SupplierStockProduct;
import ru.sir.richard.boss.model.data.conditions.ProductConditions;
import ru.sir.richard.boss.model.types.CommentTypes;
import ru.sir.richard.boss.model.types.CrmTypes;
import ru.sir.richard.boss.model.types.SupplierTypes;
import ru.sir.richard.boss.model.utils.DateTimeUtils;
import ru.sir.richard.boss.model.utils.NumberUtils;
import ru.sir.richard.boss.web.data.FormProduct;
import ru.sir.richard.boss.web.data.FormSupplierStockProduct;
import ru.sir.richard.boss.web.validator.SupplierStockProductFormValidator;

@Controller
public class WikiController extends AnyController {
	
	private final Logger logger = LoggerFactory.getLogger(WikiController.class);
	
	@Autowired
	SupplierStockProductFormValidator supplierStockProductFormValidator;
	
	@InitBinder
	protected void initBinder(WebDataBinder binder, HttpServletRequest httpServletRequest) {	
		
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat(DateTimeUtils.DATE_FORMAT_dd_MM_yyyy), true));
		
		binder.registerCustomEditor(BigDecimal.class, 
				new BigDecimalEditor(BigDecimal.class, new DecimalFormat(NumberUtils.NUMBER_FORMAT_MONEY), true));
		//binder.setValidator(supplierStockProductFormValidator);
		
	}
	
	@RequestMapping(value = "/wiki/cdek/widjet/{contextString}", method = RequestMethod.GET)
	public String showCdekWidjet(@PathVariable("contextString") String contextString, Model model) {
		
		logger.debug("showCdekWidjet() :{}", contextString);
			
		populateDefaultModel(model);		
		return "wiki/cdekwidjet";
	}
	
	@RequestMapping(value = "/wiki/products/ozon", method = RequestMethod.GET)
	public String showOzonMarketProducts(Model model) {
		
		logger.debug("showOzonMarketProducts()");
			
		//wikiService.getConfig().saveFormStringValue(1, "productForm", "product.name", contextString);				
		ProductConditions productConditions = wikiService.getConfig().loadOzonProductConditions(OrderListController.USER_ID);		
		List<Product> products = wikiService.getWiki().listOzonProductsByConditions(productConditions);
		populateDefaultModel(model);
		
		
		model.addAttribute("products", products);
		model.addAttribute("listType", "ozon");
		
		return "wiki/ozonlistproducts";
	}	
	
	@RequestMapping(value = "/wiki/products/ozon/conditions/filter", method = RequestMethod.GET)
	public String showOzonMarketProductsByConditions(Model model) {

		logger.debug("showOzonMarketProductsByConditions()");
		ProductConditions productConditions = wikiService.getConfig().loadOzonProductConditions(OrderListController.USER_ID);
		model.addAttribute("productConditions", productConditions);
		populateDefaultModel(model);
		return "wiki/ozonproductconditionsform";
	}
	
	@RequestMapping(value = "/wiki/products/ozon/conditions/filter/exec", method = RequestMethod.POST)
	public String execOzonMarketProductsByConditions(@ModelAttribute("productConditions") ProductConditions productConditions,
			Model model, final RedirectAttributes redirectAttributes) {

		logger.debug("execOzonMarketProductsByConditions():{}", productConditions);
		wikiService.getConfig().saveOzonProductConditions(OrderListController.USER_ID, productConditions);
				
		List<Product> products = wikiService.getWiki().listOzonProductsByConditions(productConditions);
		populateDefaultModel(model);
		model.addAttribute("products", products);
		model.addAttribute("listType", "ozon");
		
		return "wiki/ozonlistproducts";		
	}
	
	@RequestMapping(value = "/wiki/products/ozon/offer-prices/updates", method = RequestMethod.GET)
	public String updateOfferPricesOzonMarketProducts(Model model, final RedirectAttributes redirectAttributes) {
				 
		logger.debug("updateOfferPricesOzonMarketProducts(): start");
					
		String resultOfferPricesUpdates = wikiService.ozonOfferPricesUpdates(true);
		redirectAttributes.addFlashAttribute("css", "success");
		redirectAttributes.addFlashAttribute("msg", resultOfferPricesUpdates);
		populateDefaultModel(model);
		
		logger.debug("updateOfferPricesOzonMarketProducts(): end");

		return "redirect:/wiki/products/ozon";
	}
	
	@RequestMapping(value = "/wiki/products/ozon/offer-prices/updates-full", method = RequestMethod.GET)
	public String updateOfferPricesOzonMarketProductsFull(Model model, final RedirectAttributes redirectAttributes) {
				 
		logger.debug("updateOfferPricesOzonMarketProductsFull(): start");
					
		String resultOfferPricesUpdates = wikiService.ozonOfferPricesUpdates(false);
		redirectAttributes.addFlashAttribute("css", "success");
		redirectAttributes.addFlashAttribute("msg", resultOfferPricesUpdates);
		populateDefaultModel(model);
		
		logger.debug("updateOfferPricesOzonMarketProductsFull(): end");

		return "redirect:/wiki/products/ozon";
	}
	
	@RequestMapping(value = "/wiki/products/ozon/disconnect", method = RequestMethod.GET)
	public String updateOfferPricesOzonDisconnect(Model model, final RedirectAttributes redirectAttributes) {
				 
		logger.debug("updateOfferPricesOzonDisconnect(): start");
					
		String resultOfferPricesUpdates = wikiService.ozonDisconnect();
		redirectAttributes.addFlashAttribute("css", "success");
		redirectAttributes.addFlashAttribute("msg", resultOfferPricesUpdates);
		populateDefaultModel(model);
		
		logger.debug("updateOfferPricesOzonDisconnect(): end");

		return "redirect:/wiki/products/ozon";
	}
	
	@RequestMapping(value = "/wiki/products/ym", method = RequestMethod.GET)
	public String showYandexMarketProducts(Model model) {
		
		logger.debug("showYandexMarketProducts()");		
		ProductConditions productConditions = wikiService.getConfig().loadYmProductConditions(OrderListController.USER_ID);		
		List<Product> products = wikiService.getWiki().listYmProductsByConditions(productConditions);
		populateDefaultModel(model);		
		
		model.addAttribute("products", products);
		model.addAttribute("listType", "ym");
		
		return "wiki/ymlistproducts";
	}	
	
	@RequestMapping(value = "/wiki/products/ym/conditions/filter", method = RequestMethod.GET)
	public String showYandexMarketProductsByConditions(Model model) {

		logger.debug("showYandexMarketProductsByConditions()");
		ProductConditions productConditions = wikiService.getConfig().loadYmProductConditions(OrderListController.USER_ID);
		model.addAttribute("productConditions", productConditions);
		populateDefaultModel(model);
		return "wiki/ymproductconditionsform";
	}
	
	@RequestMapping(value = "/wiki/products/ym/conditions/filter/exec", method = RequestMethod.POST)
	public String execYandexMarketProductsByConditions(@ModelAttribute("productConditions") ProductConditions productConditions,
			Model model, final RedirectAttributes redirectAttributes) {

		logger.debug("execYandexMarketProductsByConditions():{}", productConditions);
		wikiService.getConfig().saveYmProductConditions(OrderListController.USER_ID, productConditions);
				
		List<Product> products = wikiService.getWiki().listYmProductsByConditions(productConditions);
		populateDefaultModel(model);
		model.addAttribute("products", products);
		model.addAttribute("listType", "ym");
		
		return "wiki/ymlistproducts";		
	}
	
	@RequestMapping(value = "/wiki/products/ym/offer-prices/updates", method = RequestMethod.GET)
	public String updateOfferPricesYandexMarketProducts(Model model, final RedirectAttributes redirectAttributes) {
				 
		logger.debug("updateOfferPricesYandexMarketProducts(): start");
					
		String resultOfferPricesUpdates = wikiService.ymOfferPricesUpdates();
		redirectAttributes.addFlashAttribute("css", "success");
		redirectAttributes.addFlashAttribute("msg", resultOfferPricesUpdates);
		populateDefaultModel(model);
		
		logger.debug("updateOfferPricesYandexMarketProducts(): end");

		return "redirect:/wiki/products/ym";
	}
	
	@RequestMapping(value = "/wiki/products/{id}", method = RequestMethod.GET)
	public String showProduct(@PathVariable("id") int productId, Model model) {
		
		logger.debug("showProduct() :{}", productId);	
		wikiService.getConfig().saveFormIntegerValue(1, "productForm", "product.id", productId);
				
		ProductConditions productConditions = new ProductConditions();
		productConditions.setProductId(productId);
		
		List<Product> products = wikiService.getWiki().listProductsByConditions(productConditions);
		populateDefaultModel(model);
		model.addAttribute("productConditions", productConditions);
		model.addAttribute("products", products);
		return "wiki/listproducts";
	}	
	
	@RequestMapping(value = "/wiki/products/conditions/filter/exec", method = RequestMethod.POST)
	public String showAllProducts(@ModelAttribute("productConditions") ProductConditions productConditions,
			Model model, final RedirectAttributes redirectAttributes) {
		
		String productSku = productConditions.getSku().trim();
		logger.debug("showAllProducts() :{}", productSku);
		
		wikiService.getConfig().saveFormStringValue(OrderListController.USER_ID, "productForm", "product.sku", productSku);

		List<Product> products = wikiService.getWiki().listProductsByConditions(productConditions);
		populateDefaultModel(model);
		model.addAttribute("products", products);
		model.addAttribute("listType", "products");
		return "wiki/listproducts";
		
	}

	@RequestMapping(value = "/wiki/products/{id}/update/{listType}", method = RequestMethod.GET)
	public String showUpdateProductForm(@PathVariable("id") int id, @PathVariable("listType") String listType, Model model) {
		
		Product product = wikiService.getWiki().getDbProductById(id);
		
		FormProduct formProduct = FormProduct.createForm(product);
		populateDefaultModel(model);
		model.addAttribute("product", product);
		model.addAttribute("productForm", formProduct);
		model.addAttribute("listType", listType);
				
		return "wiki/productform";
	}
	
	@RequestMapping(value = "/wiki/products/{id}/save/{listType}", method = RequestMethod.POST)
	public String saveOrUpdateProduct(@PathVariable("id") int id, @PathVariable("listType") String listType, @ModelAttribute("productForm") @Validated FormProduct product, 
			BindingResult bindingResult, Model model, final RedirectAttributes redirectAttributes) {

		logger.debug("saveOrUpdateProduct():{}", product.getId());
	
		if (bindingResult.hasErrors()) {
			logger.debug("bindingResult:{}", bindingResult.getAllErrors());
			populateDefaultModel(model);
			model.addAttribute("listType", listType);
			return "orders/orderform";
		} else {
			
			product.getMarket(CrmTypes.OZON).setSupplierStock(product.getMarket(CrmTypes.YANDEX_MARKET).isSupplierStock());
			
			wikiService.getWiki().updateProductDescriptionMeta(product);
			wikiService.getWiki().updatePriceAndQuantityProduct(product);

			wikiService.getWiki().updateOzonOfferProduct(product);			
			wikiService.getWiki().updateYmOfferProduct(product);
						
			redirectAttributes.addFlashAttribute("css", "success");			
			String msg = String.format("???????????? ????????????????: %s, %s", 
						product.getId(),						
						product.getViewName());
			redirectAttributes.addFlashAttribute("msg", msg);
			model.addAttribute("listType", listType);
			if (listType.equals("ym") || listType.equals("ozon")) {
				return "redirect:/wiki/products/" + listType; 				
			} else {
				ProductConditions productConditions = new ProductConditions();
				productConditions.setProductId(product.getId());
				List<Product> products = wikiService.getWiki().listProductsByConditions(productConditions);
				productConditions.setName(product.getName());
				populateDefaultModel(model);
				model.addAttribute("products", products);
				model.addAttribute("listType", "products");
				model.addAttribute("productConditions", productConditions);
				return "wiki/listproducts";
			}
			
		}
	}	
	
	@RequestMapping(value = "/wiki/stock-products/suppliers/{supplierId}/product-categories/{productCategoryId}", method = RequestMethod.GET)
	public String showSupplierStockProductsBySupplier(@PathVariable("supplierId") int supplierId, @PathVariable("productCategoryId") int productCategoryId, Model model) {

		logger.debug("showSupplierStockProductsBySupplier(): {}, {}", supplierId, productCategoryId);	
		SupplierTypes inputSupplier = SupplierTypes.getValueById(supplierId);
		ProductCategory inputProductCategory = wikiService.getWiki().getCategoryById(productCategoryId);
		
		SupplierStock stock = wikiService.getWiki().getSupplierStock(inputSupplier, inputProductCategory);	
		List<SupplierStockProduct> supplierStockProducts = stock.getSupplierStockProduct();		
		wikiService.getConfig().saveFormIntegerValue(OrderListController.USER_ID, "stockForm", "supplier.id", supplierId);
		wikiService.getConfig().saveFormIntegerValue(OrderListController.USER_ID, "stockForm", "productCategory.id", productCategoryId);
								
		model.addAttribute("supplierStockProducts", supplierStockProducts);
		model.addAttribute("stock", stock);
		model.addAttribute("inputSupplier", inputSupplier);
		model.addAttribute("inputProductCategory", inputProductCategory);
		
		populateDefaultModel(model);
		
		return "wiki/liststock";
	}
	
	// list page
	@RequestMapping(value = "/wiki/stock-products", method = RequestMethod.GET)
	public String showSupplierStockProducts(Model model) {
		
		int supplierId = wikiService.getConfig().getFormIntegerValueByKey(OrderListController.USER_ID, "stockForm", "supplier.id", SupplierTypes.SITITEK.getId());
		int productCategoryId = wikiService.getConfig().getFormIntegerValueByKey(OrderListController.USER_ID, "stockForm", "productCategory.id", 0);
		
		return "redirect:/wiki/stock-products/suppliers/" + supplierId + "/product-categories/" + productCategoryId;
	}
	
	// add form
	@RequestMapping(value = "/wiki/stock-products/add", method = RequestMethod.GET)
	public String showAddSupplierStockProductForm(Model model) {

			logger.debug("showAddSupplierStockProductForm()");
			FormSupplierStockProduct supplierStockProduct = new FormSupplierStockProduct();
										
			model.addAttribute("supplierStockProductForm", supplierStockProduct);
			populateDefaultModel(model);

			return "wiki/stockform";
	}
	
	// show update form
	@RequestMapping(value = "/wiki/stock-products/{id}/update", method = RequestMethod.GET)
	public String showUpdateSupplierStockProductForm(@PathVariable("id") int id, Model model) {

			logger.debug("showUpdateSupplierStockProductForm() :{}", id);

			SupplierStockProduct supplierStockProduct = wikiService.getWiki().supplierStockProductFindById(id);
			FormSupplierStockProduct formSupplierStockProduct = FormSupplierStockProduct.createForm(supplierStockProduct);		

			model.addAttribute("supplierStockProductForm", formSupplierStockProduct);
			model.addAttribute("supplierStockProduct", formSupplierStockProduct);
				

			populateDefaultModel(model);
			return "wiki/stockform";
	}
	
	// save new or update order
	@RequestMapping(value = "/wiki/stock-products/{id}/save", method = RequestMethod.POST)
	public String saveOrUpdateSupplierStockProduct(@PathVariable("id") int id, @ModelAttribute("supplierStockProductForm") @Validated FormSupplierStockProduct formSupplierStockProduct,
				BindingResult bindingResult, Model model, final RedirectAttributes redirectAttributes) {

		
			logger.debug("showUpdateSupplierStockProductForm() :{}", formSupplierStockProduct.getId());		
			if (bindingResult.hasErrors()) {
				logger.debug("bindingResult :{}", bindingResult.getAllErrors());						
				populateDefaultModel(model);
				return "wiki/stockform";
			} else {						
				wikiService.getWiki().supplierStockProductSaveOrUpdate(formSupplierStockProduct);
				redirectAttributes.addFlashAttribute("css", "success");
				if (formSupplierStockProduct.isNew()) {
					redirectAttributes.addFlashAttribute("msg", "???????????? ??????????????????!");
					wikiService.getConfig().saveFormIntegerValue(1, "stockForm", "supplier.id", formSupplierStockProduct.getSupplier().getId());
				} else {
					/*
					String msg = String.format("???????????? ????????????????: #%s ???? %s ??, %s", 
							formOrder.getNo(), 
							DateTimeUtils.defaultFormatDate(formOrder.getOrderDate()),
							formOrder.getCustomer().getViewShortName());
					redirectAttributes.addFlashAttribute("msg", msg);
					*/
				}
				return "redirect:/wiki/stock-products";
			}
	}
	
	@RequestMapping(value = "/wiki/stock-products/{id}/delete", method = RequestMethod.GET)
	public String deleteSupplierStockProduct(@PathVariable("id") int id, Model model) {
		logger.debug("deleteSupplierStockProduct():{}", id);
		wikiService.getWiki().deleteSupplierStockProduct(id);
	
		return "redirect:/wiki/stock-products";
	}
	
	@RequestMapping(value = "/wiki/products/synchronize", method = RequestMethod.GET)
	public String productsSynchronize(Model model, final RedirectAttributes redirectAttributes) {
		logger.debug("productsSynchronize()");		
		wikiService.getWiki().init();
		
		redirectAttributes.addFlashAttribute("css", "success");
		redirectAttributes.addFlashAttribute("msg", "?????????????????????????? ???????????????? ?? ?????? ???? ?????????????? ??????????????????");
		
		int supplierId = wikiService.getConfig().getFormIntegerValueByKey(OrderListController.USER_ID, "stockForm", "supplier.id", SupplierTypes.SITITEK.getId());
		int productCategoryId = wikiService.getConfig().getFormIntegerValueByKey(OrderListController.USER_ID, "stockForm", "productCategoryId.id", 0);
		return "redirect:/wiki/stock-products/suppliers/" + supplierId + "/product-categories/" + productCategoryId;		
	}
	
	@Override
	protected void populateDefaultModel(Model model) {
		super.populateDefaultModel(model);
		model.addAttribute("productCategories", wikiService.getWiki().getCategories());	
				
		List<Comment> supplierStockExistTypes = new ArrayList<Comment>();
		supplierStockExistTypes.add(new Comment(-1, CommentTypes.UNKNOWN, "-1", "?????? ??????????????"));
		supplierStockExistTypes.add(new Comment(0, CommentTypes.UNKNOWN, "0", "???? ???????????? ????????????"));
		supplierStockExistTypes.add(new Comment(1, CommentTypes.UNKNOWN, "1", "???? ???????????? ????????????????????"));
		
		model.addAttribute("supplierStockExistTypes", supplierStockExistTypes);
		
		List<Comment> yandexSellerExistTypes = new ArrayList<Comment>();
		yandexSellerExistTypes.add(new Comment(-1, CommentTypes.UNKNOWN, "-1", "?????? ??????????????"));
		yandexSellerExistTypes.add(new Comment(0, CommentTypes.UNKNOWN, "0", "?????? ???????????????????? ???? \"????????????-??????????????\""));
		yandexSellerExistTypes.add(new Comment(1, CommentTypes.UNKNOWN, "1", "?????????????????? ???? \"????????????-??????????????\""));
		model.addAttribute("yandexSellerExistTypes", yandexSellerExistTypes);
		
		List<Comment> ozonSellerExistTypes = new ArrayList<Comment>();
		ozonSellerExistTypes.add(new Comment(-1, CommentTypes.UNKNOWN, "-1", "?????? ??????????????"));
		ozonSellerExistTypes.add(new Comment(0, CommentTypes.UNKNOWN, "0", "?????? ???????????????????? ???? \"????????\""));
		ozonSellerExistTypes.add(new Comment(1, CommentTypes.UNKNOWN, "1", "?????????????????? ???? \"????????\""));
		model.addAttribute("ozonSellerExistTypes", ozonSellerExistTypes);
		
	}

}
