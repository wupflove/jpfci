/** 
* @author 吴平福 
* E-mail:421722623@qq.com 
* @version 创建时间：2017年7月19日 上午2:52:22 
* 类说明 
*/ 

package org.jpf.sonar;

import java.lang.reflect.Array;  
import java.lang.reflect.Field;  
import java.lang.reflect.Modifier;  
import java.util.AbstractMap;  
import java.util.ArrayList;  
import java.util.Collection;  
import java.util.HashMap;  
import java.util.HashSet;  
import java.util.Hashtable;  
import java.util.Iterator;  
import java.util.List;  
import java.util.Map;  
import java.util.Set;  
  
/** 
 * The TypeUtil class static methods for inspecting complex java types. 
 * The typeToString() method is used to dump the contents of a passed object  
 * of any type (or collection) to a String.  This can be very useful for debugging code that 
 * manipulates complex structures. 
 * 
 * 
 * @version $Revision : 1.2.6.4 $ 
 */  
  
/** 
 *  
 * @项目名称 :ProtectSystem 
 * @文件名称 :TypeUtil.java 
 * @所在包 :cn.yws.activity.util 
 * @功能描述 :对象属性变量值查看工具，这个是从网上找的，便于像php一样，直接查看对象里面的属性值，而不用每次都单步调试 这里利用到了java的反射机制 
 * @创建日期 :2012-5-19 
 */  
public class TypeUtil {  
  
    /** 
     * Returns a string holding the contents of the passed object, 
     *  
     * @param scope 
     *            String 
     * @param parentObject 
     *            Object 
     * @param visitedObjs 
     *            List 
     * @return String 
     */  
    @SuppressWarnings({ "rawtypes" })  
    private static String complexTypeToString( 
            Object parentObject, List visitedObjs) {  
        StringBuffer buffer = new StringBuffer("");  
        try {  
            //  
            // Ok, now we need to reflect into the object and add its child  
            // nodes...  
            //  
  
            Class cl = parentObject.getClass();  
            while (cl != null) {  
  
                processFields(cl.getDeclaredFields(),  parentObject,  
                        buffer, visitedObjs);  
  
                cl = cl.getSuperclass();  
            }  
        } catch (IllegalAccessException iae) {  
            buffer.append(iae.toString());  
        }  
  
        return (buffer.toString());  
    }  
  
    /** 
     * Method processFields 
     *  
     * @param fields 
     *            Field[] 
     * @param scope 
     *            String 
     * @param parentObject 
     *            Object 
     * @param buffer 
     *            StringBuffer 
     * @param visitedObjs 
     *            List 
     * @throws IllegalAccessException 
     */  
    @SuppressWarnings({ "rawtypes" })  
    private static void processFields(Field[] fields,  
            Object parentObject, StringBuffer buffer, List visitedObjs)  
            throws IllegalAccessException {  
        for (int i = 0; i < fields.length; i++) {  
            //  
            // Disregard certain fields for IDL structures  
            //  
            System.out.println(fields[i].getName());
            if (fields[i].getName().equals("__discriminator")  
                    || fields[i].getName().equals("__uninitialized")) {  
                continue;  
            }  
  
            //  
            // This allows us to see non-public fields. We might need to deal  
            // with some  
            // SecurityManager issues here once it is outside of VAJ...  
            //  
            fields[i].setAccessible(true);  
            if (Modifier.isStatic(fields[i].getModifiers())) {  
                //  
                // Ignore all static members. The classes that this dehydrator  
                // is  
                // meant to handle are simple data objects, so static members  
                // have no  
                // bearing....  
                //  
            } else {  
                buffer.append(typeToString(  fields[i].getName(),  
                        fields[i].get(parentObject), visitedObjs));  
                System.out.println(typeToString(fields[i].getName(),  
                        fields[i].get(parentObject), visitedObjs));
            }  
        }  
    }  
  
    /** 
     * Method isCollectionType 
     *  
     * @param obj 
     *            Object 
     * @return boolean 
     */  
  
    public static boolean isCollectionType(Object obj) {  
  
        return (obj.getClass().isArray() || (obj instanceof Collection)  
                || (obj instanceof Hashtable) || (obj instanceof HashMap)  
                || (obj instanceof HashSet) || (obj instanceof List) || (obj instanceof AbstractMap));  
    }  
  
    /** 
     * Method isComplexType 
     *  
     * @param obj 
     *            Object 
     * @return boolean 
     */  
      
    @SuppressWarnings("rawtypes")  
    public static boolean isComplexType(Object obj) {  
  
        if (obj instanceof Boolean || obj instanceof Short  
                || obj instanceof Byte || obj instanceof Integer  
                || obj instanceof Long || obj instanceof Float  
                || obj instanceof Character || obj instanceof Double  
                || obj instanceof String) {  
  
            return false;  
        } else {  
            Class objectClass = obj.getClass();  
  
            if (objectClass == boolean.class || objectClass == Boolean.class  
                    || objectClass == short.class || objectClass == Short.class  
                    || objectClass == byte.class || objectClass == Byte.class  
                    || objectClass == int.class || objectClass == Integer.class  
                    || objectClass == long.class || objectClass == Long.class  
                    || objectClass == float.class || objectClass == Float.class  
                    || objectClass == char.class  
                    || objectClass == Character.class  
                    || objectClass == double.class  
                    || objectClass == Double.class  
                    || objectClass == String.class) {  
                return false;  
            }  
  
            else {  
                return true;  
            }  
        }  
    }  
  
