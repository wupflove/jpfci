/**
 * @author wupf@asiainfo. com
 * 代码影响范围分析
 * 输入参数 
 * SVN URL
 * SVN USERNAME
 * SVN PASSWORD
 * SVN R_BEFORE
 * SVN R_AFTER
 */
package org.jpf.codeanalysis;

import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author wupf
 * 
 */
public class CodeAnalysis {
	private static final Logger LOGGER = LogManager.getLogger();

	Vector<AnalysisResult> vAnalysisResults = new Vector<AnalysisResult>();

	/**
	 * 
	 */
	public CodeAnalysis(String strSvnUrl, String strSvnUsr, String strSvnPwd,
			String strSvnNum1, String strNum2) {
		// TODO Auto-generated constructor stub
		LOGGER.debug("strSvnUrl={}", strSvnUrl);
		LOGGER.debug("strSvnUsr={}", strSvnUsr);
		LOGGER.debug("strSvnPwd={}", strSvnPwd);
		LOGGER.debug("strSvnNum1={}", strSvnNum1);
		LOGGER.debug("strNum2={}", strNum2);
		try {
			String strFilePath = "";


			// 比较差异
			CodeChange cCodeChange = new CodeChange(strSvnUrl, strSvnUsr,
					strSvnPwd, strSvnNum1, strNum2);
			LOGGER.info("change function size={}", cCodeChange.getvFunctions()
					.size());

			// 循环函数
			for (int i = 0; i < cCodeChange.getvFunctions().size(); i++) {

				// 运行单元测试
				RunUnit cRunUnit = new RunUnit();
				// 变化？

				AnalysisResult cAnalysisResult = new AnalysisResult();
				cAnalysisResult.setChangeFunName(cCodeChange.getvFunctions()
						.get(i));
				cAnalysisResult.setbInfluences(cRunUnit.isbInfluences());

				vAnalysisResults.add(cAnalysisResult);
			}

			// 循环函数
			for (int i = 0; i < vAnalysisResults.size(); i++) {
				// open call Hierarchy

				// open type Hierarchy
				TypeHierarchyCheck cTypeHierarchyCheck = new TypeHierarchyCheck(
						"", "");
			}

		} catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();
		}

	}

	public static void main(String[] args) {
		if (args.length != 5) {
			LOGGER.warn("error input param number,should be  strSvnUrl,strSvnUsr,strSvnPwd,strSvnNum1,strNum2");
		} else {
			CodeAnalysis cCodeAnalysis = new CodeAnalysis(args[0], args[1],
					args[2], args[3], args[4]);
		}
	}
}
