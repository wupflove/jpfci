/** 
 * @author 吴平福 
 * E-mail:wupf@asiainfo.com 
 * @version 创建时间：2016年5月3日 下午12:38:26 
 * 类说明 
 */

package org.jpf.exploretest;

import java.io.IOException;

import java.util.Vector;

import org.apache.http.ParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 */
public class jpfExplore {

    private final Logger logger = LogManager.getLogger();

    /**
     * @throws ParseException
     * @category 功能点
     * @throws ClientProtocolException
     * @throws IOException
     */
    public jpfExplore() {
        // 进入登录页面
        RunLogin cRunLogin = new RunLogin();
        try {
            if (cRunLogin.LoginRun())
            {

                ReadCase cReadCase = new ReadCase();
                Vector<CaseInfo> mapCaseIndex = cReadCase.ReadCase("read_case.xls");
                FuzzingCase cFuzzingCase = new FuzzingCase();
                Vector<CaseInfo> mapCaseResult=cFuzzingCase.fuzzingCaseByExcel(mapCaseIndex);
                RunCase cRunCase = new RunCase();
                cRunCase.runCaseFromMap(mapCaseResult);

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 
     * @category author 吴平福
     * @param args
     * @throws Exception
     *             update 2016年5月3日
     */
    public static void main(String[] args) throws Exception {
        jpfExplore cjpfExploreTest = new jpfExplore();
        /*
         * 1 读CASE 2 变异CASE 3 登陆 4 执行CASE 5 保存结果
         */

    }

}
