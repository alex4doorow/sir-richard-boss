<%@ include file = "../fragments/header2init.jsp" %>
<%@ include file = "../fragments/header2html2init.jsp" %>
  
<title><fmt:message key="app.title" /> | <fmt:message key="order.show.title" /></title>
    
<%@ include file = "../fragments/header2html2navbar2start.jsp" %>   
<!-- <h1 class="h2"><fmt:message key="index.header" /></h1>  -->
<div class="d-flex justify-content-start flex-wrap flex-md-nowrap align-items-start">

	<fmt:formatDate value="${order.orderDate}" pattern="dd.MM.yyyy"	var="orderDateFormated" />
	<c:set var="strOrderDate" value="${orderDateFormated}" />
	<fmt:message key="order.show.headers.h1" var="orderHeaderInfo">
		<fmt:param value="${order.viewNo}" />
		<fmt:param value="${strOrderDate}" />
	</fmt:message>
	<h1 class="h4">
		<c:out value="${orderHeaderInfo}" />
	</h1>
</div>
<div
	class="d-flex justify-content-start flex-wrap flex-md-nowrap align-items-start border-bottom- mb-2">
	<h2 class="h5">${order.customer.viewLongNameWithContactInfo}&nbsp;<span class="badge badge-secondary">${order.customer.id}</span></h2>
</div>

<div>
	<button id="btn-update-order" type="button" class="btn btn-primary">
		<fmt:message key="main.btn.updateData" />
	</button>
	<button id="btn-show-order" type="button" class="btn btn-light" disabled>
		<fmt:message key="main.btn.showData" />
	</button>
	<button id="btn-status-order" type="button" class="btn btn-light">
		<fmt:message key="main.btn.statusData" />
	</button>
	<button id="btn-clone-order" type="button" class="btn btn-light">
		<fmt:message key="main.btn.cloneData" />
	</button>
</div>
<br/>

<ul class="nav nav-tabs" id="orderTab" role="tablist">
	<li class="nav-item"><a class="nav-link active" id="order-tab"
		data-toggle="tab" href="#order" role="tab" aria-controls="order"
		aria-selected="true"><fmt:message key="order.show.tabs.order" /></a>
	</li>
	<li class="nav-item"><a class="nav-link" id="customer-tab"
		data-toggle="tab" href="#customer" role="tab" aria-controls="customer"
		aria-selected="false"><fmt:message key="order.show.tabs.customer" /></a>
	</li>
	<li class="nav-item"><a class="nav-link" id="delivery-tab"
		data-toggle="tab" href="#delivery" role="tab" aria-controls="delivery"
		aria-selected="false"><fmt:message key="order.show.tabs.delivery" /></a>
	</li>
	<li class="nav-item"><a class="nav-link" id="amount-tab"
		data-toggle="tab" href="#amount" role="tab" aria-controls="amount"
		aria-selected="false"><fmt:message key="order.show.tabs.calculate" /></a>
	</li>
