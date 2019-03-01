/**
 * @author 吴平福 E-mail:wupf@asiainfo.com
 * @version 创建时间：2017年9月27日 下午9:01:22 类说明
 */

package org.jpf.unittests.utcheck;

import java.io.File;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.utils.ios.AiFileUtil;

/**
 * 
 */
public final class UnitTestCheck {
    private static final Logger logger = LogManager.getLogger();
    final String SourceKey = "\\src\\main\\java";
    final String UtKey = "\\src\\test\\java";
    StringBuilder sb = new StringBuilder();

    /**
     * 
     */
    public UnitTestCheck(String strFilePath, int PrjType) {
        // TODO Auto-generated constructor stub
        long start = System.currentTimeMillis();
        try {
            File f = new File(strFilePath);

            File[] f2 = f.listFiles();

            if (f2 != null) {
                for (int i = 0; i < f2.length; i++) {
                    String strFile = f2[i].toString() + SourceKey;
                    logger.info(strFile);
                    File f3 = new File(strFile);
                    strFile = f2[i].toString() + UtKey;
                    logger.info(strFile);
                    File f4 = new File(strFile);


                    // logger.info(f3.isDirectory()+" ");

                    // logger.info(f4.isDirectory()+" ");
                    if (f3.isDirectory() && f4.isDirectory()) {
                        // logger.info(f3.toString());
                        // logger.info(f4.toString());
                        DoCheck(strFile);
                    }

                }
            }
            AiFileUtil.writeToCsv(strFilePath + "\\ut.csv", sb);
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        }
        logger.info("ExcuteTime " + (System.currentTimeMillis() - start) + "ms");
    }

    private void DoCheck(String strUtPath) {
        try {

            Vector<String> vUtFiles = new Vector<String>();
            AiFileUtil.getFiles(strUtPath, vUtFiles, ".java");
            for (int i = 0; i < vUtFiles.size(); i++) {
                String strUtFileName = AiFileUtil.getFileName(vUtFiles.get(i));

                if (!strUtFileName.equalsIgnoreCase("TestAll.java")) {
                    if (!strUtFileName.startsWith("Test") && !strUtFileName.endsWith("Test.java")) {
                        // 单元测试文件名称不合法
                        // System.out.println(vUtFiles.get(i));
                        sb.append("单元测试文件名称不合法\t").append(vUtFiles.get(i)).append("\n");
                    }
                }
                strUtFileName = strUtFileName.replaceAll("Test", "");
                String strSourceFileName = AiFileUtil.getFilePath(vUtFiles.get(i));
                // logger.info(strSourceFileName);
                strSourceFileName =
                        strSourceFileName.replaceAll("test", "main") + "\\" + strUtFileName;
                logger.info(strSourceFileName);
                File file = new File(strSourceFileName);
                if (!file.exists()) {
                    // 对应的源文件不存在
                    System.out.println(vUtFiles.get(i));
                    sb.append("对应的源文件不存在\t").append(vUtFiles.get(i)).append("\n");
                }

            }
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        }

    }

    /**
     * @category @author 吴平福
     * @param args update 2017年9月27日
     */

    public static void main(String[] args) {
        // TODO Auto-generated method stub
       new UnitTestCheck("D:\\svn\\ecommerce-branch-20170912", 1);
    }

}
