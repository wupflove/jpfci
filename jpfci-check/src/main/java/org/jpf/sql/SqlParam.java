/** 
 * @author 吴平福 
 * E-mail:wupf@asiainfo.com 
 * @version 创建时间：2015年2月8日 下午11:07:00 
 * 类说明 
 */
package org.jpf.sql;


public class SqlParam
{
   public String strParam;
   public long lParam;
   public int iType; //0:long 1 string
   public SqlParam(String strParam)
   {
      this.iType = 1;
      this.strParam = strParam;
   }

   public SqlParam(long lParam)
   {
      this.iType = 0;
      this.lParam = lParam;
   }

   public SqlParam(int iParam)
   {
      this.iType = 2;
      lParam = iParam;
   }
   public SqlParam()
   {
      this.iType = -1;
   }   
}
