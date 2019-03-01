/**
 * @author 吴平福 E-mail:wupf@asiainfo-linkage.com
 * @version 创建时间：2013-4-16 下午1:33:28 类说明
 */

package org.jpf.ci.rpts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.utils.AiDateTimeUtil;
import org.jpf.utils.dbsql.AiDBUtil;
import org.jpf.utils.ios.AiFileUtil;
import org.jpf.utils.mails.AiMail;


/**
 * @todo: 组织数据，发送给开发人员。
 */
public class SendDevMail {
    private static final Logger logger = LogManager.getLogger();
    private int iType = 0;
    private String strDevHtmlString = "";
    private String strCurrPrjName = "";
    private String strSelectDate = "";

    // 选择的模块
    private String strModuleIds = "";

    /**
     * @category 获取查询的SQL
     * @param strCfgType
     * @return update 2015年8月19日
     */
    public String getQueryString(final String strCfgType) {
        if (strModuleIds.equalsIgnoreCase("")) {
            return "select distinct prj_name,prj_id from jpf_prj   where areaname='" + strCfgType
                    + "' order by prj_name";
        } else {
            return "select distinct prj_name,prj_id from jpf_prj  where  areaname='" + strCfgType
                    + "' and prj_id in(" + strModuleIds + ") order by prj_name";
        }

    }

    /**
     * @category 构造函数
     * @param iType
     * @param strCfgFile
     */
    public SendDevMail(int iType, String strCfgFile) {
        doSendMail(iType, strCfgFile);
    }

    /**
     * @category 构造函数
     * @param iType
     * @param strCfgFile
     * @param _strModuleIds
     */
    public SendDevMail(int iType, String strCfgFile, String _strModuleIds) {
        strModuleIds = _strModuleIds;
        doSendMail(iType, strCfgFile);
    }

    /**
     * 
     * @param iType
     * @param strCfg 被测试类名：TODO 被测试接口名:TODO 测试场景：TODO 前置参数：TODO 入参： 校验值： 测试备注： update 2015年8月29日
     */
    private void doSendMail(int iType, String strCfg) {
        this.iType = iType;
        Connection conn = null;
        try {

            strDevHtmlString = AiFileUtil.getFileTxt("dev.html");
            strSelectDate = AiDateTimeUtil.getDay("yyyy-MM-dd", 0);
            makePrjAvgInfo(strCfg);
            String strSql = this.getQueryString(strCfg);
            logger.info("strSql=" + strSql);
            conn = RptDbConn.GetInstance().GetConn(strCfg);
            PreparedStatement pStmt = conn.prepareStatement(strSql);
            ResultSet rs = pStmt.executeQuery();
            while (rs.next()) {
                strCurrPrjName = rs.getString("prj_name").trim();
                makeModuleMail(strCurrPrjName, rs.getLong("prj_id"), strCfg);
            }
            // SendPrjMail2("wupf", strMailTxtString, " SONAR产品质量周报");

        } catch (Exception ex) {
            logger.error(ex);
            ex.printStackTrace();
        } finally {
            AiDBUtil.doClear(conn);
        }
    }

    class ModuleInfo {
        String strName;
        long lblocker_violations;
        long lcritical_violations;
        long lmajor_violations;
        double dline_coverage;
        double dcoverage;
        double dbranch_coverage;
        double dtest_success_density;
        double ltests;
        long prj_lines;
        int iType = 1;
        double utcaseperline;

        public void setValue(ResultSet rs) throws SQLException {
            if (rs != null) {
                strName = rs.getString("name");
                if (strName != null && strName.equalsIgnoreCase("prj_avg")) {
                    strName = "所有产品平均值";
                }
                lblocker_violations = rs.getInt("blocker_violations");
                lcritical_violations = rs.getInt("critical_violations");
                lmajor_violations = rs.getInt("major_violations");
                dline_coverage = rs.getDouble("line_coverage");
                dcoverage = rs.getDouble("coverage");
                dbranch_coverage = rs.getDouble("branch_coverage");
                ltests = rs.getDouble("tests");
                dtest_success_density = rs.getDouble("test_success_density");
                prj_lines = rs.getLong("prj_lines");
                if (0 != prj_lines) {
                    utcaseperline = (double) ltests * 2000 / prj_lines;
                }
            } else {
                strName = strCurrPrjName;
                lblocker_violations = -1;
                lcritical_violations = -1;
                lmajor_violations = -1;
                dline_coverage = -1;
                dcoverage = -1;
                dbranch_coverage = -1;
                ltests = -1;
                dtest_success_density = -1;
            }
            FormatData();
        }

        public void FormatData() {
            dline_coverage = (double) Math.round(dline_coverage * 10) / 10;
            dcoverage = (double) Math.round(dcoverage * 10) / 10;
            dbranch_coverage = (double) Math.round(dbranch_coverage * 10) / 10;
            dtest_success_density = (double) Math.round(dtest_success_density * 10) / 10;
            utcaseperline = (double) Math.round(utcaseperline * 10) / 10;
            ltests = (double) Math.round(ltests * 10) / 10;
        }
    }

