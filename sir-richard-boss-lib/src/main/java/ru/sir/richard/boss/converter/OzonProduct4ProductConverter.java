package ru.sir.richard.boss.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import ru.sir.richard.boss.model.data.Product;
import ru.sir.richard.boss.model.dto.OzonRequestPriceDto;
import ru.sir.richard.boss.model.dto.OzonRequestPricesDto;
import ru.sir.richard.boss.model.dto.OzonRequestStockDto;
import ru.sir.richard.boss.model.dto.OzonRequestStocksDto;
import ru.sir.richard.boss.model.types.CrmTypes;
import ru.sir.richard.boss.model.utils.NumberUtils;

@Slf4j
@Component
public class OzonProduct4ProductConverter {
	
	@Autowired
	private Environment environment;
	
	private ModelMapper modelMapper;
	
    public OzonProduct4ProductConverter() {
		super();
		modelMapper = new ModelMapper();		
	}

    public OzonRequestStockDto convertToStockDto(Product product, Long warehouseId) {
    	OzonRequestStockDto ozonStockDto = modelMapper.map(product, OzonRequestStockDto.class);
    	ozonStockDto.setOfferId(product.getSku());
    	ozonStockDto.setProductId(Long.valueOf(product.getMarket(CrmTypes.OZON).getMarketSku()));
    	ozonStockDto.setWarehouseId(warehouseId);
    	ozonStockDto.setStock(product.getQuantity());
    	return ozonStockDto;
    }
    
    public List<OzonRequestStockDto> convertToStockDtos(boolean isOzonEnabled, List<Product> products) {

    	List<OzonRequestStockDto> result = new ArrayList<>();
    	products.stream()
    		.filter((product) -> StringUtils.isNotEmpty(product.getMarket(CrmTypes.OZON).getMarketSku()))
    		.filter((product) -> product.getMarket(CrmTypes.OZON).isMarketSeller())
	    	.forEach(product -> {
				int cdekQuantity = 0;
				int ozonPickupQuantity = 0;
				int ozonExpressQuantity = 0;

				if (isOzonEnabled) {
					ozonPickupQuantity = product.getQuantity();
				}
				ozonPickupQuantity = ozonPickupQuantity < 0 ? 0 : ozonPickupQuantity;

				OzonRequestStockDto cdekOzonStockDto = convertToStockDto(product, Long.valueOf(environment.getProperty("ozon.market.cdek.warehouse")));
				cdekOzonStockDto.setStock(cdekQuantity);
	    		result.add(cdekOzonStockDto);
	    		
	    		OzonRequestStockDto pickupOzonStockDto = convertToStockDto(product, Long.valueOf(environment.getProperty("ozon.market.ozon.warehouse.pickup")));
	    		pickupOzonStockDto.setStock(ozonPickupQuantity);
	    		result.add(pickupOzonStockDto);
	    		
	    		OzonRequestStockDto expressOzonStockDto = convertToStockDto(product, Long.valueOf(environment.getProperty("ozon.market.ozon.warehouse.express")));
	    		expressOzonStockDto.setStock(ozonExpressQuantity);
	    		result.add(expressOzonStockDto);	   		    		
	    	});    	
    	return result;
    }
    
    public OzonRequestStocksDto convertToStocksDto(boolean isOzonEnabled, List<Product> products) {

    	List<OzonRequestStockDto> ozonStockDtos = convertToStockDtos(isOzonEnabled, products);
    	OzonRequestStocksDto ozonStocksDto = new OzonRequestStocksDto();
    	ozonStocksDto.setOzonRequestStockDtos(ozonStockDtos);
    	return ozonStocksDto;    	
    }
    
    public OzonRequestPriceDto convertToPriceDto(Product product) {
    	OzonRequestPriceDto ozonPriceDto = modelMapper.map(product, OzonRequestPriceDto.class);
    	ozonPriceDto.setOfferId(product.getSku());
		try {
			ozonPriceDto.setProductId(Long.valueOf(product.getMarket(CrmTypes.OZON).getMarketSku()));
		} catch (Exception e) {
			ozonPriceDto.setProductId(Long.valueOf(product.getId()));
			log.error("id={}, sku={}", product.getId(), product.getMarket(CrmTypes.OZON).getMarketSku(), e);
		}
    	
    	String stringOldPrice = "0";			
		String stringPrice;
    	if (product.getMarket(CrmTypes.OZON).getSpecialPrice() == null || product.getMarket(CrmTypes.OZON).getSpecialPrice().equals(BigDecimal.ZERO)) {
			if (product.getPriceWithoutDiscount().compareTo(product.getPrice()) > 0) {
				stringPrice = toOZONPrice(product.getPriceWithDiscount());
				stringOldPrice = toOZONPrice(product.getPriceWithoutDiscount());
			} else {
				stringPrice = toOZONPrice(product.getPrice());
			}
		} else {
			stringPrice = toOZONPrice(product.getMarket(CrmTypes.OZON).getSpecialPrice());
		}
    	ozonPriceDto.setPrice(stringPrice);
    	ozonPriceDto.setOldPrice(stringOldPrice);
    	
    	return ozonPriceDto;
    }
    
    public List<OzonRequestPriceDto> convertToPriceDtos(List<Product> products) {
    	List<OzonRequestPriceDto> result = new ArrayList<OzonRequestPriceDto>();
    	products.stream()
    		.filter((product) -> StringUtils.isNotEmpty(product.getMarket(CrmTypes.OZON).getMarketSku()))
    		.filter((product) -> product.getMarket(CrmTypes.OZON).isMarketSeller())
	    	.forEach(product -> {				
	    		OzonRequestPriceDto cdekRequestPriceDto = convertToPriceDto(product);				
	    		result.add(cdekRequestPriceDto);	    			   		    		
	    	});    	
    	return result;    	
    }
    
    public OzonRequestPricesDto convertToPricesDto(List<Product> products) {    	

    	List<OzonRequestPriceDto> ozonStockDtos = convertToPriceDtos(products);
    	OzonRequestPricesDto ozonRequestPricesDto = new OzonRequestPricesDto();
    	ozonRequestPricesDto.setOzonRequestPriceDtos(ozonStockDtos);
    	return ozonRequestPricesDto;    	
    }

	private String toOZONPrice(BigDecimal price) {
		return NumberUtils.localeFormatNumber(price, Locale.US, '.', "#########.00");
	}
}