    /** 
     * Returns a string holding the contents of the passed object, 
     *  
     * @param scope 
     *            String 
     * @param obj 
     *            Object 
     * @param visitedObjs 
     *            List 
     * @return String 
     */  
    @SuppressWarnings({  "rawtypes", "unused" })  
    private static String collectionTypeToString(Object obj,  
            List visitedObjs) {  
        StringBuffer buffer = new StringBuffer("");  
        if (obj.getClass().isArray()) {  
            if (Array.getLength(obj) > 0) {  
                for (int j = 0; j < Array.getLength(obj); j++) {  
                    Object x = Array.get(obj, j);  
                    buffer.append(typeToString( "[" + j + "]", x,  
                            visitedObjs));  
                }  
            } else {  
                buffer.append( "[]: empty\n");  
            }  
        } else {  
            boolean isCollection = (obj instanceof Collection);  
            boolean isHashTable = (obj instanceof Hashtable);  
            boolean isHashMap = (obj instanceof HashMap);  
            boolean isHashSet = (obj instanceof HashSet);  
            boolean isAbstractMap = (obj instanceof AbstractMap);  
            boolean isMap = isAbstractMap || isHashMap || isHashTable;  
            if (isMap) {  
                Set keySet = ((Map) obj).keySet();  
                Iterator iterator = keySet.iterator();  
                int size = keySet.size();  
                if (size > 0) {  
                    for (int j = 0; iterator.hasNext(); j++) {  
                        Object key = iterator.next();  
                        Object x = ((Map) obj).get(key);  
                        buffer.append(typeToString( "[" + key + "]", x,  
                                visitedObjs));  
                    }  
                } else {  
                    buffer.append( "[]: empty\n");  
                }  
            } else if (/* isHashTable || */  
            isCollection || isHashSet /* || isHashMap */  
            ) {  
                Iterator iterator = null;  
                int size = 0;  
                if (obj != null) {  
                    if (isCollection) {  
                        iterator = ((Collection) obj).iterator();  
                        size = ((Collection) obj).size();  
                    } else if (isHashTable) {  
                        iterator = ((Hashtable) obj).values().iterator();  
                        size = ((Hashtable) obj).size();  
                    } else if (isHashSet) {  
                        iterator = ((HashSet) obj).iterator();  
                        size = ((HashSet) obj).size();  
                    } else if (isHashMap) {  
                        iterator = ((HashMap) obj).values().iterator();  
                        size = ((HashMap) obj).size();  
                    }  
                    if (size > 0) {  
                        for (int j = 0; iterator.hasNext(); j++) {  
                            Object x = iterator.next();  
                            buffer.append(typeToString( "[" + j + "]",  
                                    x, visitedObjs));  
                        }  
                    } else {  
                        buffer.append( "[]: empty\n");  
                    }  
                } else {  
                    //  
                    // theObject is null  
                    buffer.append( "[]: null\n");  
                }  
            }  
        }  
        return (buffer.toString());  
    }  
  
    /** 
     * Method typeToString 
     *  
     * @param scope 
     *            String 
     * @param obj 
     *            Object 
     * @param visitedObjs 
     *            List 
     * @return String 
     */  
    @SuppressWarnings({ "rawtypes", "unchecked" })  
    private static String typeToString(String scope, Object obj,  
            List visitedObjs) {  
  
        if (obj == null) {  
            return (scope + ": null\n");  
        } else if (isCollectionType(obj)) {  
            return collectionTypeToString( obj, visitedObjs);  
        } else if (isComplexType(obj)) {  
            if (!visitedObjs.contains(obj)) {  
                visitedObjs.add(obj);  
                return complexTypeToString( obj, visitedObjs);  
            } else {  
                return (scope + ": <already visited>\n");  
            }  
        } else {  
            return (scope + ": " + obj.toString() + "\n");  
        }  
    }  
  
    /** 
     * The typeToString() method is used to dump the contents of a passed object 
     * of any type (or collection) to a String. This can be very useful for 
     * debugging code that manipulates complex structures. 
     *  
     * @param scope 
     * @param obj 
     *  
     * @return String 
     *  
     */  
  
    @SuppressWarnings("rawtypes")  
    public static String typeToString(Object obj) {  
  
        if (obj == null) {  
            return ( ": null\n");  
        } else if (isCollectionType(obj)) {  
            return collectionTypeToString(  obj, new ArrayList());  
        } else if (isComplexType(obj)) {  
            return complexTypeToString( obj, new ArrayList());  
        } else {  
            return ( ": " + obj.toString() + "\n\r");  
        }  
    }  
    public class SMSModel{  
        private Integer id;  
        private String address="广西大学西校园10栋204 宿舍";  
        private String name;  
        private String descript;  
        private String time;  
        private String number;  
        private String score;  
          
        public SMSModel(){};  
          
        public SMSModel(Integer id, String address, String name,  
                String descript, String time, String number, String score) {  
            super();  
            this.id = id;  
            this.address = address;  
            this.name = name;  
            this.descript = descript;  
            this.time = time;  
            this.number = number;  
            this.score = score;  
        }  
          
    }  
  
    public static void main(String[] args)throws Exception {  
           
         SMSModel model=new TypeUtil().new SMSModel(12, "广西大学西校园10栋204 宿舍", "云守护",  
                 "我在参加信息安全大赛呢", "2012-5-19", "1", "100");  
         System.out.println(TypeUtil.typeToString( model));
         
         String abc="org.jpf.sonar.PassWord";
         System.out.println(TypeUtil.typeToString( Class.forName(abc)));
    }  
} 
