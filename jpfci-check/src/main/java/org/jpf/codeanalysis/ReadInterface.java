/**
 * @author 吴平福 E-mail:421722623@qq.com
 * @version 创建时间：2017年7月6日 下午5:37:24 类说明
 */

package org.jpf.codeanalysis;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import org.jpf.utils.excelutils.WriteExcel;
import org.jpf.utils.ios.AiFileUtil;

/**
 * 
 */
public class ReadInterface {
    private static final Logger logger = LogManager.getLogger();

    //接口文件数
    int iInterfaceFileCount = 0;
    //接口方法数
    int iInterfaceMethodCount=0;
    //有注释的接口方法数
    int iInterfaceMethodDescCount=0;
    /**
     * 
     */
    public ReadInterface(String strInputFilePath,String strEmail,String strFileInclude) {
        // TODO Auto-generated constructor stub
        try {
            strFileInclude=strFileInclude.trim();
            logger.info("Load files from "+strInputFilePath);
            Vector<String> vFiles = new Vector<String>();
            AiFileUtil.getFiles(strInputFilePath, vFiles, ".java");
            List<String[]> mList = new LinkedList<String[]>();
            for (int i = 0; i < vFiles.size(); i++) {
                logger.info("current file "+vFiles.get(i).trim());
               if (vFiles.get(i).trim().endsWith(strFileInclude)){
                //if (vFiles.get(i).indexOf("IBO") == -1) {
                    List<String[]> list = ReadJavaFile(vFiles.get(i));
                    if (list != null) {
                        for (int j = 0; j < list.size(); j++) {
                            mList.add(list.get(j));
                        }

                        list.clear();
                    }
                }

            }
            logger.info("java file count:" + vFiles.size());
            logger.info("interface file count:" + iInterfaceFileCount);
            logger.info("interface method count:" + iInterfaceMethodCount);
            logger.info("interface method with desc  count:" + iInterfaceMethodDescCount);
            WriteExcel.writeExcel(mList, strInputFilePath+File.separator+"abc.xls");

        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        }
    }

    /**
     * 
     */
    public List<String[]> ReadJavaFile(String sourceString) {
        // TODO Auto-generated constructor stub
        List<String[]> lists = new ArrayList<String[]>();
        try {

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
                return null;
            }
            TypeDeclaration typeDec = (TypeDeclaration) types.get(0);
            /*
            if (!typeDec.isInterface()) {
                return null;
            }
            */
            iInterfaceFileCount++;


            // System.out.println("Package:" + node.getPackage().getName());
            System.out.println("className:" + typeDec.getName());
            // System.out.println("is interface:" + typeDec.isInterface());

            List interfacelist = typeDec.superInterfaceTypes();
            for (Object obj : interfacelist) {
                Type cType = (Type) obj;
                // System.out.println("implements:" + cType);
            }
            System.out.println("superclass:" + typeDec.getSuperclassType());


            // show fields
            FieldDeclaration fieldDec[] = typeDec.getFields();
            // System.out.println("Fields:");
            for (FieldDeclaration field : fieldDec) {
                System.out.print("Field fragment:" + field.fragments());
                System.out.println(" Field type:" + field.getType());
            }

            // show methods
            MethodDeclaration methodDec[] = typeDec.getMethods();

            // System.out.println("Method:");
            for (MethodDeclaration method : methodDec) {
                iInterfaceMethodCount++;
                // get method name
                String[] strArray = new String[6];
                strArray[0] = node.getPackage().getName() + "." + typeDec.getName();
                strArray[1] = "superclass:" + typeDec.getSuperclassType();

                SimpleName methodName = method.getName();
                System.out.println("method name:" + methodName);
                strArray[2] = methodName.toString();

                // get method parameters
                List param = method.parameters();
                strArray[1] =param.toString();
                // System.out.println("method parameters:" + param);
                strArray[3]="";
                if ( param.toString()!=null)
                {
                    String abc=param.toString().substring(1, param.toString().length()-1);
                    String[] params=abc.split(",");
                    for(int i=0;i<params.length;i++)
                    {
                        String[] def=params[i].trim().split(" ");
                        strArray[3]+=def[0]+",";
                    }
                    if (strArray[3].endsWith(","))
                    {
                        strArray[3]=strArray[3].substring(0, strArray[3].length()-1);
                    }
                    strArray[3]="("+strArray[3]+")";
                }
                //strArray[3] = param.toString();
                // get method return type
                Type returnType = method.getReturnType2();
                // System.out.println("method return type:" + returnType);
                if (returnType != null) {
                    strArray[4] = returnType.toString();
                } else {
                    strArray[4] = "";
                }
                // System.out.println(method.getJavadoc());
                if (method.getJavadoc() != null) {
                    strArray[5] = method.getJavadoc().toString();
                    iInterfaceMethodDescCount++;
                }

                lists.add(strArray);

            }
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        }
        return lists;
    }

    /**
     * @category @author 吴平福
     * @param args update 2017年7月6日
     */

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        long start = System.currentTimeMillis();

        if (args.length==3)
        {
            ReadInterface cReadInterface = new ReadInterface(args[0],args[1],args[2]);
                
        }else
        {
            logger.info("error input param");
        }
        logger.info("ExcuteTime " + (System.currentTimeMillis() - start) + "ms");
    }
    
    public void ReadJavaFile(String sourceString,String strMethodName) {
        // TODO Auto-generated constructor stub
        List<String[]> lists = new ArrayList<String[]>();
        try {

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
                return ;
            }
            TypeDeclaration typeDec = (TypeDeclaration) types.get(0);

            // System.out.println("Package:" + node.getPackage().getName());
            System.out.println("className:" + typeDec.getName());
            // System.out.println("is interface:" + typeDec.isInterface());

            // show methods
            MethodDeclaration methodDec[] = typeDec.getMethods();

            // System.out.println("Method:");
            for (MethodDeclaration method : methodDec) {
                // get method name
                System.out.println("full className:" +node.getPackage().getName() + "." + typeDec.getName());
   
                SimpleName methodName = method.getName();
                System.out.println("method name:" + methodName);

                // get method parameters
                List param = method.parameters();
                System.out.println("method parameters:" + param);

                // get method return type
                Type returnType = method.getReturnType2();
                System.out.println("method return type:" + returnType);

                // System.out.println(method.getJavadoc());
                if (method.getJavadoc() != null) {
                    System.out.println("method javadoc:" + method.getJavadoc().toString());
                }

            }
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        }

    }
}
