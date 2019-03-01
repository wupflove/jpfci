/**
 * @author wupf@asiainfo. com 查找代码对应的IMPORT
 */
package org.jpf.codeanalysis;

import java.util.HashMap;
import java.util.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.utils.cvsutil.JpfCvsUtil;
import org.jpf.utils.ios.AiFileUtil;



public class FindSourceChinese {
    private static final Logger logger = LogManager.getLogger();

    int iThreadNum = 0;



    public FindSourceChinese() {

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
            JpfCvsUtil.appendCsv("find_source_chinese.csv",
                    strPath + "\t" + vJavaFiles.size()+ "\t" + "\n");
            for (int i = 0; i < vJavaFiles.size(); i++) {
                // logger.info("Thread.activeCount()= " + Thread.activeCount());
                ClassUseChineseThread cClassUseChinese= new ClassUseChineseThread(vImports, vJavaFiles.get(i));
                cClassUseChinese.setName("ClassImport" + iThreadNum++);
                cClassUseChinese.run();
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

                vImports.remove(0);
            }

            logger.info("write result to find_source_chinese.csv");

        } catch (Exception ex) {
            // TODO: handle exception
        }
    }

    public static void main(String[] args) {
        if (args.length == 1) {
            long start = System.currentTimeMillis();
            FindSourceChinese cFindSourceChangeUnit = new FindSourceChinese();
            cFindSourceChangeUnit.doAnalysisImport(args[0]);

            logger.info(" ExcuteTime " + (System.currentTimeMillis() - start));

        } else {
            logger.warn("error input param");
        }
    }


}
