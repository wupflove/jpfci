/** 
* @author 吴平福 
* E-mail:421722623@qq.com 
* @version 创建时间：2017年8月30日 下午5:11:17 
* 类说明 
*/ 

package org.jpf.codeanalysis;

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * 
 */
public class ReadJavaNote {

    /**
     * 
     */
    public ReadJavaNote() {
        // TODO Auto-generated constructor stub
        try {
            String sourceString =
                    "E:\\svn\\src\\main\\java\\com\\crm\\soa\\ams\\function\\AmBalanceFunction.java";
            sourceString = CodeAnalysisUtil.getFileTxt(sourceString);
            ASTParser astParser = ASTParser.newParser(AST.JLS4);
            astParser.setKind(ASTParser.K_COMPILATION_UNIT);
            astParser.setResolveBindings(true);

            astParser.setSource(sourceString.toCharArray());
            CompilationUnit node = (CompilationUnit) astParser.createAST(null);
            // show import declarations in order

            
            //System.out.println("Package:" + node.getPackage().getName());
            
            // show class name
            List types = node.types();
            if (types.size() == 0) {
                return;
            }
            TypeDeclaration typeDec = (TypeDeclaration) types.get(0);
            System.out.println("className:" + node.getPackage().getName()+"."+typeDec.getName());
            //System.out.println("is interface:" + typeDec.isInterface());
            List interfacelist = typeDec.superInterfaceTypes();
            for (Object obj : interfacelist) {
                Type cType = (Type) obj;
                //System.out.println("implements:" + cType);
            }
            //System.out.println("superclass:" + typeDec.getSuperclassType());


            // show fields
            FieldDeclaration fieldDec[] = typeDec.getFields();
            System.out.println("Fields:");
            for (FieldDeclaration field : fieldDec) {
                System.out.println("Field fragment:" + field.fragments());
                System.out.println("Field type:" + field.getType());
                System.out.println("Field note:" +field.getJavadoc());
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
                System.out.println("method note: "+method.getJavadoc() ) ;
                if (null!=method.getJavadoc())
                {
                    List<TagElement> methodRemark = method.getJavadoc().tags();
                    System.out.println(methodRemark.size());
                    String remarkContent = "";
                    for(TagElement element:methodRemark)
                    {
                     // 获取注释种类
                        remarkContent = "类型:" + element.getTagName();
                     // 获取注释条目的类型
                        remarkContent += "\t名称：" + (element.fragments().size() > 0 ? element.fragments().get(0) : null);
                        // 获取注释条目中的注释
                        if (element.fragments().size() > 1) {
                            remarkContent += "\t注释：" + element.fragments().get(1);
                           }
                           //System.out.println(remarkContent);
                    }

                }
                //isEmptyMethod(method);
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
        ReadJavaNote cwupftest = new ReadJavaNote();
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
