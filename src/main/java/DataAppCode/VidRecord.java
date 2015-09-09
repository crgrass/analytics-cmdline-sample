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
public class VidRecord implements importRecord {

  //Contain distinct vendor info inside the
  //vendorRecord classes
//  private final int SOURCE_INDEX = 0;
//  private final int MEDIUM_INDEX = 0;
//  private final int CAMPAIGN_INDEX = 4;
  
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
  private String network; //This is new be sure to include in query
  private String adContent;
  private Integer clicks;
  private Integer impressions;
  private Integer YTViews =0;
  private Float CTR;
  private Float avgCPC;
  private Float avgCPM;
  private Float spend;
  private Integer totalConversions; //This is new be sure to include in query
  private Integer PCConversions; //This is new be sure to include in query
  private Integer PIConversions; //This is new be sure to include in query
  private Integer visits = 0;
  private Float pagesPerVisit = 0.0f;
  private Float avgDuration = 0.0f;
  private Float percentNewVisits = 0.0f;
  private Float bounceRate = 0.0f;
  
  //constructor
  //behavior metrics are always created at a later date
  public VidRecord(String[] dateArray, String src, String med, String camp, String ntwk, String adc,
                       Integer clks, Integer impr,Integer yt, Float ctr, Float cpc, Float cpm, Float spnd,
                       Integer totalcnv, Integer pccnv, Integer picnv) {
    setStartDate(dateArray[0]);
    setEndDate(dateArray[1]);
    setSource(src);
    setMedium(med);
    setCampaign(camp);
    setNetwork(ntwk);
    setAdContent(adc);
    setClicks(clks);
    setImpressions(impr);
    setYTViews(yt);
    setCTR(ctr);
    setAvgCPC(cpc);
    setAvgCPM(cpm);
    setSpend(spnd);
    setTotalConversions(totalcnv);
    setPCConversions(pccnv);
    setPIConversions(picnv);
  }
  
  
  /*
   * PreCondition: Raw data is already grouped appropriately
   */
  public static ArrayList<VidRecord> aggregate(HashMap<GroupID,ArrayList<String[]>> rawData, LocalDate sDate, 
      LocalDate eDate) {
    
    
    //Iterate through HashMap and place only digial display entries into a new HashMap
    HashMap<GroupID,ArrayList<String[]>> onlyVid = new HashMap<GroupID,ArrayList<String[]>>();
    Iterator<Map.Entry<GroupID, ArrayList<String[]>>> itr = rawData.entrySet().iterator();
    while (itr.hasNext()) {
      Map.Entry<GroupID, ArrayList<String[]>> pairs = itr.next();
      GroupID currID = pairs.getKey();
      ArrayList<String[]> currArray = pairs.getValue();
      if (currID.getMedium().equals("Preroll")) {
        onlyVid.put(currID, currArray);
      }//end of if
    }//end of while
    
    
    //The returned ArrayList
    ArrayList<VidRecord> VidRecordCollection = new ArrayList<VidRecord>();

    //Need to loop through Hash Map
    Iterator<Map.Entry<GroupID, ArrayList<String[]>>> it = onlyVid.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<GroupID, ArrayList<String[]>> pairs = it.next();
      ArrayList<String[]> currList = pairs.getValue();

      //Metrics are aggregated here
      Integer totalClicks = 0;
      Integer totalImpressions = 0;
      Integer totalYTViews = 0;
      Float totalSpend = 0.0f;
      Integer totalConversions = 0;
      Integer pcConversions = 0;
      Integer piConversions = 0;
      
      //TODO: Need to be able to handle empty cells that should be zero

      for (String[] row : currList) {
        //Index 0 : Start Date, Index 1 : End Date, !!!Index 2 : Campaign!!!
        //!!!Index 3 : Site - This is the source!!!, Index 4 : Placement, !!!Index 5 : Type This is Medium!!!
        //Index 6 : Ad, Index 7 : Size, Index: 8 Clicks
        //Index 9 : Impressions, Index 10 : Views
        //Index 11: CTR, Index 12: Average CPC, Index 13: Average CPM, Index 14: Total Media Cost
        //Index 15: Total Conversions, Index 16: PC Conversions, Index 17: PI Conversions
        totalImpressions += Integer.parseInt(row[9]);
        //TODO: Need to ensure all csvs can handle number formats with commas
        
        //This is done to catch empty fields that should instead by zero
        try {
          totalYTViews += Integer.parseInt(row[10]);
        } catch (NumberFormatException e) {
          totalYTViews = 0;
        }
        

        //Index 12: Clicks
        totalClicks += Integer.parseInt(row[8]);
        totalSpend += Float.parseFloat(row[14]);
        totalConversions += Integer.parseInt(row[15]);
        pcConversions += Integer.parseInt(row[16]);
        piConversions += Integer.parseInt(row[17]);
      }// end of outer loop

      Float aggCTR = (float)totalClicks/(float)totalImpressions;
      Float aggCPC = totalSpend/(float)totalClicks;
      Float kImpressions = (float)totalImpressions/1000;
      Float aggCPM = totalSpend/kImpressions;
      //TODOL System.out.println("Ensure this cpm calc is correct: " + aggCPM);
      
      if(Float.isNaN(aggCTR) ||
          Float.isInfinite(aggCTR)) {
        aggCTR = 0.0f;
      }
      
      if(Float.isNaN(aggCPC) ||
          Float.isInfinite(aggCPC)) {
        aggCPC = 0.0f;
      }
      
      if(Float.isNaN(aggCPM) ||
          Float.isInfinite(aggCPM)) {
        aggCPM = 0.0f;
      }


      String[] dateArray = {sDate.toString(),eDate.toString()};
      
      GroupID currID = pairs.getKey();

      VidRecord rec = new VidRecord(dateArray,currID.getSource(),currID.getMedium(),currID.getCampaign(),currID.getSource(),//<- This is network
          currID.getAdContent(),totalClicks,totalImpressions,totalYTViews,aggCTR,aggCPC,aggCPM,totalSpend, totalConversions,pcConversions,piConversions);
      VidRecordCollection.add(rec);
    }

