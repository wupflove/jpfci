/**
 * @author 吴平福 E-mail:421722623@qq.com
 * @version 创建时间：2017年5月22日 上午10:30:23 类说明
 */

package org.jpf.codeanalysis;

import java.util.List;
import java.util.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;

import org.jpf.utils.ios.AiFileUtil;

/**
 * 
 */
public class ClassImport extends Thread {

    private static final Logger logger = LogManager.getLogger();


    Vector<String> vImports;
    String sourceFile = "";

    public ClassImport(Vector<String> vImports, String sourceFile) {
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

            // TypeDeclaration typeDec1 = (TypeDeclaration) cCompilationUnit.types().get(0);
            List importList = cCompilationUnit.imports();
            logger.debug("import:");
            for (Object obj : importList) {
                ImportDeclaration importDec = (ImportDeclaration) obj;
                if (importDec != null && importDec.getName() != null) {
                    vImports.add(importDec.getName().toString());
                }
            }
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
            logger.error(sourceFile);
        } finally {
        }
        logger.info("finish:" + this.getName());
    }
}
