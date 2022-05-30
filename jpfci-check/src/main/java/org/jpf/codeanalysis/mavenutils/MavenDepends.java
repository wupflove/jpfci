/**
 * @author 吴平福 E-mail:421722623@qq.com
 * @version 创建时间：2017年9月27日 上午10:17:24 类说明
 */

package org.jpf.codeanalysis.mavenutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 */
public final class MavenDepends {
    private static final Logger logger = LogManager.getLogger();

    /**
     * 
     */
    public MavenDepends(String strLogFile) {
        // TODO Auto-generated constructor stub
        InputStreamReader read = null;
        final String strKey = "Downloading:";
        try {
            if (null == strLogFile || 0 == strLogFile.trim().length()) {
                return;
            }
            File f = new File(strLogFile);
            if (!f.exists()) {

                return;
            }
            StringBuilder sb = new StringBuilder();
            read = new InputStreamReader(new FileInputStream(f), "GBK");

            BufferedReader reader = new BufferedReader(read);

            String line;
            int iDependCount=0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(strKey)) {
                    sb.append(line.substring(strKey.length(), line.length()).trim()).append("\r\n");
                    iDependCount++;
                }
            }
            reader.close();
            System.out.println(sb.toString());
            System.out.println("total depends:"+iDependCount);
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        } finally {
            try {
                if (null != read) {
                    read.close();
                }
            } catch (Exception ex2) {
                // TODO: handle exception
            }
        }
    }



    /**
     * @category 输入MVN日志
     * @author 吴平福
     * @param args update 2017年9月27日
     */

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        MavenDepends cMavenDepends =
                new MavenDepends("D:\\svn\\ecommerce-branch-20170912\\mvn.sonar.log");
    }

}
