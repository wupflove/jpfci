/**
 * @author 吴平福 E-mail:wupf@asiainfo-linkage.com
 * @version 创建时间：2013-4-16 下午1:33:28 类说明
 */

package org.jpf.ci.rpts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.visualwall.WallsDbConn;

import org.jpf.utils.AiDateTimeUtil;
import org.jpf.utils.AiStringUtil;
import org.jpf.utils.dbsql.AiDBUtil;
import org.jpf.utils.ios.AiFileUtil;
import org.jpf.utils.mails.AiMail;


/**
 * 
 */
public class SendMailPrjScore {
    private static final Logger LOGGER = LogManager.getLogger();

    private String strCCMails = "";

    /**
     * @category 构造函数
     * @param strConfigFileName
     */
    public SendMailPrjScore(String strConfigFileName) {

        try {
            SonarRptInfo.GetInstance().readRptInfo(strConfigFileName);

            strCCMails = SonarRptInfo.GetInstance().getStrCCMails() + ",";
            LOGGER.debug("strCCMails={}", strCCMails);
            LinkedHashMap<String, ModuleQualityInfo> map_develop = getPrjInfo();

            makeModuleMail(map_develop);

            LOGGER.info("game over");
        } catch (Exception ex) {
            LOGGER.error(ex);
            ex.printStackTrace();
        }
    }


    /**
     * 
     * @category @author 吴平福
     * @param d
     * @return update 2016年9月5日
     */
    private String getChangeColor(Double d) {
        String strColor = "<font  size='2'>(" + String.valueOf(d) + "%)</font>";
        if (d > 0) {
            strColor = "<font color='" + COLOR_GREEN + "' size='2'>(+" + d + "%)</font>";
        } else if (d < 0) {
            strColor = "<font color='" + COLOR_ORANGE + "'  size='2'>(" + d + "%)</font>";
        }
        return strColor;
    }

    final String COLOR_RED = "#FF0000";
    final String COLOR_GREEN = "#008000";
    final String COLOR_ORANGE = "#fac090";

    /**
     * 
     * @category @author 吴平福
     * @param lValue
     * @return update 2016年9月5日
     */
    private String getChangeColor(long lValue) {
        String strColor = "<font  size='2'>(" + String.valueOf(lValue) + ")</font>";
        if (lValue > 0) {
            strColor = "<font color='" + COLOR_GREEN + "' size='2'>(+" + lValue + ")</font>";
        } else if (lValue < 0) {
            strColor = "<font  color='" + COLOR_ORANGE + "' size='2'>(" + lValue + ")</font>";
        }
        return strColor;
    }

    /**
     * 
     * @param lValue
     * @return 被测试类名：TODO 被测试接口名:TODO 测试场景：TODO 前置参数：TODO 入参： 校验值： 测试备注： update 2014年1月22日
     */
    private String getUnChangeColor(long lValue) {
        String strColor = "<font  size='2'>(" + String.valueOf(lValue) + ")</font>";

        strColor = "<font s size='2'>(" + lValue + ")</font>";

        return strColor;
    }

    private String getChangeUnColor(long lValue) {
        String strColor = "<font  size='2'>(" + String.valueOf(lValue) + ")</font>";
        if (lValue > 0) {
            strColor = "<font color='" + COLOR_ORANGE + "' size='2'>(+" + lValue + ")</font>";
        } else if (lValue < 0) {
            strColor = "<font  color='" + COLOR_GREEN + "' size='2'>(" + lValue + ")</font>";
        }
        return strColor;
    }

    private void WriteTdStr(StringBuffer sb, long a, long b) {
        sb.append(
                "<TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; '>");
        if (a > b) {
            sb.append("<font color='" + COLOR_ORANGE + "'>").append(a).append("</font>");
        } else {
            sb.append(a);
        }

    }

    /**
     * 
     * @category 写入红色字体
     * @author 吴平福
     * @param sb
     * @param a
     * @param b update 2016年9月5日
     */
    private void WriteTdRed(StringBuffer sb, long a, long b) {
        sb.append(
                "<TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; '>");
        if (a < b) {
            sb.append("<font color='" + COLOR_RED + "'>").append(a).append("</font>");
        } else {
            sb.append(a);
        }
        sb.append("</TD>");
    }

