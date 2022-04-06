<%@ include file = "../fragments/header2init.jsp" %>
<%@ include file = "../fragments/header2html2init.jsp" %>

<title><fmt:message key="app.title" /> | <fmt:message key="wiki.ozon.products.title" /></title>

<%@ include file = "../fragments/header2html2navbar2start.jsp" %>
<div
	class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pb-2 mb-3 border-bottom">
	<!-- local content -->
	<h1 class="h2">
		<fmt:message key="wiki.ozon.products.header" />
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
			    <button id="button-offer-prices" type="button" class="btn btn-sm btn-light" onclick="onClickUpdateOfferPrices()"><fmt:message key="wiki.ozon.products.btn.offerPrices"/></button>  
			    <button id="button-offer-prices-full" type="button" class="btn btn-sm btn-light" onclick="onClickUpdateOfferPricesFull()"><fmt:message key="wiki.ozon.products.btn.offerPricesFull"/></button>
			    <button id="button-disconnect" type="button" class="btn btn-sm btn-danger" onclick="onClickDisconnect()"><fmt:message key="wiki.ozon.products.btn.disconnect"/></button>
		</div>
		<br/>
 
        <div class="table-responsive">
            <table class="table table-striped table-sm">
            <thead>
				<tr>
					<th scope="col"><input id="checkbox-union" type="checkbox"></th>					
					<th scope="col"><fmt:message key="wiki.ozon.products.table.headers.no" /></th>
					<th scope="col"><fmt:message key="wiki.ozon.products.table.headers.sku" /></th>
					<th scope="col"><fmt:message key="wiki.ozon.products.table.headers.ozonSku" /></th>					
					<th scope="col"><fmt:message key="wiki.ozon.products.table.headers.name" /></th>					
					<th scope="col"><fmt:message key="wiki.ozon.products.table.headers.price" /></th>
					<th scope="col"><fmt:message key="wiki.ozon.products.table.headers.specialPrice" /></th>					
					<th scope="col"><fmt:message key="wiki.ozon.products.table.headers.productQuantity" /></th>
					<th scope="col"><fmt:message key="wiki.ozon.products.table.headers.stockQuantity" /></th>							
					<th scope="col"><fmt:message key="wiki.ozon.products.table.headers.status" /></th>
					<th scope="col"><fmt:message key="wiki.ozon.products.table.headers.supplierStock" /></th>
					<th scope="col"><fmt:message key="wiki.ozon.products.table.headers.ozonSeller" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="product" items="${products}">			
				<tr class="table-">				
		 				<td class="table-"><input id="checkbox-${product.id}" type="checkbox" data-no="${product.id}"></td>
		 				<td>${product.id}</td>		 				
						<td>${product.sku}</td>
						<td>${product.getMarket(CrmTypes.OZON).marketSku}</td>
						
						<td
							<c:set var = "productName" value = '${product.name.length() < 100 ? product.name : fn:substring(product.name, 0, 100).concat("...")}' />
							data-original-title="${product.name}" data-toggle="tooltip" data-placement="bottom" title="">${productName}</td>						
						<td class="text-right"> <fmt:formatNumber type = "currency" value = "${product.price}" /> </td>
						<td class="text-right"> <fmt:formatNumber type = "currency" value = "${product.getMarket(CrmTypes.OZON).specialPrice}" /> </td>
						
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
						    <c:when test="${product.getMarket(CrmTypes.OZON).supplierStock}">
						        <fmt:message key='main.const.table.value.status.yes' />
						    </c:when>
						    <c:otherwise>
						        <fmt:message key='main.const.table.value.status.no' />
						    </c:otherwise>
						</c:choose>
						</td>
						<td class="text-center">						
						<c:choose>				   
						    <c:when test="${product.getMarket(CrmTypes.OZON).marketSeller}">
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

<!-- Modal -->
<div class="modal fade" id="confirm-modal" tabindex="-1" role="dialog" aria-labelledby="confirm-modal-title" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="confirm-modal-title"><fmt:message key="order.form.modal.ozon.disconnect.header" /></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <p><fmt:message key="order.form.modal.ozon.disconnect.text" /></p>
      </div>
      <div class="modal-footer">
        <button id="button-modal-confirm-cancel" type="button" class="btn btn-light" data-dismiss="modal"><fmt:message key="main.btn.cancel" /></button>
        <button id="button-modal-confirm-ok" type="button" class="btn btn-primary"><fmt:message key="main.btn.ok" /></button>
      </div>
    </div>
  </div>
</div>
   
   
<%@ include file = "../fragments/footer2init.jsp" %>
<!-- local java script -->
<script>
	$('#nav-link-ozon-products').addClass('active');
	$('#nav-link-ozon-products i').removeClass('text-dark').addClass('text-info');
	
	function onClickUpdateProduct() {
		console.log('onClickUpdateProduct: ' + $('#button-update').attr('href'));
		window.location =  $('#button-update').attr("href");			
	}
	function onClickUpdateOfferPrices() {
		window.location = '${urlWiki}/products/ozon/offer-prices/updates';	
	}
	function onClickUpdateOfferPricesFull() {
		window.location = '${urlWiki}/products/ozon/offer-prices/updates-full';	
	}
	function onClickDisconnect() {
		console.log('onClickDisconnect: ');
		$('#confirm-modal').modal({keyboard: false});
	}	
	function onClickConditionsFilter() {
		window.location = '${urlWiki}/products/ozon/conditions/filter';			
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
	
	$('#button-modal-confirm-ok').click(function() {
		console.log('#modal-confirm-ok click: products/ozon/disconnect');
		window.location = '${urlWiki}/products/ozon/disconnect';
					
	});
		
</script>   
	 
  
<%@ include file = "../fragments/footer2html.jsp" %>