</ul>
<div class="tab-content" id="orderTabContent">
	<div class="tab-pane fade show active" id="order" role="tabpanel"
		aria-labelledby="order-tab">
		<div class="container-fluid">
			<br />
			<div class="row">
				<label class="col-sm-3"><fmt:message key="order.show.fields.id" /></label>
				<div class="col-sm-9">${order.id}</div>
			</div>
			<div class="row">
				<label class="col-sm-3"><fmt:message
						key="order.show.fields.no" /></label>
				<div class="col-sm-9">
					<strong>${order.viewNo}</strong>
				</div>
			</div>

			<div class="row">
				<label class="col-sm-3"><fmt:message key="order.show.fields.orderDate" /></label>
				<div class="col-sm-9">
					<strong><fmt:formatDate pattern="dd.MM.yyyy" value="${order.orderDate}" /></strong>
				</div>
			</div>
			<div class="row">
				<label class="col-sm-3"><fmt:message key="order.show.fields.orderType" /></label>
				<div class="col-sm-9">${order.orderType.annotation}</div>
			</div>
			<div class="row">
				<label class="col-sm-3"><fmt:message key="order.show.fields.sourceType" /></label>
				<div class="col-sm-9">${order.sourceType.annotation}</div>
			</div>
			<div class="row">
				<label class="col-sm-3"><fmt:message key="order.show.fields.advertType" /></label>
				<div class="col-sm-9">${order.advertType.annotation}</div>
			</div>
			<div class="row">
				<label class="col-sm-3"><fmt:message key="order.show.fields.paymentType" /></label>
				<div class="col-sm-9">${order.paymentType.annotation}</div>
			</div>
			<div class="row">
				<label class="col-sm-3"><fmt:message key="order.show.fields.productCategory" /></label>
				<div class="col-sm-9">${order.productCategory.name}</div>
			</div>
			<div class="row">
				<label class="col-sm-3"><fmt:message key="order.show.fields.store" /></label>
				<div class="col-sm-9">${order.store.site}</div>
			</div>
			<div class="row">
				<label class="col-sm-3"><fmt:message key="order.show.fields.annotation" /></label>
				<div class="col-sm-9">${order.annotation}</div>
			</div>
			<!-- crm id
			<div class="row">
				<label class="col-sm-3"><fmt:message key="order.show.fields.crm" /></label>
				<div class="col-sm-9">${order.annotation}</div>
			</div>			
			<div class="row">
				<label class="col-sm-3"><fmt:message key="order.show.fields.crmParentId" /></label>
				<div class="col-sm-9">${order.annotation}</div>
			</div>
			 -->
			
			
			
			<div class="row">
				<label class="col-sm-3"><fmt:message key="order.show.fields.status" /></label>
				<div class="col-sm-9">
					<strong>${order.status.annotation}</strong>
				</div>
			</div>
			<div class="row">
				<label class="col-sm-3"><fmt:message key="order.show.fields.addedDate" /></label>
				<div class="col-sm-9">
					<fmt:formatDate pattern="dd.MM.yyyy HH:mm:ss" value="${order.addedDate}" />
				</div>
			</div>
			<br />
			<h6>
				<fmt:message key="order.show.items.header" />
			</h6>

			<table class="table table-striped table-sm">

				<thead>
					<tr>
						<th scope="col"><fmt:message key="order.show.items.table.headers.no" /></th>
						<th scope="col"><fmt:message key="order.show.items.table.headers.product" /></th>
						<th scope="col"><fmt:message key="order.show.items.table.headers.unit" /></th>
						<th scope="col"><fmt:message key="order.show.items.table.headers.quantity" /></th>
						<th scope="col"><fmt:message key="order.show.items.table.headers.price" /></th>
						<th scope="col"><fmt:message key="order.show.items.table.headers.discountRate" /></th>
						<th scope="col"><fmt:message key="order.show.items.table.headers.supplierAmount" /></th>
						<th scope="col"><fmt:message key="order.show.items.table.headers.amount" /></th>
					</tr>
				</thead>
				<tbody>
				<c:forEach var="orderItem" items="${order.items}">

						<tr>
							<td>${orderItem.no}</td>							
							<td>${orderItem.product.viewName}</td>
							<td><fmt:message key="order.show.items.table.headers.unit.value" /></td>
							<td class="text-center">${orderItem.quantity}</td>
							<td class="text-right"><fmt:formatNumber type = "currency" value = "${orderItem.price}" /></td>
							<td class="text-right"><fmt:formatNumber type = "number" value = "${orderItem.discountRate}" />%</td>
							<td class="text-right"><fmt:formatNumber type = "currency" value = "${orderItem.supplierAmount}" /></td>
							<td class="text-right"><fmt:formatNumber type = "currency" value = "${orderItem.amount}" /></td>
						</tr>

				</c:forEach>
				<tr>
							<td></td>
							<td></td>
							<td></td>
							<td class="text-center"></td>
							<td class="text-right"></td>
							<td class="text-right"></td>
							<td class="text-right"><strong><fmt:formatNumber type = "currency" value = "${order.amounts.supplier}" /></strong></td>
							<td class="text-right"><strong><fmt:formatNumber type = "currency" value = "${order.amounts.total}" /></strong></td>
				</tr>
				</tbody>
				<tfoot>
					<tr>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td><fmt:message key="order.show.items.table.footer.delivery" /></td>
								<td></td>
								<td class="text-right"><fmt:formatNumber type = "currency" value = "${order.delivery.price}" /></td>
					</tr>				
					<tr>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td><strong><fmt:message key="order.show.items.table.footer.totalWithDelivery" /></strong></td>
								<td></td>
								<td class="text-right"><strong><fmt:formatNumber type = "currency" value = "${order.amounts.getValue(OrderAmountTypes.TOTAL_WITH_DELIVERY)}" /></strong></td>
					</tr>
				</tfoot>
			</table>
			<h6><fmt:message key="order.show.statuses.header" /></h6>
			<div class="row">
				<div class="col-sm-6">
					<table class="table table-striped table-sm">
						<thead>
							<tr>
								<th scope="col"><fmt:message key="order.show.statuses.table.headers.no" /></th>
								<th scope="col"><fmt:message key="order.show.statuses.table.headers.status" /></th>
								<th scope="col"><fmt:message key="order.show.statuses.table.headers.crmStatus" /></th>
								<th scope="col"><fmt:message key="order.show.statuses.table.headers.crmSubStatus" /></th>
								<th scope="col"><fmt:message key="order.show.statuses.table.headers.addedDate" /></th>
							</tr>
						</thead>
						<tbody>
						<c:forEach var="orderStatusItem" items="${order.statuses}">
							<tr>
								<td>${orderStatusItem.no}</td>
								<td>${orderStatusItem.status.annotation}</td>
								<td>${orderStatusItem.crmStatus}</td>
								<td>${orderStatusItem.crmSubStatus}</td>								
								<td><fmt:formatDate pattern="dd.MM.yyyy HH:mm:ss" value="${orderStatusItem.addedDate}" /></td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
			<br>
			<h6><fmt:message key="order.show.crm.header"/></h6>
			<div class="row">
				<div class="col-sm-5">
					<table class="table table-striped table-sm">
						<thead>
							<tr>
								<th scope="col"><fmt:message key="order.show.crm.table.headers.crm" /></th>
								<th scope="col"><fmt:message key="order.show.crm.table.headers.parentId" /></th>
								<th scope="col"><fmt:message key="order.show.crm.table.headers.parentCode" /></th>
							</tr>
						</thead>
						<tbody>
						<c:forEach var="orderExternalCrm" items="${order.externalCrms}">
							<tr>								
								<td>${orderExternalCrm.crm.annotation}</td>
								<td>${orderExternalCrm.parentId}</td>
								<td>${orderExternalCrm.parentCode}</td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</div>
			</div>			
		</div>
		
	</div>
	<div class="tab-pane fade" id="customer" role="tabpanel"
		aria-labelledby="customer-tab">

		<div class="container-fluid">
			<br />
			<div class="row">
				<label class="col-sm-3"><fmt:message
						key="order.show.customer.fields.type" /></label>
				<div class="col-sm-9">${order.customer.type.longName}</div>
			</div>
			
			<div class="row">
				<label class="col-sm-3"><fmt:message
						key="order.show.customer.fields.country" /></label>
				<div class="col-sm-9">${order.customer.country.annotation}</div>
			</div>

			
			<div class="row">
				<label class="col-sm-3"><fmt:message
						key="order.show.customer.fields.address" /></label>
				<div class="col-sm-9">${order.customer.mainAddress.address}</div>
			</div>
			<div class="row">
				<label class="col-sm-3"><fmt:message
						key="order.show.customer.fields.status" /></label>
				<div class="col-sm-9 mb-4">${order.customer.status.annotation}</div>
			</div>
			
			
			<c:choose>
				<c:when test="${order.customer.type == CustomerTypes.CUSTOMER || order.customer.type == CustomerTypes.FOREIGNER_CUSTOMER}">
				
				<div class="row">
					<label class="col-sm-3"><fmt:message
							key="order.show.customer.fields.lastName" /></label>
					<div class="col-sm-9">${order.customer.lastName}</div>
				</div>
				<div class="row">
					<label class="col-sm-3"><fmt:message
							key="order.show.customer.fields.firstName" /></label>
					<div class="col-sm-9">
						<strong>${order.customer.firstName}</strong>
					</div>
				</div>
				<div class="row">
					<label class="col-sm-3"><fmt:message
							key="order.show.customer.fields.middleName" /></label>
					<div class="col-sm-9">${order.customer.middleName}</div>
				</div>
				
				<div class="row">
					<label class="col-sm-3"><fmt:message
							key="order.show.customer.fields.telephone" /></label>
					<div class="col-sm-9">
						<strong>${order.customer.phoneNumber}</strong>
					</div>
				</div>
				<div class="row">
					<label class="col-sm-3"><fmt:message
							key="order.show.customer.fields.email" /></label>
					<div class="col-sm-9">${order.customer.email}</div>
				</div>					
				
				</c:when>
			</c:choose>
			
			<c:choose>
				<c:when test="${order.customer.type == CustomerTypes.COMPANY || order.customer.type == CustomerTypes.BUSINESSMAN || order.customer.type == CustomerTypes.FOREIGNER_COMPANY}">
				<h6><fmt:message key="order.show.customer.company.header" /></h6>
				
				<div class="row">
					<label class="col-sm-3"><fmt:message
							key="order.show.customer.fields.company.inn" /></label>
					<div class="col-sm-9">${order.customer.inn}</div>
				</div>
				<div class="row">
					<label class="col-sm-3"><fmt:message
							key="order.show.customer.fields.company.shortName" /></label>
					<div class="col-sm-9"><strong>${order.customer.shortName}</strong></div>
				</div>				
				<div class="row">
					<label class="col-sm-3"><fmt:message
							key="order.show.customer.fields.company.longName" /></label>
					<div class="col-sm-9 mb-4">${order.customer.longName}</div>
				</div>				
				
				<h6><fmt:message key="order.show.customer.company.contact.header" /></h6>
				
				<div class="row">
					<label class="col-sm-3"><fmt:message
							key="order.show.customer.fields.company.contact.lastName" /></label>
					<div class="col-sm-9">
						<strong>${order.customer.mainContact.lastName}</strong>
					</div>
				</div>				
				<div class="row">
					<label class="col-sm-3"><fmt:message
							key="order.show.customer.fields.company.contact.firstName" /></label>
					<div class="col-sm-9">
						${order.customer.mainContact.firstName}
					</div>
				</div>
				<div class="row">
					<label class="col-sm-3"><fmt:message
							key="order.show.customer.fields.company.contact.middleName" /></label>
					<div class="col-sm-9">
						${order.customer.mainContact.middleName}
					</div>
				</div>
								
				<div class="row">
					<label class="col-sm-3"><fmt:message
							key="order.show.customer.fields.company.contact.telephone" /></label>
					<div class="col-sm-9">
						<strong>${order.customer.mainContact.phoneNumber}</strong>
					</div>
				</div>
				<div class="row">
					<label class="col-sm-3"><fmt:message
							key="order.show.customer.fields.company.contact.email" /></label>
					<div class="col-sm-9"><strong>${order.customer.mainContact.email}</strong></div>
				</div>						
				
				</c:when>
			</c:choose>				
		</div>

	</div>
	<div class="tab-pane fade" id="delivery" role="tabpanel"
		aria-labelledby="delivery-tab">

		<div class="container-fluid">
			<br />
			<div class="row">
				<label class="col-sm-3"><fmt:message key="order.show.delivery.fields.type" /></label>
				<div class="col-sm-9">${order.delivery.deliveryType.annotation}</div>
			</div>
			<div class="row">
				<label class="col-sm-3"><fmt:message key="order.show.delivery.fields.paymentType" /></label>
				<div class="col-sm-9">${order.delivery.paymentDeliveryType.annotation}</div>
			</div>
			<div class="row">
				<label class="col-sm-3"><fmt:message key="order.show.delivery.fields.country" /></label>
				<div class="col-sm-9">${order.delivery.address.country.annotation}</div>
			</div>
			
			<div class="row">
				<label class="col-sm-3"><fmt:message
						key="order.show.delivery.fields.address" /></label>
				<div class="col-sm-9">
					<strong>${order.delivery.address.address}</strong>
				</div>
			</div>

			<div class="row">
				<label class="col-sm-3"><fmt:message
						key="order.show.delivery.fields.deliveryDate" /></label>
				<div class="col-sm-9">
					<strong><fmt:formatDate pattern="dd.MM.yyyy, EEE" value="${order.delivery.address.carrierInfo.courierInfo.deliveryDate}" /></strong>
				</div>
			</div>
			<div class="row">
				<label class="col-sm-3"><fmt:message
						key="order.show.delivery.fields.courierPeriod" /></label>
				<div class="col-sm-9">
					<strong>${order.delivery.address.carrierInfo.courierInfo.timeInterval()}</strong>
				</div>
			</div>
			<div class="row">
				<label class="col-sm-3"><fmt:message
						key="order.show.delivery.fields.price" /></label>
				<div class="col-sm-9">
					<strong><fmt:formatNumber type = "currency" value = "${order.delivery.price}" /></strong>
				</div>
			</div>
			<div class="row">
				<label class="col-sm-3"><fmt:message
						key="order.show.delivery.fields.annotation" /></label>
				<div class="col-sm-9">${order.delivery.annotation}</div>
			</div>
			<div class="row">
				<label class="col-sm-3"><fmt:message
						key="order.show.delivery.fields.postpayAmount" /></label>
				<div class="col-sm-9">
					<strong><fmt:formatNumber type = "currency" value = "${order.amounts.getValue(OrderAmountTypes.POSTPAY)}" /></strong>
				</div>
			</div>
			<div class="row">
				<label class="col-sm-3"><fmt:message
						key="order.show.delivery.fields.trackCode" /></label>
				<div class="col-sm-9">
					<strong>${order.delivery.trackCode}</strong>
				</div>
			</div>
		</div>
	</div>

	<div class="tab-pane fade" id="amount" role="tabpanel"
		aria-labelledby="amount-tab">
		<div class="container-fluid">
			<br />
			<div class="row">
				<label class="col-sm-2"><fmt:message
						key="order.show.calculate.fields.totalAmount" /></label>
				<div class="col-sm-2 text-right">
					<strong><fmt:formatNumber type = "currency" value = "${order.amounts.getValue(OrderAmountTypes.TOTAL_WITH_DELIVERY)}" /></strong>
				</div>
			</div>
			<div class="row">
				<label class="col-sm-2"><fmt:message
						key="order.show.calculate.fields.billAmount" /></label>
				<div class="col-sm-2 text-right"><fmt:formatNumber type = "currency" value = "${order.amounts.getValue(OrderAmountTypes.BILL)}" /></div>
			</div>
			<div class="row">
				<label class="col-sm-2"><fmt:message
						key="order.show.calculate.fields.supplierAmount" /></label>
				<div class="col-sm-2 text-right"><fmt:formatNumber type = "currency" value = "${order.amounts.getValue(OrderAmountTypes.SUPPLIER)}" /></div>
			</div>
			<div class="row">
				<label class="col-sm-2"><fmt:message
						key="order.show.calculate.fields.marginAmount" /></label>
				<div class="col-sm-2 text-right"><fmt:formatNumber type = "currency" value = "${order.amounts.getValue(OrderAmountTypes.MARGIN)}" /></div>
			</div>

			<div class="row">
				<label class="col-sm-2"><fmt:message
						key="order.show.calculate.fields.postpayAmount" /></label>
				<div class="col-sm-2 text-right">
					<strong><fmt:formatNumber type = "currency" value = "${order.amounts.getValue(OrderAmountTypes.POSTPAY)}" /></strong>
				</div>
			</div>
			<div class="row">
				<label class="col-sm-2"><fmt:message
						key="order.show.calculate.fields.postpayType" /></label>
				<div class="col-sm-2 text-right">${order.paymentType.annotation}</div>
			</div>
		</div>
	</div>
</div>

<%@ include file = "../fragments/header2html2navbar2finish.jsp" %>         
    
<!-- alert block ets -->
   
<%@ include file = "../fragments/footer2init.jsp" %>
<!-- local java script -->
<script>
	$('#nav-link-orders').addClass('active');
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
</script>
<%@ include file = "../fragments/footer2html.jsp" %>



  