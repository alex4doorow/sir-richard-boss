package ru.sir.richard.boss.model.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ru.sir.richard.boss.model.types.AddressTypes;
import ru.sir.richard.boss.model.types.Countries;
import ru.sir.richard.boss.model.types.CustomerStatuses;
import ru.sir.richard.boss.model.types.CustomerTypes;

public abstract class AnyCustomer extends AnyId {
	
	private List<Address> addresses;
	private Countries country;
	private CustomerStatuses status;

	public AnyCustomer() {
		super();
		addresses = new ArrayList<Address>();
	}
	
	public boolean isPerson() {
		return false;
	}
	
	public boolean isCompany() {
		return !isPerson();
	}
	
	public abstract CustomerTypes getType();
	public abstract String getViewLongName();
	public abstract String getViewLongNameWithContactInfo();
	public abstract String getViewShortName();
	public abstract String getViewPhoneNumber();
	
	public abstract String getEmail();
	
	public abstract int getPersonId();
	public abstract void setPersonId(int value); 
	
	public Countries getCountry() {
		return country;
	}

	public void setCountry(Countries country) {
		this.country = country;
	}

	public CustomerStatuses getStatus() {
		return status;
	}

	public void setStatus(CustomerStatuses status) {
		this.status = status;
	}

	public List<Address> getAddresses() {
		return addresses;
	}	
	
	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;		
	}
		
	public Address getMainAddress() {
		if (addresses == null) {
			addresses = new ArrayList<Address>();
		}
		if (addresses.size() == 0) {
			Address mainAddress = new Address(Countries.UNKNOWN, AddressTypes.MAIN);
			addresses.add(mainAddress);
			return mainAddress;
		}
		for (Address address : addresses) {
			if (address.getAddressType() == AddressTypes.MAIN) {
				return address;
			}
		}
		return null;		
	}
	
	public void setMainAddress(Address mainAddress) {
		if (mainAddress == null) {
			return;
		}
		mainAddress.setAddressType(AddressTypes.MAIN);		
		if (addresses.size() == 0) {			
			addresses.add(mainAddress);
		} 	
		for (Address address : addresses) {
			if (address.getAddressType() == AddressTypes.MAIN) {
				address.setId(mainAddress.getId());
				address.setAddress(mainAddress.getAddress());
				address.setAnnotation(mainAddress.getAnnotation());
				address.setCountry(mainAddress.getCountry());
				return;
			}
		}
		addresses.add(mainAddress);
	}
	
	@Override
	public AnyCustomer clone() throws CloneNotSupportedException  {
		AnyCustomer clone = (AnyCustomer) super.clone();
		clone.addresses = this.addresses == null ? null : new ArrayList<>(this.addresses);		
		clone.country = this.country;
		clone.status = this.status;
		return clone;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((addresses == null) ? 0 : addresses.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnyCustomer other = (AnyCustomer) obj;
		if (addresses == null) {
			if (other.addresses != null)
				return false;
		} else if (!addresses.equals(other.addresses))
			return false;
		if (country != other.country)
			return false;		
		if (status != other.status)
			return false;
		return true;
	}
	
	protected AnyCustomer deepCopy(AnyCustomer inCustomer) {
		inCustomer.setId(this.getId());		
		if (this.getAddresses().size() > 0) {			
			Iterator<Address> addresesIterator = this.getAddresses().iterator();
			while (addresesIterator.hasNext()) {
				Address currentAddressData = addresesIterator.next();
				Address cloneAddressData;
				try {
					cloneAddressData = currentAddressData.clone();
					inCustomer.getAddresses().add(cloneAddressData);
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}				
			}			
		}
		return inCustomer;
	}
	
	@Override
	public String toString() {
		return "AnyCustomer [addresses=" + addresses + ", country=" + country + "]";
	}

	
}
