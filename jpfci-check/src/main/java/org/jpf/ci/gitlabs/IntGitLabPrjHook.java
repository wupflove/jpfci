/** 
* @author 吴平福 
* E-mail:wupf@asiainfo.com 
* @version 创建时间：2016年7月16日 上午11:42:24 
* 类说明 
*/ 

package org.jpf.ci.gitlabs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 */
public class IntGitLabPrjHook {
    private static final Logger logger = LogManager.getLogger();
    /**
     * 
     */
    public IntGitLabPrjHook() {
        // TODO Auto-generated constructor stub
        try {

            for (int i = 1; i < 1129; i++) {

                ProjecInfo cProjecInfo = GitLabUtils.getPrjInfoById(i);
                if (cProjecInfo == null) {
                    continue;
                }
                if (cProjecInfo.getId() == null) {
                    continue;
                }

                try {

                        logger.info(cProjecInfo.toString());
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.clear();
                        params.add(new BasicNameValuePair("url", "http://10.1.31.241/poemcode/GitlabService?reqValue=exist"));
                        logger.info(GitlabInterfaceConst.HTTPURL + "/projects/" + cProjecInfo.getId() + "/hooks"
                             + "?private_token=" + GitlabInterfaceConst.PRIVATE_TOKEN);
                          GitLabUtils.postContent(GitlabInterfaceConst.HTTPURL + "/projects/" + cProjecInfo.getId() + "/hooks"
                                +  "?private_token=" + GitlabInterfaceConst.PRIVATE_TOKEN, params);
                } catch (Exception ex) {
                    // TODO: handle exception
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        }
    }

    /**
     * @category 
     * @author 吴平福 
     * @param args
     * update 2016年7月16日
     */

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        IntGitLabPrjHook cIntGitLabPrjHook=new IntGitLabPrjHook();
    }

}
