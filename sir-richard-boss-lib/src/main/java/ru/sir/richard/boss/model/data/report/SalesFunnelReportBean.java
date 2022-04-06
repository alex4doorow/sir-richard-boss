package ru.sir.richard.boss.model.data.report;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

import ru.sir.richard.boss.model.data.ProductCategory;

public class SalesFunnelReportBean extends AnyReportBean {
	
	// ВНЕШНИЕ ДАННЫЕ	
	private BigDecimal advertBudget;
	
	// visits	
	private int visits;
	private int uniqueVisitors;
	private int newVisitors;
		
	// ЛИДЫ	
	// source
	private int siteSourceLeads;
	private int emailSourceLeads;
	private int callSourceLeads;
	private int chatSourceLeads;
	private int othersSourceLeads;
	
	// channels
	private int paidChannelLeads;
	private int organicChannelLeads;
	private int socialNetworkChannelLeads;
	private int directChannelLeads;
	private int yandexMarketChannelLeads;
	private int ozonMarketChannelLeads;	
	private int othersChannelLeads;	
	
	// type
	private int orderTypeLeads;
	private int billTypeLeads;
	private int kpTypeLeads;
	private int consultationTypeLeads;
	private int refundTypeLeads;
	private int othersTypeLeads;	
	// type 2
	private int newLeads;
	private int repeatLeads;
	// customers
	private int personLeads;
	private int companyLeads;	
			
	private Set<ProductCategory> categories;
	
	/*
	conversion rate
	конверсия = число лидов / число уников
			число лидов / число посещений
	стоимость лида = рекламный бюджет / число лидов	
	*/
	
	// ORDERS	
	private int totalOrders;
	private int personTotalOrders = 0;
	private int companyTotalOrders = 0;
		
	private int myselfDeliveryOrders;
	private int courierServiceDeliveryOrders;
	private int postServiceDeliveryOrders;
	
	private int cdekDeliveryOrders = 0;
	private int dellinDeliveryOrders = 0;
	
	// доход
	private BigDecimal myselfDeliveryOrdersAmount;
	private BigDecimal courierServiceDeliveryOrdersAmount;
	private BigDecimal postServiceDeliveryOrdersAmount;
	
	private BigDecimal personOrdersAmount;
	private BigDecimal companyOrdersAmount;
					
	// транспортные расходы
	private BigDecimal courierServiceDeliveryOrdersCost;
	private BigDecimal postServiceDeliveryOrdersCost;	
	
	// MONEY	
	private BigDecimal totalAmount;
	private BigDecimal totalSupplierAmount;
	private BigDecimal totalMarginAmount;
	
	private BigDecimal personMarginAmount;
	private BigDecimal companyMarginAmount;
	
	// ОСТАТКИ
	private BigDecimal stockAmount;
	private BigDecimal postpayAmount;
		
	
	// РАСЧЕТНЫЕ ПОКАЗАТЕЛИ
	/*
	суммарный доход
	средний чек	= суммарный доход / число заказов
	
	суммарная закупка
		
	суммарная прибыль = доход - закупку
	
	средняя прибыль = суммарная прибыль / число заказов
	
	валовая прибыль = (доход - закупка)
	маркетинговая ROI = (валовая прибыль - реклама)/реклама * 100%
	
	справедливая ставка = (прибыль/2) / число кликов
	*/
	
	public SalesFunnelReportBean() {
		super();
	}	
	
	public BigDecimal getAdvertBudget() {
		return advertBudget;
	}

	public void setAdvertBudget(BigDecimal advertBudget) {
		this.advertBudget = advertBudget;
	}

	public int getVisits() {
		return visits;
	}

	public void setVisits(int visits) {
		this.visits = visits;
	}
	
	public int getUniqueVisitors() {
		return uniqueVisitors;
	}

	public void setUniqueVisitors(int uniqueVisitors) {
		this.uniqueVisitors = uniqueVisitors;
	}

	public int getNewVisitors() {
		return newVisitors;
	}

	public void setNewVisitors(int newVisitors) {
		this.newVisitors = newVisitors;
	}

	public int getSiteSourceLeads() {
		return siteSourceLeads;
	}

	public void setSiteSourceLeads(int siteSourceLeads) {
		this.siteSourceLeads = siteSourceLeads;
	}

