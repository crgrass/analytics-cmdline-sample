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

import com.google.api.services.analytics.model.GaData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class importUtils {

   
  //DD record but will leave this here as a reminder
  //iterates backward to avoid removal issues
  public static <E> ArrayList<E> remove0ImpressionRecords(ArrayList<E> origArray) {
    for (int i = origArray.size() - 1; i >= 0; i--) {
      importRecord currRec = (importRecord)origArray.get(i);
      if (currRec.getImpressions() == 0) {
        origArray.remove(i);
      }//end of if
    }//end of for
    return origArray;
  }//end of remove0ImpressionRecords
  
  public static <E> void matchBehaviorAcq(ArrayList<E> acquisition ,GaData behavior ) {
    for (E currRec : acquisition) {
      importRecord curr = (importRecord)currRec;
      for (int i = 0 ; i < behavior.getRows().size(); i ++) {
        List<String> currBehaviorRow = behavior.getRows().get(i);
        if (curr.match(currBehaviorRow)) {
            curr.setVisits(Integer.parseInt(currBehaviorRow.get(3)));
            curr.setPagesPerVisit(Float.parseFloat(currBehaviorRow.get(4)));
            curr.setAvgDuration(Float.parseFloat(currBehaviorRow.get(5)));
            curr.setPercentNewVisits(Float.parseFloat(currBehaviorRow.get(6)));
            curr.setBounceRate(Float.parseFloat(currBehaviorRow.get(7)));
        }// end of if
      }//end of inner for
    }//end of outer for
  }
  

  /*
   * These methods can eventually be turned multi-purpose by passing in the
   * source, medium and campaign mappings as a parameter
   */
  public static HashMap<GroupID, ArrayList<String[]>> groupCentroRawData(ArrayList<String[]> rawData) {
    
    HashMap<GroupID, ArrayList<String[]>> groupedData = new HashMap<GroupID, ArrayList<String[]>>();
    for (String[] row : rawData) {
      //what are the key fields necessary for the groupID
      /*
       * Source: Index: 3 Values: "Collective.com", "MobileFuse.com", "Pandora.com", "Petersons.com","SparkNotes.com","YouTube.com",
       * "BrandExchange.net",
       * Medium: Index : 5 Values: Digital Display (web),Video Display, Mobile Display, Rich Media
       * Campaign: Index: 2 Values: USM001-010
       */
      
      //These mappings convert the string identifying the source, medium and campaign in the Centro Files
      //to the string that matches the corresponding term in Google Analytics
      //Note: As USM utilizes more networks through Centro this list will need to be updated.
      HashMap<String,String> centroSourceMappings = new HashMap<String,String>();
      centroSourceMappings.put("Collective.com","Collective");
      centroSourceMappings.put("MobileFuse.com","MobileFuse");
      centroSourceMappings.put("Pandora.com","Pandora");
      centroSourceMappings.put("Petersons.com","Peterson");
      centroSourceMappings.put("SparkNotes.com","Sparknotes");
      centroSourceMappings.put("YouTube.com","YouTube");
      centroSourceMappings.put("BrandExchange.net","Brand_Exchange");
      
      HashMap<String,String> centroMediumMappings = new HashMap<String,String>();
      centroMediumMappings.put("Digital Display (web)","Display");
      centroMediumMappings.put("Video Display","Preroll");
      centroMediumMappings.put("Mobile Display","Mobile");
      centroMediumMappings.put("Rich media","Rich");
      
      HashMap<String,String> centroCampaignMappings = new HashMap<String,String>();
      centroCampaignMappings.put("USM001","");
      centroCampaignMappings.put("USM002","");
      centroCampaignMappings.put("USM003","");
      centroCampaignMappings.put("USM004","");
      centroCampaignMappings.put("USM005","");
      centroCampaignMappings.put("USM006","FY2015_Undergrad");
      centroCampaignMappings.put("USM007","FY2015_Graduate");
      centroCampaignMappings.put("USM008","FY2015_Degree_Completion");
      centroCampaignMappings.put("USM009","FY2015_Transfer");
      centroCampaignMappings.put("USM010","FY2015_Courses_Fall/Spring");
      
      
      String source = "";
      String medium = ""; 
      String campaign = "";
      
      
      if (centroSourceMappings.containsKey(row[3])) {
        source = centroSourceMappings.get(row[3]);
      } else {
        source = "Source Not Found";
      }

      
      if (centroMediumMappings.containsKey(row[5])) {
        medium = centroMediumMappings.get(row[5]); 
      } else {
        medium = "Medium Not Found: " + row[5];
        System.out.println(medium);
      }
      
      if (centroCampaignMappings.containsKey(row[2])) {
        campaign = centroCampaignMappings.get(row[2]); 
      } else {
        campaign = "Campaign Not Found";
      }
      
      //load GroupID
      GroupID currGroupID = new GroupID(source,medium,campaign);
      
      
      boolean groupIDExists = false;
      //Iterates through hashmap and raised flag if the groupID already exists
      Iterator it = groupedData.entrySet().iterator();
      while (it.hasNext()) {
        Map.Entry pairs = (Map.Entry)it.next();
        GroupID iteratedGroupID = (GroupID)pairs.getKey();
        if (iteratedGroupID.equals(currGroupID)) {
          groupIDExists = true;
          //add string once match is identified
          groupedData.get(iteratedGroupID).add(row);
        } //end of if
      }// end of while
      
      //A matching key was not found
      if (!groupIDExists) {
        groupedData.put(currGroupID, new ArrayList<String[]>()); //create the new key value pair with empty array list
        groupedData.get(currGroupID).add(row); //add row
      }//end of if   
      
    }// end of for
    
    return groupedData;
  }//end of group raw data




/*
 * FACEBOOK SPECIFIC
 */


public static HashMap<GroupID, ArrayList<String[]>> groupFacebookRawData(ArrayList<String[]> rawData) {
  
  HashMap<GroupID, ArrayList<String[]>> groupedData = new HashMap<GroupID, ArrayList<String[]>>();
  for (String[] row : rawData) {
    //what are the key fields necessary for the groupID
    /*
     * Source: Hardcoded
     * Medium: Hardcoded
     * Campaign: Index: 3 Values: Campaign Naming Conventions
     * Placement: Index: 4 Values: News Feed, Right Column
     * Device: Index: 4 Values: Mobile, Desktop
     */
    
    //These mappings convert the string identifying the campaign and placement in the Centro Files
    //to the string that matches the corresponding term in Google Analytics    
    HashMap<String,String> facebookCampaignMappings = new HashMap<String,String>();
    facebookCampaignMappings.put("fy15_lg_fb_disp_hs_ug","FY2015_Undergrad");
    facebookCampaignMappings.put("fy15_lg_fb_disp_wa_gr","FY2015_Graduate");// these are not tracked separately
    facebookCampaignMappings.put("fy15_lg_fb_disp_cu_gr","FY2015_Graduate");// 
    facebookCampaignMappings.put("fy15_lg_fb_disp_wa_dc","FY2015_Degree_Completion");
    facebookCampaignMappings.put("fy15_lg_fb_disp_cu_tt","FY2015_Transfer");
    facebookCampaignMappings.put("fy15_lg_fb_disp_wa_ic","FY2015_Courses_Fall/Spring");
    facebookCampaignMappings.put("fy15_lg_fb_disp_hs_ug_tour1","FY2015_CampusTourTest1");
    
    HashMap<String,String> facebookPlacementMappings = new HashMap<String,String>();
    facebookPlacementMappings.put("Right Column","Right_Rail");
    facebookPlacementMappings.put("News Feed","Newsfeed");
    
    
    String source = "Facebook";
    String medium = ""; 
    String campaign = "";
    String placement = "";
    
    if (facebookCampaignMappings.containsKey(row[3])) {
      campaign = facebookCampaignMappings.get(row[3]); 
    } else {
      campaign = "Campaign Not Found";
      System.out.println("Campaign for \"" + row[3] +"\" could not be identified");
    }
    
    if (row[4].contains("Right Column")) {
      placement = "Right_Rail";
      medium = "Right_Rail";
    } else if (row[4].contains("News Feed")) {
      placement = "Newsfeed";
      medium = "Newsfeed";
    } else {
      System.out.println("Placement for \"" + row[4] +"\" could not be identified");
    }
    
    //load GroupID
    GroupID currGroupID = new GroupID(source,medium,campaign,placement);
    
    
    boolean groupIDExists = false;
    //Iterates through hashmap and raised flag if the groupID already exists
    Iterator it = groupedData.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pairs = (Map.Entry)it.next();
      GroupID iteratedGroupID = (GroupID)pairs.getKey();
      if (iteratedGroupID.equals(currGroupID)) {
        groupIDExists = true;
        //add string once match is identified
        groupedData.get(iteratedGroupID).add(row);
      } //end of if
    }// end of while
    
    //A matching key was not found
    if (!groupIDExists) {
      groupedData.put(currGroupID, new ArrayList<String[]>()); //create the new key value pair with empty array list
      groupedData.get(currGroupID).add(row); //add row
    }//end of if   
    
  }// end of for
  
  return groupedData;
}//end of group raw data

