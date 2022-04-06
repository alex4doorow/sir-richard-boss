<%@ include file="../fragments/header2init.jsp"%>
<%@ include file="../fragments/header2html2init.jsp"%>

<title><fmt:message key="app.title" /> | <fmt:message
		key="wiki.product.form.update.title" /></title>

<%@ include file="../fragments/header2html2navbar2start.jsp"%>
<!-- <h1 class="h2"><fmt:message key="index.header" /></h1>  -->

<div
	class="d-flex justify-content-start flex-wrap flex-md-nowrap align-items-start border-bottom- mb-2">
	<h2 class="h5">${product.viewName}&nbsp;<span class="badge badge-secondary">${id}</span></h2>
</div>

<div>
	<button id="btn-create-meta" type="button" class="btn btn-light">
		<fmt:message key="wiki.products.btn.createMeta" />
	</button>
	
	<button id="btn-create-discount-price" type="button" class="btn btn-light">
		<fmt:message key="wiki.product.form.fields.discountPrice" />
	</button>
	
</div>
<br/>


<div class="row">

	<div class="col-md-4 order-md-2 mb-4"></div>

	<div class="col-md-8 order-md-1">
		<spring:url value="/wiki/products/${product.id}/save/${listType}" var="productFormActionUrl" />
		<form:form class="needs-validation" method="post"
			modelAttribute="productForm" action="${productFormActionUrl}">
			<form:hidden path="id" />
			<form:hidden id="input-stock-product-id-hidden" path="id" />


			<div class="form-row">
					<div class="form-group col-md">
						<label for="input-name"><fmt:message key="wiki.product.form.fields.name" /></label>
						<form:input path="name" type="text" maxlength="255" class="form-control form-control-sm form-control-customer-find" id="input-name" />
					</div>
			
			</div>
			
			<div class="form-row">
					<div class="form-group col-md">
						<label for="input-name"><fmt:message key="wiki.product.form.fields.deliveryName" /></label>
						<form:input path="deliveryName" type="text" maxlength="255" class="form-control form-control-sm form-control-customer-find" id="input-delivery-name" />
					</div>
			
			</div>
			<div class="form-row">
				<div class="form-group col-md">
					<label for="input-meta-title"><fmt:message key="wiki.product.form.fields.metaTitle" /></label>
					<form:textarea path="store.metaTitle" id="input-store-meta-title"
						class="form-control form-control-sm" rows="2" maxlength="255" />

				</div>
			</div>			
			<div class="form-row">
				<div class="form-group col-md">
					<label for="input-meta-description"><fmt:message key="wiki.product.form.fields.metaDescription" /></label>
					<form:textarea path="store.metaDescription" id="input-store-meta-description"
						class="form-control form-control-sm" rows="2" maxlength="255" />

				</div>
			</div>			
			<div class="form-row">
					<div class="form-group col-md">
						<label for="input-meta-keyword"><fmt:message key="wiki.product.form.fields.metaKeyword" /></label>
						<form:input path="store.metaKeyword" type="text" maxlength="255" class="form-control form-control-sm form-control-customer-find" id="input-store-meta-keyword" />
					</div>			
			</div>	
			
			<div class="form-row">
				<div class="form-group col-md">
					<label for="select-product-category"><fmt:message key="wiki.product.form.fields.category" /></label>
					<form:select path="category.id" id="select-product-category" class="form-control form-control-sm">
						    <form:options items="${productCategories}" itemValue="id" itemLabel="name" />
					</form:select>
				</div>
			</div>	
			
			
			<div class="form-row">
				<div class="form-group col-md-10">
					<label for="select-delivery-methods"><fmt:message key="wiki.product.form.fields.deliveryMethod" /></label>
					<form:select path="deliveryMethod" id="select-delivery-methods" class="form-control form-control-sm">
						<form:options items="${paymentDeliveryMethods}" itemLabel="annotation" />
					</form:select>

				</div>
				<div class="form-group col-md-2">
					<label for="select-types"><fmt:message key="wiki.product.form.fields.type" /></label>
					<form:select path="type" id="select-types" class="form-control form-control-sm">
						<form:options items="${productTypes}" itemLabel="annotation" />
					</form:select>
				</div>
			</div>

					
			<div class="form-row">
					<div class="form-group col-md-4">
						<label for="input-sku"><fmt:message key="wiki.product.form.fields.sku" /></label>
						<form:input path="sku" type="text" maxlength="30" class="form-control form-control-sm form-control-customer-find" id="input-sku" />
					</div>			
					<div class="form-group col-md-4">
						<label for="input-ymSku"><fmt:message key="wiki.product.form.fields.ymSku" /></label>
						<form:input path="yandexSku" type="text" maxlength="60" class="form-control form-control-sm form-control-customer-find" id="input-ym-market-sku" />
					</div>
					
					<div class="form-group col-md-4">
						<label for="input-ozonSku"><fmt:message key="wiki.product.form.fields.ozonSku" /></label>
						<form:input path="ozonSku" type="text" maxlength="60" class="form-control form-control-sm form-control-customer-find" id="input-ozon-market-sku" />
					</div>
			</div>
			<hr class="mb-4">			
			<div class="form-row">
					<div class="form-group col-md-2">
						<label for="input-quantity"><fmt:message key="wiki.product.form.fields.quantity" /></label>
						<form:input path="quantity" type="text" maxlength="30" class="form-control form-control-sm input-calc-amounts-" id="input-quantity" />
					</div>
					<div class="form-group col-md-3">
						<label ></label>						
						<div id="div-stock-quantity" class="position-absolute fixed-bottom" >
							<b>&nbsp;<span><fmt:message key="wiki.product.form.fields.quantity.stockQuantity.text" />&nbsp;${product.stockQuantity}</span></b>	
						</div>
					</div>			
					
			</div>				
			<div class="form-row">
					<div class="form-group col-md-2">
						<label for="input-price"><fmt:message key="wiki.product.form.fields.price" /></label>
						<form:input path="priceWithoutDiscount" type="text" maxlength="30" class="form-control form-control-sm input-calc-price" id="input-price" />
					</div>			
					<div class="form-group col-md-2">
						<label for="input-discountPrice"><fmt:message key="wiki.product.form.fields.discountPrice" /></label>
						<form:input path="priceWithDiscount" type="text" maxlength="30" class="form-control form-control-sm input-calc-price" id="input-discount-price" />
					</div>					
					<div class="form-group col-md-2">
						<label for="input-ozon-special-price"><fmt:message key="wiki.product.form.fields.ozonSpecialPrice" /></label>
						<form:input path="ozonSpecialPrice" type="text" maxlength="30" class="form-control form-control-sm input-calc-price" id="input-ozon-special-price" />
					</div>
					<div class="form-group col-md-2">
						<label for="input-yandex-special-price"><fmt:message key="wiki.product.form.fields.ymSpecialPrice" /></label>
						<form:input path="yandexSpecialPrice" type="text" maxlength="30" class="form-control form-control-sm input-calc-price" id="input-yandex-special-price" />
					</div>
					<div class="form-group col-md-1">
						<label ></label>						
						<div id="div-discount-price" class="position-absolute fixed-bottom" >
						</div>						
					</div>
			</div>			
			
			<hr class="mb-4">
			<div class="form-row">			
				<div class="custom-control custom-checkbox">	
	    			<form:checkbox path="visible" class="custom-control-input" id="check-visible"/>
	    			<label for="check-visible" class="custom-control-label"><fmt:message key="wiki.product.form.fields.status" /></label>
	  			</div>			
			</div>
										
			<div class="form-row">			
				<div class="custom-control custom-checkbox">	
	    			<form:checkbox path="yandexSupplierStock" class="custom-control-input" id="check-supplier-stock"/>
	    			<label for="check-supplier-stock" class="custom-control-label"><fmt:message key="wiki.product.form.fields.supplierStock" /></label>
	  			</div>			
			</div>
			<div class="form-row">			
				<div class="custom-control custom-checkbox">	
	    			<form:checkbox path="yandexMarketSeller" class="custom-control-input" id="check-yandex-seller"/>
	    			<label for="check-yandex-seller" class="custom-control-label"><fmt:message key="wiki.product.form.fields.yandexSeller" /></label>
	  			</div>			
			</div>
			
			<div class="form-row">			
				<div class="custom-control custom-checkbox">	
	    			<form:checkbox path="ozonMarketSeller" class="custom-control-input" id="check-ozon-seller"/>
	    			<label for="check-ozon-seller" class="custom-control-label"><fmt:message key="wiki.product.form.fields.ozonSeller" /></label>
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
  createDiscountPrice();

