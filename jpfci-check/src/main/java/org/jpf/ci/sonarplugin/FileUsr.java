/** 
* @author 吴平福 
* E-mail:421722623@qq.com 
* @version 创建时间：2016年3月9日 下午11:10:03 
* 类说明 
*/

package org.jpf.ci.sonarplugin;

/**
 * 
 */
public class FileUsr
{

	/**
	 * 
	 */
	public FileUsr()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the strUsr
	 */
	public String getStrUsr()
	{
		return strUsr;
	}

	/**
	 * @param strUsr
	 *            the strUsr to set
	 */
	public void setStrUsr(String strUsr)
	{
		this.strUsr = strUsr;
	}

	/**
	 * @return the strFileName
	 */
	public String getStrFileName()
	{
		return strFileName;
	}

	/**
	 * @param strFileName
	 *            the strFileName to set
	 */
	public void setStrFileName(String strFileName)
	{
		this.strFileName = strFileName;
	}

	/**
	 * @return the strFilePath
	 */
	public String getStrFilePath()
	{
		return strFilePath;
	}

	/**
	 * @param strFilePath
	 *            the strFilePath to set
	 */
	public void setStrFilePath(String strFilePath)
	{
		this.strFilePath = strFilePath;
	}

	private String strUsr;
	private String strFileName;
	private String strFilePath;

	public void Check()
	{
		if (strFileName.endsWith(".java"))
		{

			if (strFilePath.indexOf(FileUsrConst.strJavaKey) >= 0)
			{
				// for maven
				strFilePath = strFilePath.substring(
						strFilePath.indexOf(FileUsrConst.strJavaKey) + FileUsrConst.strJavaKey.length(),
						strFilePath.length() - 5);

			} else if (strFilePath.indexOf(FileUsrConst.strJavaKey2) >= 0)
			{
				// for ant
				strFilePath = strFilePath.substring(
						strFilePath.indexOf(FileUsrConst.strJavaKey2) + FileUsrConst.strJavaKey2.length(),
						strFilePath.length() - 5);
			} else if (strFilePath.indexOf(FileUsrConst.strJavaKey3) >= 0)
			{
				strFilePath = strFilePath.substring(
						strFilePath.indexOf(FileUsrConst.strJavaKey3) + FileUsrConst.strJavaKey3.length(),
						strFilePath.length() - 5);
			}
			// System.out.println(filePathName);

			strFilePath = strFilePath.replaceAll("/", ".");

		}
		if (strFilePath.endsWith(".cpp") || strFilePath.endsWith(".c") || strFilePath.endsWith(".h"))
		{
			// System.out.println("filePathName="+filePathName);


				strFilePath = strFilePath.replaceFirst("/", ":");


		}
	}
}