    Vector<ModuleInfo> vModuleInfos = new Vector<ModuleInfo>();

    private void addModuleInfo(ResultSet rs, int iType) throws SQLException {
        ModuleInfo cModuleInfo = new ModuleInfo();
        cModuleInfo.setValue(rs);
        cModuleInfo.iType = iType;
        vModuleInfos.add(cModuleInfo);
    }

    private String getModuleInfo() {
        return getModuleInfo(0);
    }

    private String getModuleInfo(int iType) {
        StringBuffer sb = new StringBuffer();

        logger.debug("vModuleInfos=" + vModuleInfos.size());
        for (int iRowCount = 0; iRowCount < vModuleInfos.size(); iRowCount++) {
            ModuleInfo cModuleInfo = (ModuleInfo) vModuleInfos.get(iRowCount);
            logger.info("cModuleInfo.strName=" + cModuleInfo.strName);
            if (null == cModuleInfo.strName) {
                continue;
            }
            int j = cModuleInfo.strName.lastIndexOf(":");
            if (j > 0) {
                cModuleInfo.strName =
                        cModuleInfo.strName.substring(j, cModuleInfo.strName.length());
                logger.info("cModuleInfo.strName=" + cModuleInfo.strName);

            }


            sb.append(
                    "<TR style='HEIGHT: 16.5pt' height=22><TD class=xl67 style='BORDER-TOP: windowtext; HEIGHT: 16.5pt; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext 0.5pt solid; BACKGROUND-COLOR: ")
                    .append(getColColor(iRowCount)).append("' height=22><FONT face=微软雅黑>")
                    .append(cModuleInfo.strName)
                    .append("</FONT></TD><TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; BACKGROUND-COLOR: ")
                    .append(getColColor(iRowCount)).append("'><FONT face=微软雅黑>");
            if (1 == iType
                    && cModuleInfo.lblocker_violations > cAvgModuleInfo.lblocker_violations) {
                sb.append("<font color='#FF0000'>").append(cModuleInfo.lblocker_violations)
                        .append("</font>");
            } else {
                sb.append(cModuleInfo.lblocker_violations);
            }

            sb.append(
                    "</FONT></TD><TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; BACKGROUND-COLOR: ")
                    .append(getColColor(iRowCount)).append("'><FONT face=微软雅黑>");
            if (1 == iType
                    && cModuleInfo.lcritical_violations > cAvgModuleInfo.lcritical_violations) {
                sb.append("<font color='#FF0000'>").append(cModuleInfo.lcritical_violations)
                        .append("</font>");
            } else {
                sb.append(cModuleInfo.lcritical_violations);
            }
            sb.append(
                    "</FONT></TD><TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; BACKGROUND-COLOR: ")
                    .append(getColColor(iRowCount)).append("'><FONT face=微软雅黑>");
            if (1 == iType && cModuleInfo.lmajor_violations > cAvgModuleInfo.lmajor_violations) {
                sb.append("<font color='#FF0000'>").append(cModuleInfo.lmajor_violations)
                        .append("</font>");
            } else {
                sb.append(cModuleInfo.lmajor_violations);
            }
            sb.append(
                    "</FONT></TD><TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; BACKGROUND-COLOR: ")
                    .append(getColColor(iRowCount)).append("'><FONT face=微软雅黑>");
            if (1 == iType && cModuleInfo.dcoverage < cAvgModuleInfo.dcoverage) {
                sb.append("<font color='#FF0000'>").append(cModuleInfo.dcoverage)
                        .append("%</font>");
            } else {
                sb.append(cModuleInfo.dcoverage).append("%");
            }
            sb.append(
                    "</FONT></TD><TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; BACKGROUND-COLOR: ")
                    .append(getColColor(iRowCount)).append("'><FONT face=微软雅黑>");
            if (1 == iType && cModuleInfo.dline_coverage < cAvgModuleInfo.dline_coverage) {
                sb.append("<font color='#FF0000'>").append(cModuleInfo.dline_coverage)
                        .append("%</font>");
            } else {
                sb.append(cModuleInfo.dline_coverage).append("%");
            }
            sb.append(
                    "</FONT></TD><TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; BACKGROUND-COLOR: ")
                    .append(getColColor(iRowCount)).append("'><FONT face=微软雅黑>");
            if (1 == iType && cModuleInfo.dbranch_coverage < cAvgModuleInfo.dbranch_coverage) {
                sb.append("<font color='#FF0000'>").append(cModuleInfo.dbranch_coverage)
                        .append("%</font>");
            } else {
                sb.append(cModuleInfo.dbranch_coverage).append("%");
            }
            if (1 == iType) {
                sb.append(
                        "</FONT></TD><TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; BACKGROUND-COLOR: ")
                        .append(getColColor(iRowCount)).append("'><FONT face=微软雅黑>");

                addCellLong(iType, cModuleInfo.ltests, cAvgModuleInfo.ltests, sb);
                sb.append(
                        "</FONT></TD><TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; BACKGROUND-COLOR: ")
                        .append(getColColor(iRowCount)).append("'><FONT face=微软雅黑>");

                addCellPercent(iType, cModuleInfo.dtest_success_density,
                        cAvgModuleInfo.dtest_success_density, sb);
            }
            // 代码行数
            sb.append(
                    "</FONT></TD><TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; BACKGROUND-COLOR: ")
                    .append(getColColor(iRowCount)).append("'><FONT face=微软雅黑>");
            if (cModuleInfo.prj_lines > 0) {
                sb.append(cModuleInfo.prj_lines);
            }
            if (1 == iType) {
                // case /代码行数
                sb.append(
                        "</FONT></TD><TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; BACKGROUND-COLOR: ")
                        .append(getColColor(iRowCount)).append("'><FONT face=微软雅黑>");

                addCellPercent(iType, cModuleInfo.utcaseperline, cAvgModuleInfo.utcaseperline, sb);
            }
            sb.append("</FONT></TD></TR>");

        }
        return sb.toString();
    }

