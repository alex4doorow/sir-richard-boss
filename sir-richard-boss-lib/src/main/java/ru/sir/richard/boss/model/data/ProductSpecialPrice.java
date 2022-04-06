package ru.sir.richard.boss.model.data;

import java.math.BigDecimal;
import java.util.Date;

import ru.sir.richard.boss.model.utils.Pair;

/**
 * Не помю что, но используется в контейнере продукта для фронта
 * oc_product_special
 * @author alex4doorow
 *
 */
public class ProductSpecialPrice extends AnyId {
	
	private int priority;
	private BigDecimal price;
	private Pair<Date> period;
	
	public ProductSpecialPrice() {
		super();
	}

	public ProductSpecialPrice(int id) {
		super(id);
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Pair<Date> getPeriod() {
		return period;
	}

	public void setPeriod(Pair<Date> period) {
		this.period = period;
	}
	
	
	
	

}
