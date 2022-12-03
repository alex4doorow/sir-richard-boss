package ru.sir.richard.boss.model.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * грузовые места 
 * @author alex4doorow
 *
 */
@Data
@NoArgsConstructor
public class OrderDeliveryShipment extends AnyId {
	
	private String fulfilmentCode; // код грузового места

	public OrderDeliveryShipment(int id) {
		super(id);
		fulfilmentCode = "";
	}
}
