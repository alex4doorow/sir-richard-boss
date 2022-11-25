package ru.sir.richard.boss.dao;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

public abstract class AnyDaoImpl {

	private final Logger logger = LoggerFactory.getLogger(AnyDaoImpl.class);

	@Autowired
	protected DataSource dataSource;

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@SuppressWarnings("unused")
	private int getLastInsert(String tableName) {
		int result = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID() AS LAST_ID from " + tableName + " LIMIT 1", Integer.class);
		logger.debug("getLastInsert(): {}", result);
		return result;
	}

	protected int getLastInsertByGeneratedKeyHolder(GeneratedKeyHolder generatedKeyHolder, int rowsAffected) {
		final int result = generatedKeyHolder.getKey().intValue();
		logger.info("rowsAffected={}, id={}", rowsAffected, result);
		return result;
	}

}
