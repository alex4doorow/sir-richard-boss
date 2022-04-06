<%@ include file = "../fragments/header2init.jsp" %>
<%@ include file = "../fragments/header2html2init.jsp" %>
  
<title><fmt:message key="app.title" /> | <fmt:message key="reports.productSalesByQuery.form.title" /></title>
    
<%@ include file = "../fragments/header2html2navbar2start.jsp" %>   
<div
	class="d-flex justify-content-start flex-wrap flex-md-nowrap align-items-start">

			<h1 class="h2">
				<fmt:message key="reports.productSalesByQuery.form.header" />
			</h1>
</div>
<div
	class="d-flex justify-content-start flex-wrap flex-md-nowrap align-items-start border-bottom- mb-2">		
</div>
<br />
			<spring:url value="${urlReport}product-sales-by-query/filter/exec" var="reportProductSalesFilterExecActionUrl" />
			<form:form id="report-product-sales-form" class="needs-validation" method="post" modelAttribute="reportForm" action="${reportProductSalesFilterExecActionUrl}">
				
				<h4 class="mb-4"><fmt:message key="reports.productSalesByQuery.form.headers.period" /></h4>
				<div class="form-row">				
					<spring:message code="reports.productSalesByQuery.form.fields.placeholder.period.start" var="reportProductSalesFormFieldsPlaceholderPeriodStart"/> 
					<spring:bind path="periodStart">
					<div class="form-group col-2">					
						<label for="input-period-start"><fmt:message key="reports.productSalesByQuery.form.fields.period.start" /></label> 
						<div class="input-group">
							<form:input path="periodStart" type="text" class="form-control form-control-sm datepicker" id="input-period-start" placeholder="${reportProductSalesFormFieldsPlaceholderPeriodStart}" />							
							<form:errors path="periodStart" class="control-label" />
							<div class="input-group-append">
								<button class="btn btn-sm btn-light btn-outline-secondary" onclick="$('#input-period-start').datepicker('show');" type="button">
									<i class="bi bi-calendar-date text-dark"></i>
								</button>
							</div>
							<div class="invalid-feedback"><fmt:message key="reports.productSalesByQuery.form.fields.invalidFeedback.period.start" /></div>
						</div>	
					</div>
					</spring:bind>
					
					<spring:message code="reports.productSalesByQuery.form.fields.placeholder.period.end" var="reportProductSalesFormFieldsPlaceholderPeriodEnd"/> 
					<spring:bind path="periodEnd">
					<div class="form-group col-2">					
						<label for="input-period-end"><fmt:message key="reports.productSalesByQuery.form.fields.period.end" /></label> 
						<div class="input-group">
							<form:input path="periodEnd" type="text" class="form-control form-control-sm datepicker" id="input-period-end" placeholder="${reportProductSalesFormFieldsPlaceholderPeriodEnd}" />							
							<form:errors path="periodEnd" class="control-label" />
							<div class="input-group-append">
								<button class="btn btn-sm btn-light btn-outline-secondary" onclick="$('#input-period-end').datepicker('show');" type="button">
									<i class="bi bi-calendar-date text-dark"></i>
								</button>
							</div>
							<div class="invalid-feedback"><fmt:message key="reports.productSalesByQuery.form.fields.invalidFeedback.period.end" /></div>
						</div>	
					</div>
					</spring:bind>
				</div>
			
				
				<h6 class="mb-4"><fmt:message key="reports.productSalesByQuery.form.headers.deliveryTypes" /></h6>
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
				<h6 class="mb-4"><fmt:message key="reports.productSalesByQuery.form.headers.customerTypes" /></h6>				
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
				
				<h6 class="mb-4"><fmt:message key="reports.productSalesByQuery.form.headers.paymentTypes" /></h6>				
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
				
				<h6 class="mb-4"><fmt:message key="reports.productSalesByQuery.form.headers.advertTypes" /></h6>				
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
				<button id="btn-clear-filter" type="button" class="btn btn-light">
					<fmt:message key="main.btn.clear"/>
				</button>							
				<button id="btn-submit-filter" type="submit" class="btn btn-primary">
					<fmt:message key="main.btn.execute" />
				</button>
			</form:form>
			<br/>
 			<div id="feedback"></div>
			

<%@ include file = "../fragments/header2html2navbar2finish.jsp" %>    
     
<!-- alert block ets -->
   
<%@ include file = "../fragments/footer2init.jsp" %>
<!-- local java script -->
<script>
	
	$('#nav-link-report-product-sales-by-query').addClass('active');
	$('#nav-link-report-product-sales-by-query i').removeClass('text-dark').addClass('text-info');
	$('#btn-clear-filter').click(function() {
		$('.form-control-input-text').val(''); 
		$('.form-check-input').prop('checked', false);
		$('#checkbox-period-exist').prop('checked',  true);
	});
	$('#btn-excute-filter').click(function() {
		$('#report-product-sales-form').submit();
	});
	
/*
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
	
	formChecksWraping('deliveryTypes');
	formChecksWraping('customerTypes');
	formChecksWraping('paymentTypes');
	formChecksWraping('advertTypes');
	
	function formChecksWraping(elementName) {
		
		$('#form-checks-' + elementName + ' .form-check').wrap('<div class="form-group col-6"></div>');
		$('#form-checks-' + elementName + ' .form-group:nth-child(odd)').addClass('form-row-odd');
		$('#form-checks-' + elementName + ' .form-group:nth-child(even)').addClass('form-row-even');	
		$('#form-checks-' + elementName + ' .form-row-odd').wrap('<div class="form-row"></div>');
		$('#form-checks-' + elementName + ' .form-row-even').wrap('<div class="form-row"></div>');	
		$('#form-checks-' + elementName + ' input:checkbox').addClass('form-check-input');	
		$('#form-checks-' + elementName + ' label').addClass('form-check-label');	
		
	}


		
</script>  
<%@ include file = "../fragments/footer2html.jsp" %>