    /**
     * @category 增加百分比类型的单元格
     * @param iType
     * @param currentValue
     * @param avgValue
     * @param sb update 2015年8月29日
     */
    private void addCellPercent(int iType, double currentValue, double avgValue, StringBuffer sb) {
        if (currentValue > 0) {
            if (1 == iType && currentValue < avgValue) {
                sb.append("<font color='#FF0000'>").append(currentValue).append("%</font>");
            } else {
                sb.append(currentValue).append("%");
            }
        }
    }

    /**
     * @category 增加整形类型的单元格
     * @param iType
     * @param currentValue
     * @param avgValue
     * @param sb update 2015年8月29日
     */
    private void addCellLong(int iType, double currentValue, double avgValue, StringBuffer sb) {
        if (currentValue > 0) {
            if (1 == iType && currentValue < avgValue) {
                sb.append("<font color='#FF0000'>").append(currentValue).append("</font>");
            } else {
                sb.append(currentValue);
            }
        }
    }

    private void makeModuleMailCxx(String strPrjName, long lPrj_Id, String strCfgFile)
            throws Exception {
        Connection conn = null;
        try {

            String strSql =
                    "select * from projects t3 where scope='DIR'  and INSTR(t3.kee,'[root]')=0 and t3.enabled=1 and root_id="
                            + lPrj_Id + " order by name";
            logger.debug("strSql=" + strSql);
            conn = RptDbConn.GetInstance().GetConn(strCfgFile);
            PreparedStatement pStmt = conn.prepareStatement(strSql);
            ResultSet rs = pStmt.executeQuery();
            String strModuleName = "";
            while (rs.next()) {
                String tmpString = rs.getString("name");
                int i = tmpString.indexOf("/");
                if (i > 0) {
                    tmpString = tmpString.substring(0, i);
                }
                if (strModuleName.equalsIgnoreCase("")) {
                    strModuleName = tmpString;
                    // CalLineCoverage(lPrj_Id, strPrjName, strModuleName);
                    tmpString = rs.getString("kee");
                    i = tmpString.indexOf("/");
                    if (i > 0) {
                        tmpString = tmpString.substring(0, i);
                    }
                    calTestCoverage(lPrj_Id, tmpString, strCfgFile);
                } else {
                    if (!tmpString.equalsIgnoreCase(strModuleName)) {
                        strModuleName = tmpString;
                        // CalLineCoverage(lPrj_Id, strPrjName, strModuleName);
                        tmpString = rs.getString("kee");
                        i = tmpString.indexOf("/");
                        if (i > 0) {
                            tmpString = tmpString.substring(0, i);
                        }
                        calTestCoverage(lPrj_Id, tmpString, strCfgFile);
                    }
                }
                // CalLineCoverage(rs.getLong("prj_id"),
                // rs.getString("prj_name"), rs.getString("module_name"));
            }

            rs.close();
            pStmt.close();
        } catch (Exception ex) {
            logger.error(ex);
            ex.printStackTrace();
        } finally {
            AiDBUtil.doClear(conn);
        }
    }