public static HashMap<GroupID, ArrayList<String[]>> groupTwitterRawData(ArrayList<String[]> rawData) {
  
  HashMap<GroupID, ArrayList<String[]>> groupedData = new HashMap<GroupID, ArrayList<String[]>>();
  for (String[] row : rawData) {

    //what are the key fields necessary for the groupID
    /*
     * Source: Hardcoded
     * Medium: Hardcoded
     * Campaign: Index: 3 Values: Campaign Naming Conventions
     * Placement: Index: 4 Values: News Feed, Right Column
     * Device: Index: 4 Values: Mobile, Desktop
     */
    
    //These mappings convert the string identifying the campaign and placement in the Centro Files
    //to the string that matches the corresponding term in Google Analytics    
    HashMap<String,String> twitterCampaignMappings = new HashMap<String,String>();
    twitterCampaignMappings.put("USM High School Undergrad-Additional Spend ","FY2015_Undergrad");
    twitterCampaignMappings.put("USM High School Undergrad","FY2015_Undergrad");
    twitterCampaignMappings.put("USM Graduate","FY2015_Graduate");// these are not tracked separately
    twitterCampaignMappings.put("Copy of USM Graduate","FY2015_Graduate");// 
    twitterCampaignMappings.put("Degree Completer","FY2015_Degree_Completion");
    twitterCampaignMappings.put("UG_Transfer","FY2015_Transfer");
    twitterCampaignMappings.put("UG_Transfer_Additional Spend","FY2015_Transfer");
    
    
    String source = "Twitter";
    String medium = "Social"; 
    String campaign = "";
    
    if (twitterCampaignMappings.containsKey(row[1])) {
      campaign = twitterCampaignMappings.get(row[1]); 
    } else {
      campaign = "Campaign Not Found";
      System.out.println("Campaign for \"" + row[1] +"\" could not be identified");
    }
    
    //load GroupID
    GroupID currGroupID = new GroupID(source,medium,campaign);
    
    
    boolean groupIDExists = false;
    //Iterates through hashmap and raised flag if the groupID already exists
    Iterator it = groupedData.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pairs = (Map.Entry)it.next();
      GroupID iteratedGroupID = (GroupID)pairs.getKey();
      if (iteratedGroupID.equals(currGroupID)) {
        groupIDExists = true;
        //add string once match is identified
        groupedData.get(iteratedGroupID).add(row);
      } //end of if
    }// end of while
    
    //A matching key was not found
    if (!groupIDExists) {
      groupedData.put(currGroupID, new ArrayList<String[]>()); //create the new key value pair with empty array list
      groupedData.get(currGroupID).add(row); //add row
    }//end of if   
    
  }// end of for
  
  return groupedData;
}//end of group raw data
  
  
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
