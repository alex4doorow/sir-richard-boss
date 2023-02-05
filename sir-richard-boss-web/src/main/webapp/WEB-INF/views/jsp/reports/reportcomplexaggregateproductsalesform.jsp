<%@ include file = "../fragments/header2init.jsp" %>
<%@ include file = "../fragments/header2html2init.jsp" %>
  
<title><fmt:message key="app.title" /> | <fmt:message key="reports.aggregate.productSales.form.title" /></title>
    
<%@ include file = "../fragments/header2html2navbar2start.jsp" %>   
<div
	class="d-flex justify-content-start flex-wrap flex-md-nowrap align-items-start">

			<h1 class="h2">
				<fmt:message key="reports.aggregate.productSales.form.header" />
			</h1>
</div>
<div
	class="d-flex justify-content-start flex-wrap flex-md-nowrap align-items-start border-bottom- mb-2">		
</div>
<br />
			<spring:url value="${urlReport}complex-aggregate-product-sales/filter/exec" var="reportProductSalesFilterExecActionUrl" />
			<form:form id="report-product-sales-form" class="needs-validation" method="post" modelAttribute="reportForm" action="${reportProductSalesFilterExecActionUrl}">
				
				<h4 class="mb-4"><fmt:message key="reports.aggregate.productSales.form.headers.period" /></h4>
				
				<div class="form-row">				
					<spring:message code="reports.aggregate.productSales.form.fields.placeholder.period.start" var="reportProductSalesFormFieldsPlaceholderPeriodStart"/> 
					<spring:bind path="periodStart">
					<div class="form-group col-2">					
						<label for="input-period-start"><fmt:message key="reports.aggregate.productSales.form.fields.period.start" /></label> 
						<div class="input-group">
							<form:input path="periodStart" type="text" class="form-control form-control-sm datepicker" id="input-period-start" placeholder="${reportProductSalesFormFieldsPlaceholderPeriodStart}" />							
							<form:errors path="periodStart" class="control-label" />
							<div class="input-group-append">
								<button class="btn btn-sm btn-light btn-outline-secondary" onclick="$('#input-period-start').datepicker('show');" type="button">
									<i class="bi bi-calendar-date text-dark"></i>
								</button>
							</div>
							<div class="invalid-feedback"><fmt:message key="reports.aggregate.productSales.form.fields.invalidFeedback.period.start" /></div>
						</div>	
					</div>
					</spring:bind>
					
					<spring:message code="reports.aggregate.productSales.form.fields.placeholder.period.end" var="reportProductSalesFormFieldsPlaceholderPeriodEnd"/> 
					<spring:bind path="periodEnd">
					<div class="form-group col-2">					
						<label for="input-period-end"><fmt:message key="reports.aggregate.productSales.form.fields.period.end" /></label> 
						<div class="input-group">
							<form:input path="periodEnd" type="text" class="form-control form-control-sm datepicker" id="input-period-end" placeholder="${reportProductSalesFormFieldsPlaceholderPeriodEnd}" />							
							<form:errors path="periodEnd" class="control-label" />
							<div class="input-group-append">
								<button class="btn btn-sm btn-light btn-outline-secondary" onclick="$('#input-period-end').datepicker('show');" type="button">
									<i class="bi bi-calendar-date text-dark"></i>
								</button>
							</div>
							<div class="invalid-feedback"><fmt:message key="reports.aggregate.productSales.form.fields.invalidFeedback.period.end" /></div>
						</div>	
					</div>
					</spring:bind>
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
	
	$('#nav-link-report-product-sales').addClass('active');
	$('#nav-link-report-product-sales i').removeClass('text-dark').addClass('text-info');
	
	$('#btn-clear-filter').click(function() {
		$('.form-control-input-text').val(''); 
	});
	$('#btn-excute-filter').click(function() {
		$('#report-product-sales-form').submit();
	});
	
	
		
</script>  
<%@ include file = "../fragments/footer2html.jsp" %>
