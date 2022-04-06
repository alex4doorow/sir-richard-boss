<%@ include file = "../fragments/header2init.jsp" %>
<%@ include file = "../fragments/header2html2init.jsp" %>
  
<title><fmt:message key="app.title" /> | <fmt:message key="order.form.changeStatus.title" /></title>
    
<%@ include file = "../fragments/header2html2navbar2start.jsp" %>   
<!-- <h1 class="h2"><fmt:message key="index.header" /></h1>  -->
<div
	class="d-flex justify-content-start flex-wrap flex-md-nowrap align-items-start">					
		
			<fmt:formatDate value="${order.orderDate}" pattern="dd.MM.yyyy"	var="orderDateFormated" />
			<c:set var="strOrderDate" value="${orderDateFormated}" />
			<fmt:message key="order.form.changeStatus.headers.h1" var="orderHeaderInfo">
				<fmt:param value="${order.viewNo}" />
				<fmt:param value="${strOrderDate}" />
			</fmt:message>
			<h1 class="h4">
				<c:out value="${orderHeaderInfo}" />
			</h1>	

				
</div>

<div
	class="d-flex justify-content-start flex-wrap flex-md-nowrap align-items-start border-bottom- mb-2">	
	<h2 id="header-customer-longName" class="h5">${order.customer.viewLongNameWithContactInfo}&nbsp;<span class="badge badge-secondary">${order.customer.id}</span></h2>	
</div>

<div>
	<button id="btn-update-order" type="button" class="btn btn-light">
		<fmt:message key="main.btn.updateData" />
	</button>
	<button id="btn-show-order" type="button" class="btn btn-light">
		<fmt:message key="main.btn.showData" />
	</button>
	<button id="btn-status-order" type="button" class="btn btn-light" disabled>
		<fmt:message key="main.btn.statusData" />
	</button>
	<button id="btn-clone-order" type="button" class="btn btn-light">
		<fmt:message key="main.btn.cloneData" />
	</button>
	<button id="btn-save-order" type="button" class="btn btn-primary">
		<fmt:message key="main.btn.save" />
	</button>
</div>
<br/>


			<spring:url value="/orders/${orderForm.id}/change-status/save/${listType}" var="orderActionUrl" />
			<form:form class="needs-validation" method="post" modelAttribute="orderForm" action="${orderActionUrl}">
				<form:hidden path="id" />
				<form:hidden path="no" />
				<form:hidden path="orderDate" />
				<form:hidden path="delivery.address.id" />
				<form:hidden path="delivery.address.carrierInfo.courierInfo.deliveryDate" />
								
				<form:hidden path="formCustomer.id" />								
				<form:hidden path="formCustomer.customerType" />
				
				<form:hidden path="formCustomer.firstName" />
				<form:hidden path="formCustomer.phoneNumber" />
				
				<form:hidden path="formCustomer.shortName" />
				<form:hidden path="formCustomer.longName" />				
				<form:hidden path="formCustomer.inn" />			
				<form:hidden path="formCustomer.mainContact.firstName" />
				<form:hidden path="formCustomer.mainContact.phoneNumber" />
				<form:hidden path="formCustomer.mainContact.email" />
				<form:hidden path="formCustomer.mainAddress.id" />
								
				<div class="form-row">
					<div class="form-group col-4">
						<label for="select-order-type"><fmt:message key="order.form.fields.orderType" /></label> 
						<form:select path="orderType" id="select-order-type" class="form-control form-control-sm">
						    <form:options items="${orderTypes}" itemLabel="annotation" />
						</form:select>
					</div>					
				</div>
				
				<div class="form-row">
					<div class="form-group col-4">
						<label for="select-source-type"><fmt:message key="order.form.fields.sourceType" /></label> 
						<form:select path="sourceType" id="select-source-type" class="form-control form-control-sm">
						    <form:options items="${sourceTypes}" itemLabel="annotation" />
						</form:select>
					</div>					
				</div>
				
				<div class="form-row">
					<div class="form-group col-4">
						<label for="select-payment-type"><fmt:message key="order.form.payment.fields.type" /></label> 
						<form:select path="paymentType" id="select-payment-type" class="form-control form-control-sm">
						    <form:options items="${paymentTypes}" itemLabel="annotation" />
						</form:select>
					</div>					
				</div>
				
				<div class="form-row">
					<div class="form-group col-4">
						<label for="select-product-category"><fmt:message key="order.form.fields.productCategory" /></label> 
						<form:select path="productCategory.id" id="select-product-category" class="form-control form-control-sm">
						    <form:options items="${productCategories}" itemValue="id" itemLabel="name" />
						</form:select>
					</div>					
				</div>
				
				
				<div class="form-row">
					<div class="form-group col-4">
						<label for="select-order-status"><fmt:message key="order.form.fields.orderStatus" /></label> 
						<form:select path="status" id="select-order-status" class="form-control form-control-sm">
						    <form:options items="${orderStatuses}" itemLabel="annotation" />
						</form:select>
					</div>					
				</div>					
				<div class="form-row">
					<div class="form-group col-4">
						<label for="input-payment-annotation"><fmt:message key="order.form.fields.annotation" /></label>
						<form:textarea path="annotation" id="input-payment-annotation" class="form-control form-control-sm" 
							rows="2" maxlength="255"/>	
					</div>
				</div>				
				<div class="form-row">
					<div class="form-group col-4">
						<label for="input-delivery-trackCode"><fmt:message key="order.form.delivery.fields.trackCode" /></label> 
						<form:input path="delivery.trackCode" type="text" class="form-control form-control-sm" id="input-delivery-trackCode" />
					</div>
				</div>

				<h5 class="mb-3"><fmt:message key="order.form.headers.delivery.sendMessage" /></h5>								
				<div class="custom-control custom-checkbox">
					<form:checkbox path="sendMessage" class="custom-control-input" id="checkbox-send-message"/>
              		<label for="checkbox-send-message" class="custom-control-label"><fmt:message key="order.form.delivery.fields.isSendMessage" /></label>
            	</div>            	
            	         	
				
				<hr class="mb-4">	
				<button id="btn-submit-order" type="submit" class="btn btn-primary"><fmt:message key="main.btn.save" /></button>
			</form:form>
			<br/>
 			<div id="feedback"></div>
			

