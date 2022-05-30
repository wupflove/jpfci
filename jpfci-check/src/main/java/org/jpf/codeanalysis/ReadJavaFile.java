/**
 * @author 吴平福 E-mail:421722623@qq.com
 * @version 创建时间：2017年5月11日 下午9:07:48 类说明
 */

package org.jpf.codeanalysis;


import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;


/**
 * 
 */
public class ReadJavaFile {


    /**
     * 
     */
    public ReadJavaFile() {
        // TODO Auto-generated constructor stub
        try {
            String sourceString =
                    "D:\\workspace\\CodeAnalysis\\src\\org\\jpf\\codeanalysis\\hierarchys\\actions\\AbstractFindClassesAction.java";
            sourceString = CodeAnalysisUtil.getFileTxt(sourceString);
            ASTParser astParser = ASTParser.newParser(AST.JLS4);
            astParser.setKind(ASTParser.K_COMPILATION_UNIT);
            astParser.setResolveBindings(true);

            astParser.setSource(sourceString.toCharArray());
            CompilationUnit node = (CompilationUnit) astParser.createAST(null);
            // show import declarations in order

            
            System.out.println("Package:" + node.getPackage().getName());
            List importList = node.imports();
            System.out.println("import:");
            for (Object obj : importList) {
                ImportDeclaration importDec = (ImportDeclaration) obj;
                System.out.println(importDec.getName());
            }
            
            // show class name
            List types = node.types();
            if (types.size() == 0) {
                return;
            }
            TypeDeclaration typeDec = (TypeDeclaration) types.get(0);
            System.out.println("className:" + typeDec.getName());
            System.out.println("is interface:" + typeDec.isInterface());
            List interfacelist = typeDec.superInterfaceTypes();
            for (Object obj : interfacelist) {
                Type cType = (Type) obj;
                System.out.println("implements:" + cType);
            }
            System.out.println("superclass:" + typeDec.getSuperclassType());


            // show fields
            FieldDeclaration fieldDec[] = typeDec.getFields();
            System.out.println("Fields:");
            for (FieldDeclaration field : fieldDec) {
                System.out.println("Field fragment:" + field.fragments());
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

                isEmptyMethod(method);
            }
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        }
    }

    /**
     * @category @author 吴平福
     * @param args update 2017年4月2日
     */

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        System.out.println("begin ...");
        ReadJavaFile cwupftest = new ReadJavaFile();
        System.out.println("game over");
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
