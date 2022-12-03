package ru.sir.richard.boss.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;

@Slf4j
public abstract class AnyDaoImpl {

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
		log.debug("getLastInsert(): {}", result);
		return result;
	}

	protected int getLastInsertByGeneratedKeyHolder(GeneratedKeyHolder generatedKeyHolder, int rowsAffected) {
		final int result = generatedKeyHolder.getKey().intValue();
		log.info("rowsAffected={}, id={}", rowsAffected, result);
		return result;
	}

}
