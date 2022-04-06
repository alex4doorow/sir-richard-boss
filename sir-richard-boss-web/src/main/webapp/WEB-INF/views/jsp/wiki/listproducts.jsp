<%@ include file = "../fragments/header2init.jsp" %>
<%@ include file = "../fragments/header2html2init.jsp" %>

<title><fmt:message key="app.title" /> | <fmt:message key="wiki.products.title" /></title>

<%@ include file = "../fragments/header2html2navbar2start.jsp" %>
<div
	class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pb-2 mb-3 border-bottom">
	<!-- local content -->
	<h1 class="h2">
		<fmt:message key="wiki.products.header" />
	</h1>

	<div class="btn-toolbar mb-2 mb-md-0">	
		<div class="btn-group mr-2">
		
					<spring:url value="/wiki/products/conditions/filter/exec" var="productConditionsExecActionUrl" />
					<spring:message code="wiki.products.conditions.form.fields.placeholder.name" var="wikiProductsConditionsFormFieldsPlaceholderName"/> 
					<form:form id="product-conditions-form" class="needs-validation" method="post" modelAttribute="productConditions" action="${productConditionsExecActionUrl}">
						<form:input path="sku" type="text" class="form-control form-control-sm" id="input-search-by-products-conditions-name" placeholder='${wikiProductsConditionsFormFieldsPlaceholderName}'  />							
					</form:form>
		
			<button id="btn-product-find" class="btn btn-sm btn-outline-primary"><fmt:message key="main.btn.find" /></button>
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
			    <!-- <button id="button-products-synchronize" type="button" class="btn btn-sm btn-light"><fmt:message key="wiki.products.btn.synchronizing"/></button>  --> 
			    					    		
		</div>
		<br/>
 
          <div class="table-responsive">
            <table class="table table-striped table-sm">

            <thead>
				<tr>
					<th scope="col"><input id="checkbox-union" type="checkbox"></th>
					<th scope="col"><fmt:message key="wiki.products.table.headers.no" /></th>
					<th scope="col"><fmt:message key="wiki.products.table.headers.sku" /></th>
					<th scope="col"><fmt:message key="wiki.products.table.headers.name" /></th>
					<th scope="col"><fmt:message key="wiki.products.table.headers.model" /></th>
					<th scope="col"><fmt:message key="wiki.products.table.headers.price" /></th>
					<th scope="col"><fmt:message key="wiki.products.table.headers.quantity" /></th>		
					<th scope="col"><fmt:message key="wiki.products.table.headers.status" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="product" items="${products}">			
				<tr class="table-">				
		 				<td class="table-"><input id="checkbox-${product.id}" type="checkbox" data-no="${product.id}"></td>
		 				<td>${product.id}</td>		 				
						<td>${product.sku}</td>
						<td data-original-title="${product.name}" data-toggle="tooltip" data-placement="bottom" title="">${product.viewNameShort}</td>
						<td data-original-title="${product.model}" data-toggle="tooltip" data-placement="bottom" title="">${product.viewModelShort}</td>
						<td class="text-right"> <fmt:formatNumber type = "currency" value = "${product.price}" /> </td>
						<td class="text-right">${product.quantity}</td>
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
				</tr>			
				</c:forEach>
	         </tbody>
	         </table>
	         
            
<%@ include file = "../fragments/header2html2navbar2finish.jsp" %>               
            
</div>
   
   
<%@ include file = "../fragments/footer2init.jsp" %>
<!-- local java script -->
<script>
	$('#nav-link-products').addClass('active');
	$('#nav-link-products i').removeClass('text-dark').addClass('text-info');
				
		$('#button-products-synchronize').click(function() {
			window.location =  $('#button-products-synchronize').attr("href");
		});
		/*		
		$('#btn-product-find').click(function() {
			window.location.href = '${urlWiki}/products/context/' + $('#input-search-by-products-conditions').val();
		});
		*/
		
		$('#btn-product-find').click(function() {
			$('#product-conditions-form').submit();
			
			//window.location.href = '${urlWiki}/products/context/' + $('#input-search-by-products-conditions').val();
		});
		
		function onClickUpdateProduct() {
			console.log('onClickUpdateProduct: ' + $('#button-update').attr('href'));
			window.location =  $('#button-update').attr("href");			
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
					synchronizeHref = '${urlWiki}/products/outer/synchronize/' + productId;
					
					$('#button-products-synchronize').removeAttr('disabled').attr('href', synchronizeHref);
					$('#button-update').removeAttr('disabled').attr('href', updateHref);        	
    	        	    	
        	    	console.log(updateHref);
    				    				  		
    			} else {
    				// ...
    			}

    	    });
		});		
		
</script>   
	 
  
<%@ include file = "../fragments/footer2html.jsp" %>


