</head>
  <body>
    <nav class="navbar navbar-dark sticky-top bg-dark flex-md-nowrap p-0">
      <a class="navbar-brand col-sm-3 col-md-2 mr-0" target="_blank" href="http://sir-richard.ru/admin"><fmt:message key="app.title" /></a>
            	
      <input id="input-search-by-order-conditions" class="search-by-order-no form-control form-control-dark w-100" type="text" placeholder='<fmt:message key="main.placeholder.find" />' aria-label='<fmt:message key="main.placeholder.find" />'>	       
      <ul class="navbar-nav px-3">
        <li class="nav-item text-nowrap">
          <a id="link-search-by-order-no" class="search-by-order-no nav-link" href="#"> <fmt:message key="main.btn.find" /> </a>
        </li>
      </ul>
    </nav>

    <div class="container-fluid">
      <div class="row">
        <nav class="col-md-2 d-none d-md-block bg-light sidebar">
          <div class="sidebar-sticky">
            <ul class="nav flex-column">
              <li class="nav-item">
                <a id="nav-link-home" class="nav-menus-item nav-link active" href="${urlHome}">
                  <i class="bi bi-house-door text-dark"></i>	              
                  <fmt:message key="main.menus.left.home" /> <span class="sr-only">(current)</span>
                </a>
              </li>
              <li class="nav-item">
                <a id="nav-link-orders" class="nav-menus-item nav-link" href="${urlHome}orders">                  
                  <i class="bi bi-files text-dark"></i>
                  <fmt:message key="main.menus.left.orders" />
                </a>
              </li>
              
              <li class="nav-item">
                <a id="nav-link-customers" class="nav-menus-item nav-link" href="#">                  
                  <i class="bi bi-people text-dark"></i>
                  <fmt:message key="main.menus.left.customers" />
                </a>
              </li>
              
              <li class="nav-item">
                <a id="nav-link-alarm" class="nav-menus-item nav-link" href="${urlHome}alarm/car">                  
                  <i class="bi bi-alarm text-dark"></i>
                  <fmt:message key="main.menus.left.alarm" />
                </a>
              </li> 
                            
            </ul>        
            
            <h6 class="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-muted">
              <span>Wiki</span>              
            </h6>
            <ul class="nav flex-column mb-2">
             <li class="nav-item">
                <a id="nav-link-products" class="nav-menus-item nav-link" href="${urlHome}wiki/products/-1">
                  <i class="bi bi-cart-check text-dark"></i>                  
                  <fmt:message key="main.menus.left.products" />
                </a>
              </li>
              <li class="nav-item">
                <a id="nav-link-wiki-stock" class="nav-link" href="${urlHome}wiki/stock-products">
                  <i class="bi bi-shop-window text-dark"></i>
                  <fmt:message key="main.menus.left.stock" />
                </a>
              </li>
              <!-- 
              <li class="nav-item">
                <a class="nav-link" href="#">
                  <span data-feather="file-text"></span>
                  Categories
                </a>
              </li>
              
              <li class="nav-item">
                <a class="nav-link" href="#">
                  <span data-feather="file-text"></span>
                  Delivery types
                </a>
              </li>
              -->
            </ul>
            
            <h6 class="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-muted">
              <span><fmt:message key="main.menus.left.integration" /></span>              
            </h6>
            <ul class="nav flex-column mb-2">
             <li class="nav-item">
                <a id="nav-link-ym-products" class="nav-menus-item nav-link" href="${urlHome}wiki/products/ym">
                  <i class="bi bi-cart-check text-dark"></i>
                  <fmt:message key="main.menus.left.ymProducts" />
                </a>
              </li>
              
              <li class="nav-item">
                <a id="nav-link-ozon-products" class="nav-menus-item nav-link" href="${urlHome}wiki/products/ozon">
				  <i class="bi bi-cart-check text-dark"></i>
                  <fmt:message key="main.menus.left.ozonProducts" />
                </a>
              </li>
              
            </ul>
            
            <h6 class="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-muted">
              <span><fmt:message key="main.menus.left.Reports" /></span>              
            </h6>
            
            <div class="sidebar-sticky">  
                <ul class="nav flex-column">
	                <li class="nav-item">	                
		                <a id="nav-link-report-product-sales" class="nav-link" href="${urlHome}reports/product-sales">
                  		  <i class="bi bi-printer text-dark"></i>
		                  <fmt:message key="main.menus.left.Reports.productSales" />
		                </a>
	                </li>
	                
	                <li class="nav-item">	                
		                <a id="nav-link-report-product-sales-by-query" class="nav-link" href="${urlHome}reports/product-sales-by-query">
                          <i class="bi bi-printer text-dark"></i>
		                  <fmt:message key="main.menus.left.Reports.productSalesByQuery" />
		                </a>
	                </li>
	                
	                <li class="nav-item">	                
		                <a id="nav-link-report-sales-funnel" class="nav-link" href="${urlHome}reports/sales-funnel">
                          <i class="bi bi-printer text-dark"></i>
		                  <fmt:message key="main.menus.left.Reports.salesFunnel" />
		                </a>
	                </li>
	                
                </ul>
	        </div>            
          </div>
        </nav>

        <main role="main" class="col-md-9 ml-sm-auto col-lg-10 pt-3 px-4">
        	
        
      