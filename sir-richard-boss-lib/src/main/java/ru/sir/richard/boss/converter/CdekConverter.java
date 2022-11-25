package ru.sir.richard.boss.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import ru.sir.richard.boss.model.data.Address;
import ru.sir.richard.boss.model.data.CarrierInfo;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.dto.CdekCityDto;
import ru.sir.richard.boss.model.dto.CdekOrderDto;
import ru.sir.richard.boss.model.dto.CdekPvzDto;
import ru.sir.richard.boss.model.types.AddressTypes;
import ru.sir.richard.boss.model.types.CarrierStatuses;
import ru.sir.richard.boss.model.types.Countries;
import ru.sir.richard.boss.model.types.CrmTypes;

@Component
public class CdekConverter {
	
	public Address convertCityDtoToAddress(CdekCityDto cdekCityDto) {

		Countries country = Countries.getValueByCode(cdekCityDto.getCountryCode());
		Address result = new Address(country, AddressTypes.MAIN);			
		result.getCarrierInfo().setCityId(cdekCityDto.getCode());
		result.getCarrierInfo().setCityContext(cdekCityDto.getCity());
		result.getCarrierInfo().setRegion(cdekCityDto.getRegion());
		String addressText;
		if (StringUtils.isNoneEmpty(cdekCityDto.getRegion()) && StringUtils.isNoneEmpty(cdekCityDto.getSubRegion())) {
			addressText = cdekCityDto.getCity() + ", " + cdekCityDto.getSubRegion() + ", " + cdekCityDto.getRegion() + ", " + cdekCityDto.getCountry();
		} else if (StringUtils.isNoneEmpty(cdekCityDto.getRegion())) {
			addressText = cdekCityDto.getCity() + ", " + cdekCityDto.getRegion() + ", " + cdekCityDto.getCountry();
		} else {
			addressText = cdekCityDto.getCity() + ", " + cdekCityDto.getCountry();
		}		
    	result.setAddress(addressText);
    	result.getCarrierInfo().setCoordX(cdekCityDto.getLatitude());
    	result.getCarrierInfo().setCoordY(cdekCityDto.getLongitude());
    	result.getCarrierInfo().setPostalCodes(cdekCityDto.getPostalCodes());
		return result;
	}
	
	public List<Address> convertCityDtosToAddresses(CdekCityDto[] cdekCityDtos) {
		
		List<Address> addresses = new ArrayList<Address>();
		Arrays.asList(cdekCityDtos).stream()
			.forEach(cdekCityDto -> {
				Address address = convertCityDtoToAddress(cdekCityDto);
				address.setId(addresses.size());
				addresses.add(address);
				});
		addresses.sort((a1, a2) -> Integer.compare(a2.getCarrierInfo().getPostalSize(), a1.getCarrierInfo().getPostalSize()));
		return addresses;
	}
	
	public CarrierInfo convertPvzDtoToCarrierInfo(CdekPvzDto cdekPvzDto) {				
		CarrierInfo cdekInfo = new CarrierInfo();
		cdekInfo.setPvzId(cdekPvzDto.getId());
		cdekInfo.setPvz(cdekPvzDto.getCode());
		cdekInfo.setRegion(cdekPvzDto.getLocation().getRegion());
		cdekInfo.setCityContext(cdekPvzDto.getLocation().getCity());
		cdekInfo.setShortAddress(cdekPvzDto.getLocation().getAddressShort());
		cdekInfo.setFullAddress(cdekPvzDto.getLocation().getAddressFull());		
		cdekInfo.setAddressComment(cdekPvzDto.getAddressComment());					
		cdekInfo.setPhone(cdekPvzDto.getSinglePhone());
		cdekInfo.setEmail(cdekPvzDto.getEmail());
		cdekInfo.setNote(cdekPvzDto.getName());
		cdekInfo.setPvzType(cdekPvzDto.getType());
		cdekInfo.setHaveCash(String.valueOf(cdekPvzDto.getHaveCash()));		
		cdekInfo.setAllowedCod(String.valueOf(cdekPvzDto.getAllowedCode()));
		cdekInfo.setNearestStation(cdekPvzDto.getNearestStation());
		cdekInfo.setMetroStation(cdekPvzDto.getNearestStation());		
		return cdekInfo;
	}
	
	public List<CarrierInfo> convertPvzDtosToCarrierInfo(CdekPvzDto[] cdekPvzDtos) {
		List<CarrierInfo> pvzs = new ArrayList<CarrierInfo>();
		Arrays.asList(cdekPvzDtos).stream()
			.forEach(cdekPvzDto -> {
				CarrierInfo pvz = convertPvzDtoToCarrierInfo(cdekPvzDto);				
				pvzs.add(pvz);
				});
		pvzs.sort((p1, p2) -> Integer.compare(p1.getPvzId(), p2.getPvzId()));		
		return pvzs;
	}
	
	public List<Address> convertPvzDtosToAddresses(CdekPvzDto[] cdekPvzDtos) {		
		List<CarrierInfo> carrierInfos = convertPvzDtosToCarrierInfo(cdekPvzDtos);
		List<Address> addresses = new ArrayList<Address>();
		carrierInfos.forEach(carrierInfo -> {
				Address address = new Address();
				address.setCarrierInfo(carrierInfo);
				addresses.add(address);
				});
		addresses.sort((a1, a2) -> Integer.compare(a1.getCarrierInfo().getPvzId(), a2.getCarrierInfo().getPvzId()));		
		return addresses;
	}
	
	public Order convertCdekOrderDtoToOrder(CdekOrderDto cdekOrderDto) {
		Order order = new Order();
		order.setNo(cdekOrderDto.getEntity().getNo());
		order.getDelivery().setTrackCode(cdekOrderDto.getEntity().getCdekNumber());
		
		if (cdekOrderDto.getEntity().getStatuses() != null && cdekOrderDto.getEntity().getStatuses().size() > 0) {
			CarrierStatuses carrierStatus = CarrierStatuses.getValueByCode(cdekOrderDto.getEntity().getStatuses().get(0).getCode(), CrmTypes.CDEK);
			order.getDelivery().setCarrierStatus(carrierStatus);					
		} 		
		return order;		
	}
}
