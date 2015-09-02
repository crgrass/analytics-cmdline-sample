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

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class AdwordsRecord {
  
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
  private String adGroup;
  private Integer clicks;
  private Integer impressions;
  private Float CTR;
  private Float avgCPC;
  private Float spend;
  private Integer visits;
  private Float pagesPerVisit;
  private Float avgDuration;
  private Float percentNewVisits;
  private Float bounceRate;
  
  
  public AdwordsRecord(String[] dateArray, String src, String med, String camp,
                       String adg, Integer clks, Integer impr, Float ctr, Float cpc, Float spnd,
                       Integer vsts, Float ppv, Float avgDur, Float pnv, Float bounce) {
    
    setStartDate(dateArray[0]);
    setEndDate(dateArray[1]);
    source = src;
    medium = med;
    campaign = camp;
    adGroup = adg;
    clicks = clks;
    impressions = impr;
    CTR = ctr;
    avgCPC = cpc;
    spend = spnd;
    visits = vsts;
    pagesPerVisit = ppv;
    avgDuration = avgDur;
    percentNewVisits = pnv;
    bounceRate = bounce;
  }
  
  @Override
  public String toString() {
    String printThis = "";
    printThis += "Start Date: " + this.getStartDate() + "\n";
    printThis += "End Date: " + this.getEndDate() + "\n";
    printThis += "Source: " + this.getSource() + "\n";
    printThis += "Medium: " + this.getMedium() + "\n";
    printThis += "Campaign: " + this.getCampaign() + "\n";
    printThis += "AdGroup: " + this.getAdGroup() + "\n";
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
   * Getter and Setter Methods below
   */
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


  public String getAdGroup() {
    return adGroup;
  }


  public void setAdGroup(String adGroup) {
    this.adGroup = adGroup;
  }


  public int getClicks() {
    return clicks;
  }


  public void setClicks(int clicks) {
    this.clicks = clicks;
  }


  public int getImpressions() {
    return impressions;
  }


  public void setImpressions(int impressions) {
    this.impressions = impressions;
  }


  public float getCTR() {
    return CTR;
  }


  public void setCTR(float cTR) {
    CTR = cTR;
  }


  public float getAvgCPC() {
    return avgCPC;
  }


  public void setAvgCPC(float avgCPC) {
    this.avgCPC = avgCPC;
  }


  public float getSpend() {
    return spend;
  }


  public void setSpend(float spend) {
    this.spend = spend;
  }


  public int getVisits() {
    return visits;
  }


  public void setVisits(int visits) {
    this.visits = visits;
  }


  public float getPagesPerVisit() {
    return pagesPerVisit;
  }


  public void setPagesPerVisit(float pagesPerVisit) {
    this.pagesPerVisit = pagesPerVisit;
  }


  public Float getAvgDuration() {
    return avgDuration;
  }


  public void setAvgDuration(Float avgDuration) {
    this.avgDuration = avgDuration;
  }


  public float getPercentNewVisits() {
    return percentNewVisits;
  }


  public void setPercentNewVisits(float percentNewVisits) {
    this.percentNewVisits = percentNewVisits;
  }


  public float getBounceRate() {
    return bounceRate;
  }


  public void setBounceRate(float bounceRate) {
    this.bounceRate = bounceRate;
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

  /**
   * @param args
   */
  public static void main(String[] args) {
    System.out.println("Testing Method for AdwordsRecord Class");

  }

}
