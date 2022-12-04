package ru.sir.richard.boss.web.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sir.richard.boss.model.data.SupplierStock;
import ru.sir.richard.boss.model.data.SupplierStockProduct;
import ru.sir.richard.boss.model.data.comparators.SupplierStockProductComparators;
import ru.sir.richard.boss.model.paging.Column;
import ru.sir.richard.boss.model.paging.Page;
import ru.sir.richard.boss.model.paging.PagingRequest;
import ru.sir.richard.boss.model.paging.SortingOrder;
import ru.sir.richard.boss.model.types.SupplierTypes;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class WikiRestService {
	
	private static final Comparator<SupplierStockProduct> EMPTY_COMPARATOR = (ssp1, ssp2) -> 0;

	@Autowired
	protected WikiService wikiService;
	
	public Page<SupplierStockProduct> getSupplierData(PagingRequest pagingRequest) {
	
		pagingRequest.setColumns(Stream.of("id", "no", "productName", "productCategoryName", "stockQuantity", "productQuantity", "supplierPrice", "productPrice")
                .map(Column::new)
                .collect(Collectors.toList()));

		log.info("getSupplierData: {}", pagingRequest);
				
		SupplierTypes supplier = SupplierTypes.SITITEK;
		SupplierStock stock = wikiService.getWiki().getSupplierStock(supplier, null);	
		List<SupplierStockProduct> supplierStockProducts = stock.getSupplierStockProduct();	
        return getPage(supplierStockProducts, pagingRequest);
    }
	
	private Page<SupplierStockProduct> getPage(List<SupplierStockProduct> supplierStockProducts, PagingRequest pagingRequest) {
        List<SupplierStockProduct> filtered = supplierStockProducts.stream()
                                           .sorted(sortItems(pagingRequest))
                                           .filter(filterItems(pagingRequest))
                                           .skip(pagingRequest.getStart())
                                           .limit(pagingRequest.getLength())
                                           .collect(Collectors.toList());

        long count = supplierStockProducts.stream()
                              .filter(filterItems(pagingRequest))
                              .count();

        Page<SupplierStockProduct> page = new Page<>(filtered);
        page.setRecordsFiltered((int) count);
        page.setRecordsTotal((int) count);
        page.setDraw(pagingRequest.getDraw());            
                
        return page;
    }
	
	private Predicate<SupplierStockProduct> filterItems(PagingRequest pagingRequest) {
 		
        if (pagingRequest.getSearch() == null || StringUtils.isEmpty(pagingRequest.getSearch().getValue())) {
            return order -> true;
        }        
        String value = pagingRequest.getSearch().getValue();
        return order -> String.valueOf(order.getId()).contains(value) 
        		|| String.valueOf(order.getProduct().getName()).contains(value); 
    }
	
	private Comparator<SupplierStockProduct> sortItems(PagingRequest pagingRequest) {
        if (pagingRequest.getData() == null) {
            return EMPTY_COMPARATOR;
        }

        try {
        	SortingOrder order = pagingRequest.getData()
                                       .get(0);

            int columnIndex = order.getColumn();
            Column column = pagingRequest.getColumns()
                                         .get(columnIndex);

            Comparator<SupplierStockProduct> comparator = SupplierStockProductComparators.getComparator(column.getData(), order.getDir());
            if (comparator == null) {
                return EMPTY_COMPARATOR;
            }

            return comparator;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return EMPTY_COMPARATOR;
    }	

}
