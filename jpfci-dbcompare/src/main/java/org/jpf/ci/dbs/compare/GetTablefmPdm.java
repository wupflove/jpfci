package org.jpf.ci.dbs.compare;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.XPath;

import org.jpf.utils.ios.AiFileUtil;

public class GetTablefmPdm {
    private static final Logger LOGGER = LogManager.getLogger();

    // 是否要进行数据类型转换
    private boolean isConver = false;

    // oracle到mysql映射
    private Map<String, String> converData = new HashMap<String, String>();

    // mysql版本，但是必须要转换的域
    private List<String> forceDomain = new LinkedList<String>();

    // PDM路径
    private String PDMPath;

    // 指定某个域某个表某个字段的类型
    private HashMap<String, HashMap<String, HashMap<String, String>>> map = new HashMap<String, HashMap<String, HashMap<String, String>>>();

    // 指定某个域某个字段的默认值
    private HashMap<String, HashMap<String, HashMap<String, String>>> defmap = new HashMap<String, HashMap<String, HashMap<String, String>>>();

    // 域名-表
    private LinkedHashMap<String, TreeMap<String, AiTable>> domainTable = new LinkedHashMap<String, TreeMap<String, AiTable>>();

    // 域名-索引
    private LinkedHashMap<String, TreeMap<String, AiTable>> domainIndex = new LinkedHashMap<String, TreeMap<String, AiTable>>();

    private LinkedList<String> userlist = new LinkedList<String>();

    protected StringBuffer[] errInfoSb = { new StringBuffer(), new StringBuffer(), new StringBuffer(),
            new StringBuffer(), new StringBuffer(), new StringBuffer() };

    String getErrorHtmlName() {
        return "dbcompare_errinfo_pdm.html";
    }

    String getErrorMailTitle() {
        return "读取PDM异常(自动发出)";
    }

    public LinkedList<String> getUserlist() {
        return userlist;
    }

    public void setUserlist(String user) {
        userlist.add(user);
    }

    public Map<String, String> getConverData() {
        return converData;
    }

    public void setConverData(String key, String value) {
        converData.put(key, value);
    }

    public List<String> getForceDomain() {
        return forceDomain;
    }

    public void setForceDomain(String fDomain) {
        forceDomain.add(fDomain);
    }

    public String getPDMPath() {
        return PDMPath;
    }

    public void setPDMPath(String pDMPath) {
        PDMPath = pDMPath;
    }

    public HashMap<String, HashMap<String, HashMap<String, String>>> getMap() {
        return map;
    }

    /**
     * 
     * @category @author 吴平福
     * @param domain
     * @param tableName
     * @param column
     * @param type
     *            update 2016年5月3日
     */
    public void setMap(String domain, String tableName, String column, String type) {

        // 第一步，判断对应的数据库是否在schemeMap当中，如果存在判断tableMap是存在
        if (map.containsKey(domain)) {
            HashMap tableMap = map.get(domain);
            /*
             * 第二步，判断对应的表名字是否在tableMap当中，如果存在则把要修改的信息放入columnMap当中
             * ,如果不存在对应表名不在，则创建相应的columnMap并存入
             */
            if (tableMap.containsKey(tableName)) {
                // 表名字对应一个列map
                HashMap<String, String> columnMap = (HashMap<String, String>) tableMap.get(tableName);
                columnMap.put(column, type);
            } else {
                HashMap<String, String> columnMap = new HashMap<String, String>();
                columnMap.put(column, type);
                tableMap.put(tableName, columnMap);
            }
        } else {
            /*
             * 发现一个新数据库名字：创建tableMap，columnMap
             */
            HashMap<String, String> columnMap = new HashMap<String, String>();
            HashMap<String, HashMap<String, String>> tableMap = new HashMap<String, HashMap<String, String>>();
            columnMap.put(column, type);
            tableMap.put(tableName, columnMap);
            map.put(domain, tableMap);
        }

    }

    public HashMap<String, HashMap<String, HashMap<String, String>>> getDefMap() {
        return defmap;
    }

    /**
     * 
     * @category @author 吴平福
     * @param domain
     * @param table
     * @param column
     * @param dtype
     *            update 2016年5月2日
     */
    public void setDefMap(String domain, String table, String column, String dtype) {

        // 第一步，判断对应的数据库是否在schemeMap当中，如果存在判断tableMap是存在
        if (defmap.containsKey(domain)) {
            HashMap tableMap = defmap.get(domain);
            /*
             * 第二步，判断对应的表名字是否在tableMap当中，如果存在则把要修改的信息放入columnMap当中
             * ,如果不存在对应表名不在，则创建相应的columnMap并存入
             */
            if (tableMap.containsKey(table)) {
                // 表名字对应一个列map
                HashMap<String, String> columnMap = (HashMap<String, String>) tableMap.get(table);
                columnMap.put(column, dtype);
            } else {
                HashMap<String, String> columnMap = new HashMap<String, String>();
                columnMap.put(column, dtype);
                tableMap.put(table, columnMap);
            }
        } else {
            /*
             * 发现一个新数据库名字：创建tableMap，columnMap
             */
            HashMap<String, String> columnMap = new HashMap<String, String>();
            HashMap<String, HashMap<String, String>> tableMap = new HashMap<String, HashMap<String, String>>();
            columnMap.put(column, dtype);//
            tableMap.put(table, columnMap);
            defmap.put(domain, tableMap);
        }

    }

