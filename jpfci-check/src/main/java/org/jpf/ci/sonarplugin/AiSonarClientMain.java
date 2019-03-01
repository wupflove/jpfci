/** 
* @author 吴平福 
* E-mail:wupf@asiainfo.com 
* @version 创建时间：2017年7月16日 上午9:24:35 
* 类说明 
*/ 

package org.jpf.ci.sonarplugin;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jpf.utils.logUtil.TextAreaLogAppender;

import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * 
 */
public class AiSonarClientMain extends JFrame  implements ActionListener {

    private JPanel contentPane;
    JScrollPane scrollPane = new JScrollPane();
    JButton button1 = new JButton("...");// 选择  
    JButton button2 = new JButton("...");// 选择  
    JButton button3 = new JButton("...");// 选择  
    JButton button4 = new JButton("本地同步到服务器");// 选择  
    
    JTextField text1 = new JTextField("10.1.234.74");// TextField 目录的路径
    JTextField text2 = new JTextField("E:\\zgb\\原始文件\\工作室\\7.17\\两朵会11");// TextField 目录的路径
    JTextField text3 = new JTextField("e:\\zgb");// TextField 目录的路径
    
    JFileChooser jfc = new JFileChooser();// 文件选择器
    
    JTextArea textArea = new JTextArea();
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    AiSonarClientMain frame = new AiSonarClientMain();
                    frame.setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init()
    {
        try {
            TextAreaLogAppender.setTextAreaLogAppender(textArea);
        } catch (Exception ex) {
            // TODO: handle exception
        }
    }
    /**
     * Create the frame.
     */
    public AiSonarClientMain() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 550, 700);
        setTitle("Asiainfo SONAR Client");
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        

        contentPane.add(scrollPane, BorderLayout.SOUTH);
        
        textArea.setRows(30);
        textArea.setEditable(false);
        textArea.setAutoscrolls(true);
        scrollPane.setViewportView(textArea);
        
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        contentPane.add(tabbedPane, BorderLayout.CENTER);
        Container con = new Container();//  
        JLabel label1 = new JLabel("服务器地址");  
        JLabel label2 = new JLabel("标题文件目录");  
        JLabel label3 = new JLabel("缓存文件保存目录");  

        label1.setBounds(10, 10, 120, 20);  
        text1.setBounds(125, 10, 320, 20);
        button1.setBounds(450, 10, 50, 20); 
        
        label2.setBounds(10, 35, 120, 20);  
        text2.setBounds(125, 35, 320, 20);  
        button2.setBounds(450, 35, 50, 20);  

        label3.setBounds(10, 60, 120, 20);  
        text3.setBounds(125, 60, 320, 20);  
        button3.setBounds(450, 60, 50, 20); 
        
        button4.setBounds(10, 85, 150, 20);
        button1.addActionListener(this); // 添加事件处理  
        button2.addActionListener(this); // 添加事件处理  
        button3.addActionListener(this); // 添加事件处理  
        button4.addActionListener(this); // 添加事件处理  
        con.add(label1);
        con.add(text1);
        con.add(button1);
        
        con.add(label2);
        con.add(text2);
        con.add(button2);
        
        con.add(label3);
        con.add(text3);
        con.add(button3);
        con.add(button4);

        Container con2 = new Container();//  
        
        JLabel label11 = new JLabel("输入商品关键字");  
        JTextField text11 = new JTextField();// TextField 目录的路径
        
        label11.setBounds(10, 10, 120, 20);  
        text11.setBounds(125, 10, 320, 20);
        
        con2.add(label11) ;
        con2.add(text11) ;
        
        tabbedPane.add("和服务器SONAR同步", con);// 添加布局1 
        tabbedPane.add("和本地SONAR同步", con2);// 添加布局1 
        
        init();
    }
    
    /** 
     * 时间监听的方法 
     */  
    public void actionPerformed(ActionEvent e) {  
        // TODO Auto-generated method stub  
        if (e.getSource().equals(button1)) {// 判断触发方法的按钮是哪个  
            jfc.setFileSelectionMode(1);// 设定只能选择到文件夹  
            int state = jfc.showOpenDialog(null);// 此句是打开文件选择器界面的触发语句  
            if (state == 1) {  
                return;  
            } else {  
                File f = jfc.getSelectedFile();// f为选择到的目录  
                text1.setText(f.getAbsolutePath());  
            }  
        }  
        // 绑定到选择文件，先择文件事件  
        if (e.getSource().equals(button2)) {  
            jfc.setFileSelectionMode(1);// 设定只能选择到文件  
            int state = jfc.showOpenDialog(null);// 此句是打开文件选择器界面的触发语句  
            if (state == 1) {  
                return;// 撤销则返回  
            } else {  
                File f = jfc.getSelectedFile();// f为选择到的文件  
                text2.setText(f.getAbsolutePath());  
            }  
        }  
        // 绑定到选择文件，先择文件事件  
        if (e.getSource().equals(button3)) {  
            jfc.setFileSelectionMode(1);// 设定只能选择到文件  
            int state = jfc.showOpenDialog(null);// 此句是打开文件选择器界面的触发语句  
            if (state == 1) {  
                return;// 撤销则返回  
            } else {  
                File f = jfc.getSelectedFile();// f为选择到的文件  
                text3.setText(f.getAbsolutePath());  
            }  
        }  
        if (e.getSource().equals(button4)) {  
            // 弹出对话框可以改变里面的参数具体得靠大家自己去看，时间很短  
            JOptionPane.showMessageDialog(null, text2.getText()+" "+text1.getText(), "提示", 2);  
            TextAreaLogAppender.clearLog();
            long start = System.currentTimeMillis();

            try {
            } catch (Exception ex) {
                // TODO: handle exception
                ex.printStackTrace();
            }
            TextAreaLogAppender.log ("ExcuteTime " + (System.currentTimeMillis() - start) + "ms");
        }  
    }  
    private void doWork_a()
    {
        
    }

}
