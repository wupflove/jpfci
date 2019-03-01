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
public class ClassModify extends Thread {

    private static final Logger logger = LogManager.getLogger();


    public ClassModify(Vector<String> vChangeMethods, SvnChangeInfo cSvnChangeInfo,
            String strStartDateTime, String strEndStartDateTime, String UserName, String PassWord,int iNumber) {
        // TODO Auto-generated constructor stub

        this.vChangeMethods = vChangeMethods;
        this.cSvnChangeInfo = cSvnChangeInfo;
        this.strStartDateTime = strStartDateTime;
        this.strEndDateTime = strEndStartDateTime;
        this.UserName = UserName;
        this.PassWord = PassWord;
        this.iNumber=iNumber;

    }

    Vector<String> vChangeMethods;
    String sourceFile1;
    String sourceFile2;

    SvnChangeInfo cSvnChangeInfo;
    String strStartDateTime;
    String strEndDateTime;
    String UserName;
    String PassWord;

    int iNumber=0;
    
    @Override
    public void run() {
        try {

            logger.info(this.getClass().toString()+iNumber+" begin");
            //logger.info("Thread.activeCount()= " + Thread.activeCount());
            String sourceFile1 = CodeAnalysisUtil.getFile(cSvnChangeInfo, strStartDateTime,
                    UserName, PassWord, iNumber,"tmp");
            logger.info(sourceFile1);
            //logger.info("Thread.activeCount()= " + Thread.activeCount());
            String sourceFile2 = CodeAnalysisUtil.getFile(cSvnChangeInfo, strEndDateTime,
                    UserName, PassWord, iNumber,"tmp2");
            logger.info(sourceFile2);

            CompilationUnit cCompilationUnit1 = RunCompilationUnit.getCompilationUnit(sourceFile1);
            CompilationUnit cCompilationUnit2 = RunCompilationUnit.getCompilationUnit(sourceFile2);
            
            //ENUM类，无法在这里分析
            if (cCompilationUnit1==null)
            {
                return;
            }
            if (cCompilationUnit2==null)
            {
                return;
            }
            //logger.debug(cCompilationUnit1);
            //logger.debug(cCompilationUnit1.types());
            //logger.debug(cCompilationUnit1.getNodeType());
            //logger.debug(cCompilationUnit1.getPackage());
            TypeDeclaration typeDec1 = (TypeDeclaration) cCompilationUnit1.types().get(0);
            String strClassName =
                    cCompilationUnit1.getPackage().getName() + "." + typeDec1.getName();
            logger.debug("className1:" + strClassName);

            TypeDeclaration typeDec2 = (TypeDeclaration) cCompilationUnit2.types().get(0);
            logger.debug("className2:" + cCompilationUnit2.getPackage().getName() + "."
                    + typeDec2.getName());

            // show methods
            MethodDeclaration[] methodDec1 = typeDec1.getMethods();
            MethodDeclaration[] methodDec2 = typeDec2.getMethods();

            // 查找删除的方法
            for (MethodDeclaration method1 : methodDec1) {
                boolean isFind = false;
                String strA = method1.getName().toString();
                for (MethodDeclaration method2 : methodDec2) {

                    if (strA.equalsIgnoreCase(method2.getName().toString())) {
                        isFind = true;
                        break;
                    }
                }
                if (!isFind) {
                    logger.debug("class1:" + method1.getName().toString() + " "
                            + method1.parameters().toString());
                    vChangeMethods.add("delete:" + strClassName + method1.getName().toString() + " "
                            + method1.parameters().toString());
                }
            }
            // 查找变化的方法和新增加的方法
            for (MethodDeclaration method2 : methodDec2) {
                boolean isFindMethodWithBody = false;
                boolean isFindMethod = false;
                String strMethodName = method2.getName().toString();
                String strMethodWithBody = method2.getName().toString()
                        + method2.parameters().toString() + method2.getBody();
                for (MethodDeclaration method1 : methodDec1) {
                    if (strMethodName.equalsIgnoreCase(method1.getName().toString())) {
                        isFindMethod = true;
                    }
                    if (strMethodWithBody.equalsIgnoreCase(method1.getName().toString()
                            + method1.parameters().toString() + method1.getBody())) {
                        isFindMethodWithBody = true;
                        break;
                    }
                }

                if (!isFindMethod) {
                    /*(<a href='"
                    + RevisionInfo.getSvnFileOldUrl(cSvnChangeInfo.getChangeFileName())
                    + "'>old</a>,<a href='"
                    + RevisionInfo.getSvnFileNewUrl(cSvnChangeInfo.getChangeFileName())
                    + "'>new</a>)"
                    */
                    vChangeMethods.add("addnew:" + strClassName + method2.getName().toString() + " "
                            + method2.parameters().toString());
                } else if (!isFindMethodWithBody) {

                    /*
                     :(<a href='"
                            + RevisionInfo.getSvnFileOldUrl(cSvnChangeInfo.getChangeFileName())
                            + "'>old</a>,<a href='"
                            + RevisionInfo.getSvnFileNewUrl(cSvnChangeInfo.getChangeFileName())
                            + "'>new</a>)
                     */
                    vChangeMethods.add("change:" + strClassName + method2.getName().toString() + " "
                            + method2.parameters().toString());
                    //logger.debug("method2.getBody().getLength():"+method2.getBody().getLength());
                    //logger.debug("method2.getBody().getLeadingComment():"+method2.getJavadoc());
                    //logger.debug("method2.getStartPosition():"+method2.getStartPosition());

                }
            }
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        } finally {
        }
        logger.info("finish:"+this.getName());
    }



}

