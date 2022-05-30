/**
 * @author 吴平福 E-mail:421722623@qq.com
 * @version 创建时间：2015年5月2日 下午2:31:28 类说明
 */

package org.jpf.ci.checks;

import java.util.regex.Pattern;

/**
 * 
 */
public class RegTest extends AbstractCheck {

    /**
     * 
     */
    public RegTest() {}


    /*
     * 
     */
    public static void main(String[] args) {
        // args = new String[]{"c:\\temp\\source\\balance"};
        // TODO Auto-generated method stub
        if (args.length == 1) {
            RegTest cRegTest = new RegTest();
            cRegTest.DoCheck(args[0]);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jpf.ci.checks.AbstractCheck#GetFileType()
     */
    @Override
    public String GetFileType() {
        // TODO Auto-generated method stub
        return ".java;.cpp;.h;.c;.cxx";
    }

    private static Pattern IP_P = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    private static Pattern LOCLAHOST_P = Pattern.compile("localhost:");
    private static Pattern EJB_P = Pattern.compile("(.*[^a-zA-Z_])?ejb:/.*");
    private static Pattern SCHEMA_P = Pattern.compile(
            "(?!.*import.*)(.*[^a-zA-Z_])?(([aAbBcCiIjJpPrRsStTuUwWzZ][dD])|([fF][cCJj]))\\.[a-zA-Z]+_.*");
    private static Pattern JDBC_P = Pattern.compile(
            "(.*[^a-zA-Z_])?jdbc:((oracle:thin:)|(jdbc:odbc:)|(microsoft:sqlserver:)|(db2:)|(sybase:)|(mysql:)).*");
    private static Pattern CJK_P = Pattern.compile("[\u4e00-\u9fa5]");
    private static Pattern CPrintf =
            Pattern.compile(".*[ \t]((printf)|(cout)|(cerr)|(System.out))");
    private static Pattern SQL_KEY=Pattern.compile("!exec\\(\\)|(\\.)exec\\(|exec[ ]|select |select/|select\\(|select\\*|^(.)insert |^(.)insert/|^(.)insert\\(|^(.)insert\\*|^(.)update |^(.)update/|^(.)update\\(|^(.)update\\*|^(.)delete |^(.)delete/|^(.)delete\\(|^(.)delete\\*|^(.)truncate |^(.)truncate/|^(.)truncate\\(|^(.)truncate\\*|^(.)drop |^(.)drop/|^(.)drop\\(|^(.)drop\\*");

    long lIP = 0;
    long lLocalHost = 0;
    long lEjb = 0;
    long lSchema = 0;
    long lJdbc = 0;
    long lCjk = 0;
    long lPrintf = 0;
    long lSql_Error=0;



    /*
     * (non-Javadoc)
     * 
     * @see org.jpf.ci.checks.AbstractCheck#DoCheckRule(java.lang.String, java.lang.String, long)
     */
    @Override
    public void DoCheckRule(String strLine, String strFileName, long lLineNumber) {
        // System.out.println(lLineNumber+":"+strLine);
       
        if (IP_P.matcher(strLine).find()) {
            lIP++;
            System.out.println("Violations1--" + strFileName + ":" + lLineNumber + "  " + strLine);
            sBuffer.append("<error file=\"").append(GetOppoFileName(strFileName))
                    .append("\" line=\"").append(lLineNumber)
                    .append("\" id=\"bss_check_rule\" severity=\"error\" msg=\"[bss custom rule]:ip address should not appear\" data=\""
                            + strLine + "\"/>");
        }
        /*
        if (LOCLAHOST_P.matcher(strLine).find()) {
            lLocalHost++;
            System.out.println("Violations2--" + strFileName + ":" + lLineNumber + "  " + strLine);
            sBuffer.append("<error file=\"").append(GetOppoFileName(strFileName))
                    .append("\" line=\"").append(lLineNumber)
                    .append("\" id=\"bss_check_rule\" severity=\"error\" msg=\"[bss custom rule]:ip address should not appear\" data=\""+ strLine + "\"/>");
        }

        // EJB
        if (EJB_P.matcher(strLine).matches()) {
            lEjb++;
            System.out.println("Violations3--" + strFileName + ":" + lLineNumber + "  " + strLine);
        }

        // System.out.println(SCHEMA_P.matcher("select * from AD.CA_QUEUE").matches());
        // System.out.println(SCHEMA_P.matcher("select * from \"cD.CA_QUEUE").matches());
        // System.out.println(SCHEMA_P.matcher("select * from cd.CA_QUEUE").matches());
        
         * if(SCHEMA_P.matcher(strLine).matches()){ lSchema++;
         * System.out.println("Violations4(数据库scheme名)--"+strFileName + ":" + lLineNumber + "  " +
         * strLine);
         * sBuffer.append("<error file=\"").append(GetOppoFileName(strFileName)).append("\" line=\""
         * ).append(lLineNumber).
         * append("\" id=\"bss_check_rule\" severity=\"error\" msg=\"[bss custom rule]:scheme should not appear\"/>"
         * ); }
         */
        /*
        if (JDBC_P.matcher(strLine).matches()) {
            lJdbc++;
            System.out.println("Violations5--" + strFileName + ":" + lLineNumber + "  " + strLine);
        }
    
        if (CJK_P.matcher(strLine).find()) {
            lCjk++;
            System.out.println("Violations6--" + strFileName + ":" + lLineNumber + "  " + strLine);
            sBuffer.append("<error file=\"").append(GetOppoFileName(strFileName))
                    .append("\" line=\"").append(lLineNumber)
                    .append("\" id=\"bss_check_rule\" severity=\"error\" msg=\"[bss custom rule]:chinese word should not appear\"/>");
        }
        if (CPrintf.matcher(strLine).find()) {
            lPrintf++;
            System.out.println("Violations7--" + strFileName + ":" + lLineNumber + "  " + strLine);
            sBuffer.append("<error file=\"").append(GetOppoFileName(strFileName))
                    .append("\" line=\"").append(lLineNumber)
                    .append("\" id=\"bss_check_rule\" severity=\"error\" msg=\"[bss custom rule]:printf/cout/cerr/system.out should not appear\"/>");
        }
    */
    	/*
        if (SQL_KEY.matcher(strLine).find()) {
            lSql_Error++;
            System.out.println("Violations8--" + strFileName + ":" + lLineNumber + "  " + strLine);
            sBuffer.append("<error file=\"").append(GetOppoFileName(strFileName))
                    .append("\" line=\"").append(lLineNumber)
                    .append("\" id=\"bss_check_rule\" severity=\"error\" msg=\"[bss custom rule]:select/update/insert/update/delete/truncate/exec/drop  should not appear\"/>");
        }
        */
    }


    /* (non-Javadoc)
     * @see org.jpf.ci.checks.AbstractCheck#doResult()
     */
    @Override
    protected void doResult() {
        // TODO Auto-generated method stub
        System.out.println("lIP:" + lIP);
        System.out.println("lLocalHost:" + lLocalHost);
        System.out.println("lEjb:" + lEjb);
        System.out.println("lSchema:" + lSchema);
        System.out.println("lJdbc:" + lJdbc);
        System.out.println("lCjk:" + lCjk);
        System.out.println("lPrintf:" + lPrintf);
        System.out.println("lSql_Error:" + lSql_Error);
        
    }

}

