/** 
* @author 吴平福 
* E-mail:421722623@qq.com 
* @version 创建时间：2016年6月10日 上午9:07:22 
* 类说明 
*/

package org.jpf.exploretest;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 */
public class RunCase2 {
    private static final Logger logger = LogManager.getLogger();
    private long iSuccessCount = 0;
    private long iFailCount = 0;

    /**
     * 
     */
    public RunCase2(String strCaseName) {
        // TODO Auto-generated constructor stub
        logger.info(strCaseName);
        for (int i = 0; i < 1; i++) {

            try {
                ExcuteURLPostThread cExcuteURLPostThread = new ExcuteURLPostThread();
                cExcuteURLPostThread.SetUrl(strCaseName);
                cExcuteURLPostThread.start();
            } catch (Exception ex) {
                // TODO: handle exception
                ex.printStackTrace();
            }

        }

    }

    /**
     * @category @author 吴平福
     * @param args
     *            update 2016年6月10日
     */

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        logger.info(args.length);
        RunCase2 cRunCase2 = new RunCase2(args[0]);
    }

    class ExcuteURLPostThread extends Thread {
        String strUrlName;

        public void SetUrl(String strUrlName) {
            this.strUrlName = strUrlName;
        }

        public void run() {
            runHttpGet();

        }

        public void runHttpPost() {
            // for (int i = 0; i < 100000; i++) {
            while (true) {
                try {
                    DefaultHttpClient httpclient = new DefaultHttpClient();
                    if (iSuccessCount % 2 == 0) {
                        strUrlName = "http://cart.jd.com/gate.action?pid=1082266&pcount=1&ptype=1";
                    } else {
                        strUrlName = "http://cart.jd.com/gate.action?pid=2493392&pcount=1&ptype=1";
                    }
                    // strUrlName="http://cart.jd.com/gate.action?pid=2493392&pcount=1&ptype=1";
                    HttpPost httppost = new HttpPost(strUrlName);
                    httppost.setHeader("Content-Type", "application/json");
                    httppost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                    httppost.addHeader("X-Requested-With", "XMLHttpRequest");
                    httppost.addHeader("User-Agent",
                            "Mozilla/5.0 (Windows NT 6.1; rv:46.0) Gecko/20100101 Firefox/46.0");
                    HttpResponse response = httpclient.execute(httppost);
                    httppost.releaseConnection();
                    // logger.info(response.getStatusLine());
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        iSuccessCount++;
                        logger.info(EntityUtils.toString(response.getEntity(), "UTF-8"));
                    } else {
                        iFailCount++;
                    }
                    logger.info(response.getStatusLine().getStatusCode());
                    logger.info(this.getId() + ":" + iSuccessCount + "/" + iFailCount);

                    sleep(1 + (int) (Math.random() * 100));
                } catch (Exception ex) {
                    // TODO: handle exception
                    ex.printStackTrace();
                }

            }

        }

        public void runHttpGet() {
            // for (int i = 0; i < 100000; i++) {
            while (true) {
                try {
                    DefaultHttpClient httpclient = new DefaultHttpClient();
                    if (iSuccessCount % 2 == 0) {
                        strUrlName = "http://cart.jd.com/gate.action?pid=1082266&pcount=1&ptype=1";
                    } else {
                        strUrlName = "http://cart.jd.com/gate.action?pid=2493392&pcount=1&ptype=1";
                    }
                    // strUrlName="http://cart.jd.com/gate.action?pid=2493392&pcount=1&ptype=1";
                    HttpGet cHttpGet = new HttpGet(strUrlName);
                    /*
                     * httppost.setHeader("Content-Type", "application/json");
                     * httppost.addHeader("Content-Type",
                     * "application/x-www-form-urlencoded; charset=UTF-8");
                     * httppost.addHeader("X-Requested-With", "XMLHttpRequest");
                     * httppost.addHeader("User-Agent",
                     * "Mozilla/5.0 (Windows NT 6.1; rv:46.0) Gecko/20100101 Firefox/46.0"
                     * );
                     */
                    HttpResponse response = httpclient.execute(cHttpGet);
                    cHttpGet.releaseConnection();
                    // logger.info(response.getStatusLine());
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        iSuccessCount++;
                        logger.info(EntityUtils.toString(response.getEntity(), "UTF-8"));
                    } else {
                        iFailCount++;
                    }
                    logger.info(response.getStatusLine().getStatusCode());
                    logger.info(this.getId() + ":" + iSuccessCount + "/" + iFailCount);

                    sleep(1 + (int) (Math.random() * 100));
                } catch (Exception ex) {
                    // TODO: handle exception
                    ex.printStackTrace();
                }

            }

        }
    }
}
