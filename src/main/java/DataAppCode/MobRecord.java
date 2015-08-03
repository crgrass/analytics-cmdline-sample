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
public class MobRecord implements importRecord {
  
  private final int SOURCE_INDEX = 0;
  private final int MEDIUM_INDEX = 0;
  private final int CAMPAIGN_INDEX = 4;
  
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
  //Behavior metrics are missing from the constructor as they are added to the record
  //after construction once the GA API has been called
  public MobRecord(String[] dateArray, String src, String med, String camp, String ntwk, String adc,
                       Integer clks, Integer impr, Float ctr, Float cpc, Float cpm, Float spnd,
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
    setCTR(ctr);
    setAvgCPC(cpc);
    setAvgCPM(cpm);
    setSpend(spnd);
    setTotalConversions(totalcnv);
    setPCConversions(pccnv);
    setPIConversions(picnv);
  }
  
  
  //TODO: The DD aggregate method is in the importDD class. Need to move one method
  //or another to ensure consistency.
  
  /*
   * PreCondition: Raw data is already grouped appropriately
   */
  public static ArrayList<MobRecord> aggregate(HashMap<GroupID,ArrayList<String[]>> rawData) {

    System.out.println("Aggregating rows based on Source, Medium, Campaign and AdContent...\n");
    
    
    //Iterate through HashMap and place only digial display entries into a new HashMap
    HashMap<GroupID,ArrayList<String[]>> onlyMob = new HashMap<GroupID,ArrayList<String[]>>();
    Iterator itr = rawData.entrySet().iterator();
    while (itr.hasNext()) {
      Map.Entry pairs = (Map.Entry)itr.next();
      GroupID currID = (GroupID)pairs.getKey();
      ArrayList<String[]> currArray = (ArrayList<String[]>)pairs.getValue();
      if (currID.getMedium().equals("Mobile")) {
        onlyMob.put(currID, currArray);
      }//end of if
    }//end of while
    
    //The returned ArrayList
    ArrayList<MobRecord> MobRecordCollection = new ArrayList<MobRecord>();

    //Need to loop through Hash Map
    Iterator it = onlyMob.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pairs = (Map.Entry)it.next();
      ArrayList<String[]> currList = (ArrayList<String[]>)pairs.getValue();

      //Metrics are aggregated here
      Integer totalClicks = 0;
      Integer totalImpressions = 0;
      Float totalSpend = 0.0f;
      Integer totalConversions = 0;
      Integer pcConversions = 0;
      Integer piConversions = 0;

      //TODO:Discarding last row as a band aid
      //eventually need to stop importing last row

      for (String[] row : currList) {
        //Index 0 : Start Date, Index 1 : End Date, !!!Index 2 : Campaign!!!
        //!!!Index 3 : Site - This is the source!!!, Index 4 : Placement, !!!Index 5 : Type This is Medium!!!
        //Index 6 : Ad, Index 7 : Size, Index: 8 Clicks
        //Index 9 : Impressions, Index 10 : Views
        //Index 11: CTR, Index 12: Average CPC, Index 13: Average CPM, Index 14: Total Media Cost
        //Index 15: Total Conversions, Index 16: PC Conversions, Index 17: PI Conversions
        totalImpressions += Integer.parseInt(row[9]);
        //TODO: Need to ensure all csvs can handle number formats with commas

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
      //TODO: System.out.println("Ensure this cpm calc is correct: " + aggCPM);
      
      //TODO: checking for NaN and Infinity happens in the updateDD method
      //as opposed to here where it takes place in the aggregate method.
      //Need to bring this into alignment for consistency.
      
      
      //NaN must be converted as MySQL wil not import NaN values
      if (Float.isNaN(aggCTR) || Float.isInfinite(aggCTR)) {
        aggCTR = 0.0f;
      }
      if (Float.isNaN(aggCPC) || Float.isInfinite(aggCPC)) {
        aggCPC = 0.0f;
      }
      if (Float.isNaN(aggCPM) || Float.isInfinite(aggCPM)) {
        aggCPM = 0.0f;
      }      

      //Access start and end dates from DataAppTest static methods and load into array
      String[] dateArray = {guiCode.DataAppTest.startDate.toString(),guiCode.DataAppTest.endDate.toString()};
      
      //Get the GroupID of the current record
      GroupID currID = (GroupID)pairs.getKey();

      MobRecord rec = new MobRecord(dateArray,currID.getSource(),currID.getMedium(),currID.getCampaign(),currID.getSource(),//<- This is network
      currID.getAdContent(),totalClicks,totalImpressions,aggCTR,aggCPC,aggCPM,totalSpend, totalConversions,pcConversions,piConversions);
      MobRecordCollection.add(rec);
    }

    return MobRecordCollection;
  }
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