    private void calTestCoverage(Long lPrjId, String strKee, String strCfgFile) {
        Connection conn = null;
        try {

            String strSql =
                    "select max(case t1.metric_id when 41 then t1.value  end)uncovered_lines,"
                            + " max(case t1.metric_id when 39 then t1.value  end)lines_to_cover,"
                            + "max(case t1.metric_id when 48 then t1.value  end)uncovered_conditions,"
                            + "max(case t1.metric_id when 50 then t1.value  end)branch_coverage,"
                            + "max(case t1.metric_id when 80 then t1.value  end)blocker_violations,"
                            + "max(case t1.metric_id when 81 then t1.value  end)critical_violations,"
                            + "max(case t1.metric_id when 82 then t1.value  end)major_violations,"
                            + "max(case t1.metric_id when 37 then t1.value  end)coverage,"
                            + "max(case t1.metric_id when 1 then t1.value  end)module_lines,"
                            + "t3.kee,t2.scope,t2.build_date,t3.id from project_measures  t1,snapshots t2,projects t3"
                            + " where t1.metric_id in (1,37,39,41,48,50,80,81,82) and t1.snapshot_id=t2.id and t2.islast=1"
                            + " and t2.project_id=t3.id and t2.scope='DIR' and rule_id is null and t2.root_project_id="
                            + lPrjId
                            + " and INSTR(t3.kee,'[root]')=0 and t3.enabled=1 and (instr(t3.kee,'"
                            + strKee + "/')>0 or RIGHT(t3.kee," + (strKee.length()) + ")='" + strKee
                            + "') " + " and RIGHT(t3.kee,5)<>'/test' group by t3.kee";
            logger.info("strSql=" + strSql);
            conn = RptDbConn.GetInstance().GetConn(strCfgFile);
            // conn.setAutoCommit(false);
            PreparedStatement pStmt = conn.prepareStatement(strSql);

            ResultSet rs = pStmt.executeQuery();

            double uncovered_lines = 0;
            double lines_to_cover = 0;
            double uncovered_conditions = 0;
            double branch_coverage = 0;

            ModuleInfo cModuleInfo = new ModuleInfo();
            cModuleInfo.strName = strKee;

            while (rs.next()) {

                // CalLineCoverage(rs.getLong("prj_id"),rs.getString("prj_name"),rs.getString("module_name"));
                uncovered_lines += rs.getDouble("uncovered_lines");
                lines_to_cover += rs.getDouble("lines_to_cover");
                uncovered_conditions += rs.getDouble("uncovered_conditions");
                if (rs.getDouble("branch_coverage") > 0) {
                    // System.out.println(rs.getDouble("uncovered_conditions")*100
                    // / rs.getDouble("branch_coverage"));
                    branch_coverage += rs.getDouble("uncovered_conditions") * 100
                            / rs.getDouble("branch_coverage") - 1;
                }
                cModuleInfo.lblocker_violations += rs.getDouble("blocker_violations");
                cModuleInfo.lcritical_violations += rs.getDouble("critical_violations");
                cModuleInfo.lmajor_violations += rs.getDouble("major_violations");
                cModuleInfo.dcoverage = rs.getDouble("coverage");
                cModuleInfo.prj_lines += rs.getLong("module_lines");

            } // conn.commit();
            rs.close();
            pStmt.close();
            // logger.info("uncovered_lines="+uncovered_lines);
            // logger.info("lines_to_cover="+lines_to_cover);
            // System.out.println(uncovered_lines/lines_to_cover);
            if (lines_to_cover > 0) {
                logger.info("lines_coverage=" + (1 - uncovered_lines / lines_to_cover) * 100 + "%");
                cModuleInfo.dline_coverage = (1 - uncovered_lines / lines_to_cover) * 100;
            } else {
                logger.info("lines_coverage=0%");
                cModuleInfo.dline_coverage = 0;
            }
            // logger.info("uncovered_conditions=" + uncovered_conditions);
            // logger.info("branch_coverage=" + branch_coverage);
            if (branch_coverage > 0) {
                logger.debug(
                        "branch_coverage=" + uncovered_conditions * 100 / branch_coverage + "%");
                cModuleInfo.dbranch_coverage = uncovered_conditions * 100 / branch_coverage;
            } else {
                logger.debug("branch_coverage=0%");
                cModuleInfo.dbranch_coverage = 0;
            }
            cModuleInfo.FormatData();
            vModuleInfos.add(cModuleInfo);

        } catch (Exception ex) {
            logger.error(ex);
            ex.printStackTrace();
        } finally {
            AiDBUtil.doClear(conn);
        }
    }

