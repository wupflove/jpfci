/** 
* @author 吴平福 
* E-mail:421722623@qq.com 
* @version 创建时间：2016年6月25日 上午12:49:00 
* 类说明 
*/ 

package org.jpf.ci.gitlabs;

/**
 * 
 */
public class RespJsonData {

    private int ResultCode;
    private String Msg;
    /**
     * @return the resultCode
     */
    public int getResultCode() {
        return ResultCode;
    }
    /**
     * @param resultCode the resultCode to set
     */
    public void setResultCode(int resultCode) {
        ResultCode = resultCode;
    }
    /**
     * @return the msg
     */
    public String getMsg() {
        return Msg;
    }
    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        Msg = msg;
    }

}
