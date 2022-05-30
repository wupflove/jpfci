/**
 * @author 吴平福 E-mail:421722623@qq.com
 * @version 创建时间：2016年8月17日 下午5:12:24 类说明
 */

package org.jpf.ci.checks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 */
public class StatNumForMethod {
    
    private static final Logger logger = LogManager.getLogger();
    private final String FILE_TYPE=".java";
    /**
     * 
     */
    public StatNumForMethod() {
        // TODO Auto-generated constructor stub
        File dir = new File("D:/jworkspaces/jpfapp/src");// 要统计的java包的绝对路径

        ergodicDir(dir);
    }

    /**
     * @category @author 吴平福
     * @param args update 2016年8月17日
     */

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        StatNumForMethod cStatNumForMethod=new StatNumForMethod();
    }

    /**
     * 遍历目录下的所有java文件
     * 
     * @param dir
     */
    private  void ergodicDir(File dir) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    ergodicDir(file);
                }

                if (file.isFile() && file.getName().endsWith(FILE_TYPE)) {
                    statInFile(file);
                }

            }
        }
    }


    private  void statInFile(File file) {
        BufferedReader reader = null;
        try {
            // 读一个文件输入流
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            Stack<String[]> stack = new Stack<String[]>();
            int lineNum = 0;

            while ((line = reader.readLine()) != null) {// 遍历行
                lineNum++;
                // 遇到{时把当前行和行号作为数组的元素进栈，遇到}时计算出方法的行数并打印行，出栈
                int matchNum = getOccur(line, "{") - getOccur(line, "}");// 一行可能有多个{或}
                if (matchNum > 0) {
                    for (int i = 0; i < matchNum; i++) {
                        stack.push(new String[] {line, lineNum + ""});
                    }
                } else {
                    for (int i = 0; i < -matchNum; i++) {
                        if (stack.isEmpty()) {
                            logger.info(file.getPath() + "文件的{}不匹配，不能统计行数。。。当前的行号是" + lineNum);
                            return;
                        }
                        String[] popArr = stack.pop();
                        if (stack.size() == 1) {// 当栈大小为1时说明一个方法结束了
                            int beginNum = Integer.parseInt(popArr[1]);// 方法开始的行号
                            if (lineNum - beginNum > 100) {// 方法内的代码超过100行
                                logger.info(file.getPath() + "文件的" + popArr[0] + "方法(第"
                                        + beginNum + "行)多达" + (lineNum - beginNum + 1) + "行");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * src 源，find 查找目标,返回的就是找到的数目
     * 
     * @param src 源
     * @param find 查找目标
     * @return 找到的数目
     */
    private  int getOccur(String src, String find) {
        int o = 0;
        int index = -1;
        while ((index = src.indexOf(find, index)) > -1) {
            ++index;
            ++o;
        }
        return o;
    }
}
