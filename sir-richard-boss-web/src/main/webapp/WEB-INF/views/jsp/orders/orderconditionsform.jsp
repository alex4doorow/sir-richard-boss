<%@ include file = "../fragments/header2init.jsp" %>
<%@ include file = "../fragments/header2html2init.jsp" %>
  
<title><fmt:message key="app.title" /> | <fmt:message key="orders.conditions.form.filter.title" /></title>
    
<%@ include file = "../fragments/header2html2navbar2start.jsp" %>   
<div
	class="d-flex justify-content-start flex-wrap flex-md-nowrap align-items-start">
	

			<h1 class="h2">
				<fmt:message key="orders.conditions.form.filter.header" />
			</h1>
			
				
</div>
<div class="d-flex justify-content-start flex-wrap flex-md-nowrap align-items-start border-bottom- mb-2"></div>

<div>
	<button id="btn-clear-filter" type="button" class="btn btn-light"><fmt:message key="main.btn.clear"/></button>
	<button id="btn-excute-filter" type="button" class="btn btn-primary"><fmt:message key="main.btn.execute" /></button>
</div>
<br/>
			<spring:url value="/orders/conditions/filter/exec" var="orderConditionsExecActionUrl" />
			<form:form id="order-conditions-form" class="needs-validation" method="post" modelAttribute="orderConditionsForm" action="${orderConditionsExecActionUrl}">
				
				<h4 class="mb-4"><fmt:message key="order.conditions.form.headers.periods" /></h4>
				<div class="form-row">			
					<div class="col-md-2 mb-1">					  
					  	<form:select path="reportPeriodType" id="select-period-type" class="form-control form-control-sm">
						    <form:options items="${reportPeriodTypes}" itemLabel="annotation" />
						</form:select>	
					</div>				
					<div class="col-md-1 mb-1">	
						<form:select path="reportPeriodMonth" id="select-period-month" class="form-control form-control-sm">
						    <form:options items="${reportPeriodMonths}" />
						</form:select>	
						
					</div>
					<div class="col-md-1 mb-1">
						<form:input path="reportPeriodYear" type="text" class="form-control form-control-sm" id="input-period-year"/>
					</div>
				</div>				
				
				<div class="form-row">				
					<spring:message code="order.conditions.form.fields.placeholder.period.start" var="orderConditionsFormFieldsPlaceholderPeriodStart"/> 
					<spring:bind path="periodStart">
					<div class="form-group col-2">					
						<label for="input-period-start"><fmt:message key="order.conditions.form.fields.period.start" /></label> 
						<div class="input-group">
							<form:input path="periodStart" type="text" class="form-control form-control-sm datepicker" id="input-period-start" placeholder="${orderConditionsFormFieldsPlaceholderPeriodStart}" />							
							<form:errors path="periodStart" class="control-label" />
							<div class="input-group-append">
								<button class="btn btn-sm btn-light btn-outline-secondary" onclick="$('#input-period-start').datepicker('show');" type="button">
									<i class="bi bi-calendar-date text-dark"></i>
								</button>
							</div>
							<div class="invalid-feedback"><fmt:message key="order.conditions.form.fields.invalidFeedback.period.start" /></div>
						</div>	
					</div>
					</spring:bind>
					
					<spring:message code="order.conditions.form.fields.placeholder.period.end" var="orderConditionsFormFieldsPlaceholderPeriodEnd"/> 
					<spring:bind path="periodEnd">
					<div class="form-group col-2">					
						<label for="input-period-end"><fmt:message key="order.conditions.form.fields.period.end" /></label> 
						<div class="input-group">
							<form:input path="periodEnd" type="text" class="form-control form-control-sm datepicker" id="input-period-end" placeholder="${orderConditionsFormFieldsPlaceholderPeriodEnd}" />							
							<form:errors path="periodEnd" class="control-label" />
							<div class="input-group-append">
								<button class="btn btn-sm btn-light btn-outline-secondary" onclick="$('#input-period-end').datepicker('show');"
									type="button">
									<i class="bi bi-calendar-date text-dark"></i>									
								</button>
							</div>
							<div class="invalid-feedback"><fmt:message key="order.conditions.form.fields.invalidFeedback.period.end" /></div>
						</div>	
					</div>
					</spring:bind>					
				</div>
				<div class="form-row">
					<div class="form-group col-2">				
						<div class="custom-control custom-checkbox">
							<form:checkbox path="periodExist" class="custom-control-input" id="checkbox-period-exist"/>
		              		<label for="checkbox-period-exist" class="custom-control-label"><fmt:message key="order.conditions.form.fields.period.isPeriod" /></label>
		            	</div>
	            	</div>            	
				</div>
				<div class="form-row">
					<br/>
				</div>
				
				<h4 class="mb-4"><fmt:message key="order.conditions.form.headers.orders" /></h4>
				<div class="form-row">
					<div class="form-group col-2">
						<label for="input-no"><fmt:message key="order.conditions.form.fields.no" /></label> 
						<form:input path="no" type="text" class="form-control form-control-sm form-control-input-text" id="input-no" />
					</div>
					<div class="form-group col-2">					
						<label for="input-trackCode"><fmt:message key="order.conditions.form.fields.trackCode" /></label> 
						<form:input path="trackCode" type="text" class="form-control form-control-sm form-control-input-text" id="input-trackCode" />
					</div>
					<div class="form-group col-2">					
						<label for="input-deliveryAddress"><fmt:message key="order.conditions.form.fields.deliveryAddress" /></label> 
						<form:input path="deliveryAddress" type="text" class="form-control form-control-sm form-control-input-text" id="input-deliveryAddress" />
					</div>
				</div>				
				
				<div class="form-row">
					<div class="form-group col-2">
						<label for="input-customer-person-phone-number"><fmt:message key="order.conditions.form.fields.customer.person.phoneNumber" /></label> 
						<form:input path="customerConditions.personPhoneNumber" type="text" class="form-control form-control-sm form-control-input-text input-mask-phone" id="input-customer-person-phone-number" />
					</div>
					<div class="form-group col-2">
						<label for="input-customer-person-email"><fmt:message key="order.conditions.form.fields.customer.person.email" /></label> 
						<form:input path="customerConditions.personEmail" type="text" class="form-control form-control-sm form-control-input-text" id="input-customer-person-email" />
					</div>
					<div class="form-group col-2">
						<label for="input-customer-last-name"><fmt:message key="order.conditions.form.fields.customer.person.lastName" /></label> 
						<form:input path="customerConditions.personLastName" type="text" class="form-control form-control-sm form-control-input-text" id="input-customer-last-name" />
					</div>
				</div>
				<h6 class="mb-4"><fmt:message key="order.conditions.form.headers.orders.customers.companies" /></h6>
				<div class="form-row">
					<div class="form-group col-2">
						<label for="input-customer-company-inn"><fmt:message key="order.conditions.form.fields.customer.company.inn" /></label> 
						<form:input path="customerConditions.companyInn" type="text" class="form-control form-control-sm form-control-input-text" id="input-customer-company-inn" />
					</div>
					<div class="form-group col-4">
						<label for="input-customer-company-short-name"><fmt:message key="order.conditions.form.fields.customer.company.shortName" /></label> 
						<form:input path="customerConditions.companyShortName" type="text" class="form-control form-control-sm form-control-input-text" id="input-customer-company-shortName" />
					</div>					
				</div>
				
				<h6 class="mb-4"><fmt:message key="order.conditions.form.headers.additional" /></h6>
				<div class="form-row">
					<div class="form-group col-6">
							<label for="input-product"><fmt:message key="order.conditions.form.fields.product" /></label>
							<div class="input-group">	
								<form:hidden id="input-details-product-id" path="product.id" />
								<form:input path="product.name"
									type="text"
									class="form-control form-control-sm form-control-input-text"
									list="datalist-product-name"
									id="input-details-product" />
								<datalist id="datalist-product-name"></datalist>	
	
								<div class="input-group-append">
									<button id="btn-find-product"
										class="btn btn-sm btn-light btn-outline-secondary btn-find-details-product"
										type="button">
										<i class="bi bi-search text-dark"></i>										
									</button>
								</div>
							</div>
					</div>
				</div>
				<h6 class="mb-4"><fmt:message key="order.conditions.form.headers.statuses" /></h6>
				<div class="form-row">
					<div class="col-sm-5">										
						<div id="form-checks-statuses">
						<spring:bind path="viewStatuses">
									<form:checkboxes path="viewStatuses" items="${allViewStatuses}" element="div class='form-check'" />							
									<form:errors path="viewStatuses" class="control-label" />				 
						</spring:bind>
						</div>
					</div>
				</div>
				
				<h6 class="mb-4"><fmt:message key="order.conditions.form.headers.orderTypes" /></h6>
				<div class="form-row">
					<div class="col-sm-5">										
						<div id="form-checks-orderTypes">
						<spring:bind path="viewOrderTypes">
									<form:checkboxes path="viewOrderTypes" items="${allViewOrderTypes}" element="div class='form-check'" />							
									<form:errors path="viewOrderTypes" class="control-label" />				 
						</spring:bind>
						</div>
					</div>
				</div>
				
				<h6 class="mb-4"><fmt:message key="order.conditions.form.headers.deliveryTypes" /></h6>
				<div class="form-row">
					<div class="col-sm-5">										
						<div id="form-checks-deliveryTypes">
						<spring:bind path="viewDeliveryTypes">
									<form:checkboxes path="viewDeliveryTypes" items="${allViewDeliveryTypes}" element="div class='form-check'" />							
									<form:errors path="viewDeliveryTypes" class="control-label" />				 
						</spring:bind>
						</div>
					</div>
				</div>
				<h6 class="mb-4"><fmt:message key="order.conditions.form.headers.customerTypes" /></h6>				
				<div class="form-row">
					<div class="col-sm-5">										
						<div id="form-checks-customerTypes">
						<spring:bind path="viewCustomerTypes">
									<form:checkboxes path="viewCustomerTypes" items="${allViewCustomerTypes}" element="div class='form-check'" />							
									<form:errors path="viewCustomerTypes" class="control-label" />				 
						</spring:bind>
						</div>
					</div>
				</div>
				
				<h6 class="mb-4"><fmt:message key="order.conditions.form.headers.paymentTypes" /></h6>				
				<div class="form-row">
					<div class="col-sm-5">										
						<div id="form-checks-paymentTypes">
						<spring:bind path="viewPaymentTypes">
									<form:checkboxes path="viewPaymentTypes" items="${allViewPaymentTypes}" element="div class='form-check'" />							
									<form:errors path="viewPaymentTypes" class="control-label" />				 
						</spring:bind>
						</div>
					</div>
				</div>									
				
				<h6 class="mb-4"><fmt:message key="order.conditions.form.headers.advertTypes" /></h6>				
				<div class="form-row">
					<div class="col-sm-5">										
						<div id="form-checks-advertTypes">
						<spring:bind path="viewAdvertTypes">
									<form:checkboxes path="viewAdvertTypes" items="${allViewAdvertTypes}" element="div class='form-check'" />							
									<form:errors path="viewAdvertTypes" class="control-label" />				 
						</spring:bind>
						</div>
					</div>
				</div>
														
				<hr class="mb-4">				
				<button id="btn-submit-filter" type="submit" class="btn btn-primary"><fmt:message key="main.btn.execute" /></button>
			</form:form>
			<br/>
 			<div id="feedback"></div>
			