    private void WriteTdStr(StringBuffer sb, String strDate) {
        sb.append(
                "<TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; '>");

        if (1 == AiDateTimeUtil.compare_date(AiDateTimeUtil.getDay("yyyy-MM-dd", -3), strDate)) {
            sb.append("<font color='" + COLOR_ORANGE + "'>").append(strDate).append("</font>");
        } else {
            sb.append(strDate);
        }
        sb.append("</TD>");
    }

    /**
     * 
     * @category @author 吴平福
     * @param sb
     * @param a
     * @param b update 2016年9月5日
     */
    private void getTdStrReversed(StringBuffer sb, long a, long b) {
        addTdTitle(sb);
        if (a < b) {
            sb.append("<font color='" + COLOR_ORANGE + "'>").append(a).append("</font>");
        } else {
            sb.append(a);
        }
    }

    /**
     * 
     * @category @author 吴平福
     * @param sb
     * @param a
     * @param b update 2016年9月5日
     */
    private void getTdStrReversed(StringBuffer sb, Double a, Double b) {
        if (a > b) {
            sb.append("<font color='" + COLOR_ORANGE + "'>").append(String.format("%.2f", a))
                    .append("%</font>");
        } else {
            sb.append(String.format("%.2f", a)).append("%");
        }

    }

    /**
     * 
     * @category @author 吴平福
     * @param sb
     * @param a
     * @param b update 2016年9月5日
     */
    private void WriteTdStr(StringBuffer sb, Double a, Double b) {
        addTdTitle(sb);
        if (a < b) {
            sb.append("<font color='" + COLOR_ORANGE + "'>").append(String.format("%.2f", a))
                    .append("%</font>");
        } else {
            sb.append(String.format("%.2f", a)).append("%");
        }

    }

    private void addTdTitle(StringBuffer sb) {
        sb.append(
                "<TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; '>");

    }

    private String addTdMiddle(String str) {
        str = "<TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; '>"
                + str + "</TD>";
        return str;

    }

    private String addTdRedMiddle(String str) {
        str = "<TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; '>"
                + "<font color='" + COLOR_RED + "'>" + str + "</font></TD>";
        return str;

    }

    private String getModuleInfo(int iRowCount, ModuleQualityInfo cModuleInfo,
            ModuleQualityInfo cAvgModuleInfo) {
        StringBuffer sb = new StringBuffer();

        String strColorString = "";
        LOGGER.info("cModuleInfo.strName=" + cModuleInfo.prj_name);

        if (iRowCount % 2 == 0) {
            strColorString = "transparent";
        } else {
            strColorString = " #fefefe";
        }
        sb.append("<TR style='HEIGHT: 16.5pt' height=22 bgcolor='").append(strColorString)
                .append("'><TD class=xl67 style='BORDER-TOP: windowtext; HEIGHT: 16.5pt; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext 0.5pt solid; '")
                .append(" height=22>").append(cModuleInfo.prj_name).append("</TD>")
                .append(addTdMiddle(cModuleInfo.areaname));
        if (cModuleInfo.score == 0) {
            sb.append(addTdRedMiddle(cModuleInfo.prj_owner));
        } else {
            sb.append(addTdMiddle(cModuleInfo.prj_owner));
        }
        WriteTdRed(sb, cModuleInfo.score, cAvgModuleInfo.score);

        WriteTdStr(sb, cModuleInfo.build_date);

        WriteTdStr(sb, cModuleInfo.blocker_violations, cAvgModuleInfo.blocker_violations);
        sb.append(getChangeUnColor(cModuleInfo.blocker_violationsChange));
        sb.append("</td>");

        WriteTdStr(sb, cModuleInfo.critical_violations, cAvgModuleInfo.critical_violations);
        sb.append(getChangeUnColor(cModuleInfo.critical_violationsChange));
        sb.append("</td>");

        WriteTdStr(sb, cModuleInfo.major_violations, cAvgModuleInfo.major_violations);
        sb.append(getChangeUnColor(cModuleInfo.major_violationsChange));
        sb.append("</td>");

        WriteTdStr(sb, cModuleInfo.PrjLines, cAvgModuleInfo.PrjLines);
        sb.append(getChangeUnColor(cModuleInfo.PrjLinesChange));
        sb.append("</td>");;


        WriteTdStr(sb, cModuleInfo.dbranch_coverage, cAvgModuleInfo.dbranch_coverage);
        sb.append(getChangeColor(cModuleInfo.dbranch_coverageChange));
        sb.append("</td>");


        WriteTdStr(sb, cModuleInfo.dline_coverage, cAvgModuleInfo.dline_coverage);
        sb.append(getChangeColor(cModuleInfo.dline_coverageChange));
        sb.append("</td>");


        getTdStrReversed(sb, cModuleInfo.ltests, cAvgModuleInfo.ltests);
        sb.append(getChangeColor(cModuleInfo.ltestsChange));
        sb.append("</td>");


        WriteTdStr(sb, cModuleInfo.dtest_success_density, cAvgModuleInfo.dtest_success_density);
        sb.append(getChangeColor(cModuleInfo.dtest_success_densityChange));
        sb.append("</td>");


        WriteTdStr(sb, cModuleInfo.dUtcaseperline, cAvgModuleInfo.dUtcaseperline);
        sb.append(getChangeColor(cModuleInfo.dUtcaseperlineChange));
        sb.append("</td>");
        sb.append("</TR>");

        return sb.toString();
    }