    private void calTestCoverage(Long lPrjId, String strPrjName, String strModuleName,
            String strCfgFile) {
        Connection conn = null;
        try {

            String strSql =
                    "select max(case t1.metric_id when 41 then t1.value  end)uncovered_lines,"
                            + " max(case t1.metric_id when 39 then t1.value  end)lines_to_cover,"
                            + "max(case t1.metric_id when 48 then t1.value  end)uncovered_conditions,"
                            + "max(case t1.metric_id when 50 then t1.value  end)branch_coverage,"
                            + "max(case t1.metric_id when 80 then t1.value  end)blocker_violations,"
                            + "max(case t1.metric_id when 81 then t1.value  end)critical_violations,"
                            + "max(case t1.metric_id when 82 then t1.value  end)major_violations,"
                            + "t3.kee,t2.scope,t2.build_date,t3.id from project_measures  t1,snapshots t2,projects t3"
                            + " where t1.metric_id in (39,41,48,50,80,81,82) and t1.snapshot_id=t2.id and t2.islast=1"
                            + " and t2.project_id=t3.id and t2.scope='DIR' and rule_id is null and t2.root_project_id="
                            + lPrjId
                            + " and INSTR(t3.kee,'[root]')=0 and t3.enabled=1 and (instr(t3.kee,'"
                            + strPrjName + ":" + strModuleName + "/')>0 or RIGHT(t3.kee,"
                            + (strPrjName.length() + 1 + strModuleName.length()) + ")='"
                            + strPrjName + ":" + strModuleName + "') "
                            + " and RIGHT(t3.kee,5)<>'/test' group by t3.kee";
            logger.info("strSql=" + strSql);
            conn = RptDbConn.GetInstance().GetConn(strCfgFile);
            conn.setAutoCommit(false);
            PreparedStatement pStmt = conn.prepareStatement(strSql);

            ResultSet rs = pStmt.executeQuery();

            double uncovered_lines = 0;
            double lines_to_cover = 0;
            double uncovered_conditions = 0;
            double branch_coverage = 0;

            ModuleInfo cModuleInfo = new ModuleInfo();
            cModuleInfo.strName = strModuleName;

            while (rs.next()) {

                // CalLineCoverage(rs.getLong("prj_id"),rs.getString("prj_name"),rs.getString("module_name"));
                uncovered_lines += rs.getDouble("uncovered_lines");
                lines_to_cover += rs.getDouble("lines_to_cover");
                uncovered_conditions += rs.getDouble("uncovered_conditions");
                if (rs.getDouble("branch_coverage") > 0) {
                    // System.out.println(rs.getDouble("uncovered_conditions")*100
                    // / rs.getDouble("branch_coverage"));
                    branch_coverage += rs.getDouble("uncovered_conditions") * 100
                            / rs.getDouble("branch_coverage") - 1;
                }
                cModuleInfo.lblocker_violations += rs.getDouble("blocker_violations");
                cModuleInfo.lcritical_violations += rs.getDouble("critical_violations");
                cModuleInfo.lmajor_violations += rs.getDouble("major_violations");
            } // conn.commit();
            rs.close();
            pStmt.close();
            // logger.info("uncovered_lines="+uncovered_lines);
            // logger.info("lines_to_cover="+lines_to_cover);
            // System.out.println(uncovered_lines/lines_to_cover);
            if (lines_to_cover > 0) {
                logger.info("lines_coverage=" + (1 - uncovered_lines / lines_to_cover) * 100 + "%");
                cModuleInfo.dline_coverage = (1 - uncovered_lines / lines_to_cover) * 100;
            } else {
                logger.info("lines_coverage=0%");
                cModuleInfo.dline_coverage = 0;
            }
            // logger.info("uncovered_conditions=" + uncovered_conditions);
            // logger.info("branch_coverage=" + branch_coverage);
            if (branch_coverage > 0) {
                logger.info(
                        "branch_coverage=" + uncovered_conditions * 100 / branch_coverage + "%");
                cModuleInfo.dbranch_coverage = uncovered_conditions * 100 / branch_coverage;
            } else {
                logger.info("branch_coverage=0%");
                cModuleInfo.dbranch_coverage = 0;
            }

            vModuleInfos.add(cModuleInfo);

        } catch (Exception ex) {
            logger.error(ex);
            ex.printStackTrace();
        } finally {
            AiDBUtil.doClear(conn);
        }
    }

    /**
     * 
     * @param strPrjName
     * @return
     * @throws Exception 被测试类名：TODO 被测试接口名:TODO 测试场景：TODO 前置参数：TODO 入参： 校验值： 测试备注： update 2013-5-31
     */
    private void makeModuleMailJava(String strPrjName, long lPrj_Id, String strCfg)
            throws Exception {
        // 扫描本地目录，从数据库中获取记录
        Connection conn = null;
        try {
            strPrjName = strPrjName.trim();
            logger.info("java prj_name=" + strPrjName);
            String strSql =
                    "select count(*) mycount from projects t1 where t1.scope='PRJ' and root_id="
                            + lPrj_Id;
            logger.info("strSql=" + strSql);

            // String strCondString = "t1.root_id=" +
            // lPrj_Id+" and t2.depth=1 ";
            String strCondString = "t1.root_id=" + lPrj_Id;
            conn = RptDbConn.GetInstance().GetConn(strCfg);
            PreparedStatement pStmt = conn.prepareStatement(strSql);
            ResultSet rs = pStmt.executeQuery();
            if (rs.next()) {
                if (0 == rs.getInt("mycount")) {
                    strCondString = "t1.id=" + lPrj_Id;
                }
            }

            strSql = "select t1.id,t1.name,t2.id,DATE_FORMAT(t2.build_date,'%Y-%m-%d') build_date"
                    + ",max(case t3.metric_id when 80 then t3.value  end)blocker_violations"
                    + ",max(case t3.metric_id when 81 then t3.value  end)critical_violations"
                    + ",max(case t3.metric_id when 82 then t3.value  end)major_violations"
                    + ",max(case t3.metric_id when 43 then t3.value  end)line_coverage"
                    + ",max(case t3.metric_id when 37 then t3.value  end)coverage"
                    + ",max(case t3.metric_id when 50 then t3.value  end)branch_coverage"
                    + ",max(case t3.metric_id when 30 then t3.value  end)tests"
                    + ",max(case t3.metric_id when 35 then t3.value  end)test_success_density"
                    + ",max(case t3.metric_id when 1 then t3.value  end)prj_lines"
                    + " from projects t1,snapshots t2,project_measures t3"
                    + " where t1.scope='PRJ' and " + strCondString
                    + " and t1.enabled=1  and t1.id=t2.project_id 	 and t2.islast=1  and t2.id=t3.snapshot_id and t3.metric_id in (1,37,43,30,80,81,82,35,50)"
                    + "  and DATE_FORMAT(t2.build_date,'%Y-%m-%d')='" + strSelectDate + "'"
                    + "  group by t1.id  order by t1.name";

            logger.info("strSql=" + strSql);

            pStmt = conn.prepareStatement(strSql);
            rs = pStmt.executeQuery();

            while (rs.next()) {

                addModuleInfo(rs, 1);
            }

            // logger.debug("写入文件完成,共写入行数：" + iCount);
        } catch (Exception ex) {
            logger.error(ex);
            ex.printStackTrace();

        } finally {
            AiDBUtil.doClear(conn);
        }
    }

