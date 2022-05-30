/**
 * @author 吴平福 E-mail:421722623@qq.com
 * @version 创建时间：2017年7月6日 下午5:37:24 类说明
 */

package org.jpf.codeanalysis;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.jpf.utils.ios.AiFileUtil;

/**
 * 
 */
public class ReadInterface2H2 {
    private static final Logger logger = LogManager.getLogger();

    // 接口文件数
    int iInterfaceFileCount = 0;
    // 接口方法数
    int iInterfaceMethodCount = 0;
    // 有注释的接口方法数
    int iInterfaceMethodDescCount = 0;

    /**
     * 
     */
    public ReadInterface2H2(String strInputFilePath, String strFileInclude) {
        // TODO Auto-generated constructor stub
        try {
            strFileInclude = strFileInclude.trim();
            logger.info("Load files from " + strInputFilePath);
            Vector<String> vFiles = new Vector<String>();
            AiFileUtil.getFiles(strInputFilePath, vFiles, ".java");
            List<String[]> mList = new LinkedList<String[]>();
            for (int i = 0; i < vFiles.size(); i++) {
                logger.info("current file " + vFiles.get(i).trim());
                if (vFiles.get(i).trim().endsWith(strFileInclude)) {
                    // if (vFiles.get(i).indexOf("IBO") == -1) {
                    ReadJavaFile(vFiles.get(i));

                }

            }
            logger.info("java file count:" + vFiles.size());
            logger.info("interface file count:" + iInterfaceFileCount);
            logger.info("interface method count:" + iInterfaceMethodCount);
            logger.info("interface method with desc  count:" + iInterfaceMethodDescCount);


        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        }
    }

    /**
     * 
     */
    public void ReadJavaFile(String sourceString) {
        // TODO Auto-generated constructor stub
        Connection conn = null;
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection("jdbc:h2:~/aicat/bin/db", "yue", "yue");
            // add application code here
            String strSql =
                    "insert into BUSINESS_INTEFACE(BNAME,MODULE_NAME,FULLY_QUALIFIED_NAME) VALUES(?,?,?)";
            String strSql2 =
                    "delete from  BUSINESS_INTEFACE where FULLY_QUALIFIED_NAME=?";
            PreparedStatement pstmt = conn.prepareStatement(strSql);
            PreparedStatement pstmt2 = conn.prepareStatement(strSql2);
            // sourceString =
            // "D:\\jworkspaces\\jpfapp\\src\\org\\jpf\\ci\\checks\\interfaceCheck.java";
            sourceString = CodeAnalysisUtil.getFileTxt(sourceString, "GBK");
            ASTParser astParser = ASTParser.newParser(AST.JLS4);
            astParser.setKind(ASTParser.K_COMPILATION_UNIT);
            astParser.setResolveBindings(true);

            astParser.setSource(sourceString.toCharArray());
            CompilationUnit node = (CompilationUnit) astParser.createAST(null);

            // show class name
            List types = node.types();
            if (types.size() == 0) {
                return;
            }
            TypeDeclaration typeDec = (TypeDeclaration) types.get(0);

            iInterfaceFileCount++;

            System.out.println("className:" + typeDec.getName());

            // show methods
            MethodDeclaration methodDec[] = typeDec.getMethods();

            // System.out.println("Method:");
            for (MethodDeclaration method : methodDec) {
                iInterfaceMethodCount++;
                // get method name

                // String strMethodName = node.getPackage().getName() + "." +
                // typeDec.getName()+method.getName();
                String strMethodName = "";
                // get method parameters
                List param = method.parameters();
                if (param.toString() != null) {
                    String abc = param.toString().substring(1, param.toString().length() - 1);
                    String[] params = abc.split(",");
                    for (int i = 0; i < params.length; i++) {
                        String[] def = params[i].trim().split(" ");
                        strMethodName += def[0] + ",";
                    }
                    if (strMethodName.endsWith(",")) {
                        strMethodName = strMethodName.substring(0, strMethodName.length() - 1);
                    }
                    strMethodName = "(" + strMethodName + ")";
                }
                strMethodName = node.getPackage().getName() + "." + typeDec.getName()
                        + method.getName() + strMethodName;
                // System.out.println(method.getJavadoc());
                if (method.getJavadoc() != null) {
                    pstmt2.setString(1, strMethodName);
                    pstmt2.executeUpdate();
                    pstmt.setString(1, method.getJavadoc().toString().trim());
                    pstmt.setString(2, "wupf add");
                    pstmt.setString(3, strMethodName);
                    pstmt.executeUpdate();
                    conn.commit();
                    iInterfaceMethodDescCount++;
                }



            }
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex2) {
                // TODO: handle exception
            }
        }

    }

    /**
     * @category @author 吴平福
     * @param args update 2017年7月6日
     */

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        long start = System.currentTimeMillis();

        if (args.length == 2) {
            ReadInterface2H2 cReadInterface = new ReadInterface2H2(args[0], args[1]);

        } else {
            logger.info("error input param");
        }
        logger.info("ExcuteTime " + (System.currentTimeMillis() - start) + "ms");
    }

}
