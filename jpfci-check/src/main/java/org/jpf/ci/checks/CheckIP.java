/**
 * @author 吴平福 E-mail:421722623@qq.com
 * @version 创建时间：2015年5月2日 下午2:31:28 类说明
 */

package org.jpf.ci.checks;

import java.util.regex.Pattern;

/**
 * 
 */
public class CheckIP {

    /**
     * 
     */
    public CheckIP() {

    }

    private static Pattern JDBC_P = Pattern.compile("!exec\\(\\)|(\\.)exec\\(|exec[ ]");

    /*
     * 
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String strLine = "public void exec(){";
        if (JDBC_P.matcher(strLine).find()) {

            System.out.println("1");
        }
        strLine = "Runtime.getRuntime().exec(\"/ecapp/soft/ECShellScript/checklog.sh\");";
        if (JDBC_P.matcher(strLine).find()) {

            System.out.println("2");
        }
        strLine = "exec(\"/ecapp/soft/ECShellScript/checklog.sh\");";
        if (JDBC_P.matcher(strLine).find()) {

            System.out.println("3");
        }
        strLine = "exec (\"/ecapp/soft/ECShellScript/checklog.sh\");";
        if (JDBC_P.matcher(strLine).find()) {

            System.out.println("4");
        }
        
        strLine = ".executeClass";
        if (JDBC_P.matcher(strLine).find()) {

            System.out.println("5");
        }
        System.out.println("over");
    }

}
