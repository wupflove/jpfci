/** 
* @author 吴平福 
* E-mail:wupf@asiainfo.com 
* @version 创建时间：2017年10月19日 下午3:18:47 
* 类说明 
*/ 

package org.jpf.unittests.samples;

/**
 * 
 */
public enum MyEnum {
    Public("public"), Protected("protected"), PackageLocal(""), Private("private");

    private String name;

    private MyEnum(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