    public LinkedHashMap<String, TreeMap<String, AiTable>> getDomainTable() {
        return domainTable;
    }

    public void setDomainTable(
            LinkedHashMap<String, TreeMap<String, AiTable>> domain_table) {
        this.domainTable = domain_table;
    }

    public LinkedHashMap<String, TreeMap<String, AiTable>> getDomainIndex() {
        return domainIndex;
    }

    public void setDomainIndex(
            LinkedHashMap<String, TreeMap<String, AiTable>> domain_index) {
        this.domainIndex = domain_index;
    }

    /**
     * 
     * @category 需要转换的数值类型
     * @author 吴平福
     * @param word
     * @param dataLength
     * @return update 2016年5月3日
     */
    private String switchNumberType(String word, int dataLength) {
        if (isConver) {
            switch (dataLength)
            {
            case 0:
                break;
            case 1:
            case 2:
                word = "tinyint(2)";
                break;
            case 3:
            case 4:
                word = "smallint(" + dataLength + ")";
                break;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                word = "int(" + dataLength + ")";
                break;
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
                word = "bigint(" + dataLength + ")";
                break;
            default:
                word = "decimal(" + dataLength + "," + "0)";
                break;
            }
        }
        return word;

    }

    /**
     * 
     * @category 需要转换的默认值类型
     * @author 吴平福
     * @param isMysql
     * @param domain
     * @param tableName
     * @param columnName
     * @param columnType
     * @param defaultValue
     * @return update 2016年5月2日
     */
    public String converDefaultValue(boolean isMysql, String domain,
            String tableName, String columnName, String columnType,
            String defaultValue) {
        if (defaultValue == "" || defaultValue == null) {
            return "";
        }
        if (defaultValue.equalsIgnoreCase("null")) {
            return "";
        }
        defaultValue = defaultValue.toLowerCase().trim();
        columnType = columnType.toLowerCase().trim();
        String s[];
        if (columnType == null) {
            return "";
        } else {
            columnType = columnType.toLowerCase();
        }
        if (tableName != null) {
            tableName = tableName.toLowerCase();
        }
        if (columnName != null) {
            columnName = columnName.toLowerCase();
        }
        if (domain != null) {
            domain = domain.toLowerCase();
        }
        if (defaultValue.contains("\"")) {
            s = defaultValue.split("\"");
            if (s.length < 1) {
                return "";
            } else {
                defaultValue = s[1];
            }
        }
        if (defaultValue.contains("\'")) {
            s = defaultValue.split("\'");
            if (s.length < 1) {
                return "";
            } else {
                defaultValue = s[1];
            }
        }

        // for mysql
        if (isMysql && !(defaultValue.equals("0000-00-00 00:00:00"))
                && !(defaultValue.equalsIgnoreCase("CURRENT_TIMESTAMP"))) {
            if (columnType.equals("timestamp")) {
                defaultValue = "CURRENT_TIMESTAMP";
            } else if (columnType.equals("datetime")) {
                defaultValue = "CURRENT_TIMESTAMP";
            } else if (columnType.equals("date")) {
                defaultValue = "0000-00-00 00:00:00";
            }
        }
        // 如果是oracle类型或者必须要转换的mysql类型，将字段类型转换为mysql类型
        // oracle版本强制转换为指定数据类型
        if (!isMysql && (defmap.containsKey(domain))) {

            if (defmap.get(domain).containsKey(tableName)) {
                if (defmap.get(domain).get(tableName).containsKey(columnName)) {
                    return defmap.get(domain).get(tableName).get(columnName);
                }
            }
        }

        return defaultValue;
    }

