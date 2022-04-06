<%@ include file="../fragments/header2init.jsp"%>
<%@ include file="../fragments/header2html2init.jsp"%>

<title><fmt:message key="app.title" /> | <fmt:message
		key="order.form.update.title" /></title>

<%@ include file="../fragments/header2html2navbar2start.jsp"%>
<!-- <h1 class="h2"><fmt:message key="index.header" /></h1>  -->
<div
	class="d-flex justify-content-start flex-wrap flex-md-nowrap align-items-start">

	<c:choose>
		<c:when test="${orderForm['new']}">
			<h1 class="h4">
				<fmt:message key="order.form.new.header" />
			</h1>
		</c:when>
		<c:otherwise>
			<fmt:formatDate value="${order.orderDate}" pattern="dd.MM.yyyy" var="orderDateFormated" />
			<c:set var="strOrderDate" value="${orderDateFormated}" />
			<fmt:message key="order.form.update.headers.h1" var="orderHeaderInfo">
				<fmt:param value="${order.viewNo}" />
				<fmt:param value="${strOrderDate}" />
			</fmt:message>
			<h1 class="h4">
				<c:out value="${orderHeaderInfo}" />
			</h1>
		</c:otherwise>
	</c:choose>

</div>
<div
	class="d-flex justify-content-start flex-wrap flex-md-nowrap align-items-start border-bottom- mb-2">
	<c:choose>
		<c:when test="${orderForm['new']}">
			<h2 id="header-customer-longName" class="h5">
				<fmt:message key="order.form.new.headers.h2" />
			</h2>
		</c:when>
		<c:otherwise>
			<h2 id="header-customer-longName" class="h5">${orderForm.customer.viewLongNameWithContactInfo}&nbsp;<span class="badge badge-secondary">${order.customer.id}</span>
			</h2>
		</c:otherwise>
	</c:choose>
</div>
<c:choose>
	<c:when test="${orderForm['new']}">

	</c:when>
	<c:otherwise>
		<div>
			<button id="btn-update-order" type="button" class="btn btn-light" disabled>
				<fmt:message key="main.btn.updateData" />
			</button>
			<button id="btn-show-order" type="button" class="btn btn-primary">
				<fmt:message key="main.btn.showData" />
			</button>			
			<button id="btn-status-order" type="button" class="btn btn-light">
				<fmt:message key="main.btn.statusData" />
			</button>
			<button id="btn-clone-order" type="button" class="btn btn-light">
				<fmt:message key="main.btn.cloneData" />
			</button>			
			<button id="btn-save-order" type="button" class="btn btn-light">
				<fmt:message key="main.btn.save" />
			</button>
		</div>

	</c:otherwise>
</c:choose>

<br />

