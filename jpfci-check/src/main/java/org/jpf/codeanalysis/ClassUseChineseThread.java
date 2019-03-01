/**
 * @author 吴平福 E-mail:wupf@asiainfo.com
 * @version 创建时间：2017年5月22日 上午10:30:23 类说明
 */

package org.jpf.codeanalysis;

import java.util.List;
import java.util.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import org.jpf.utils.AiStringUtil;
import org.jpf.utils.ios.AiFileUtil;


/**
 * 
 */
public class ClassUseChineseThread extends Thread {

    private static final Logger logger = LogManager.getLogger();


    Vector<String> vImports;
    String sourceFile = "";

    public ClassUseChineseThread(Vector<String> vImports, String sourceFile) {
        // TODO Auto-generated constructor stub
        this.sourceFile = sourceFile;
        this.vImports = vImports;
    }

    public void run() {
        try {

            if (!AiFileUtil.FileExist(sourceFile)) {
                logger.info(sourceFile + ": not exist");
                return;
            }
            CompilationUnit cCompilationUnit = RunCompilationUnit.getCompilationUnit(sourceFile);
            if (cCompilationUnit.types().isEmpty())
            {
                return;
            }
            TypeDeclaration typeDec = (TypeDeclaration) cCompilationUnit.types().get(0);

            // show methods
            MethodDeclaration methodDec[] = typeDec.getMethods();

            //System.out.println("Method:");
            for (MethodDeclaration method : methodDec) {
                // get method name
                SimpleName methodName = method.getName();
                //System.out.println("method name:" + methodName);

                // get method parameters
                List param = method.parameters();
                //System.out.println("method parameters:" + param);

                // get method return type
                Type returnType = method.getReturnType2();
                //System.out.println("method return type:" + returnType);
                
                //System.out.println("method body:" + method.getBody());
                if (null!=method.getBody())
                {
                    if (AiStringUtil.isChinese(method.getBody().toString()))
                    {
                        System.out.println(sourceFile+":"+method.getName());
                        
                        
                    }
                }

              }
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
            logger.error(sourceFile);
        } finally {
        }
        logger.debug("finish:" + this.getName());
    }

}
