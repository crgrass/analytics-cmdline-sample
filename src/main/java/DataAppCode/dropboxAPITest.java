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
import java.util.Locale;
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author cgrass@google.com (Chris Grass)
 *
 */
public class dropboxAPITest {
  
  //Create a dictionary here that contains the absolute paths of 
  //all import folders
  
  
  //Note: Tracy and Andrea will need to link their accounts to 
  public static DbxClient providedDBConnection() throws IOException, DbxException {
    final String APP_KEY = "k63qpk4lyn6l84q";
    final String APP_SECRET = "nj72tunelko2ynb";
    
    DbxAppInfo appInfo = new DbxAppInfo(APP_KEY,APP_SECRET);
    
    DbxRequestConfig config = new DbxRequestConfig(
        "JavaTuturial/1.0",Locale.getDefault().toString());
    DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config,appInfo);
    
    String authorizeUrl = webAuth.start();
    System.out.println("1. Go to: " + authorizeUrl);
    System.out.println("2. Click \"Allow\" (you might have to log in first)");
    System.out.println("3. Copy the authorization code.");
    
    try {
      if(Desktop.isDesktopSupported()) {
        Desktop.getDesktop().browse(new URI(authorizeUrl));
      }
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    
    
    
    String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
    
    
    DbxAuthFinish authFinish = webAuth.finish(code);
    String accessToken = authFinish.accessToken;
    
    DbxClient client = new DbxClient(config, accessToken);
    return client;
    
  }
  
  
  /*
   * The purpose of getDropBoxClient is to get the client
   * for Chris and return this client which we can now query on.
   */
  public static DbxClient getDropBoxClient() throws DbxException {
    final String APP_KEY = "k63qpk4lyn6l84q";
    final String APP_SECRET = "nj72tunelko2ynb";
    
    DbxAppInfo appInfo = new DbxAppInfo(APP_KEY,APP_SECRET);
    
    DbxRequestConfig config = new DbxRequestConfig(
        "JavaTuturial/1.0",Locale.getDefault().toString());
    DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config,appInfo);
    
    String authorizeUrl = webAuth.start();
    String code = "0lhnDVy9D58AAAAAAAAACvRlCCQ33GH-jzlU9nYOkOk";
    DbxAuthFinish authFinish = webAuth.finish(code);
    
    String accessToken = authFinish.accessToken;
    
    DbxClient client = new DbxClient(config, accessToken);
    
    return client;
  }
  
  public static void listContents(DbxClient cl) throws DbxException{
    DbxEntry.WithChildren listing = cl.getMetadataWithChildren("/Vendor Data - Import and Archive/LinkedIn/FY15/FY15 LinkedIn Reporting - Weekly Drop Folder");
    System.out.println("Files in the root path:");
    for (DbxEntry child : listing.children) {
      System.out.println("      " + child.name + child.toString());
    }
  }
  
  
  public static File testDownloadLinkedIn(DbxClient cl) throws DbxException, IOException {
    DbxEntry.File md;
    File outputFile = new File("retrievedLinkedIn.csv");
    OutputStream out = new FileOutputStream(outputFile);
    
    try {
      md = cl.getFile("/Vendor Data - Import and Archive/LinkedIn/FY15/FY15 LinkedIn Reporting - Weekly Drop Folder//LinkedIn_2015-02-03.csv", null, out); //null second param indicates latest version requested
      System.out.println("Printing output:");
      BufferedReader r = new BufferedReader( new FileReader(outputFile));
      String s = "", line = null;
      while((line = r.readLine()) != null) {
        s += line;
      }
      System.out.print(s);
    } finally {
      out.close();
    }
    
    return outputFile;
  }
  
  

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    DbxClient client;
    
    try {
      client = providedDBConnection();
      DbxEntry.File f = testDownloadLinkedIn(client);
      
//      listContents(client);
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    
    
    
    System.out.println("Execution Finished.");
    

  }

}