<%@ include file = "../fragments/header2html2navbar2finish.jsp" %>    
     
<!-- alert block ets -->
   
<%@ include file = "../fragments/footer2init.jsp" %>
<!-- local java script -->
<script>
	
	$('#nav-link-orders').addClass('active');	
	$('#btn-submit-filter').attr('hidden', 'true');	
	$('.input-mask-phone').mask('(000) 000-00-00');
	selectReportTypeOnChange();
	/*
	$('#form-checks-statuses .form-check').wrap('<div class="form-group col-6"></div>');
	$('#form-checks-statuses .form-group:nth-child(odd)').addClass('form-row-odd');
	$('#form-checks-statuses .form-group:nth-child(even)').addClass('form-row-even');	
	$('#form-checks-statuses .form-row-odd').wrap('<div class="form-row"></div>');
	$('#form-checks-statuses .form-row-even').wrap('<div class="form-row"></div>');	
	$('#form-checks-statuses input:checkbox').addClass('form-check-input');	
	$('#form-checks-statuses label').addClass('form-check-label');
	
	$('#form-checks-orderTypes .form-check').wrap('<div class="form-group col-6"></div>');
	$('#form-checks-orderTypes .form-group:nth-child(odd)').addClass('form-row-odd');
	$('#form-checks-orderTypes .form-group:nth-child(even)').addClass('form-row-even');	
	$('#form-checks-orderTypes .form-row-odd').wrap('<div class="form-row"></div>');
	$('#form-checks-orderTypes .form-row-even').wrap('<div class="form-row"></div>');	
	$('#form-checks-orderTypes input:checkbox').addClass('form-check-input');	
	$('#form-checks-orderTypes label').addClass('form-check-label');
	
	$('#form-checks-deliveryTypes .form-check').wrap('<div class="form-group col-6"></div>');
	$('#form-checks-deliveryTypes .form-group:nth-child(odd)').addClass('form-row-odd');
	$('#form-checks-deliveryTypes .form-group:nth-child(even)').addClass('form-row-even');	
	$('#form-checks-deliveryTypes .form-row-odd').wrap('<div class="form-row"></div>');
	$('#form-checks-deliveryTypes .form-row-even').wrap('<div class="form-row"></div>');	
	$('#form-checks-deliveryTypes input:checkbox').addClass('form-check-input');	
	$('#form-checks-deliveryTypes label').addClass('form-check-label');	
	
	$('#form-checks-customerTypes .form-check').wrap('<div class="form-group col-6"></div>');
	$('#form-checks-customerTypes .form-group:nth-child(odd)').addClass('form-row-odd');
	$('#form-checks-customerTypes .form-group:nth-child(even)').addClass('form-row-even');	
	$('#form-checks-customerTypes .form-row-odd').wrap('<div class="form-row"></div>');
	$('#form-checks-customerTypes .form-row-even').wrap('<div class="form-row"></div>');	
	$('#form-checks-customerTypes input:checkbox').addClass('form-check-input');	
	$('#form-checks-customerTypes label').addClass('form-check-label');	
	
	$('#form-checks-paymentTypes .form-check').wrap('<div class="form-group col-6"></div>');
	$('#form-checks-paymentTypes .form-group:nth-child(odd)').addClass('form-row-odd');
	$('#form-checks-paymentTypes .form-group:nth-child(even)').addClass('form-row-even');	
	$('#form-checks-paymentTypes .form-row-odd').wrap('<div class="form-row"></div>');
	$('#form-checks-paymentTypes .form-row-even').wrap('<div class="form-row"></div>');	
	$('#form-checks-paymentTypes input:checkbox').addClass('form-check-input');	
	$('#form-checks-paymentTypes label').addClass('form-check-label');
	
	$('#form-checks-advertTypes .form-check').wrap('<div class="form-group col-6"></div>');
	$('#form-checks-advertTypes .form-group:nth-child(odd)').addClass('form-row-odd');
	$('#form-checks-advertTypes .form-group:nth-child(even)').addClass('form-row-even');	
	$('#form-checks-advertTypes .form-row-odd').wrap('<div class="form-row"></div>');
	$('#form-checks-advertTypes .form-row-even').wrap('<div class="form-row"></div>');	
	$('#form-checks-advertTypes input:checkbox').addClass('form-check-input');	
	$('#form-checks-advertTypes label').addClass('form-check-label');
	*/
	
	formChecksWraping('statuses');
	formChecksWraping('orderTypes');
	formChecksWraping('deliveryTypes');
	formChecksWraping('customerTypes');
	formChecksWraping('paymentTypes');
	formChecksWraping('advertTypes');
		
	$('.btn-find-details-product').on('click', {selector: this}, btnDetailsProductOnClick);
		
	$('#btn-clear-filter').click(function() {
		$('.form-control-input-text').val('');		
		$('.form-check-input').prop('checked', false);
		$('#checkbox-period-exist').prop('checked',  true);
		$('#input-details-product').val('');
		$('#input-details-product-id').val(0);		
		$('#datalist-product-name option').remove();
		
	});
	$('#btn-excute-filter').click(function() {
		$('#select-period-month').prop('disabled', false);
		$('#input-period-year').prop('disabled', false);
		$('#input-period-start').prop('disabled', false);
		$('#input-period-end').prop('disabled', false);		
		$('#order-conditions-form').submit();
	});
	
	$('#select-period-type').change(function() {
		selectReportTypeOnChangeWithAjax(this);
	});
	
	$('#select-period-month').change(function() {
		selectMonthYearOnChange(this);
	});
	
	$('#input-period-year').change(function() {
		selectMonthYearOnChange(this);
	});
	
	function formChecksWraping(elementName) {
		$('#form-checks-' + elementName + ' .form-check').wrap('<div class="form-group col-6"></div>');
		$('#form-checks-' + elementName + ' .form-group:nth-child(odd)').addClass('form-row-odd');
		$('#form-checks-' + elementName + ' .form-group:nth-child(even)').addClass('form-row-even');	
		$('#form-checks-' + elementName + ' .form-row-odd').wrap('<div class="form-row"></div>');
		$('#form-checks-' + elementName + ' .form-row-even').wrap('<div class="form-row"></div>');	
		$('#form-checks-' + elementName + ' input:checkbox').addClass('form-check-input');	
		$('#form-checks-' + elementName + ' label').addClass('form-check-label');		
	}
		
	function btnDetailsProductOnClick(element) {
		
		var inputProductId = $(this).attr('id');	
		var stringContext = $('#input-details-product').val();
		if (stringContext == null || stringContext.trim() == '') {
			return;
		}		
		var data = {};	
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
				display(data);				
				if (data.code == '200') {
					if (data.result.products.length > 0) {					
						$('#input-details-product').val(data.result.products[0].name);
						$('#input-details-product-id').val(data.result.products[0].id);
						
						$('#datalist-product-name option').remove();
						for (var key in data.result.products) {						
							  $('#datalist-product-name').append('<option>' + data.result.products[key].name + '</option>');
						}	
					}					
				} 
				btnSearchProductsDisabled(false);
			},
			error: function(e) {
				console.log('ERROR: ', e);
				display(e);
				btnSearchProductsDisabled(false);
			},
			done: function(e) {
				console.log('DONE');
				btnSearchProductsDisabled(false);
			}
		});			
	}	
	
	function btnSearchProductsDisabled(flag) {
		$('.btn-find-details-product').prop('disabled', flag);
	}	
	
	function selectReportTypeOnChange() {
		var stringContext = $('#select-period-type').val();
		if (stringContext == null || stringContext.trim() == '') {
			return;
		}	
		if (stringContext == 'ANY_MONTH') {
			$('#select-period-month').prop('disabled', false);
			$('#input-period-year').prop('disabled', false);
			$('#input-period-start').prop('disabled', true);
			$('#input-period-end').prop('disabled', true);
		} else if (stringContext == 'ANY_PERIOD') {
			$('#select-period-month').prop('disabled', false);
			$('#input-period-year').prop('disabled', false);
			$('#input-period-start').prop('disabled', false);
			$('#input-period-end').prop('disabled', false);
		} else {
			$('#select-period-month').prop('disabled', true);
			$('#input-period-year').prop('disabled', true);
			$('#input-period-start').prop('disabled', true);
			$('#input-period-end').prop('disabled', true);
		}
	}
	
	function selectReportTypeOnChangeWithAjax(element) {		
		selectReportTypeOnChange();
		var stringContext = $('#select-period-type').val();
		if (stringContext == null || stringContext.trim() == '') {
			return;
		}		
		if (stringContext != 'ANY_PERIOD') {			
			$.ajax({
				type: 'POST',
				contentType: 'application/json',
				url: '${urlHome}ajax/orders/conditions/filter/get-periods-by-type',
				data: stringContext,
				dataType: 'json',
				timeout: 100000,
				success: function(data) {
					console.log('SUCCESS: ', data);
					display(data);
					if (data.code == '200') {
						$('#input-period-start').val(data.result.viewPeriodStart);
						$('#input-period-end').val(data.result.viewPeriodEnd);
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
	}
	
	function selectMonthYearOnChange(element) {
		
		var reportPeriodContainer = {
				reportPeriodType: $('#select-period-type').val(),		
				reportPeriodMonth: $('#select-period-month').val().toNumber(),
				reportPeriodQuarter:1,
				reportPeriodHalfYear: 1,				
				reportPeriodYear: $('#input-period-year').val().toNumber()					
    	}		
			
		$.ajax({
			type: 'POST',
			contentType: 'application/json',
			url: '${urlHome}ajax/orders/conditions/filter/get-periods-by-month-year',
			data: JSON.stringify(reportPeriodContainer),
			dataType: 'json',
			timeout: 100000,
			success: function(data) {
				console.log('SUCCESS: ', data);
				display(data);
				if (data.code == '200') {
					$('#input-period-start').val(data.result.viewPeriodStart);
					$('#input-period-end').val(data.result.viewPeriodEnd);
					selectReportTypeOnChange();
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
	
	function display(data) {
		var json = "<h4>Ajax Response</h4><pre>"
				+ JSON.stringify(data, null, 4) + "</pre>";
		//$('#feedback').html(json);
	}
	
		
</script>  
<%@ include file = "../fragments/footer2html.jsp" %>
