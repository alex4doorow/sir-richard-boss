package ru.sir.richard.boss.web.response;

import com.fasterxml.jackson.annotation.JsonView;

import ru.sir.richard.boss.model.data.ProductStore;

public class ProductFormAjaxResponceBody extends AnyAjaxResponseBody {
	
	@JsonView(Views.Public.class)
	ResultProductFormAjaxResponseBody result;
	
	public ProductFormAjaxResponceBody() {
		super();
		this.result = new ResultProductFormAjaxResponseBody();
	}
		
	public ResultProductFormAjaxResponseBody getResult() {
		return result;
	}
	
	public void setResult(ResultProductFormAjaxResponseBody result) {
		this.result = result;
	}

	public class ResultProductFormAjaxResponseBody {
		
		@JsonView(Views.Public.class)
		ProductStore store;
		
		@JsonView(Views.Public.class)
		String priceText;

		public ProductStore getStore() {
			return store;
		}

		public void setStore(ProductStore store) {
			this.store = store;
		}

		public String getPriceText() {
			return priceText;
		}

		public void setPriceText(String priceText) {
			this.priceText = priceText;
		}
	}

}
