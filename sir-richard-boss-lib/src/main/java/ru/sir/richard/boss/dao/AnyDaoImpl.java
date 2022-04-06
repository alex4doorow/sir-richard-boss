package ru.sir.richard.boss.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public abstract class AnyDaoImpl {
	
	private final Logger logger = LoggerFactory.getLogger(AnyDaoImpl.class);
			
	@Autowired  
	protected JdbcTemplate jdbcTemplate;
	
	@Autowired  
	protected JdbcTemplate jdbcTemplateSlave;
	
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.jdbcTemplateSlave = new JdbcTemplate(dataSource);
	}
	
	protected int getLastInsert() {	
		final String sqlSelectLastInsert = "SELECT LAST_INSERT_ID() AS LAST_ID";
		Integer result = this.jdbcTemplate.queryForObject(sqlSelectLastInsert,
		        new Object[]{},
		        new RowMapper<Integer>() {
		            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	Integer result = rs.getInt("LAST_ID");
		            	logger.debug("getLastInsert():{}", result.intValue());
		                return result.intValue();
		            }
		        });
		return result.intValue();
	}
}
