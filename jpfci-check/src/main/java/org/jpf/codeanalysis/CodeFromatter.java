/**
 * @author wupf@asiainfo. com
 */
package org.jpf.codeanalysis;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.jdt.internal.formatter.*;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;

/**
 * @author wupf
 * 
 */
public class CodeFromatter {

	/**
	 * format java source by default rule
	 * 
	 * @param fileContent
	 * @exception Exception
	 * @return sourceCode
	 */
	public static String format(String fileContent) throws Exception {
		String sourceCode = fileContent;
		// get default format for java
		Map options = DefaultCodeFormatterConstants.getEclipseDefaultSettings();
		DefaultCodeFormatterOptions preferences = new DefaultCodeFormatterOptions(
				options);
		Document doc = new Document(sourceCode);

		try {
			Map compilerOptions = new HashMap();
			// confirm java source base on java 1.6
			compilerOptions.put(CompilerOptions.OPTION_Compliance,
					CompilerOptions.VERSION_1_6);
			compilerOptions.put(CompilerOptions.OPTION_TargetPlatform,
					CompilerOptions.VERSION_1_6);
			compilerOptions.put(CompilerOptions.OPTION_Source,
					CompilerOptions.VERSION_1_6);
			DefaultCodeFormatter codeFormatter = new DefaultCodeFormatter(
					preferences, compilerOptions);
			// format
			TextEdit edits = codeFormatter.format(
					CodeFormatter.K_COMPILATION_UNIT, sourceCode, 0,
					sourceCode.length(), 0, null);
			edits.apply(doc);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		sourceCode = doc.get();
		return sourceCode;
	}

	public static void main(String[] arg) {
		String javaCodeBefore = "public class anc{public void def(){System.out.println(\"fef\");}   public void abc(){try{ int i=0; }catch(Exception ex){ex.print();}}}";
		String javaCodeAfter = "";
		System.out.println("format before:" + "\n");
		System.out.println(javaCodeBefore);
		try {
			javaCodeAfter = CodeFromatter.format(javaCodeBefore);
			System.out.println("format after:" + "\n");
			System.out.println(javaCodeAfter);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("format error");
		}
	}
}
