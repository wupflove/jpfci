package org.jpf.ci.dbs.compare;

import java.io.File;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.jpf.utils.ios.AiFileUtil;
import org.jpf.utils.scms.SVNUtil;
import org.jpf.utils.web.AiUrlUtil;

public class PdmUtils {

    /**
     * 
     * @category 获取PDM文件
     * @author 吴平福
     * @param el
     * @param pdmpath
     * @param user
     * @param password update 2016年5月2日
     */
    public static void getPdmsBySvnKit(Element el, String pdmpath, String user, String password) {
        // 更新svn上的pdm到指定文件中
        NodeList npath = el.getElementsByTagName("svnurl");
        for (int i = 0; i < npath.getLength(); i++) {
            Node child = npath.item(i);
            if (child instanceof Element) {
                String pdmurl = child.getFirstChild().getNodeValue();
                if (pdmurl.contains("/")) {
                    int j = pdmurl.lastIndexOf("/");
                    String url = pdmurl.substring(0, j);
                    String name = pdmurl.substring(j + 1, pdmurl.length()).trim();
                    // 获得倒数第二层目录
                    int secd = url.lastIndexOf("/");
                    String secdpath = url.substring(secd + 1, url.length());
                    String subPdmPath = pdmpath + File.separator + i + File.separator + secdpath;
                    if (AiUrlUtil.isConnect(AiUrlUtil.getUrlIndex(pdmurl))) {
                        AiFileUtil.delDirWithFiles(subPdmPath);
                        SVNUtil.checkout(url, subPdmPath, user, password);
                        SVNUtil.updateLatest(url, subPdmPath, name, user, password);
                    }
                }
            }
        }

    }
    
    /**
     * 
     * @category PDM文件保存目录 
     * @author 吴平福 
     * @param strConfigFileName
     * @return
     * @throws Exception
     * update 2016年9月19日
     */
    public static String getPdmSavePath(String strConfigFileName)throws Exception
    {
        File directory = new File("");// 参数为空
        String downPdmPath = directory.getCanonicalPath();
        String[] downPdmSvn = strConfigFileName.split(".xml");
        String downPdmFmSvn = downPdmSvn[0];
        downPdmPath = downPdmPath + File.separator + downPdmFmSvn;
        return downPdmPath;
    }
    
    /**
     * 
     * @category oracle_mysql,oracle与mysql映射关系
     * @author 吴平福
     * @param el
     * @param mGetTablefmPdm update 2016年5月2日
     */
    public static void getDbRelation(Element el, GetTablefmPdm mGetTablefmPdm) {

        NodeList npdm = el.getElementsByTagName("oracle_mysql");
        for (int i = 0; i < npdm.getLength(); i++) {
            Node child = npdm.item(i);
            if (child instanceof Element) {
                String oracleTomy = child.getFirstChild().getNodeValue();
                String[] s = oracleTomy.toLowerCase().trim().split(";");
                String[] s1;
                String[] s2;
                if (s.length >= 2) {
                    if (s[0].contains("(") && s[1].contains("(")) {
                        s1 = s[0].split("\\(");
                        s2 = s[1].split("\\(");
                        mGetTablefmPdm.setConverData(s1[0], s2[0]);
                    } else {
                        mGetTablefmPdm.setConverData(s[0], s[1]);
                    }

                }
            }
        }

    }
    

    /**
     * 
     * @category forceConvert强制需要转换的域
     * @author 吴平福
     * @param el
     * @param mGetTablefmPdm update 2016年5月2日
     */
    public static void getConverDomain(Element el, GetTablefmPdm mGetTablefmPdm) {
        NodeList nforce = el.getElementsByTagName("forceConvert");
        for (int i = 0; i < nforce.getLength(); i++) {
            Node child = nforce.item(i);
            if (child instanceof Element) {
                String forceData = child.getFirstChild().getNodeValue();
                if (forceData != null) {
                    if (forceData.contains(";")) {
                        String[] s = forceData.toLowerCase().trim().split(";");
                        if (s.length >= 1) {
                            for (String s1 : s) {
                                mGetTablefmPdm.setForceDomain(s1);
                            }

                        }
                    }
                }
            }

        }
    }
    
    /**
     * 
     * @category forceConvertDtType 指定某个域某个表某个字段的数据类型
     * @author 吴平福
     * @param el
     * @param mGetTablefmPdm update 2016年5月2日
     */
    public static void getConverDtType(Element el, GetTablefmPdm mGetTablefmPdm) {
        // forceConvertDtType 指定某个域某个表某个字段的数据类型
        NodeList ntype = el.getElementsByTagName("forceConvertDtType");
        for (int i = 0; i < ntype.getLength(); i++) {
            Node child = ntype.item(i);
            if (child instanceof Element) {
                String DataType = child.getFirstChild().getNodeValue();
                if (DataType != null) {
                    if (DataType.contains(",")) {
                        String[] s = DataType.toLowerCase().trim().split(",");
                        if (s.length >= 4) {
                            mGetTablefmPdm.setMap(s[0], s[1], s[2], s[3]);
                        }

                    }
                }
            }

        }
    }

    /**
     * 
     * @category 指定某个域某个表某个字段的默认值数据类型
     * @author 吴平福
     * @param el
     * @param mGetTablefmPdm update 2016年5月2日
     */
    public static void getConverDefType(Element el, GetTablefmPdm mGetTablefmPdm) {
        // forceConvertDefType 指定某个域某个表某个字段的默认值数据类型
        NodeList ndtype = el.getElementsByTagName("forceConvertDefType");
        for (int i = 0; i < ndtype.getLength(); i++) {
            Node child = ndtype.item(i);
            if (child instanceof Element) {
                String DataType = child.getFirstChild().getNodeValue();
                if (DataType != null) {
                    if (DataType.contains(",")) {
                        String[] s = DataType.toLowerCase().trim().split(",");
                        if (s.length >= 4) {
                            mGetTablefmPdm.setDefMap(s[0], s[1], s[2], s[3]);
                        }

                    }
                }
            }

        }
    }
}
