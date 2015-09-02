/*
 * Copyright (c) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package DataAppCode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class DatabaseUtils {

  
  
  //This connection connects to the Microsoft Access Test Database
  public static Connection getTestDBConnection() throws Exception {
    Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
    String url = "jdbc:ucanaccess://C:/users/cgrass/Desktop/"
        + "USM Marketing Application/Test Database/"
        + "test_USMMarketingIntelligenceDatabase.accdb";
    Connection conn = DriverManager.getConnection(url);
    return conn;
  }
  
 
  
  public static Connection getGoogleCloudTestDBConnection() throws Exception {
    
    Class.forName("com.mysql.jdbc.Driver").newInstance();
    Connection conn = null;
    
    try {
      String url = "jdbc:mysql://173.194.248.217:3306/USM Marketing Intelligence Database";
      String user = "crgrass";
      String pass = "Swan!1dive";
      conn = 
          DriverManager.getConnection(url,user,pass);
      
    } catch (SQLException ex) {
      //handle any errors
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
    }
    
    return conn;
  }
  
  
  public static void main(String[] args) {
    
    //Test googleCloudTestDB method
    try {
      getGoogleCloudTestDBConnection();
    } catch (Exception e) {
      //errors are printed in the method
      e.printStackTrace();
    }
    

  }

}