    /**
     * 
     * @category 需要转换的数据类型
     * @author 吴平福
     * @param isMysql
     * @param columnType
     * @param tableName
     * @param length
     * @param precision
     * @param domain
     * @param columnName
     * @return update 2016年5月2日
     */
    public String convertData(boolean isMysql, String columnType,
            String tableName, int length, int precision, String domain,
            String columnName) {
        if (columnType == null) {
            return "";
        } else {
            columnType = columnType.toLowerCase().trim();
        }
        if (tableName != null) {
            tableName = tableName.toLowerCase().trim();
        }
        if (columnName != null) {
            columnName = columnName.toLowerCase().trim();
        }
        if (domain != null) {
            domain = domain.toLowerCase();
        }
        if (columnType.startsWith("numeric")) {
            columnType = columnType.replace("numeric", "number");
        }

        // 如果是oracle类型或者必须要转换的mysql类型，将字段类型转换为mysql类型
        if (!isMysql || forceDomain.contains(domain)) {
            // oracle版本强制转换为指定数据类型
            if (map.containsKey(domain)) {
                if (map.get(domain).containsKey(tableName)) {
                    if (map.get(domain).get(tableName).containsKey(columnName)) {
                        return map.get(domain).get(tableName).get(columnName);
                    }
                }
            }

            String s;
            String[] type = null;
            if (columnType.contains("(")) {
                type = columnType.split("\\(");
                s = type[0];
            } else {
                s = columnType;
            }

            if ((s.equals("decimal") || s.equals("number")) && (precision == 0)) {
                columnType = switchNumberType(s, length);
            } else {
                if (converData.containsKey(s)) {
                    if (columnType.contains("(")) {
                        if (precision == 0) {
                            columnType = converData.get(s) + "(" + length + ")";
                        } else

                        {
                            columnType = converData.get(s) + "(" + length + ","
                                    + precision + ")";
                        }
                    } else {
                        if (isMysql && s.equals("date")) {
                            columnType = "date";
                        } else {
                            columnType = converData.get(s);
                        }

                    }
                }

            }
        }

        return columnType;
    }

    /**
     * 
     * @category 获得所有的：id-user映射
     * @author 吴平福
     * @param doc
     * @param userColumn
     *            update 2016年5月3日
     */
    public void getXmlUser(Document doc, Map<String, String> userColumn) {
        // 根据xpath方式得到所要得到xml文档的具体对象,根据分析解析xml文档可知，xml文档中含有前缀名
        Map<String, String> map = new HashMap<String, String>();
        map.put("c", "collection");
        // 根据xml文档，//c:Table 即为得到的文档对象
        XPath path = doc.createXPath("//c:Users");
        path.setNamespaceURIs(map);
        List<Element> list = path.selectNodes(doc);
        // 得到tables对象，该对象是该pdm文件中所有表的集合
        for (Element element : list) {
            for (Iterator<Element> iter = element.elementIterator("User"); iter
                    .hasNext();) {
                Element userElement = iter.next();
                Attribute attribute = userElement.attribute("Id");
                userColumn.put(attribute.getText(),
                        userElement.elementText("Code"));
                // setUserlist(userElement.elementText("Code"));
            }
        }
    }

    /**
     * 
     * @category 获得每个table的user的id
     * @author 吴平福
     * @param table_s
     * @param userColumn
     * @return update 2016年5月2日
     */
    public String getTableUser(Element table_s, Map<String, String> userColumn) {
        String userName = null;
        Element tableOwner = table_s.element("Owner");
        if (tableOwner != null) {
            Element tableUser = tableOwner.element("User");
            userName = tableUser.attributeValue("Ref");
            if (userColumn.containsKey(userName)) {
                userName = userColumn.get(userName);
            }
        }
        return userName;
    }

    // 解析文档是否是mysql或oracle类型
    public boolean isMysql(Document doc) {
        // 根据xpath方式得到所要得到xml文档的具体对象,根据分析解析xml文档可知，xml文档中含有前缀名
        Map<String, String> map = new HashMap<String, String>();
        map.put("c", "collection");
        // 根据xml文档，//c:Table 即为得到的文档对象
        XPath path = doc.createXPath("//c:DBMS");
        path.setNamespaceURIs(map);
        List<Element> list = path.selectNodes(doc);
        // 得到tables对象，该对象是该pdm文件中所有表的集合
        for (Element element : list) {
            for (Iterator<Element> iter = element.elementIterator("Shortcut"); iter
                    .hasNext();) {
                Element userElement = iter.next();
                String s = userElement.elementText("Code").toUpperCase();
                if (s.substring(0, 5).equals("MYSQL")) {
                    LOGGER.debug(s);
                    return true;
                } else if (s.substring(0, 6).equals("ORACLE")) {
                    LOGGER.debug(s);
                    return false;
                }

            }
        }
        return false;
    }

    /**
     * 
     * @category @author 吴平福
     * @param sb
     * @param table_s
     *            update 2016年5月3日
     */
    private void addErrMsgBuffer(StringBuffer sb, Element table_s) {
        sb.append("<tr>").append("<th></th><td>").append(OpenPdm.getLastPath())
                .append("</td><td>").append(table_s.elementText("Code")).append("</td><td></td><td></td><td>")
                .append("Exists the table name, but the fields do not exist").append("</td></tr>");

        LOGGER.error("获得PDM字段列ERROR: " + "PDM name:" + OpenPdm.getLastPath() + "\n table_name "
                + table_s.elementText("Code")
                + " :Exists the table name, but the fields do not exist");
    }