	public int getEmailSourceLeads() {
		return emailSourceLeads;
	}

	public void setEmailSourceLeads(int emailSourceLeads) {
		this.emailSourceLeads = emailSourceLeads;
	}

	public int getCallSourceLeads() {
		return callSourceLeads;
	}

	public void setCallSourceLeads(int callSourceLeads) {
		this.callSourceLeads = callSourceLeads;
	}

	public int getChatSourceLeads() {
		return chatSourceLeads;
	}

	public void setChatSourceLeads(int chatSourceLeads) {
		this.chatSourceLeads = chatSourceLeads;
	}

	public int getOthersSourceLeads() {
		return othersSourceLeads;
	}

	public void setOthersSourceLeads(int othersSourceLeads) {
		this.othersSourceLeads = othersSourceLeads;
	}

	public int getPaidChannelLeads() {
		return paidChannelLeads;
	}

	public void setPaidChannelLeads(int paidChannelLeads) {
		this.paidChannelLeads = paidChannelLeads;
	}

	public int getYandexMarketChannelLeads() {
		return yandexMarketChannelLeads;
	}

	public void setYandexMarketChannelLeads(int yandexMarketLeads) {
		this.yandexMarketChannelLeads = yandexMarketLeads;
	}

	public int getOzonMarketChannelLeads() {
		return ozonMarketChannelLeads;
	}

	public void setOzonMarketChannelLeads(int ozonMarketChannelLeads) {
		this.ozonMarketChannelLeads = ozonMarketChannelLeads;
	}

	public int getOrganicChannelLeads() {
		return organicChannelLeads;
	}

	public void setOrganicChannelLeads(int organicChannelLeads) {
		this.organicChannelLeads = organicChannelLeads;
	}

	public int getSocialNetworkChannelLeads() {
		return socialNetworkChannelLeads;
	}

	public void setSocialNetworkChannelLeads(int socialNetworkChannelLeads) {
		this.socialNetworkChannelLeads = socialNetworkChannelLeads;
	}

	public int getDirectChannelLeads() {
		return directChannelLeads;
	}

	public void setDirectChannelLeads(int directChannelLeads) {
		this.directChannelLeads = directChannelLeads;
	}

	public int getOthersChannelLeads() {
		return othersChannelLeads;
	}

	public void setOthersChannelLeads(int othersChannelLeads) {
		this.othersChannelLeads = othersChannelLeads;
	}

	public int getOrderTypeLeads() {
		return orderTypeLeads;
	}
	
	public void setOrderTypeLeads(int orderTypeLeads) {
		this.orderTypeLeads = orderTypeLeads;
	}

	public int getBillTypeLeads() {
		return billTypeLeads;
	}

	public void setBillTypeLeads(int billTypeLeads) {
		this.billTypeLeads = billTypeLeads;
	}

	public int getKpTypeLeads() {
		return kpTypeLeads;
	}

	public void setKpTypeLeads(int kpTypeLeads) {
		this.kpTypeLeads = kpTypeLeads;
	}

	public int getConsultationTypeLeads() {
		return consultationTypeLeads;
	}

	public void setConsultationTypeLeads(int consultationTypeLeads) {
		this.consultationTypeLeads = consultationTypeLeads;
	}	

	public int getRefundTypeLeads() {
		return refundTypeLeads;
	}

	public void setRefundTypeLeads(int refundTypeLeads) {
		this.refundTypeLeads = refundTypeLeads;
	}

	public int getOthersTypeLeads() {
		return othersTypeLeads;
	}

	public void setOthersTypeLeads(int othersTypeLeads) {
		this.othersTypeLeads = othersTypeLeads;
	}

	public int getNewLeads() {
		return newLeads;
	}

	public void setNewLeads(int newLeads) {
		this.newLeads = newLeads;
	}

	public int getRepeatLeads() {
		return repeatLeads;
	}

	public void setRepeatLeads(int repeatLeads) {
		this.repeatLeads = repeatLeads;
	}

	public int getPersonLeads() {
		return personLeads;
	}

	public void setPersonLeads(int personLeads) {
		this.personLeads = personLeads;
	}	

	public int getCompanyLeads() {
		return companyLeads;
	}

	public void setCompanyLeads(int companyLeads) {
		this.companyLeads = companyLeads;
	}

