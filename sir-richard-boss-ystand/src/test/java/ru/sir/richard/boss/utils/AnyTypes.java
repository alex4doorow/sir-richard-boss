package ru.sir.richard.boss.utils;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public abstract class AnyTypes<T extends AnyTypes<?>> {
	
	public abstract int getId();
	public abstract String getAnnotation();
	
	public Set<T> getStatusesByArray(String strSuppliers) {
		Set<T> suppliers = new HashSet<T>();
		if (StringUtils.isEmpty(strSuppliers)) {
			return suppliers;
		}
		final String spliter = ",";
		String[] arrSuppliers = strSuppliers.split(spliter);		
		for (String arrSupplier : arrSuppliers) {
			T supplier = this.getValueById(Integer.valueOf(arrSupplier));
			suppliers.add(supplier);
		}
		return suppliers;
	}
	
	public abstract T getValueById(int value);

}
