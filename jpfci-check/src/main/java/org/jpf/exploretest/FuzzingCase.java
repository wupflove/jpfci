/** 
 * @author 吴平福 
 * E-mail:wupf@asiainfo.com 
 * @version 创建时间：2016年5月6日 下午2:39:04 
 * 类说明 
 */

package org.jpf.exploretest;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.alibaba.fastjson.JSON;


/**
 * 
 */
public class FuzzingCase {

    public String postResult = "";
    private static final Logger logger = LogManager.getLogger();

    public FuzzingCase() {
        // TODO Auto-generated constructor stub
        
    }

    /**
     * 
     * @category 根据我们默认规则变异CASE
     * @author 吴平福
     * @param mapCase
     *            update 2016年5月6日
     */
    public void fuzzingCaseByRule(LinkedHashMap<String, CaseInfo> mapCase)
    {

    }

    /**
     * 
     * @category 根据EXCEL规则变异CASE
     * @author 谭亮
     * @param mapCase
     * @update 2016年5月6日
     */
    public Vector<CaseInfo> fuzzingCaseByExcel(Vector<CaseInfo> mapCase)
    {
        Vector<CaseInfo> caseMapResult= new Vector<CaseInfo>();
        ObjectMapper mapper = new ObjectMapper();
        long lSize = mapCase.size();
        for (int i = 0; i < lSize; i++)
        {
            // 取得vector里的各个case对象
            CaseInfo cCaseInfo = (CaseInfo) mapCase.get(i);
            logger.debug("CaseIndex:" + cCaseInfo);
            Map<String, String> map = new LinkedHashMap<String, String>();
            if (null != cCaseInfo.getCaseChange() && cCaseInfo.getCaseChange().length() > 0)
            {
                logger.debug("CaseChange-->" + cCaseInfo.getCaseChange());
                // 需要修改的值保存到map里
                Map<String, String> value_change = (HashMap<String, String>) JSON.parseObject(
                        cCaseInfo.getCaseChange(), Map.class);
                String[] index = cCaseInfo.getCaseIndex().split("&");

                for (int j = 0; j < index.length; j++)
                {
                    String[] value = index[j].split("=", 2);

                    // 如果post里的值是“{”开头，说明是json数据，直接在json对象里修改我们需要的值
                    if (value[1].startsWith("{"))
                    {
                        Map<String, String> json_value = (HashMap<String, String>) JSON.parseObject(value[1],
                                Map.class);
                        // 这里还需要增加一个for循环与我们需要修改的数据进行替换
                        for (Map.Entry<String, String> entrypost : json_value.entrySet())
                        {

                            for (Map.Entry<String, String> entrychange : value_change.entrySet())
                            {
                                if (entrychange.getKey() == entrypost.getKey())
                                {
                                    json_value.put(entrypost.getKey(), entrychange.getValue());
                                    logger.info("need_change_num:" + entrychange.getKey() + "-->"
                                            + entrychange.getValue());
                                }
                            }
                        }
                        try {
                            map.put(value[0], mapper.writeValueAsString(json_value));
                        } catch (JsonGenerationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (JsonMappingException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        for (Map.Entry<String, String> entrychange : value_change.entrySet())
                        {
                            if (entrychange.getKey() == value[0])
                            {
                                map.put(value[0], entrychange.getValue());
                            }
                        }
                    }
                }
                for (Entry<String, String> entry : map.entrySet())
                {
                    postResult += entry.getKey().toString() + "=" + entry.getValue().toString() + "&";

                }
                cCaseInfo.setCaseResult(cCaseInfo.getCaseUrl() + "?" + postResult);

            }
            else
            {
                cCaseInfo.setCaseResult(cCaseInfo.toString());

            }
            caseMapResult.add(cCaseInfo);
            logger.debug("CaseResult:" + cCaseInfo.getCaseResult());

        }
        return caseMapResult;
    }

    // 处理post里的值，将所有值以key,value形式保存到map里，方便post和修改数据的处理
    /**
     * 
     * @category author 谭亮
     * @param value
     * @return update 2016-5-23
     */
    /*
     * public LinkedHashMap<String, String> dealPost(String value) {
     * LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
     * String[] index = value.split("&"); for (int i = 0; i < index.length; i++)
     * { String[] setValue = index[i].split(":", 2); map.put(setValue[0],
     * setValue[1]); } return map; }
     */
    public static void main(String[] args)
    {
        FuzzingCase cFuzzingCase = new FuzzingCase();

        try {
            cFuzzingCase.fuzzingCaseByExcel(new ReadCase().readFromExcel("read_case.xls"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
