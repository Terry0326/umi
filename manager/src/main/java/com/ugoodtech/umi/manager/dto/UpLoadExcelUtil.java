package com.ugoodtech.umi.manager.dto;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Kotone
 * Date: 13-9-11
 * Time: 下午4:45
 * To change this template use File | Settings | File Templates.
 */
public class UpLoadExcelUtil {

    XSSFWorkbook workbook;

    public XSSFWorkbook getWorkbook() {
        return workbook;
    }

    public void setWorkbook(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    /**
     * 要读取的excel文件的数据 例如：
     * A   B   C   D   E   F
     * 11  12      14  15  16
     * 21      23      25  26
     * ...
     * ...
     * 有些行的部分列为空，但其后面的列又有值
     */


    public List<String[]> getExcelTest(InputStream is) {
//        声明集合 List<String[]> ，
//        List<String[]> 的元素 行数组String[]为excel中的每一行
        List<String[]> list = new ArrayList<String[]>();

        try {
//            将is流实例到 一个excel流里
//            HSSFWorkbook hwk = new HSSFWorkbook(is);

            try {
                Workbook hwk = null;
                XSSFWorkbook xwk = null;
                xwk = new XSSFWorkbook(is);
                hwk = xwk;
                setWorkbook(xwk);
                //            得到book第一个工作薄sheet
//            HSSFSheet sh = hwk.getSheetAt(0);
                Sheet sh = hwk.getSheetAt(0);
//            总行数
                int rows = sh.getLastRowNum() + 1 - sh.getFirstRowNum();
//            System.out.println(rows);
                for (int i = 0; i < rows; i++) {
//                HSSFRow row = sh.getRow(i);
                    Row row = sh.getRow(i);
//                该行的总列数
                    int cols = row.getLastCellNum() + 1 - row.getFirstCellNum();
//                System.out.println("第"+i+"行的总列数"+cols);
//                用来存放该行每一列的值
                    String[] str = new String[cols];
                    for (int j = 0; j < cols; j++) {
                        Cell col = row.getCell((short) j);
                        Cell colNext = row.getCell((short) (j + 1));

//                    该列不为空，直接读到 行数组里
                        if (col != null) {
                            System.out.println(str[j]+"   "+col.getCellType());
                            try {
                                str[j] = col.getStringCellValue();
                            } catch (Exception e) {
                            }

                            try {
                                double d = col.getNumericCellValue();
                                DecimalFormat df = new DecimalFormat("#");
                                str[j] = df.format(d);
                            } catch (Exception e) {
                            }

                        } else {                    // 该列为空
//                        该列的后面一列不为空，用空字符串占位
                            if (colNext != null) {
                                Object colValue = "";
                                str[j] = colValue.toString();
                            }
                        }

                    }
                    list.add(str);
                }
            } catch (Exception ex) {
                HSSFWorkbook hwk = new HSSFWorkbook(is);
//                hwk = new HSSFWorkbook(is);
                //            得到book第一个工作薄sheet
                HSSFSheet sh = hwk.getSheetAt(0);
//                Sheet sh = hwk.getSheetAt(0);
//            总行数
                int rows = sh.getLastRowNum() + 1 - sh.getFirstRowNum();
//            System.out.println(rows);
                for (int i = 0; i < rows; i++) {
                HSSFRow row = sh.getRow(i);
//                    Row row = sh.getRow(i);
//                该行的总列数
                    int cols = row.getLastCellNum() + 1 - row.getFirstCellNum();
//                System.out.println("第"+i+"行的总列数"+cols);
//                用来存放该行每一列的值
                    String[] str = new String[cols];
                    for (int j = 0; j < cols; j++) {
                        Object col = row.getCell(j);
                        Object colNext = row.getCell( (j + 1));
//                    该列不为空，直接读到 行数组里
                        if (col != null) {
                            str[j] = col.toString();
                        } else {                    // 该列为空
//                        该列的后面一列不为空，用空字符串占位
                            if (colNext != null) {
                                Object colValue = "";
                                str[j] = colValue.toString();
                            }
                        }

                    }
                    list.add(str);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String[]> getExcelBySheetNumber(InputStream is, int sheetNumber) {
//        声明集合 List<String[]> ，
//        List<String[]> 的元素 行数组String[]为excel中的每一行
        List<String[]> list = new ArrayList<String[]>();

        try {
//            将is流实例到 一个excel流里
//            HSSFWorkbook hwk = new HSSFWorkbook(is);

            try {
                Workbook hwk = null;
                hwk = new XSSFWorkbook(is);
                //            得到book第一个工作薄sheet
                Sheet sh= hwk.getSheetAt(sheetNumber);
//            HSSFSheet sh = hwk.getSheetAt(sheetNumber);
//            总行数
                int rows = sh.getLastRowNum() + 1 - sh.getFirstRowNum();
//            System.out.println(rows);
                for (int i = 0; i < rows; i++) {
//                HSSFRow row = sh.getRow(i);
                    Row row = sh.getRow(i);
//                该行的总列数
                    int cols = row.getLastCellNum() + 1 - row.getFirstCellNum();
//                System.out.println("第"+i+"行的总列数"+cols);
//                用来存放该行每一列的值
                    String[] str = new String[cols];
                    for (int j = 0; j < cols; j++) {
                        Object col = row.getCell((short) j);
                        Object colNext = row.getCell((short) (j + 1));
//                    该列不为空，直接读到 行数组里
                        if (col != null) {
                            str[j] = col.toString();
                        } else {                    // 该列为空
//                        该列的后面一列不为空，用空字符串占位
                            if (colNext != null) {
                                Object colValue = "";
                                str[j] = colValue.toString();
                            }
                        }

                    }
                    list.add(str);
                }
            } catch (Exception ex) {
                HSSFWorkbook hwk = new HSSFWorkbook(is);
//                hwk = new HSSFWorkbook(is);
                //            得到book第一个工作薄sheet
//                Sheet sh= hwk.getSheetAt(sheetNumber);
            HSSFSheet sh = hwk.getSheetAt(sheetNumber);
//            总行数
                int rows = sh.getLastRowNum() + 1 - sh.getFirstRowNum();
//            System.out.println(rows);
                for (int i = 0; i < rows; i++) {
                HSSFRow row = sh.getRow(i);
//                    Row row = sh.getRow(i);
//                该行的总列数
                    int cols = row.getLastCellNum() + 1 - row.getFirstCellNum();
//                System.out.println("第"+i+"行的总列数"+cols);
//                用来存放该行每一列的值
                    String[] str = new String[cols];
                    for (int j = 0; j < cols; j++) {
                        Object col = row.getCell(j);
                        Object colNext = row.getCell((j + 1));
//                    该列不为空，直接读到 行数组里
                        if (col != null) {
                            str[j] = col.toString();
                        } else {                    // 该列为空
//                        该列的后面一列不为空，用空字符串占位
                            if (colNext != null) {
                                Object colValue = "";
                                str[j] = colValue.toString();
                            }
                        }

                    }
                    list.add(str);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


}
