/**
 * @author 吴平福 E-mail:wupf@asiainfo.com
 * @version 创建时间：2017年11月4日 下午6:09:27 类说明 查找非法的import关系
 */

package org.jpf.codeanalysis.importscheck;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.utils.ios.AiFileUtil;

/**
 * 
 */
public class ImportCheck {
    private transient static final Logger logger = LogManager.getLogger();

    /**
     * 
     */
    public ImportCheck(String inFilePath) {
        // TODO Auto-generated constructor stub
        long start = System.currentTimeMillis();
        long lFileCount=0;
        try {
            // init check rule from xml
            List<CheckRule> listCheckRule = new LinkedList<CheckRule>();
            initCheckRule(listCheckRule);
            // load file
            Vector<String> vJavaFile = new Vector<String>();
            AiFileUtil.getFiles(inFilePath, vJavaFile, ".java");
            lFileCount=vJavaFile.size();
            // just file
            for (int i = 0; i < vJavaFile.size(); i++) {
                doCheckRule(vJavaFile.get(i), listCheckRule);
            }

        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        }
        logger.info("ExcuteTime " + (System.currentTimeMillis() - start) + "ms");
    }

    /**
     * 
     * @category @author 吴平福
     * @param strInFileName
     * @param listCheckRule update 2017年11月5日
     */
    private void doCheckRule(final String strInFileName, List<CheckRule> listCheckRule) {
        String strFileName = strInFileName.replace("\\", ".");
        // logger.info(strFileName);

        for (int i = 0; i < listCheckRule.size(); i++) {
            if (strFileName.indexOf(listCheckRule.get(i).getPkgName()) >= 0) {
                // logger.info(listCheckRule.get(i).getPkgName());
                doCheckRule(strInFileName, listCheckRule.get(i));
            }
        }
    }

    /**
     * 
     * @category @author 吴平福
     * @param strInFileName
     * @param clistCheckRule update 2017年11月5日
     */
    private void doCheckRule(final String strInFileName, CheckRule clistCheckRule) {
        // logger.info(strInFileName);
        InputStreamReader read = null;
        BufferedReader reader = null;
        try {
            File f = new File(strInFileName);
            if (!f.exists()) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            read = new InputStreamReader(new FileInputStream(f), "GBK");

            reader = new BufferedReader(read);

            String line;
            int iLineNum = 0;
            while ((line = reader.readLine()) != null) {
                iLineNum++;
                if (line.trim().length() > 0) {
                    for (int j = 0; j < clistCheckRule.getListExcludePkg().size(); j++) {
                        if (line.indexOf(clistCheckRule.getListExcludePkg().get(j)) >= 0) {
                            sb.append(strInFileName).append(":").append(iLineNum).append("   ").append(line)
                                    .append("\r\n");
                        }
                    }
                }
            }
            if (sb.length() > 0) {
                System.out.println(sb.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (null != reader)
                    reader.close();
            } catch (Exception ex2) {
                // TODO: handle exception
            }
            try {
                if (null != read)
                    read.close();
            } catch (Exception ex2) {
                // TODO: handle exception
            }
        }


    }

    /**
     * 
     * @category @author 吴平福
     * @param listCheckRule update 2017年11月5日
     */
    private void initCheckRule(List<CheckRule> listCheckRule) {
        CheckRule cCheckRule = new CheckRule();
        List<String> listExcludePkg = new LinkedList<String>();
        listExcludePkg.add("java.sql");
        cCheckRule.setPkgName("org.jpf.ci");
        cCheckRule.setListExcludePkg(listExcludePkg);
        listCheckRule.add(cCheckRule);
    }

    /**
     * @category @author 吴平福
     * @param args update 2017年11月4日
     */

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        ImportCheck cImportCheck = new ImportCheck("D:\\jworkspaces\\jpfapp\\src");
    }

}
