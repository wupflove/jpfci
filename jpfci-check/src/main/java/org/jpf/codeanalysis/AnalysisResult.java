/**
 * @author 421722623@qq.com
 * 结果保存
 */
package org.jpf.codeanalysis;

import java.util.Vector;

/**
 * @author wupf
 *
 */
public class AnalysisResult {

	/**
	 * 
	 */
	public AnalysisResult() {
		// TODO Auto-generated constructor stub
	}
	private String ChangeFunName="";
	private Vector<String> vInfluencesFunName=new Vector<String>();
	private boolean bInfluences=true;
	
	public String getChangeFunName() {
		return ChangeFunName;
	}
	public void setChangeFunName(String changeFunName) {
		ChangeFunName = changeFunName;
	}
	public Vector<String> getvInfluencesFunName() {
		return vInfluencesFunName;
	}
	public void setvInfluencesFunName(Vector<String> vInfluencesFunName) {
		this.vInfluencesFunName = vInfluencesFunName;
	}
	public boolean isbInfluences() {
		return bInfluences;
	}
	public void setbInfluences(boolean bInfluences) {
		this.bInfluences = bInfluences;
	}
}
