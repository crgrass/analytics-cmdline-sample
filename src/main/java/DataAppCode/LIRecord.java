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





public class LIRecord implements importRecord {
  
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
  private String source;
  private String medium;
  private String campaign;
  private Integer clicks;
  private Integer impressions;
  private Float CTR;
  private Float avgCPC;
  private Float avgCPM;
  private Float spend;
  private Integer visits;
  private Float pagesPerVisit;
  private Float avgDuration;
  private Float percentNewVisits;
  private Float bounceRate;
  
  
  
  
  //constructor
  //behavior metrics are always created at a later date
  public LIRecord(String[] dateArray, String src, String med, String camp,
                       Integer clks, Integer impr, Float ctr, Float cpc, Float cpm, Float spnd) {
    setStartDate(dateArray[0]);
    setEndDate(dateArray[1]);
    setSource(src);
    setMedium(med);
    setCampaign(camp);
    setClicks(clks);
    setImpressions(impr);
    setCTR(ctr);
    setAvgCPC(cpc);
    setAvgCPM(cpm);
    setSpend(spnd);
  }
  
  /*
   * Pre: The query passed to the GA API requests source, medium and campaign
   * as the first, second and third dimensions. Without this query structure the
   * the wrong dimension from the GA results will be matched always returning false 
   */
  @Override
  public boolean match(List<String> gaRow) {
    if (gaRow.get(0).equals(this.source) && gaRow.get(1).equals(this.medium) && gaRow.get(2).equals(this.campaign) ) {
      return true;
    }
    return false;
  }
  
  public boolean matchDebug(List<String> gaRow) {
    return true;
  }
  
  
 
  /*
   * PreCondition: Raw data is already grouped appropriately
   */
  public static ArrayList<LIRecord> aggregate(HashMap<GroupID,ArrayList<String[]>> rawData) {
    System.out.println("Aggregating rows based on Source, Medium and Campaign...\n");
    
    ArrayList<LIRecord> LIRecordCollection = new ArrayList<LIRecord>();

    //Need to loop through Hash Map
    Iterator<Map.Entry<GroupID, ArrayList<String[]>>> it = rawData.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<GroupID, ArrayList<String[]>> pairs = it.next();
      //pairs.getValue() is the adwords record
      ArrayList<String[]> currList = pairs.getValue();

      //Metrics are aggregated here
      Integer totalClicks = 0;
      Integer totalImpressions = 0;
      Float totalSpend = 0.0f;

      for (String[] row : currList) {
        //Index 0 : Date, Index 1 : Advertiser Name, Index 2 : Currency
        //Index 3 : Campaign ID, Index 4 : Campaign Name, Index 5 : Campaign Type
        //Index 6 : Campaign Status, Index 7 :Cost Type, Index:8 Campaign Daily Budget
        //Index 9 : Campaign Total Budget, Index 10 : Lead Count
        //Index 11: Impressions
        totalImpressions += Integer.parseInt(row[11]);

        //Index 12: Clicks
        totalClicks += Integer.parseInt(row[12]);
        //Index 13: Other Clicks, Index 14: Social Actions, Index 15: Total Clicks
        //Index 16: CTR, Index 17: Total CTR, Index 18: Avg. CPC
        //Index 19: Average CPM, Index 20: Total Spent
        totalSpend += Float.parseFloat(row[20]);
      }// end of outer loop

      Float aggCTR = (float)totalClicks/(float)totalImpressions;
      Float aggCPC = totalSpend/(float)totalClicks;
      Float kImpressions = (float)totalImpressions/1000;
      Float aggCPM = totalSpend/kImpressions;

      String startDate = guiCode.DataAppTest.startDate.toString();
      String endDate = guiCode.DataAppTest.endDate.toString();
      String[] dateArray = {startDate,endDate};

      LIRecord rec = new LIRecord(dateArray,"LinkedIn","Social","FY2015_PDP",
          totalClicks,totalImpressions,aggCTR,aggCPC,aggCPM,totalSpend);
      LIRecordCollection.add(rec);
    }

    return LIRecordCollection;
  }

  
  
  
  public int getSourceIndex() {
    return SOURCE_INDEX;
  }

  public int getMEDIUM_INDEX() {
    return MEDIUM_INDEX;
  }

  public int getCAMPAIGN_INDEX() {
    return CAMPAIGN_INDEX;
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



  public Float getAvgCPC() {
    return avgCPC;
  }



  public void setAvgCPC(Float avgCPC) {
    this.avgCPC = avgCPC;
  }



  public Float getCTR() {
    return CTR;
  }



  public void setCTR(Float cTR) {
    CTR = cTR;
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



  public int getDaysActive() {
    return daysActive;
  }



  public void setDaysActive(int daysActive) {
    this.daysActive = daysActive;
  }



  public boolean getPartialWeek() {
    return partialWeek;
  }



  public void setPartialWeek(boolean partialWeek) {
    this.partialWeek = partialWeek;
  }



  public int getRecordCount() {
    return recordCount;
  }



  public void setRecordCount(int recordCount) {
    this.recordCount = recordCount;
  }



  @Override
  public String toString() {
    String printThis = "";
    printThis += "Start Date: " + this.getStartDate() + "\n";
    printThis += "End Date: " + this.getEndDate() + "\n";
    printThis += "Source: " + this.getSource() + "\n";
    printThis += "Medium: " + this.getMedium() + "\n";
    printThis += "Campaign: " + this.getCampaign() + "\n";
    printThis += "Clicks: " + this.getClicks() + "\n";
    printThis += "Impressions " + this.getImpressions() + "\n";
    printThis += "CTR: " + this.getCTR() + "\n";
    printThis += "Average CPC: " + this.getAvgCPC() + "\n";
    printThis += "Spend: " + this.getSpend() + "\n";
    printThis += "Visits: " + this.getVisits() + "\n";
    printThis += "Pages Per Visit: " + this.getPagesPerVisit() + "\n";
    printThis += "Average Duration: " + this.getAvgDuration() + "\n";
    printThis += "Percent New Visits: " + this.getPercentNewVisits() + "\n";
    printThis += "Bounce Rate: " + this.getBounceRate() + "\n";
    printThis += "Partial Week: " + this.getPartialWeek() + "\n";
    printThis += "Days Active: " + this.getDaysActive() + "\n";
    return printThis;
  }
  
  /*
   * Turn this into a debugging method to check alignment of
   * indexes and values
   */
//  for (String[] row: rawData){
//    System.out.println("Row 0: " + row[0]);
//    System.out.println("Row 1: " + row[1]);
//    System.out.println("Row 2: " + row[2]);
//    System.out.println("Row 3: " + row[3]);
//    System.out.println("Row 4: " + row[4]);
//    System.out.println("Row 5: " + row[5]);
//    System.out.println("Row 6: " + row[6]);
//    System.out.println("Row 7: " + row[7]);
//    System.out.println("Row 8: " + row[8]);
//    System.out.println("Row 9: " + row[9]);
//    System.out.println("Row 10: " + row[10]);
//    System.out.println("Row 11: " + row[11]);
//    System.out.println("Row 12: " + row[12]);
//    System.out.println("Row 13: " + row[13]);
//    System.out.println("Row 14: " + row[14]);
//    System.out.println("Row 15: " + row[15]);
//    System.out.println("Row 16: " + row[16]);
//    System.out.println("Row 17: " + row[17]);
//    System.out.println("Row 18: " + row[18]);
//    System.out.println("end of row");
//  }
  
  public static void main(String[] args) {
    System.out.println("Testing method for LIRecord class");
    
    
  }


}
