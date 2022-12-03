package ru.sir.richard.boss.model.data;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductImage extends AnyId {
	
	private String image;
	private int SortOrder;

}