    private void makePrjAvgInfo(String strCfgFile) throws Exception {
        // 扫描本地目录，从数据库中获取记录
        Connection conn = null;
        try {

            conn = RptDbConn.GetInstance().GetConn(strCfgFile);

            String strSql =
                    "select t1.id,t1.name,t2.id,DATE_FORMAT(t2.build_date,'%Y-%m-%d') build_date"
                            + ",max(case t3.metric_id when 80 then t3.value  end)blocker_violations"
                            + ",max(case t3.metric_id when 81 then t3.value  end)critical_violations"
                            + ",max(case t3.metric_id when 82 then t3.value  end)major_violations"
                            + ",max(case t3.metric_id when 43 then t3.value  end)line_coverage"
                            + ",max(case t3.metric_id when 37 then t3.value  end)coverage"
                            + ",max(case t3.metric_id when 50 then t3.value  end)branch_coverage"
                            + ",max(case t3.metric_id when 30 then t3.value  end)tests"
                            + ",max(case t3.metric_id when 35 then t3.value  end)test_success_density,max(case t3.metric_id when 1 then t3.value  end)prj_lines"
                            + " from projects t1,snapshots t2,project_measures t3"
                            + " where t1.scope='PRJ' and t1.id ="
                            + RptDevConst.getPRJ_AVG_ID(strCfgFile)
                            + " and t1.enabled=1  and t1.id=t2.project_id 	 and t2.islast=1  "
                            + " and t2.id=t3.snapshot_id 	 and t3.metric_id in ("
                            + RptDevConst.getPRJ_AVG_METRIC(strCfgFile) + ")"
                            // + " and DATE_FORMAT(t2.build_date,'%Y-%m-%d')='" + strSelectDate +
                            // "'"
                            + "  group by t1.id  order by t1.name";

            logger.info("strSql=" + strSql);

            PreparedStatement pStmt = conn.prepareStatement(strSql);
            ResultSet rs = pStmt.executeQuery();

            while (rs.next()) {

                cAvgModuleInfo.setValue(rs);
            }

            // logger.debug("写入文件完成,共写入行数：" + iCount);
        } catch (Exception ex) {
            logger.error(ex);
            ex.printStackTrace();

        } finally {
            AiDBUtil.doClear(conn);
        }
    }

    ModuleInfo cAvgModuleInfo = new ModuleInfo();

    private void makePrjMail(String strPrjName, long lPrj_Id, String strCfg) throws Exception {
        // 扫描本地目录，从数据库中获取记录
        Connection conn = null;
        try {
            strPrjName = strPrjName.trim();
            logger.info("get prj_name=" + strPrjName);
            conn = RptDbConn.GetInstance().GetConn(strCfg);

            String strSql =
                    "select t1.id,t1.name,t2.id,DATE_FORMAT(t2.build_date,'%Y-%m-%d') build_date"
                            + ",max(case t3.metric_id when 80 then t3.value  end)blocker_violations"
                            + ",max(case t3.metric_id when 81 then t3.value  end)critical_violations"
                            + ",max(case t3.metric_id when 82 then t3.value  end)major_violations"
                            + ",max(case t3.metric_id when 37 then t3.value  end)coverage"
                            + ",max(case t3.metric_id when 43 then t3.value  end)line_coverage"
                            + ",max(case t3.metric_id when 50 then t3.value  end)branch_coverage"
                            + ",max(case t3.metric_id when 30 then t3.value  end)tests"
                            + ",max(case t3.metric_id when 35 then t3.value  end)test_success_density"
                            + ",max(case t3.metric_id when 1 then t3.value  end)prj_lines"
                            + " from projects t1,snapshots t2,project_measures t3"
                            + " where t1.scope='PRJ' and t1.id=" + lPrj_Id
                            + " and t1.enabled=1  and t1.id=t2.project_id 	 and t2.islast=1  and t2.id=t3.snapshot_id 	 and t3.metric_id in (1,37,30,43,80,81,82,35,50)"
                            // + " and DATE_FORMAT(t2.build_date,'%Y-%m-%d')='" +strSelectDate + "'"
                            + "  group by t1.id  order by t1.name";

            logger.info("strSql=" + strSql);

            PreparedStatement pStmt = conn.prepareStatement(strSql);
            ResultSet rs = pStmt.executeQuery();

            if (rs.next()) {
                addModuleInfo(rs, 0);
            } else {
                addModuleInfo(null, 0);
            }

            // logger.debug("写入文件完成,共写入行数：" + iCount);
        } catch (Exception ex) {
            logger.error(ex);
            ex.printStackTrace();

        } finally {
            AiDBUtil.doClear(conn);
        }
    }

