/**
 * @author wupf@asiainfo. com 查找代码对应的IMPORT
 */
package org.jpf.codeanalysis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.utils.cvsutil.JpfCvsUtil;
import org.jpf.utils.ios.AiFileUtil;



public class FindSourceImport {
    private static final Logger logger = LogManager.getLogger();

    int iThreadNum = 0;



    public FindSourceImport() {

    }

    /**
     * 
     * @param strSvnUrl
     */
    public void doAnalysisImport(String strPath) {

        logger.info(strPath);
        // TODO Auto-generated constructor stub
        try {
            Vector<String> vJavaFiles = new Vector<String>();
            Vector<String> vImports = new Vector<String>();
            AiFileUtil.getFiles(strPath, vJavaFiles, ".java");

            logger.info("JAVA FILES= " + vJavaFiles.size());
            JpfCvsUtil.appendCsv("find_source_import.csv",
                    strPath + "\t" + vJavaFiles.size()+ "\t" + "\n");
            for (int i = 0; i < vJavaFiles.size(); i++) {
                // logger.info("Thread.activeCount()= " + Thread.activeCount());
                ClassImport cClassImport = new ClassImport(vImports, vJavaFiles.get(i));
                cClassImport.setName("ClassImport" + iThreadNum++);
                cClassImport.start();
                while (Thread.activeCount() > 100) {
                    try {
                        // 主线程等待子线程执行
                        Thread.sleep(1000);
                        logger.info("Thread.activeCount()="+Thread.activeCount());
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

            while (Thread.activeCount() > 1) {
                try {
                    // 主线程等待子线程执行
                    Thread.sleep(2000);
                    logger.info("Thread.activeCount()="+Thread.activeCount());
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            vJavaFiles.clear();

            HashMap<String, Integer> map = new HashMap<String, Integer>();
            while (vImports.size() > 0) {
                String strImport = vImports.get(0);
                if (map.containsKey(strImport)) {

                    map.put(strImport, map.get(strImport) + 1);
                } else {
                    map.put(strImport, 1);
                }
                vImports.remove(0);
            }
            Iterator iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                logger.info( entry.getKey()+":"+entry.getValue());
                JpfCvsUtil.appendCsv("find_source_import.csv",
                        entry.getKey() + "\t" + entry.getValue() + "\t" + "\n");
            }

            logger.info("write result to find_source_import.csv");

        } catch (Exception ex) {
            // TODO: handle exception
        }
    }

    public static void main(String[] args) {
        if (args.length == 1) {
            long start = System.currentTimeMillis();
            FindSourceImport cFindSourceChangeUnit = new FindSourceImport();
            cFindSourceChangeUnit.doAnalysisImport(args[0]);

            logger.info(" ExcuteTime " + (System.currentTimeMillis() - start));

        } else {
            logger.warn("error input param");
        }
    }


}