<%@ include file = "../fragments/header2html2navbar2finish.jsp" %>    
     
<!-- alert block ets -->
   
<%@ include file = "../fragments/footer2init.jsp" %>
<!-- local java script -->
<script>      
	$('#nav-link-orders').addClass('active');	
	$('#checkbox-send-message').prop('checked', false);
	//$('#checkbox-send-message').prop('disabled', true);
	
	$('#btn-show-order').click(function() {
		window.location.href = '${urlHome}orders/${order.id}/${listType}';
	});
	$('#btn-update-order').click(function() {
		window.location.href = '${urlHome}orders/${order.id}/update/${listType}';
	});
	$('#btn-status-order').click(function() {
		window.location.href = '${urlHome}orders/${order.id}/change-status/${listType}';
	});
	$('#btn-clone-order').click(function() {
		window.location.href = '${urlHome}orders/${order.id}/clone';
	});	
	
	$('#btn-save-order').click(function() {		
		$('#btn-submit-order').click();
	});
	
	(function() {
	  'use strict';
	  window.addEventListener('load', function() {
	    // Fetch all the forms we want to apply custom Bootstrap validation styles to
	    var forms = document.getElementsByClassName('needs-validation');
	    // Loop over them and prevent submission
	    var validation = Array.prototype.filter.call(forms, function(form) {
	      form.addEventListener('submit', function(event) {
	        if (form.checkValidity() === false) {
	          event.preventDefault();
	          event.stopPropagation();
	        }
	        form.classList.add('was-validated');
	      }, false);
	    });
	  }, false);
	})();
	
	$('#select-order-status').change(function() {		
		var status = $('#select-order-status').val();				
		if ((status == 'DELIVERING' || status == 'READY_GIVE_AWAY' || status == 'READY_GIVE_AWAY_TROUBLE') && $('#input-delivery-trackCode').val().trim() != '') {
			//$('#checkbox-send-message').prop('disabled', false);
			$('#checkbox-send-message').prop('checked', true);
		} else {
			//$('#checkbox-send-message').prop('disabled', true);
			$('#checkbox-send-message').prop('checked', false);
		}
	});	
	
	
</script>  
<%@ include file = "../fragments/footer2html.jsp" %>