    /**
     * 
     * @category @author 吴平福
     * @param sb
     * @param table_s
     * @param cTableIndex
     *            update 2016年5月3日
     */
    private void addErrMsgBuffer(StringBuffer sb, Element table_s, AiTableIndex cTableIndex) {

        LOGGER.error("获得PDM字段列ERROR: " + "PDM name:" + OpenPdm.getLastPath() + "\n table_name "
                + table_s.elementText("Code")
                + " index_name "
                + cTableIndex.getIndexName()
                + " :Exists index name, but index fields  do not exist");
        sb.append("<tr>").append("<th></th><td>").append(OpenPdm.getLastPath())
                .append("</td><td>").append(table_s.elementText("Code")).append("</td><td>")
                .append(cTableIndex.getIndexName())
                .append("</td><td></td><td>")
                .append("Exists index name, but index fields  do not exist")
                .append("</td></tr>");
    }

    /**
     * 
     * @category 获得字段列
     * @author 吴平福
     * @param table_s
     * @param s
     * @param tableColumn
     * @param owner
     * @param isMysql
     * @param columnmap
     *            update 2016年5月2日
     */
    private void getColumns(Element table_s, String s,
            Map<String, AiColumn> tableColumn, String owner, boolean isMysql,
            LinkedHashMap columnmap) {
        Element columns = table_s.element(s);
        if (columns == null) {
            addErrMsgBuffer(errInfoSb[0], table_s);
        } else {
            List<AiColumn> columnList = new ArrayList<AiColumn>();
            int i = 0;
            for (Iterator<Element> cols = columns.elementIterator("Column"); cols.hasNext();) {
                Element column = cols.next();
                i++;
                String type;// 数据类型
                String def; // 默认值
                String columnName;// 列名
                String nullable = null;// 是否为空
                int length = 0, precision = 0;// 数据长度，精度
                boolean isSequnce = false;

                type = column.elementText("DataType");
                if (column.elementText("Length") != null) {
                    length = Integer.parseInt(column.elementText("Length"));
                }
                if (column.elementText("Precision") != null) {
                    precision = Integer.parseInt(column.elementText("Precision"));
                }

                columnName = column.elementText("Code");
                columnName = columnName.toLowerCase().trim();
                type = convertData(isMysql, type, table_s.elementText("Code"),
                        length, precision, owner, columnName);
                type = type.replaceAll("\\s*", "");
                type = type.toLowerCase();
                // add by wupf for oracle 5.5 begin
                type = type.replaceAll("byte", "");
                // add by wupf for oracle 5.5 end
                // 是否自增长
                if (column.element("Sequence") != null
                        || (column.element("Identity") != null)) {
                    isSequnce = true;
                }
                // 是否为空
                if (((column.elementText("Mandatory")) != null)
                        || ((column.elementText("Column.Mandatory")) != null)) {
                    nullable = "no";
                } else {
                    nullable = "yes";
                }
                String s1 = column.elementText("DefaultValue");
                def = converDefaultValue(isMysql, owner,
                        table_s.elementText("Code"), columnName, type,
                        column.elementText("DefaultValue"));
                // 新建字段列
                AiColumn column2 = new AiColumn(columnName, type, nullable, def);

                if (isSequnce) {
                    column2.setExtra("auto_increment");
                } else if (def.trim().equalsIgnoreCase(
                        "CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")) {
                    column2.setExtra("on update CURRENT_TIMESTAMP");
                }

                else {
                    column2.setExtra("");
                }

                column2.setDataLength(length);
                column2.setOrdinal_position(i);
                column2.setCHARACTER_SET_NAME("utf8");
                column2.setCOLLATION_NAME(null);
                column2.setNullable(nullable);
                Attribute attribute = column.attribute("Id");
                tableColumn.put(attribute.getText(), column2);
                columnmap.put(column2.getColumnName(), column2);
                // System.out.println(column2.getNullable());
            }
        }

    }

