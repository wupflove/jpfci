package org.jpf.ci.dbs.compare;

/**
 * 
 */
import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AiTableInfoMainAndSub {

	private static HashMap<String, String> parent_child1 = new HashMap<String, String>();

    public static HashMap<String, String> getParentChild() {
        return parent_child1;
    }
    /**
     * @category 从XML中读取信息
     * @param nlParentChild
     */
    public static void add(NodeList nlParentChild)
    {
        // 获得需要进行特殊处理的分表——母表对应关系
        parent_child1.clear();
        for (int i = 0; i < nlParentChild.getLength(); i++) {
            Node child = nlParentChild.item(i);
            if (child instanceof Element) {
                String s = child.getFirstChild().getNodeValue().toLowerCase().trim();
                String[] s1 = s.split(";");
                if (s1.length == 2) {
                	parent_child1.put(s1[1], s1[0]);
                }
            }
        }
    }
}
