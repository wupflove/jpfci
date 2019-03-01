/**
 * @author 吴平福 E-mail:wupf@asiainfo-linkage.com
 * @version 创建时间：2013-4-16 下午1:33:28 类说明
 */

package org.jpf.ci.rpts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.visualwall.WallsDbConn;

import org.jpf.utils.AiDateTimeUtil;
import org.jpf.utils.dbsql.AiDBUtil;
import org.jpf.utils.ios.AiFileUtil;
import org.jpf.utils.mails.AiMail;

/**
 * 
 */
public class SendPUPrjMail {
    private static final Logger LOGGER = LogManager.getLogger();

    private String strCCMails = "";

    /**
     * 
     */
    public SendPUPrjMail(String strConfigFileName) {

        try {
            SonarRptInfo.GetInstance().readRptInfo(strConfigFileName);

            strCCMails = SonarRptInfo.GetInstance().getStrCCMails() + ",";
            LOGGER.debug("strCCMails={}", strCCMails);
            LinkedHashMap<String, ModuleQualityInfo> map_develop = getPrjInfo();

            makeModuleMail(map_develop);
            // SendPrjMail2("wupf", strMailTxtString, " SONAR产品质量周报");
            LOGGER.info("game over");
        } catch (Exception ex) {
            LOGGER.error(ex);
            ex.printStackTrace();
        }
    }



    private String getChangeColor(Double d) {
        String strColor = "<font  size='2'>(" + String.valueOf(d) + "%)</font>";
        if (d > 0) {
            strColor = "<font color='#008000' size='2'>(+" + d + "%)</font>";
        } else if (d < 0) {
            strColor = "<font color='#FF0000'  size='2'>(" + d + "%)</font>";
        }
        return strColor;
    }

    private String getChangeColor(long lValue) {
        String strColor = "<font  size='2'>(" + String.valueOf(lValue) + ")</font>";
        if (lValue > 0) {
            strColor = "<font color='#008000' size='2'>(+" + lValue + ")</font>";
        } else if (lValue < 0) {
            strColor = "<font  color='#FF0000' size='2'>(" + lValue + ")</font>";
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
            strColor = "<font color='#FF0000' size='2'>(+" + lValue + ")</font>";
        } else if (lValue < 0) {
            strColor = "<font  color='#008000' size='2'>(" + lValue + ")</font>";
        }
        return strColor;
    }

    private void getTdStr(StringBuffer sb, long a, long b) {
        if (a > b) {
            sb.append("<font color='#FF0000'>").append(a).append("</font>");
        } else {
            sb.append(a);
        }
    }

    private void getTdStr(StringBuffer sb, String strDate) {

        if (1 == AiDateTimeUtil.compare_date(AiDateTimeUtil.getDay("yyyy-MM-dd", -3), strDate)) {
            sb.append("<font color='#FF0000'>").append(strDate).append("</font>");
        } else {
            sb.append(strDate);
        }
    }

    private void getTdStrReversed(StringBuffer sb, long a, long b) {
        if (a < b) {
            sb.append("<font color='#FF0000'>").append(a).append("</font>");
        } else {
            sb.append(a);
        }
    }

    private void getTdStrReversed(StringBuffer sb, Double a, Double b) {
        if (a > b) {
            sb.append("<font color='#FF0000'>").append(String.format("%.2f", a)).append("%</font>");
        } else {
            sb.append(String.format("%.2f", a)).append("%");
        }

    }

    private void getTdStr(StringBuffer sb, Double a, Double b) {
        if (a < b) {
            sb.append("<font color='#FF0000'>").append(String.format("%.2f", a)).append("%</font>");
        } else {
            sb.append(String.format("%.2f", a)).append("%");
        }

    }