    /**
     * 
     * @category 获得PDM字段列,不转换数据类型
     * @author 吴平福
     * @param table_s
     * @param s
     * @param tableColumn
     * @param owner
     * @param isMysql
     * @param columnmap
     *            update 2016年5月2日
     */
    public void getColumnNoConvert(Element table_s, String s,
            Map<String, AiColumn> tableColumn, String owner, boolean isMysql,
            LinkedHashMap columnmap) {
        Element columns = table_s.element(s);
        if (columns == null) {
            addErrMsgBuffer(errInfoSb[1], table_s);
        } else {
            List<AiColumn> columnList = new ArrayList<AiColumn>();
            int i = 0;
            for (Iterator<Element> cols = columns.elementIterator("Column"); cols.hasNext();) {
                Element column = cols.next();
                i++;
                String type, def, columnName, nullable = null;
                int length = 0, precision = 0;
                boolean isSequnce = false;
                type = column.elementText("DataType");
                if (column.elementText("Length") != null) {
                    length = Integer.parseInt(column.elementText("Length"));
                }
                if (column.elementText("Precision") != null) {
                    precision = Integer.parseInt(column
                            .elementText("Precision"));
                }
                columnName = column.elementText("Code");
                columnName = columnName.toLowerCase().trim();
                // type=
                // convertData(isMysql,type,table_s.elementText("Code"),length,precision,owner,columnName);
                if (type == null) {
                    type = "";
                }
                type = type.replaceAll("\\s*", "");
                type = type.toLowerCase();

                // add by wupf for oracle 5.5 begin
                type = type.replaceAll("byte", "");
                // add by wupf for oracle 5.5 end

                Element sequenceElement = column.element("Sequence");
                if (column.element("Sequence") != null
                        || (column.element("Identity") != null)) {
                    isSequnce = true;
                }

                if (((column.elementText("Mandatory")) != null)
                        || ((column.elementText("Column.Mandatory")) != null)) {
                    nullable = "no";
                } else {
                    nullable = "yes";
                }
                def = column.elementText("DefaultValue");
                // def=converDefaultValue(isMysql,owner,table_s.elementText("Code"),columnName,type,column.elementText("DefaultValue"));
                if (def == null || def == "") {
                    def = "";
                }
                // 新建字段列
                AiColumn column2 = new AiColumn(columnName, type, nullable, def);
                if (isSequnce) {
                    column2.setExtra("auto_increment");
                }

                else if (def.trim().equalsIgnoreCase(
                        "CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")) {
                    column2.setExtra("on update CURRENT_TIMESTAMP");
                }

                else {
                    column2.setExtra("");
                }

                column2.setDataLength(length);
                column2.setOrdinal_position(i);
                column2.setCHARACTER_SET_NAME("utf8");
                column2.setCOLLATION_NAME(null);
                column2.setNullable(nullable);
                Attribute attribute = column.attribute("Id");
                tableColumn.put(attribute.getText(), column2);
                columnmap.put(column2.getColumnName(), column2);
                // System.out.println(column2.getNullable());
            }
        }

    }

    /**
     * 
     * @category 获得索引
     * @author 吴平福
     * @param table_s
     * @param tableColumn
     * @param indexmap
     *            update 2016年5月2日
     */
    public void getIndex(Element table_s, Map<String, AiColumn> tableColumn, LinkedHashMap indexmap) {
        Element indexe = table_s.element("Indexes");

        if (indexe != null) {

            for (Iterator<Element> index = indexe.elementIterator("Index"); index.hasNext();) {
                String indexName, constraint_type = null;
                int isUnique;
                Element Eachindex = index.next();
                Element LinkedObjectElement = Eachindex.element("LinkedObject");
                if (LinkedObjectElement == null) {
                    indexName = Eachindex.elementText("Code");
                    indexName = indexName.toLowerCase();
                    indexName = indexName.replaceAll("\\s*", "");
                    if (Eachindex.elementText("Unique") != null) {
                        isUnique = 0;
                        constraint_type = "UNIQUE";
                    } else {
                        isUnique = 1;
                    }
                    // ADD BY WUPF FOR ORACLE 5/5
                    if (constraint_type == null) {
                        constraint_type = "INDEX";
                    }

                    Element indexColumn = Eachindex.element("IndexColumns");
                    AiTableIndex cTableIndex = new AiTableIndex(indexName, isUnique);
                    cTableIndex.setConstraint_type(constraint_type);
                    if (indexColumn != null) {
                        for (Iterator<Element> cols = indexColumn
                                .elementIterator("IndexColumn"); cols.hasNext();) {
                            Element column = cols.next();

                            Element columnin = column.element("Column");
                            if (columnin != null) {
                                columnin = columnin.element("Column");
                                Attribute attribute = columnin.attribute("Ref");

                                if (tableColumn.containsKey(attribute.getText())) {
                                    AiColumn column2 = tableColumn.get(attribute.getText());
                                    cTableIndex.addColName(column2.getColumnName());

                                    indexmap.put(cTableIndex.getIndexName(), cTableIndex);
                                }
                            } else {
                                addErrMsgBuffer(errInfoSb[2], table_s, cTableIndex);
                            }

                        }

                    }
                }

            }

        }
    }

