package ru.sir.richard.boss.converter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ru.sir.richard.boss.api.cdek.CdekUtils;
import ru.sir.richard.boss.model.data.Address;
import ru.sir.richard.boss.model.data.CarrierInfo;
import ru.sir.richard.boss.model.data.ForeignerCompanyCustomer;
import ru.sir.richard.boss.model.data.Order;
import ru.sir.richard.boss.model.data.crm.DeliveryServiceResult;
import ru.sir.richard.boss.model.dto.*;
import ru.sir.richard.boss.model.types.*;
import ru.sir.richard.boss.utils.MathUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

@Component
@Slf4j
public class CdekConverter {

    @Autowired
    private Environment environment;

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

        List<Address> addresses = new ArrayList<>();
        Arrays.asList(cdekCityDtos).forEach(cdekCityDto -> {
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
        List<CarrierInfo> pvzs = new ArrayList<>();
        Arrays.asList(cdekPvzDtos).forEach(cdekPvzDto -> {
                    CarrierInfo pvz = convertPvzDtoToCarrierInfo(cdekPvzDto);
                    pvzs.add(pvz);
                });
        pvzs.sort((p1, p2) -> Integer.compare(p1.getPvzId(), p2.getPvzId()));
        return pvzs;
    }

    public List<Address> convertPvzDtosToAddresses(CdekPvzDto[] cdekPvzDtos) {
        List<CarrierInfo> carrierInfos = convertPvzDtosToCarrierInfo(cdekPvzDtos);
        List<Address> addresses = new ArrayList<>();
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

    public CdekOrderDto convertOrderToCdekOrderDto(Order order, int weightOfG) {
        CdekOrderDto cdekOrder = new CdekOrderDto();

        CdekEntityOrderDto entity = new CdekEntityOrderDto();
        entity.setNumber(String.valueOf(order.getNo()));
        entity.setCdekNumber(String.valueOf(order.getDelivery().getTrackCode()));
        entity.setType(CdekUtils.ORDER_TYPE_ONLINE_STORE);
        entity.setTariffCode(CdekUtils.getCdekTariffId(order.getDelivery().getDeliveryType()));
        entity.setComment(order.getDelivery().getAnnotation());
        entity.setShipmentPoint(environment.getProperty("cdek.shipment.point"));
        if (order.getDelivery().getDeliveryType().isCdekPvz()) {
            String pvz = order.getDelivery().getAddress().getCarrierInfo().getPvz();
            entity.setDeliveryPoint(pvz);
        } else {
            CdekEntityOrderLocationDto toLocation = new CdekEntityOrderLocationDto();
            toLocation.setCity(order.getDelivery().getAddress().getCity());
            toLocation.setCode(order.getDelivery().getAddress().getCarrierInfo().getCityId());
            toLocation.setAddress(order.getDelivery().getAddress().getStreetAddress());
            entity.setToLocation(toLocation);
        }
        CdekEntityAmountDto deliveryRecipientCost = new CdekEntityAmountDto();
        BigDecimal deliveryAmount;
        if (order.isPrepayment()) {
            deliveryAmount = BigDecimal.ZERO;
        } else {
            if (order.getAdvertType() == OrderAdvertTypes.OZON) {
                deliveryAmount = BigDecimal.ZERO;
            } else if (order.getCustomer().isCompany()) {
                deliveryAmount = BigDecimal.ZERO;
            } else if (order.getDelivery().getDeliveryType() == DeliveryTypes.PICKUP) {
                deliveryAmount = order.getAmounts().getTotal();
                entity.setDeliveryPoint(environment.getProperty("cdek.shipment.point"));
            } else {
                deliveryAmount = order.getDelivery().getCustomerPrice() != null ? order.getDelivery().getCustomerPrice() : BigDecimal.ZERO;
            }
        }
        deliveryRecipientCost.setValue(deliveryAmount);
        entity.setDeliveryRecipientCost(deliveryRecipientCost);

        CdekEntityRecipientDto recipient = new CdekEntityRecipientDto();
        recipient.setEmail(order.getCustomer().getEmail());
        recipient.setPhones(Collections.singletonList(new CdekPhoneDto(order.getCustomer().getViewPhoneNumber())));
        if (order.getCustomer().isPerson()) {
            if (order.getDelivery().getRecipient() == null) {
                recipient.setName(order.getCustomer().getViewLongName());
            } else {
                recipient.setName(order.getDelivery().getRecipient().getViewLongName());
                recipient.setCompany(order.getCustomer().getViewLongName());
                recipient.setPhones(Collections.singletonList(new CdekPhoneDto(order.getDelivery().getRecipient().getPhoneNumber().trim())));
            }
        } else {
            ForeignerCompanyCustomer company = (ForeignerCompanyCustomer) order.getCustomer();
            recipient.setCompany(company.getShortName());
            recipient.setName(company.getMainContact().getViewLongName());
        }
        entity.setRecipient(recipient);

        List<CdekEntityOrderPackageDto> orderPackages = new ArrayList<>();
        CdekEntityOrderPackageDto orderPackage = new CdekEntityOrderPackageDto();
        orderPackage.setNumber("1");
        orderPackage.setComment(order.getProductCategory().getName());
        orderPackage.setWeight(weightOfG);
        List<CdekEntityPackageItemDto> items = new ArrayList<>();
        order.getItems().forEach(orderItem -> {
            CdekEntityPackageItemDto item = new CdekEntityPackageItemDto();
            item.setSku(orderItem.getProduct().getViewSKU());

            String productName;
            if (StringUtils.isNoneEmpty((orderItem.getProduct().getDeliveryName()))) {
                productName = orderItem.getProduct().getDeliveryName();
            } else {
                productName = orderItem.getProduct().getName();
            }
            item.setName(productName);

            BigDecimal productPrice;
            BigDecimal productPay;
            if (order.isPrepayment()) {
                productPrice = orderItem.getPrice();
                productPay = BigDecimal.ZERO;
            } else {
                if (order.getAdvertType() == OrderAdvertTypes.OZON) {
                    productPrice = orderItem.getPrice();
                    productPay = BigDecimal.ZERO;
                } else if (order.getCustomer().isCompany()) {
                    productPrice = orderItem.getPrice();
                    productPay = BigDecimal.ZERO;
                } else if (order.getDelivery().getDeliveryType() == DeliveryTypes.PICKUP) {
                    productPrice = BigDecimal.ONE;
                    productPay = BigDecimal.ZERO;
                } else {
                    productPrice = orderItem.calcPriceWithDiscount();
                    productPay = orderItem.calcPriceWithDiscount();
                }
            }
            item.setCost(productPrice);
            CdekEntityAmountDto itemPayment = new CdekEntityAmountDto();
            itemPayment.setValue(productPay);
            item.setPayment(itemPayment);
            item.setAmount(orderItem.getQuantity());

            item.setWeight(50);
            items.add(item);

            if (items.size() == 1) {
                orderPackage.setLength(orderItem.getProduct().getStore().getLength());
                orderPackage.setHeight(orderItem.getProduct().getStore().getHeight());
                orderPackage.setWidth(orderItem.getProduct().getStore().getWidth());
            }
        });
        orderPackage.setItems(items);
        orderPackages.add(orderPackage);
        entity.setPackages(orderPackages);
        //entity.setServices(Collections.singletonList(new CdekEntityServiceOrderDto(CdekUtils.ORDER_SERVICE_INSURANCE)));
        cdekOrder.setEntity(entity);
        return cdekOrder;
    }

    public CdekEntityOrderDto convertTariffDataToCdekOrderDto(int weightOfG,
                                                              int tariffId,
                                                              int receiverCityId) {
        CdekEntityOrderDto data = new CdekEntityOrderDto();
        data.setType(CdekUtils.ORDER_TYPE_ONLINE_STORE);
        data.setTariffCode(tariffId);

        CdekEntityOrderLocationDto fromLocation = new CdekEntityOrderLocationDto();
        fromLocation.setCode(Integer.parseInt(environment.getProperty("cdek.from.location")));
        data.setFromLocation(fromLocation);

        CdekEntityOrderLocationDto toLocation = new CdekEntityOrderLocationDto();
        toLocation.setCode(receiverCityId);
        data.setToLocation(toLocation);

        CdekEntityOrderPackageDto orderPackage = new CdekEntityOrderPackageDto();
        orderPackage.setWeight(weightOfG);
        orderPackage.setLength(10);
        orderPackage.setHeight(10);
        orderPackage.setWidth(10);

        List<CdekEntityOrderPackageDto> orderPackages = new ArrayList<>();
        orderPackages.add(orderPackage);
        data.setPackages(orderPackages);
        return data;
    }

    public DeliveryServiceResult convertTariffDataToDeliveryServiceResult(CdekResponseTariffDto responseData,
                                                                          int weightOfG,
                                                                          BigDecimal totalAmount,
                                                                          int tariffId,
                                                                          int receiverCityId,
                                                                          boolean isPostpay,
                                                                          boolean isPaySeller) {

        if (responseData == null || responseData.getTotalSum() == null) {
            return DeliveryServiceResult.createEmpty();
        }
        BigDecimal deliveryPrice;
        BigDecimal deliveryFullPrice;

        BigDecimal deliverySellerSummary = BigDecimal.ZERO;
        BigDecimal deliveryCustomerSummary;

        BigDecimal deliveryInsurance;
        BigDecimal deliveryPostpayFee = BigDecimal.ZERO;
        BigDecimal postpayAmount = BigDecimal.ZERO;
        Integer deliveryPeriodMin;
        Integer deliveryPeriodMax;
        String errorText = "";

        deliveryPeriodMin = responseData.getPeriodMin();
        deliveryPeriodMax = responseData.getPeriodMax();
        deliveryPrice = responseData.getDeliverySum();
        deliveryInsurance = totalAmount.multiply(new BigDecimal("0.75")).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        deliveryCustomerSummary = responseData.getTotalSum().setScale(2, RoundingMode.HALF_UP);
        //deliveryCustomerSummary = deliveryCustomerSummary.add(totalAmount.multiply(BigDecimal.ONE.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))).round(new MathContext(0, RoundingMode.HALF_UP)).setScale(0, RoundingMode.CEILING);
        deliveryFullPrice = deliveryCustomerSummary;

        // страховка = (0.75 * totalAmount) / 100
        // комиссия за наложку = (totalAmount + стоимость доставки) * 0,03
        // доставка = price + страховка + комиссия за наложку

        if (isPostpay) {
            BigDecimal oPostpayAmount = BigDecimal.ZERO;
            for (int ii = 0; ii < 10; ii++) {
                BigDecimal nDeliverySellerSummary = deliveryPrice.add(deliveryInsurance).add(deliveryPostpayFee);
                BigDecimal nPostpayAmount = totalAmount.add(nDeliverySellerSummary);
                BigDecimal delta = nPostpayAmount.subtract(oPostpayAmount);
                if (delta.abs().compareTo(BigDecimal.ONE) < 0) {
                    deliverySellerSummary = nDeliverySellerSummary;
                    postpayAmount = nPostpayAmount;
                    break;
                } else {
                    deliveryPostpayFee = (totalAmount.add(nDeliverySellerSummary)).multiply(new BigDecimal("3")).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
                    oPostpayAmount = nPostpayAmount;
                }
            }
            deliverySellerSummary = deliverySellerSummary.round(new MathContext(2, RoundingMode.HALF_UP));
            deliveryPostpayFee = deliveryPostpayFee.round(new MathContext(2, RoundingMode.HALF_UP));
            deliveryCustomerSummary = deliverySellerSummary.subtract(deliveryPostpayFee);
            if (isPaySeller) {
                postpayAmount = totalAmount.subtract(deliveryPrice).subtract(deliveryInsurance).subtract(deliveryPostpayFee);
                deliveryCustomerSummary = BigDecimal.ZERO;
                deliveryFullPrice = deliverySellerSummary;
            } else {
                postpayAmount = totalAmount.add(deliveryCustomerSummary).subtract(deliveryPrice).subtract(deliveryInsurance).subtract(deliveryPostpayFee);
                deliveryFullPrice = deliveryCustomerSummary;
            }
        } else {
            deliveryCustomerSummary = deliveryPrice.add(deliveryInsurance);
            deliverySellerSummary = deliveryPrice.add(deliveryInsurance).round(new MathContext(0, RoundingMode.HALF_UP)).setScale(0, RoundingMode.CEILING);
            deliveryFullPrice = deliveryCustomerSummary;
        }

        DeliveryServiceResult result = new DeliveryServiceResult();
		result.setDeliveryAmount(deliveryCustomerSummary);
		result.setDeliveryPrice(deliveryPrice);
		result.setDeliveryInsurance(deliveryInsurance);
		result.setDeliveryPostpayFee(deliveryPostpayFee);
		result.setDeliveryFullPrice(deliveryFullPrice);
		result.setDeliverySellerSummary(deliverySellerSummary);
		result.setDeliveryCustomerSummary(deliveryCustomerSummary);
		result.setDeliveryPeriodMin(deliveryPeriodMin);
		result.setDeliveryPeriodMax(deliveryPeriodMax);
		result.setPostpayAmount(postpayAmount);
		if (deliveryPeriodMin.compareTo(deliveryPeriodMax) == 0) {
			result.setTermText(deliveryPeriodMax + " дн.");
		} else {
			result.setTermText(deliveryPeriodMin + "-" + deliveryPeriodMax + " дн.");
		}
		result.setParcelType("tariffId: " + tariffId);
		result.setTo("receiverCityId: " + receiverCityId);
		result.setWeightText(MathUtils.weightG2Kg(weightOfG) + " кг.");
		result.setErrorText(errorText);
		return result;
    }
}
