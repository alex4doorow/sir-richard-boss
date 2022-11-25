package ru.sir.richard.boss.model.data.comparators;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import ru.sir.richard.boss.model.data.SupplierStockProduct;
import ru.sir.richard.boss.model.paging.Direction;

public class SupplierStockProductComparators {
	
	static class Key {
		
        String name;
        Direction dir;
        
		
		public Key(String name, Direction dir) {
			super();
			this.name = name;
			this.dir = dir;
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Direction getDir() {
			return dir;
		}
		public void setDir(Direction dir) {
			this.dir = dir;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((dir == null) ? 0 : dir.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Key other = (Key) obj;
			if (dir != other.dir)
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return "Key [name=" + name + ", dir=" + dir + "]";
		}
    }

    static Map<Key, Comparator<SupplierStockProduct>> map = new HashMap<>();

    static {

        map.put(new Key("id", Direction.asc), Comparator.comparing(SupplierStockProduct::getId));
        map.put(new Key("id", Direction.desc), Comparator.comparing(SupplierStockProduct::getId).reversed());
    	
    }

    public static Comparator<SupplierStockProduct> getComparator(String name, Direction dir) {
        return map.get(new Key(name, dir));
    }

    private SupplierStockProductComparators() {
    	
    }
	

}
