<%@ include file = "../fragments/header2init.jsp" %>
<%@ include file = "../fragments/header2html2init.jsp" %>

<title><fmt:message key="app.title" /> | <fmt:message key="wiki.ym.products.title" /></title>

<%@ include file = "../fragments/header2html2navbar2start.jsp" %>
<div
	class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pb-2 mb-3 border-bottom">
	<!-- local content -->
	<h1 class="h2">
		<fmt:message key="wiki.ym.products.header" />
	</h1>

	<div class="btn-toolbar mb-2 mb-md-0">
		<div class="btn-group mr-2">
			
			<button class="btn btn-sm btn-outline-secondary" onclick="onClickConditionsFilter()"><fmt:message key="orders.conditions.btn.filter" /></button>
		</div>
		
	</div>
	
	

</div>    
   
<!-- alert block ets -->

<c:if test="${not empty msg}">		
			<div class="alert alert-${css} alert-dismissible fade show" role="alert">	
				${msg}
				<button type="button" class="close" data-dismiss="alert" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>				
			</div>	
</c:if>
		
			

		<div>				
			    <button id="button-update" type="button" class="btn btn-sm btn-light" disabled onclick="onClickUpdateProduct()"><fmt:message key="main.btn.update"/></button>
			    <button id="button-offer-prices" type="button" class="btn btn-sm btn-light" onclick="onClickUpdateOfferPrices()"><fmt:message key="wiki.ym.products.btn.offerPrices"/></button>  
		</div>
		<br/>
 
        <div class="table-responsive">
            <table class="table table-striped table-sm">
            <thead>
				<tr>
					<th scope="col"><input id="checkbox-union" type="checkbox"></th>					
					<th scope="col"><fmt:message key="wiki.ym.products.table.headers.no" /></th>
					<th scope="col"><fmt:message key="wiki.ym.products.table.headers.sku" /></th>
					<th scope="col"><fmt:message key="wiki.ym.products.table.headers.ymSku" /></th>					
					<th scope="col"><fmt:message key="wiki.ym.products.table.headers.name" /></th>					
					<th scope="col"><fmt:message key="wiki.ym.products.table.headers.price" /></th>
					<th scope="col"><fmt:message key="wiki.ym.products.table.headers.specialPrice" /></th>
					<th scope="col"><fmt:message key="wiki.ym.products.table.headers.productQuantity" /></th>
					<th scope="col"><fmt:message key="wiki.ym.products.table.headers.stockQuantity" /></th>							
					<th scope="col"><fmt:message key="wiki.ym.products.table.headers.status" /></th>
					<th scope="col"><fmt:message key="wiki.ym.products.table.headers.supplierStock" /></th>
					<th scope="col"><fmt:message key="wiki.ym.products.table.headers.ymSeller" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="product" items="${products}">			
				<tr class="table-">				
		 				<td class="table-"><input id="checkbox-${product.id}" type="checkbox" data-no="${product.id}"></td>
		 				<td>${product.id}</td>		 				
						<td>${product.sku}</td>
						<td>${product.getMarket(CrmTypes.YANDEX_MARKET).marketSku}</td>
						<td
							<c:set var = "productName" value = '${product.name.length() < 100 ? product.name : fn:substring(product.name, 0, 100).concat("...")}' />
							data-original-title="${product.name}" data-toggle="tooltip" data-placement="bottom" title="">${productName}</td>						
						<td class="text-right"> <fmt:formatNumber type = "currency" value = "${product.price}" /> </td>
						<td class="text-right"> <fmt:formatNumber type = "currency" value = "${product.getMarket(CrmTypes.YANDEX_MARKET).specialPrice}" /> </td>
						<td class="text-right">${product.quantity}</td>
						<td class="text-right">${product.stockQuantity}</td>
						<td class="text-center">					
						<c:choose>				   
						    <c:when test="${product.visible}">
						        <fmt:message key='main.const.table.value.status.yes' />
						    </c:when>
						    <c:otherwise>
						        <fmt:message key='main.const.table.value.status.no' />
						    </c:otherwise>
						</c:choose>
						</td>
						<td class="text-center">
						<c:choose>				   
						    <c:when test="${product.getMarket(CrmTypes.YANDEX_MARKET).supplierStock}">
						        <fmt:message key='main.const.table.value.status.yes' />
						    </c:when>
						    <c:otherwise>
						        <fmt:message key='main.const.table.value.status.no' />
						    </c:otherwise>
						</c:choose>
						</td>
						<td class="text-center">						
						<c:choose>				   
						    <c:when test="${product.getMarket(CrmTypes.YANDEX_MARKET).marketSeller}">
						        <fmt:message key='main.const.table.value.status.yes' />
						    </c:when>
						    <c:otherwise>
						        <fmt:message key='main.const.table.value.status.no' />
						    </c:otherwise>
						</c:choose>						
						</td>
								
						
				</tr>			
				</c:forEach>
	         </tbody>
	         </table>
	         
            
<%@ include file = "../fragments/header2html2navbar2finish.jsp" %>               
            
</div>
   
   
<%@ include file = "../fragments/footer2init.jsp" %>
<!-- local java script -->
<script>
	$('#nav-link-ym-products').addClass('active');
	$('#nav-link-ym-products i').removeClass('text-dark').addClass('text-info');
	
	function onClickUpdateProduct() {
		console.log('onClickUpdateProduct: ' + $('#button-update').attr('href'));
		window.location =  $('#button-update').attr("href");			
	}
	function onClickUpdateOfferPrices() {
		window.location = '${urlWiki}/products/ym/offer-prices/updates';	
	}
	
	function onClickConditionsFilter() {
		window.location = '${urlWiki}/products/ym/conditions/filter';			
	}
		
	$(document).ready(function() {		
			
			var $checkboxes = $('input[type="checkbox"]');
			$checkboxes.click(function() {
				var productId,
					updateHref,
					synchronizeHref;
				if (this.id == 'checkbox-union') {
					console.log('checkbox-union: ' + $('#checkbox-union').prop('checked')); 
					$checkboxes.prop('checked', $('#checkbox-union').prop('checked'));
										
					return;
				}
				if ($checkboxes.filter(':checked').length == 0) {					
				}	
				
				$checkboxes.filter(':checked').not(this).prop('checked', false);
				
				if ($(this).prop('checked')) {
					productId = this.id.substring(9, this.id.length);
					
					updateHref = '${urlWiki}/products/' + productId + '/update/${listType}';  
					

					$('#button-update').removeAttr('disabled').attr('href', updateHref);        	
    	        	    	
        	    	console.log(updateHref);
    				    				  		
    			} else {
    				// ...
    			}

    	    });
		});		
		
</script>   
	 
  
<%@ include file = "../fragments/footer2html.jsp" %>


