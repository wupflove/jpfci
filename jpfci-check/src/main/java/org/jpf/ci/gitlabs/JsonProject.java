package org.jpf.ci.gitlabs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.jpf.utils.dbsql.AiDBUtil;
import org.jpf.utils.dbsql.AppConn;


public class JsonProject {
  private final Logger logger = LogManager.getLogger();
  private String DELETE_Owners = "delete from json_project_owners where id=?";
  private String DELETE_Projects = "delete from json_projects where id=?";
  private String INSERT_Owners =
      "insert into json_projects (id,description,default_branch,public,archived,visibility_level,ssh_url_to_repo,http_url_to_repo,web_url,name_with_namespace,path_with_namespace,created_at,last_activity_at,creator_id)             values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
  private String INSERT_Projects =
      "insert into json_project_owners (name,username,id,state,avatar_url) values(?,?,?,?,?)";

  /**
   * '
   * 
   * @param args
   * @throws Exception
   */
  public JsonProject() throws Exception {
    Connection conn = null;
    try {


      String address = GitLabUtils.getContent(
          "http://10.1.130.29/api/v3/projects/all?visibility=public&private_token=bcZ9nuy_rYseRQ3sy8Dw");
      logger.info(address);
      String data = address;

      ObjectMapper mapper = new ObjectMapper().setVisibility(JsonMethod.FIELD, Visibility.ANY);
      mapper.configure(Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
      mapper.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
      mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      List<ProjecInfo> list = mapper.readValue(data, new TypeReference<List<ProjecInfo>>() {});
      logger.info(list.size());

      PreparedStatement pStmt2 = null;
      PreparedStatement pStmt1 = null;
      conn = AppConn.GetInstance().GetConn(GitlabInterfaceConst.dbConection);
      if (conn != null) {
        // conn.setAutoCommit(false);
        pStmt1 = conn.prepareStatement(DELETE_Owners);
        pStmt2 = conn.prepareStatement(DELETE_Projects);
        logger.debug(DELETE_Owners);

        for (int i = 0; i < list.size(); i++) {


          PreparedStatement pStmt3 = conn.prepareStatement(INSERT_Owners);
          pStmt3.setString(1, list.get(i).getId());
          pStmt3.setString(2, list.get(i).getDescription());
          pStmt3.setString(3, list.get(i).getDefault_branch());
          pStmt3.setString(4, list.get(i).getPublic());
          pStmt3.setString(5, list.get(i).getArchived());
          pStmt3.setString(6, list.get(i).getVisibility_level());
          pStmt3.setString(7, list.get(i).getHttp_url_to_repo());
          pStmt3.setString(8, list.get(i).getSsh_url_to_repo());
          pStmt3.setString(9, list.get(i).getWeb_url());
          pStmt3.setString(10, list.get(i).getName_with_namespace());
          pStmt3.setString(11, list.get(i).getPath_with_namespace());
          pStmt3.setString(12, list.get(i).getCreated_at());
          pStmt3.setString(13, list.get(i).getLast_activity_at());
          pStmt3.setString(14, list.get(i).getCreator_id());
          if (list.get(i).getOwner() != null) {
            PreparedStatement pStmt4 = conn.prepareStatement(INSERT_Projects);
            pStmt4.setString(1, list.get(i).getOwner()[0].getName());
            pStmt4.setString(2, list.get(i).getOwner()[0].getUsername());
            pStmt4.setString(3, list.get(i).getOwner()[0].getId());
            pStmt4.setString(4, list.get(i).getOwner()[0].getState());
            pStmt4.setString(5, list.get(i).getOwner()[0].getAvatar_url());
            pStmt4.executeUpdate();
          }
          pStmt1.setString(1, list.get(i).getId());
          pStmt1.executeUpdate();
          pStmt2.setString(1, list.get(i).getId());
          pStmt2.executeUpdate();
          pStmt3.executeUpdate();

          // logger.info("onwers={" + list.get(i).getOwner()[0].toString() + "}");
          logger.info(list.get(i).toString());

        }
        conn.commit();
        logger.info("game over");
      }
    } catch (Exception ex) {
      // TODO: handle exception
      ex.printStackTrace();
    } finally {
      AiDBUtil.doClear(conn);
    }
  }



  public static void main(String[] args) {

    try {
      JsonProject jsonproject = new JsonProject();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}