<div class="row">

	<div class="col-md-4 order-md-2 mb-4"></div>

	<div class="col-md-8 order-md-1">
		<spring:url value="/orders/${orderForm.id}/save/${listType}" var="orderActionUrl" />
		<form:form class="needs-validation" method="post" modelAttribute="orderForm" action="${orderActionUrl}">
			<form:hidden path="id" />

			<div class="form-row">
				<spring:message code="order.form.fields.placeholder.no" var="orderFormFieldsPlaceholderNo" />
				<spring:bind path="no">
					<div class="form-group col-md-2">
						<label for="input-order-no"><fmt:message key="order.form.fields.no" /></label>
						<form:input path="no" type="text" 
							class="${status.error ? 'is-invalid' : ''} form-control form-control-sm form-control-required" 
							id="input-order-no" 
							placeholder="${orderFormFieldsPlaceholderNo}" />
						<form:errors path="no" class="control-label invalid-feedback" />
						<div class="invalid-feedback">
							<fmt:message key="order.form.fields.invalidFeedback.no" />
						</div>
					</div>
				</spring:bind>
				<div class="form-group col-md-1">
					<label for="input-order-subno"><fmt:message key="order.form.fields.subNo" /></label>
					<form:input path="subNo" type="number" min="0" max="5" class="form-control form-control-sm" id="input-order-subno" />
				</div>

				<spring:message code="order.form.fields.placeholder.orderDate" var="orderFormFieldsPlaceholderOrderDate" />
				<spring:bind path="orderDate">
					<div class="form-group col-md-2">
						<label for="input-order-orderDate"><fmt:message key="order.form.fields.orderDate" /></label>
						<div class="input-group">
							<form:input path="orderDate" type="text" maxlength="10"
								class="${status.error ? 'is-invalid' : ''} form-control form-control-sm form-control-required datepicker"
								id="input-order-orderDate"
								placeholder="${orderFormFieldsPlaceholderOrderDate}" />							
							<div class="input-group-append">
								<button class="btn btn-sm btn-light btn-outline-secondary" onclick="$('#input-order-orderDate').datepicker('show');" type="button">
									<i class="bi bi-calendar2-date text-dark"></i>									
								</button>
							</div>
							<form:errors path="orderDate" class="control-label invalid-feedback" />
							<div class="invalid-feedback">
								<fmt:message key="order.form.fields.invalidFeedback.orderDate" />
							</div>
						</div>
					</div>
				</spring:bind>
				
				<div class="form-group col-md-2">					
					<label for="select-store"><fmt:message key="order.form.fields.store" /></label>
					<form:select path="store" id="select-store" class="form-control form-control-sm">
						<form:options items="${store}" itemLabel="site" />
					</form:select>
				</div>

			</div>
			<div class="form-row">
				<div class="form-group col-md-3">
					<label for="select-order-type"><fmt:message	key="order.form.fields.orderType" /></label>
					<form:select path="orderType" id="select-order-type" class="form-control form-control-sm">
						<form:options items="${orderTypes}" itemLabel="annotation" />
					</form:select>

				</div>
				<div class="form-group col-md-2">
					<label for="select-source-type"><fmt:message key="order.form.fields.sourceType" /></label>
					<form:select path="sourceType" id="select-source-type" class="form-control form-control-sm">
						<form:options items="${sourceTypes}" itemLabel="annotation" />
					</form:select>
				</div>

				<div class="form-group col-md-2">
					<label for="select-advert-type"><fmt:message key="order.form.fields.advertType" /></label>

					<form:select path="advertType" id="select-advert-type" class="form-control form-control-sm">
						<form:options items="${advertTypes}" itemLabel="annotation" />
					</form:select>
				</div>
				<div class="form-group col-md-4">
					<label for="select-product-category"><fmt:message key="order.form.fields.productCategory" /></label>
					<form:select path="productCategory.id" id="select-product-category"	class="form-control form-control-sm">
						<form:options items="${productCategories}" itemValue="id" itemLabel="name" />
					</form:select>
				</div>
				
				<div class="form-group col-md-1">
					<label for="input-order-offer-count-day">
						<fmt:formatDate pattern="dd.MM.yyyy" value="${orderForm.offer.expiredDate}" />
					</label>					
					<form:input path="offer.countDay" type="number" min="0" max="90" class="form-control form-control-sm" id="input-order-offer-count-day" />
				</div>
				
			</div>
			<div class="form-row">
				<div class="form-group col-md">
					<label for="input-payment-annotation"><fmt:message key="order.form.payment.fields.annotation" /></label>
					<form:textarea path="annotation" id="input-payment-annotation"
						class="form-control form-control-sm" rows="2" maxlength="255" />

				</div>
			</div>
			<h4 id="header-group-customer" class="mb-4">
				<fmt:message key="order.form.headers.customer" />
			</h4>
			<hr class="mb-4">

			<form:hidden path="formCustomer.id" />
			<form:hidden path="formCustomer.personId" />
			<form:hidden path="formCustomer.mainAddress.id" />
			
			<div class="form-row">
			<c:forEach items="${customerTypes}" var="item">			
				<c:choose>
					<c:when test="${item.id != 6}">				
						<div class="form-group col-md-6">			
							<div class="form-check">
							  <input class="form-check-input radio-customer-type" type="radio" name="gridRadiosCustomerType" id="customer-types-${item}" value="${item}">
							  <label class="form-check-label" for="customer-types-customer">${item.longName}</label>
							</div>	
						</div>			
					</c:when>			
				</c:choose>
			</c:forEach>
			</div>
		
			<div class="form-row form-row-select-customer-types">
				<div class="form-group col-md-6">
					<label for="select-customer-type"><fmt:message key="order.form.customer.fields.type" /></label>
					<form:select path="formCustomer.customerType" id="select-customer-type" class="form-control form-control-sm">
						<form:options items="${customerTypes}" itemLabel="longName" />
					</form:select>
				</div>
			</div>
			<div class="form-row">	
				<div class="form-group col-md-6">
					<label for="select-customer-country"><fmt:message key="order.form.customer.fields.country" /></label>
					<form:select path="formCustomer.country" id="select-customer-country" class="form-control form-control-sm">
						<form:options items="${countries}" itemLabel="annotation" />
					</form:select>
				</div>
			</div>

			<!--  CUSTOMER -->
			<div class="form-group-customer">

				<div class="form-row">							
					<spring:message code="order.form.customer.fields.placeholder.telephone" var="orderFormCustomerFieldsPlaceholderTelephone" />	
					<spring:bind path="formCustomer.phoneNumber">
						<div class="form-group col-md-3">
							<label for="input-customer-phone"><fmt:message key="order.form.customer.fields.telephone" /></label>							
							<div class="input-group">
								<form:input path="formCustomer.phoneNumber" type="text"
									maxlength="15"
									class="${status.error ? 'is-invalid' : ''} form-control form-control-sm phone input-mask-phone form-control-customer-find"
									id="input-customer-phone"
									placeholder="${orderFormCustomerFieldsPlaceholderTelephone}" />									
								<div class="input-group-append">
									<button id="btn-find-customer-phone"
										class="btn btn-sm btn-light btn-outline-secondary"
										type="button">
										<i class="bi bi-search text-dark"></i>
									</button>
								</div>
								<form:errors path="formCustomer.phoneNumber" class="control-label invalid-feedback" />
							</div>
							<div class="invalid-feedback">
								<fmt:message key="order.form.customer.fields.invalidFeedback.telephone" />
							</div>
						</div>
					</spring:bind>

					<div class="form-group col-md-4">
						<label for="input-customer-email"><fmt:message key="order.form.customer.fields.email" /></label>
						<form:input path="formCustomer.email" type="text" maxlength="30" class="form-control form-control-sm form-control-customer-find" id="input-customer-email" />
					</div>
					
					<div class="form-group col-md-5">					
						<label for="input-customer-orders">&nbsp;</label>
						<div class="dropdown">
						  <button id="btn-find-customer-orders" class="btn btn-sm btn-light dropdown-toggle" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
						    <fmt:message key="orders.btn.customerOrders" />&nbsp;<span class="badge badge-secondary">0</span>
						  </button>
						  <div class="dropdown-menu dropdown-menu-customer-orders" aria-labelledby="dropdownMenuButton">
						  	<!-- 
						    <a class="dropdown-item dropdown-menu-customer-order" href="#">Action</a>
						    <a class="dropdown-item dropdown-menu-customer-order" href="#">Another action</a>
						    <a class="dropdown-item dropdown-menu-customer-order" href="#">Something else here</a>
						     -->
						  </div>
						</div>
					</div>	
				</div>

				<div class="form-row">
					<spring:message code="order.form.customer.fields.placeholder.firstName" var="orderFormCustomerFieldsPlaceholderFirstName" />
					<spring:bind path="formCustomer.firstName">
						<div class="form-group col-md-3">
							<label for="input-customer-first-name"><fmt:message	key="order.form.customer.fields.firstName" /></label>							
							<form:input path="formCustomer.firstName" type="text" class="${status.error ? 'is-invalid' : ''} form-control form-control-sm"
								id="input-customer-first-name" placeholder="${orderFormCustomerFieldsPlaceholderFirstName}" />
							<form:errors path="formCustomer.firstName" class="control-label invalid-feedback" />		
							<div class="invalid-feedback">
								<fmt:message key="order.form.customer.fields.invalidFeedback.firstName" />
							</div>

						</div>
					</spring:bind>
					<div class="form-group col-md-4">
						<label for="input-customer-middle-name"><fmt:message key="order.form.customer.fields.middleName" /></label>
						<form:input path="formCustomer.middleName" type="text" class="form-control form-control-sm" id="input-customer-middle-name" />
					</div>
					<div class="form-group col-md-5">
						<label for="input-customer-last-name"><fmt:message key="order.form.customer.fields.lastName" /></label>
						<form:input path="formCustomer.lastName" type="text" class="form-control form-control-sm" id="input-customer-last-name" />
					</div>
				</div>

			</div>
			<!--  COMPANY -->
			<div class="form-group-company">				
				<div class="form-row">
					<div class="form-group col-md-2">
						<label for="input-customer-inn"><fmt:message key="order.form.customer.fields.inn" /></label>
						<spring:message code="order.form.customer.fields.placeholder.inn" var="orderFormCustomerFieldsPlaceholderInn" />
						<form:input path="formCustomer.inn" type="text" maxlength="12" class="form-control form-control-sm form-control-customer-find"
							id="input-customer-inn"
							placeholder="${orderFormCustomerFieldsPlaceholderInn}" />
						<div class="invalid-feedback">
							<fmt:message key="order.form.customer.fields.invalidFeedback.inn" />
						</div>
					</div>
					
					<spring:message code="order.form.customer.fields.placeholder.shortName" var="orderFormCustomerFieldsPlaceholderShortName" />
					<spring:bind path="formCustomer.shortName">
					<div class="form-group col-md-3">
						<label for="input-customer-short-name"><fmt:message key="order.form.customer.fields.shortName" /></label>						
						<form:input path="formCustomer.shortName" type="text" 
							class="${status.error ? 'is-invalid' : ''} form-control form-control-sm form-control-customer-find"
							id="input-customer-short-name"
							placeholder="${orderFormCustomerFieldsPlaceholderShortName}" />
						<form:errors path="formCustomer.shortName" class="control-label invalid-feedback" />			
						<div class="invalid-feedback">
							<fmt:message key="order.form.customer.fields.invalidFeedback.shortName" />
						</div>
					</div>
					</spring:bind>
					<div class="form-group col-md-7">
						<label for="input-customer-long-name"><fmt:message
								key="order.form.customer.fields.longName" /></label>
						<spring:message code="order.form.customer.fields.placeholder.longName" var="orderFormCustomerFieldsPlaceholderLongName" />
						<form:input path="formCustomer.longName" type="text"
							class="form-control form-control-sm"
							id="input-customer-long-name"
							placeholder="${orderFormCustomerFieldsPlaceholderLongName}" />
						<div class="invalid-feedback"> 
							<fmt:message key="order.form.customer.fields.invalidFeedback.longName" />
						</div>
					</div>
				</div>

				<h5 class="mb-4">
					<fmt:message key="order.form.headers.customer.contact" />
				</h5>
				<div class="form-row">
					<form:hidden path="formCustomer.mainContact.id" />
					<form:hidden path="formCustomer.mainContact.personId" />
					<form:hidden path="formCustomer.mainContact.country" />
					
					<spring:message code="order.form.customer.fields.placeholder.contact.firstName" var="orderFormCustomerFieldsPlaceholderContactFirstName" />
					<spring:bind path="formCustomer.mainContact.firstName">
					<div class="form-group col-md-3">
						<label for="input-customer-contact-first-name"><fmt:message key="order.form.customer.fields.contact.firstName" /></label>						
						<form:input path="formCustomer.mainContact.firstName" type="text"
							class="${status.error ? 'is-invalid' : ''} form-control form-control-sm"
							id="input-customer-contact-first-name"
							placeholder="${orderFormCustomerFieldsPlaceholderContactFirstName}" />
						<form:errors path="formCustomer.mainContact.firstName" class="control-label invalid-feedback" />
						<div class="invalid-feedback">
							<fmt:message key="order.form.customer.fields.invalidFeedback.contact.firstName" />
						</div>
					</div>
					</spring:bind>
					<div class="form-group col-md-4">
						<label for="input-customer-contact-middle-name"><fmt:message key="order.form.customer.fields.contact.middleName" /></label>
						<spring:message code="order.form.customer.fields.placeholder.contact.middleName" var="orderFormCustomerFieldsPlaceholderContactMiddleName" />
						<form:input path="formCustomer.mainContact.middleName" type="text"
							class="form-control form-control-sm"
							id="input-customer-contact-middle-name"
							placeholder="${orderFormCustomerFieldsPlaceholderContactMiddleName}" />
						<div class="invalid-feedback">
							<fmt:message key="order.form.customer.fields.invalidFeedback.contact.middleName" />
						</div>
					</div>
					<div class="form-group col-md-5">
						<label for="input-customer-contact-last-name"><fmt:message
								key="order.form.customer.fields.contact.lastName" /></label>
						<spring:message code="order.form.customer.fields.placeholder.contact.lastName" var="orderFormCustomerFieldsPlaceholderContactLastName" />
						<form:input path="formCustomer.mainContact.lastName" type="text"
							class="form-control form-control-sm"
							id="input-customer-contact-last-name"
							placeholder="${orderFormCustomerFieldsPlaceholderContactLastName}" />
						<div class="invalid-feedback">
							<fmt:message key="order.form.customer.fields.invalidFeedback.contact.lastName" />
						</div>
					</div>

				</div>

				<div class="form-row">
					<spring:message code="order.form.customer.fields.placeholder.contact.telephone" var="orderFormCustomerFieldsPlaceholderContactTelephone" />
					<spring:bind path="formCustomer.mainContact.phoneNumber">
						<div class="form-group col-md-3">
							<label for="input-customer-contact-phone"><fmt:message key="order.form.customer.fields.contact.telephone" /></label>							
							<div class="input-group">
								<form:input path="formCustomer.mainContact.phoneNumber"
									type="text" maxlength="15"
									class="${status.error ? 'is-invalid' : ''} form-control form-control-sm phone input-mask-phone form-control-customer-find"
									id="input-customer-contact-phone"
									placeholder="${orderFormCustomerFieldsPlaceholderContactTelephone}" />								
								<div class="input-group-append">
									<button id="btn-find-customer-contact-phone"
										class="btn btn-sm btn-light btn-outline-secondary"
										type="button">
										<i class="bi bi-search text-dark"></i>
									</button>
								</div>
								<form:errors path="formCustomer.mainContact.phoneNumber" class="control-label invalid-feedback" />
							</div>
							<div class="invalid-feedback">
								<fmt:message key="order.form.customer.fields.invalidFeedback.contact.telephone" />
							</div>
						</div>
					</spring:bind>
					<spring:message code="order.form.customer.fields.placeholder.contact.email" var="orderFormCustomerFieldsPlaceholderContactEmail" />
					<spring:bind path="formCustomer.mainContact.email">
						<div class="form-group col-md-9">
							<label for="input-customer-contact-email"><fmt:message key="order.form.customer.fields.contact.email" /></label>
							<form:input path="formCustomer.mainContact.email" type="text" 
								class="${status.error ? 'is-invalid' : ''} form-control form-control-sm form-control-customer-find" 
								id="input-customer-contact-email" />
							<form:errors path="formCustomer.mainContact.email" class="control-label invalid-feedback" />	
						</div>
					</spring:bind>
				</div>
			</div>

			<div class="form-row">
				<div class="form-group col-md">
					<label for="input-customer-address"><fmt:message key="order.form.customer.fields.address" /></label>
					<form:textarea path="formCustomer.mainAddress.address"
						id="input-customer-address" class="form-control form-control-sm"
						rows="2" maxlength="255" />
				</div>
			</div>
			
			<h4 class="mb-3">
				<fmt:message key="order.form.headers.delivery" />
			</h4>
			<hr class="mb-4">
			<form:hidden path="delivery.id" />
			<div class="form-row">
				<div class="form-group col-md-2">
					<label for="select-payment-types"><fmt:message key="order.form.payment.fields.type" /></label>
					<form:select path="paymentType" id="select-payment-type" class="form-control form-control-sm">
						<form:options items="${paymentTypes}" itemLabel="annotation" />
					</form:select>
				</div>
				
				<div class="form-group col-md-2">
					<label for="select-payment-delivery-types"><fmt:message key="order.form.delivery.fields.paymentType" /></label>
					<form:select path="delivery.paymentDeliveryType" id="select-payment-delivery-type" class="form-control form-control-sm input-calc-amounts">
						<form:options items="${paymentDeiveryTypes}" itemLabel="annotation" />
					</form:select>
				</div>

				<div class="form-group col-md-3">
					<label for="select-delivery-type"><fmt:message key="order.form.delivery.fields.type" /></label>
					<form:select path="delivery.deliveryType" id="select-delivery-type" class="form-control form-control-sm">
						<form:options items="${deliveryTypes}" itemLabel="annotation" />
					</form:select>
				</div>
				
				
				<div class="form-group col-md-2">
				
					<label for="select-delivery-price-type"><fmt:message key="order.form.delivery.fields.priceType" /></label>
					<form:select path="delivery.deliveryPrice" id="select-delivery-price-type" class="form-control form-control-sm">						
					</form:select>
		
				</div>
								
				<div class="form-group col-md-3">
					<label for="select-delivery-address-country"><fmt:message key="order.form.delivery.fields.country" /></label>
					<form:select path="delivery.address.country" id="select-delivery-address-country" class="form-control form-control-sm">
						<form:options items="${countries}" itemLabel="annotation" />
					</form:select>
				</div>
			</div>

			<form:hidden path="delivery.address.id" />
			<div class="form-row">
				<div class="form-group col-md">
					<label for="input-delivery-address"><fmt:message key="order.form.delivery.fields.address" /></label>
					<form:textarea path="delivery.address.address" id="input-delivery-address" class="form-control form-control-sm" rows="2" maxlength="255" />
					
					<!-- HIDEN BEGIN -->
					<form:input path="delivery.address.carrierInfo.cityId" type="text" 
								class="form-control form-control-sm delivery-address-carrierPvz" 
								id="input-delivery-address-carrierPvz-cityId" />
					<form:input path="delivery.address.carrierInfo.cityContext" type="text" 
								class="form-control form-control-sm delivery-address-carrierPvz" 
								id="input-delivery-address-carrierPvz-city" />
					<form:input path="delivery.address.carrierInfo.pvz" type="text" 
								class="form-control form-control-sm delivery-address-carrierPvz" 
								id="input-delivery-address-carrierPvz-code" />								
					<form:input path="delivery.address.carrierInfo.street" type="text" 
								class="form-control form-control-sm delivery-address-carrierPvz" 
								id="input-delivery-address-carrierPvz-street" />
					<form:input path="delivery.address.carrierInfo.house" type="text" 
								class="form-control form-control-sm delivery-address-carrierPvz" 
								id="input-delivery-address-carrierPvz-house" />
					<form:input path="delivery.address.carrierInfo.flat" type="text" 
								class="form-control form-control-sm delivery-address-carrierPvz" 
								id="input-delivery-address-carrierPvz-flat" />
										
					<form:input path="delivery.address.carrierInfo.deliveryVariantId" type="text" 
								class="form-control form-control-sm delivery-address-carrierPvz" 
								id="input-delivery-address-carrierPvz-deviveryVariantId" />											
								
																
								
					<!-- HIDEN END -->														
								
					
				</div>
			</div>
			
			<form:hidden path="delivery.recipient.personId" />
  			<div class="form-row">
  			</div>
  			<div class="custom-control custom-checkbox">	
    			<form:checkbox path="customerEqualsRecipient" class="custom-control-input" id="check-delivery-customer-equals-recipient"/>
    			<label for="check-delivery-customer-equals-recipient" class="custom-control-label"><fmt:message key="order.form.delivery.fields.recipient.isCustomerEqualsRecipient" /></label>
  			</div>
						
			<div class="form-row">
					<div class="form-group col-md-3">
						<label for="input-customer-contact-first-name"><fmt:message key="order.form.delivery.fields.recipient.contact.firstName" /></label>						
						<form:input path="delivery.recipient.firstName" type="text" class="form-control form-control-sm input-delivery-recipient" id="input-delivery-recipient-first-name"/>
					</div>
					<div class="form-group col-md-3">
						<label for="input-customer-contact-middle-name"><fmt:message key="order.form.delivery.fields.recipient.contact.middleName" /></label>						
						<form:input path="delivery.recipient.middleName" type="text" class="form-control form-control-sm input-delivery-recipient" id="input-delivery-recipient-middle-name"/>
					</div>
					<div class="form-group col-md-3">
						<label for="input-customer-contact-last-name"><fmt:message key="order.form.delivery.fields.recipient.contact.lastName" /></label>						
						<form:input path="delivery.recipient.lastName" type="text" class="form-control form-control-sm input-delivery-recipient" id="input-delivery-recipient-last-name"/>
					</div>					
					<div class="form-group col-md-3">
						<label for="input-customer-contact-phone-number"><fmt:message key="order.form.delivery.fields.recipient.contact.telephone" /></label>						
						<form:input path="delivery.recipient.phoneNumber" type="text" class="form-control form-control-sm input-delivery-recipient" id="input-delivery-recipient-phone-number"/>
					</div>
				
			</div>
			
			<div class="form-row">
				<div class="form-group col-md-3">
					<label for="input-delivery-price"><fmt:message key="order.form.delivery.fields.price" /></label>
										
					<div class="input-group">
						<form:input path="delivery.price" type="text" class="form-control form-control-sm input-calc-amounts-" id="input-delivery-price" />
					
						<div class="input-group-append">
									<button id="btn-calc-parcel-delivery-amounts-1" 
											class="btn btn-sm btn-light btn-outline-secondary btn-calc-parcel-delivery-amounts" 
											type="button">
											<i class="bi bi-truck text-dark"></i>
										
									</button>
									
									<button id="btn-cdek-widjet-1" 
											class="btn btn-sm btn-light btn-outline-secondary btn-cdek-widjet" 
											type="button">
											<i class="bi bi-map text-dark"></i>
										
									</button>
									
									<button id="btn-cdek-pvzs" 
											class="btn btn-sm btn-light btn-outline-secondary" 
											type="button">
										<i class="bi bi-list text-dark"></i>
									</button>							
																		
						</div>
					</div>
					
				</div>
				<form:hidden id="input-delivery-seller-price" path="delivery.factSellerPrice" />
				<form:hidden id="input-delivery-customer-price" path="delivery.factCustomerPrice" />
				
				<div class="form-group col-md-2">
					<label for="input-delivery-courier-date"><fmt:message key="order.form.delivery.fields.courier.deliveryDate" /></label>
					<div class="input-group">
						<form:input path="delivery.address.carrierInfo.courierInfo.deliveryDate" type="text" class="form-control form-control-sm datepicker" id="input-delivery-courier-date" />
						<div class="input-group-append">
							<button class="btn btn-sm btn-light btn-outline-secondary"
								onclick="$('#input-delivery-courier-date').datepicker('show');"	type="button">
								<i class="bi bi-calendar2-date text-dark"></i>								
							</button>							
						</div>
					</div>
				</div>

				<div class="form-group col-md-1">
					<label for="input-delivery-courier-period-start"><fmt:message
							key="order.form.delivery.fields.courier.period.start" /></label>

					<form:input path="delivery.address.carrierInfo.courierInfo.startTime" type="text"
						class="form-control form-control-sm input-mask-time"
						id="input-delivery-courier-period-start" />


				</div>
				<div class="form-group col-md-1">
					<label for="input-delivery-courier-period-end"><fmt:message
							key="order.form.delivery.fields.courier.period.end" /></label>

					<form:input path="delivery.address.carrierInfo.courierInfo.endTime" type="text"
						class="form-control form-control-sm input-mask-time"
						id="input-delivery-courier-period-end" />
				</div>
				
				<div class="form-group col-md-3">
							<label for="input-amounts-postpay"><fmt:message key="order.form.delivery.fields.postpayAmount" /></label>							
							<div class="input-group">
								<form:input path="amounts.postpay" type="text"
									maxlength="12"
									class="form-control form-control-sm"
									id="input-amounts-postpay"
									placeholder="" />									
								<div class="input-group-append">
									
									<button id="btn-calc-parcel-delivery-amounts-info" 
											class="btn btn-sm btn-light btn-outline-secondary" 
											type="button" 
											data-toggle="popover" title="${orderForm.delivery.deliveryType.annotation.toUpperCase()}" data-trigger="focus" data-placement="right" data-content="${orderForm.delivery.address.address}">
											<i class="bi bi-cash-coin text-dark"></i>
									</button>
									
								</div>
							</div>
							<div class="invalid-feedback">
								<fmt:message key="order.form.customer.fields.invalidFeedback.telephone" />
							</div>
				</div>

				<div class="form-group col-md-2">
					<label for="input-delivery-trackCode"><fmt:message key="order.form.delivery.fields.trackCode" /></label>
					<form:input path="delivery.trackCode" type="text" class="form-control form-control-sm" id="input-delivery-trackCode" />
				</div>

			</div>


			<div class="form-row">
				<div class="form-group col-md">
					<label for="input-delivery-courier-annotation"><fmt:message
							key="order.form.delivery.fields.courier.annotation" /></label>
					<form:input path="delivery.annotation" type="text"
						class="form-control form-control-sm"
						id="input-delivery-courier-annotation" />
				</div>
			</div>

			<h4 class="mb-3">
				<fmt:message key="order.form.headers.details" />
			</h4>
			<hr class="mb-4">

			<div class="row">
				<div class="col-sm-3">
					<fmt:message key="order.form.items.table.headers.product" />
				</div>
				<div class="col-sm-1">
					<fmt:message key="order.form.items.table.headers.quantity" />
				</div>
				<div class="col-sm-2">
					<fmt:message key="order.form.items.table.headers.price" />
				</div>
				<div class="col-sm-2">
					<fmt:message key="order.form.items.table.headers.supplierAmount" />
				</div>
				<div class="col-sm-1">
					<fmt:message key="order.form.items.table.headers.discountRate" />
				</div>
				<div class="col-sm-2">
					<fmt:message key="order.form.items.table.headers.amount" />
				</div>
				<div class="col-sm-1"></div>
			</div>


			<!-- items start -->
			<c:set var="currentStatusIndex" value="0" />
			

