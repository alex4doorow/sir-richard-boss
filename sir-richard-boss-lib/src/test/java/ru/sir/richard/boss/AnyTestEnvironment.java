package ru.sir.richard.boss;

import java.util.Map;

import org.springframework.core.env.PropertyResolver;

public abstract class AnyTestEnvironment implements PropertyResolver {
	
	/**
	 * testing application.properties
	 * @return
	 */
	protected abstract Map<String, String> getEnvironmentMap();

	@Override
	public String getProperty(String key) {			
		return getEnvironmentMap().get(key);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProperty(String key, Class<T> targetType) {			
		if (targetType instanceof Class) {
			  Class<?> clazz = (Class<?>) targetType;
			  if (clazz == Integer.class) {
				  return (T) Integer.valueOf(getEnvironmentMap().get(key));
			  } else if (clazz == Long.class) {
				  return (T) Long.valueOf(getEnvironmentMap().get(key));					  
			  } else {
				  return (T) getEnvironmentMap().get(key);
			  }			  
		}
		return null;
	}
	
	@Override
	public boolean containsProperty(String key) {
		return false;
	}

	@Override
	public String getProperty(String key, String defaultValue) {			
		return null;
	}		

	@Override
	public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
		return null;
	}

	@Override
	public String getRequiredProperty(String key) throws IllegalStateException {			
		return null;
	}

	@Override
	public <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {			
		return null;
	}

	@Override
	public String resolvePlaceholders(String text) {			
		return null;
	}

	@Override
	public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {			
		return null;
	}
	
}	

