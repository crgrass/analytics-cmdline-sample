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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class FBRecord implements importRecord {

//record attributes
  private int recordCount = 0;
  private boolean partialWeek = false;
  private int daysActive = 7;
  
  //import data
  private String startDate;
  private String endDate;
  private String source; // the source is also the network
  private String medium;
  private String campaign;
  private String adContent;
  private String placement; //This is new be sure to include in query
  private String device;
  private Integer reach;
  private Float frequency;
  private Integer clicks;
  private Integer uniqueClicks;
  private Integer impressions;
  private Float CTR;
  private Float uniqueCTR;
  private Float avgCPC;
  private Float avgCPM;
  private Float CP1KR;
  private Integer actions;
  private Integer PTA;
  private Integer likes;
  private Integer websiteClicks;
  private Float spend;
  private Integer visits = 0;
  private Float pagesPerVisit = 0.0f;
  private Float avgDuration = 0.0f;
  private Float percentNewVisits = 0.0f;
  private Float bounceRate = 0.0f;
  
  //constructor
  //behavior metrics are always created at a later date
  public FBRecord(String[] dateArray, String src, String med, String camp,String adc, String plc, String dvc, 
                  Integer rch, Float frq,Integer clks, Integer unqclks, Integer impr, Float ctr, Float unqctr, 
                  Float cpc, Float cpm,Float cp1k, Integer act, Integer pa, Integer lks, Integer webclks, Float spnd) {
    setStartDate(dateArray[0]);
    setEndDate(dateArray[1]);
    setSource(src);
    setMedium(med);
    setCampaign(camp);
    setAdContent(adc);
    setPlacement(plc);
    setDevice(dvc);
    setReach(rch);
    setFrequency(frq);
    setClicks(clks);
    setUniqueClicks(unqclks);
    setImpressions(impr);
    setCTR(ctr);
    setUniqueCTR(unqctr);
    setAvgCPC(cpc);
    setAvgCPM(cpm);
    setCP1KR(cp1k);
    setActions(act);
    setPTA(pa);
    setLikes(lks);
    setWebsiteClicks(webclks);
    setSpend(spnd);
  }
  
  
  /*
   * PreCondition: Raw data is already grouped appropriately
   */
  public static ArrayList<FBRecord> aggregate(HashMap<GroupID,ArrayList<String[]>> rawData,
      LocalDate sDate, LocalDate eDate) {
    
    //Iterate through HashMap and place only digial display entries into a new HashMap
    HashMap<GroupID,ArrayList<String[]>> onlyFB = new HashMap<GroupID,ArrayList<String[]>>();
    Iterator<Map.Entry<GroupID,ArrayList<String[]>>> itr = rawData.entrySet().iterator();
    while (itr.hasNext()) {
      Map.Entry<GroupID, ArrayList<String[]>> pairs = itr.next();
      GroupID currID = pairs.getKey();
      @SuppressWarnings("unchecked")
      ArrayList<String[]> currArray = pairs.getValue();
      if (currID.getSource().equals("Facebook")) {
        onlyFB.put(currID, currArray);
      }//end of if
    }//end of while
    
    //The returned ArrayList
    ArrayList<FBRecord> FBRecordCollection = new ArrayList<FBRecord>();

    //Need to loop through Hash Map
    Iterator<Map.Entry<GroupID, ArrayList<String[]>>> it = onlyFB.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<GroupID,ArrayList<String[]>> pairs = it.next();
      ArrayList<String[]> currList = pairs.getValue();

      //Metrics are aggregated here
      Integer totalReach = 0;
      Integer totalClicks = 0;
      Integer totalUniqueClicks = 0;
      Integer totalImpressions = 0;
      Integer totalActions = 0;
      Integer totalPTA = 0; //is this Integer??
      Integer totalLikes = 0;
      Integer totalWebsiteClicks = 0;
     
      Float totalSpend = 0.0f;
      
      //TODO: Need to be able to handle empty cells that should be zero. This problem is apparent
      //When execution fails due to indexes 19 and 20 being null when they should be zero.
      


      for (String[] row : currList) {
        
        //Each array should be of size 20
        if (row.length != 21) {
          System.out.println("The Facebook data was not imported due to an incorrect"
              + " number of fields.  Check the source file.");
          return new ArrayList<FBRecord>(); //exit method
        }

        totalReach += Integer.parseInt(row[6]);
        totalImpressions += Integer.parseInt(row[8]);
        totalClicks += Integer.parseInt(row[9]);
        totalUniqueClicks += Integer.parseInt(row[10]);
        totalSpend += Float.parseFloat(row[13]);
        totalActions += Integer.parseInt(row[17]);
        totalPTA += Integer.parseInt(row[18]);
        totalLikes += Integer.parseInt(row[19]);
        totalWebsiteClicks += Integer.parseInt(row[20]);
        
        
      }// end of inner loop

      Float aggFrequency = (float)totalImpressions/(float)totalReach;
      Float aggCTR = (float)totalClicks/(float)totalImpressions;
      Float aggUniqueCTR = (float)totalUniqueClicks /(float)totalReach;
      Float aggCPC = totalSpend/(float)totalClicks;
      Float kImpressions = (float)totalImpressions/1000;
      Float aggCPM = totalSpend/kImpressions;
      Float aggCP1KR = totalSpend / (totalReach/1000);
      
      //this might be simplified by grouping and then looping through
      //Check for 0 by 0 division and replace NaN with 0
      if (Float.isNaN(aggFrequency) || Float.isInfinite(aggFrequency)){
        aggCP1KR = 0.0f;
      }
      
      if (Float.isNaN(aggCTR) || Float.isInfinite(aggCTR)){
        aggCP1KR = 0.0f;
      }
      
      if (Float.isNaN(aggCP1KR) || Float.isInfinite(aggCP1KR)){
        aggCP1KR = 0.0f;
      }
      if (Float.isNaN(aggCPC) || Float.isInfinite(aggCPC)){
        aggCPC = 0.0f;
      }
      
      if (Float.isNaN(aggCPM) || Float.isInfinite(aggCPM)){
        aggCPM = 0.0f;
      }
      

      String startDate = sDate.toString();
      String endDate = eDate.toString();
      String[] dateArray = {startDate,endDate};
      
      GroupID currID = pairs.getKey();

      FBRecord rec = new FBRecord(dateArray,currID.getSource(),currID.getMedium(),currID.getCampaign(),currID.getAdContent(), currID.getPlacement(), "Device",//placeholder for device
          totalReach,aggFrequency,totalClicks,totalUniqueClicks,totalImpressions,aggCTR,aggUniqueCTR,aggCPC,aggCPM,
          aggCP1KR,totalActions,totalPTA,totalLikes,totalWebsiteClicks,totalSpend);
      FBRecordCollection.add(rec);
    }
    

    return FBRecordCollection;
  }
  
  @Override
  public String toString() {
    String returnString = "";
    returnString += "Start Date: " + this.startDate + "\n";
    returnString += "End Date: " + this.endDate + "\n";
    returnString += "Source: " + this.source + "\n";
    returnString += "Medium: " + this.medium + "\n";
    returnString += "Campaign: " + this.campaign + "\n";
    returnString += "AdContent: " + this.adContent + "\n";
    returnString += "Placement: " + this.placement + "\n"; //This is new be sure to include in query
    returnString += "Device: " + this.device + "\n";
    returnString += "Reach: " + this.reach + "\n";
    returnString += "Frequency: " + this.frequency + "\n";
    returnString += "Clicks: " + this.clicks + "\n";
    returnString += "Unique Clicks: " + this.uniqueClicks + "\n";
    returnString += "Impressions: " + this.impressions + "\n";
    returnString += "CTR: " + this.CTR + "\n";
    returnString += "Unique CTR: " + this.uniqueCTR + "\n";
    returnString += "Average CPC: " + this.avgCPC + "\n";
    returnString += "Average CPM: " + this.avgCPM + "\n";
    returnString += "Cost per 1,000 People Reached: " + this.CP1KR + "\n";
    returnString += "Actions: " + this.actions + "\n";
    returnString += "People Taking Actions: " + this.PTA + "\n";
    returnString += "Likes: " + this.likes + "\n";
    returnString += "Website Clicks: " + this.websiteClicks + "\n";
    returnString += "Spend: " + this.spend + "\n";
    returnString += "Visits: " + this.visits + "\n";
    returnString += "Pages Per Visit: " + this.pagesPerVisit + "\n";
    returnString += "Average Duration: " + this.avgDuration + "\n";
    returnString += "Percent New Visits: " + this.percentNewVisits + "\n";
    returnString += "Bounce Rate: " + this.bounceRate + "\n";
    returnString += "Partial Week: " + this.getPartialWeek() + "\n";
    returnString += "Days Active: " + this.getDaysActive() + "\n";
    return returnString;
  }
  public static void main(String[] args) {

  }
  /**
   * @return the recordCount
   */
  public int getRecordCount() {
    return recordCount;
  }
  /**
   * @param recordCount the recordCount to set
   */
  public void setRecordCount(int recordCount) {
    this.recordCount = recordCount;
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
  public int getDaysActive() {
    return daysActive;
  }
  /**
   * @param daysActive the daysActive to set
   */
  public void setDaysActive(int daysActive) {
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
   * @return the placement
   */
  public String getPlacement() {
    return placement;
  }
  /**
   * @param placement the placement to set
   */
  public void setPlacement(String placement) {
    this.placement = placement;
  }
  /**
   * @return the device
   */
  public String getDevice() {
    return device;
  }
  /**
   * @param device the device to set
   */
  public void setDevice(String device) {
    this.device = device;
  }
  /**
   * @return the reach
   */
  public Integer getReach() {
    return reach;
  }
  /**
   * @param reach the reach to set
   */
  public void setReach(Integer reach) {
    this.reach = reach;
  }
  /**
   * @return the frequency
   */
  public Float getFrequency() {
    return frequency;
  }
  /**
   * @param frequency the frequency to set
   */
  public void setFrequency(Float frequency) {
    this.frequency = frequency;
  }
  /**
   * @return the clicks
   */
  public Integer getClicks() {
    return clicks;
  }
  /**
   * @param clicks the clicks to set
   */
  public void setClicks(Integer clicks) {
    this.clicks = clicks;
  }
  /**
   * @return the uniqueClicks
   */
  public Integer getUniqueClicks() {
    return uniqueClicks;
  }
  /**
   * @param uniqueClicks the uniqueClicks to set
   */
  public void setUniqueClicks(Integer uniqueClicks) {
    this.uniqueClicks = uniqueClicks;
  }
  /**
   * @return the impressions
   */
  @Override
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
   * @return the cTR
   */
  public Float getCTR() {
    return CTR;
  }
  /**
   * @param cTR the cTR to set
   */
  public void setCTR(Float cTR) {
    CTR = cTR;
  }
  /**
   * @return the uniqueCTR
   */
  public Float getUniqueCTR() {
    return uniqueCTR;
  }
  /**
   * @param uniqueCTR the uniqueCTR to set
   */
  public void setUniqueCTR(Float uniqueCTR) {
    this.uniqueCTR = uniqueCTR;
  }
  /**
   * @return the avgCPC
   */
  public Float getAvgCPC() {
    return avgCPC;
  }
  /**
   * @param avgCPC the avgCPC to set
   */
  public void setAvgCPC(Float avgCPC) {
    this.avgCPC = avgCPC;
  }
  /**
   * @return the avgCPM
   */
  public Float getAvgCPM() {
    return avgCPM;
  }
  /**
   * @param avgCPM the avgCPM to set
   */
  public void setAvgCPM(Float avgCPM) {
    this.avgCPM = avgCPM;
  }
  /**
   * @return the cP1KR
   */
  public Float getCP1KR() {
    return CP1KR;
  }
  /**
   * @param cP1KR the cP1KR to set
   */
  public void setCP1KR(Float cP1KR) {
    CP1KR = cP1KR;
  }
  /**
   * @return the actions
   */
  public Integer getActions() {
    return actions;
  }
  /**
   * @param actions the actions to set
   */
  public void setActions(Integer actions) {
    this.actions = actions;
  }
  /**
   * @return the pTA
   */
  public Integer getPTA() {
    return PTA;
  }
  /**
   * @param pTA the pTA to set
   */
  public void setPTA(Integer pTA) {
    PTA = pTA;
  }
  /**
   * @return the likes
   */
  public Integer getLikes() {
    return likes;
  }
  /**
   * @param likes the likes to set
   */
  public void setLikes(Integer likes) {
    this.likes = likes;
  }
  /**
   * @return the websiteClicks
   */
  public Integer getWebsiteClicks() {
    return websiteClicks;
  }
  /**
   * @param websiteClicks the websiteClicks to set
   */
  public void setWebsiteClicks(Integer websiteClicks) {
    this.websiteClicks = websiteClicks;
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
   * @return the visits
   */
  public Integer getVisits() {
    return visits;
  }
  /**
   * @param visits the visits to set
   */
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
  public void setBounceRate(Float bounceRate) {
    this.bounceRate = bounceRate;
  }
  
  //TODO: Generalize the match method. There only needs to be one match method for all vendors.
  /*
   * Pre: The query passed to the GA API requests source, medium and campaign
   * as the first, second and third dimensions. Without this query structure the
   * the wrong dimension from the GA results will be matched always returning false 
   */
  @Override
  public boolean match(List<String> gaRow) {
    if (gaRow.get(0).equals(this.source) && gaRow.get(1).equals(this.medium) && gaRow.get(2).equals(this.campaign)
        && gaRow.get(3).equals(this.adContent)) {
      return true;
    }
    return false;
  }
  
  /*
   * Method is identical to match but includes debug statements when dimensions to not match
   */
  @Override
  public boolean matchDebug(List<String> gaRow) {
    System.out.println("Source, GA response first: " + gaRow.get(0) + "=? " + this.source);
    System.out.println("Medium, GA response first: " + gaRow.get(1) + "=? " + this.medium);
    System.out.println("Campaign, GA response first: " + gaRow.get(2) + "=? " + this.campaign);
    System.out.println("AdContent, GA response first: " + gaRow.get(3) + "=? " + this.adContent);
    System.out.println();
    if (gaRow.get(0).equals(this.source) && gaRow.get(1).equals(this.medium) && gaRow.get(2).equals(this.campaign)
        && gaRow.get(3).equals(this.adContent)) {
      return true;
    }
    return false;
  }


  public String getAdContent() {
    return adContent;
  }


  public void setAdContent(String adContent) {
    this.adContent = adContent;
  }

}
