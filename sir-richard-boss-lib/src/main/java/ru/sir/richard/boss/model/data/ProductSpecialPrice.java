package ru.sir.richard.boss.model.data;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sir.richard.boss.utils.Pair;

/**
 * Не помю что, но используется в контейнере продукта для фронта
 * oc_product_special
 * @author alex4doorow
 *
 */
@Data
@NoArgsConstructor
public class ProductSpecialPrice extends AnyId {
	
	private int priority;
	private BigDecimal price;
	private Pair<Date> period;

	public ProductSpecialPrice(int id) {
		super(id);
	}
}