    /**
     * 
     * @category @author 吴平福
     * @return
     * @throws Exception update 2016年9月5日
     */
    private LinkedHashMap<String, ModuleQualityInfo> getPrjInfo() throws Exception {
        // 扫描本地目录，从数据库中获取记录
        Connection conn = null;
        LinkedHashMap<String, ModuleQualityInfo> map =
                new LinkedHashMap<String, ModuleQualityInfo>();
        try {

            conn = WallsDbConn.getInstance().getConn();

            String strSql =
                    "select t2. prj_cname,t2.prj_mail,t2.prj_name,t2.leader_mail,t1.build_date,t1.blocker_violations,t1.critical_violations,t1.major_violations,t1.is_last "
                            + " ,t1.areaname,t1.prj_lines,t1.tests,t1.test_success_density,t1.line_coverage,t1.branch_coverage from "
                            + " (select * from bss_rpt a where 2>(select count(*) from bss_rpt where areaname=a.areaname and prj_id=a.prj_id and build_date>a.build_date)"
                            + " and prj_id>0 and areaname in ("
                            + SonarRptInfo.GetInstance().getStrAreaInfo()
                            + "))t1,bss_prj t2 where t1.prj_id=t2.prj_id and t1.areaname=t2.areaname"
                            + " order by t1.areaname,t2.prj_name,t1.build_date desc";
            LOGGER.debug("strSql=" + strSql);

            PreparedStatement pStmt = conn.prepareStatement(strSql);
            ResultSet rs = pStmt.executeQuery();
            String strPrjName = "";
            ModuleQualityInfo cModuleInfo = null;

            while (rs.next()) {
                String strNewPrj =
                        rs.getString("prj_name").trim() + "|" + rs.getString("areaname").trim();

                if (!strPrjName.equalsIgnoreCase(strNewPrj)) {
                    strPrjName = strNewPrj;
                    cModuleInfo = new ModuleQualityInfo();
                    cModuleInfo.prj_owner = rs.getString("prj_mail");
                    cModuleInfo.prj_name = rs.getString("prj_name");
                    cModuleInfo.prj_mail = rs.getString("leader_mail");
                    if (strCCMails.indexOf(cModuleInfo.prj_mail) < 0) {
                        strCCMails += cModuleInfo.prj_mail + ",";

                    }

                    cModuleInfo.build_date = rs.getString("build_date");
                    cModuleInfo.areaname = rs.getString("areaname");
                    cModuleInfo.blocker_violations = rs.getLong("blocker_violations");
                    cModuleInfo.critical_violations = rs.getLong("critical_violations");
                    cModuleInfo.major_violations = rs.getLong("major_violations");
                    cModuleInfo.PrjLines = rs.getLong("prj_lines");
                    if (cModuleInfo.prj_name.equalsIgnoreCase("dbe")) {
                        cModuleInfo.PrjLines = 20649;
                    }
                    cModuleInfo.ltests = rs.getLong("tests");
                    cModuleInfo.dtest_success_density = rs.getDouble("test_success_density") * 100;
                    cModuleInfo.dline_coverage = rs.getDouble("line_coverage") * 100;
                    cModuleInfo.dbranch_coverage = rs.getDouble("branch_coverage") * 100;
                    cModuleInfo.dUtcaseperline =
                            (double) cModuleInfo.ltests * 1000 / cModuleInfo.PrjLines;
                    if (cModuleInfo.areaname.trim().equalsIgnoreCase("AE")) {
                        cModuleInfo.areaname = "AppEngine";
                    }

                    cModuleInfo.FormatData();
                    map.put(strPrjName, cModuleInfo);
                } else {
                    if (cModuleInfo != null) {
                        cModuleInfo.blocker_violationsChange =
                                cModuleInfo.blocker_violations - rs.getLong("blocker_violations");
                        cModuleInfo.critical_violationsChange =
                                cModuleInfo.critical_violations - rs.getLong("critical_violations");
                        cModuleInfo.major_violationsChange =
                                cModuleInfo.major_violations - rs.getLong("major_violations");
                        cModuleInfo.PrjLinesChange = cModuleInfo.PrjLines - rs.getLong("prj_lines");
                        if (cModuleInfo.prj_name.equalsIgnoreCase("dbe")) {
                            cModuleInfo.PrjLinesChange = 0;
                        }
                        cModuleInfo.ltestsChange = cModuleInfo.ltests - rs.getLong("tests");
                        cModuleInfo.dtest_success_densityChange = cModuleInfo.dtest_success_density
                                - rs.getDouble("test_success_density") * 100;
                        cModuleInfo.dline_coverageChange =
                                cModuleInfo.dline_coverage - rs.getDouble("line_coverage") * 100;
                        cModuleInfo.dbranch_coverageChange = cModuleInfo.dbranch_coverage
                                - rs.getDouble("branch_coverage") * 100;
                        cModuleInfo.FormatData();
                    }
                }
            }
            rs.close();
            // logger.debug("写入文件完成,共写入行数：" + iCount);
        } catch (Exception ex) {
            LOGGER.error(ex);
            ex.printStackTrace();

        } finally {
            AiDBUtil.doClear(conn);
        }
        LOGGER.debug("strCCMails={}", strCCMails);
        setAverage(map);
        return map;
    }

