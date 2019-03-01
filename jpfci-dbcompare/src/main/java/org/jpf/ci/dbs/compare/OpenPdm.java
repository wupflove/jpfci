package org.jpf.ci.dbs.compare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

public class OpenPdm {

    private static String lastPath;
    private static final Logger LOGGER = LogManager.getLogger();

    public static Document getPath(final String XMLPATH) throws IOException {

        // 替换掉xml中不可识别的字符
        LOGGER.debug("encodeing:" + System.getProperty("file.encoding"));
        SAXReader reader = new SAXReader();
        String path = null;

        Document doc = null;
        String line = "";
        File fileWriter = null;
        File file = null;
        if (XMLPATH.contains(File.separator)) {
            LOGGER.debug("last path:" + XMLPATH);
            path = XMLPATH.substring(0, XMLPATH.lastIndexOf(File.separator) + 1) + "tmp.xml";
            fileWriter = new File(path);
            LOGGER.debug("lastest path:" + path);
            setLastPath(XMLPATH);
        } else {
            fileWriter = new File(path);
            path = "tmp.xml";
            setLastPath(XMLPATH);
        }

        if (fileWriter.exists()) {
            fileWriter.delete();

        }
        fileWriter.createNewFile();

        FileOutputStream out = new FileOutputStream(fileWriter, true);

        File f = new File(XMLPATH);
        FileInputStream in = new FileInputStream(f);

        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        while ((line = br.readLine()) != null) {
            StringBuffer sb = new StringBuffer();
            line = line.replaceAll("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]", "");
            sb.append(line);
            out.write(sb.toString().getBytes("utf-8"));

        }
        br.close();
        out.close();
        file = new File(path);
        try {
            doc = reader.read(file);

            File fileD = new File(path);
            if (fileD.exists()) {
                fileD.delete();
            }

        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return doc;
    }

    // 得到一个目录下的所有pdm文件
    public static boolean readFile(String filePath, List<String> pdmFile) throws FileNotFoundException, IOException {

        try {
            File file = new File(filePath);
            if (!file.isDirectory()) {

                LOGGER.info("path=" + file.getPath());
                LOGGER.info("absolutepath=" + file.getAbsolutePath());
                LOGGER.info("name" + file.getName());
            } else if (file.isDirectory()) {
                String[] fileList = file.list();
                for (int i = 0; i < fileList.length; i++) {
                    File readFile = new File(filePath + File.separator + fileList[i]);
                    if (!readFile.isDirectory()) {
                        if (readFile.isFile() &&
                                ((readFile.getName().indexOf(".pdm") > -1)
                                        || (readFile.getName().indexOf(".PDM") > -1))) {
                            // System.out.println("path="+readFile.getPath());
                            pdmFile.add(readFile.getAbsolutePath());
                            // System.out.println("absolutepath="+readFile.getAbsolutePath());
                            LOGGER.info(readFile.getName());
                        }
                    } else if (readFile.isDirectory()) {

                        readFile(filePath + File.separator + fileList[i], pdmFile);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("readfile() Exception" + e.getMessage());
        }
        return true;
    }

    public static String getLastPath() {
        return lastPath;
    }

    public static String setLastPath(String xmlPath) {
        return lastPath = xmlPath;
    }
}
