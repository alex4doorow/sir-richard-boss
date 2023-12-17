package ru.sir.richard.boss.utils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

public class ExcelUtils {
		
	/**
	 * Значение ячейки типа double
	 * 
	 * @param cell
	 * @param evaluator
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static double getDoubleCellValue(HSSFCell cell, FormulaEvaluator evaluator) {

		double result = 0;
		if (cell == null) {
			return result;
		}
		
		switch (evaluator.evaluateInCell(cell).getCellType()) {
		case Cell.CELL_TYPE_BOOLEAN:
			System.out.println("CELL_TYPE_BOOLEAN: " + cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_NUMERIC:
			// System.out.println("CELL_TYPE_NUMERIC: " + cell.getNumericCellValue());
			result = cell.getNumericCellValue();
			break;

		case Cell.CELL_TYPE_STRING:
			System.out.println("CELL_TYPE_STRING:" + cell.getStringCellValue());
			break;
		case Cell.CELL_TYPE_BLANK:
			break;
		case Cell.CELL_TYPE_ERROR:
			System.out.println("CELL_TYPE_ERROR: " + cell.getErrorCellValue());
			break;

		// CELL_TYPE_FORMULA will never occur
		case Cell.CELL_TYPE_FORMULA:
			System.out.println("CELL_TYPE_FORMULA");
			break;
		}
		return result;
	}

	/**
	 * значение трэккода
	 * 
	 * @param cell
	 * @param evaluator
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getTrackCodeCellValue(HSSFCell cell, FormulaEvaluator evaluator) {
		String result = "";

		switch (evaluator.evaluateInCell(cell).getCellType()) {
		case Cell.CELL_TYPE_BOOLEAN:
			System.out.println("CELL_TYPE_BOOLEAN: " + cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_NUMERIC:
			DataFormatter formatter = new DataFormatter();
			result = formatter.formatCellValue(cell);
			break;

		case Cell.CELL_TYPE_STRING:
			// System.out.println("CELL_TYPE_STRING:" + cell.getStringCellValue());
			result = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_BLANK:
			break;
		case Cell.CELL_TYPE_ERROR:
			System.out.println("CELL_TYPE_ERROR: " + cell.getErrorCellValue());
			break;

		// CELL_TYPE_FORMULA will never occur
		case Cell.CELL_TYPE_FORMULA:
			System.out.println("CELL_TYPE_FORMULA");
			break;
		}
		return result;
	}

}
