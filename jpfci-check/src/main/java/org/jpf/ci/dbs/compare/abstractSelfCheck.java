package org.jpf.ci.dbs.compare;

import java.sql.Connection;

public abstract class abstractSelfCheck {

	public abstractSelfCheck() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * @category 检查数据库
	 * @return
	 */
	public abstract int checkDb()throws Exception;
	
	/**
	 * @category 检查数据库
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public abstract int checkDb(Connection conn)throws Exception;
}