  public int getRecordCount() {
    return recordCount;
  }

  public void setRecordCount(int recordCount) {
    this.recordCount = recordCount;
  }

  public boolean getPartialWeek() {
    return partialWeek;
  }

  public void setPartialWeek(boolean partialWeek) {
    this.partialWeek = partialWeek;
  }

  public int getDaysActive() {
    return daysActive;
  }

  public void setDaysActive(int daysActive) {
    this.daysActive = daysActive;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getMedium() {
    return medium;
  }

  public void setMedium(String medium) {
    this.medium = medium;
  }

  public String getCampaign() {
    return campaign;
  }

  public void setCampaign(String campaign) {
    this.campaign = campaign;
  }

  public String getNetwork() {
    return network;
  }

  public void setNetwork(String network) {
    this.network = network;
  }

  public Integer getClicks() {
    return clicks;
  }

  public void setClicks(Integer clicks) {
    this.clicks = clicks;
  }

  @Override
  public Integer getImpressions() {
    return impressions;
  }

  public void setImpressions(Integer impressions) {
    this.impressions = impressions;
  }

  public Float getCTR() {
    return CTR;
  }

  public void setCTR(Float cTR) {
    CTR = cTR;
  }

  public Float getAvgCPC() {
    return avgCPC;
  }

  public void setAvgCPC(Float avgCPC) {
    this.avgCPC = avgCPC;
  }

  public Float getAvgCPM() {
    return avgCPM;
  }

  public void setAvgCPM(Float avgCPM) {
    this.avgCPM = avgCPM;
  }

  public Float getSpend() {
    return spend;
  }

  public void setSpend(Float spend) {
    this.spend = spend;
  }

  public Integer getTotalConversions() {
    return totalConversions;
  }

  public void setTotalConversions(Integer totalConversions) {
    this.totalConversions = totalConversions;
  }

  public Integer getPCConversions() {
    return PCConversions;
  }

  public void setPCConversions(Integer pCConversions) {
    PCConversions = pCConversions;
  }

  public Integer getPIConversions() {
    return PIConversions;
  }

  public void setPIConversions(Integer pIConversions) {
    PIConversions = pIConversions;
  }

  public Integer getVisits() {
    return visits;
  }

  @Override
  public void setVisits(Integer visits) {
    this.visits = visits;
  }

  public Float getPagesPerVisit() {
    return pagesPerVisit;
  }

  @Override
  public void setPagesPerVisit(Float pagesPerVisit) {
    this.pagesPerVisit = pagesPerVisit;
  }

  public Float getAvgDuration() {
    return avgDuration;
  }

  @Override
  public void setAvgDuration(Float avgDuration) {
    this.avgDuration = avgDuration;
  }

  public Float getPercentNewVisits() {
    return percentNewVisits;
  }

  @Override
  public void setPercentNewVisits(Float percentNewVisits) {
    this.percentNewVisits = percentNewVisits;
  }

  public Float getBounceRate() {
    return bounceRate;
  }

  @Override
  public void setBounceRate(Float bounceRate) {
    this.bounceRate = bounceRate;
  }

  /*
   * Pre: The query passed to the GA API requests source, medium, campaign and adcontent
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

  public String getAdContent() {
    return adContent;
  }

  public void setAdContent(String adContent) {
    this.adContent = adContent;
  }

}