    /**
     * 
     * @category 获得key
     * @author 吴平福
     * @param table_s
     * @param tableColumn
     * @param indexmap
     *            update 2016年5月2日
     */
    public void getKey(Element table_s, Map<String, AiColumn> tableColumn,
            LinkedHashMap indexmap) {

        // primarykey
        // 通过一个table对象，得到primarykey对象
        Element primaryKey = table_s.element("PrimaryKey");
        String pattribute = null;
        if (primaryKey != null) {
            Element primaryColumn = primaryKey.element("Key");
            if (primaryColumn != null) {
                pattribute = primaryColumn.attribute("Ref").getText();
            }

        }
        // 通过一个table对象，得到keys对象

        Element keys = table_s.element("Keys");
        if (keys != null) {
            for (Iterator<Element> key = keys.elementIterator("Key"); key
                    .hasNext();) {
                Element Eachkey = key.next();
                int isUnique = 0;
                String keyName = null;
                String constraint_type = null;
                AiTableIndex cTableIndex = new AiTableIndex(keyName, isUnique);

                if (Eachkey.attribute("Id").getText().equals(pattribute)) {
                    keyName = "PRIMARY";
                    constraint_type = "PRIMARY KEY";
                } else {
                    constraint_type = "UNIQUE";
                    // key的命名规则：如果constraintName不为空，则与constraintName相同，如果为空，则取key名+表名前八位
                    if (Eachkey.elementText("ConstraintName") != null) {
                        keyName = Eachkey.elementText("ConstraintName");
                    } else {
                        if (table_s.elementText("Code").length() >= 8) {
                            keyName = "AK_" + Eachkey.elementText("Code") + "_"
                                    + table_s.elementText("Code").substring(0, 8);
                        } else {
                            keyName = "AK_" + Eachkey.elementText("Code") + "_" + table_s.elementText("Code");
                        }

                    }
                }
                keyName = keyName.toLowerCase();
                keyName = keyName.replaceAll("\\s*", "");

                // add by wupf for oracle 5/5 BEGIN
                if (keyName.equalsIgnoreCase("primary")) {
                    cTableIndex.setIndexName("pk_" + table_s.elementText("Code").toLowerCase());
                } else {
                    cTableIndex.setIndexName(keyName);
                }
                // add by wupf for oracle 5/5 END
                cTableIndex.setNON_UNIQUE(isUnique);
                cTableIndex.setConstraint_type(constraint_type);
                if (indexmap.containsKey(cTableIndex.getIndexName())) {
                    LOGGER.error("获得PDM字段列ERROR: " + "PDM name:" + OpenPdm.getLastPath()
                            + "\n Exists same name of index and key: table_name "
                            + table_s.elementText("Code")
                            + " key_name "
                            + cTableIndex.getIndexName()
                            + " :keep index and remove key");
                    errInfoSb[3].append("<tr>").append("<th></th><td>").append(OpenPdm.getLastPath())
                            .append("</td><td>").append(table_s.elementText("Code")).append("</td><td>")
                            .append(cTableIndex.getIndexName())
                            .append("</td><td>").append(cTableIndex.getIndexName()).append("</td><td>")
                            .append("Exists same name of index and key:" + cTableIndex.getIndexName()
                                    + "keep index and remove key")
                            .append("</td></tr>");
                }

                Element keyColumn = Eachkey.element("Key.Columns");
                if (keyColumn != null) {
                    for (Iterator<Element> cols = keyColumn.elementIterator("Column"); cols.hasNext();) {
                        Element column = cols.next();
                        Attribute attribute = column.attribute("Ref");
                        if (tableColumn.containsKey(attribute.getText())) {
                            AiColumn column2 = tableColumn.get(attribute.getText());
                            cTableIndex.addColName(column2.getColumnName());
                            indexmap.put(cTableIndex.getIndexName(), cTableIndex);
                        }
                    }

                } else {
                    LOGGER.error("获得PDM字段列ERROR: " + "PDM name:" + OpenPdm.getLastPath() + "\n table_name "
                            + table_s.elementText("Code")
                            + " key_name "
                            + cTableIndex.getIndexName()
                            + " :Exists key name, but key fields  do not exist");
                    errInfoSb[4].append("<tr>").append("<th></th><td>").append(OpenPdm.getLastPath())
                            .append("</td><td>").append(table_s.elementText("Code")).append("</td><td>")
                            .append("</td><td>").append(cTableIndex.getIndexName()).append("</td><td>")
                            .append("Exists key name, but key fields  do not exist").append("</td></tr>");
                }
            }
        }
    }

