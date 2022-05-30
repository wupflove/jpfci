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
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import org.jpf.utils.ios.AiFileUtil;


/**
 * 
 */
public abstract class ClassFindKey extends Thread {

    private static final Logger logger = LogManager.getLogger();


    Vector<String> vImports;
    String sourceFile = "";
    
    /**
     * 1:import 2:classname 3:supername 4: interface 5:methods 6:methodbody 
     * @category 
     * @author 吴平福 
     * @return
     * update 2017年8月2日
     */
    public abstract int getKey();
    
    public abstract boolean getValue();
    
    public ClassFindKey(Vector<String> vImports, String sourceFile) {
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

            TypeDeclaration typeDec = (TypeDeclaration) cCompilationUnit.types().get(0);
            // show fields
            FieldDeclaration fieldDec[] = typeDec.getFields();
            System.out.println("Fields:");
            for (FieldDeclaration field : fieldDec) {
                System.out.println("Field fragment:" + field.fragments());  //check point
                System.out.println("Field type:" + field.getType());
            }

            // show methods
            MethodDeclaration methodDec[] = typeDec.getMethods();

            System.out.println("Method:");
            for (MethodDeclaration method : methodDec) {
                // get method name
                SimpleName methodName = method.getName();
                System.out.println("method name:" + methodName);

                // get method parameters
                List param = method.parameters();
                System.out.println("method parameters:" + param);

                // get method return type
                Type returnType = method.getReturnType2();
                System.out.println("method return type:" + returnType);
                
                System.out.println("method body:" + method.getBody());
                
                isEmptyMethod(method);
              }
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
            logger.error(sourceFile);
        } finally {
        }
        logger.info("finish:" + this.getName());
    }
    private boolean isEmptyMethod(MethodDeclaration method) {
        // System.out.println(method.getBody());
        if (method.getBody() == null) {
            return false;
        }
        String strMethodBody = method.getBody().toString().trim();
        System.out.println(strMethodBody);
        // strMethodBody.substring(1, strMethodBody.length()-1);
        if (method.getReturnType2() == null) {
            if (!method.parameters().isEmpty()) {
                return true;
            } else {
                return false;
            }
        }
        System.out.println("method.parameters().isEmpty()=" + method.parameters().isEmpty());
        if (strMethodBody.trim().equalsIgnoreCase("{\n}")) {
            System.out.println("method empty is true");
            return true;
        } else {
            return false;
        }

    }
}