$('#btn-create-meta').click(function() {
	createMetaDescription();
});

$('#btn-create-discount-price').click(function() {
	createDiscountPrice();
});	

$('.input-calc-price').change(function() {
	createDiscountPrice();
});


function createMetaDescription() {		
	var contextId = ${id};

	$.ajax({
		type: 'POST',
		contentType: 'application/json',
		url: '${urlHome}ajax/product/create-meta',
		data: JSON.stringify(contextId),
		dataType: 'json',
		timeout: 100000,
		success: function(data) {
			console.log('SUCCESS: ', data);
			display(data);
			
			$('#input-store-meta-title').val(data.result.store.metaTitle);
			$('#input-store-meta-description').val(data.result.store.metaDescription);
				
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

/*
function cleanString(input) {
    var output = "";
    for (var i = 0; i < input.length; i++) {
        if (input.charCodeAt(i) <= 127) {
            output += input.charAt(i);
        }
    }
    return output;
}
*/

function createDiscountPrice() {		
	
	var s1 = $('#input-price').val().cleanTrashString();
	var s2 = $('#input-discount-price').val().cleanTrashString();
	var productContainer = {
			priceWithoutDiscountText: s1,
			priceWithDiscountText: s2
		};	
	/*
	var productContainer = {
		priceWithoutDiscountText: cleanString($('#input-price').val()),
		priceWithDiscountText: cleanString($('#input-discount-price').val())
	};
	*/

	$.ajax({
		type: 'POST',
		contentType: 'application/json',
		url: '${urlHome}ajax/product/priceText',
		data: JSON.stringify(productContainer),
		dataType: 'json',
		timeout: 100000,
		success: function(data) {
			console.log('SUCCESS: ', data);		
			
			$('#div-discount-price').empty();
			$('#div-discount-price').append(data.result.priceText);
				
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
	var json = '<h4>Ajax Response</h4><pre>'
			+ JSON.stringify(data, null, 4) + '</pre>';
	$('#feedback').html(json);
}

	
</script>
<%@ include file="../fragments/footer2html.jsp"%>
