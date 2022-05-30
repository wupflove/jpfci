/** 
 * @author 吴平福 
 * E-mail:421722623@qq.com 
 * @version 创建时间：2015年5月2日 下午2:31:28 
 * 类说明 
 */

package org.jpf.ci.checks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jpf.utils.ios.AiFileUtil;

/**
 * 
 */
public abstract class AbstractCheck {
    StringBuilder sBuffer = new StringBuilder();
	private String strCheckPath = "";
	private static final Logger logger = LogManager.getLogger();

	public abstract String GetFileType();

	
	protected abstract void doResult();

	public boolean GetExcludeFile(String strFileName) {
		if (strFileName.indexOf(File.separator + "test" + File.separator) > 0) {
			return true;
		}
		if (strFileName.indexOf(File.separator + "test-case" + File.separator) > 0) {
			return true;
		}
		if (strFileName.indexOf(File.separator + "sample" + File.separator) > 0) {
			return true;
		}

		return false;
	}

	public abstract void DoCheckRule(String strLine, String strFileName,
			long lLineNumber);

	/**
	 * 
	 */
	public AbstractCheck() {
		// TODO Auto-generated constructor stub

	}

	private void DoRead(String strFileName) throws Exception {
		// logger.info(strFileName);
		File f = new File(strFileName);
		BufferedReader br = null;
		boolean bln = false;
		try {
			FileInputStream in = new FileInputStream(f);
			String strFileCode = "GBK";
			if (strFileName.endsWith(".java")) {
				strFileCode = "UTF-8";
			}
			br = new BufferedReader(new InputStreamReader(in, strFileCode));
			String line = "";
			try {
				long lLineNumber = 0;
				while ((line = br.readLine()) != null) {
					lLineNumber++;
					line = line.trim();

					bln = CheckStr(line, bln, strFileName, lLineNumber);

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

	}

	private boolean CheckStr(String strLine, boolean isComment,
			String strFileName, long lLineNumber) {
		String regEx1 = "/[*].*[*]/";
		String regEx2 = "//.*";
		String regEx3 = "/[*]";
		String regEx4 = ".*[*]/";
		Pattern pattern = null;
		boolean bComment = isComment;
		// ǰ����/*
		if (isComment) {
			pattern = Pattern.compile(regEx4);
			Matcher matcher = pattern.matcher(strLine.trim());

			if (matcher.find()) {
				bComment = false;
				strLine = matcher.replaceAll("");
			} else {
				return bComment;
			}
			// strLine = matcher.replaceAll("");
		}
		// �ж�/**/
		pattern = Pattern.compile(regEx1);
		Matcher matcher = pattern.matcher(strLine.trim());
		strLine = matcher.replaceAll("");

		// �ж�//
		if (strLine.length() > 0) {
			pattern = Pattern.compile(regEx2);
			matcher = pattern.matcher(strLine.trim());
			strLine = matcher.replaceAll("");
		}

		// �ж�/*
		if (strLine.length() > 0) {
			pattern = Pattern.compile(regEx3);
			matcher = pattern.matcher(strLine.trim());
			bComment = matcher.find();
		}

		if (bComment == false && strLine.length() > 0) {
			DoCheckRule(strLine, strFileName, lLineNumber);
		}

		return bComment;

	}

	/*
	 * 取文件相对路径名
	 */
	protected String GetOppoFileName(String strFileName) {
		String tmpStr = AiFileUtil.getFilePath(strCheckPath);
		return strFileName.substring(tmpStr.length(), strFileName.length());
	}

	public void DoCheck(String InFilePath) {
		try {
			this.strCheckPath = InFilePath;
			if (strCheckPath.endsWith(File.separator)) {
				strCheckPath = strCheckPath.substring(0,
						strCheckPath.length() - 1);
			}
			logger.info("检查文件...");
			long sTime = System.currentTimeMillis();
			Vector<String> vector = new Vector<String>();
			AiFileUtil.getFiles(InFilePath, vector);
			long lCondFileCount = 0;
			String[] sFileType = GetFileType().split(";");
			logger.info("文件总数" + vector.size());
			logger.info("处理文件...");
			sBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><results>");
			for (int i = 0; i < vector.size(); i++) {
				String tmpFileName = (String) vector.get(i);

				if (GetExcludeFile(tmpFileName)) {
					continue;
				}
				for (int j = 0; j < sFileType.length; j++) {
					if (tmpFileName.trim().endsWith(sFileType[j])) {
						lCondFileCount++;
						DoRead(tmpFileName);
						break;
					}
				}

			}
			sBuffer.append("</results>");
			AiFileUtil.saveFile(strCheckPath + File.separator
					+ "hz11-cppcheck-report.xml", sBuffer);
			System.out.println("符合条件的文件总数" + lCondFileCount);
			long eTime = System.currentTimeMillis();
			System.out.println("处理文件用时(单位MS):" + (eTime - sTime));
			doResult();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void getVersion() {

		System.out.println("java version: "
				+ System.getProperty("java.version"));
	}
}
