<%@ include file="../fragments/header2init.jsp"%>
<%@ include file="../fragments/header2html2init.jsp"%>

<title><fmt:message key="app.title" /> | <fmt:message
		key="wiki.stock.form.update.title" /></title>

<%@ include file="../fragments/header2html2navbar2start.jsp"%>
<!-- <h1 class="h2"><fmt:message key="index.header" /></h1>  -->
<div
	class="d-flex justify-content-start flex-wrap flex-md-nowrap align-items-start">

	<c:choose>
		<c:when test="${supplierStockProductForm['new']}">
			<h1 class="h4">
				<fmt:message key="wiki.stock.form.new.header" />
			</h1>
		</c:when>
		<c:otherwise>
			<!--  
			<fmt:message key="wiki.stock.form.update.headers.h1" var="wikiStockInfo">
				<fmt:param value="${order.viewNo}" />
				<fmt:param value="${strOrderDate}" />
			</fmt:message>
			<h1 class="h4">
				<c:out value="${orderHeaderInfo}" />
			</h1>
			-->
		</c:otherwise>
	</c:choose>

</div>
<!-- 
<c:choose>
	<c:when test="${supplierStockProductForm['new']}">

	</c:when>
	<c:otherwise>


		<div>
			<button id="btn-update-order" type="button" class="btn btn-light"
				disabled>
				<fmt:message key="main.btn.updateData" />
			</button>
			<button id="btn-show-order" type="button" class="btn btn-primary">
				<fmt:message key="main.btn.showData" />
			</button>
		</div>

	</c:otherwise>
</c:choose>

<br />
 -->
<div class="row">

	<div class="col-md-4 order-md-2 mb-4"></div>

	<div class="col-md-8 order-md-1">
		<spring:url value="/wiki/stock-products/${supplierStockProductForm.id}/save" var="supplierStockProductFormActionUrl" />
		<form:form class="needs-validation" method="post"
			modelAttribute="supplierStockProductForm" action="${supplierStockProductFormActionUrl}">
			<form:hidden path="id" />
			<form:hidden id="input-stock-product-id-hidden" path="product.id" />

			<div class="form-row">
					<div class="form-group col-md-6">
						<label for="input-stock-product"><fmt:message key="wiki.stock.form.fields.product" /></label>
						<div class="input-group">
							<form:input path="product.name" type="text" class="form-control form-control-sm" id="input-stock-product" />
							<div class="input-group-append">
								<button id="btn-stock-product"
									class="btn btn-sm btn-light btn-outline-secondary btn-find-details-item-product"
									type="button">
									<i class="bi bi-search text-dark"></i>
								</button>
							</div>
						</div>
					</div>
			</div>	
			
			<div class="form-row">
				<div class="form-group col-md-6">
					<label for="select-stock-category"><fmt:message key="wiki.stock.form.fields.category" /></label>
					<form:select path="product.category.id" id="select-stock-category" class="form-control form-control-sm">
						    <form:options items="${productCategories}" itemValue="id" itemLabel="name" />
					</form:select>
				</div>
			</div>			
			
			<div class="form-row">
				<div class="form-group col-md-6">
					<label for="select-stock-supplier"><fmt:message key="wiki.stock.form.fields.supplier" /></label>
					<form:select path="supplier" id="select-stock-supplier" class="form-control form-control-sm">
						<form:options items="${suppliers}" itemLabel="annotation" />
					</form:select>
				</div>
			</div>			
			<div class="form-row">
				<div class="form-group col-md-12">
					<label for="select-stock-delivery-method"><fmt:message key="wiki.stock.form.fields.deliveryMethod" /></label>
					<br/>
					<strong>[${supplierStockProductForm.product.deliveryMethod}]</strong>&nbsp;${supplierStockProductForm.product.deliveryMethod.annotation}					
				</div>
			</div>
			<div class="form-row">
				<div class="form-group col-md-2">
					<label for="input-stock-supplier-price"><fmt:message key="wiki.stock.form.fields.supplier.price" /></label>
					<form:input path="product.supplierPrice" type="text" class="form-control form-control-sm input-calc-amounts" id="input-stock-supplier-price" />
				</div>				
				<div class="form-group col-md-2">
					<label for="input-stock-product-price"><fmt:message key="wiki.stock.form.fields.product.price" /></label>
					<form:input path="product.price" type="text" class="form-control form-control-sm input-calc-amounts" id="input-stock-product-price" />
				</div>
			</div>			
			<div class="form-row">
				<div class="form-group col-md-2">
					<label for="input-stock-stock-quantity"><fmt:message key="wiki.stock.form.fields.stock.quantity" /></label>
					<form:input path="product.stockQuantity" type="text" class="form-control form-control-sm input-calc-amounts" id="input-stock-stock-quantity" />
				</div>				
				
				<div class="form-group col-md-2">
					<label for="input-stock-product-quantity"><fmt:message key="wiki.stock.form.fields.product.quantity" /></label>
					<form:input path="product.quantity" type="text" class="form-control form-control-sm input-calc-amounts" id="input-stock-product-quantity" />
				</div>
				
				<div class="form-group col-md-2">
					<label for="input-stock-supplier-quantity"><fmt:message key="wiki.stock.form.fields.supplier.quantity" /></label>
					<form:input path="product.supplierQuantity" type="text" class="form-control form-control-sm input-calc-amounts" id="input-stock-supplier-quantity" />
				</div>
				
			</div>			
			<div class="form-row">
				<div class="form-group col-md-6">
					<label for="input-stock-product-comment"><fmt:message key="wiki.stock.form.fields.stock.comment" /></label>
					<form:textarea path="comment" id="input-stock-product-comment"
						class="form-control form-control-sm" rows="2" maxlength="255" />

				</div>
			</div>

			<hr class="mb-4">
			<button type="submit" class="btn btn-primary">
				<fmt:message key="main.btn.save" />
			</button>
		</form:form>


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
	$('#nav-link-wiki-stock').addClass('active');
	
	$('.btn-find-details-item-product').on('click', {selector: this}, btnDetailsItemProductOnClick);
	
	function btnDetailsItemProductOnClick(element) {
		
		var stringContext = $('#input-stock-product').val();
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
				displayProducts(data);				
				if (data.code == '200') {
					if (data.result.products.length > 0) {
						$('#input-stock-product').val(data.result.products[0].name);
						$('#input-stock-product-id-hidden').val(data.result.products[0].id);
						$('#input-stock-product-price').val(data.result.products[0].price);
						$('#input-stock-product-quantity').val(data.result.products[0].quantity);
												
						if (data.result.products[0].category.id > 0 && $('#select-product-category').val() == 0) {
							$('#select-product-category').val(data.result.products[0].category.id);
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
		$('.btn-find-details-item-product').prop('disabled', flag);
	}
	
	function displayProducts(data) {
		var json = '<h4>Ajax Response</h4><pre>'
				+ JSON.stringify(data, null, 4) + '</pre>';
		$('#feedback-product').html(json);
	}
	
		
	
</script>
<%@ include file="../fragments/footer2html.jsp"%>