<c:forEach items="${orderForm.items}" var="item" varStatus="status">
				<div id="details-item-row-${status.index}" class="row">
					<form:hidden id="input-details-item-id-${status.index}" path="items[${status.index}].id" />
					<form:hidden id="input-details-item-no-${status.index}" path="items[${status.index}].no" />
					<form:hidden id="input-details-item-product-id-${status.index}" path="items[${status.index}].product.id" />
					<!-- ${status.count} ${item.no}  -->
					<div class="col-sm-3">
						<div class="input-group">

							<form:input path="items[${status.index}].product.name"
								type="text"
								class="form-control form-control-sm input-details-item-product input-calc-amounts"
								list="datalist-product-name-${status.index}"
								id="input-details-item-product-${status.index}" />
							<datalist id="datalist-product-name-${status.index}"></datalist>	

							<div class="input-group-append">
								<button id="btn-find-details-item-product-${status.index}"
									class="btn btn-sm btn-light btn-outline-secondary btn-find-details-item-product"
									type="button">
									<i class="bi bi-search text-dark"></i>
								</button>
							</div>
						</div>
						<span id="span-details-item-product-sku-info-${status.index}" class="badge badge-light">
							${orderForm.items[status.index].product.sku}
						</span>
					</div>
					<div class="col-sm-1">
						<div class="input-group">
							<form:input path="items[${status.index}].quantity" type="text" 
							    class="form-control form-control-sm input-details-item-quantity input-calc-amounts"
								id="input-details-item-quantity-${status.index}" />
							<!--  
							<input id="input-details-item-quantity-${item.id}" type="text"							 
								class="form-control form-control-sm input-details-item-quantity" name="items[${status.index}].quantity" value="${item.quantity}">
						-->
						</div>
						<span id="span-details-item-quantity-stock-info-${status.index}" class="badge badge-${orderForm.items[status.index].product.viewStockQuantityClass}">
							${orderForm.items[status.index].product.viewStockQuantityText}
						</span>
					</div>
					<div class="col-sm-2">
						<div class="input-group">
							<form:input path="items[${status.index}].price" type="text"
								class="form-control form-control-sm input-details-item-price input-calc-amounts"
								id="input-details-item-price-${status.index}" />
							<!-- 
							<input id="input-details-item-price-${item.id}" type="text" 
								class="form-control form-control-sm input-details-item-price" name="items[${status.index}].price" value="${item.price}">
						 	-->
						</div>
					</div>
					<div class="col-sm-2">
						<div class="input-group">
							<form:input path="items[${status.index}].supplierAmount"
								type="text"
								class="form-control form-control-sm input-details-item-supplier-amount input-calc-amounts"
								id="input-details-item-supplier-amount-${status.index}" />

						</div>
					</div>
					<div class="col-sm-1">
						<div class="input-group">
							<form:input path="items[${status.index}].discountRate"
								type="text"
								class="form-control form-control-sm input-details-item-discount-rate input-calc-amounts"
								id="input-details-item-discount-rate-${status.index}" />
							<!--
							<input id="input-details-item-discount-rate-${item.id}" type="text" 
								class="form-control form-control-sm input-details-item-discount-rate" name="items[${status.index}].discountRate" value="${item.discountRate}">
						  	-->
						</div>
					</div>
					
					<div class="col-sm-2">
						<div class="input-group">
							<form:input path="items[${status.index}].amount" type="text"
								class="form-control form-control-sm input-details-item-amount input-calc-amounts"
								id="input-details-item-amount-${status.index}" />
						</div>
					</div>
					
					<div class="col-sm-1">
						<button id="btn-details-item-delete-${status.index}" type="button"
							class="btn btn-sm btn-light btn-details-item-delete">
							<i class="bi bi-dash text-dark"></i>
							
						</button>
					</div>
				</div>
				<c:set var="currentStatusIndex" value="${status.count}" />
				
<script>
	var currentStatusIndex = ${status.count};
</script>
</c:forEach>
<c:set var="hiddenStatusIndex" value="7" />

			<div id="details-item-row-hidden" class="row" hidden>

				<form:hidden id="input-details-item-id-hidden" path="items[${hiddenStatusIndex}].id" value="-1" />
				<form:hidden id="input-details-item-no-hidden" path="items[${hiddenStatusIndex}].no" value="-1" />
				<form:hidden id="input-details-item-product-id-hidden" path="items[${hiddenStatusIndex}].product.id" />
				<!-- ${status.count} ${item.no}  -->
				<div class="col-sm-3">
					<div class="input-group">
						<form:input path="items[${hiddenStatusIndex}].product.name"
							type="text"
							list="datalist-product-name-hidden"
							class="form-control form-control-sm input-details-item-product input-calc-amounts"
							id="input-details-item-product-hidden" />						
						<datalist id="datalist-product-name-hidden"></datalist>
							
						<div class="input-group-append">
							<button
								class="btn btn-sm btn-light btn-outline-secondary btn-find-details-item-product"
								type="button" id="btn-find-details-item-product-hidden">
								<i class="bi bi-search text-dark"></i>
								
							</button>
						</div>
					</div>
					<span id="span-details-item-product-sku-info-hidden" class="badge badge-light"></span>
				</div>
				<div class="col-sm-1">
					<div class="input-group">
						<form:input path="items[${hiddenStatusIndex}].quantity"
							type="text"
							class="form-control form-control-sm input-details-item-quantity input-calc-amounts"
							id="input-details-item-quantity-hidden" />
					</div>
					<span id="span-details-item-quantity-stock-info-hidden" class="badge badge-light"></span>
				</div>
				<div class="col-sm-2">
					<div class="input-group">
						<form:input path="items[${hiddenStatusIndex}].price" type="text"
							class="form-control form-control-sm input-details-item-price input-calc-amounts"
							id="input-details-item-price-hidden" />
					</div>
				</div>
				<div class="col-sm-2">
					<div class="input-group">
						<form:input path="items[${hiddenStatusIndex}].supplierAmount"
							type="text"
							class="form-control form-control-sm input-details-item-supplier-amount input-calc-amounts"
							id="input-details-item-supplier-amount-hidden" />
					</div>
				</div>
				<div class="col-sm-1">
					<div class="input-group">
						<form:input path="items[${hiddenStatusIndex}].discountRate"
							type="text"
							class="form-control form-control-sm input-details-item-discount-rate input-calc-amounts"
							id="input-details-item-discount-rate-hidden" />
					</div>
				</div>
				<div class="col-sm-2">
					<div class="input-group">
						<form:input path="items[${hiddenStatusIndex}].amount" type="text"
							class="form-control form-control-sm input-details-item-amount input-calc-amounts"
							id="input-details-item-amount-hidden" />
					</div>
				</div>
				<div class="col-sm-1">
					<button type="button"
						class="btn btn-sm btn-light btn-details-item-delete"
						id="btn-details-item-delete-hidden">
						<i class="bi bi-dash text-dark"></i>
						
					</button>
				</div>
			</div>

			<div class="row">
				<div class="col-sm-7 mb-4">			
					<button id="btn-details-item-add" type="button"
						class="btn btn-sm btn-light">						
						<i class="bi bi-plus text-dark"></i>&nbsp;
						<fmt:message key="main.btn.items.add" />
					</button>
				</div>				
				<div class="col-sm-2">
					<fmt:message key="order.form.items.amounts.total" />
				</div>
				<div id="details-items-amounts-total" class="col-sm-2 text-right">--</div>
				<div class="col-sm-1"></div>
			</div>
			<div class="row">
				<div class="col-sm-7"></div>
				<div class="col-sm-2">
					<fmt:message key="order.form.items.amounts.delivery" />
				</div>
				<div id="details-items-amounts-delivery" class="col-sm-2 text-right">--</div>
				<div class="col-sm-1"></div>
			</div>
			<div class="row">
				<div class="col-sm-7"></div>
				<div class="col-sm-2">
					<p>
						<strong> <fmt:message
								key="order.form.items.amounts.totalWithDelivery" />
						</strong>
					</p>
				</div>
				<div id="details-items-amounts-total-with-delivery"	class="col-sm-2 text-right">--</div>
				<div class="col-sm-1"></div>
			</div>

			<div class="row">
				<div class="col-sm-7"></div>
				<div class="col-sm-2">
					<fmt:message key="order.form.items.amounts.bill" />
				</div>
				<div id="details-items-amounts-bill" class="col-sm-2 text-right">--</div>
				<div class="col-sm-1"></div>
			</div>
			<div class="row">
				<div class="col-sm-7"></div>
				<div class="col-sm-2">
					<fmt:message key="order.form.items.amounts.supplier" />
				</div>
				<div id="details-items-amounts-supplier" class="col-sm-2 text-right">--</div>
				<div class="col-sm-1"></div>
			</div>
			<div class="row">
				<div class="col-sm-7"></div>
				<div class="col-sm-2">
					<fmt:message key="order.form.items.amounts.margin" />
				</div>
				<div id="details-items-amounts-margin" class="col-sm-2 text-right">--</div>
				<div class="col-sm-1"></div>
			</div>
			<div class="row">
				<div class="col-sm-7"></div>
				<div class="col-sm-2">
					<p>
						<strong><fmt:message key="order.form.items.amounts.postpay" /></strong>
					</p>
				</div>
				<div id="details-items-amounts-postpay" class="col-sm-2 text-right">--</div>
				<div class="col-sm-1"></div>
			</div>
			
			<div class="row">
				<div class="col-sm-7"></div>
				<div class="col-sm-2">
					<fmt:message key="order.form.items.amounts.deliveryFactSellerPrice" />
				</div>
				<div id="details-items-amounts-delivery-seller-summary" class="col-sm-2 text-right"><fmt:formatNumber type = "currency" value = "${order.delivery.factSellerPrice}" /></div>
				<div class="col-sm-1"></div>
			</div>
			<div class="row">
				<div class="col-sm-7"></div>
				<div class="col-sm-2">
					<fmt:message key="order.form.items.amounts.deliveryFactCustomerPrice" />
				</div>
				<div id="details-items-amounts-delivery-customer-summary" class="col-sm-2 text-right"><fmt:formatNumber type = "currency" value = "${order.delivery.factCustomerPrice}" /></div>
				<div class="col-sm-1"></div>
			</div>
						
			
			<div class="row">
				<div class="col-sm-12"></div>
			</div>
			<!-- items end -->
			<hr class="mb-4">
			<button id="btn-submit-order" type="submit" class="btn btn-primary">
				<fmt:message key="main.btn.save" />
			</button>
		</form:form>


	</div>
</div>

<!-- Modal -->

<!-- delivery-ozon-rocket-modal -->
<div class="modal fade" id="delivery-ozon-rocket-modal" tabindex="-1" role="dialog" aria-labelledby="delivery-ozon-rocket-modal-title" aria-hidden="true">
  <div class="modal-dialog modal-xl modal-dialog-centered" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="delivery-ozon-rocket-modal-title"><fmt:message key="order.form.modal.delivery.ozon.rocket.header" /></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
	      <div class="container-fluid">
		    <div class="row">
		      <div class="col-sm-12">
		      
		      	<iframe id="iframe-ozon-widget" title="Ozon widget" style="width: 100%; height: 100%; min-width: 320px; min-height: 320px; border: none; overflow: hidden" 
		  			src="https://rocket.ozon.ru/lk/widget?token=WJSvphyEiaY3sEyWk2jNOA%3D%3D&showdeliveryprice=true&showdeliverytime=true&defaultcity=${order.delivery.address.carrierInfo.cityContext}">Браузер не поддерживает iframe</iframe>
		      </div>
		      <input type="text" id="input-ozon-rocket-delivery-variant-data" hidden="hidden"/>
		      <input type="text" id="input-ozon-rocket-delivery-variant-id" hidden="hidden"/>
		      <input type="text" id="input-ozon-rocket-delivery-variant-address" hidden="hidden"/>	      
		      		      
		    </div>
		    
		  </div>
            
      <!--             
	    <form>	    
		  <div class="input-group mb-5">
			<input type="text" id="input-delivery-ozon-rocket-city-search" class="form-control form-control-sm" placeholder='<fmt:message key="order.form.modal.delivery.ozon.rocket.fields.city.search" />' aria-label='<fmt:message key="order.form.modal.delivery.ozon.rocket.fields.city.search" />' aria-describedby="basic-addon2">
			<div class="input-group-append">
			    <button id="btn-find-delivery-ozon-rocket-city" class="btn btn-light btn-sm btn-outline-secondary" type="button">
			    <i class="bi bi-search text-dark"></i>
			    </button>
			</div>
		  </div>
		  
		  <h5 class="mb-3"><fmt:message key="order.form.modal.delivery.ozon.rocket.fields.pay.header" />&nbsp;[PREPAY]</h5>
		  
		  <div class="input-group- mb-3">
		  
		  	<iframe title="Ozon widget" style="width: 100%; height: 100%; min-width: 320px; min-height: 320px; border: none; overflow: hidden" 
		  		src="https://rocket.ozon.ru/lk/widget?token=WJSvphyEiaY3sEyWk2jNOA%3D%3D&showdeliveryprice=true&showdeliverytime=true&defaultcity=pskov">Браузер не поддерживает iframe</iframe>
		  </div>		  
		  
		  	
		  <div class="input-group- mb-3">		  	
		  	<div class="form-group form-check">
			    <input type="checkbox" class="form-check-input" id="checkbox-pay-card">
			    <label class="form-check-label" for="checkbox-pay-card"><fmt:message key="order.form.modal.delivery.ozon.rocket.fields.pay.card"/></label>
			  </div>
		  </div>			
		  <div class="input-group- mb-3">			
				<div class="form-group form-check">
				    <input type="checkbox" class="form-check-input" id="checkbox-pay-cash">
				    <label class="form-check-label" for="checkbox-pay-cash"><fmt:message key="order.form.modal.delivery.ozon.rocket.fields.pay.cash"/></label>
				  </div>
		  </div>          
          <div class="form-group">
            <label for="input-delivery-ozon-rocket-city-address" class="col-form-label"><fmt:message key="order.form.modal.delivery.ozon.rocket.fields.address"/></label>
            <input type="text" class="form-control form-control-sm" id="input-delivery-ozon-rocket-city-address">
          </div>
        </form>  
        -->      
      </div>
      <div class="modal-footer">
        <button id="button-modal-delivery-ozon-rocket-cancel" type="button" class="btn btn-light" data-dismiss="modal"><fmt:message key="main.btn.cancel" /></button>
        <button id="button-modal-delivery-ozon-rocket-ok" type="button" class="btn btn-primary"><fmt:message key="main.btn.ok" /></button>
      </div>
    </div>
  </div>
