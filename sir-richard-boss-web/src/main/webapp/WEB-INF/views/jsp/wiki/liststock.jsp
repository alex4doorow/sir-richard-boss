<%@ include file = "../fragments/header2init.jsp" %>
<%@ include file = "../fragments/header2html2init.jsp" %>

<title><fmt:message key="app.title" /> | <fmt:message key="wiki.stock.title" /></title>

<%@ include file = "../fragments/header2html2navbar2start.jsp" %>
<div
	class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pb-2 mb-3 border-bottom">
	<!-- local content -->
	<h1 class="h2">
		<fmt:message key="wiki.stock.header" />
	</h1>

	<div class="btn-toolbar mb-2 mb-md-0">
	
		<div class="btn-group mr-2">
			<select id="select-stock-category" name="productCategories" class="form-control form-control-sm">
				<c:forEach items="${productCategories}" var="productCategory">
				<option value="${productCategory.id}">${productCategory.name}</option>
				</c:forEach>
			</select>			
		</div>
	
		<div class="btn-group mr-2">
			<select id="select-stock-supplier" name="suppliers" class="form-control form-control-sm">
				<c:forEach items="${suppliers}" var="supplier">
				<option value="${supplier.id}">${supplier.annotation}</option>
				</c:forEach>
			</select>
			<button id="btn-supplier-find" class="btn btn-sm btn-outline-primary"><fmt:message key="main.btn.find" /></button>
		</div>
		
		<!-- 
		<div class="btn-group mr-2">
			<button class="btn btn-sm btn-outline-secondary">Share</button>
			<button class="btn btn-sm btn-outline-secondary" onclick="onClickConditionsFilter()"><fmt:message key="orders.conditions.btn.filter" /></button>
		</div>
		<button id="button-conditions" type="button" class="btn btn-sm btn-outline-secondary dropdown-toggle" data-toggle="dropdown" data-action="no-action" aria-haspopup="true" aria-expanded="false">
			<span data-feather="calendar"></span><fmt:message key="orders.conditions.btn.currentMonth" /> 
		</button>
		<div class="dropdown-menu" aria-labelledby="button-conditions">
			      <a href="${urlOrders}/conditions/period/current-month" id="button-conditions-current-month" class="dropdown-item"><fmt:message key="orders.conditions.btn.currentMonth" /></a>
			      <a href="${urlOrders}/conditions/period/last-7-days" id="button-conditions-last-7-days" class="dropdown-item"><fmt:message key="orders.conditions.btn.last7Days" /></a>
			      <a href="${urlOrders}/conditions/period/last-30-days" id="button-conditions-last-30-days" class="dropdown-item"><fmt:message key="orders.conditions.btn.last30Days" /></a>
			      <a href="${urlOrders}/conditions/period/last-90-days" id="button-conditions-last-90-days" class="dropdown-item"><fmt:message key="orders.conditions.btn.last90Days" /></a>
		</div>
		 -->
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
			    <button id="button-add" type="button" class="btn btn-sm btn-light" onclick="onClickAddSupplierStockProduct()"><fmt:message key="main.btn.add" /></button>
			    <button id="button-update" type="button" class="btn btn-sm btn-light" disabled onclick="onClickUpdateSupplierStockProduct()"><fmt:message key="main.btn.update" /></button>
			    <button id="button-delete" type="button" class="btn btn-sm btn-danger" disabled onclick="onClickDeleteSupplierStockProduct()"><fmt:message key="main.btn.delete"/></button>
			    			    
			    <button id="button-products-synchronize" type="button" class="btn btn-sm btn-light"><fmt:message key="wiki.stock.btn.synchronizing"/></button>
			    <button id="button-price-reload" type="button" class="btn btn-sm btn-light"><fmt:message key="wiki.stock.btn.price.reload"/></button>
			    		
		</div>
		<br/>
 
          <div class="table-responsive">
            <table class="table table-striped table-sm">

            <thead>
				<tr>
					<th scope="col"><input id="checkbox-union" type="checkbox"></th>
					<th scope="col"><fmt:message key="wiki.stock.table.headers.no" /></th>
					<th scope="col"><fmt:message key="wiki.stock.table.headers.product.name" /></th>
					<th scope="col"><fmt:message key="wiki.stock.table.headers.product.category.name" /></th>
					<th scope="col"><fmt:message key="wiki.stock.table.headers.stock.quantity" /></th>		
					<th scope="col"><fmt:message key="wiki.stock.table.headers.product.quantity" /></th>
					<th scope="col"><fmt:message key="wiki.stock.table.headers.supplier.price" /></th>
					<th scope="col"><fmt:message key="wiki.stock.table.headers.product.price" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="supplierStockProduct" items="${supplierStockProducts}">			
				<tr class="table-">				
		 				<td class="table-"><input id="checkbox-${supplierStockProduct.id}" type="checkbox" data-no="${supplierStockProduct.id}"></td>
		 				<td data-original-title="${supplierStockProduct.comment}" data-toggle="tooltip" data-placement="bottom" title="">${supplierStockProduct.product.id}</td>		 				
						<td>
						<c:set var = "productName" value = '${supplierStockProduct.product.name.length() < 80 ? supplierStockProduct.product.name : fn:substring(supplierStockProduct.product.name, 0, 80).concat("...")}' />
						<a href="${urlWiki}/products/${supplierStockProduct.product.id}/update/products">[${supplierStockProduct.product.viewSKU}] ${productName}</a>
						
						</td>
					
						<td>${supplierStockProduct.product.category.name}</td>
						<td class="text-right">${supplierStockProduct.product.stockQuantity}</td>
						<td class="text-right">${supplierStockProduct.product.quantity}</td>						
						<td class="text-right">${supplierStockProduct.product.supplierPrice}</td>
						<td class="text-right">${supplierStockProduct.product.price}</td>
				</tr>			
				</c:forEach>
	         </tbody>
	         </table>

	         <p class="text-right"><fmt:message key="wiki.stock.table.footers.amount.supplier" />&nbsp;<strong><fmt:formatNumber type = "currency" value = "${stock.supplierAmount}" /></strong></p>
	         <p class="text-right"><fmt:message key="wiki.stock.table.footers.amount.bill" />&nbsp;<strong><fmt:formatNumber type = "currency" value = "${stock.billAmount}" /></strong></p>
	         
	         <p class="text-right"><fmt:message key="wiki.stock.table.footers.amount.totalSupplier" />&nbsp;<strong><fmt:formatNumber type = "currency" value = "${stock.totalSupplierAmount}" /></strong></p>
	         <p class="text-right"><fmt:message key="wiki.stock.table.footers.amount.totalBill" />&nbsp;<strong><fmt:formatNumber type = "currency" value = "${stock.totalBillAmount}" /></strong></p>
   
	         
            