	public Set<ProductCategory> getCategories() {
		return categories;
	}

	public void setCategories(Set<ProductCategory> categories) {
		this.categories = categories;
	}

	public int getTotalOrders() {
		return totalOrders;
	}
	
	public void setTotalOrders(int totalOrders) {
		this.totalOrders = totalOrders;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getTotalSupplierAmount() {
		return totalSupplierAmount;
	}

	public void setTotalSupplierAmount(BigDecimal totalSupplierAmount) {
		this.totalSupplierAmount = totalSupplierAmount;
	}	

	public BigDecimal getTotalMarginAmount() {
		return totalMarginAmount;
	}

	public void setTotalMarginAmount(BigDecimal totalMarginAmount) {
		this.totalMarginAmount = totalMarginAmount;
	}

	public int getMyselfDeliveryOrders() {
		return myselfDeliveryOrders;
	}

	public void setMyselfDeliveryOrders(int myselfDeliveryOrders) {
		this.myselfDeliveryOrders = myselfDeliveryOrders;
	}
	
	public int getCdekDeliveryOrders() {
		return cdekDeliveryOrders;
	}

	public void setCdekDeliveryOrders(int cdekDeliveryOrders) {
		this.cdekDeliveryOrders = cdekDeliveryOrders;
	}

	public int getDellinDeliveryOrders() {
		return dellinDeliveryOrders;
	}

	public void setDellinDeliveryOrders(int dellinDeliveryOrders) {
		this.dellinDeliveryOrders = dellinDeliveryOrders;
	}

	public int getCourierServiceDeliveryOrders() {
		return courierServiceDeliveryOrders;
	}

	public void setCourierServiceDeliveryOrders(int courierServiceDeliveryOrders) {
		this.courierServiceDeliveryOrders = courierServiceDeliveryOrders;
	}

	public int getPostServiceDeliveryOrders() {
		return postServiceDeliveryOrders;
	}

	public void setPostServiceDeliveryOrders(int postServiceDeliveryOrders) {
		this.postServiceDeliveryOrders = postServiceDeliveryOrders;
	}

	public BigDecimal getMyselfDeliveryOrdersAmount() {
		return myselfDeliveryOrdersAmount;
	}

	public void setMyselfDeliveryOrdersAmount(BigDecimal myselfDeliveryOrdersAmount) {
		this.myselfDeliveryOrdersAmount = myselfDeliveryOrdersAmount;
	}

	public BigDecimal getCourierServiceDeliveryOrdersAmount() {
		return courierServiceDeliveryOrdersAmount;
	}

	public void setCourierServiceDeliveryOrdersAmount(BigDecimal courierServiceDeliveryOrdersAmount) {
		this.courierServiceDeliveryOrdersAmount = courierServiceDeliveryOrdersAmount;
	}

	public BigDecimal getPostServiceDeliveryOrdersAmount() {
		return postServiceDeliveryOrdersAmount;
	}

	public void setPostServiceDeliveryOrdersAmount(BigDecimal postServiceDeliveryOrdersAmount) {
		this.postServiceDeliveryOrdersAmount = postServiceDeliveryOrdersAmount;
	}

	public BigDecimal getCourierServiceDeliveryOrdersCost() {
		return courierServiceDeliveryOrdersCost;
	}

	public void setCourierServiceDeliveryOrdersCost(BigDecimal courierServiceDeliveryOrdersCost) {
		this.courierServiceDeliveryOrdersCost = courierServiceDeliveryOrdersCost;
	}

	public BigDecimal getPostServiceDeliveryOrdersCost() {
		return postServiceDeliveryOrdersCost;
	}

	public void setPostServiceDeliveryOrdersCost(BigDecimal postServiceDeliveryOrdersCost) {
		this.postServiceDeliveryOrdersCost = postServiceDeliveryOrdersCost;
	}

	public BigDecimal getPersonOrdersAmount() {
		return personOrdersAmount;
	}

	public void setPersonOrdersAmount(BigDecimal personOrdersAmount) {
		this.personOrdersAmount = personOrdersAmount;
	}

	public BigDecimal getCompanyOrdersAmount() {
		return companyOrdersAmount;
	}

	public void setCompanyOrdersAmount(BigDecimal companyOrdersAmount) {
		this.companyOrdersAmount = companyOrdersAmount;
	}

	public BigDecimal getPersonMarginAmount() {
		return personMarginAmount;
	}

	public void setPersonMarginAmount(BigDecimal personMarginAmount) {
		this.personMarginAmount = personMarginAmount;
	}

	public BigDecimal getCompanyMarginAmount() {
		return companyMarginAmount;
	}

	public void setCompanyMarginAmount(BigDecimal companyMarginAmount) {
		this.companyMarginAmount = companyMarginAmount;
	}

	public BigDecimal getStockAmount() {
		return stockAmount;
	}

	public void setStockAmount(BigDecimal stockAmount) {
		this.stockAmount = stockAmount;
	}

	public BigDecimal getPostpayAmount() {
		return postpayAmount;
	}

	public void setPostpayAmount(BigDecimal postpayAmount) {
		this.postpayAmount = postpayAmount;
	}	

	public int getPersonTotalOrders() {
		return personTotalOrders;
	}

	public void setPersonTotalOrders(int personTotalOrders) {
		this.personTotalOrders = personTotalOrders;
	}

	public int getCompanyTotalOrders() {
		return companyTotalOrders;
	}

	public void setCompanyTotalOrders(int companyTotalOrders) {
		this.companyTotalOrders = companyTotalOrders;
	}

	public BigDecimal getVisitCost() {
		// стоимость сеанса = рекламный бюджет / число сеансов
		if (visits == 0) {
			return BigDecimal.ZERO;
		}
		return advertBudget.divide(BigDecimal.valueOf(visits), 2, RoundingMode.HALF_UP);		
	}
	
	public BigDecimal getVisitorCost() {
		// стоимость уника = рекламный бюджет / число уников
		if (uniqueVisitors == 0) {
			return BigDecimal.ZERO;
		}
		return advertBudget.divide(BigDecimal.valueOf(uniqueVisitors), 2, RoundingMode.HALF_UP);
	}
	
	public BigDecimal getUniqueVisitorsByVisitConversionRate() {
		// число уников / число сеансов
		if (visits == 0) {
			return BigDecimal.ZERO;
		}
		return BigDecimal.valueOf(uniqueVisitors).divide(BigDecimal.valueOf(visits), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
	}
	
	public BigDecimal getNewVisitorsRate() {
		// процент новых = новые / уникам
		if (uniqueVisitors == 0) {
			return BigDecimal.ZERO;
		}		
		return BigDecimal.valueOf(newVisitors).divide(BigDecimal.valueOf(uniqueVisitors), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
	} 
	
	public int getRepeatedVisitors() {
		return uniqueVisitors - newVisitors;
	}
	
	public BigDecimal getRepeatedVisitorsRate() {
		// процент повтор / уникам
		return BigDecimal.valueOf(100).subtract(getNewVisitorsRate());
	} 
	
	public int getTotalLeads() {
		return siteSourceLeads + emailSourceLeads + callSourceLeads + chatSourceLeads + othersSourceLeads;
	}
	
	public BigDecimal getLeadByVisitorsConversionRate() {
		// число лидов / число уников
		if (uniqueVisitors == 0) {
			return BigDecimal.ZERO;
		}
		return BigDecimal.valueOf(getTotalLeads()).divide(BigDecimal.valueOf(uniqueVisitors), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
	}
	
	public BigDecimal getLeadByVisitsConversionRate() {
		// число лидов / число посещений
		if (visits == 0) {
			return BigDecimal.ZERO;
		}
		return BigDecimal.valueOf(getTotalLeads()).divide(BigDecimal.valueOf(visits), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
	}
	
	public BigDecimal getLeadCost() {
		// стоимость лида = рекламный бюджет / число лидов
		if (getTotalLeads() == 0) {
			return BigDecimal.ZERO;
		}
		return advertBudget.divide(BigDecimal.valueOf(getTotalLeads()), 2, RoundingMode.HALF_UP);
	}
	
	public BigDecimal getRepeatedLeadsRate() {
		// процент повторных лидов к общему количеству
		if (getTotalLeads() == 0) {
			return BigDecimal.ZERO;
		}
		return BigDecimal.valueOf(repeatLeads).divide(BigDecimal.valueOf(getTotalLeads()), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
	}
	
	public BigDecimal getNewLeadsRate() {
		// процент новых лидов к общему количеству
		if (getTotalLeads() == 0) {
			return BigDecimal.ZERO;
		}
		return BigDecimal.valueOf(newLeads).divide(BigDecimal.valueOf(getTotalLeads()), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
	} 
	
	public BigDecimal getOrderCost() {
		// стоимость заказа = рекламный бюджет / число заказов
		if (getTotalOrders() == 0) {
			return BigDecimal.ZERO;
		}
		return advertBudget.divide(BigDecimal.valueOf(getTotalOrders()), 2, RoundingMode.HALF_UP);
	}
		
	public BigDecimal getOrdersByLeadsConversionRate() {
		// число заказов / число лидов
		if (getTotalLeads() == 0) {
			return BigDecimal.ZERO;
		}
		return BigDecimal.valueOf(getTotalOrders()).divide(BigDecimal.valueOf(getTotalLeads()), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
	}
	
	public BigDecimal getAverageBill() {
		// средний чек	= суммарный доход / число заказов
		if (getTotalOrders() == 0) {
			return BigDecimal.ZERO;
		}
		return getTotalAmount().divide(BigDecimal.valueOf(getTotalOrders()), 2, RoundingMode.HALF_UP);
	}
	
	/*
	public BigDecimal getTotalMarginAmount() {
		// суммарная прибыль = доход - закупку
		return getTotalAmount().subtract(getTotalSupplierAmount()).subtract(advertBudget);		
	}
	*/
	
	public BigDecimal getAverageMargin() {
		// средняя прибыль = суммарная прибыль / число заказов  
		if (getTotalOrders() == 0) {
			return BigDecimal.ZERO;
		}
		return getTotalMarginAmount().divide(BigDecimal.valueOf(getTotalOrders()), 2, RoundingMode.HALF_UP);
	}
	
	public BigDecimal getGrossProfit() {
		// валовая прибыль = (доход - закупка)
		return getTotalAmount().subtract(getTotalSupplierAmount());		
	}
	
	public BigDecimal getRoi() {
		if (getAdvertBudget() == null) {
			return BigDecimal.ZERO;
		}	
		if (getAdvertBudget().compareTo(BigDecimal.ZERO) <= 0) {
			return BigDecimal.ZERO;
		}		
		// маркетинговая ROI = (валовая прибыль - реклама)/реклама * 100%
		return getGrossProfit().subtract(getAdvertBudget()).divide(getAdvertBudget(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
	}

	public BigDecimal getPersonAverageBill() {
		// средний чек	по физлицу = суммарный доход / число заказов
		if (getPersonTotalOrders() == 0) {
			return BigDecimal.ZERO;
		}
		return getPersonOrdersAmount().divide(BigDecimal.valueOf(getPersonTotalOrders()), 2, RoundingMode.HALF_UP);
	}
	
	public BigDecimal getCompanyAverageBill() {
		// средний чек	по компании = суммарный доход / число заказов
		if (getCompanyTotalOrders() == 0) {
			return BigDecimal.ZERO;
		}
		return getCompanyOrdersAmount().divide(BigDecimal.valueOf(getCompanyTotalOrders()), 2, RoundingMode.HALF_UP);
	}
	
	public BigDecimal getPersonAverageMargin() {
		// средняя прибыль по физлицу = суммарный доход / число заказов
		if (getPersonTotalOrders() == 0) {
			return BigDecimal.ZERO;
		}
		return getPersonMarginAmount().divide(BigDecimal.valueOf(getPersonTotalOrders()), 2, RoundingMode.HALF_UP);
	}
	
	public BigDecimal getCompanyAverageMargin() {
		// средняя прибыль по компании = суммарный доход / число заказов
		if (getCompanyTotalOrders() == 0) {
			return BigDecimal.ZERO;
		}
		return getCompanyMarginAmount().divide(BigDecimal.valueOf(getCompanyTotalOrders()), 2, RoundingMode.HALF_UP);
	}
	
	public BigDecimal getFairRate() {
		// справедливая ставка = (прибыль/2) / число кликов

		if (getVisits() == 0) {
			return BigDecimal.ZERO;
		}
		return getCompanyMarginAmount().divide(BigDecimal.valueOf(2)).divide(BigDecimal.valueOf(getVisits()), 2, RoundingMode.HALF_UP);
	}
	
}