    return VidRecordCollection;
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
   * @return the network
   */
  public String getNetwork() {
    return network;
  }


  /**
   * @param network the network to set
   */
  public void setNetwork(String network) {
    this.network = network;
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


  public Integer getYTViews() {
    return YTViews;
  }


  public void setYTViews(Integer yTViews) {
    YTViews = yTViews;
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
   * @return the totalConversions
   */
  public Integer getTotalConversions() {
    return totalConversions;
  }


  /**
   * @param totalConversions the totalConversions to set
   */
  public void setTotalConversions(Integer totalConversions) {
    this.totalConversions = totalConversions;
  }


  /**
   * @return the pCConversions
   */
  public Integer getPCConversions() {
    return PCConversions;
  }


  /**
   * @param pCConversions the pCConversions to set
   */
  public void setPCConversions(Integer pCConversions) {
    PCConversions = pCConversions;
  }


  /**
   * @return the pIConversions
   */
  public Integer getPIConversions() {
    return PIConversions;
  }


  /**
   * @param pIConversions the pIConversions to set
   */
  public void setPIConversions(Integer pIConversions) {
    PIConversions = pIConversions;
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


  /*
   * Pre: The query passed to the GA API requests source, medium and campaign
   * as the first, second and third dimensions. Without this query structure the
   * the wrong dimension from the GA results will be matched always returning false 
   */
  @Override
  public boolean match(List<String> gaRow) {
    if (gaRow.get(0).equals(this.source) && gaRow.get(1).equals(this.medium) && gaRow.get(2).equals(this.campaign) &&
        gaRow.get(3).equals(this.adContent)) {
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
  
  @Override
  public boolean matchDebug(List<String> gaRow) {
    return true;
  }

}// end of VidRecord