    private void addTdTitle(StringBuffer sb, String strColorString) {
        sb.append(
                "</TD><TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; '>");

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
                .append(" height=22>").append(cModuleInfo.prj_name)
                .append("</TD><TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; ' >")
                .append(cModuleInfo.areaname)
                .append("</TD><TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; '> ")
                .append(cModuleInfo.prj_owner)
                .append("</TD><TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; '> ");
        getTdStr(sb, cModuleInfo.build_date);
        sb.append(
                "</TD><TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; '> ");

        getTdStr(sb, cModuleInfo.blocker_violations, cAvgModuleInfo.blocker_violations);
        sb.append(getChangeUnColor(cModuleInfo.blocker_violationsChange));
        addTdTitle(sb, strColorString);

        getTdStr(sb, cModuleInfo.critical_violations, cAvgModuleInfo.critical_violations);
        sb.append(getChangeUnColor(cModuleInfo.critical_violationsChange));
        addTdTitle(sb, strColorString);

        getTdStr(sb, cModuleInfo.major_violations, cAvgModuleInfo.major_violations);
        sb.append(getChangeUnColor(cModuleInfo.major_violationsChange));
        addTdTitle(sb, strColorString);

        getTdStr(sb, cModuleInfo.PrjLines, cAvgModuleInfo.PrjLines);
        sb.append(getChangeUnColor(cModuleInfo.PrjLinesChange));
        addTdTitle(sb, strColorString);

        if (!cModuleInfo.areaname.equalsIgnoreCase("CRM6.X")) {
            getTdStr(sb, cModuleInfo.dbranch_coverage, cAvgModuleInfo.dbranch_coverage);
            sb.append(getChangeColor(cModuleInfo.dbranch_coverageChange));
        }
        addTdTitle(sb, strColorString);

        if (!cModuleInfo.areaname.equalsIgnoreCase("CRM6.X")) {
            getTdStr(sb, cModuleInfo.dline_coverage, cAvgModuleInfo.dline_coverage);
            sb.append(getChangeColor(cModuleInfo.dline_coverageChange));
        }
        addTdTitle(sb, strColorString);

        if (!cModuleInfo.areaname.equalsIgnoreCase("CRM6.X")) {
            getTdStrReversed(sb, cModuleInfo.ltests, cAvgModuleInfo.ltests);
            sb.append(getChangeColor(cModuleInfo.ltestsChange));
        }
        addTdTitle(sb, strColorString);

        if (!cModuleInfo.areaname.equalsIgnoreCase("CRM6.X")) {
            getTdStr(sb, cModuleInfo.dtest_success_density, cAvgModuleInfo.dtest_success_density);
            sb.append(getChangeColor(cModuleInfo.dtest_success_densityChange));
        }
        addTdTitle(sb, strColorString);

        if (!cModuleInfo.areaname.equalsIgnoreCase("CRM6.X")) {
            getTdStr(sb, cModuleInfo.dUtcaseperline, cAvgModuleInfo.dUtcaseperline);
            sb.append(getChangeColor(cModuleInfo.dUtcaseperlineChange));
        }
        sb.append("</FONT></TD></TR>");

        return sb.toString();
    }

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
                    if (cModuleInfo.areaname.trim().equalsIgnoreCase("HZ")) {
                        cModuleInfo.areaname = "Billing";
                    }
                    if (cModuleInfo.areaname.trim().equalsIgnoreCase("NJ")) {
                        cModuleInfo.areaname = "CRM6.X";
                    }
                    if (cModuleInfo.areaname.trim().equalsIgnoreCase("FZ")) {
                        cModuleInfo.areaname = "O2P";
                    }
                    if (cModuleInfo.areaname.trim().equalsIgnoreCase("BJ")) {
                        cModuleInfo.areaname = "C3";
                    }
                    cModuleInfo.FormatData();
                    map.put(strPrjName, cModuleInfo);
                } else {
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
                    cModuleInfo.dbranch_coverageChange =
                            cModuleInfo.dbranch_coverage - rs.getDouble("branch_coverage") * 100;
                    cModuleInfo.FormatData();
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

    private ModuleQualityInfo setAverage(LinkedHashMap<String, ModuleQualityInfo> map_develop) {
        int iCount = map_develop.size();
        LOGGER.debug("map_develop.size={}", iCount);
        ModuleQualityInfo cAvgModuleInfo = new ModuleQualityInfo();
        cAvgModuleInfo.prj_name = "所有产品平均值";
        cAvgModuleInfo.build_date = AiDateTimeUtil.getCurrDate();
        cAvgModuleInfo.areaname = "";
        cAvgModuleInfo.prj_mail = "";
        cAvgModuleInfo.prj_owner = "";
        long lValue = 0;
        for (Iterator iter_table = map_develop.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();
            ModuleQualityInfo cModuleInfo = map_develop.get(key_table);
            lValue += cModuleInfo.critical_violationsChange;
        }
        cAvgModuleInfo.critical_violationsChange = lValue / iCount;

        lValue = 0;
        for (Iterator iter_table = map_develop.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();
            ModuleQualityInfo cModuleInfo = map_develop.get(key_table);
            lValue += cModuleInfo.critical_violations;
        }
        cAvgModuleInfo.critical_violations = lValue / iCount;

        lValue = 0;
        for (Iterator iter_table = map_develop.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();
            ModuleQualityInfo cModuleInfo = map_develop.get(key_table);
            lValue += cModuleInfo.blocker_violationsChange;
        }
        cAvgModuleInfo.blocker_violationsChange = lValue / iCount;

        lValue = 0;
        for (Iterator iter_table = map_develop.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();
            ModuleQualityInfo cModuleInfo = map_develop.get(key_table);
            lValue += cModuleInfo.blocker_violations;
        }
        cAvgModuleInfo.blocker_violations = lValue / iCount;

        lValue = 0;
        for (Iterator iter_table = map_develop.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();
            ModuleQualityInfo cModuleInfo = map_develop.get(key_table);
            lValue += cModuleInfo.major_violationsChange;
        }
        cAvgModuleInfo.major_violationsChange = lValue / iCount;

        lValue = 0;
        for (Iterator iter_table = map_develop.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();
            ModuleQualityInfo cModuleInfo = map_develop.get(key_table);
            lValue += cModuleInfo.major_violations;
        }
        cAvgModuleInfo.major_violations = lValue / iCount;

        lValue = 0;
        for (Iterator iter_table = map_develop.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();
            ModuleQualityInfo cModuleInfo = map_develop.get(key_table);
            lValue += cModuleInfo.PrjLinesChange;
        }
        cAvgModuleInfo.PrjLinesChange = lValue / iCount;

        lValue = 0;
        for (Iterator iter_table = map_develop.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();
            ModuleQualityInfo cModuleInfo = map_develop.get(key_table);
            lValue += cModuleInfo.PrjLines;
        }
        cAvgModuleInfo.PrjLines = lValue / iCount;

        lValue = 0;
        for (Iterator iter_table = map_develop.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();
            ModuleQualityInfo cModuleInfo = map_develop.get(key_table);
            lValue += cModuleInfo.ltestsChange;
        }
        cAvgModuleInfo.ltestsChange = lValue / iCount;

        lValue = 0;
        for (Iterator iter_table = map_develop.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();
            ModuleQualityInfo cModuleInfo = map_develop.get(key_table);
            lValue += cModuleInfo.ltests;

        }
        cAvgModuleInfo.ltests =
                lValue / (iCount - SonarRptInfo.GetInstance().getCoverageExcludeCount());

        lValue = 0;
        for (Iterator iter_table = map_develop.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();
            ModuleQualityInfo cModuleInfo = map_develop.get(key_table);
            lValue += cModuleInfo.dbranch_coverageChange;
        }
        cAvgModuleInfo.dbranch_coverageChange =
                lValue / (iCount - SonarRptInfo.GetInstance().getCoverageExcludeCount());

        lValue = 0;
        for (Iterator iter_table = map_develop.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();
            ModuleQualityInfo cModuleInfo = map_develop.get(key_table);
            lValue += cModuleInfo.dbranch_coverage;
        }
        cAvgModuleInfo.dbranch_coverage =
                lValue / (iCount - SonarRptInfo.GetInstance().getCoverageExcludeCount());

        lValue = 0;
        for (Iterator iter_table = map_develop.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();
            ModuleQualityInfo cModuleInfo = map_develop.get(key_table);
            lValue += cModuleInfo.dline_coverageChange;
        }
        cAvgModuleInfo.dline_coverageChange =
                lValue / (iCount - SonarRptInfo.GetInstance().getCoverageExcludeCount());

        lValue = 0;
        for (Iterator iter_table = map_develop.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();
            ModuleQualityInfo cModuleInfo = map_develop.get(key_table);
            lValue += cModuleInfo.dline_coverage;
        }
        cAvgModuleInfo.dline_coverage =
                lValue / (iCount - SonarRptInfo.GetInstance().getCoverageExcludeCount());

        lValue = 0;
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
        } ;

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

        lValue = 0;
        for (Iterator iter_table = map_develop.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();
            ModuleQualityInfo cModuleInfo = map_develop.get(key_table);
            lValue += cModuleInfo.dUtcaseperlineChange;
        }
        cAvgModuleInfo.dUtcaseperlineChange =
                lValue / (iCount - SonarRptInfo.GetInstance().getCoverageExcludeCount());

        lValue = 0;
        for (Iterator iter_table = map_develop.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();
            ModuleQualityInfo cModuleInfo = map_develop.get(key_table);
            lValue += cModuleInfo.dUtcaseperline;
        }
        cAvgModuleInfo.dUtcaseperline =
                lValue / (iCount - SonarRptInfo.GetInstance().getCoverageExcludeCount());

        cAvgModuleInfo.FormatData();
        return cAvgModuleInfo;
    }

    private void makeModuleMail(LinkedHashMap<String, ModuleQualityInfo> map_develop)
            throws Exception {
        // 扫描本地目录，从数据库中获取记录


        try {
            ModuleQualityInfo cAvgModuleInfo = setAverage(map_develop);

            // String newMailString = JpfFileUtil.GetFileTxt("grdprj.html");
            String newMailString = AiFileUtil.getFileTxt(SonarRptInfo.GetInstance().getHtmlMail());

            int iRowCount = 0;
            String strMailText = getModuleInfo(iRowCount, cAvgModuleInfo, cAvgModuleInfo);
            for (Iterator iter_table = map_develop.keySet().iterator(); iter_table.hasNext();) {
                iRowCount++;
                String key_table = (String) iter_table.next();
                ModuleQualityInfo cModuleInfo = map_develop.get(key_table);
                strMailText += getModuleInfo(iRowCount, cModuleInfo, cAvgModuleInfo);
            }
            newMailString = newMailString.replaceFirst("#wupf6", strMailText);
            newMailString = newMailString.replaceFirst("#wupf5", AiDateTimeUtil.getToday());

            LOGGER.info("strCCMail=" + strCCMails);
            // JpfMail .SendMail("", newMailString, "GBK",
            // SonarRptInfo.GetInstance().getPrjName()+" SONAR产品质量日报(自动发送)");
            AiMail.sendMail("wupf", newMailString, "GBK",
                    SonarRptInfo.GetInstance().getPrjName() + " SONAR产品质量日报(自动发送)");

            // logger.debug("写入文件完成,共写入行数：" + iCount);
        } catch (Exception ex) {
            LOGGER.error(ex);
            ex.printStackTrace();
        }
    }

    /**
     * @param args 被测试类名：TODO 被测试接口名:TODO 测试场景：TODO 前置参数：TODO 入参： 校验值： 测试备注： update 2013-4-16
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        if (1 == args.length) {
            SendPUPrjMail cSendRrjMail = new SendPUPrjMail(args[0]);
        }

    }
}