<%@ include file = "../fragments/header2html2navbar2finish.jsp" %>               
            
</div>

<!-- Modal -->
<div class="modal fade" id="confirm-modal" tabindex="-1" role="dialog" aria-labelledby="confirm-modal-title" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="confirm-modal-title"><fmt:message key="wiki.stock.form.modal.delete.header" /></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <p><fmt:message key="wiki.stock.form.modal.delete.text" /></p>
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
		$('#nav-link-wiki-stock').addClass('active');
		$('#nav-link-wiki-stock i').removeClass('text-dark').addClass('text-info');
		$('#select-stock-supplier').val(${inputSupplier.id});
		$('#select-stock-category').val(${inputProductCategory.id});
						
		$('#btn-supplier-find').click(function() {
			window.location.href = '${urlWiki}/stock-products/suppliers/' + $('#select-stock-supplier').val() + '/product-categories/' + $('#select-stock-category').val();
		});
		
		if ($('#select-stock-supplier').val() == 1) {
			$('#button-price-reload').prop('hidden', false);
		} else {
			$('#button-price-reload').prop('hidden', true);
		}
		
		function onClickAddSupplierStockProduct() {
			console.log('${urlWiki}/stock-products/add');			
			window.location = '${urlWiki}/stock-products/add';
		}
		function onClickUpdateSupplierStockProduct() {
			console.log('onClickUpdateSupplierStockProduct: ' + $('#button-update').attr('href'));
			window.location =  $('#button-update').attr("href");			
		}		
		function onClickDeleteSupplierStockProduct() {
			console.log('onClickDeleteSupplierStockProduct: ' + $('#button-erase').attr('href-data'));
			$('#confirm-modal').modal({keyboard: false});
			
		}
		
		$('#button-products-synchronize').click(function() {
			window.location.href = '${urlWiki}/products/synchronize';
		});

		
		$('#button-price-reload').click(function() {
			window.location.href = '${urlWiki}/stock-products/suppliers/sititek/price/reload';
		});
		
		
		$(document).ready(function() {		
			
			var $checkboxes = $('input[type="checkbox"]');
			$checkboxes.click(function() {
				var supplierStockProductId,
					updateHref,
					deleteHref;
				if (this.id == 'checkbox-union') {
					console.log('checkbox-union: ' + $('#checkbox-union').prop('checked')); 
					$checkboxes.prop('checked', $('#checkbox-union').prop('checked'));
					
					$('#button-update').attr('disabled', '').attr('href', '#');        	    	
    				$('#button-delete').attr('disabled', '').attr('href', '#');    				
    				$('#button-operates').attr('disabled', '');    				
    				$('#button-reportes').attr('disabled', '');
					
					return;
				}
				if ($checkboxes.filter(':checked').length == 0) {
					$('#button-update').attr('disabled', '').attr('href', '#');        	    	
    				$('#button-delete').attr('disabled', '').attr('href', '#');    				
    				$('#button-operates').attr('disabled', '');    				
    				$('#button-reportes').attr('disabled', '');
				}	
				
				$checkboxes.filter(':checked').not(this).prop('checked', false);
				
				if ($(this).prop('checked')) {
					supplierStockProductId = this.id.substring(9, this.id.length);
        	    	
        	    	updateHref = '${urlWiki}/stock-products/' + supplierStockProductId + '/update';        	    	        	    	
        	    	deleteHref = '${urlWiki}/stock-products/' + supplierStockProductId + '/delete';
        	    	        	    	        	    	
        	    	console.log(updateHref);
        	    	console.log(deleteHref);
        	    	
        	    	$('#button-update').removeAttr('disabled').attr('href', updateHref);        	    	             	    	
    				$('#button-delete').removeAttr('disabled').attr('href', deleteHref);
    				    				  		
    			} else {
    				// ...
    			}
    	    	
    	        //$unique.filter(':checked').not(this).removeAttr('checked');
    	    });
		});		
		
		$('#button-modal-confirm-ok').click(function() {
			var deleteHref = $('#button-delete').attr('href');
			console.log('#modal-confirm-ok click: ' + deleteHref);
			document.location.href = deleteHref;							
		});
		
</script>   
	 
  
<%@ include file = "../fragments/footer2html.jsp" %>


