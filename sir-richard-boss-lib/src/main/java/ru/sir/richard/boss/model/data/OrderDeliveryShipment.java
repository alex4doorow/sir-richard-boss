package ru.sir.richard.boss.model.data;

/**
 * грузовые места 
 * @author alex4
 *
 */
public class OrderDeliveryShipment extends AnyId {
	
	private String fulfilmentCode; // код грузового места

	public OrderDeliveryShipment() {
		super();
		fulfilmentCode = "";
	}

	public OrderDeliveryShipment(int id) {
		super(id);
		fulfilmentCode = "";
	}

	public String getFulfilmentCode() {
		return fulfilmentCode;
	}

	public void setFulfilmentCode(String fulfilmentCode) {
		this.fulfilmentCode = fulfilmentCode;
	} 	
	
	
}
