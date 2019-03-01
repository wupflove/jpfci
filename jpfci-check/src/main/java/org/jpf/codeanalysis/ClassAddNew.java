/** 
* @author 吴平福 
* E-mail:wupf@asiainfo.com 
* @version 创建时间：2017年5月22日 上午10:30:23 
* 类说明 
*/ 

package org.jpf.codeanalysis;

import java.util.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import org.jpf.utils.ios.AiFileUtil;


/**
 * 
 */
public class ClassAddNew extends Thread {

    private static final Logger logger = LogManager.getLogger();


    SvnChangeInfo cSvnChangeInfo;
    String strEndDateTime;
    String UserName;
    String PassWord;
    Vector<String> vChangeMethods;
    int iNumber=0;
    public ClassAddNew(Vector<String> vChangeMethods, SvnChangeInfo cSvnChangeInfo,
             String strEndStartDateTime, String UserName, String PassWord,int iNumber) {
        // TODO Auto-generated constructor stub

        this.vChangeMethods = vChangeMethods;
        this.cSvnChangeInfo = cSvnChangeInfo;
        this.strEndDateTime = strEndStartDateTime;
        this.UserName = UserName;
        this.PassWord = PassWord;
        this.iNumber=iNumber;
    }
    public void run() {
        try {

            String sourceFile1 = CodeAnalysisUtil.getFile(cSvnChangeInfo, strEndDateTime,
                    UserName, PassWord, iNumber,"tmp");
            logger.info(sourceFile1+":"   + AiFileUtil.FileExist(sourceFile1));
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
                    //(<a href='"+RevisionInfo.getSvnFileNewUrl(cSvnChangeInfo.getChangeFileName())+"'>new</a>)
                    vChangeMethods.add("addnew:" + method1.getReturnType2() +" "+strClassName + method1.getName().toString() + " "
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
