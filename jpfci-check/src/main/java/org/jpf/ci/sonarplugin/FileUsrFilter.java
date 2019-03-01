/** 
* @author 吴平福 
* E-mail:wupf@asiainfo.com 
* @version 创建时间：2016年3月9日 下午11:42:31 
* 类说明 
*/

package org.jpf.ci.sonarplugin;

/**
 * 
 */
public class FileUsrFilter
{
	/**
	 * 
	 * @category 是否是需要的文件类型
	 * @author 吴平福
	 * @param strFileName
	 * @return update 2016年3月9日
	 */
	public static boolean IsCheckFile(String strFileName)
	{
		if (strFileName.indexOf("/build/") >= 0)
		{
			return false;
		}
		if (strFileName.endsWith(".java") || strFileName.endsWith(".cpp") || strFileName.endsWith(".c")
				|| strFileName.endsWith(".h"))
		{

				return true;

		}
		return false;
	}

}
