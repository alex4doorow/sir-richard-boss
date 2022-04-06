<%@ include file = "../fragments/header2init.jsp" %>
<%@ include file = "../fragments/header2html2init.jsp" %>
  
<title><fmt:message key="app.title" /> | <fmt:message key="wiki.ym.products.conditions.form.filter.title" /></title>
    
<%@ include file = "../fragments/header2html2navbar2start.jsp" %>   
<div
	class="d-flex justify-content-start flex-wrap flex-md-nowrap align-items-start">
	
			<h1 class="h2">
				<fmt:message key="wiki.ym.products.conditions.form.filter.header" />
			</h1>
			
				
</div>
<div class="d-flex justify-content-start flex-wrap flex-md-nowrap align-items-start border-bottom- mb-2"></div>

<div>
	<button id="btn-clear-filter" type="button" class="btn btn-light"><fmt:message key="main.btn.clear"/></button>
	<button id="btn-excute-filter" type="button" class="btn btn-primary"><fmt:message key="main.btn.execute" /></button>
</div>
<br/>
			<spring:url value="/wiki/products/ym/conditions/filter/exec" var="productConditionsExecActionUrl" />
			<form:form id="wiki-ym-products-conditions-form" class="needs-validation" method="post" modelAttribute="productConditions" action="${productConditionsExecActionUrl}">
								
				<h4 class="mb-4"><fmt:message key="wiki.ym.products.form.headers.products" /></h4>
								
				<div class="form-row">
					<div class="form-group col-3">	
						<label for="select-yandex-seller-exist-types"><fmt:message key="wiki.ym.products.conditions.form.fields.yandexSellerExist" /></label>
						<form:select path="yandexSellerExist" id="select-yandex-seller-exist-types" class="form-control form-control-sm">
							<form:options items="${yandexSellerExistTypes}" itemValue="id" itemLabel="value" />
						</form:select>					
					</div>				
				</div>
				<div class="form-row">
					<div class="form-group col-3">	
						<label for="select-supplier-stock-types"><fmt:message key="wiki.ym.products.conditions.form.fields.supplierStockExist" /></label>
						<form:select path="supplierStockExist" id="select-supplier-stock-types" class="form-control form-control-sm">
							<form:options items="${supplierStockExistTypes}" itemValue="id" itemLabel="value" />
						</form:select>					
					</div>				
				</div>
				
				
				
				<hr class="mb-4">
				<h6 class="mb-4"><fmt:message key="wiki.ym.products.form.headers.suppliers" /></h6>
				<div class="form-row">
					<div class="col-sm-5">										
						<div id="form-checks-suppliers">
						<spring:bind path="viewSuppliers">
									<form:checkboxes path="viewSuppliers" items="${allViewSuppliers}" element="div class='form-check'" />							
									<form:errors path="viewSuppliers" class="control-label" />				 
						</spring:bind>
						</div>
					</div>
				</div>
				
							
														
								
				<button id="btn-submit-filter" type="submit" class="btn btn-primary"><fmt:message key="main.btn.execute" /></button>
			</form:form>
			<br/>
 			<div id="feedback"></div>
			

<%@ include file = "../fragments/header2html2navbar2finish.jsp" %>    
     
<!-- alert block ets -->
   
<%@ include file = "../fragments/footer2init.jsp" %>
<!-- local java script -->
<script>
	
	$('#nav-link-ym-products').addClass('active');	

	$('#btn-submit-filter').attr('hidden', 'true');	

	$('#form-checks-suppliers .form-check').wrap('<div class="form-group col-6"></div>');
	$('#form-checks-suppliers .form-group:nth-child(odd)').addClass('form-row-odd');
	$('#form-checks-suppliers .form-group:nth-child(even)').addClass('form-row-even');	
	$('#form-checks-suppliers .form-row-odd').wrap('<div class="form-row"></div>');
	$('#form-checks-suppliers .form-row-even').wrap('<div class="form-row"></div>');	
	$('#form-checks-suppliers input:checkbox').addClass('form-check-input');	
	$('#form-checks-suppliers label').addClass('form-check-label');
		
	
	$('#btn-clear-filter').click(function() {
		$('.form-control-input-text').val('');		
		$('.form-check-input').prop('checked', false);

		
	});
	$('#btn-excute-filter').click(function() {
			
		$('#wiki-ym-products-conditions-form').submit();
	});

	function display(data) {
	}
	
		
</script>  
<%@ include file = "../fragments/footer2html.jsp" %>
