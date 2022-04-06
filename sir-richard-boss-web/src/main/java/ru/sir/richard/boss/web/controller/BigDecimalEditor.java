package ru.sir.richard.boss.web.controller;

import java.text.NumberFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.lang.Nullable;

public class BigDecimalEditor extends CustomNumberEditor {
	
	private final Logger logger = LoggerFactory.getLogger(BigDecimalEditor.class);
	
	public BigDecimalEditor(Class<? extends Number> numberClass,
			@Nullable NumberFormat numberFormat, boolean allowEmpty) throws IllegalArgumentException {
		super(numberClass, numberFormat, allowEmpty);
	}
	
	@Override
	public void setAsText(String inputText) {		
		try {			
			if (inputText != null) {
				String text = inputText;
				if (text.indexOf(".") >= 0) {
					text = text.replace(".", ",");
				}
				super.setAsText(text);
			} else {
				super.setAsText(inputText);
			}
        } catch (Exception e) {
            logger.error("parse error BigDecimalEditor.setAsText:{}", inputText);
        }
    }

}
