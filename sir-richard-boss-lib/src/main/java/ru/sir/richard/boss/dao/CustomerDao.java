package ru.sir.richard.boss.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ru.sir.richard.boss.model.data.Address;
import ru.sir.richard.boss.model.data.AnyCustomer;
import ru.sir.richard.boss.model.data.Contact;
import ru.sir.richard.boss.model.data.ForeignerCompanyCustomer;
import ru.sir.richard.boss.model.data.ForeignerCustomer;
import ru.sir.richard.boss.model.data.Person;
import ru.sir.richard.boss.model.data.conditions.CustomerConditions;
import ru.sir.richard.boss.model.factories.CustomersFactory;
import ru.sir.richard.boss.model.types.AddressTypes;
import ru.sir.richard.boss.model.types.Countries;
import ru.sir.richard.boss.model.types.CustomerStatuses;
import ru.sir.richard.boss.model.types.CustomerTypes;
import ru.sir.richard.boss.model.utils.TextUtils;

@Repository
public class CustomerDao extends AnyDaoImpl {

	private final Logger logger = LoggerFactory.getLogger(CustomerDao.class);
	
	public AnyCustomer findById(int id) {
		
		final String sqlSelectCustomer = "SELECT * FROM sr_v_customer WHERE id = ?";
		List<AnyCustomer> customers = jdbcTemplate.query(sqlSelectCustomer,
		        new Object[] { id },
		        new int[] { Types.INTEGER },
		        new RowMapper<AnyCustomer>() {
					@Override
					public AnyCustomer mapRow(ResultSet rs, int rowNum) throws SQLException {
						Countries country = Countries.getValueByCode(rs.getString("COUNTRY_ISO_CODE_2"));
						CustomerTypes customerType = CustomerTypes.getValueById(rs.getInt("TYPE"));

						AnyCustomer customer = CustomersFactory.createCustomer(customerType);
						customer.setId(rs.getInt("ID"));
						customer.setCountry(country);
						if (customer.isPerson()) {
							ForeignerCustomer personCustomer = (ForeignerCustomer) customer;
							personCustomer.setPersonId(rs.getInt("PERSON_ID"));
							personCustomer.setFirstName(rs.getString("FIRST_NAME"));
							personCustomer.setLastName(rs.getString("LAST_NAME"));
							personCustomer.setMiddleName(rs.getString("MIDDLE_NAME"));
							personCustomer.setPhoneNumber(rs.getString("PHONE_NUMBER"));
							personCustomer.setEmail(rs.getString("EMAIL"));
						} else {
							ForeignerCompanyCustomer companyCustomer = (ForeignerCompanyCustomer) customer;
							companyCustomer.setShortName(rs.getString("SHORT_NAME"));
							companyCustomer.setLongName(rs.getString("LONG_NAME"));
							companyCustomer.setInn(rs.getString("INN"));
							companyCustomer.getMainContact().setId(rs.getInt("CONTACT_ID"));
							companyCustomer.getMainContact().setPersonId(rs.getInt("PERSON_ID"));
							companyCustomer.getMainContact().setFirstName(rs.getString("FIRST_NAME"));
							companyCustomer.getMainContact().setLastName(rs.getString("LAST_NAME"));
							companyCustomer.getMainContact().setMiddleName(rs.getString("MIDDLE_NAME"));
							companyCustomer.getMainContact().setPhoneNumber(rs.getString("PHONE_NUMBER"));
							companyCustomer.getMainContact().setEmail(rs.getString("EMAIL"));
						}
						customer.setStatus(CustomerStatuses.getValueById(rs.getInt("STATUS")));
						customer.setAddresses(getCustomerAddresses(customer.getId()));
						return customer;
					}
				});
		
		if (customers == null || customers.size() == 0) {
			return null;
		} else {
			return customers.get(0);
		}
	}
	
