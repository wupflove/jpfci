/** 
* @author 吴平福 
* E-mail:wupf@asiainfo.com 
* @version 创建时间：2017年11月5日 下午9:04:17 
* 类说明 
*/ 

package org.jpf.codeanalysis.importscheck;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jpf.utils.xmls.JpfXmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 */
public class ReadXmlRule {
    private transient static final Logger logger = LogManager.getLogger();
    /**
     * 
     */
    public ReadXmlRule() {
        // TODO Auto-generated constructor stub
    }
    
    public boolean loadCheckRule(List<CheckRule> listCheckRule)
    {
        try {
            NodeList cNodeList = JpfXmlUtil.getNodeList("CheckRule", "ImportCheckRule.xml");
            if ( cNodeList.getLength()>0) {
                Element el = (Element) cNodeList.item(0);
                // 下载pdm时用户名
                String svnUser = JpfXmlUtil.getParStrValue(el, "PkgName");
                logger.info(svnUser);
                svnUser = JpfXmlUtil.getParStrValue(el, "ExcludePkg");
                logger.info(svnUser);
            }else
            {
                logger.info("Not find Node : AI_ImportCheckRule ");
                return false;

            }

            
            CheckRule cCheckRule = new CheckRule();
            List<String> listExcludePkg = new LinkedList<String>();
            listExcludePkg.add("java.sql");
            cCheckRule.setPkgName("org.jpf.ci");
            cCheckRule.setListExcludePkg(listExcludePkg);
            listCheckRule.add(cCheckRule);
            return true;
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        }
        return false;
    }
    
    public static void main(String[] args)
    {
        ReadXmlRule cReadXmlRule=new ReadXmlRule();
        List<CheckRule> listCheckRule=new ArrayList<CheckRule> ();
        cReadXmlRule.loadCheckRule(listCheckRule);
        logger.info("game over");
    }
}
