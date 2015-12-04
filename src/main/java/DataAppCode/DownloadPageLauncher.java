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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */

/*
 * The purpose of the downloadPageLauncher method is to quickly launch the URLs of all vendor
 * websites necessary for the weekly import. This is used to save time during the weekly download
 * of vendor files.
 */
public class DownloadPageLauncher {
  
  
  public static void launchDownloadPages(){
    if(Desktop.isDesktopSupported()) {
      try {
        Desktop.getDesktop().browse(new URI("https://www.facebook.com/ads/manager/accounts"));
        Desktop.getDesktop().browse(new URI("https://ads.twitter.com/accounts/doh5s/campaigns_dashboard"));
        Desktop.getDesktop().browse(new URI("https://www.google.com/dfa/trafficking"));
        Desktop.getDesktop().browse(new URI("https://www.linkedin.com/uas/login?session_redirect=https%3A%2F%2Fwww.linkedin.com%2Fad%2F"));
        
      } catch (IOException | URISyntaxException e) {
        e.printStackTrace();
      }
    }
  }
  
  

  /**
   * @param args
   */
  public static void main(String[] args) {
    launchDownloadPages();

  }

}