</div>

<!-- delivery-cdek-modal -->
<div class="modal fade" id="delivery-cdek-modal" tabindex="-1" role="dialog" aria-labelledby="delivery-cdek-modal-title" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="delivery-cdek-modal-title"><fmt:message key="order.form.modal.delivery.cdek.header" /></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      
	    <form>	    
		  <div class="input-group mb-3">
			<input type="text" id="input-delivery-cdek-city-search" class="form-control form-control-sm" placeholder='<fmt:message key="order.form.modal.delivery.cdek.fields.city.search" />' aria-label='<fmt:message key="order.form.modal.delivery.cdek.fields.city.search" />' aria-describedby="basic-addon2">
			<div class="input-group-append">
			    <button id="btn-find-delivery-cdek-city" class="btn btn-light btn-sm btn-outline-secondary" type="button">
			    <i class="bi bi-search text-dark"></i>
			    </button>
			</div>
		  </div>
          <div class="form-group">
            <label for="select-delivery-cdek-city" class="col-form-label"><fmt:message key="order.form.modal.delivery.cdek.fields.city" /></label>            
            <select id="select-delivery-cdek-city" class="form-control form-control-sm"></select>
          </div>          
          <div class="form-group">
            <label for="select-delivery-cdek-pvz" class="col-form-label"><fmt:message key="order.form.modal.delivery.cdek.fields.pvz" /></label>
            <select multiple id="select-delivery-cdek-pvz" class="form-control form-control-sm"></select>
          </div>
          
          <div class="form-row">         
	          <div class="form-group col-md-8">
	            <label for="select-delivery-cdek-street" class="col-form-label"><fmt:message key="order.form.modal.delivery.cdek.fields.street" /></label>
	            <input type="text" class="form-control form-control-sm input-delivery-cdek-address-part" id="input-delivery-cdek-street">
	          </div>	          
	          <div class="form-group col-md-2">
	            <label for="select-delivery-cdek-house" class="col-form-label"><fmt:message key="order.form.modal.delivery.cdek.fields.house" /></label>
	            <input type="text" class="form-control form-control-sm input-delivery-cdek-address-part" id="input-delivery-cdek-house">
	          </div>	          
	          <div class="form-group col-md-2">
	            <label for="select-delivery-cdek-flat" class="col-form-label"><fmt:message key="order.form.modal.delivery.cdek.fields.flat" /></label>
	            <input type="text" class="form-control form-control-sm input-delivery-cdek-address-part" id="input-delivery-cdek-flat">
	          </div> 
	      </div>     
          
          <div class="form-group">
            <label for="input-delivery-cdek-city-address" class="col-form-label"><fmt:message key="order.form.modal.delivery.cdek.fields.address"/></label>
            <input type="text" class="form-control form-control-sm" id="input-delivery-cdek-city-address">
          </div>
        </form>        
      </div>
      <div class="modal-footer">
        <button id="button-modal-delivery-cdek-cancel" type="button" class="btn btn-light" data-dismiss="modal"><fmt:message key="main.btn.cancel" /></button>
        <button id="button-modal-delivery-cdek-ok" type="button" class="btn btn-primary"><fmt:message key="main.btn.ok" /></button>
      </div>
    </div>
  </div>
</div>

<!-- pvzs-cdek-modal -->
<div class="modal fade" id="pvzs-cdek-modal" tabindex="-1" role="dialog" aria-labelledby="pvzs-cdek-modal-title" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="pvzs-cdek-modal-title"><fmt:message key="order.form.modal.pvzs.cdek.header" /></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      
	    <form>	    
		  <div class="input-group mb-3">
			<input type="text" id="input-pvzs-cdek-city-search" class="form-control form-control-sm" placeholder='<fmt:message key="order.form.modal.pvzs.cdek.fields.city.search" />' aria-label='<fmt:message key="order.form.modal.pvzs.cdek.fields.city.search" />' aria-describedby="basic-addon2">
			<div class="input-group-append">
			    <button id="btn-find-pvzs-cdek-city" class="btn btn-light btn-sm btn-outline-secondary" type="button">
			    	<i class="bi bi-search text-dark"></i>
			    </button>
			</div>
		  </div>
          <div class="form-group">
            <label for="select-pvzs-cdek-city" class="col-form-label"><fmt:message key="order.form.modal.pvzs.cdek.fields.city" /></label>            
            <select id="select-pvzs-cdek-city" class="form-control form-control-sm"></select>
          </div>          
          <div class="form-group">
            <label for="textarea-pvzs-cdek" class="col-form-label"><fmt:message key="order.form.modal.pvzs.cdek.fields.pvzs" /></label>
            <textarea id="textarea-pvzs-cdek" class="form-control form-control-sm" rows="10"></textarea>
          </div>
          
        </form>        
      </div>
      <div class="modal-footer">
        <button id="button-modal-pvzs-cdek-cancel" type="button" class="btn btn-light" data-dismiss="modal"><fmt:message key="main.btn.cancel" /></button>
      </div>
    </div>
  </div>
</div>

<!-- cdek-widjet-modal -->
<div class="modal fade" id="cdek-widjet-modal" tabindex="-1" role="dialog" aria-labelledby="cdek-widjet-modal-title" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="cdek-widjet-modal-title"><fmt:message key="order.form.modal.cdek.widjet.header" /></h5> 
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">      
		<div id="forpvz" style="width: 1060px; height: 600px;"></div>
      </div>
      <div class="modal-footer">
        <button id="button-modal-delivery-cdek-cancel" type="button" class="btn btn-light" data-dismiss="modal"><fmt:message key="main.btn.cancel" /></button>        
      </div>
    </div>
  </div>
</div>

<br />
<div id="feedback"></div>
<div id="feedback-product"></div>

<%@ include file="../fragments/header2html2navbar2finish.jsp"%>

<!-- alert block ets -->

<%@ include file="../fragments/footer2init.jsp"%>
<!-- local java script -->
<script>
/*
var widjet = new ISDEKWidjet({
    defaultCity: '${cdekDefaultCity}',
    cityFrom: '${cdekCityFrom}',
    link: 'forpvz',
    path: 'https://pribormaster.ru/catalog/view/theme/zemez808/js/cdek-pvzwidget/scripts/',
    servicepath: 'https://pribormaster.ru/catalog/controller/extension/shipping/cdek/service.php' 
});
*/


	$('#btn-cdek-pvzs').click(function() {		
		openCdekPvzs();
	});
		
	$('.btn-cdek-widjet').click(function() {		
		openCdekWidjet();
	});
	
	$('#btn-save-order').click(function() {		
		$('#btn-submit-order').click();
	});
	
	function openCdekPvzs(element) {
		
		if ($('#input-delivery-address-carrierPvz-city').val().trim() == '') {
			$('#input-pvzs-cdek-city-search').val($('#input-delivery-address').val().trim());
		} else {
			$('#input-pvzs-cdek-city-search').val($('#input-delivery-address-carrierPvz-city').val());
		}	
		
		$('#pvzs-cdek-modal').modal({keyboard: false});
	}
	
	function openCdekWidjet(element) {
		if ($('#select-delivery-type').val() == 'CDEK_PVZ_TYPICAL' || $('#select-delivery-type').val() == 'CDEK_PVZ_ECONOMY') {
			$('#btn-calc-parcel-delivery-amounts-info').popover('hide');
			
			//widjet.open();
			var cityCheck = $('#input-delivery-address-carrierPvz-city').val();			
			if (cityCheck == "" || cityCheck == null) {
				cityCheck = '${cdekDefaultCity}';
			}
			//console.log('openCdekWidjet: <' + cityCheck + '>');
			
			var widjet = new ISDEKWidjet({
			    path: 'https://pribormaster.ru/catalog/view/theme/zemez808/js/cdek-pvzwidget/scripts/',
			    servicepath: 'https://pribormaster.ru/catalog/controller/extension/shipping/cdek/service.php',
			    choose: true,
			    country: '${cdekDefaultCountry}',
			    defaultCity: cityCheck, // $('#input-delivery-address-carrierPvz-city').val
			    cityFrom: '${cdekCityFrom}',
			    link: 'forpvz',
			    //link: false,
			    popup: true,
			    hidedress: true,
			    hidecash: true,
			    hidedelt: false,
			    detailAddress: true,
			    onReady: onCdekWidjetReady,
			    onChoose: onCdekWidjetChoose,
			    onChooseProfile: onCdekWidjetChooseProfile,
			    onCalculate: onCdekWidjetCalculate
			});	
			$('#cdek-widjet-modal').modal({keyboard: false});			
		}		
	}
	
	function onCdekWidjetReady() {
	   //alert('widjet loaded');
	}
	
	function onCdekWidjetChoose(wat) {
	    /*
	    alert(
	        'Set PVZ ' + wat.id + "\n" +
	        'цена ' + wat.price + "\n" +
	        'срок ' + wat.term + " дн.\n" +
	        'город ' + wat.cityName + ', код города ' + wat.city
	    );
	    )
	    */    
	    //var cityId = $('#input-delivery-address-carrierPvz-cityId').val();
	    $('#input-delivery-address-carrierPvz-city').val(wat.cityName);
	    $('#input-delivery-address-carrierPvz-cityId').val(wat.city);
	    $('#input-delivery-address').val(wat.cityName + ", " + wat.PVZ.Address);
	    $('#input-delivery-address-carrierPvz-code').val(wat.id);
	    $('#input-delivery-address-carrierPvz-street').val(wat.PVZ.Address);
		$('#input-delivery-address-carrierPvz-house').val("");
		$('#input-delivery-address-carrierPvz-flat').val("");
		
		var data = {}, 
		orderItems = [], 
		orderItem;
		$('.input-details-item-product').each(function() {
			
			var indexItem = $(this).attr('id');
			if (indexItem != 'input-details-item-product-hidden') {				
				indexItem = indexItem.substr('input-details-item-product'.length + 1, 2);	
				//console.log(indexItem);
				orderItem = {
						product: {
							id: $('#input-details-item-product-id-' + indexItem).val().toNumber(), 
							name: $('#input-details-item-product-' + indexItem).val()
						},
						quantity: $('#input-details-item-quantity-' + indexItem).val().toNumber(),
						price: $('#input-details-item-price-' + indexItem).val().toNumber(),
						supplierAmount: $('#input-details-item-supplier-amount-' + indexItem).val().toNumber(),
						amount: $('#input-details-item-amount-' + indexItem).val().toNumber()}
				orderItems.push(orderItem);				
			}
	
		});
				
		var orderContainer = {
				orderType: $('#select-order-type').val(),
				paymentType: $('#select-payment-type').val(),
				delivery: {
					price: $('#input-delivery-price').val().toNumber(),
					factCustomerPrice: $('#input-delivery-price').val().toNumber(),
					deliveryType: $('#select-delivery-type').val(),
					deliveryPrice: $('#select-delivery-price-type').val(),
					paymentDeliveryType: $('#select-payment-delivery-type').val(),
					address: {
						cdekInfo: {
							cityContext: wat.cityName,
							cityId: wat.city,							
							pvz: wat.id							
						},
						address: $('#input-delivery-address').val()
					}
				},
				amounts: {
					postpay: $('#input-amounts-postpay').val().toNumber()
				},
    			items: orderItems
    	}
		
		
		$.ajax({
			type: 'POST',
			contentType: 'application/json',
			url: '${urlHome}ajax/orders/calc/parcel-delivery-amounts',
			data : JSON.stringify(orderContainer),
			dataType: 'json',
			timeout: 100000,
			success: function(data) {
				console.log('SUCCESS: ', data);
				display(data);
				$('#input-amounts-postpay').val(data.result.deliveryServiceResult.postpayAmount);
				$('#input-delivery-price').val(data.result.deliveryServiceResult.deliveryFullPrice);
				$('#input-delivery-customer-price').val(data.result.deliveryServiceResult.deliveryCustomerSummary);
				$('#input-delivery-seller-price').val(data.result.deliveryServiceResult.deliverySellerSummary);
				
				$('#details-items-amounts-delivery-seller-summary').text(data.result.deliveryServiceResult.deliverySellerSummary.formatMoney(2));
				$('#details-items-amounts-delivery-customer-summary').text(data.result.deliveryServiceResult.deliveryCustomerSummary.formatMoney(2));
												
				$('#btn-calc-parcel-delivery-amounts-info').attr('data-original-title', data.result.deliveryServiceResult.parcelType);
				$('#btn-calc-parcel-delivery-amounts-info').attr('data-content', data.result.deliveryServiceResult.info);				
				$('#btn-calc-parcel-delivery-amounts-info').popover('show');
				
				$('#input-delivery-price').focusout();
				
			},
			error: function(e) {
				console.log('ERROR: ', e);
				display(e);
			},
			done: function(e) {
				console.log('DONE');
			}
		});			

	    $('#cdek-widjet-modal').modal('hide');	
	}
	
	function onCdekWidjetChooseProfile(wat) {
		/*
	    alert(
	        'Set courier in city ' + wat.cityName + ', код города ' + wat.city + "\n" +
	        'цена ' + wat.price + "\n" +
	        'срок ' + wat.term + ' дн.'
	    );
		*/
		//$('#cdek-widjet-modal').modal('hide');
	}
	
	function onCdekWidjetCalculate(wat) {
	    //alert('calc delivery finished');
	}
	$('#nav-link-orders').addClass('active');
	
<c:choose>
	<c:when test="${orderForm['new']}">	
	var isNew = true;
	</c:when>
	<c:otherwise>
	var isNew = false;
	</c:otherwise>
