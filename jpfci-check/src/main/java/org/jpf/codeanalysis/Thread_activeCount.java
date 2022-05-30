/** 
* @author 吴平福 
* E-mail:421722623@qq.com 
* @version 创建时间：2017年7月21日 上午11:25:14 
* 类说明 
*/ 

package org.jpf.codeanalysis;


import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
  
public class Thread_activeCount
{
    private static final Logger logger = LogManager.getLogger();
    public static void main(String[] args) throws Exception {
        
        logger.info("main thread name : " + Thread.currentThread().getName());
        Thread thread = new Thread1(
            Thread.currentThread().getThreadGroup(), "thread1");
        thread.start();
        System.out.println("thread name : " + thread.getName());
        logger.info("thread count : " +  Thread.activeCount());
        SvnChangeInfo cSvnChangeInfo=new SvnChangeInfo(); 
        cSvnChangeInfo.setChangeFileName("http://svn/products/openboss/src/main/java/com/crm/center/ams/inter/out/service/impl/Am2UmmpSVImpl.java");
        Vector<String> vChangeMethods = new Vector<String>();
        ClassModify cClassModify= new ClassModify(vChangeMethods, cSvnChangeInfo, "2017-05-02",
                "2017-05-02", "", "",1);
        cClassModify.setName("ClassModify1");
        cClassModify.start();
        logger.info("Thread.activeCount()= " + Thread.activeCount());
        while (Thread.activeCount() > 1) {
            try {
                // 主线程等待子线程执行
                Thread.sleep(3000);
                logger.info("Thread.activeCount()= " + Thread.activeCount());
                logger.info(SvnCheckOutUtil.getSVNClientManager());
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    private static final class Thread1 extends Thread {
        public Thread1(ThreadGroup group, String name) {
            super(group, name);
        }
        
        public void run() {
            System.out.println(">> Thread1 run begin ");
            try {
                Thread.sleep(20000);
            } catch (Exception e) {
                System.out.println("Ex: " + e.getMessage());
            }
            System.out.println(">> Thread1 run end ");
        }
    }
}