    /**
     * 
     * @category 将获得的表结构信息及索引名存入map中
     * @author 吴平福
     * @param table_s
     * @param owner
     * @param tableColumn
     * @param isMysql
     * @param in_PdmName
     *            update 2016年5月2日
     */
    private void readTableAndIndex(Element table_s, String owner, Map<String, AiColumn> tableColumn, boolean isMysql,
            final String in_PdmName) {

        // 表名
        String tableName = table_s.elementText("Code");
        tableName = tableName.toLowerCase();
        LOGGER.debug(owner+"."+tableName);
        // 如果domainTable已经存在owner，将table追加在对应的owner中
        // ,domainTable=<owner,<tableName,table>>
        if (domainTable.containsKey(owner)) {
            TreeMap<String, AiTable> tableMap = domainTable.get(owner);
            // 同一个owner下不可能存在同名的表
            if (tableMap.containsKey(tableName)) {
                return;
            } else {
                AiTable table = new AiTable(tableName, "BASE TABLE".toLowerCase().trim());
                table.pdmName = in_PdmName;
                if (isConver) {
                    getColumns(table_s, "Columns",
                            tableColumn, owner, isMysql,
                            table.getColumns());
                } else {
                    getColumnNoConvert(table_s, "Columns",
                            tableColumn, owner, isMysql,
                            table.getColumns());
                }

                if (table.getColumns() != null) {
                    tableMap.put(tableName, table);
                }

            }

        } else {
            TreeMap<String, AiTable> tableMap = new TreeMap<String, AiTable>();
            AiTable table = new AiTable(tableName, "BASE TABLE".toLowerCase().trim());
            table.pdmName = in_PdmName;
            LinkedHashMap columnmap = new LinkedHashMap();
            if (isConver) {
                getColumns(table_s, "Columns", tableColumn,
                        owner, isMysql, columnmap);
            } else {
                getColumnNoConvert(table_s, "Columns",
                        tableColumn, owner, isMysql,
                        columnmap);
            }
            if (columnmap != null) {
                table.setColumns(columnmap);
                tableMap.put(tableName, table);
                domainTable.put(owner, tableMap);
            }
        }

        // 获得domain_index 索引
        // 对于已经存在的owner，追加在里面
        if (domainIndex.containsKey(owner)) {
            TreeMap<String, AiTable> tableMap = domainIndex.get(owner);
            if (tableMap.containsKey(tableName)) {
                return;
            } else {
                TreeMap<String, AiTable> indexMap = new TreeMap<String, AiTable>();
                AiTable table = new AiTable(tableName, "BASE TABLE".toLowerCase().trim());
                table.pdmName = in_PdmName;
                LinkedHashMap indexmap = new LinkedHashMap();
                getIndex(table_s, tableColumn, indexmap);
                getKey(table_s, tableColumn, indexmap);
                if (indexmap != null) {
                    table.indexs = indexmap;
                    tableMap.put(tableName, table);
                }

            }
        } else {
            TreeMap<String, AiTable> tableMap = new TreeMap<String, AiTable>();
            AiTable table = new AiTable(tableName, "BASE TABLE".toLowerCase().trim());
            table.pdmName = in_PdmName;
            LinkedHashMap indexmap = new LinkedHashMap();
            getIndex(table_s, tableColumn, indexmap);
            getKey(table_s, tableColumn, indexmap);
            if (indexmap != null) {
                table.indexs = indexmap;
                tableMap.put(tableName, table);
                domainIndex.put(owner, tableMap);
            }

        }
    }

