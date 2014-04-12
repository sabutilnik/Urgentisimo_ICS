package uno.Urgentisimo;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;



import android.util.Log;

import jxl.CellView;
import jxl.format.*;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

//import jxl.write.getCellFormat;

public class WriteExcel {

  private static final String TAG = "URGENTISIMO : WRITE EXCEL : ";
  private Integer CELLHEIGHT = 240;
  private Integer CELLMIN = 50;
  private WritableCellFormat timesBoldUnderline;
  private WritableCellFormat times;
  private String outputFile;
  private ArrayList<ArrayList<String>> datos;

  
  public WriteExcel(ArrayList<ArrayList<String>> reporte, String reportefile) {

	  this.datos = reporte;
	  this.outputFile = reportefile;
	  
  }
public void setOutputFile(String outputFile) {
  this.outputFile = outputFile;
  }

  public void write() throws IOException, WriteException {
    File file = new File(outputFile);
    WorkbookSettings wbSettings = new WorkbookSettings();

    wbSettings.setLocale(new Locale("es", "ES"));

    WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
    workbook.createSheet("Reporte", 0);
    WritableSheet excelSheet = workbook.getSheet(0);
    createLabel(excelSheet);
    createBoldLabel(excelSheet);
    createContent(excelSheet);

    // if the directory does not exist, create it
    if (!file.getParentFile().exists()) {
      Log.v(TAG,"creating directory: " + file.getParent());
      boolean result = file.mkdir();  

       if(result) {    
         Log.v(TAG,file.getParent()+" created");  
       }
    }
    
    
    workbook.write();
    workbook.close();
  }

  private void createLabel(WritableSheet sheet)
      throws WriteException {
    WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
    times = new WritableCellFormat(times10pt);
    times.setWrap(true);
    WritableFont times10ptBoldUnderline = new WritableFont(WritableFont.TIMES, 10, WritableFont.NO_BOLD, false,
        UnderlineStyle.SINGLE);
    timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
    timesBoldUnderline.setWrap(true);
    CellView cv = new CellView();
    cv.setFormat(times);
    cv.setFormat(timesBoldUnderline);
    cv.setAutosize(true);
  }
  
  private void createBoldLabel(WritableSheet sheet)
	      throws WriteException {
	    WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
	    times = new WritableCellFormat(times10pt);
	    times.setWrap(true);
	    WritableFont times10ptBoldUnderline = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false,
	        UnderlineStyle.SINGLE);
	    timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
	    timesBoldUnderline.setWrap(true);
	    CellView cv = new CellView();
	    cv.setFormat(times);
	    cv.setFormat(timesBoldUnderline);
	    cv.setAutosize(true);
	  }

  private void createContent(WritableSheet sheet) throws WriteException,
      RowsExceededException {
	  	for (int i = 0; i< 5; i++) {
	  		switch (i) {
	  		case 0:
	  			sheet.setColumnView(i, 20);
	  			break;
	  		case 1:
	  			sheet.setColumnView(i, 35);
	  			break;
	  		case 2:
	  			sheet.setColumnView(i, 40);
	  			break;
	  		default:
	  			sheet.setColumnView(i, 10);
	  			break;
	  		} 		
	  	}
	    
	    for (int i = 0; i<datos.size(); i++) {
	    	ArrayList<String> fila = datos.get(i);
	    	for (int j = 0; j<fila.size(); j++) {
	    		String texto = "";
	    		try {
	    			texto = fila.get(j).toString();
	    		} catch (Exception e) {
	    			texto = "vacio ";
	    		}
	    		try{
	    			Integer numero = Integer.valueOf(texto);
	    			addNumber(sheet,j,i,numero);
	    		} catch (Exception e) {
                    if (j == 0 && texto.length()>0) {
                        addLabel(sheet,j,i,texto);
                    } else {
                        addLabel(sheet, j, i, texto);
                    }
	    		}
	    		
	    	}
	    }
  }

//    private static WritableCellFormat getCellFormat(Colour colour, Colour pattern) throws WriteException {
//        WritableFont cellFont = new WritableFont(WritableFont.TIMES, 16);
//        WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
//        cellFormat.setBackground(colour, pattern);
//        return cellFormat;
//    }


    private void addCaption(WritableSheet sheet, int column, int row, String s)
      throws RowsExceededException, WriteException {
    Label label;
    label = new Label(column, row, s, timesBoldUnderline);
    sheet.addCell(label);
  }

  private void addNumber(WritableSheet sheet, int column, int row,
      Integer integer) throws WriteException, RowsExceededException {
    Number number;
    number = new Number(column, row, integer, times);
    sheet.addCell(number);
  }

  private void addLabel(WritableSheet sheet, int column, int row, String s)
      throws WriteException, RowsExceededException {
    Label label;
    
    WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
    WritableCellFormat cellFont = new WritableCellFormat(times10pt);
    cellFont.setWrap(true);
    WritableFont times10ptBoldUnderline = new WritableFont(WritableFont.TIMES, 10, WritableFont.NO_BOLD, false,
            UnderlineStyle.SINGLE);
    if (column == 0 && s.length()>0) {
    	sheet.mergeCells(0, row, 4, row);
        times10ptBoldUnderline = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false,
                UnderlineStyle.SINGLE);
    } 
    
    timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
    timesBoldUnderline.setWrap(true);

    WritableCellFormat cellFormat = new WritableCellFormat(cellFont);

    if (column == 0 && s.length() > 0) {
        cellFormat.setBackground(Colour.GRAY_25);
    }


    label = new Label(column, row, s, cellFormat);
    if (s.length()> CELLMIN) {
    	Integer division = s.length()/CELLMIN;
    	if (division > 0) {
    		division = division+1;
    		sheet.setRowView(row, division*CELLHEIGHT);
    	} else {
    		division = 1;
    	}
	    sheet.setRowView(row,division*CELLHEIGHT);
    }  
    sheet.addCell(label);
  }

} 