	public String nextEmptyPhoneNumber() {
		
		final String sqlSelectMaxPhoneNumber = "select MAX(phone_number) max_phone_number from sr_person where phone_number like '(000)%'";
		String stringMaxPhoneNumber = this.jdbcTemplate.queryForObject(sqlSelectMaxPhoneNumber,
		        new Object[] {},
		        new int[] {},
		        new RowMapper<String>() {
					@Override
		            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	return rs.getString("max_phone_number");	
		            }
		        });
		int maxPhoneNumber = Integer.valueOf(TextUtils.phoneNumberDigit(stringMaxPhoneNumber));
		int nextPhoneNumber = maxPhoneNumber + 1;
		String stringNextPhoneNumber = StringUtils.leftPad(Integer.valueOf(nextPhoneNumber).toString(), 10, '0');
		return TextUtils.formatPhoneNumber(stringNextPhoneNumber);
	}	

	private AnyCustomer findCustomerByPhoneNumber(String inputPhoneNumber) {
		logger.debug("findByPhoneNumber():{}", inputPhoneNumber);

		if (StringUtils.isEmpty(inputPhoneNumber)) {
			return null;
		}
		String phoneNumber = inputPhoneNumber.trim();

		final String sqlSelectCustomer = "SELECT MAX(id) max_id FROM sr_v_customer WHERE phone_number = ?";
		Integer customerId = this.jdbcTemplate.queryForObject(sqlSelectCustomer, 
				new Object[] { phoneNumber },
				new int[] { Types.VARCHAR },
				new RowMapper<Integer>() {
					@Override
					public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
						return Integer.valueOf(rs.getInt("max_id"));
					}
				});

		if (customerId == 0L) {
			return null;
		} else {
			return findById(customerId);
		}
	}
	
	private Contact findContactByConditions(String inputPhoneNumber, String inputEmail) {
		logger.debug("findContactByConditions():{}, {}", inputPhoneNumber, inputEmail);

		if (StringUtils.isEmpty(inputPhoneNumber) && StringUtils.isEmpty(inputEmail)) {
			return null;
		}
		String phoneNumber = inputPhoneNumber.trim();
		String email = inputEmail.trim();
		
		Contact contact = null;
		if (StringUtils.isNotEmpty(phoneNumber) && StringUtils.isNotEmpty(inputEmail)) {
			final String sqlSelectCustomer1 = "SELECT MAX(id) id, MAX(country_iso_code_2) country_iso_code_2,"
					+ "  MAX(first_name) first_name, MAX(last_name) last_name, MAX(middle_name) middle_name,"
					+ "  MAX(email) email, MAX(phone_number) phone_number"
					+ "  FROM sr_person"
					+ "  WHERE phone_number = ? AND email = ?";			
			contact = this.jdbcTemplate.queryForObject(sqlSelectCustomer1, 
					new Object[] {phoneNumber, email},
					new int[] { Types.VARCHAR, Types.VARCHAR },
					new RowMapper<Contact>() {
						@Override
						public Contact mapRow(ResultSet rs, int rowNum) throws SQLException {
							Contact contact = new Contact(Countries.getValueByCode(rs.getString("country_iso_code_2")));
							contact.setId(rs.getInt("id"));
							contact.setFirstName(rs.getString("first_name"));
							contact.setLastName(rs.getString("last_name"));
							contact.setMiddleName(rs.getString("middle_name"));						
							contact.setEmail(rs.getString("email"));
							contact.setPhoneNumber(rs.getString("phone_number"));
							return contact;
						}
					});			
		} else if (StringUtils.isEmpty(phoneNumber) && StringUtils.isNotEmpty(inputEmail)) {
			final String sqlSelectCustomer2 = "SELECT MAX(id) id, MAX(country_iso_code_2) country_iso_code_2,"
					+ "  MAX(first_name) first_name, MAX(last_name) last_name, MAX(middle_name) middle_name,"
					+ "  MAX(email) email, MAX(phone_number) phone_number"
					+ "  FROM sr_person"
					+ "  WHERE email = ?";		
			contact = this.jdbcTemplate.queryForObject(sqlSelectCustomer2, 
					new Object[] {email},
					new int[] { Types.VARCHAR },
					new RowMapper<Contact>() {
						@Override
						public Contact mapRow(ResultSet rs, int rowNum) throws SQLException {
							Contact contact = new Contact(Countries.getValueByCode(rs.getString("country_iso_code_2")));
							contact.setId(rs.getInt("id"));
							contact.setFirstName(rs.getString("first_name"));
							contact.setLastName(rs.getString("last_name"));
							contact.setMiddleName(rs.getString("middle_name"));						
							contact.setEmail(rs.getString("email"));
							contact.setPhoneNumber(rs.getString("phone_number"));
							return contact;
						}
					});			
		} else if (StringUtils.isNotEmpty(phoneNumber) && StringUtils.isEmpty(inputEmail)) {
			final String sqlSelectCustomer3 = "SELECT MAX(id) id, MAX(country_iso_code_2) country_iso_code_2,"
					+ "  MAX(first_name) first_name, MAX(last_name) last_name, MAX(middle_name) middle_name,"
					+ "  MAX(email) email, MAX(phone_number) phone_number"
					+ "  FROM sr_person"
					+ "  WHERE phone_number = ?";	
			contact = this.jdbcTemplate.queryForObject(sqlSelectCustomer3, 
					new Object[] {phoneNumber},
					new int[] { Types.VARCHAR },
					new RowMapper<Contact>() {
						@Override
						public Contact mapRow(ResultSet rs, int rowNum) throws SQLException {
							Contact contact = new Contact(Countries.getValueByCode(rs.getString("country_iso_code_2")));
							contact.setId(rs.getInt("id"));
							contact.setFirstName(rs.getString("first_name"));
							contact.setLastName(rs.getString("last_name"));
							contact.setMiddleName(rs.getString("middle_name"));						
							contact.setEmail(rs.getString("email"));
							contact.setPhoneNumber(rs.getString("phone_number"));
							return contact;
						}
					});			
		}
		return contact;		
	}

	public AnyCustomer findByConditions(CustomerConditions customerConditions) {
		logger.debug("findByConditions():{}", customerConditions);		
		Integer customerId = 0;
		if (customerConditions.getCustomerType() == CustomerTypes.CUSTOMER) {
			return findCustomerByPhoneNumber(customerConditions.getPersonPhoneNumber());
		} else if (customerConditions.getCustomerType() == CustomerTypes.COMPANY || customerConditions.getCustomerType() == CustomerTypes.BUSINESSMAN) {
			customerId = 0;
			if (StringUtils.isNotEmpty(customerConditions.getCompanyInn())) {
				String inn = StringUtils.defaultIfEmpty(customerConditions.getCompanyInn(), "");
				final String sqlSelectCompanyCustomer = "SELECT MAX(customer_id) max_id"
						+ " FROM sr_customer_company cc" 
						+ " WHERE cc.inn = ?";
				customerId = this.jdbcTemplate.queryForObject(sqlSelectCompanyCustomer, 
						new Object[] {inn},
						new int[] { Types.VARCHAR },
						new RowMapper<Integer>() {
							@Override
							public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
								return Integer.valueOf(rs.getInt("max_id"));
							}
						});				
			}			
			if (customerId == 0L && (StringUtils.isNotEmpty(customerConditions.getCompanyMainContactPhoneNumber()))) {			

				String contactPhoneNumber = customerConditions.getCompanyMainContactPhoneNumber().trim();					
				final String sqlSelectCompanyCustomerContact = "SELECT MAX(cc.customer_id) max_id"
							+ " FROM sr_customer_contact cc, sr_person p" 
							+ " WHERE cc.person_id = p.id"
							+ " AND (p.phone_number = ?)";
				customerId = this.jdbcTemplate.queryForObject(sqlSelectCompanyCustomerContact,
							new Object[] {contactPhoneNumber},
							new int[] { Types.VARCHAR },
							new RowMapper<Integer>() {
								@Override
								public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
									return Integer.valueOf(rs.getInt("max_id"));
								}
							});
					
			}
			if (customerId == 0L && (StringUtils.isNotEmpty(customerConditions.getCompanyMainContactEmail()))) {
				String contactEmail = customerConditions.getCompanyMainContactEmail().trim();					
				final String sqlSelectCompanyCustomerContact = "SELECT MAX(cc.customer_id) max_id"
							+ " FROM sr_customer_contact cc, sr_person p" 
							+ " WHERE cc.person_id = p.id"
							+ " AND (p.email = ?)";
				customerId = this.jdbcTemplate.queryForObject(sqlSelectCompanyCustomerContact,
							new Object[] {contactEmail},
							new int[] { Types.VARCHAR },
							new RowMapper<Integer>() {
								@Override
								public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
									return Integer.valueOf(rs.getInt("max_id"));
								}
							});
					
			}			
			if (customerId == 0L) {
				return null;
			} else {
				return findById(customerId);
			}
		}
		return null;
	}

	public Address getAddress(int addressId) {
		final String sqlSelectAddress = "SELECT * FROM sr_address a" 
				+ " WHERE id = ?" 
				+ " ORDER BY id";
		Address address = this.jdbcTemplate.queryForObject(sqlSelectAddress, 
				new Object[] { addressId },
				new int[] { Types.INTEGER },
				new RowMapper<Address>() {
					public Address mapRow(ResultSet rs, int rowNum) throws SQLException {
						Countries country = Countries.getValueByCode(rs.getString("COUNTRY_ISO_CODE_2"));
						Address address = new Address(country, AddressTypes.getValueById(rs.getInt("TYPE")));
						address.setId(rs.getInt("ID"));
						address.getCarrierInfo().setCityId(rs.getInt("CITY_ID"));
						address.getCarrierInfo().setCityContext(rs.getString("CITY"));
						address.getCarrierInfo().setPvz(rs.getString("PVZ"));						
						address.getCarrierInfo().setStreet(rs.getString("STREET"));
						address.getCarrierInfo().setHouse(rs.getString("HOUSE"));
						address.getCarrierInfo().setFlat(rs.getString("FLAT"));
						if (StringUtils.isEmpty(rs.getString("PVZ_ID")) || "null".equalsIgnoreCase(rs.getString("PVZ_ID"))) {
							address.getCarrierInfo().setDeliveryVariantId(0L);							
						} else {
							address.getCarrierInfo().setDeliveryVariantId(Long.valueOf(rs.getString("PVZ_ID")));							
						}
						address.setAddress(rs.getString("ADDRESS"));
						address.getCarrierInfo().setMetroStation(rs.getString("SUBWAY_STATION"));
						address.setAnnotation(rs.getString("ANNOTATION"));
						return address;
					}
				});
		return address;
	}
	
	public Person getPerson(int personId) {
		if (personId <= 0) {
			return null;
		}				
		final String sqlSelectPerson = "SELECT * FROM sr_person a WHERE id = ?" 
				+ " ORDER BY id";
		Person person = this.jdbcTemplate.queryForObject(sqlSelectPerson, 
				new Object[] { personId },
				new int[] { Types.INTEGER },
				new RowMapper<Person>() {
					public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
						
						Contact contact = new Contact(Countries.getValueByCode(rs.getString("country_iso_code_2")));
						contact.setId(rs.getInt("id"));
						contact.setPersonId(rs.getInt("id"));						
						contact.setFirstName(rs.getString("first_name"));
						contact.setLastName(rs.getString("last_name"));
						contact.setMiddleName(rs.getString("middle_name"));						
						contact.setEmail(rs.getString("email"));
						contact.setPhoneNumber(rs.getString("phone_number"));
						return contact;
					}
				});
		return person;
	}
	
	public Person findPerson(Person person) {
		
		if (person == null || StringUtils.isEmpty(person.getPhoneNumber())) {
			return new Contact();
		}			
		
		final String sqlSelectCountPerson = "SELECT COUNT(p.id) COUNT_PERSON FROM sr_person p"
				+ " WHERE phone_number = ?" 
				+ " ORDER BY id";
		Integer count = this.jdbcTemplate.queryForObject(sqlSelectCountPerson,
		        new Object[]{ person.getPhoneNumber().trim() },
		        new int[] { Types.VARCHAR },
		        new RowMapper<Integer>() {
					@Override
		            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	return Integer.valueOf(rs.getInt("COUNT_PERSON"));	
		            }
		        });		
		if (count == 0) {
			return new Contact();
		}
		final String sqlSelectPerson = "SELECT * FROM sr_person" + 
				" WHERE id IN (SELECT MAX(id) from sr_person WHERE phone_number = ?)";
		Person findingPerson = this.jdbcTemplate.queryForObject(sqlSelectPerson, 
				new Object[] { person.getPhoneNumber().trim() },
				new int[] { Types.VARCHAR },
				new RowMapper<Person>() {
					@Override
					public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
						
						Contact contact = new Contact(Countries.getValueByCode(rs.getString("country_iso_code_2")));
						contact.setId(rs.getInt("id"));
						contact.setPersonId(rs.getInt("id"));						
						contact.setFirstName(rs.getString("first_name"));
						contact.setLastName(rs.getString("last_name"));
						contact.setMiddleName(rs.getString("middle_name"));						
						contact.setEmail(rs.getString("email"));
						contact.setPhoneNumber(rs.getString("phone_number"));
						return contact;
					}
				});
		return findingPerson;
	}

	private List<Address> getCustomerAddresses(int customerId) {
		final String sqlSelectCustomerAddress = "SELECT * FROM sr_v_customer_address" 
				+ " WHERE customer_id = ?"
				+ " ORDER BY id";
		
		List<Address> addresses = this.jdbcTemplate.query(sqlSelectCustomerAddress,
		        new Object[] { customerId },
		        new int[] { Types.VARCHAR },
		        new RowMapper<Address>() {
					public Address mapRow(ResultSet rs, int rowNum) throws SQLException {
						Address address = new Address(Countries.getValueByCode(rs.getString("COUNTRY_ISO_CODE_2")),
								AddressTypes.getValueById(rs.getInt("TYPE")));
						address.setId(rs.getInt("ID"));
						//address.setCity(rs.getString("CITY"));
						//address.setPvz(rs.getString("PVZ"));
						//address.setPostCode(rs.getString("POST_CODE"));
						address.setAddress(rs.getString("ADDRESS"));
						address.getCarrierInfo().setMetroStation(rs.getString("SUBWAY_STATION"));
						address.setAnnotation(rs.getString("ANNOTATION"));
						return address;
					}
				});
		
		return addresses;
	}

	public int addAddress(Address address) {
		if (StringUtils.isEmpty(address.getAddress())) {
			return 0;
		}		
		String textAddress = StringUtils.truncate(address.getAddress().trim(), 255);
		logger.debug("addAddress(): {}", address);
		GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();	
		final String sqlInsertAddress = "INSERT INTO sr_address"
				+ " (type, country_iso_code_2,"
				+ "  city_id, city, pvz, pvz_id, "
				+ "  street, house, flat, "
				+ "  post_code, address, subway_station, annotation)" 
				+ " VALUES"
				+ " (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		int rowsAffected = jdbcTemplate.update(connection -> {            
            // Pre-compiling SQL
            PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertAddress, Statement.RETURN_GENERATED_KEYS);
            // Set parameters
            int index = 1;
            preparedStatement.setInt(index++, address.getAddressType().getId());            
            preparedStatement.setString(index++, address.getCountry().getCode());
            preparedStatement.setInt(index++, address.getCarrierInfo().getCityId());
            preparedStatement.setString(index++, address.getCarrierInfo().getCityContext());
            preparedStatement.setString(index++, address.getCarrierInfo().getPvz());
            preparedStatement.setString(index++, String.valueOf(address.getCarrierInfo().getDeliveryVariantId()));
            preparedStatement.setString(index++, address.getCarrierInfo().getStreet());
            preparedStatement.setString(index++, address.getCarrierInfo().getHouse());
            preparedStatement.setString(index++, address.getCarrierInfo().getFlat());
            preparedStatement.setString(index++, address.getPostCode()); 
            preparedStatement.setString(index++, textAddress);
            preparedStatement.setString(index++, address.getCarrierInfo().getMetroStation());
            preparedStatement.setString(index++, address.getAnnotation());            
            return preparedStatement;
            
        }, generatedKeyHolder);
		return getLastInsertByGeneratedKeyHolder(generatedKeyHolder, rowsAffected);					
                		
		/*
		this.jdbcTemplate.update(sqlInsertAddress,
				new Object[] {address.getAddressType().getId(), address.getCountry().getCode(),
						address.getCarrierInfo().getCityId(), address.getCarrierInfo().getCityContext(), address.getCarrierInfo().getPvz(), String.valueOf(address.getCarrierInfo().getDeliveryVariantId()),
						address.getCarrierInfo().getStreet(), address.getCarrierInfo().getHouse(), address.getCarrierInfo().getFlat(),
						address.getPostCode(), textAddress, address.getCarrierInfo().getMetroStation(),
						address.getAnnotation()});
		int addressId = getLastInsert("sr_address");		
		return addressId;
		*/
	}

	public void updateAddress(int addressId, Address address) {
		logger.debug("updateAddress():{}", address);
		if (addressId == 0) {
			return;
		}

		final String sqlUpdateAddress = "UPDATE sr_address a" 
				+ " SET a.address = ?, "
				+ "     a.city_id = ?, "
				+ "     a.city = ?, "
				+ "     a.pvz = ?, "
				+ "     a.pvz_id = ?, "
				+ "     a.street = ?, "
				+ "     a.house = ?, "
				+ "     a.flat = ?, "				
				+ "     a.country_iso_code_2 = ?"
				+ " WHERE id = ?";
		this.jdbcTemplate.update(sqlUpdateAddress,
				new Object[] {address.getAddress(),
						address.getCarrierInfo().getCityId(),
						address.getCarrierInfo().getCityContext(),
						address.getCarrierInfo().getPvz(),
						String.valueOf(address.getCarrierInfo().getDeliveryVariantId()),						
						address.getCarrierInfo().getStreet(),
						address.getCarrierInfo().getHouse(),
						address.getCarrierInfo().getFlat(),						
						address.getCountry().getCode(), 
						address.getId()});
	}
		
	public int addPerson(Person person) {
		final String sqlInsertPerson = "INSERT INTO sr_person"
				+ " (first_name, last_name, middle_name, country_iso_code_2, phone_number, email)" 
				+ " VALUES"
				+ " (?, ?, ?, ?, ?, ?)";
		GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
		int rowsAffected = jdbcTemplate.update(connection -> {            
            PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertPerson, Statement.RETURN_GENERATED_KEYS);
            int index = 1;
            preparedStatement.setString(index++, person.getFirstName()); 
            preparedStatement.setString(index++, person.getLastName());
            preparedStatement.setString(index++, person.getMiddleName()); 
            preparedStatement.setString(index++, person.getCountry().getCode());
            preparedStatement.setString(index++, person.getPhoneNumber());
            preparedStatement.setString(index++, person.getEmail());            
            return preparedStatement;	            
        }, generatedKeyHolder);
		return getLastInsertByGeneratedKeyHolder(generatedKeyHolder, rowsAffected);
		/*
		this.jdbcTemplate.update(sqlInsertPerson,
				new Object[] {person.getFirstName(), 
						person.getLastName(),
						person.getMiddleName(), 
						person.getCountry().getCode(),
						person.getPhoneNumber(), 
						person.getEmail()});
		return getLastInsert("sr_person");
		*/		
	}

	public void updatePerson(int personId, Person person) {

		final String sqlUpdatePerson = "UPDATE sr_person p" 
				+ " SET p.phone_number = ?, " 
				+ "     p.first_name = ?,"
				+ "     p.last_name = ?," 
				+ "     p.middle_name = ?," 
				+ "     p.country_iso_code_2 = ?,"
				+ "     p.email = ?" + " WHERE id = ?";

		this.jdbcTemplate.update(sqlUpdatePerson,
					new Object[] {person.getPhoneNumber(), 
							person.getFirstName(), 
							person.getLastName(),
							person.getMiddleName(), 
							person.getCountry().getCode(), 
							person.getEmail(),
							person.getPersonId()});
	}
	
	@Transactional
	public int addCustomer(AnyCustomer customer) {
		logger.debug("addCustomer():{}", customer);

		final String sqlInsertPerson = "INSERT INTO sr_person"
				+ " (first_name, last_name, middle_name, country_iso_code_2, phone_number, email)" 
				+ " VALUES"
				+ " (?, ?, ?, ?, ?, ?)";

		final String sqlInsertCompanyContact = "INSERT INTO sr_customer_contact" 
				+ " (customer_id, person_id)"
				+ " VALUES" 
				+ " (?, ?)";

		final int personId;
		int personContactId = 0;
		int customerId = 0;
		GeneratedKeyHolder generatedKeyHolder;
		if (customer.isPerson()) {
			ForeignerCustomer personCustomer = (ForeignerCustomer) customer;
			
			generatedKeyHolder = new GeneratedKeyHolder();
			int rowsAffected = jdbcTemplate.update(connection -> {            
	            PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertPerson, Statement.RETURN_GENERATED_KEYS);
	            int index = 1;
	            preparedStatement.setString(index++, personCustomer.getFirstName()); 
	            preparedStatement.setString(index++, personCustomer.getLastName());
	            preparedStatement.setString(index++, personCustomer.getMiddleName()); 
	            preparedStatement.setString(index++, personCustomer.getCountry().getCode());
	            preparedStatement.setString(index++, personCustomer.getPhoneNumber()); 
	            preparedStatement.setString(index++, personCustomer.getEmail());
	            return preparedStatement;	            
	        }, generatedKeyHolder);
			personId = getLastInsertByGeneratedKeyHolder(generatedKeyHolder, rowsAffected);
					
			/*
			this.jdbcTemplate.update(sqlInsertPerson,
					new Object[] {personCustomer.getFirstName(), 
							personCustomer.getLastName(),
							personCustomer.getMiddleName(), 
							personCustomer.getCountry().getCode(),
							personCustomer.getPhoneNumber(), 
							personCustomer.getEmail()});
			personId = getLastInsert("sr_person");
			*/

			final String sqlInsertCustomer = "INSERT INTO sr_customer" 
					+ " (type, person_id, status)" 
					+ " VALUES"
					+ " (?, ?, ?)";
			generatedKeyHolder = new GeneratedKeyHolder();
			rowsAffected = jdbcTemplate.update(connection -> {            
	            PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertCustomer, Statement.RETURN_GENERATED_KEYS);
	            int index = 1;
	            preparedStatement.setInt(index++, customer.getType().getId()); 
	            preparedStatement.setInt(index++, personId); 
	            preparedStatement.setInt(index++, CustomerStatuses.PROCEED.getId());
	            return preparedStatement;	            
	        }, generatedKeyHolder);
			customerId = getLastInsertByGeneratedKeyHolder(generatedKeyHolder, rowsAffected);
			
			/*
			this.jdbcTemplate.update(sqlInsertCustomer,
					new Object[] {customer.getType().getId(), 
							personId, 
							CustomerStatuses.PROCEED.getId() });
			customerId = getLastInsert("sr_customer");
			*/

		} else {
			final String sqlInsertCompany = "INSERT INTO sr_customer" 
					+ " (type, person_id, status)" 
					+ " VALUES"
					+ " (?, NULL, ?)";
			
			generatedKeyHolder = new GeneratedKeyHolder();
			int rowsAffected = jdbcTemplate.update(connection -> {            
	            PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertCompany, Statement.RETURN_GENERATED_KEYS);
	            int index = 1;
	            preparedStatement.setInt(index++, customer.getType().getId()); 
	            preparedStatement.setInt(index++, CustomerStatuses.PROCEED.getId());	            
	            return preparedStatement;	            
	        }, generatedKeyHolder);
			customerId = getLastInsertByGeneratedKeyHolder(generatedKeyHolder, rowsAffected);
			/*
			this.jdbcTemplate.update(sqlInsertCompany,
					new Object[] {customer.getType().getId(), CustomerStatuses.PROCEED.getId()});
			customerId = getLastInsert("sr_customer");					
			*/
			final String sqlInsertCompanyCustomer = "INSERT INTO sr_customer_company"
					+ " (customer_id, country_iso_code_2, inn, short_name, long_name)" 
					+ " VALUES"
					+ " (?, ?, ?, ?, ?)";
			ForeignerCompanyCustomer companyCustomer = (ForeignerCompanyCustomer) customer;
			this.jdbcTemplate.update(sqlInsertCompanyCustomer,
					new Object[] {customerId, 
							companyCustomer.getCountry().getCode(), 
							companyCustomer.getInn(),
							companyCustomer.getShortName().toUpperCase(), 
							companyCustomer.getLongName().toUpperCase()});
			
			Contact contact = companyCustomer.getMainContact();
			Contact beforeContact = findContactByConditions(contact.getPhoneNumber(), contact.getEmail());
			if (beforeContact != null && beforeContact.getId() > 0) {
				personContactId = beforeContact.getId();
			} else {
				generatedKeyHolder = new GeneratedKeyHolder();
				rowsAffected = jdbcTemplate.update(connection -> {            
		            PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertPerson, Statement.RETURN_GENERATED_KEYS);
		            int index = 1;
		            preparedStatement.setString(index++, contact.getFirstName());
		            preparedStatement.setString(index++, contact.getLastName());
		            preparedStatement.setString(index++, contact.getMiddleName());
		            preparedStatement.setString(index++, contact.getCountry().getCode()); 
		            preparedStatement.setString(index++, contact.getPhoneNumber());
		            preparedStatement.setString(index++, contact.getEmail());            
		            return preparedStatement;	            
		        }, generatedKeyHolder);
				personContactId = getLastInsertByGeneratedKeyHolder(generatedKeyHolder, rowsAffected);
				/*
				this.jdbcTemplate.update(sqlInsertPerson,
						new Object[] {contact.getFirstName(), 
								contact.getLastName(), 
								contact.getMiddleName(),
								contact.getCountry().getCode(), 
								contact.getPhoneNumber(), 
								contact.getEmail()});
				personContactId = getLastInsert("sr_person");
				*/
				this.jdbcTemplate.update(sqlInsertCompanyContact, 
						new Object[] {customerId, 
								personContactId});												
			}

		}
		for (Address address : customer.getAddresses()) {
			int addressId = addAddress(address);
			addCustomerAddress(customerId, addressId);
		}
		return customerId;
	}

	public void updateCustomer(AnyCustomer customer) {

		final String sqlUpdatePerson = "UPDATE sr_person p" 
				+ " SET p.phone_number = ?, " 
				+ "     p.first_name = ?,"
				+ "     p.last_name = ?," 
				+ "     p.middle_name = ?," 
				+ "     p.country_iso_code_2 = ?,"
				+ "     p.email = ?" 
				+ " WHERE id = ?";

		if (customer.isPerson()) {
			Person person = (Person) customer;
			this.jdbcTemplate.update(sqlUpdatePerson,
					new Object[] {person.getPhoneNumber(), 
							person.getFirstName(), 
							person.getLastName(),
							person.getMiddleName(), 
							person.getCountry().getCode(), 
							person.getEmail(),
							person.getPersonId()});
		} else {
			ForeignerCompanyCustomer companyCustomer = (ForeignerCompanyCustomer) customer;
			final String sqlUpdateCompanyCustomer = "UPDATE sr_customer_company cc" 
					+ " SET cc.long_name = ?, "
					+ "     cc.short_name = ?," 
					+ "     cc.inn = ?,"
					+ "     cc.country_iso_code_2 = ?"					
					+ " WHERE customer_id = ?";
			this.jdbcTemplate.update(sqlUpdateCompanyCustomer, new Object[] {companyCustomer.getLongName().toUpperCase().trim(),					
					companyCustomer.getShortName().toUpperCase().trim(), 
					companyCustomer.getInn().trim(),
					companyCustomer.getCountry().getCode(),					
					companyCustomer.getId()});

			// mainContact
			Contact contact = companyCustomer.getMainContact();
			this.jdbcTemplate.update(sqlUpdatePerson,
					new Object[] { contact.getPhoneNumber(), contact.getFirstName(), contact.getLastName(),
							contact.getMiddleName(), contact.getCountry().getCode(), contact.getEmail(),
							contact.getPersonId() });
		}
		
		for (Address address : customer.getAddresses()) {
			if (address.isNew()) {
				int addressId = addAddress(address);
				addCustomerAddress(customer.getId(), addressId);
			} else {
				updateAddress(address.getId(), address);
			}
		}		 

	}

	public void eraseCustomer(int customerId) {
		
		final String sqlDeletePerson = "DELETE FROM sr_person WHERE id = ?";		
		final String sqlDeleteAddress = "DELETE FROM sr_address WHERE id = ?";
		final String sqlDeleteCustomerAddress = "DELETE FROM sr_customer_address WHERE customer_id = ?";
		final String sqlDeleteCustomerCompany = "DELETE FROM sr_customer_company WHERE customer_id = ?";
		final String sqlDeleteCustomerContact = "DELETE FROM sr_customer_contact WHERE customer_id = ?";
		final String sqlDeleteCustomer = "DELETE FROM sr_customer WHERE ID = ?";
		
		AnyCustomer customer = findById(customerId);		
		int personId;
		if (customer.isPerson()) {
			ForeignerCustomer personCustomer = (ForeignerCustomer) customer;
			personId = personCustomer.getPersonId();
			
		} else {
			ForeignerCompanyCustomer companyCustomer = (ForeignerCompanyCustomer) customer;
			personId = companyCustomer.getMainContact().getPersonId();			
			this.jdbcTemplate.update(sqlDeleteCustomerCompany, new Object[] {customerId});
			this.jdbcTemplate.update(sqlDeleteCustomerContact, new Object[] {customerId});
		}		
		this.jdbcTemplate.update(sqlDeleteCustomerAddress, new Object[] {customerId});
		if (customer.getMainAddress().getId() > 0) {
			this.jdbcTemplate.update(sqlDeleteAddress, new Object[] {customer.getMainAddress().getId()});
		}		
		this.jdbcTemplate.update(sqlDeletePerson, new Object[] {personId});

		this.jdbcTemplate.update(sqlDeleteCustomer, new Object[] {customerId});
	}

	private void addCustomerAddress(int customerId, int addressId) {
		if (addressId == 0) {
			return;
		}

		final String sqlInsertCustomerAddress = "INSERT INTO sr_customer_address" 
				+ " (address_id, customer_id)"
				+ " VALUES" 
				+ " (?, ?)";

		this.jdbcTemplate.update(sqlInsertCustomerAddress, new Object[] {addressId, 
				customerId});
	}

}
