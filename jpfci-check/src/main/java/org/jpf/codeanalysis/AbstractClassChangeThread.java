package org.jpf.codeanalysis;

import java.io.File;
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
public abstract class AbstractClassChangeThread extends Thread {

    private static final Logger logger = LogManager.getLogger();

    /**
     * 
     */
    public AbstractClassChangeThread(Vector<String> vChangeMethods, String sourceFile1, String sourceFile2) {
        // TODO Auto-generated constructor stub

        this.vChangeMethods = vChangeMethods;
        this.sourceFile1 = sourceFile1;
        this.sourceFile2 = sourceFile2;
    }

    public AbstractClassChangeThread(Vector<String> vChangeMethods, SvnChangeInfo cSvnChangeInfo,
            String strStartDateTime, String strEndStartDateTime, String UserName, String PassWord) {
        // TODO Auto-generated constructor stub

        this.vChangeMethods = vChangeMethods;
        this.cSvnChangeInfo = cSvnChangeInfo;
        this.strStartDateTime = strStartDateTime;
        this.strEndStartDateTime = strEndStartDateTime;
        this.UserName = UserName;
        this.PassWord = PassWord;

    }

    Vector<String> vChangeMethods;
    String sourceFile1;
    String sourceFile2;

    SvnChangeInfo cSvnChangeInfo;
    String strStartDateTime;
    String strEndStartDateTime;
    String UserName;
    String PassWord;

    protected CompilationUnit getCompilationUnit(String strDateTime) throws Exception{
        String strCmd = "cd " + AiFileUtil.getCurrentPath() + ";mdir -p tmp;cd tmp;svn cat "
                + cSvnChangeInfo.getChangeFileName() + " -r {\"" + strDateTime
                + "\"} --username " + UserName + " --password " + PassWord + " >"
                + AiFileUtil.getFileName(cSvnChangeInfo.getChangeFileName());
        RunCmd.runBashCmd(strCmd);

        sourceFile1 = AiFileUtil.getCurrentPath() + File.separator + "tmp" + File.separator
                + AiFileUtil.getFileName(cSvnChangeInfo.getChangeFileName());
 
        logger.info(sourceFile1);

        return RunCompilationUnit.getCompilationUnit(sourceFile1);
    }

    public void run() {
        try {
            // String sourceFile1 =
            // "D:\\workspace\\CodeAnalysis\\src\\org\\jpf\\codeanalysis\\hierarchys\\actions\\AbstractFindClassesAction.java";

            // String sourceFile2 =
            // "D:\\workspace\\CodeAnalysis\\src\\org\\jpf\\codeanalysis\\hierarchys\\actions\\AbstractFindClassesAction2.java";
            /*
            String strCmd = "cd " + AiFileUtil.getCurrentPath() + ";mdir -p tmp;cd tmp;svn cat "
                    + cSvnChangeInfo.getChangeFileName() + " -r {\"" + strStartDateTime
                    + "\"} --username " + UserName + " --password " + PassWord + " >"
                    + AiFileUtil.getFileName(cSvnChangeInfo.getChangeFileName());
            RunCmd.runBashCmd(strCmd);
            strCmd = "cd " + AiFileUtil.getCurrentPath() + ";mdir -p tmp2;cd tmp2;svn cat "
                    + cSvnChangeInfo.getChangeFileName() + " -r {\"" + strEndStartDateTime
                    + "\"} --username " + UserName + " --password " + PassWord + " >"
                    + AiFileUtil.getFileName(cSvnChangeInfo.getChangeFileName());
            RunCmd.runBashCmd(strCmd);
            sourceFile1 = AiFileUtil.getCurrentPath() + File.separator + "tmp" + File.separator
                    + AiFileUtil.getFileName(cSvnChangeInfo.getChangeFileName());
            sourceFile2 = AiFileUtil.getCurrentPath() + File.separator + "tmp2" + File.separator
                    + AiFileUtil.getFileName(cSvnChangeInfo.getChangeFileName());

            logger.info(sourceFile1);
            logger.info(sourceFile2);
            */
            CompilationUnit cCompilationUnit1 = getCompilationUnit(strStartDateTime);
            CompilationUnit cCompilationUnit2 =getCompilationUnit(strEndStartDateTime);

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
                    logger.debug("class2:" + method2.getName().toString() + " "
                            + method2.parameters().toString());
                    vChangeMethods.add("addnew:(<a href='"
                            + RevisionInfo.getSvnFileOldUrl(cSvnChangeInfo.getChangeFileName())
                            + "'>old</a>,<a href='"
                            + RevisionInfo.getSvnFileNewUrl(cSvnChangeInfo.getChangeFileName())
                            + "'>new</a>)" + strClassName + method2.getName().toString() + " "
                            + method2.parameters().toString());
                } else if (!isFindMethodWithBody) {
                    logger.debug("class2:" + method2.getName().toString() + " "
                            + method2.parameters().toString());
                    vChangeMethods.add("change:(<a href='"
                            + RevisionInfo.getSvnFileOldUrl(cSvnChangeInfo.getChangeFileName())
                            + "'>old</a>,<a href='"
                            + RevisionInfo.getSvnFileNewUrl(cSvnChangeInfo.getChangeFileName())
                            + "'>new</a>)" + strClassName + method2.getName().toString() + " "
                            + method2.parameters().toString());
                }
            }
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        } finally {
        }

    }



}