    /**
     * 
     * @category 计算平均值
     * @author 吴平福
     * @param map_develop
     * @return update 2016年9月5日
     */
    private ModuleQualityInfo setAverage(LinkedHashMap<String, ModuleQualityInfo> map_develop) {
        int iCount = map_develop.size();
        LOGGER.debug("map_develop.size={}", iCount);
        ModuleQualityInfo cAvgModuleInfo = new ModuleQualityInfo();
        cAvgModuleInfo.prj_name = "所有产品平均值";
        cAvgModuleInfo.build_date = AiDateTimeUtil.getCurrDate();

        cAvgModuleInfo.critical_violationsChange = 0;
        cAvgModuleInfo.critical_violations = 0;
        cAvgModuleInfo.blocker_violationsChange = 0;
        cAvgModuleInfo.blocker_violations = 0;
        cAvgModuleInfo.major_violations = 0;
        cAvgModuleInfo.major_violationsChange = 0;
        cAvgModuleInfo.PrjLinesChange = 0;
        cAvgModuleInfo.PrjLines = 0;
        cAvgModuleInfo.ltestsChange = 0;
        cAvgModuleInfo.ltests = 0;
        cAvgModuleInfo.dbranch_coverageChange = 0;
        cAvgModuleInfo.dbranch_coverage = 0;
        cAvgModuleInfo.dline_coverageChange = 0;
        cAvgModuleInfo.dline_coverage = 0;
        cAvgModuleInfo.dUtcaseperlineChange = 0;
        cAvgModuleInfo.dUtcaseperline = 0;

        for (Iterator iter_table = map_develop.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();
            ModuleQualityInfo cModuleInfo = map_develop.get(key_table);
            cAvgModuleInfo.critical_violationsChange += cModuleInfo.critical_violationsChange;
            cAvgModuleInfo.critical_violations += cModuleInfo.critical_violations;
            cAvgModuleInfo.blocker_violationsChange += cModuleInfo.blocker_violationsChange;
            cAvgModuleInfo.blocker_violations += cModuleInfo.blocker_violations;
            cAvgModuleInfo.major_violationsChange += cModuleInfo.major_violationsChange;
            cAvgModuleInfo.major_violations += cModuleInfo.major_violations;
            cAvgModuleInfo.PrjLinesChange += cModuleInfo.PrjLinesChange;
            cAvgModuleInfo.PrjLines += cModuleInfo.PrjLines;
            cAvgModuleInfo.ltestsChange += cModuleInfo.ltestsChange;
            cAvgModuleInfo.ltests += cModuleInfo.ltests;
            cAvgModuleInfo.dbranch_coverageChange += cModuleInfo.dbranch_coverageChange;
            cAvgModuleInfo.dbranch_coverage += cModuleInfo.dbranch_coverage;
            cAvgModuleInfo.dline_coverageChange += cModuleInfo.dline_coverageChange;
            cAvgModuleInfo.dline_coverage += cModuleInfo.dline_coverage;
            cAvgModuleInfo.dUtcaseperlineChange += cAvgModuleInfo.dUtcaseperlineChange;
            cAvgModuleInfo.dUtcaseperline += cAvgModuleInfo.dUtcaseperline;

        }
        cAvgModuleInfo.critical_violationsChange =
                cAvgModuleInfo.critical_violationsChange / iCount;
        cAvgModuleInfo.critical_violations = cAvgModuleInfo.critical_violations / iCount;
        cAvgModuleInfo.blocker_violations = cAvgModuleInfo.blocker_violations / iCount;
        cAvgModuleInfo.blocker_violationsChange = cAvgModuleInfo.blocker_violationsChange / iCount;
        cAvgModuleInfo.major_violations = cAvgModuleInfo.major_violations / iCount;
        cAvgModuleInfo.major_violationsChange = cAvgModuleInfo.major_violationsChange / iCount;
        cAvgModuleInfo.PrjLinesChange = cAvgModuleInfo.PrjLinesChange / iCount;
        cAvgModuleInfo.PrjLines = cAvgModuleInfo.PrjLines / iCount;
        cAvgModuleInfo.ltestsChange = cAvgModuleInfo.ltestsChange
                / (iCount - SonarRptInfo.GetInstance().getCoverageExcludeCount());
        cAvgModuleInfo.ltests = cAvgModuleInfo.ltests
                / (iCount - SonarRptInfo.GetInstance().getCoverageExcludeCount());
        cAvgModuleInfo.dbranch_coverageChange = cAvgModuleInfo.dbranch_coverageChange
                / (iCount - SonarRptInfo.GetInstance().getCoverageExcludeCount());
        cAvgModuleInfo.dbranch_coverage = cAvgModuleInfo.dbranch_coverage
                / (iCount - SonarRptInfo.GetInstance().getCoverageExcludeCount());
        cAvgModuleInfo.dline_coverageChange = cAvgModuleInfo.dline_coverageChange
                / (iCount - SonarRptInfo.GetInstance().getCoverageExcludeCount());
        cAvgModuleInfo.dline_coverage = cAvgModuleInfo.dline_coverage
                / (iCount - SonarRptInfo.GetInstance().getCoverageExcludeCount());
        cAvgModuleInfo.dUtcaseperlineChange = cAvgModuleInfo.dUtcaseperlineChange
                / (iCount - SonarRptInfo.GetInstance().getCoverageExcludeCount());
        cAvgModuleInfo.dUtcaseperline = cAvgModuleInfo.dUtcaseperline
                / (iCount - SonarRptInfo.GetInstance().getCoverageExcludeCount());

        long lValue = 0;
        int iTestSucc = 0;
        for (Iterator iter_table = map_develop.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();
            ModuleQualityInfo cModuleInfo = map_develop.get(key_table);
            if (cModuleInfo.dtest_success_density > 0) {
                lValue += cModuleInfo.dtest_success_density;
                iTestSucc++;
            }
        }
        if (iTestSucc != 0) {
            cAvgModuleInfo.dtest_success_density = lValue / iTestSucc;
        } else {
            cAvgModuleInfo.dtest_success_density = 0;
        }


        lValue = 0;
        for (Iterator iter_table = map_develop.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();
            ModuleQualityInfo cModuleInfo = map_develop.get(key_table);
            lValue += cModuleInfo.dtest_success_densityChange;
        }
        if (iTestSucc != 0) {
            cAvgModuleInfo.dtest_success_densityChange = lValue / iTestSucc;
        } else {
            cAvgModuleInfo.dtest_success_densityChange = 0;
        }


        cAvgModuleInfo.FormatData();
        return cAvgModuleInfo;
    }

