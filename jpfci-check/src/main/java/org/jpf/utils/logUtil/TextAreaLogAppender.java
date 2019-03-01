/** 
* @author 吴平福 
* E-mail:wupf@asiainfo.com 
* @version 创建时间：2017年7月18日 下午3:12:27 
* 类说明 
*/ 

package org.jpf.utils.logUtil;


import java.io.IOException;
import java.util.Vector;
import javax.swing.JTextArea;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 */
public class TextAreaLogAppender {
    private static final Logger logger = LogManager.getLogger();
    private static JTextArea textArea;
    private static Vector<String> vector=new Vector<String>();

    public static Vector<String> getLogVector()
    {
        return vector;
    }
    /**
     * 默认的构造
     * 
     * @param textArea 记录器名称，该记录器输出的日志信息将被截取并输出到指定的JTextArea组件
     * @param scroll JTextArea组件使用的滚动面板，因为在JTextArea中输出日志时，默认会使垂直滚动条自动向下滚动，若不需要此功能，此参数可省略
     * @throws IOException
     */
    public static void setTextAreaLogAppender(JTextArea m_textArea)
            throws IOException {
        textArea = m_textArea;

    }

    public static void log(String line) {
        try {
          vector.add(line);
          //logger.info(vector.size());
        } catch (Exception ex) {
            // 异常不做处理
        }
    }
    public static void clearLog()
    {
        textArea.setText("");
        textArea.paintImmediately(textArea.getBounds());
    }
}