</c:choose>	
	
	if ($('#check-delivery-customer-equals-recipient').prop('checked')) {
		 $('.input-delivery-recipient').prop('disabled', true);	
	} else {
		$('.input-delivery-recipient').prop('disabled', false);
	}		
	
	var customerType = $('#select-customer-type').val();
	$('.radio-customer-type').prop('checked', false);
	$('#customer-types-' + customerType).prop('checked', true);	
	$('.form-row-select-customer-types').prop('hidden', true);
	
	$('.delivery-address-carrierPvz').attr('type', 'hidden');
	
	selectCustomerTypeOnChange();		
	
	$('.radio-customer-type').change(function() {
		selectCustomerTypeOnChange();
	});
	
	$('.input-delivery-cdek-address-part').change(function() {
		inputDeliveryCdekChange();
	});
	
	$('#select-order-type').change(function() {
		var orderType = $('#select-order-type').val();
		if (orderType == 'BILL') {
			$('#select-payment-type').val('PREPAYMENT');			
		} else if (orderType == 'ORDER') {
			$('#select-payment-type').val('POSTPAY');			
		} 		
	});		
	
	$('#check-delivery-customer-equals-recipient').change(function() {		
		var check = $('#check-delivery-customer-equals-recipient').prop('checked');
		if (check) {
			 $('.input-delivery-recipient').prop('disabled', true);
			
		} else {
			$('.input-delivery-recipient').prop('disabled', false);
		}		
	});	

	$('#select-payment-type').change(function() {
		var paymentType = $('#select-payment-type').val();
		if (paymentType == 'POSTPAY') {
			$('#select-delivery-type').val('CDEK_PVZ_TYPICAL');
			$('#input-delivery-trackCode').prop('disabled', false);
			$('#input-amounts-postpay').prop('disabled', false);
		} else if (paymentType == 'PREPAYMENT') {
			$('#select-delivery-type').val('CDEK_PVZ_TYPICAL');		
			$('#input-delivery-trackCode').prop('disabled', false);
			$('#input-amounts-postpay').prop('disabled', true);
		} else if (paymentType == 'PAYMENT_COURIER') {
			$('#select-delivery-type').val('COURIER_MOSCOW_TYPICAL');
			$('#input-delivery-trackCode').prop('disabled', true);
			$('#input-amounts-postpay').prop('disabled', true);
		} 
	});		
	
	$('#select-delivery-type').change(function() {		
		var deliveryType = $('#select-delivery-type').val();
		
		$('#input-delivery-address').prop('disabled', false);
		$('.btn-cdek-widjet').prop('disabled', true);
		$('#btn-cdek-pvzs').prop('disabled', true);
		
		if (deliveryType == 'CDEK_PVZ_TYPICAL' || deliveryType == 'CDEK_PVZ_ECONOMY') {
			$('.btn-cdek-widjet').prop('disabled', false);	
			$('#btn-cdek-pvzs').prop('disabled', false);
		} 				
		if (deliveryType == 'CDEK_PVZ_TYPICAL' || deliveryType == 'CDEK_PVZ_ECONOMY' || deliveryType == 'CDEK_COURIER' || deliveryType == 'CDEK_COURIER_ECONOMY') {
			$('#input-delivery-trackCode').prop('disabled', false);			
			if ($('#select-payment-type').val() == 'PAYMENT_COURIER') {
				$('#select-payment-type').val('POSTPAY');
			}						
		} else if (deliveryType == 'OZON_ROCKET_PICKPOINT' || deliveryType == 'OZON_ROCKET_POSTAMAT') {			
			$('btn-calc-parcel-delivery-amounts').prop('disabled', false);
			$('#input-delivery-trackCode').prop('disabled', false);				
		} else if (deliveryType == 'OZON_ROCKET_COURIER') {			
			$('btn-calc-parcel-delivery-amounts').prop('disabled', true);
			$('#input-delivery-trackCode').prop('disabled', false);			
		} else if (deliveryType == 'DELLIN') {
			$('#input-delivery-trackCode').prop('disabled', false);
			if ($('#select-payment-type').val() == 'PAYMENT_COURIER') {
				$('#select-payment-type').val('POSTPAY');
			}
		} else if (deliveryType == 'COURIER_MOSCOW_TYPICAL' || deliveryType == 'COURIER_MOSCOW_FAST' || deliveryType == 'COURIER_MO_TYPICAL') {
			$('#input-delivery-trackCode').prop('disabled', true);
			//$('#select-payment-type').val('PAYMENT_COURIER');
		} else if (deliveryType == 'POST_TYPICAL' || deliveryType == 'POST_I_CLASS' || deliveryType == 'POST_EMS') {
			$('#input-delivery-trackCode').prop('disabled', false);
			if ($('#select-payment-type').val() == 'PAYMENT_COURIER') {
				$('#select-payment-type').val('POSTPAY');
			}
		} else if (deliveryType == 'PICKUP') {
			$('#input-delivery-trackCode').prop('disabled', false);	
			$('#input-delivery-address').prop('disabled', true);
			$('#input-delivery-address').val('');
			if ($('#select-payment-type').val() == 'PAYMENT_COURIER') {
				$('#select-payment-type').val('POSTPAY');
			}
		}
		findDeliveryPricesByDeliveryType(deliveryType);
	});	
	
	$('#select-delivery-price-type').change(function() {		
		var price = $('#select-delivery-price-type option:selected').attr('price');
		$('#input-delivery-price').val(price);
	});
	
	$('#select-delivery-cdek-city').change(function() {		
		findDeliveryCdekPvz(this);
	});	
	
	$('#select-pvzs-cdek-city').change(function() {		
		findPvzsCdekPvzs(this);
	});	

	$('#btn-show-order').click(function() {
		window.location.href = '${urlHome}orders/${order.id}/${listType}';
	});
	$('#btn-update-order').click(function() {
		window.location.href = '${urlHome}orders/${order.id}/update';
	});	
	$('#btn-status-order').click(function() {
		window.location.href = '${urlHome}orders/${order.id}/change-status/${listType}';
	});
	$('#btn-clone-order').click(function() {
		window.location.href = '${urlHome}orders/${order.id}/clone';
	});	
	$('#btn-calc-total-amounts').click(function() {
		calcTotalAmounts(this);
	});	
	
	$('#btn-find-customer-phone').click(function() {
		findCustomerByFields();
	});	
	
	$('.btn-calc-parcel-delivery-amounts').click(function() {
		$('#btn-calc-parcel-delivery-amounts-info').popover('hide');
		calcParcelDeliveryAmounts();
	});
			
	$('.form-control-customer-find').focusout(function() {
		findCustomerByFields();
	});	
	
	$('#input-delivery-price').focusout(function() {
		console.log('#input-delivery-price focusout: 1001');
		
		if ($('#select-payment-delivery-type').val() == 'CUSTOMER') {
			$('#input-delivery-customer-price').val($('#input-delivery-price').val());
			if ($('#select-delivery-type').val() == 'CDEK_PVZ_TYPICAL' || $('#select-delivery-type').val() == 'CDEK_PVZ_ECONOMY' || $('#select-delivery-type').val() == 'CDEK_COURIER' || $('#select-delivery-type').val() == 'CDEK_COURIER_ECONOMY') {
				// ...
			} else {				
				$('#input-delivery-seller-price').val($('#input-delivery-price').val());				
			}			
			
		} else {
			$('#input-delivery-customer-price').val('0');
			$('#input-delivery-seller-price').val($('#input-delivery-price').val());
			
		}		
		$('#details-items-amounts-delivery-seller-summary').text($('#input-delivery-seller-price').val().toNumber().formatMoney(2));
		$('#details-items-amounts-delivery-customer-summary').text($('#input-delivery-customer-price').val().toNumber().formatMoney(2));
		
		calcTotalAmounts($('#input-delivery-price'));			
	});
	
	$('#btn-find-delivery-cdek-city').click(function() {
		findDeliveryCdekCities(this);
	});
	
	$('#btn-find-pvzs-cdek-city').click(function() {
		findPvzsCdekCities(this);
	});
	
	$('#button-modal-delivery-cdek-ok').click(function() {
		
		var cityContext = $('#input-delivery-cdek-city-search').val(),
			cityId = $('#select-delivery-cdek-city').find('option:selected').attr('city-id'),
			pvz = $('#select-delivery-cdek-pvz').find('option:selected').attr('pvz-code'),
			street = $('#input-delivery-cdek-street').val(),
			house = $('#input-delivery-cdek-house').val(),
			flat = $('#input-delivery-cdek-flat').val(),
			address = $('#input-delivery-cdek-city-address').val();
		
		$('#input-delivery-address-carrierPvz-city').val(cityContext);
		$('#input-delivery-address-carrierPvz-cityId').val(cityId);		
		$('#input-delivery-address-carrierPvz-code').val(pvz);		
		$('#input-delivery-address-carrierPvz-street').val(street);
		$('#input-delivery-address-carrierPvz-house').val(house);
		$('#input-delivery-address-carrierPvz-flat').val(flat);
			
		if (($('#select-delivery-type').val() == 'CDEK_COURIER' || $('#select-delivery-type').val() == 'CDEK_COURIER_ECONOMY') && address != '') {
			$('#input-delivery-address').val(address);
		} else if ($('#select-delivery-type').val() == 'CDEK_PVZ_TYPICAL' || $('#select-delivery-type').val() == 'CDEK_PVZ_ECONOMY') {
			$('#input-delivery-address').val($('#select-delivery-cdek-pvz').find('option:selected').attr('pvz-name'));
		}		
		
		var data = {}, 
		orderItems = [], 
		orderItem;
		$('.input-details-item-product').each(function() {
			
			var indexItem = $(this).attr('id');
			if (indexItem != 'input-details-item-product-hidden') {				
				indexItem = indexItem.substr('input-details-item-product'.length + 1, 2);	
				//console.log(indexItem);
				orderItem = {
						product: {
							id: $('#input-details-item-product-id-' + indexItem).val().toNumber(), 
							name: $('#input-details-item-product-' + indexItem).val()
						},
						quantity: $('#input-details-item-quantity-' + indexItem).val().toNumber(),
						price: $('#input-details-item-price-' + indexItem).val().toNumber(),
						supplierAmount: $('#input-details-item-supplier-amount-' + indexItem).val().toNumber(),
						amount: $('#input-details-item-amount-' + indexItem).val().toNumber()}
				orderItems.push(orderItem);				
			}
	
		});
		
		var orderContainer = {
				orderType: $('#select-order-type').val(),
				paymentType: $('#select-payment-type').val(),
				delivery: {
					price: $('#input-delivery-price').val().toNumber(),
					factCustomerPrice: $('#input-delivery-price').val().toNumber(),
					deliveryType: $('#select-delivery-type').val(),
					deliveryPrice: $('#select-delivery-price-type').val(),
					paymentDeliveryType: $('#select-payment-delivery-type').val(),
					address: {
						carrierInfo: {
							cityContext: cityContext,
							cityId: cityId,							
							pvz: pvz							
						},
						address: $('#input-delivery-address').val()
					}
				},
				amounts: {
					postpay: $('#input-amounts-postpay').val().toNumber()
				},
    			items: orderItems
    	}
		
		
		$.ajax({
			type: 'POST',
			contentType: 'application/json',
			url: '${urlHome}ajax/orders/calc/parcel-delivery-amounts',
			data : JSON.stringify(orderContainer),
			dataType: 'json',
			timeout: 100000,
			success: function(data) {
				console.log('SUCCESS: ', data);
				display(data);
				$('#input-amounts-postpay').val(data.result.deliveryServiceResult.postpayAmount);
				$('#input-delivery-price').val(data.result.deliveryServiceResult.deliveryFullPrice);
				$('#input-delivery-customer-price').val(data.result.deliveryServiceResult.deliveryCustomerSummary);
				$('#input-delivery-seller-price').val(data.result.deliveryServiceResult.deliverySellerSummary);
				
				$('#details-items-amounts-delivery-seller-summary').text(data.result.deliveryServiceResult.deliverySellerSummary.formatMoney(2));
				$('#details-items-amounts-delivery-customer-summary').text(data.result.deliveryServiceResult.deliveryCustomerSummary.formatMoney(2));
												
				$('#btn-calc-parcel-delivery-amounts-info').attr('data-original-title', data.result.deliveryServiceResult.parcelType);
				$('#btn-calc-parcel-delivery-amounts-info').attr('data-content', data.result.deliveryServiceResult.info);				
				$('#btn-calc-parcel-delivery-amounts-info').popover('show');
				
				$('#input-delivery-price').focusout();
				
			},
			error: function(e) {
				console.log('ERROR: ', e);
				display(e);
			},
			done: function(e) {
				console.log('DONE');
			}
		});		
		
		$('#delivery-cdek-modal').modal('hide');							
	});
		
	$('#button-modal-delivery-ozon-rocket-ok').click(function() {
		
		var cityContext = $('#input-delivery-address-carrierPvz-city').val(),
			cityId = 0,
			pvz = 0,
			street = "",
			house = "",
			flat = "",
			address = $('#input-ozon-rocket-delivery-variant-address').val();
				 
		var deliveryVariantId = 0;
		if ($('#select-delivery-type').val() != 'OZON_ROCKET_COURIER') {
			deliveryVariantId = $('#input-ozon-rocket-delivery-variant-id').val();
		}		 
				 				
		$('#input-delivery-address-carrierPvz-city').val(cityContext);
		$('#input-delivery-address-carrierPvz-cityId').val(cityId);		
		$('#input-delivery-address-carrierPvz-code').val(pvz);		
		$('#input-delivery-address-carrierPvz-street').val(street);
		$('#input-delivery-address-carrierPvz-house').val(house);
		$('#input-delivery-address-carrierPvz-flat').val(flat);
		$('#input-delivery-address-carrierPvz-deliveryVariantId').val(deliveryVariantId);
		
		$('#input-delivery-address').val(address);
		
		var data = {}, 
		orderItems = [], 
		orderItem;
		$('.input-details-item-product').each(function() {			
			var indexItem = $(this).attr('id');
			if (indexItem != 'input-details-item-product-hidden') {				
				indexItem = indexItem.substr('input-details-item-product'.length + 1, 2);	
				//console.log(indexItem);
				orderItem = {
						product: {
							id: $('#input-details-item-product-id-' + indexItem).val().toNumber(), 
							name: $('#input-details-item-product-' + indexItem).val()
						},
						quantity: $('#input-details-item-quantity-' + indexItem).val().toNumber(),
						price: $('#input-details-item-price-' + indexItem).val().toNumber(),
						supplierAmount: $('#input-details-item-supplier-amount-' + indexItem).val().toNumber(),
						amount: $('#input-details-item-amount-' + indexItem).val().toNumber()}
				orderItems.push(orderItem);				
			}
	
		});
		
		var orderContainer = {
				/*
				customer: {
					customerType: $('.radio-customer-type:checked').val()
				},
				*/
				orderType: $('#select-order-type').val(),
				paymentType: $('#select-payment-type').val(),
				delivery: {
					price: $('#input-delivery-price').val().toNumber(),
					factCustomerPrice: $('#input-delivery-price').val().toNumber(),
					deliveryType: $('#select-delivery-type').val(),
					deliveryPrice: $('#select-delivery-price-type').val(),
					paymentDeliveryType: $('#select-payment-delivery-type').val(),
					address: {
						carrierInfo: {
							cityContext: cityContext,
							cityId: cityId,							
							pvz: pvz,
							deliveryVariantId: deliveryVariantId
						},
						address: $('#input-delivery-address').val()
					}
				},
				amounts: {
					postpay: $('#input-amounts-postpay').val().toNumber()
				},
    			items: orderItems
    	}		
		
		$.ajax({
			type: 'POST',
			contentType: 'application/json',
			url: '${urlHome}ajax/orders/calc/parcel-delivery-amounts',
			data : JSON.stringify(orderContainer),
			dataType: 'json',
			timeout: 100000,
			success: function(data) {
				console.log('SUCCESS: ', data);
				display(data);
				
				for (var key in data.result.deliveryServiceResult.addresses) {
		
					$('#input-delivery-address-carrierPvz-city').val(data.result.deliveryServiceResult.addresses[key].carrierInfo.cityContext);
					$('#input-delivery-address-carrierPvz-cityId').val(data.result.deliveryServiceResult.addresses[key].carrierInfo.cityId);		
					$('#input-delivery-address-carrierPvz-code').val("");		
					$('#input-delivery-address-carrierPvz-street').val(data.result.deliveryServiceResult.addresses[key].carrierInfo.street);
					$('#input-delivery-address-carrierPvz-house').val(data.result.deliveryServiceResult.addresses[key].carrierInfo.house);
					$('#input-delivery-address-carrierPvz-flat').val(data.result.deliveryServiceResult.addresses[key].carrierInfo.flat);
									
					$('#input-delivery-address').val(data.result.deliveryServiceResult.addresses[key].address);
				
				}				
				
				$('#input-amounts-postpay').val(data.result.deliveryServiceResult.postpayAmount);
				$('#input-delivery-price').val(data.result.deliveryServiceResult.deliveryFullPrice);
				$('#input-delivery-customer-price').val(data.result.deliveryServiceResult.deliveryCustomerSummary);
				$('#input-delivery-seller-price').val(data.result.deliveryServiceResult.deliverySellerSummary);
				
				$('#details-items-amounts-delivery-seller-summary').text(data.result.deliveryServiceResult.deliverySellerSummary.formatMoney(2));
				$('#details-items-amounts-delivery-customer-summary').text(data.result.deliveryServiceResult.deliveryCustomerSummary.formatMoney(2));
												
				$('#btn-calc-parcel-delivery-amounts-info').attr('data-original-title', data.result.deliveryServiceResult.parcelType);
				$('#btn-calc-parcel-delivery-amounts-info').attr('data-content', data.result.deliveryServiceResult.info);				
				$('#btn-calc-parcel-delivery-amounts-info').popover('show');
				
				$('#input-delivery-price').focusout();

				
			},
			error: function(e) {
				console.log('ERROR: ', e);
				display(e);
			},
			done: function(e) {
				console.log('DONE');
			}
		});			
		
		$('#delivery-ozon-rocket-modal').modal('hide');							
	});
	
	$('.btn-find-details-item-product').on('click', {selector: this}, btnDetailsItemProductOnClick);

	
	$('.input-details-item-price').on('change', {selector: this}, inputItemPriceChange);
	$('.input-details-item-quantity').on('change', {selector: this}, inputItemPriceChange);
	$('.input-details-item-discount-rate').on('change', {selector: this}, inputItemPriceChange);
	
	$('.input-calc-amounts').on('focusout', {selector: this}, calcTotalAmounts);
	
