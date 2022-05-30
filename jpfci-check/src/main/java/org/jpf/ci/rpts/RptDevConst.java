/**
 * @author 吴平福 E-mail:421722623@qq.com
 * @version 创建时间：2015年7月23日 下午1:39:59 类说明
 */

package org.jpf.ci.rpts;


/**
 * 
 */
public class RptDevConst {
    class MVNOConst {
        private static final long PRJ_AVG_ID = 14700;
        private static final String INCLUDE_PRJ_ID = "15153,15434,15507";
        private static final String PRJ_AVG_METRIC = "1,24,30,35,37,43,50,75,78,80,81,82";
    }

    class BIUConst {
        private static final long PRJ_AVG_ID = 14700;
        private static final String INCLUDE_PRJ_ID =
                "15770,16535,17977,18180,19074,22633,22695,22884,23358,25978,26075,26076,26077,26496,26497,26798,26912,26913,26969,27325,27407,27419,27444";
        private static final String PRJ_AVG_METRIC = "1,24,30,35,37,43,50,75,78,80,81,82";

    }
    class BIURUNNERConst {
        private static final long PRJ_AVG_ID = 14700;
        private static final String INCLUDE_PRJ_ID =
                "27921,27572,27524,27523,27535,28052,28045,28067,27660,28153,28634,27753,28635,28637,27610,29464,27684,27582,27810";
        private static final String PRJ_AVG_METRIC = "1,24,30,35,37,43,50,75,78,80,81,82";
        private static final String SONARURL = "http://10.1.241.51:8080/sonar/resource/index/";
    }
    class BILLINGConst {
        private static final long PRJ_AVG_ID = 80664;
        private static final String INCLUDE_PRJ_ID =
                "162751,147976,9644,200095,38506,37517,107633,199771,80896,82444,75725,78222,78199,6897,171182,168064,198264,85129,176168,11944,185527,58290,48384,172388,56890,195729,201570";
        private static final String PRJ_AVG_METRIC = "1,24,30,35,37,43,50,75,78,80,81,82";
        private static final String DBURL = "jdbc:mysql://10.1.234.174:3306/sonar";
        private static final String DBUSR = "sonar";
        private static final String DBPWD = "qmcsonar2017!";
        private static final String SONARURL = "http://10.1.234.75:9000/sonar/drilldown/issues/";
    }
    class BIUBAASConst {
        private static final long PRJ_AVG_ID = 14700;
        private static final String INCLUDE_PRJ_ID =
                "104157,103706,103973,103646,103993,103856,103953,103932,103914,104247,104298,104364,103917,104507,103882,103459,103374";
        private static final String PRJ_AVG_METRIC = "1,24,30,35,37,43,50,75,78,80,81,82";
        private static final String SONARURL = "http://10.1.241.52:8080/sonar/resource/index/";
    }

    class VISUALWALL_CONST {
        private static final String DBURL = "jdbc:mysql://10.1.234.75:4306/visualwall";
        private static final String DBUSR = "sonar";
        private static final String DBPWD = "wupflove";

    }

    /**
     * @return the pRJ_AVG_ID
     */
    public static long getPRJ_AVG_ID(String strType) {
        if (strType.equalsIgnoreCase("MVNO")) {
            return MVNOConst.PRJ_AVG_ID;
        }
        if (strType.equalsIgnoreCase("BIU")) {
            return BIUConst.PRJ_AVG_ID;
        }
        if (strType.equalsIgnoreCase("BIU_RUNNER")) {
            return BIURUNNERConst.PRJ_AVG_ID;
        }
        if (strType.equalsIgnoreCase("BILLING")) {
            return BILLINGConst.PRJ_AVG_ID;
        }
        return 0;
    }

