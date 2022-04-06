<%@ include file = "../fragments/header2init.jsp" %>
<%@ include file = "../fragments/header2html2init.jsp" %>
  
<title><fmt:message key="app.title" /> | <fmt:message key="order.form.billExpiredStatus.title" /></title>
    
<%@ include file = "../fragments/header2html2navbar2start.jsp" %>   
<!-- <h1 class="h2"><fmt:message key="index.header" /></h1>  -->
<div
	class="d-flex justify-content-start flex-wrap flex-md-nowrap align-items-start">					
		
			<fmt:formatDate value="${order.orderDate}" pattern="dd.MM.yyyy"	var="orderDateFormated" />
			<c:set var="strOrderDate" value="${orderDateFormated}" />
			<fmt:message key="order.form.billExpiredStatus.headers.h1" var="orderHeaderInfo">
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
<br/>
			<spring:url value="/orders/${orderForm.id}/bill-expired-status/save/${listType}" var="orderActionUrl" />
			<form:form class="needs-validation" method="post" modelAttribute="orderForm" action="${orderActionUrl}">
				<form:hidden path="id" />
				<form:hidden path="no" />
				<form:hidden path="orderDate" />
				
				<form:hidden path="formCustomer.customerType" />
				
				<form:hidden path="formCustomer.firstName" />
				<form:hidden path="formCustomer.phoneNumber" />
				
				<form:hidden path="formCustomer.shortName" />
				<form:hidden path="formCustomer.longName" />				
				<form:hidden path="formCustomer.inn" />			
				<form:hidden path="formCustomer.mainContact.firstName" />
				<form:hidden path="formCustomer.mainContact.phoneNumber" />
				<form:hidden path="formCustomer.mainContact.email" />
								
				<div class="form-row">
					<div class="form-group col-3">
						<label for="select-order-type"><fmt:message key="order.form.fields.orderType" /></label> 
						<form:select path="orderType" id="select-order-type" class="form-control form-control-sm">
						    <form:options items="${orderTypes}" itemLabel="annotation" />
						</form:select>
					</div>					
					<div class="form-group col-1">
						<label for="input-order-offer-count-day">
							<fmt:formatDate pattern="dd.MM.yyyy" value="${order.offer.expiredDate}" />
						</label>					
						<form:input path="offer.countDay" type="number" min="0" max="90" class="form-control form-control-sm" id="input-order-offer-count-day" />
					</div>			
				</div>	
								
				<div class="form-row">
					<div class="form-group col">
						<label for="input-payment-annotation"><fmt:message key="order.form.billExpiredStatus.textMessage" /></label>
						<form:textarea path="textMessage" id="input-payment-annotation" class="form-control form-control-sm" 
							rows="10" maxlength="1024"/>	
					</div>
				</div>			
					
				<h5 class="mb-3"><fmt:message key="order.form.headers.delivery.sendMessage" /></h5>								
				<div class="custom-control custom-checkbox">
					<form:checkbox path="sendMessage" class="custom-control-input" id="checkbox-send-message"/>
              		<label for="checkbox-send-message" class="custom-control-label"><fmt:message key="order.form.delivery.fields.isSendEmail" /></label>
            	</div>  	
				
				<hr class="mb-4">	
				<button type="submit" class="btn btn-primary"><fmt:message key="main.btn.save" /></button>
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
	
	$('#checkbox-send-message').prop('checked', true);

	
</script>  
<%@ include file = "../fragments/footer2html.jsp" %>