<c:choose>
	<c:when test="${orderForm['new']}">	
		$('.radio-customer-type').prop('disabled', false);
	</c:when>
	<c:otherwise>
		calcTotalAmounts(this);		
		$('.radio-customer-type').prop('disabled', true);
	</c:otherwise>
</c:choose>		

	function selectCustomerTypeOnChange() {		
		var customerType = $('.radio-customer-type:checked').val();
				
		$('#select-customer-type').val(customerType);		
		if (customerType == 'CUSTOMER') {
			$('#header-group-customer').text('<fmt:message key="order.form.headers.customer.person" />');
			
			$('.form-group-customer').prop('hidden', false);
			$('.form-group-company').prop('hidden', true);
			$('#select-customer-country').attr('disabled', true);
			$('#select-customer-country').val('RUSSIA');
			
			$('#select-delivery-address-country').attr('disabled', true);
			$('#select-delivery-address-country').val('RUSSIA');
			$('.input-mask-phone').mask('(000) 000-00-00');
			
		} else if (customerType == 'COMPANY') {
			$('#header-group-customer').text('<fmt:message key="order.form.headers.customer.company" />');			
			$('.form-group-customer').prop('hidden', true);
			$('.form-group-company').prop('hidden', false);			
			$('#select-customer-country').attr('disabled', true);
			$('#select-customer-country').val('RUSSIA');
			
			$('#select-delivery-address-country').attr('disabled', true);
			$('#select-delivery-address-country').val('RUSSIA');
			$('.input-mask-phone').mask('(000) 000-00-00');
			
		} else if (customerType == 'BUSINESSMAN') {
			$('#header-group-customer').text('<fmt:message key="order.form.headers.customer.businessman" />');
			
			$('.form-group-customer').prop('hidden', true);
			$('.form-group-company').prop('hidden', false);			
			$('#select-customer-country').attr('disabled', true);
			$('#select-customer-country').val('RUSSIA');
			
			$('#select-delivery-address-country').attr('disabled', true);
			$('#select-delivery-address-country').val('RUSSIA');
			$('.input-mask-phone').mask('(000) 000-00-00');
			
		} else if (customerType == 'FOREIGNER_CUSTOMER') {
			$('#header-group-customer').text('<fmt:message key="order.form.headers.customer.foreignerCustomer" />');
			
			$('.form-group-customer').prop('hidden', false);
			$('.form-group-company').prop('hidden', true);
			
			$('#select-customer-country').attr('disabled', false);
			$('#input-customer-phone').unmask();
			
			if (isNew) {
				$('#select-customer-country').val('KAZAKHSTAN');
				$('#select-delivery-address-country').attr('disabled', false);
				$('#select-delivery-address-country').val('KAZAKHSTAN');
			} else {
				
			}			
		} else if (customerType == 'FOREIGNER_COMPANY') {
			$('#header-group-customer').text('<fmt:message key="order.form.headers.customer.foreignerCompany" />');			
			$('.form-group-customer').prop('hidden', true);
			$('.form-group-company').prop('hidden', false);			
			$('#select-customer-country').attr('disabled', false);
			$('#input-customer-contact-phone').unmask();
			if (isNew) {
				$('#select-customer-country').val('KAZAKHSTAN');				
				$('#select-delivery-address-country').attr('disabled', false);
				$('#select-delivery-address-country').val('KAZAKHSTAN');				
			} else {
				
			}			
		} 
		nextPhoneNumber(customerType);
	}
	
	function btnDetailsItemDeleteOnClick(event) {
		var id = $(this).attr('id');
		
		if (typeof(id) == "string") {
			id = id.substr('btn-details-item-delete-'.length, id.length);	
		} else {
			id = $(event).attr('id');
			id = id.substr('btn-details-item-delete-'.length, id.length);	
		}		
		console.log(id);
		$('#details-item-row-' + id).remove();		
	}
	
	
	function nextPhoneNumber(customerType) {		
		if (customerType == 'CUSTOMER') {			
			return;
		} else if (customerType == 'COMPANY') {
			if ($('#input-customer-contact-phone').val() != '') {
				return;
			}			
		} else if (customerType == 'BUSINESSMAN') {
			if ($('#input-customer-contact-phone').val() != '') {
				return;
			}			
		} else if (customerType == 'FOREIGNER_CUSTOMER') {
			if ($('#input-customer-phone').val() != '') {
				return;
			}							
		} else if (customerType == 'FOREIGNER_COMPANY') {
			if ($('#input-customer-contact-phone').val() != '') {
				return;
			}
		}
		$.ajax({
			type: 'POST',
			contentType: 'application/json',
			url: '${urlHome}ajax/customer/search/next-phone-number',
			data: JSON.stringify(""),
			dataType: 'json',
			timeout: 100000,
			success: function(data) {
				console.log('SUCCESS: ', data);
				display(data);
				
				if (customerType == 'FOREIGNER_CUSTOMER') {
					$('#input-customer-phone').val(data.msg);
				} else {
					$('#input-customer-contact-phone').val(data.msg);
				}
			},
			error: function(e) {
				console.log('ERROR: ', e);
				display(e);

			},
			done: function(e) {
				console.log('DONE');				
			}
		});			
	}
	
	function findDeliveryPricesByDeliveryType(deliveryType) {
		
		$.ajax({
			type: 'POST',
			contentType: 'application/json',
			url: '${urlHome}ajax/wiki/search/find-price-by-delivery-types',
			data: JSON.stringify(deliveryType),
			dataType: 'json',
			timeout: 100000,
			success: function(data) {
				console.log('SUCCESS: ', data);
				display(data);
				if (data.code == '200') {
					$('#select-delivery-price-type option').remove();
					for (var key in data.result.deliveryPrices) {						
						$('#select-delivery-price-type').append('<option value=' + data.result.deliveryPrices[key].code + ' price=' + data.result.deliveryPrices[key].price + ' id="option-delivery-price-type-' + data.result.deliveryPrices[key].code + '">' + data.result.deliveryPrices[key].annotation + '</option>');
					}
					if (data.result.deliveryPrices.length > 0) {
						$("#select-delivery-price-type").val($("#select-delivery-price-type option:first").val());
						$('#input-delivery-price').val(data.result.deliveryPrices[0].price);
					} else {
						//$('#input-delivery-price').val('0');
					}
				}				
			},
			error: function(e) {
				console.log('ERROR: ', e);
				display(e);

			},
			done: function(e) {
				console.log('DONE');
				
			}
		});			
	}
	
	function findCustomerByFields() {
		
		var customerConditions = {
				customerType: $('#select-customer-type').val(),
				personPhoneNumber: $('#input-customer-phone').val(),
				personEmail: $('#input-customer-email').val(),
				personLastName: $('#input-customer-first-name').val(),
				companyInn: $('#input-customer-inn').val(), 
				companyShortName: $('#input-customer-short-name').val(),
				companyMainContactPhoneNumber: $('#input-customer-contact-phone').val(),
				companyMainContactEmail: $('#input-customer-contact-email').val()
    	}	
		
		if ((customerConditions.personPhoneNumber == null || customerConditions.personPhoneNumber.trim() == '') &&
			(customerConditions.personEmail == null || customerConditions.personEmail.trim() == '') &&
			(customerConditions.companyInn == null || customerConditions.companyInn.trim() == '') &&
			(customerConditions.companyMainContactPhoneNumber == null || customerConditions.companyMainContactPhoneNumber.trim() == '') &&
			(customerConditions.companyMainContactEmail == null || customerConditions.companyMainContactEmail.trim() == '')) {
			return;
		}
		
		$.ajax({
			type: 'POST',
			contentType: 'application/json',
			url: '${urlHome}ajax/cusomer/search/find-customer-by-conditions',
			data: JSON.stringify(customerConditions),
			dataType: 'json',
			timeout: 100000,
			success: function(data) {
				console.log('SUCCESS: ', data);
				display(data);
				if (data.code == '200') {
					$('#formCustomer.id').val(data.result.formCustomer.id);
					
					// CUSTOMER
					
					setDefaultValue('#input-customer-first-name', data.result.formCustomer.firstName);
					setDefaultValue('#input-customer-middle-name', data.result.formCustomer.middleName);
					setDefaultValue('#input-customer-last-name', data.result.formCustomer.lastName);
					setDefaultValue('#input-customer-address', data.result.formCustomer.mainAddress.address);
					setDefaultValue('#input-customer-email', data.result.formCustomer.email);			
					setDefaultValue('#customer.id', data.result.formCustomer.id);
					
					// COMPANY
					setDefaultValue('#input-customer-inn', data.result.formCustomer.inn); 
					setDefaultValue('#input-customer-short-name', data.result.formCustomer.shortName);
					setDefaultValue('#input-customer-long-name', data.result.formCustomer.longName);
					setDefaultValue('#input-customer-contact-first-name', data.result.formCustomer.mainContact.firstName);
					setDefaultValue('#input-customer-contact-middle-name', data.result.formCustomer.mainContact.middleName);
					setDefaultValue('#input-customer-contact-last-name', data.result.formCustomer.mainContact.lastName);
					setDefaultValue('#input-customer-contact-phone', data.result.formCustomer.mainContact.phoneNumber);
					setDefaultValue('#input-customer-contact-email', data.result.formCustomer.mainContact.email);

					$('#header-customer-longName').html(data.result.formCustomer.viewLongName + '&nbsp;' + '<span class="badge badge-secondary">' + data.result.formCustomer.id + '</span>');
					
					$('.dropdown-menu-customer-order').remove();										
					for(var order in data.result.formShortInfoOrders) {
						console.log(order, data.result.formShortInfoOrders[order]);
						$('.dropdown-menu-customer-orders').append('<a class="dropdown-item dropdown-menu-customer-order" target="_blank" href="${urlHome}orders/' + data.result.formShortInfoOrders[order].id + '/${listType}">' + data.result.formShortInfoOrders[order].name + '</a>');
					}
					$('#btn-find-customer-orders').html('<fmt:message key="orders.btn.customerOrders" />&nbsp;<span class="badge badge-secondary">' + data.result.formShortInfoOrders.length + '</span>');
					
				<c:choose>
					<c:when test="${orderForm['new']}">					
					if (data.result.formShortInfoOrders.length >= 1) {
						$('#select-advert-type').val('REPEAT_CALL');
					}						
					</c:when>
					<c:otherwise>
					</c:otherwise>
				</c:choose>	
				
				}				
			},
			error: function(e) {
				console.log('ERROR: ', e);
				display(e);

			},
			done: function(e) {
				console.log('DONE');
				
			}
		});		
		
	}
	
	function setDefaultValue(selectorName, defaultValue) {
		if ($(selectorName).val() == '') {
			$(selectorName).val(defaultValue);	
		}		
	}
	
	function btnDetailsItemProductOnClick(element) {
		
		var inputProductId = $(this).attr('id');
		var itemId = inputProductId.substr('btn-find-details-item-product-'.length, inputProductId.length);
		
		var stringContext = $('#input-details-item-product-' + itemId).val();
		if (stringContext == null || stringContext.trim() == '') {
			return;
		}
		
		var data = {};
		//data["query"] = $("#query").val();
		
		btnSearchProductsDisabled(true);
		
		$.ajax({
			type: 'POST',
			contentType: 'application/json',
			url: '${urlHome}ajax/wiki/search/find-products-by-context',
			data: stringContext,
			dataType: 'json',
			timeout: 100000,
			success: function(data) {
				console.log('SUCCESS: ', data);
				displayProducts(data);
				
				if (data.code == '200') {
					if (data.result.products.length > 0) {
						$('#input-details-item-product-' + itemId).val(data.result.products[0].name);
						$('#input-details-item-product-id-' + itemId).val(data.result.products[0].id);
						$('#input-details-item-price-' + itemId).val(data.result.products[0].price);
						$('#input-details-item-supplier-amount-' + itemId).val(data.result.products[0].supplierPrice);
						if (itemId == 0 && data.result.products[0].category.id > 0 && $('#select-product-category').val() == 0) {
							$('#select-product-category').val(data.result.products[0].category.id);
						}											
						$('#datalist-product-name-' + itemId + ' option').remove();
						for (var key in data.result.products) {						
							  $('#datalist-product-name-' + itemId).append('<option>' + data.result.products[key].name + '</option>');
						}
						
						$('#span-details-item-product-sku-info-' + itemId).text(data.result.products[0].sku);
						
						$('#span-details-item-quantity-stock-info-' + itemId).text(data.result.products[0].viewStockQuantityText);						
						$('#span-details-item-quantity-stock-info-' + itemId).removeClass('badge-danger badge-warning badge-light').addClass('badge-' + data.result.products[0].viewStockQuantityClass);
						
						calcAmount(itemId);	
					}					
				} 
				btnSearchProductsDisabled(false);

			},
			error: function(e) {
				console.log('ERROR: ', e);
				displayProducts(e);
				btnSearchProductsDisabled(false);
			},
			done: function(e) {
				console.log('DONE');
				btnSearchProductsDisabled(false);
			}
		});			
	}	
	
	function findDeliveryCdekCities(element) {
		
		var contextString = $('#input-delivery-cdek-city-search').val().trim();
		if (contextString == '') {
			return;
		}
		
		$.ajax({
			type: 'POST',
			contentType: 'application/json',
			url: '${urlHome}ajax/orders/calc/parcel-delivery-cdek-cities',
			data : JSON.stringify(contextString),
			dataType: 'json',
			timeout: 100000,
			success: function(data) {
				console.log('SUCCESS: ', data);
				display(data);
				
				$('#select-delivery-cdek-city').find('option').remove();
				for (var key in data.result.deliveryServiceResult.addresses) {						
					$('#select-delivery-cdek-city').append('<option value=' + data.result.deliveryServiceResult.addresses[key].carrierInfo.cityContext + ' city-id=' + data.result.deliveryServiceResult.addresses[key].carrierInfo.cityId + ' id="option-delivery-cdek-city-' + data.result.deliveryServiceResult.addresses[key].carrierInfo.cityId + '">' + data.result.deliveryServiceResult.addresses[key].address + '</option>');
				}
				$('#select-delivery-cdek-city').change();
				
			},
			error: function(e) {
				console.log('ERROR: ', e);
				display(e);
			},
			done: function(e) {
				console.log('DONE');
			}
		});		
	}
	
    function findPvzsCdekCities(element) {
		
		var contextString = $('#input-pvzs-cdek-city-search').val().trim();
		if (contextString == '') {
			return;
		}
		
		$.ajax({
			type: 'POST',
			contentType: 'application/json',
			url: '${urlHome}ajax/orders/calc/parcel-delivery-cdek-cities',
			data : JSON.stringify(contextString),
			dataType: 'json',
			timeout: 100000,
			success: function(data) {
				console.log('SUCCESS: ', data);
				display(data);
				
				$('#select-pvzs-cdek-city').find('option').remove();
				for (var key in data.result.deliveryServiceResult.addresses) {						
					$('#select-pvzs-cdek-city').append('<option value=' + data.result.deliveryServiceResult.addresses[key].carrierInfo.cityContext + ' city-id=' + data.result.deliveryServiceResult.addresses[key].carrierInfo.cityId + ' id="option-delivery-cdek-city-' + data.result.deliveryServiceResult.addresses[key].carrierInfo.cityId + '">' + data.result.deliveryServiceResult.addresses[key].address + '</option>');
				}
				$('#select-pvzs-cdek-city').change();
				
			},
			error: function(e) {
				console.log('ERROR: ', e);
				display(e);
			},
			done: function(e) {
				console.log('DONE');
			}
		});		
	}
	
	$("#select-delivery-cdek-pvz").change(function() {
		
		var address = $('#select-delivery-cdek-pvz').find('option:selected').attr('pvz-name');
		//console.log(address);
		$('#input-delivery-cdek-city-address').val(address);
	});
	
	function findDeliveryCdekPvz(element) {
		
		var contextId = $('#select-delivery-cdek-city').find('option:selected').attr('city-id');
		if (contextId == '') {
			return;
		}
		
		$.ajax({
			type: 'POST',
			contentType: 'application/json',
			url: '${urlHome}ajax/orders/calc/parcel-delivery-cdek-pvzs',
			data : JSON.stringify(contextId),
			dataType: 'json',
			timeout: 100000,
			success: function(data) {
				console.log('SUCCESS: ', data);
				display(data);
				
				$('#select-delivery-cdek-pvz').find('option').remove();
				for (var key in data.result.deliveryServiceResult.addresses) {
					$('#select-delivery-cdek-pvz').append('<option value="' + data.result.deliveryServiceResult.addresses[key].carrierInfo.pvz + '" pvz-name="' + data.result.deliveryServiceResult.addresses[key].carrierInfo.cityContext + ', ' + data.result.deliveryServiceResult.addresses[key].carrierInfo.shortAddress +'" pvz-code="' + data.result.deliveryServiceResult.addresses[key].carrierInfo.pvz + '" id="option-delivery-cdek-pvz-' + data.result.deliveryServiceResult.addresses[key].carrierInfo.pvz + '">' + '[' + data.result.deliveryServiceResult.addresses[key].carrierInfo.pvz + '] ' + data.result.deliveryServiceResult.addresses[key].carrierInfo.fullAddress + '</option>');
				}
				
			},
			error: function(e) {
				console.log('ERROR: ', e);
				display(e);
			},
			done: function(e) {
				console.log('DONE');
			}
		});		
	}
	
	function findPvzsCdekPvzs(element) {
		
		var contextId = $('#select-pvzs-cdek-city').find('option:selected').attr('city-id');
		if (contextId == '') {
			return;
		}
		
		$.ajax({
			type: 'POST',
			contentType: 'application/json',
			url: '${urlHome}ajax/orders/calc/parcel-delivery-cdek-pvzs',
			data : JSON.stringify(contextId),
			dataType: 'json',
			timeout: 100000,
			success: function(data) {
				console.log('SUCCESS: ', data);
				display(data);
				
				//$('#textarea-pvzs-cdek').val('');
				var pvzs = "";
				for (var key in data.result.deliveryServiceResult.addresses) {
					var pvzInfo = "";
					
					if (data.result.deliveryServiceResult.addresses[key].carrierInfo.metroStation.trim() != "") {
						pvzInfo = pvzInfo + data.result.deliveryServiceResult.addresses[key].carrierInfo.metroStation.trim();
					}
					if (data.result.deliveryServiceResult.addresses[key].carrierInfo.nearestStation.trim() != "") {
						if (pvzInfo == "") {
							pvzInfo = data.result.deliveryServiceResult.addresses[key].carrierInfo.nearestStation.trim();
						} else {
							pvzInfo = pvzInfo + ", " + data.result.deliveryServiceResult.addresses[key].carrierInfo.nearestStation.trim();
						}
					}		
					if (data.result.deliveryServiceResult.addresses[key].carrierInfo.pvzType == "POSTAMAT") {
						if (pvzInfo == "") {
							pvzInfo = data.result.deliveryServiceResult.addresses[key].carrierInfo.pvzType;
						} else {
							pvzInfo = pvzInfo + ", " + data.result.deliveryServiceResult.addresses[key].carrierInfo.pvzType;
						}
						
					}					
					if (data.result.deliveryServiceResult.addresses[key].carrierInfo.phone.trim() != "") {
						if (pvzInfo == "") {
							pvzInfo = data.result.deliveryServiceResult.addresses[key].carrierInfo.phone.trim();
						} else {
							pvzInfo = pvzInfo + ", " + data.result.deliveryServiceResult.addresses[key].carrierInfo.phone.trim();
						}
					}
					if (data.result.deliveryServiceResult.addresses[key].carrierInfo.weightMax && data.result.deliveryServiceResult.addresses[key].carrierInfo.weightMax != "") {
						if (pvzInfo == "") {
							pvzInfo = data.result.deliveryServiceResult.addresses[key].carrierInfo.weightMax.trim();
						} else {
							pvzInfo = pvzInfo + ", " + data.result.deliveryServiceResult.addresses[key].carrierInfo.weightMax.trim();
						}
					}
					if (pvzInfo != "") {
						pvzInfo = ", (" + pvzInfo.trim() + ")";
					}
			
					pvzs += '[' + data.result.deliveryServiceResult.addresses[key].carrierInfo.pvz + '] ' + data.result.deliveryServiceResult.addresses[key].carrierInfo.shortAddress + pvzInfo + '\r\n';
				}
				$('#textarea-pvzs-cdek').val(pvzs);
				
			},
			error: function(e) {
				console.log('ERROR: ', e);
				display(e);
			},
			done: function(e) {
				console.log('DONE');
			}
		});		
	}
		
	function calcParcelDeliveryAmounts(element) {
		
		var data = {}, 
		orderItems = [], 
		orderItem;
		$('.input-details-item-product').each(function() {
			
			var indexItem = $(this).attr('id');
			if (indexItem != 'input-details-item-product-hidden') {				
				indexItem = indexItem.substr('input-details-item-product'.length + 1, 2);	
				//console.log(indexItem);
				orderItem = {
						product: {
							id: $('#input-details-item-product-id-' + indexItem).val().toNumber(), 
							name: $('#input-details-item-product-' + indexItem).val()
						},
						quantity: $('#input-details-item-quantity-' + indexItem).val().toNumber(),
						price: $('#input-details-item-price-' + indexItem).val().toNumber(),
						supplierAmount: $('#input-details-item-supplier-amount-' + indexItem).val().toNumber(),
						amount: $('#input-details-item-amount-' + indexItem).val().toNumber()}
				orderItems.push(orderItem);				
			}
	
		});
		
		var orderContainer = {
				orderType: $('#select-order-type').val(),
				paymentType: $('#select-payment-type').val(),
				delivery: {
					price: $('#input-delivery-price').val().toNumber(),
					deliveryType: $('#select-delivery-type').val(),
					deliveryPrice: $('#select-delivery-price-type').val(),
					paymentDeliveryType: $('#select-payment-delivery-type').val(),
					address: {
						address: $('#input-delivery-address').val()
					}
				},
				amounts: {
					postpay: $('#input-amounts-postpay').val().toNumber()
				},
    			items: orderItems
    	}
		
		if ($('#select-delivery-type').val() == 'CDEK_PVZ_TYPICAL' || $('#select-delivery-type').val() == 'CDEK_PVZ_ECONOMY' || $('#select-delivery-type').val() == 'CDEK_COURIER' || $('#select-delivery-type').val() == 'CDEK_COURIER_ECONOMY') {
			// cdek
			
			$('#delivery-cdek-modal-title').text('<fmt:message key="order.form.modal.delivery.cdek.header" />' + ' [' + $('#select-delivery-type').find('option:selected').text() + ']');
			
			if ($('#input-delivery-address-carrierPvz-city').val().trim() == '') {
				$('#input-delivery-cdek-city-search').val($('#input-delivery-address').val().trim());
			} else {
				$('#input-delivery-cdek-city-search').val($('#input-delivery-address-carrierPvz-city').val());
			}		
			$('#input-delivery-cdek-street').val($('#input-delivery-address-carrierPvz-street').val());
			$('#input-delivery-cdek-house').val($('#input-delivery-address-carrierPvz-house').val());
			$('#input-delivery-cdek-flat').val($('#input-delivery-address-carrierPvz-flat').val());
			
			$('#input-delivery-cdek-city-address').val($('#input-delivery-address').val());
		
			$('#delivery-cdek-modal').modal({keyboard: false});
			
		} else if ($('#select-delivery-type').val() == 'OZON_ROCKET_COURIER') {
			// ozon.rocket courier
			
			var cityContext = $('#input-delivery-address-carrierPvz-city').val(),
			cityId = 0,
			pvz = 0,
			street = "",
			house = "",
			flat = "",
			address = $('#input-delivery-address').val();
				 
			var deliveryVariantId = 0;
					 				
			$('#input-delivery-address-carrierPvz-city').val(cityContext);
			$('#input-delivery-address-carrierPvz-cityId').val(cityId);		
			$('#input-delivery-address-carrierPvz-code').val(pvz);		
			$('#input-delivery-address-carrierPvz-street').val(street);
			$('#input-delivery-address-carrierPvz-house').val(house);
			$('#input-delivery-address-carrierPvz-flat').val(flat);
			$('#input-delivery-address-carrierPvz-deliveryVariantId').val(deliveryVariantId);
			
	
			var data = {}, 
			orderItems = [], 
			orderItem;
			$('.input-details-item-product').each(function() {			
				var indexItem = $(this).attr('id');
				if (indexItem != 'input-details-item-product-hidden') {				
					indexItem = indexItem.substr('input-details-item-product'.length + 1, 2);	
					//console.log(indexItem);
					orderItem = {
							product: {
								id: $('#input-details-item-product-id-' + indexItem).val().toNumber(), 
								name: $('#input-details-item-product-' + indexItem).val()
							},
							quantity: $('#input-details-item-quantity-' + indexItem).val().toNumber(),
							price: $('#input-details-item-price-' + indexItem).val().toNumber(),
							supplierAmount: $('#input-details-item-supplier-amount-' + indexItem).val().toNumber(),
							amount: $('#input-details-item-amount-' + indexItem).val().toNumber()}
					orderItems.push(orderItem);				
				}
		
			});
			
			var orderContainer = {
					/*
					customer: {
						customerType: $('.radio-customer-type:checked').val()
					},
					*/
					orderType: $('#select-order-type').val(),
					paymentType: $('#select-payment-type').val(),
					delivery: {
						price: $('#input-delivery-price').val().toNumber(),
						factCustomerPrice: $('#input-delivery-price').val().toNumber(),
						deliveryType: $('#select-delivery-type').val(),
						deliveryPrice: $('#select-delivery-price-type').val(),
						paymentDeliveryType: $('#select-payment-delivery-type').val(),
						address: {
							carrierInfo: {
								cityContext: cityContext,
								cityId: cityId,							
								pvz: pvz,
								deliveryVariantId: deliveryVariantId
							},
							address: $('#input-delivery-address').val()
						}
					},
					amounts: {
						postpay: $('#input-amounts-postpay').val().toNumber()
					},
	    			items: orderItems
	    	}		
			
			$.ajax({
				type: 'POST',
				contentType: 'application/json',
				url: '${urlHome}ajax/orders/calc/parcel-delivery-amounts',
				data : JSON.stringify(orderContainer),
				dataType: 'json',
				timeout: 100000,
				success: function(data) {
					console.log('SUCCESS: ', data);
					display(data);
									
					$('#input-amounts-postpay').val(data.result.deliveryServiceResult.postpayAmount);
					$('#input-delivery-price').val(data.result.deliveryServiceResult.deliveryFullPrice);
					$('#input-delivery-customer-price').val(data.result.deliveryServiceResult.deliveryCustomerSummary);
					$('#input-delivery-seller-price').val(data.result.deliveryServiceResult.deliverySellerSummary);
					
					$('#details-items-amounts-delivery-seller-summary').text(data.result.deliveryServiceResult.deliverySellerSummary.formatMoney(2));
					$('#details-items-amounts-delivery-customer-summary').text(data.result.deliveryServiceResult.deliveryCustomerSummary.formatMoney(2));
													
					$('#btn-calc-parcel-delivery-amounts-info').attr('data-original-title', data.result.deliveryServiceResult.parcelType);
					$('#btn-calc-parcel-delivery-amounts-info').attr('data-content', data.result.deliveryServiceResult.info);				
					$('#btn-calc-parcel-delivery-amounts-info').popover('show');
					
					$('#input-delivery-price').focusout();
					
				},
				error: function(e) {
					console.log('ERROR: ', e);
					display(e);
				},
				done: function(e) {
					console.log('DONE');
				}
			});			
			
		} else if ($('#select-delivery-type').val() == 'OZON_ROCKET_PICKPOINT' || $('#select-delivery-type').val() == 'OZON_ROCKET_POSTAMAT') {
			// ozon.rocket pocket point
						
			$('#delivery-ozon-rocket-modal-title').text('<fmt:message key="order.form.modal.delivery.ozon.rocket.header" />' + ' [' + $('#select-delivery-type').find('option:selected').text() + ']');
				
			var ozonRocketDeliveryCity = "";
			if ($('#input-delivery-address-carrierPvz-city').val().trim() == '') {
				ozonRocketDeliveryCity = $('#input-delivery-address').val().trim();
			} else {
				ozonRocketDeliveryCity = $('#input-delivery-address-carrierPvz-city').val().trim();
				
			}
			var ozonWidgetSrc = "https://rocket.ozon.ru/lk/widget?token=WJSvphyEiaY3sEyWk2jNOA%3D%3D&showdeliveryprice=false&showdeliverytime=false&defaultcity=" + ozonRocketDeliveryCity;
			$('#iframe-ozon-widget').attr('src', ozonWidgetSrc);
			$('#delivery-ozon-rocket-modal').modal({keyboard: false});
			
		} else {
			// post office, courier, pickup			
			$.ajax({
				type: 'POST',
				contentType: 'application/json',
				url: '${urlHome}ajax/orders/calc/parcel-delivery-amounts',
				data : JSON.stringify(orderContainer),
				dataType: 'json',
				timeout: 100000,
				success: function(data) {
					console.log('SUCCESS: ', data);
					display(data);
					$('#input-amounts-postpay').val(data.result.deliveryServiceResult.postpayAmount);
					
					$('#input-delivery-price').val(data.result.deliveryServiceResult.deliveryCustomerSummary);
					$('#input-delivery-seller-price').val(data.result.deliveryServiceResult.deliverySellerSummary);
					
					$('#details-items-amounts-delivery-seller-summary').text(data.result.deliveryServiceResult.deliverySellerSummary.formatMoney(2));
					$('#details-items-amounts-delivery-customer-summary').text(data.result.deliveryServiceResult.deliveryCustomerSummary.formatMoney(2));
		
					
					$('#btn-calc-parcel-delivery-amounts-info').attr('data-original-title', data.result.deliveryServiceResult.parcelType);
					$('#btn-calc-parcel-delivery-amounts-info').attr('data-content', data.result.deliveryServiceResult.info);				
					$('#btn-calc-parcel-delivery-amounts-info').popover('show');
					$('#input-delivery-price').focusout();
					
				},
				error: function(e) {
					console.log('ERROR: ', e);
					display(e);
				},
				done: function(e) {
					console.log('DONE');					
				}
			});
		}		
	}
	
	function inputDeliveryCdekChange() {
		
		var city = $('#select-delivery-cdek-city').find('option:selected').text(), 
			street = $('#input-delivery-cdek-street').val(),
			house = $('#input-delivery-cdek-house').val(),
			flat = $('#input-delivery-cdek-flat').val();

		//г. Москва, Байкальская ул., дом 18, корп. 1, кв. 82
		
		$('#input-delivery-cdek-city-address').val('<fmt:message key="order.form.modal.delivery.cdek.fields.address.prefix.city" /> ' + city + '<fmt:message key="order.form.modal.delivery.cdek.fields.address.prefix.street" /> ' + street + ' <fmt:message key="order.form.modal.delivery.cdek.fields.address.prefix.house" /> ' + house + '<fmt:message key="order.form.modal.delivery.cdek.fields.address.prefix.flat" /> ' + flat);
		
	}
	
	function calcTotalAmounts(element) {		
				
		var data = {}, 
			orderItems = [], 
			orderItem;
		$('.input-details-item-product').each(function() {
			
			var indexItem = $(this).attr('id');
			if (indexItem != 'input-details-item-product-hidden') {				
				indexItem = indexItem.substr('input-details-item-product'.length + 1, 2);	
				//console.log(indexItem);
				orderItem = {
						quantity: $('#input-details-item-quantity-' + indexItem).val().toNumber(),
						price: $('#input-details-item-price-' + indexItem).val().toNumber(),
						supplierAmount: $('#input-details-item-supplier-amount-' + indexItem).val().toNumber(),
						amount: $('#input-details-item-amount-' + indexItem).val().toNumber()}
				console.log(orderItem);
				orderItems.push(orderItem);				
			}

		});
				
		var orderContainer = {
				orderType: $('#select-order-type').val(),
				paymentType: $('#select-payment-type').val(),
				delivery: {
					paymentDeliveryType: $('#select-payment-delivery-type').val(),
					price: $('#input-delivery-price').val().toNumber(),
					factCustomerPrice: $('#input-delivery-customer-price').val().toNumber(),
					factSellerPrice: $('#input-delivery-seller-price').val().toNumber(),					
					deliveryType: $('#select-delivery-type').val()					
				},
				amounts: {
					postpay: $('#input-amounts-postpay').val().toNumber()
				},
    			items: orderItems
    	}

		$.ajax({
			type: 'POST',
			contentType: 'application/json',
			url: '${urlHome}ajax/orders/calc/total-amounts',
			data : JSON.stringify(orderContainer),
			dataType: 'json',
			timeout: 100000,
			success: function(data) {
				console.log('SUCCESS: ', data);
				display(data);	
				$('#details-items-amounts-total').text(data.result.amounts.total.formatMoney(2));
				$('#details-items-amounts-total-with-delivery').html('<p><strong>' + data.result.amounts.totalWithDelivery.formatMoney(2) + '</strong></p>');
				$('#details-items-amounts-delivery').text(data.result.amounts.delivery.formatMoney(2));
				$('#details-items-amounts-bill').text(data.result.amounts.bill.formatMoney(2));
				$('#details-items-amounts-supplier').text(data.result.amounts.supplier.formatMoney(2));
				$('#details-items-amounts-margin').text(data.result.amounts.margin.formatMoney(2));
				$('#details-items-amounts-postpay').html('<p><strong>' + data.result.amounts.postpay.formatMoney(2) + '</strong></p>');
				
				//$('#input-delivery-price').val(data.result.amounts.delivery);
				$('#input-amounts-postpay').val(data.result.amounts.postpay);
								
				//$('#details-items-amounts-delivery-seller-summary').text($('#input-delivery-customer-price').val().toNumber().formatMoney(2));
				//$('#details-items-amounts-delivery-customer-summary').text($('#input-delivery-customer-price').val().toNumber().formatMoney(2));
				
				
			},
			error: function(e) {
				console.log('ERROR: ', e);
				display(e);
			},
			done: function(e) {
				console.log('DONE');
			}
		});
	}
	
	
	$('#btn-details-item-add').click(function() {
		currentStatusIndex++;
		console.log(currentStatusIndex);
		
		var s = $('#details-item-row-hidden').prop('outerHTML');
		
		s = s.replace('details-item-row-hidden', 'details-item-row-' + currentStatusIndex);
		s = s.replace('btn-find-details-item-product-hidden', 'btn-find-details-item-product-' + currentStatusIndex);	
		
		s = s.replace('input-details-item-id-hidden', 'input-details-item-id-' + currentStatusIndex);
		s = s.replace('input-details-item-no-hidden', 'input-details-item-no-' + currentStatusIndex);
		s = s.replace('input-details-item-product-id-hidden', 'input-details-item-product-id-' + currentStatusIndex);
		
		s = s.replace('input-details-item-product-hidden', 'input-details-item-product-' + currentStatusIndex);
		s = s.replaceAll('datalist-product-name-hidden', 'datalist-product-name-' + currentStatusIndex);

		s = s.replace('input-details-item-quantity-hidden', 'input-details-item-quantity-' + currentStatusIndex);
		s = s.replace('span-details-item-quantity-stock-info-hidden', 'span-details-item-quantity-stock-info-' + currentStatusIndex);
		
		s = s.replace('span-details-item-product-sku-info-hidden', 'span-details-item-product-sku-info-' + currentStatusIndex);
		
		s = s.replace('input-details-item-price-hidden', 'input-details-item-price-' + currentStatusIndex);
		s = s.replace('input-details-item-discount-rate-hidden', 'input-details-item-discount-rate-' + currentStatusIndex);
		s = s.replace('input-details-item-amount-hidden', 'input-details-item-amount-' + currentStatusIndex);
		s = s.replace('input-details-item-supplier-amount-hidden', 'input-details-item-supplier-amount-' + currentStatusIndex);
		
		s = s.replace('btn-details-item-delete-hidden', 'btn-details-item-delete-' + currentStatusIndex);		
		s = s.replaceAll('items[' + ${hiddenStatusIndex} + ']', 'items[' + currentStatusIndex + ']');
		s = s.replaceAll('items' + ${hiddenStatusIndex}, 'items' + currentStatusIndex);
				
		var html = $.parseHTML(s);
		$('#details-item-row-hidden').before(html);
		$('#input-details-item-id-' + currentStatusIndex).val(-1);
		$('#input-details-item-product-id-' + currentStatusIndex).val(-1);
		$('#input-details-item-no-' + currentStatusIndex).val(currentStatusIndex);
		$('#details-item-row-' + currentStatusIndex).removeAttr('hidden');

		$('#btn-find-details-item-product-' + currentStatusIndex).on('click', {selector: this}, btnDetailsItemProductOnClick);
		$('#btn-details-item-delete-' + currentStatusIndex).on('click', {selector: this}, btnDetailsItemDeleteOnClick);
		$('#input-details-item-price-' + currentStatusIndex).on('change', {selector: this}, inputItemPriceChange);
		$('#input-details-item-quantity-' + currentStatusIndex).on('change', {selector: this}, inputItemPriceChange);
		$('#input-details-item-discount-rate-' + currentStatusIndex).on('change', {selector: this}, inputItemPriceChange);
		calcAmount(inputItemPriceChange);		
	});
	

	$('.btn-find-details-item-product').click(function() {
		btnDetailsItemProductOnClick(this);		
	});
	
	$('.btn-details-item-delete').click(function() {
		btnDetailsItemDeleteOnClick(this);
		/*
		var id = $(this).attr('id');
		id = id.substr('btn-details-item-delete-'.length, id.length);
		console.log(id);
		$('#details-item-row-' + id).remove();
		*/
		
	});
	
	
	function calcAmount(indexItem) {
		
		var quantity = $('#input-details-item-quantity-' + indexItem).val().toNumber();
		var price = $('#input-details-item-price-' + indexItem).val().toNumber();
		var discount = $('#input-details-item-discount-rate-' + indexItem).val().toNumber();
	
		var amount = quantity * price - (quantity * price) * discount/100;
		$('#input-details-item-amount-' + indexItem).val(amount);
		calcTotalAmounts(this);
		
	}
	
	function inputItemPriceChange() {
		var indexItem;
		if ($(this).hasClass('input-details-item-price')) {
			indexItem = $(this).attr('id').substr('input-details-item-price'.length + 1, 2);			
		} else if ($(this).hasClass('input-details-item-quantity')) {
			indexItem = $(this).attr('id').substr('input-details-item-quantity'.length + 1, 2);	
		}  else if ($(this).hasClass('input-details-item-discount-rate')) {
			indexItem = $(this).attr('id').substr('input-details-item-discount-rate'.length + 1, 2);	
		} else {
			return;
		}		
		calcAmount(indexItem);		
	}	

	function btnSearchProductsDisabled(flag) {
		$('.btn-find-details-item-product').prop('disabled', flag);
	}
	
	function display(data) {
		var json = '<h4>Ajax Response</h4><pre>'
				+ JSON.stringify(data, null, 4) + '</pre>';
		//$('#feedback').html(json);
	}
	
	function displayProducts(data) {
		var json = '<h4>Ajax Response</h4><pre>'
				+ JSON.stringify(data, null, 4) + '</pre>';
		//$('#feedback-product').html(json);
	}
	
	// Example starter JavaScript for disabling form submissions if there are invalid fields
	(function() {
	  'use strict';
	  window.addEventListener('load', function() {
	    // Fetch all the forms we want to apply custom Bootstrap validation styles to
	    var forms = document.getElementsByClassName('needs-validation');
	    // Loop over them and prevent submission
	    var validation = Array.prototype.filter.call(forms, function(form) {
	      form.addEventListener('submit', function(event) {
	        if (form.checkValidity() === false) {
	          event.preventDefault();
	          event.stopPropagation();
	        }
	        form.classList.add('was-validated');
	      }, false);
	    });
	  }, false);
	})();
	
	
	window.addEventListener("message", ozonRocketReceiveMessage, false);
	function ozonRocketReceiveMessage(event) {
		// Важно не слушать чужие события
		if (event.origin !== "https://rocket.ozon.ru" || typeof event.data !== "string")
			return;
		console.log(event.data);
		var ozonRocketDeliveryVariantData = JSON.parse(event.data);
		$('#input-ozon-rocket-delivery-variant-data').attr('data', ozonRocketDeliveryVariantData);
		
		$('#input-ozon-rocket-delivery-variant-id').val(ozonRocketDeliveryVariantData.id);
		$('#input-delivery-address-carrierPvz-deviveryVariantId').val(ozonRocketDeliveryVariantData.id);		
		
		$('#input-ozon-rocket-delivery-variant-address').val(ozonRocketDeliveryVariantData.address);

		$('#input-delivery-address-carrierPvz-city').val(ozonRocketDeliveryVariantData.address);
		$('#button-modal-delivery-ozon-rocket-ok').click();
		
	}
	
</script>
<%@ include file="../fragments/footer2html.jsp"%>
