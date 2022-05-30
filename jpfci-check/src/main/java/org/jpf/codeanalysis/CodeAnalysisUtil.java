/**
 * @author 吴平福 E-mail:421722623@qq.com
 * @version 创建时间：2017年7月6日 下午5:40:42 类说明
 */

package org.jpf.codeanalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jpf.utils.ios.AiFileUtil;

/**
 * 
 */
public class CodeAnalysisUtil {
    private static final Logger logger = LogManager.getLogger();

    /**
     * 
     */
    public CodeAnalysisUtil() {
        // TODO Auto-generated constructor stub
    }

    /**
     * 
     * @category 
     * @author 吴平福
     * @param FileName
     * @return
     * @throws Exception update 2016年4月16日
     */
    public static String getFileTxt(String strFileName) throws Exception {
        if (null == strFileName || 0 == strFileName.trim().length()) {
            return "";
        }
        File f = new File(strFileName);
        if (!f.exists()) {

            return "";
        }
        StringBuffer sb = new StringBuffer();
        InputStreamReader read = new InputStreamReader(new FileInputStream(f), "GBK");

        BufferedReader reader = new BufferedReader(read);

        String line;

        while ((line = reader.readLine()) != null) {

            sb.append(line).append("\r\n");

        }
        reader.close();
        read.close();
        return sb.toString();
    }
    /**
     * 
     * @category 
     * @author 吴平福 
     * @param strFileName
     * @param strCode
     * @return
     * @throws Exception
     * update 2017年9月28日
     */
    public static String getFileTxt(String strFileName, String strCode) throws Exception {
        if (null == strFileName || 0 == strFileName.trim().length()) {
            return "";
        }
        File f = new File(strFileName);
        if (!f.exists()) {

            return "";
        }
        StringBuffer sb = new StringBuffer();
        InputStreamReader read = new InputStreamReader(new FileInputStream(f), strCode);

        BufferedReader reader = new BufferedReader(read);

        String line;

        while ((line = reader.readLine()) != null) {

            sb.append(line).append("\r\n");

        }
        reader.close();
        read.close();
        return sb.toString();
    }

    /**
     * 
     * @category 
     * @author 吴平福
     * @param strFilePath
     * @return update 2017年7月20日
     */
    public static String getLastFilePath(String strFilePath) {
        int i = strFilePath.lastIndexOf(java.io.File.separator);
        if (i > 0) {
            return strFilePath.substring(i + 1, strFilePath.length());
        } else {
            i = strFilePath.lastIndexOf("/");
            if (i > 0) {
                return strFilePath.substring(i + 1, strFilePath.length());
            }
        }
        return "";
    }

    /**
     * @category获取文件路径
     * @param sFilePathName String
     * @return String
     */
    public static String getFilePath(String sFilePathName) {
        int i = sFilePathName.lastIndexOf("\\");
        
        if (i > 0) {
            return sFilePathName.substring(0, i);
        }
        //for linux
        i = sFilePathName.lastIndexOf("/");
        if (i>0)
        {
            return sFilePathName.substring(0, i);
        }
        return sFilePathName;
    }
    /**
     * 
     * @category @author 吴平福
     * @param cSvnChangeInfo
     * @param strStartDateTime
     * @param UserName
     * @param PassWord
     * @param iNumber
     * @return
     * @throws Exception update 2017年7月20日
     */
    public static String getFile(SvnChangeInfo cSvnChangeInfo, String strStartDateTime,
            String UserName, String PassWord, int iNumber, String strPathKey) throws Exception {
        String tmpFilePath = AiFileUtil.getCurrentPath() + File.separator + strPathKey
                + File.separator + iNumber;
        AiFileUtil.mkdir(tmpFilePath);

        String strLastFilePath =
                getLastFilePath(AiFileUtil.getFilePath(cSvnChangeInfo.getChangeFileName()));
        //logger.info(cSvnChangeInfo.getChangeFileName());
        //logger.info(getFilePath(cSvnChangeInfo.getChangeFileName()));
        //logger.info("Thread.activeCount()= " + Thread.activeCount());
        SvnCheckOutUtil.CheckOut(getFilePath(cSvnChangeInfo.getChangeFileName()),
                UserName, PassWord, tmpFilePath, strStartDateTime);
        //logger.info("Thread.activeCount()= " + Thread.activeCount());
        /*
         * String strCmd = "cd " +tmpFilePath + ";svn co " +
         * AiFileUtil.getFilePath(cSvnChangeInfo.getChangeFileName()) + " -r {\"" + strStartDateTime
         * + "\"} --username " + UserName + " --password " + PassWord ; //logger.debug(strCmd);
         * RunCmd.runBashCmd(strCmd);
         */
        String sourceFile1 = tmpFilePath + File.separator + strLastFilePath + File.separator
                + AiFileUtil.getFileName(cSvnChangeInfo.getChangeFileName());
        if (AiFileUtil.FileExist(sourceFile1)) {
            return sourceFile1;
        }
        sourceFile1 = tmpFilePath + File.separator
                + AiFileUtil.getFileName(cSvnChangeInfo.getChangeFileName());
        if (AiFileUtil.FileExist(sourceFile1)) {
            return sourceFile1;
        } else {
            throw new Exception("file not exist:" + cSvnChangeInfo.getChangeFileName());
        }

    }
    
    /**
     * 
     * @category @author 吴平福
     * @return
     * @throws IOException update 2017年5月18日
     */
    public static String getCurrentPath() throws IOException {
        File directory = new File("");// 参数为空
        return directory.getCanonicalPath();

    }
}