    /**
     * 
     * @category @author 吴平福
     * @param map_develop
     * @throws Exception update 2016年9月5日
     */
    private void makeModuleMail(LinkedHashMap<String, ModuleQualityInfo> map_develop)
            throws Exception {

        try {
            ModuleQualityInfo cAvgModuleInfo = setAverage(map_develop);

            String newMailString = AiFileUtil.getFileTxt(SonarRptInfo.GetInstance().getHtmlMail());

            int iRowCount = 0;
            String strMailText = getModuleInfo(iRowCount, cAvgModuleInfo, cAvgModuleInfo);

            // 排序后的输出
            List<Map.Entry<String, ModuleQualityInfo>> info =
                    new ArrayList<Map.Entry<String, ModuleQualityInfo>>(map_develop.entrySet());
            Collections.sort(info, new Comparator<Map.Entry<String, ModuleQualityInfo>>() {
                public int compare(Map.Entry<String, ModuleQualityInfo> obj1,
                        Map.Entry<String, ModuleQualityInfo> obj2) {
                    // return obj2.getValue() - obj1.getValue();
                    return (int) (obj1.getValue().score - obj2.getValue().score);
                }
            });
            StringBuilder sBuilder = new StringBuilder();
            long minScore = -1;
            for (int j = 0; j < info.size(); j++) {
                iRowCount++;

                ModuleQualityInfo cModuleInfo = info.get(j).getValue();

                strMailText += getModuleInfo(iRowCount, cModuleInfo, cAvgModuleInfo);
                // 最小分比较
                if (-1 == minScore) {
                    minScore = cModuleInfo.score;
                }
                if (minScore == cModuleInfo.score) {
                    sBuilder.append(cModuleInfo.prj_owner).append(";");
                }

            }

            newMailString = newMailString.replaceFirst("#wupf4",
                    AiStringUtil.removeRepeatString(sBuilder.toString(), ";"));
            newMailString = newMailString.replaceFirst("#wupf6", strMailText);
            newMailString = newMailString.replaceFirst("#wupf5", AiDateTimeUtil.getToday());

            LOGGER.info("strCCMail=" + strCCMails);
            AiMail.sendMail("wupf", newMailString, "GBK", SonarRptInfo.GetInstance().getPrjName());
            // AiMail.sendMail(strCCMails, newMailString, "GBK",
            // SonarRptInfo.GetInstance().getPrjName());

        } catch (Exception ex) {
            LOGGER.error(ex);
            ex.printStackTrace();
        }
    }

    /**
     * 
     * @category @author 吴平福
     * @param args update 2016年9月5日
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        if (1 == args.length) {
            SendMailPrjScore cSendMailPrjScore = new SendMailPrjScore(args[0]);
        } else {
            LOGGER.warn("no input parameter");
        }

    }
}
