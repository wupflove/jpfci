/**
 * @author 吴平福 E-mail:wupf@asiainfo.com
 * @version 创建时间：2016年7月16日 上午12:20:54 类说明
 */

package org.jpf.ci.gitlabs;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.utils.dbsql.AiDBUtil;
import org.jpf.utils.dbsql.AppConn;

/**
 * 
 */
public class IntGitLabPrjAdd {
  private static final Logger logger = LogManager.getLogger();

  private String DELETE_Owners = "delete from json_project_owners where id=?";
  private String DELETE_Projects = "delete from json_projects where id=?";
  private String INSERT_Owners =
      "insert into json_projects (id,description,default_branch,public,archived,visibility_level,ssh_url_to_repo,http_url_to_repo,web_url,name_with_namespace,path_with_namespace,created_at,last_activity_at,creator_id)             values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
  private String INSERT_Projects =
      "insert into json_project_owners (name,username,id,state,avatar_url) values(?,?,?,?,?)";

  /**
   * 
   */
  public IntGitLabPrjAdd() {
    // TODO Auto-generated constructor stub

    try {
      // TODO Auto-generated constructor stub
      for (int i = 1; i < 1129; i++) {

        ProjecInfo cProjecInfo = GitLabUtils.getPrjInfoById(i);
        if (cProjecInfo == null) {
          continue;
        }
        if (cProjecInfo.getId() == null) {
          continue;
        }
        Connection conn = null;
        try {
          conn = AppConn.GetInstance().GetConn(GitlabInterfaceConst.dbConection);
          PreparedStatement pStmt2 = null;
          PreparedStatement pStmt1 = null;
          if (conn != null) {
            // conn.setAutoCommit(false);
            pStmt1 = conn.prepareStatement(DELETE_Owners);
            pStmt2 = conn.prepareStatement(DELETE_Projects);
            // logger.debug(DELETE_Owners);

            PreparedStatement pStmt3 = conn.prepareStatement(INSERT_Owners);
            pStmt3.setString(1, cProjecInfo.getId());
            pStmt3.setString(2, cProjecInfo.getDescription());
            pStmt3.setString(3, cProjecInfo.getDefault_branch());
            pStmt3.setString(4, cProjecInfo.getPublic());
            pStmt3.setString(5, cProjecInfo.getArchived());
            pStmt3.setString(6, cProjecInfo.getVisibility_level());
            pStmt3.setString(7, cProjecInfo.getHttp_url_to_repo());
            pStmt3.setString(8, cProjecInfo.getSsh_url_to_repo());
            pStmt3.setString(9, cProjecInfo.getWeb_url());
            pStmt3.setString(10, cProjecInfo.getName_with_namespace());
            pStmt3.setString(11, cProjecInfo.getPath_with_namespace());
            pStmt3.setString(12, cProjecInfo.getCreated_at());
            pStmt3.setString(13, cProjecInfo.getLast_activity_at());
            pStmt3.setString(14, cProjecInfo.getCreator_id());
            if (cProjecInfo.getOwner() != null) {
              PreparedStatement pStmt4 = conn.prepareStatement(INSERT_Projects);
              pStmt4.setString(1, cProjecInfo.getOwner()[0].getName());
              pStmt4.setString(2, cProjecInfo.getOwner()[0].getUsername());
              pStmt4.setString(3, cProjecInfo.getOwner()[0].getId());
              pStmt4.setString(4, cProjecInfo.getOwner()[0].getState());
              pStmt4.setString(5, cProjecInfo.getOwner()[0].getAvatar_url());
              pStmt4.executeUpdate();
            }
            pStmt1.setString(1, cProjecInfo.getId());
            pStmt1.executeUpdate();
            pStmt2.setString(1, cProjecInfo.getId());
            pStmt2.executeUpdate();
            pStmt3.executeUpdate();

            // logger.info("onwers={" + cProjecInfo.getOwner()[0].toString() + "}");
            logger.info(cProjecInfo.toString());


            conn.commit();
          }
        } catch (Exception ex) {
          // TODO: handle exception
          ex.printStackTrace();
        } finally {
          AiDBUtil.doClear(conn);
        }
      }
    } catch (Exception ex) {
      // TODO: handle exception
      ex.printStackTrace();
    }

  }

  /**
   * 
   * @category @author 吴平福
   * @param args update 2016年7月16日
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    IntGitLabPrjAdd cIntGitLabPrjAdd = new IntGitLabPrjAdd();
  }


}
