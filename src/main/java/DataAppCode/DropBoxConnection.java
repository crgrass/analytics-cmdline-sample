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
import com.dropbox.core.*;

import java.io.*;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class DropBoxConnection {
  public static DbxClient client;
  public static DbxAuthFinish finishedAuth; //this is wher the access token is stored
  final static String APP_KEY = "k63qpk4lyn6l84q";
  final static String APP_SECRET = "nj72tunelko2ynb";
  
  //where will auth token be stored
  
  
  
  public static void verifyDropboxConnection() {
    //check to see if there is a valid authorization code
    
    try {
      //Dummy request to Dropbox API see if token works
      client.getAccountInfo();
    } catch (DbxException exception){ //If exception is thrown token is not good.
      System.out.println("Problem with original token. Attempting to reauthorize.");
      
      //This try block reauthorizes dropbox
      try {
        authorizeDropbox();
      } catch (IOException exception1) {
        exception1.printStackTrace();
      } catch (DbxException exception1) {
        exception1.printStackTrace();
      }//end of authorize dropbox catch
    }
  }//end of dropBoxConnect
  
  
  public static void authorizeDropbox() throws IOException, DbxException {
    DbxAppInfo appInfo = new DbxAppInfo(APP_KEY,APP_SECRET);
    DbxRequestConfig config = new DbxRequestConfig(
        "JavaTuturial/1.0",Locale.getDefault().toString());
    DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config,appInfo);
    
    String authorizeUrl = webAuth.start();
    System.out.println("1. Go to: " + authorizeUrl);
    System.out.println("2. Click \"Allow\" (you might have to log in first)");
    System.out.println("3. Copy the authorization code.");
    System.out.println("4. Paste this code into the console and press ENTER.");
    
    //Open a browser with the authorize URL
    try {
      if(Desktop.isDesktopSupported()) {
        Desktop.getDesktop().browse(new URI(authorizeUrl));
      }
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    
    //Read the input auth code use the code to receive the access token
    String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
    
    
    finishedAuth = webAuth.finish(code);
    String accessToken = finishedAuth.accessToken;
    
    //write code to file for later use
    System.out.println("Creating File.");
    System.out.println("access Token: " + accessToken);
    PrintWriter writer = new PrintWriter("dropboxAccess.txt", "UTF-8");
    writer.println(accessToken);
    writer.close();
    
    client = new DbxClient(config, accessToken);
    
  }
  
  
  /*
   * This method loads the authorization token from the
   * dropboxAccess.txt file. If the file does not exist
   * the authorizeDropbox method is called which prompts
   * the user to verify an authcode from sales force. This
   * auth code is then used to create a new token which is
   * written to a file for use next time. If the token is valid
   * the client is created and stored in the client static variable
   * which is to be utilized throughout the data app when making 
   * requests to dropbox.
   */
  public static void createDropboxClient() {
    //The file exists
    File f = new File("dropboxAccess.txt");//File contains the access code
    if (!f.exists()) {//if the file doesn't exists
      System.out.println("The dropboxAccess.txt file does not exist.");
      //get the access code
      try {
        authorizeDropbox();
      } catch (IOException exception) {
        exception.printStackTrace();
      } catch (DbxException exception) {
        exception.printStackTrace();
      }
    } else {//The file exists
      System.out.println("The Dropbox access file was found."
          + " Authorization token created and additional authorization is not required.\n");
      
      try {
        DbxRequestConfig config = new DbxRequestConfig(
            "JavaTuturial/1.0",Locale.getDefault().toString()); //Create config
        BufferedReader br = new BufferedReader(new FileReader(f)); //Reads token from file
        String token = br.readLine();// There should only be one line in this file
        
        //client is created
        client = new DbxClient(config, token); //Create client with config and token
        br.close();
      } catch (FileNotFoundException e1) {
        e1.printStackTrace();
      } catch (IOException e2) {
        e2.printStackTrace();
      }
      
    }
    
    //The file does not exist
    
    //If the file does not exist we can skip straight to the authorize dropbox methdo
    
  }
  
  
  public static File pullCSV(String vendor, LocalDate startDate) throws DbxException, IOException {
    DbxEntry.File md;
    //where is this file built in mac
    File outputFile = new File("retrieved" + vendor + ".csv");
    OutputStream out = new FileOutputStream(outputFile);
    
    try {
      Map<String,String> paths = FilePathBuilder.buildFilePathMapDropBox(startDate);
      System.out.println("paths size: " + paths.size());
      //check if the file exists
      if (client.getMetadata(paths.get(vendor)) == null ) {
        System.out.println("The file at path " + paths.get(vendor) + " could not be found");
      }
      md = client.getFile(paths.get(vendor), null, out); //null second param indicates latest version requested
      BufferedReader r = new BufferedReader( new FileReader(outputFile));
      String s = "", line = null;
      while((line = r.readLine()) != null) {
        s += line;
      }
      r.close();
    } finally {
      out.close();
    }
    
    return outputFile;
  }
  
  public static void initializeDropboxConnection(){
    createDropboxClient();
    verifyDropboxConnection();
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    
    initializeDropboxConnection();

  }//end of main method

}
