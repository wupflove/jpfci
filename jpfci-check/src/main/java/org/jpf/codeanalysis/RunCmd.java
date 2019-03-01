/**
 * @author 吴平福 E-mail:wupf@asiainfo.com
 * @version 创建时间：2017年5月22日 上午10:40:03 类说明
 */

package org.jpf.codeanalysis;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.ci.sonarplugin.SvnUsr;

import org.jpf.utils.ios.AiOsUtil;

/**
 * 
 */
public class RunCmd {
    private static final Logger logger = LogManager.getLogger();

    /**
     * 
     */
    private RunCmd() {
        // TODO Auto-generated constructor stub
    }

    /**
     * 
     * @category 执行命令不等待输出
     * @author 吴平福
     * @param strCmd update 2017年7月10日
     */
    public static void runBashCmd(String strCmd) {

        try {
            if (AiOsUtil.getInstance().isWindows()) {
                runBashCmd_Win(strCmd);
            } else {
                runBashCmd_Linux(strCmd);
            }
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        }
    }

    /**
     * 
     * @category WINDOWS执行命令不等待输出
     * @author 吴平福
     * @param strCmd update 2017年7月10日
     */
    public static void runBashCmd_Win(String strCmd) {
        Process p = null;
        try {
            logger.info(strCmd);
            Runtime rt = Runtime.getRuntime();
            p = rt.exec("cmd.exe /c " + strCmd);
            // System.out.println(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (null != p) {
                    p.destroy();
                    p = null;
                }
            } catch (Exception ex) {
                // TODO: handle exception
            }
        }
    }

    /**
     * 
     * @category LINUX执行命令不等待输出
     * @author 吴平福
     * @param strCmd update 2017年7月10日
     */
    public static void runBashCmd_Linux(String strCmd) throws Exception {
        if (strCmd == null || strCmd.trim().length() == 0) {
            return;
        }
        logger.info(strCmd);

        String[] cmd = new String[] {"bash", "-c", strCmd};
        Process process = Runtime.getRuntime().exec(cmd);

        process.waitFor();
        InputStreamReader ir = new InputStreamReader(process.getInputStream());
        LineNumberReader input = new LineNumberReader(ir);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = input.readLine()) != null) {
            sb.append(line.trim());
            //logger.info(line.trim());
        }
        int iRetValue = process.exitValue();

        if (iRetValue != 0) {
            logger.error(sb.toString());
            sb.setLength(0);
            throw new RuntimeException(
                    "run error!".concat(String.valueOf(String.valueOf(iRetValue))));
        }

    }

    /**
     * 
     * @category 执行命令得到结果
     * @author 吴平福
     * @param strCmd
     * @return update 2017年7月10日
     */
    public static String runBashCmdResult_Win(String strCmd) {
        logger.info(strCmd);
        Process p = null;
        try {

            Runtime rt = Runtime.getRuntime();
            p = rt.exec("cmd.exe /c " + strCmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");

            }
            // System.out.println(sb.toString());
            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            try {
                if (null != p) {
                    p.destroy();
                    p = null;
                }
            } catch (Exception ex) {
                // TODO: handle exception
            }
        }
    }
}