    /**
     * @return the eXCLUDE_PRJ_ID
     */
    public static String getIncludePrjId(String strType) {
        if (strType.equalsIgnoreCase("MVNO")) {
            return MVNOConst.INCLUDE_PRJ_ID;
        }
        if (strType.equalsIgnoreCase("BIU")) {
            return BIUConst.INCLUDE_PRJ_ID;
        }
        if (strType.equalsIgnoreCase("BIU_RUNNER")) {
            return BIURUNNERConst.INCLUDE_PRJ_ID;
        }
        if (strType.equalsIgnoreCase("BILLING")) {
            return BILLINGConst.INCLUDE_PRJ_ID;
        }
        if (strType.equalsIgnoreCase("BIU_BAAS")) {
            return BIUBAASConst.INCLUDE_PRJ_ID;
        }
        return "";
    }

    /**
     * @return the pRJ_AVG_METRIC
     */
    public static String getPRJ_AVG_METRIC(String strType) {
        if (strType.equalsIgnoreCase("MVNO")) {
            return MVNOConst.PRJ_AVG_METRIC;
        }
        if (strType.equalsIgnoreCase("BIU")) {
            return BIUConst.PRJ_AVG_METRIC;
        }
        if (strType.equalsIgnoreCase("BIU_RUNNER")) {
            return BIURUNNERConst.PRJ_AVG_METRIC;
        }
        if (strType.equalsIgnoreCase("BILLING")) {
            return BILLINGConst.PRJ_AVG_METRIC;
        }
        if (strType.equalsIgnoreCase("BIU_BAAS")) {
            return BIUBAASConst.PRJ_AVG_METRIC;
        }
        return "";
    }

    /**
     * 
     * @param strType
     * @return 被测试类名：TODO 被测试接口名:TODO 测试场景：TODO 前置参数：TODO 入参： 校验值： 测试备注： update 2015年12月5日
     */
    public static String getDBUrl(String strType) {
        if (strType.equalsIgnoreCase("BILLING")) {
            return BILLINGConst.DBURL;
        } else if (strType.equalsIgnoreCase("VISUALWALL")) {
            return VISUALWALL_CONST.DBURL;
        }
        return "";
    }

    /**
     * 
     * @param strType
     * @return 被测试类名：TODO 被测试接口名:TODO 测试场景：TODO 前置参数：TODO 入参： 校验值： 测试备注： update 2015年12月5日
     */
    public static String getStrDbUsr(String strType) {
        if (strType.equalsIgnoreCase("BILLING")) {
            return BILLINGConst.DBUSR;

        } else if (strType.equalsIgnoreCase("VISUALWALL")) {
            return VISUALWALL_CONST.DBUSR;
        }
        return "";
    }

    /**
     * 
     * @param strType
     * @return 被测试类名：TODO 被测试接口名:TODO 测试场景：TODO 前置参数：TODO 入参： 校验值： 测试备注： update 2015年12月5日
     */
    public static String getStrDbPwd(String strType) {
        if (strType.equalsIgnoreCase("BILLING")) {
            return BILLINGConst.DBPWD;
        } else if (strType.equalsIgnoreCase("VISUALWALL")) {
            return VISUALWALL_CONST.DBPWD;
        }
        return "";
    }

    /**
     * 
     * @param strType
     * @param lPrjId
     * @return 被测试类名：TODO 被测试接口名:TODO 测试场景：TODO 前置参数：TODO 入参： 校验值： 测试备注： update 2015年12月5日
     */
    public static String getSonarUrl(String strType, Long lPrjId) {
        if (strType.equalsIgnoreCase("BILLING")) {
            return BILLINGConst.SONARURL;
        }
        if (strType.equalsIgnoreCase("BIU_RUNNER")) {
            return BIURUNNERConst.SONARURL;
        }
        return "http://10.1.241.51:8080/sonar/drilldown/issues/" + lPrjId;
    }

    /**
     * 
     * @param strType
     * @param lPrjId
     * @param strPrjName
     * @param strFileName
     * @return 被测试类名：TODO 被测试接口名:TODO 测试场景：TODO 前置参数：TODO 入参： 校验值： 测试备注： update 2015年12月5日
     */
    public static String getSonarUrl(String strType, Long lPrjId, String strPrjName,
            String strFileName) {
        if (strType.equalsIgnoreCase("BIU_RUNNER")) {
            return BIURUNNERConst.SONARURL + strPrjName + ":" + strFileName
                    + "?display_title=true&metric=82&rule=MAJOR";
        }
        return "";
    }
}
