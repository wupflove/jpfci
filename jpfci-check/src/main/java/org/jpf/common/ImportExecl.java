/**
 * 
 */
package org.jpf.common;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.utils.dbsql.AiDBUtil;
import org.jpf.utils.dbsql.AppConn;
import org.jpf.utils.excelutils.AiExcelJxlUtil;
import org.jpf.utils.ios.AiFileUtil;
import org.jpf.utils.xmls.JpfXmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class ImportExecl {

  private final static String DB_CONFIG_XML = "jpf_import_excel_config.xml";
  private static final Logger logger = LogManager.getLogger();

  /**
   * @category: construct
   * @param m_strSaveFileName :file name
   * @throws Exception
   */
  private String delete_str = "";
  private String insert_str = "";
  private int START_ROW = 0;
  private String importCols = "";
  private String PKCOLS = "";

  /**
   * @category @param strImportFileName
   * @param strNodeName
   * @throws Exception
   */
  private ImportExecl(String strImportFileName, String strNodeName) throws Exception {

    AiFileUtil.checkFile(DB_CONFIG_XML);
    NodeList n = JpfXmlUtil.getNodeList(strNodeName, DB_CONFIG_XML);
    if (1 == n.getLength()) {
      Element el = (Element) n.item(0);
      delete_str = JpfXmlUtil.getParStrValue(el, "DELETE_STR");//
      insert_str = JpfXmlUtil.getParStrValue(el, "INSERT_STR");//
      importCols = JpfXmlUtil.getParStrValue(el, "importCols");//
      PKCOLS = JpfXmlUtil.getParStrValue(el, "PKCOLS");//
      START_ROW = JpfXmlUtil.getParIntValue(el, "START_ROW");
      try {
        String m_str = importDataFromExcel(strImportFileName, START_ROW, strNodeName);
      } catch (Exception exp) {
        logger.error(exp);
      }
    }
  }

  /**
   * @category @author 吴平福
   * @param strFileName
   * @param START_ROW
   * @param strNodeName
   * @return
   * @throws Exception update 2016年4月15日
   */
  private String importDataFromExcel(String strFileName, int START_ROW, String strNodeName)
      throws Exception {
    Connection conn = null;
    Workbook rwb = null;
    File importFile = null;
    PreparedStatement pStmt = null;
    long m_rowcount = 0;
    long m_succcount = 0;

    try {
      conn = AppConn.GetInstance().GetConn(strNodeName);
      if (conn != null) {
        conn.setAutoCommit(false);
        pStmt = conn.prepareStatement(delete_str);
        pStmt.executeUpdate();
        conn.commit();
        pStmt = conn.prepareStatement(insert_str);

        String[] m_cols = importCols.split(",");
        importFile = new File(strFileName);
        rwb = Workbook.getWorkbook(importFile);
        for (int m = 0; m < rwb.getSheets().length; m++) {
          Sheet sheet = rwb.getSheet(m);
          if (logger.isInfoEnabled()) {
            logger.info("all rows=" + sheet.getRows());
          }

          if (AiExcelJxlUtil.isRowNull(sheet, START_ROW)
              && AiExcelJxlUtil.isRowNull(sheet, START_ROW + 1)) {
            break;

          }
          if (m_cols.length > sheet.getColumns()) {
            throw new Exception("no enough column!");
          }

          for (int i = START_ROW; i < sheet.getRows(); i++) {
            try {
              if (AiExcelJxlUtil.isRowNull(sheet, i) && AiExcelJxlUtil.isRowNull(sheet, i + 1)) {
                break;
              }
              int j;
              for (j = 0; j < m_cols.length; j++) {
                try {
                  Cell m_cell = sheet.getCell(Integer.parseInt(m_cols[j]), i);
                  String m_data = getCellLabel(m_cell);

                  // String
                  // m_data=sheet.getCell(Integer.parseInt(m_cols[j]),i).getContents();
                  if (PKCOLS.indexOf(String.valueOf(j)) >= 0) {
                    if (m_data == null || m_data.equalsIgnoreCase("")) {
                      // throw new
                      // Exception("Import error!");
                      continue;
                    }
                  }
                  pStmt.setString(j + 1, m_data);
                  System.out.println(m_data);

                } catch (SQLException e) {
                  e.printStackTrace();
                  logger.error(e);
                }

              }
              m_rowcount++;
              pStmt.executeUpdate();
              m_succcount++;
            } catch (SQLException e) {
              e.printStackTrace();
              logger.error(e);
            }

          }
        }
        conn.commit();
      }

    } catch (SQLException sqlExp) {
      logger.error(sqlExp.getMessage(), sqlExp);
      logger.error("PK_ERROR_CODE:" + sqlExp.getErrorCode());

    } catch (Exception exp) {
      exp.printStackTrace();
      logger.info(exp);
      throw exp;
    } finally {
      AiDBUtil.doClear(conn);
      AiExcelJxlUtil.close(rwb);
    }
    return "Total Record Count:" + m_rowcount + ";Sucess Record Count:" + m_succcount;
  }

  /**
   * 
   * @param in_cell Cell
   * @param in_row String
   * @return String
   */

  private String getCellLabel(Cell in_cell) {

    return in_cell.getContents().trim();

  }

  public static void main(String[] args) throws Exception {
    // TODO Auto-generated method stub
    if (2 == args.length) {
      ImportExecl cImportExecl = new ImportExecl(args[0], args[1]);
    } else {
      logger.warn("parameter error!");
    }
  }
}
