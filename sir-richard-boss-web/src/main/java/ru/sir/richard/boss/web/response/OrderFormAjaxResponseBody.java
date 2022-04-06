package ru.sir.richard.boss.web.response;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.OrderAmounts;
import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.data.crm.DeliveryServiceResult;
import ru.sir.richard.boss.web.data.FormCustomer;
import ru.sir.richard.boss.web.data.FormOrder;
import ru.sir.richard.boss.web.data.FormShortInfoOrder;

public class OrderFormAjaxResponseBody extends AnyAjaxResponseBody {
		
	@JsonView(Views.Public.class)
	ResultOrderFormAjaxResponseBody result;
	
	public OrderFormAjaxResponseBody() {
		super();
		this.result = new ResultOrderFormAjaxResponseBody();
	}	
	
	public ResultOrderFormAjaxResponseBody getResult() {
		return result;
	}

	public void setResult(ResultOrderFormAjaxResponseBody result) {
		this.result = result;
	}	
	
	public void createOrders(List<Order> orders) {
		result.convertFormOrders(orders);		
	}
	
	public class ResultOrderFormAjaxResponseBody {
		
		@JsonView(Views.Public.class)
		FormCustomer formCustomer;
		
		@JsonView(Views.Public.class)
		List<Product> products;
		
		@JsonView(Views.Public.class)
		List<AjaxDeliveryPrice> deliveryPrices;
		
		@JsonView(Views.Public.class)
		List<FormShortInfoOrder> formShortInfoOrders;
		
		@JsonView(Views.Public.class)
		OrderAmounts amounts;
		
		@JsonView(Views.Public.class)
		DeliveryServiceResult deliveryServiceResult;
		
		public ResultOrderFormAjaxResponseBody() {
			this.formShortInfoOrders = new ArrayList<FormShortInfoOrder>();
			this.deliveryServiceResult = new DeliveryServiceResult();
		}
						
		public FormCustomer getFormCustomer() {
			return formCustomer;
		}

		public void setFormCustomer(FormCustomer formCustomer) {
			this.formCustomer = formCustomer;
		}

		public List<Product> getProducts() {
			return products;
		}		

		public void setProducts(List<Product> products) {
			this.products = products;
		}

		public List<AjaxDeliveryPrice> getDeliveryPrices() {
			return deliveryPrices;
		}

		public void setDeliveryPrices(List<AjaxDeliveryPrice> deliveryPrices) {
			this.deliveryPrices = deliveryPrices;
		}

		public List<FormShortInfoOrder> getFormShortInfoOrders() {
			return formShortInfoOrders;
		}

		public void setFormShortInfoOrders(List<FormShortInfoOrder> formShortInfoOrders) {
			this.formShortInfoOrders = formShortInfoOrders;
		}

		public DeliveryServiceResult getDeliveryServiceResult() {
			return deliveryServiceResult;
		}

		public void setDeliveryServiceResult(DeliveryServiceResult deliveryServiceResult) {
			this.deliveryServiceResult = deliveryServiceResult;
		}

		public void convertFormOrders(List<Order> sourceOrders) {
			if (sourceOrders == null || sourceOrders.size() == 0) {
				return;
			}
			for (Order sourceOrder : sourceOrders) {
				FormOrder formOrder = FormOrder.createForm(sourceOrder);
				FormShortInfoOrder formShortInfoOrder = new FormShortInfoOrder(formOrder);
				this.formShortInfoOrders.add(formShortInfoOrder);
			}
		}

		public OrderAmounts getAmounts() {
			return amounts;
		}

		public void setAmounts(OrderAmounts amounts) {
			this.amounts = amounts;
		}	
		
	}

}