    /**
     * 
     * @category 以xml方式，借助dom4j和xpath查找到表的信息
     * @author 吴平福
     * @param doc
     * @param cCompareInfo
     * @param in_PdmName
     *            update 2016年5月2日
     */
    public void readXmlTable(Document doc, CompareInfo cCompareInfo, final String in_PdmName) {
        LOGGER.info("work with pdm:" + AiFileUtil.getFileName(in_PdmName));
        // 存储:id-column——每个列对应一个唯一的id号，通过id号便可知道对应的列
        Map<String, AiColumn> tableColumn = new HashMap<String, AiColumn>();
        // 存储:id-user——每个user对应一个唯一的id号
        Map<String, String> userColumn = new HashMap<String, String>();
        // 根据xpath方式得到所要得到xml文档的具体对象,根据分析解析xml文档可知，xml文档中含有前缀名
        Map<String, String> map = new HashMap<String, String>();
        // 是否是mysql版本
        boolean isMysql = false;
        // 获得所有的：id-user映射
        getXmlUser(doc, userColumn);

        // 解析文档是否是mysql或oracle类型
        isMysql = isMysql(doc);

        map.put("c", "collection");
        // 根据xml文档，//c:Table 即为得到的文档对象
        XPath path = doc.createXPath("//c:Tables");
        path.setNamespaceURIs(map);
        List<Element> list = path.selectNodes(doc);

        // 得到tables对象，该对象是该pdm文件中所有表的集合
        try {
            for (Element element : list) {
                for (Iterator<Element> iter = element.elementIterator("Table"); iter.hasNext();) {
                    Element table_s = iter.next();

                    // 通过一个table对象，得到user对象
                    String owner = getTableUser(table_s, userColumn);

                    if (owner == null) {
                        LOGGER.error("获得PDM字段列ERROR: " + "PDM name:" + OpenPdm.getLastPath() + "\n table_name "
                                + table_s.elementText("Code") + ":do not add Owner");

                        errInfoSb[5].append("<tr>").append("<th></th><td>").append(OpenPdm.getLastPath())
                                .append("</td><td>").append(table_s.elementText("Code"))
                                .append("</td><td></td><td></td><td>")
                                .append(table_s.elementText("Code") + " :do not add Owner").append("</td></tr>");

                        // LOGGER.debug(cCompareInfo.getAllMail());
                        // CompareUtil.errorSendMail(errInfoSb,
                        // getErrorMailTitle(), getErrorHtmlName(),
                        // cCompareInfo);
                        owner = "";
                    }
                    owner = owner.toLowerCase();
                    // 保证pdm中的user只出现一次
                    if (!userlist.contains(owner)) {
                        setUserlist(owner);
                    }
                    // 如果owner含有“+”，表达是有几个owner，将这几个owner的内容都拷贝一份
                    if (owner.contains("+")) {
                        String[] owners = owner.split("\\+");
                        for (int i = 0; i < owners.length; i++) {
                            if (owners[i] != null && owners[i] != "") {
                                if (!userlist.contains(owners[i])) {
                                    setUserlist(owners[i]);
                                }
                                readTableAndIndex(table_s, owners[i], tableColumn, isMysql,
                                        AiFileUtil.getFileName(in_PdmName));
                            }
                        }
                    } else {
                        readTableAndIndex(table_s, owner, tableColumn, isMysql, AiFileUtil.getFileName(in_PdmName));
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
       
    }

    /**
     * 
     * @category 解析pdm，用于pdm与pdm直接比较
     * @author 吴平福
     * @param cCompareInfo
     *            update 2016年5月2日
     */
    public void readPdmMap(CompareInfo cCompareInfo) {
        // 不需要转换数据类型
        isConver = false;
        Document doc = null;
        try {
            doc = OpenPdm.getPath(PDMPath);
            readXmlTable(doc, cCompareInfo, PDMPath);
        } catch (Exception ex) {
            ex.printStackTrace();

        }
        LOGGER.debug("the number of the Owner: " + domainTable.size());

    }

    /**
     * 
     * @category 解析pdm用于pdm与数据库比较
     * @author 吴平福
     * @param cCompareInfo
     * @throws Exception
     *             update 2016年5月2日
     */
    public void getPdmsMap(CompareInfo cCompareInfo) throws Exception {
        // 需要转换数据类型
        if (cCompareInfo.getDbType().equalsIgnoreCase("mysql")) {
            isConver = true;
        } else {
            isConver = false;
        }
        List<String> pdmFile = new ArrayList<String>();

        // 打开PDM文件,返回pdm路径

        OpenPdm.readFile(PDMPath, pdmFile);
        LOGGER.debug("the number of pdms is " + pdmFile.size());

        // 解析PDM,并且存入map中
        for (String docPath : pdmFile) {
            Document doc = null;
            try {
                LOGGER.info(docPath);
                doc = OpenPdm.getPath(docPath);
                // 将表信息存入 map容器——domainTable，将索引信息存入map容器——domainIndex;
                readXmlTable(doc, cCompareInfo, docPath);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        CompareMailsUtil.sendErrorMail(errInfoSb, getErrorMailTitle(), getErrorHtmlName(), cCompareInfo);
        // 打印表
        LOGGER.debug("PDM表的数量：" + domainTable.size());
        /*
        for (Map.Entry<String, TreeMap<String, Table>> entry : domainTable.entrySet()) {
            TreeMap<String, Table> map = new TreeMap<String, Table>();
            map = entry.getValue();
            for (Map.Entry<String, Table> entryT : map.entrySet()) {

                LOGGER.debug("表名：" + entryT.getKey() + "(");
                Table table_pdm = entryT.getValue();
                for (Iterator iter_column = table_pdm.columns.keySet().iterator(); iter_column.hasNext();) {
                    String key_column = (String) iter_column.next();
                    Column column_develop = (Column) table_pdm.columns.get(key_column);
                    LOGGER.debug(column_develop.getColumnName() + "  " + column_develop.getDataType() + " "
                            + column_develop.getColumnDefault() + " "
                            + column_develop.getNullable());

                }
            }

        }
        */
        // 打印索引
        LOGGER.debug("索引的数量：" + domainIndex.size()); 
        /*
        for (Map.Entry<String, TreeMap<String, Table>> entry : domainIndex
                .entrySet()) {

            TreeMap<String, Table> map = new TreeMap<String, Table>();
            map = entry.getValue();
            for (Map.Entry<String, Table> entryT : map.entrySet()) {
                LOGGER.debug("索引名：" + entryT.getKey());
                Table table_pdm = entryT.getValue();
                for (Iterator iter_column = table_pdm.indexs.keySet()
                        .iterator(); iter_column.hasNext();) {
                    // 获得开发库中的索引
                    String key_index = (String) iter_column.next();
                    TableIndex index_pdm = (TableIndex) table_pdm.indexs.get(key_index);
                    LOGGER.debug(index_pdm.getColNames() + " " + index_pdm.getConstraint_type());
                }

            }
        }
        */

    }

}
