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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class TWRecord implements importRecord {

      private boolean partialWeek = false;
      private Integer daysActive = 0;
           
      private String startDate;
      private String endDate;
      private String source;
      private String medium;
      private String campaign;
      private Integer impressions;
      private Integer TWclicks;
      private Float avgCTR;
      private Float spend;
      private Float avgCPC;
      private Integer engagements;
      private Integer billedEngagements;
      private Integer retweets;
      private Integer replies;
      private Integer follows;
      private Integer favorites;
      private Integer cardEngagements;
      private Integer unfollows;
      private Float engagementRate;
      private Float avgCPE;
      private Integer linkClicks; //this is used for clicks      
       
      private Integer visits = 0;
      private Float pagesPerVisit = 0.0f;
      private Float avgDuration = 0.0f;
      private Float percentNewVisits = 0.0f;
      private Float bounceRate = 0.0f;
      
      
      

      //constructor
      //behavior metrics are always created at a later date
      public TWRecord(String[] dates,
          String source, String medium, String campaign, Integer impressions, Integer tWclicks,
          Float cTR, Float spend, Float cPC, Integer engagements, Integer billedEngagements,
          Integer retweets, Integer replies, Integer follows, Integer favorites,
          Integer cardEngagements, Integer unfollows, Float engagementRate, Float cPE,
          Integer linkClicks) {
        this.startDate = dates[0];
        this.endDate = dates[1];
        this.source = source;
        this.medium = medium;
        this.campaign = campaign;
        this.impressions = impressions;
        this.TWclicks = tWclicks;
        this.avgCTR = cTR;
        this.spend = spend;
        this.avgCPC = cPC;
        this.engagements = engagements;
        this.billedEngagements = billedEngagements;
        this.retweets = retweets;
        this.replies = replies;
        this.follows = follows;
        this.favorites = favorites;
        this.cardEngagements = cardEngagements;
        this.unfollows = unfollows;
        this.engagementRate = engagementRate;
        this.avgCPE = cPE;
        this.linkClicks = linkClicks;
      }
      
      @Override
      public String toString() {
        String returnString = "";
        returnString += "Start Date: " + this.startDate + "\n";
        returnString += "End Date: " + this.endDate + "\n";
        returnString += "Source: " + this.source + "\n";
        returnString += "Medium: " + this.medium + "\n";
        returnString += "Campaign: " + this.campaign + "\n";
        returnString += "Clicks: " + this.TWclicks + "\n";
        returnString += "Impressions: " + this.impressions + "\n";
        returnString += "CTR: " + this.avgCTR + "\n";
        returnString += "Average CPC: " + this.avgCPC + "\n";
        returnString += "Engagements: " + this.engagements + "\n";
        returnString += "BilledEngagements: " + this.billedEngagements + "\n";
        returnString += "Retweets: " + this.retweets + "\n";
        returnString += "Replies: " + this.replies + "\n";
        returnString += "Follows: " + this.follows + "\n";
        returnString += "Favorites: " + this.favorites + "\n";
        returnString += "Card Engagements" + this.cardEngagements +"\n";
        returnString += "Unfollows: " + this.unfollows + "\n";
        returnString += "Engagement Rate: " + this.engagementRate + "\n";
        returnString += "Cost Per Engagement: " + this.avgCPE + "\n";
        returnString += "Link Clicks: " + this.linkClicks + "\n";
        returnString += "Spend: " + this.spend + "\n";
        returnString += "Visits: " + this.visits + "\n";
        returnString += "Pages Per Visit: " + this.pagesPerVisit + "\n";
        returnString += "Average Duration: " + this.avgDuration + "\n";
        returnString += "Percent New Visits: " + this.percentNewVisits + "\n";
        returnString += "Bounce Rate: " + this.bounceRate + "\n";
        returnString += "Partial Week: " + this.partialWeek + "\n";
        returnString += "Days Active: " + this.daysActive + "\n";
        return returnString;
      }
      
      /*
       * PreCondition: Raw data is already grouped appropriately
       */
      public static ArrayList<TWRecord> aggregate(HashMap<GroupID,ArrayList<String[]>> rawData) {
        System.out.println("Aggregating rows based on Source, Medium and Campaign...\n");
        
        //Iterate through HashMap and place only Twitter entries into a new HashMap
        HashMap<GroupID,ArrayList<String[]>> onlyTW = new HashMap<GroupID,ArrayList<String[]>>();
        Iterator<Map.Entry<GroupID,ArrayList<String[]>>> itr = rawData.entrySet().iterator();
        while (itr.hasNext()) {
          Map.Entry<GroupID,ArrayList<String[]>> pairs = itr.next();
          GroupID currID = pairs.getKey();
          @SuppressWarnings("unchecked")
          ArrayList<String[]> currArray = pairs.getValue();
          if (currID.getSource().equals("Twitter")) {
            onlyTW.put(currID, currArray);
          }//end of if
        }//end of while
        
        //The returned ArrayList
        ArrayList<TWRecord> TWRecordCollection = new ArrayList<TWRecord>();

        //Need to loop through Hash Map
        Iterator<Map.Entry<GroupID,ArrayList<String[]>>> it = onlyTW.entrySet().iterator();
        while (it.hasNext()) {
          Map.Entry<GroupID,ArrayList<String[]>> pairs = it.next();
          ArrayList<String[]> currList = pairs.getValue();

          //Metrics are aggregated here
          Integer totalTWClicks = 0;
          Integer totalImpressions = 0;
          Integer totalEngagements = 0;
          Integer totalBilledEngagements = 0;
          Integer totalRetweets = 0;
          Integer totalReplies = 0;
          Integer totalFavorites = 0;
          Integer totalFollows = 0;
          Integer totalCardEngagements = 0;
          Integer totalUnfollows = 0;
          Integer totalLinkClicks = 0;
          Float totalSpend = 0.0f;
          
          
          //TODO: Need to be able to handle empty cells that should be zero

          //TODO:Discarding last row as a band aid
          //eventually need to stop importing last row

          for (String[] row : currList) {
            //Index 0 : id, Index 1 : Campaign, Index 2 : Campaign URL
            //Index 3 : Funding Instrument ID, Index 4 : Funding Instrument Name,
            //Index 5 : Time, Index 6 : Campaign Start Date,Index 7: Campaign End Date,
            totalSpend += Float.parseFloat(row[8]); //Index 8 : Spend
            totalImpressions += Integer.parseInt(row[9]); //Index9:  Impressions
            String currCampaign = pairs.getKey().getCampaign();
            totalEngagements += Integer.parseInt(row[10]); //Index 10: Engagements
            totalBilledEngagements += Integer.parseInt(row[11]);//Index 11: Billed Engagements
            totalRetweets += Integer.parseInt(row[12]);//Index 12: Retweets
            totalReplies += Integer.parseInt(row[13]);//Index13: Replies
            totalFollows += Integer.parseInt(row[14]);//Index 14: Follows
            totalTWClicks += Integer.parseInt(row[15]);//Index 15: TWClicks
            totalFavorites += Integer.parseInt(row[16]);//Index 16: Favorites
            totalCardEngagements += Integer.parseInt(row[17]);//Index 17: Card Engagements
            totalUnfollows += Integer.parseInt(row[18]);//Index 18: Unfollows
            //Index 19: Engagement Rate
            totalLinkClicks += Integer.parseInt(row[20]);//Index 20: Link Clicks
            //TODO: Need to ensure all csvs can handle number formats with commas
          }// end of inner loop
          


          Float aggCTR = (float)totalTWClicks/(float)totalImpressions;
          Float aggCPC = totalSpend/(float)totalTWClicks;
          Float aggCPE = totalSpend/totalEngagements;
          Float aggEngagementRate = (float)totalEngagements/(float)totalImpressions;
          
          //this might be simplified by grouping and then looping through
          //Check for 0 by 0 division and replace NaN with 0
          if (Float.isNaN(aggCPE)){
            aggCPE = 0.0f;
          }
          if (Float.isNaN(aggCPC)){
            aggCPC = 0.0f;
          }

          String startDate = guiCode.DataAppTest.startDate.toString();
          String endDate = guiCode.DataAppTest.endDate.toString();
          String[] dateArray = {startDate,endDate};
          
          GroupID currID = pairs.getKey();

          TWRecord rec = new TWRecord(dateArray,currID.getSource(),currID.getMedium(),currID.getCampaign(),totalImpressions,
              totalTWClicks,aggCTR,totalSpend,aggCPC,totalEngagements,totalBilledEngagements,totalRetweets,totalReplies,totalFollows,
              totalFavorites,totalCardEngagements,totalUnfollows,aggEngagementRate,aggCPE,totalLinkClicks);
          TWRecordCollection.add(rec);
        }
        

        return TWRecordCollection;
      }









  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }



  /**
   * @return the partialWeek
   */
  public boolean getPartialWeek() {
    return partialWeek;
  }



  /**
   * @param partialWeek the partialWeek to set
   */
  public void setPartialWeek(boolean partialWeek) {
    this.partialWeek = partialWeek;
  }



  /**
   * @return the daysActive
   */
  public Integer getDaysActive() {
    return daysActive;
  }



  /**
   * @param daysActive the daysActive to set
   */
  public void setDaysActive(Integer daysActive) {
    this.daysActive = daysActive;
  }



  /**
   * @return the startDate
   */
  public String getStartDate() {
    return startDate;
  }



  /**
   * @param startDate the startDate to set
   */
  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }



  /**
   * @return the endDate
   */
  public String getEndDate() {
    return endDate;
  }



  /**
   * @param endDate the endDate to set
   */
  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }



  /**
   * @return the source
   */
  public String getSource() {
    return source;
  }



  /**
   * @param source the source to set
   */
  public void setSource(String source) {
    this.source = source;
  }



  /**
   * @return the medium
   */
  public String getMedium() {
    return medium;
  }



  /**
   * @param medium the medium to set
   */
  public void setMedium(String medium) {
    this.medium = medium;
  }



  /**
   * @return the campaign
   */
  public String getCampaign() {
    return campaign;
  }



  /**
   * @param campaign the campaign to set
   */
  public void setCampaign(String campaign) {
    this.campaign = campaign;
  }



  /**
   * @return the impressions
   */
  public Integer getImpressions() {
    return impressions;
  }



  /**
   * @param impressions the impressions to set
   */
  public void setImpressions(Integer impressions) {
    this.impressions = impressions;
  }



  /**
   * @return the tWclicks
   */
  public Integer getTWclicks() {
    return TWclicks;
  }



  /**
   * @param tWclicks the tWclicks to set
   */
  public void setTWclicks(Integer tWclicks) {
    TWclicks = tWclicks;
  }



  /**
   * @return the cTR
   */
  public Float getCTR() {
    return avgCTR;
  }



  /**
   * @param cTR the cTR to set
   */
  public void setCTR(Float cTR) {
    avgCTR = cTR;
  }



  /**
   * @return the spend
   */
  public Float getSpend() {
    return spend;
  }



  /**
   * @param spend the spend to set
   */
  public void setSpend(Float spend) {
    this.spend = spend;
  }



  /**
   * @return the cPC
   */
  public Float getCPC() {
    return avgCPC;
  }



  /**
   * @param cPC the cPC to set
   */
  public void setCPC(Float cPC) {
    avgCPC = cPC;
  }



  /**
   * @return the engagements
   */
  public Integer getEngagements() {
    return engagements;
  }



  /**
   * @param engagements the engagements to set
   */
  public void setEngagements(Integer engagements) {
    this.engagements = engagements;
  }



  /**
   * @return the billedEngagements
   */
  public Integer getBilledEngagements() {
    return billedEngagements;
  }



  /**
   * @param billedEngagements the billedEngagements to set
   */
  public void setBilledEngagements(Integer billedEngagements) {
    this.billedEngagements = billedEngagements;
  }



  /**
   * @return the retweets
   */
  public Integer getRetweets() {
    return retweets;
  }



  /**
   * @param retweets the retweets to set
   */
  public void setRetweets(Integer retweets) {
    this.retweets = retweets;
  }



  /**
   * @return the replies
   */
  public Integer getReplies() {
    return replies;
  }



  /**
   * @param replies the replies to set
   */
  public void setReplies(Integer replies) {
    this.replies = replies;
  }



  /**
   * @return the follows
   */
  public Integer getFollows() {
    return follows;
  }



  /**
   * @param follows the follows to set
   */
  public void setFollows(Integer follows) {
    this.follows = follows;
  }



  /**
   * @return the favorites
   */
  public Integer getFavorites() {
    return favorites;
  }



  /**
   * @param favorites the favorites to set
   */
  public void setFavorites(Integer favorites) {
    this.favorites = favorites;
  }



  /**
   * @return the cardEngagements
   */
  public Integer getCardEngagements() {
    return cardEngagements;
  }



  /**
   * @param cardEngagements the cardEngagements to set
   */
  public void setCardEngagements(Integer cardEngagements) {
    this.cardEngagements = cardEngagements;
  }



  /**
   * @return the unfollows
   */
  public Integer getUnfollows() {
    return unfollows;
  }



  /**
   * @param unfollows the unfollows to set
   */
  public void setUnfollows(Integer unfollows) {
    this.unfollows = unfollows;
  }



  /**
   * @return the engagementRate
   */
  public Float getEngagementRate() {
    return engagementRate;
  }



  /**
   * @param engagementRate the engagementRate to set
   */
  public void setEngagementRate(Float engagementRate) {
    this.engagementRate = engagementRate;
  }



  /**
   * @return the cPE
   */
  public Float getCPE() {
    return avgCPE;
  }



  /**
   * @param cPE the cPE to set
   */
  public void setCPE(Float cPE) {
    avgCPE = cPE;
  }



  /**
   * @return the linkClicks
   */
  public Integer getLinkClicks() {
    return linkClicks;
  }



  /**
   * @param linkClicks the linkClicks to set
   */
  public void setLinkClicks(Integer linkClicks) {
    this.linkClicks = linkClicks;
  }



  /**
   * @return the visits
   */
  public Integer getVisits() {
    return visits;
  }



  /**
   * @param visits the visits to set
   */
  public void setVisits(Integer visits) {
    this.visits = visits;
  }



  /**
   * @return the pagesPerVisit
   */
  public Float getPagesPerVisit() {
    return pagesPerVisit;
  }



  /**
   * @param pagesPerVisit the pagesPerVisit to set
   */
  public void setPagesPerVisit(Float pagesPerVisit) {
    this.pagesPerVisit = pagesPerVisit;
  }



  /**
   * @return the avgDuration
   */
  public Float getAvgDuration() {
    return avgDuration;
  }



  /**
   * @param avgDuration the avgDuration to set
   */
  public void setAvgDuration(Float avgDuration) {
    this.avgDuration = avgDuration;
  }



  /**
   * @return the percentNewVisits
   */
  public Float getPercentNewVisits() {
    return percentNewVisits;
  }



  /**
   * @param percentNewVisits the percentNewVisits to set
   */
  public void setPercentNewVisits(Float percentNewVisits) {
    this.percentNewVisits = percentNewVisits;
  }



  /**
   * @return the bounceRate
   */
  public Float getBounceRate() {
    return bounceRate;
  }



  /**
   * @param bounceRate the bounceRate to set
   */
  public void setBounceRate(Float bounceRate) {
    this.bounceRate = bounceRate;
  }

  /*
   * Pre: The query passed to the GA API requests source, medium and campaign
   * as the first, second and third dimensions. Without this query structure the
   * the wrong dimension from the GA results will be matched always returning false 
   */
  @Override
  public boolean match(List<String> gaRow) {
    if (gaRow.get(0).equals(this.source) && gaRow.get(1).equals(this.medium) && gaRow.get(2).equals(this.campaign)) {
      return true;
    }
    return false;
  }

}
