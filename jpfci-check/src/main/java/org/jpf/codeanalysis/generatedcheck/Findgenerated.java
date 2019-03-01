/** 
* @author 吴平福 
* E-mail:wupf@asiainfo.com 
* @version 创建时间：2017年9月23日 下午3:37:49 
* 类说明 
*/ 

package org.jpf.codeanalysis.generatedcheck;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.utils.ios.AiFileUtil;



/**
 * 
 */
public class Findgenerated {
    private static final Logger logger = LogManager.getLogger();

    /**
     * 
     * @category 去除非SRC目录下文件 
     * @author 吴平福 
     * @param vFiles
     * update 2017年9月23日
     */
    private void RemoveNonSrcPath(Vector<String> vFiles)
    {
        for(int i=vFiles.size()-1;i>=0;i--)
        {
            if (vFiles.get(i).indexOf("src")==-1)
            {
                vFiles.remove(i);
                i--;
            }
            if (vFiles.get(i).indexOf("test")>=0)
            {
                vFiles.remove(i);
                i--;
            }
            if (vFiles.get(i).indexOf("target")>=0)
            {
                vFiles.remove(i);
                i--;
            }
        }
    }
    /**
     * 
     */
    public Findgenerated(String strInputSrcPath ) {
        // TODO Auto-generated constructor stub
        try {
            Vector<String> vFiles=new Vector<String>();
            AiFileUtil.getFiles(strInputSrcPath, vFiles, ".java");
            RemoveNonSrcPath(vFiles);
            logger.info("total file count {}",vFiles.size());
            String strChat="GBK";
            StringBuilder sb=new StringBuilder();
            //自动化生成文件数
            int iGeneratedFileCount=0;
            for(int i=0;i<vFiles.size();i++)
            {
                String strFileName=vFiles.get(i);
                FileInputStream in=null;
                try {
                    
                    if (null == strFileName || 0 == strFileName.trim().length()) {
                        break;
                    }
                    File f = new File(strFileName);
                    if (!f.exists()) {

                        break;
                    }
                    

                    in = new FileInputStream(f);
                    // 指定读取文件时以UTF-8的格式读取
                    if (strChat.length() > 0) {
                        strChat = Charset.defaultCharset().name();
                    }
                    BufferedReader br = new BufferedReader(new InputStreamReader(in, strChat));

                    String line = br.readLine();
                    int iLineNum=1;
                    while (line != null) {
                        if (line.trim().startsWith("import "))
                        {
                            break;
                        }
                        if (line.indexOf("generated")>=0)
                        {
                            sb.append(strFileName).append("\t").append(iLineNum).append("\t").append(line).append("\n");
                            iGeneratedFileCount++;
                            break;
                        }
                        line = br.readLine();
                        iLineNum++;
                    }
                    
                } catch (Exception ex) {
                    // TODO: handle exception
                    ex.printStackTrace();
                }finally {
                    try {
                        if (null!=in)
                        {
                            in.close();
                        }
                    } catch (Exception ex2) {
                        // TODO: handle exception
                    }
                }
            }
            
            //result write to csv
            AiFileUtil.writeToCsv(this.getClass().getName()+  ".csv", sb);
            logger.info("write result into {}",this.getClass().getName());
            logger.info("iGeneratedFileCount ={}",iGeneratedFileCount);
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        }

    }

    /**
     * @category 
     * @author 吴平福 
     * @param args
     * update 2017年9月23日
     */

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        long start = System.currentTimeMillis();
        Findgenerated cFindgenerated=new Findgenerated("F:\\crm3.0_code\\code");
        logger.info("ExcuteTime " + (System.currentTimeMillis() - start) + "ms");
    }

}