    private void makeModuleMail(String strPrjName, long inPrj_id, String strCfg) throws Exception {
        // 扫描本地目录，从数据库中获取记录
        Connection conn = null;
        String strMailCC = "";
        String strLanuage = "";
        long lPrjId = 0;
        try {
            strPrjName = strPrjName.trim();
            logger.info("prj_name=" + strPrjName);

            String strSql =
                    "select t1.prj_mail,t1.leader_mail,t2.language,t2.id from jpf_prj t1,projects t2 where t1.prj_id=t2.id and t2.id="
                            + inPrj_id;
            logger.info("strSql=" + strSql);
            conn = RptDbConn.GetInstance().GetConn(strCfg);
            PreparedStatement pStmt = conn.prepareStatement(strSql);
            ResultSet rs = pStmt.executeQuery();

            rs = pStmt.executeQuery(strSql);

            if (rs.next()) {
                strMailCC += rs.getString("leader_mail") + ",";
                strLanuage = rs.getString("language");
                lPrjId = rs.getLong("id");
            }

            strSql = "select login ,sum(blocker_violations)blocker_violations"
                    + ",sum(critical_violations)critical_violations"
                    + ",sum(major_violations)major_violations"
                    + "  from (select distinct * from jpf_file_user2  where prj_name='" + strPrjName
                    + "' and  DATE_FORMAT(state_date,'%Y-%m-%d')='" + strSelectDate + "'"
                    + " and login is not null and "
                    + " (blocker_violations>0 or critical_violations>0 or major_violations>0))t1 "
                    + " group by login";

            logger.info("strSql=" + strSql);

            rs = pStmt.executeQuery(strSql);

            String mailTextString = strDevHtmlString;
            StringBuffer sb = new StringBuffer();
            sb.setLength(0);
            String newMailString = "";
            int iRowCount = 0;

            while (rs.next()) {
                strMailCC += rs.getString("login") + ",";
                sb.append(
                        "<TR style='HEIGHT: 16.5pt' height=22><TD class=xl67 style='BORDER-TOP: windowtext; HEIGHT: 16.5pt; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext 0.5pt solid; BACKGROUND-COLOR: ")
                        .append(getColColor(iRowCount)).append("' height=22><FONT face=微软雅黑>")
                        .append(rs.getString("login"))
                        .append("</FONT></TD><TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; BACKGROUND-COLOR: ")
                        .append(getColColor(iRowCount)).append("'><FONT face=微软雅黑>")
                        .append(rs.getInt("blocker_violations"))
                        .append("</FONT></TD><TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; BACKGROUND-COLOR: ")
                        .append(getColColor(iRowCount)).append("'><FONT face=微软雅黑>").append("")
                        .append("</FONT></TD><TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; BACKGROUND-COLOR: ")
                        .append(getColColor(iRowCount)).append("'><FONT face=微软雅黑>")
                        .append(rs.getInt("critical_violations"))
                        .append("</FONT></TD><TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; BACKGROUND-COLOR: ")
                        .append(getColColor(iRowCount)).append("'><FONT face=微软雅黑>").append("")
                        .append("</FONT></TD><TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; BACKGROUND-COLOR: ")
                        .append(getColColor(iRowCount)).append("'><FONT face=微软雅黑>")
                        .append(rs.getInt("major_violations"))
                        .append("</FONT></TD><TD class=xl70 style='BORDER-TOP: windowtext; BORDER-RIGHT: windowtext 0.5pt solid; BORDER-BOTTOM: windowtext 0.5pt solid; BORDER-LEFT: windowtext; BACKGROUND-COLOR: ")
                        .append(getColColor(iRowCount)).append("'><FONT face=微软雅黑>").append("")
                        .append("</FONT></TD></TR>");
                iRowCount++;
            }
            // System.out.println(sb.toString());
            /*
             * if (0 == iRowCount) { return; }
             */
            newMailString = mailTextString.replaceFirst("#wupf1", sb.toString());
            newMailString = newMailString.replaceFirst("#wupf2", String.valueOf(iRowCount));
            strSql = "select login,kee,sum(value1)value1,t2.description from	("
                    + " select prj_name as prj_name,build_date as build_date,state_date as state_date,login as login,kee as kee, major_violations as value1,'82' as metric_id from jpf_file_user2"
                    + " union select  prj_name as prj_name,build_date as build_date,state_date as state_date, login as login,kee as kee, critical_violations as value1,'81' as metric_id from jpf_file_user2"
                    + " union select  prj_name as prj_name ,build_date as build_date,state_date as state_date,login as login,kee as kee, blocker_violations as value1,'80' as metric_id from jpf_file_user2"
                    + " )t1,metrics t2 where value1 is not null and t1.metric_id=t2.id and t1.metric_id in (80,81,82)"
                    + "  and login is not null and prj_name='" + strPrjName + "'"
                    + " and DATE_FORMAT(state_date,'%Y-%m-%d')='" + strSelectDate + "'"
                    + " group by login,kee,t2.description order by login";

            logger.info("strSql=" + strSql);
            rs = pStmt.executeQuery(strSql);
            iRowCount = 0;
            sb.setLength(0);
            while (rs.next()) {

                sb.append(
                        "<TR style='HEIGHT: 16.5pt' height=22><TD class=xl66 style='border:0.5pt solid windowtext; HEIGHT: 16.5pt; WIDTH: 91px; BACKGROUND-COLOR: ")
                        .append(getColColor(iRowCount)).append("' height=22><FONT face=微软雅黑>")
                        .append(rs.getString("login"))
                        .append("</font></TD><TD class=xl66 style='BORDER-TOP: 0.5pt solid windowtext; BORDER-RIGHT: 0.5pt solid windowtext; BORDER-BOTTOM: 0.5pt solid windowtext; BORDER-LEFT: medium none windowtext; WIDTH: 75px; BACKGROUND-COLOR: ")
                        .append(getColColor(iRowCount)).append("'><FONT face=微软雅黑>")
                        .append(rs.getInt("value1"))
                        .append("</font></TD><TD class=xl66 style='BORDER-TOP: 0.5pt solid windowtext; BORDER-RIGHT: 0.5pt solid windowtext; BORDER-BOTTOM: 0.5pt solid windowtext; BORDER-LEFT: medium none windowtext; WIDTH: 86px; BACKGROUND-COLOR: ")
                        .append(getColColor(iRowCount)).append("'><FONT face=微软雅黑>")
                        .append(rs.getString("description"))
                        .append("</font></TD><TD class=xl66 style='BORDER-TOP: 0.5pt solid windowtext; BORDER-RIGHT: 0.5pt solid windowtext; BORDER-BOTTOM: 0.5pt solid windowtext; BORDER-LEFT: medium none windowtext; BACKGROUND-COLOR: ")
                        .append(getColColor(iRowCount))
                        .append("' width=332><FONT face=微软雅黑><a href='")
                        .append(RptDevConst.getSonarUrl(strCfg, inPrj_id, strPrjName,
                                rs.getString("kee")))
                        .append("'>").append(rs.getString("kee")).append("</a></font></TD></TR>");
                iRowCount++;
            }
            rs.close();
            pStmt.close();

            newMailString = newMailString.replaceFirst("#wupf3", sb.toString());
            newMailString = newMailString.replaceFirst("#wupf4", String.valueOf(iRowCount));
            // for prj
            vModuleInfos.clear();
            makePrjMail(strPrjName, lPrjId, strCfg);
            vModuleInfos.add(cAvgModuleInfo);
            newMailString = newMailString.replaceFirst("#wupf6", getModuleInfo(1));

            // for module
            vModuleInfos.clear();
            if (strLanuage.equalsIgnoreCase("c++")) {
                makeModuleMailCxx(strPrjName, lPrjId, strCfg);
                newMailString = newMailString.replaceFirst("#wupf5", getModuleInfo());
            }
            if (strLanuage.equalsIgnoreCase("java")) {
                makeModuleMailJava(strPrjName, lPrjId, strCfg);
                newMailString = newMailString.replaceFirst("#wupf5", getModuleInfo());
            }

            logger.info("strMailCC=" + strMailCC);
            if (0 == iType) {
                AiMail.sendMail("", newMailString, "GBK", strPrjName + " --SONAR个人质量日报(自动发出)");
            }
            if (1 == iType) {
                AiMail.sendMail(strMailCC, newMailString, "GBK", strPrjName + " --SONAR质量日报(自动发出)");
            }
            // logger.debug("写入文件完成,共写入行数：" + iCount);
        } catch (Exception ex) {
            logger.error(ex);
            ex.printStackTrace();
        } finally {
            AiDBUtil.doClear(conn);
        }
    }

    private String getColColor(int iRowCount) {
        if (iRowCount % 2 == 0) {
            return "transparent";
        } else {
            return " #f2f2f2";
        }
    }

    /**
     * @param args 被测试类名：TODO 被测试接口名:TODO 测试场景：TODO 前置参数：TODO 入参： 校验值： 测试备注： update 2013-4-16
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        int i = 0;
        if (2 == args.length) {
            i = Integer.parseInt(args[0]);
            SendDevMail cSendDevMail = new SendDevMail(i, args[1]);
        }
        if (3 == args.length) {
            i = Integer.parseInt(args[0]);
            SendDevMail cSendDevMail = new SendDevMail(i, args[1], args[2]);
        }
        logger.info("game over");
    }

}
