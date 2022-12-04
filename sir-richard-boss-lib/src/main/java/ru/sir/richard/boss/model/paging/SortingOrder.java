package ru.sir.richard.boss.model.paging;

import lombok.Data;

@Data
public class SortingOrder {
    private Integer column;
    private Direction dir;
}
