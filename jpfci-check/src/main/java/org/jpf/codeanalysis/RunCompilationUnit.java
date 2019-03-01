/** 
* @author 吴平福 
* E-mail:wupf@asiainfo.com 
* @version 创建时间：2017年5月22日 上午10:42:41 
* 类说明 
*/ 

package org.jpf.codeanalysis;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import org.jpf.utils.ios.AiFileUtil;

/**
 * 
 */
public class RunCompilationUnit {
    private static final Logger logger = LogManager.getLogger();
    /**
     * 
     */
    public RunCompilationUnit() {
        // TODO Auto-generated constructor stub
    }
    /**
     * 
     * @category @author 吴平福
     * @param strJavaFileName
     * @return
     * @throws Exception update 2017年5月11日
     */
    public static CompilationUnit getCompilationUnit(String strJavaFileName) throws Exception {

        String sourceString = AiFileUtil.getFileTxt(strJavaFileName);
        ASTParser astParser = ASTParser.newParser(AST.JLS3);
        astParser.setKind(ASTParser.K_COMPILATION_UNIT);
        astParser.setResolveBindings(true);

        astParser.setSource(sourceString.toCharArray());
        CompilationUnit cCompilationUnit = (CompilationUnit) astParser.createAST(null);
        // show import declarations in order

        //logger.info(cCompilationUnit);
        //logger.info(cCompilationUnit.types());
        //logger.info(cCompilationUnit.getNodeType());

        //logger.info(cCompilationUnit.getAST());
        //logger.info(cCompilationUnit.getCommentList());
        //logger.info(cCompilationUnit.TYPES_PROPERTY);
        
        
        // show class name
        List types = cCompilationUnit.types();
        if (types.size() == 0) {
            return null;
        }
        return cCompilationUnit;

    }
    
    public static void main(String[] args)
    {
        try {
            getCompilationUnit("E:\\svn\\svn10.3.3.233\\newcrm_jx_modules\\newcrm_jx_common\\src\\main\\java\\com\\asiainfo\\crm\\common\\constant\\MixBusiSoOrderCenterEntryEnum.java");    
            getCompilationUnit("E:\\svn\\svn10.3.3.233\\newcrm_jx_modules\\newcrm_jx_common\\src\\main\\java\\com\\asiainfo\\crm\\common\\constant\\OmConstEX.java");   
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        }
        
    }
}
