/**
 * @author 吴平福 E-mail:421722623@qq.com
 * @version 创建时间：2017年5月22日 上午10:54:38 类说明
 */

package org.jpf.codeanalysis;

import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;


/**
 * 
 */
public class ClassDelete extends Thread {

    private static final Logger logger = LogManager.getLogger();


    SvnChangeInfo cSvnChangeInfo;
    String strStartDateTime;
    String UserName;
    String PassWord;
    Vector<String> vChangeMethods;
    int iNumber = 0;

    public ClassDelete(Vector<String> vChangeMethods, SvnChangeInfo cSvnChangeInfo,
            String strStartDateTime, String UserName, String PassWord, int iNumber) {
        // TODO Auto-generated constructor stub

        this.vChangeMethods = vChangeMethods;
        this.cSvnChangeInfo = cSvnChangeInfo;
        this.strStartDateTime = strStartDateTime;
        this.UserName = UserName;
        this.PassWord = PassWord;
        this.iNumber = iNumber;
    }


    public void run() {
        try {

            String sourceFile1 = CodeAnalysisUtil.getFile(cSvnChangeInfo, strStartDateTime,
                    UserName, PassWord, iNumber,"tmp");
            logger.info(sourceFile1);
            CompilationUnit cCompilationUnit1 = RunCompilationUnit.getCompilationUnit(sourceFile1);

            TypeDeclaration typeDec1 = (TypeDeclaration) cCompilationUnit1.types().get(0);
            String strClassName =
                    cCompilationUnit1.getPackage().getName() + "." + typeDec1.getName();
            logger.debug("className1:" + strClassName);


            // show methods
            MethodDeclaration[] methodDec1 = typeDec1.getMethods();

            // 查找增加的方法
            for (MethodDeclaration method1 : methodDec1) {

                logger.debug("class1:" + method1.getName().toString() + " "
                        + method1.parameters().toString());
                // (<a
                // href='"+RevisionInfo.getSvnFileOldUrl(cSvnChangeInfo.getChangeFileName())+"'>old</a>)
                vChangeMethods.add("delete:" + strClassName + method1.getName().toString() + " "
                        + method1.parameters().toString());

            }

        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        } finally {
        }
        logger.info("finish:"+this.getName());
    }
}
