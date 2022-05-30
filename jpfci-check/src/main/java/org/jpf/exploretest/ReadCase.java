/** 
 * @author 吴平福 
 * E-mail:421722623@qq.com 
 * @version 创建时间：2016年5月6日 下午2:30:05 
 * 类说明 
 */

package org.jpf.exploretest;

import java.io.File;
import java.util.Vector;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.utils.xmls.JpfXmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.jpf.utils.ios.AiFileUtil;

/**
 * 
 */
public class ReadCase {
    private static final Logger logger = LogManager.getLogger();

    public Vector< CaseInfo> ReadCase(String strFileName) {
        try {
            if (strFileName.endsWith(".xls"))
            {
              
                return readFromExcel(strFileName);
            }
            else if (strFileName.endsWith(".xml"))
            {
                return readFromXml(strFileName);
            }
            else {
                logger.error("fileName Is error!");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
        // TODO Auto-generated constructor stub
    }

    /**
     * 
     * @category 从EXECL读取CASE
     * @author 吴平福
     * @return update 2016年5月6日
     */
    public Vector<CaseInfo> readFromExcel(String strFileName) throws Exception {
        String importCols = "0,1,2,3,4";
        int start_row = 0;
        Workbook rwb = null;
        File importFile = null;
        importFile = new File(strFileName);
        rwb = Workbook.getWorkbook(importFile);
        String[] m_cols = importCols.split(",");

        Vector<CaseInfo> caseMapIndex = new Vector< CaseInfo>();
        for (int m = 0; m < rwb.getSheets().length; m++)
        {
            Sheet sheet = rwb.getSheet(m);

            for (int i = start_row+1; i < 4; i++)
            {
                CaseInfo cCaseInfo = new CaseInfo();
                cCaseInfo.setCaseName(sheet.getCell(Integer.parseInt(m_cols[1]), i).getContents().trim());
                cCaseInfo.setCaseUrl(sheet.getCell(Integer.parseInt(m_cols[2]), i).getContents().trim());
                cCaseInfo.setCaseIndex(sheet.getCell(Integer.parseInt(m_cols[3]), i).getContents().trim());
                cCaseInfo.setCaseChange(sheet.getCell(Integer.parseInt(m_cols[4]), i).getContents().trim());
                logger.debug(cCaseInfo.getCaseName() + "\t" + cCaseInfo.getCaseUrl()
                        + "\t" + cCaseInfo.getCaseIndex() + "\n");
                caseMapIndex.add(cCaseInfo);
             
                //logger.debug(map.get(cCaseInfo.getCaseName()).toString());
            }

        }

        return caseMapIndex;
    }

    /**
     * 
     * @category 从XML文件读取CASE
     * @author 吴平福
     * @return update 2016年5月6日
     */
    public Vector< CaseInfo> readFromXml(String strFileName) throws Exception {
        final String strNodeName = "case_conf";
        CaseInfo cCaseInfo = new CaseInfo();
        Vector< CaseInfo> map = new Vector< CaseInfo>();
        AiFileUtil.checkFile(strFileName);
        NodeList n = JpfXmlUtil.getNodeList(strNodeName, strFileName);
        for (int i = 0; i < n.getLength(); i++)
        {
            Element el = (Element) n.item(0);
            cCaseInfo.setCaseName(JpfXmlUtil.getParStrValue(el, "case_name"));
            cCaseInfo.setCaseUrl(JpfXmlUtil.getParStrValue(el, "case_url"));
            cCaseInfo.setCaseIndex(JpfXmlUtil.getParStrValue(el, "post_data"));
            logger.debug("caseName=" + cCaseInfo.getCaseName() + "\tcaseUrl=" + cCaseInfo.getCaseUrl() + "\tcaseData="
                    + cCaseInfo.getCaseIndex() + "\n");
            map.add( cCaseInfo);
        }

        return map;
    }
}
