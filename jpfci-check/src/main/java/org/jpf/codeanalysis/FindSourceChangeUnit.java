/**
 * @author wupf@asiainfo. com 查找代码对应的单元测试
 */
package org.jpf.codeanalysis;

import java.util.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.codeanalysis.statsvn.SvnDoDiff;
import org.jpf.utils.AiDateTimeUtil;
import org.jpf.utils.cvsutil.JpfCvsUtil;
import org.jpf.utils.ios.AiFileUtil;

import java.io.File;
import java.io.IOException;

public class FindSourceChangeUnit {
    private static final Logger logger = LogManager.getLogger();

    int iThreadNum = 0;

    /**
     * 
     * @category @author 吴平福
     * @return
     * @throws IOException update 2017年5月18日
     */
    public static String getCurrentPath() throws IOException {
        File directory = new File("");// 参数为空
        return directory.getCanonicalPath();

    }

    public FindSourceChangeUnit() {

    }

    /**
     * 
     * @param strSvnUrl
     */
    public void doAnalysisBySVN(String strSvnUrl, String strStartDateTime, String strEndDateTime) {

        logger.info(strSvnUrl);
        logger.info(strStartDateTime);
        logger.info(strEndDateTime);
        // TODO Auto-generated constructor stub
        int iModifyFileCount = 0;
        int iDeleteFileCount = 0;
        int iNewFileCount = 0;
        try {
            Vector<String> vChangeMethods = new Vector<String>();

            Vector<SvnChangeInfo> vChangeInfos = new Vector<SvnChangeInfo>();
            String UserName = "liaocj";
            String PassWord = "liaocj";

            SvnDoDiff.getSvnDiff(strSvnUrl, strStartDateTime, strEndDateTime, vChangeInfos,
                    UserName, PassWord);
            /*
             * RevisionInfo.setNew_Revison(SVNUtil.getSvnRevisionByDate(strSvnUrl, UserName,
             * PassWord, AiDateTimeUtil.StrToDate(strEndDateTime)));
             * RevisionInfo.setOld_Revison(SVNUtil.getSvnRevisionByDate(strSvnUrl, UserName,
             * PassWord, AiDateTimeUtil.StrToDate(strStartDateTime)));
             * RevisionInfo.setSvnUrl(strSvnUrl);
             */
            // clear tmp path
            AiFileUtil.delDirWithFiles(AiFileUtil.getCurrentPath() + File.separator + "tmp");
            AiFileUtil.delDirWithFiles(AiFileUtil.getCurrentPath() + File.separator + "tmp2");

            AiFileUtil.mkdir(AiFileUtil.getCurrentPath() + File.separator + "tmp");
            AiFileUtil.mkdir(AiFileUtil.getCurrentPath() + File.separator + "tmp2");

            logger.info("Thread.activeCount()= " + Thread.activeCount());
            for (int i = 0; i < vChangeInfos.size(); i++) {
                // logger.info("Thread.activeCount()= " + Thread.activeCount());
                iThreadNum++;
                SvnChangeInfo cSvnChangeInfo = (SvnChangeInfo) vChangeInfos.get(i);
                if (cSvnChangeInfo.getChangeType().equalsIgnoreCase("M")) {
                    // 变化类
                    iModifyFileCount++;

                    ClassModify cClassModify = new ClassModify(vChangeMethods, cSvnChangeInfo,
                            strStartDateTime, strEndDateTime, UserName, PassWord, iThreadNum);
                    cClassModify.setName("ClassModify" + iThreadNum);
                    cClassModify.start();
                    /*
                     * Thread thread = new Thread1( Thread.currentThread().getThreadGroup(),
                     * "thread1"); thread.start();
                     */
                }
                // logger.info("Thread.activeCount()= " + Thread.activeCount());
                if (cSvnChangeInfo.getChangeType().equalsIgnoreCase("A")) {
                    // 增加类
                    iNewFileCount++;
                    ClassAddNew cClassAddNew = new ClassAddNew(vChangeMethods, cSvnChangeInfo,
                            strEndDateTime, UserName, PassWord, iThreadNum);
                    cClassAddNew.setName("ClassAddNew" + iThreadNum);
                    cClassAddNew.start();
                }
                if (cSvnChangeInfo.getChangeType().equalsIgnoreCase("D")) {
                    // 删除类
                    iDeleteFileCount++;
                    ClassDelete cClassDelete = new ClassDelete(vChangeMethods, cSvnChangeInfo,
                            strStartDateTime, UserName, PassWord, iThreadNum);
                    cClassDelete.setName("ClassDelete" + iThreadNum);
                    cClassDelete.start();
                }
                // System.out.println("summary:DeleteFileCount=" + iDeleteFileCount);
                // System.out.println("summary:ModifyFileCount=" + iModifyFileCount);
                // System.out.println("summary:NewFileCount=" + iNewFileCount);
            }

            while (Thread.activeCount() > 1) {
                try {
                    // 主线程等待子线程执行
                    Thread.sleep(2000);
                    // logger.info("Thread.activeCount()= " + Thread.activeCount());
                    // logger.info("SvnCheckOutUtil.getSVNClientManager()= " +
                    // SvnCheckOutUtil.getSVNClientManager());
                    if (SvnCheckOutUtil.getSVNClientManager() && Thread.activeCount() == 2) {
                        break;
                    }
                    // getThreads();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            int iAddNewMethodCount = 0;
            for (int i = 0; i < vChangeMethods.size(); i++) {
                if (vChangeMethods.get(i).startsWith("addnew:")) {
                    System.out.println(vChangeMethods.get(i));
                    iAddNewMethodCount++;
                }
            }


            int iChangeMethodCount = 0;
            for (int i = 0; i < vChangeMethods.size(); i++) {
                if (vChangeMethods.get(i).startsWith("change:")) {
                    System.out.println(vChangeMethods.get(i));
                    iChangeMethodCount++;
                }
            }


            int iDeleteMethodCount = 0;
            for (int i = 0; i < vChangeMethods.size(); i++) {
                if (vChangeMethods.get(i).startsWith("delete:")) {
                    System.out.println(vChangeMethods.get(i));
                    iDeleteMethodCount++;
                }
            }
            System.out.println("summary: " + strSvnUrl);
            System.out.println("summary: start=" + strStartDateTime);
            System.out.println("summary: end=" + strEndDateTime);
            System.out.println("summary: addnew method count:" + iAddNewMethodCount);
            System.out.println("summary: change method count:" + iChangeMethodCount);
            System.out.println("summary: delete method count:" + iDeleteMethodCount);
            System.out.println("summary:total methods=" + vChangeMethods.size());

            System.out.println("summary:DeleteFileCount=" + iDeleteFileCount);
            System.out.println("summary:ModifyFileCount=" + iModifyFileCount);
            System.out.println("summary:NewFileCount=" + iNewFileCount);

            logger.info("write result to abc.csv");
            JpfCvsUtil.appendCsv("abc.csv",
                    strSvnUrl + "\t" + strStartDateTime + "\t" + iAddNewMethodCount + "\t"
                            + iChangeMethodCount + "\t" + iDeleteMethodCount + "\t"
                            + vChangeMethods.size() + "\t" + iDeleteFileCount + "\t"
                            + iModifyFileCount + "\t" + iNewFileCount + "\n");
        } catch (Exception ex) {
            // TODO: handle exception
        }
    }

    public static void main(String[] args) {
        if (args.length == 1) {
            long start = System.currentTimeMillis();
            String strStartDateTime = AiDateTimeUtil.getCurrentDay(-1) + " 00:00:00";
            String strEndDateTime = AiDateTimeUtil.getCurrentDay(0) + " 00:00:00";
            FindSourceChangeUnit cFindSourceChangeUnit = new FindSourceChangeUnit();
            cFindSourceChangeUnit.doAnalysisBySVN(args[0], strStartDateTime, strEndDateTime);
            // new
            // UCaller().callHierachyOfWorkspaceProject("org.jpf.codeanalysis.hierarchys.actions.FindMethodBase.getTextFromFile()",
            // "1");
            logger.info(" ExcuteTime " + (System.currentTimeMillis() - start));
        } else if (args.length == 3) {
            try {
                String strStartDateTime = args[1];
                String strEndDateTime = args[2];
                long iDays = AiDateTimeUtil.getBetweenDays(strEndDateTime,
                        AiDateTimeUtil.getCurrentDay(0));
                if (iDays >= 0) {
                    strEndDateTime = AiDateTimeUtil.getCurrentDay(0);
                }
                iDays = AiDateTimeUtil.getBetweenDays(strStartDateTime, strEndDateTime);
                FindSourceChangeUnit cFindSourceChangeUnit = new FindSourceChangeUnit();
                for (int i = 1; i < iDays; i++) {
                    String strCurrStartDateTime = AiDateTimeUtil
                            .getDateAddDay(strStartDateTime + " 00:00:00", i - 1, "yyyy-MM-dd");
                    String strCurrEndDateTime = AiDateTimeUtil
                            .getDateAddDay(strStartDateTime + " 00:00:00", i, "yyyy-MM-dd");
                    cFindSourceChangeUnit.doAnalysisBySVN(args[0], strCurrStartDateTime,
                            strCurrEndDateTime);
                }

            } catch (Exception ex) {
                // TODO: handle exception
                ex.printStackTrace();
            }


        } else {
            logger.warn("error input param");
        }
    }


}
