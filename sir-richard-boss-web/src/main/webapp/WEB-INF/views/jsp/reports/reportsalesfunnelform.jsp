<%@ include file = "../fragments/header2init.jsp" %>
<%@ include file = "../fragments/header2html2init.jsp" %>
  
<title><fmt:message key="app.title" /> | <fmt:message key="reports.salesFunnel.form.title" /></title>
    
<%@ include file = "../fragments/header2html2navbar2start.jsp" %>   
<div
	class="d-flex justify-content-start flex-wrap flex-md-nowrap align-items-start">

			<h1 class="h2">
				<fmt:message key="reports.salesFunnel.form.header" />
			</h1>
</div>
<div
	class="d-flex justify-content-start flex-wrap flex-md-nowrap align-items-start border-bottom- mb-2">		
</div>
<br />
			<spring:url value="${urlReport}sales-funnel/filter/exec" var="reportSalesFunnelFilterExecActionUrl" />
			<form:form id="report-product-sales-form" class="needs-validation" method="post" modelAttribute="reportForm" action="${reportSalesFunnelFilterExecActionUrl}">
				
				<h4 class="mb-4"><fmt:message key="reports.anyReport.form.headers.period" /></h4>
				
				<div class="form-row">			
					<div class="col-md-2 mb-1">					  
					  	<form:select path="reportPeriodType" id="select-period-type" class="form-control form-control-sm">
						    <form:options items="${reportPeriodTypes}" itemLabel="text" />
						</form:select>	
					</div>				
					
				</div>
				<div class="form-row">			
					<div class="col-md-1 mb-1">	
						<form:select path="reportPeriodHalfYear" id="select-period-half-year" class="form-control form-control-sm">
						    <form:options items="${reportPeriodHalfYears}" />
						</form:select>	
					</div>
					<div class="col-md-1 mb-1">	
						<form:select path="reportPeriodQuarter" id="select-period-quarter" class="form-control form-control-sm">
						    <form:options items="${reportPeriodQuarters}" />
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
					<spring:message code="reports.anyReport.form.fields.placeholder.period.start" var="reportSalesFunnelFormFieldsPlaceholderPeriodStart"/> 
					<spring:bind path="periodStart">
					<div class="form-group col-2">					
						<label for="input-period-start"><fmt:message key="reports.anyReport.form.fields.period.start" /></label> 
						<div class="input-group">
							<form:input path="periodStart" type="text" class="form-control form-control-sm datepicker" id="input-period-start" placeholder="${reportSalesFunnelFormFieldsPlaceholderPeriodStart}" />							
							<form:errors path="periodStart" class="control-label" />
							<div class="input-group-append">
								<button class="btn btn-sm btn-light btn-outline-secondary" onclick="$('#input-period-start').datepicker('show');" type="button">
									<i class="bi bi-calendar-date text-dark"></i>
								</button>
							</div>
							<div class="invalid-feedback"><fmt:message key="reports.anyReport.form.fields.invalidFeedback.period.start" /></div>
						</div>	
					</div>
					</spring:bind>
					
					<spring:message code="reports.anyReport.form.fields.placeholder.period.end" var="reportSalesFunnelFormFieldsPlaceholderPeriodEnd"/> 
					<spring:bind path="periodEnd">
					<div class="form-group col-2">					
						<label for="input-period-end"><fmt:message key="reports.anyReport.form.fields.period.end" /></label> 
						<div class="input-group">
							<form:input path="periodEnd" type="text" class="form-control form-control-sm datepicker" id="input-period-end" placeholder="${reportSalesFunnelFormFieldsPlaceholderPeriodEnd}" />							
							<form:errors path="periodEnd" class="control-label" />
							<div class="input-group-append">
								<button class="btn btn-sm btn-light btn-outline-secondary" onclick="$('#input-period-end').datepicker('show');" type="button">
									<i class="bi bi-calendar-date text-dark"></i>
								</button>
							</div>
							<div class="invalid-feedback"><fmt:message key="reports.anyReport.form.fields.invalidFeedback.period.end" /></div>
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
	
	$('#nav-link-report-sales-funnel').addClass('active');
	$('#nav-link-report-sales-funnel i').removeClass('text-dark').addClass('text-info');
	
	$('#btn-clear-filter').click(function() {
		$('.form-control-input-text').val(''); 
	});
	$('#btn-excute-filter').click(function() {
		$('#report-sales-funnel-form').submit();
	});
	
	$('#select-period-type').change(function() {
		selectMonthYearOnChange(this);
	});	
	$('#select-period-half-year').change(function() {
		selectMonthYearOnChange(this);
	});
	$('#select-period-quarter').change(function() {
		selectMonthYearOnChange(this);
	});
	$('#select-period-month').change(function() {
		selectMonthYearOnChange(this);
	});
	$('#input-period-year').change(function() {
		selectMonthYearOnChange(this);
	});
	
	
	var stringContext = $('#select-period-type').val();	
	selectMonthYearOnChange(null);
	
	function selectMonthYearOnChange(element) {
		
		var reportPeriodContainer = {
				reportPeriodType: $('#select-period-type').val(),		
				reportPeriodMonth: $('#select-period-month').val().toNumber(),
				reportPeriodQuarter: $('#select-period-quarter').val().toNumber(),
				reportPeriodHalfYear: $('#select-period-half-year').val().toNumber(),				
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
	
	
	function selectReportTypeOnChange(element) {		
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
	
	
	
	function display(data) {
		var json = "<h4>Ajax Response</h4><pre>"
				+ JSON.stringify(data, null, 4) + "</pre>";
		// $('#feedback').html(json);
	}
	
		
</script>  
<%@ include file = "../fragments/footer2html.jsp" %>
