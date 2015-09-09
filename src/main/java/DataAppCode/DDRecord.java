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

import java.util.List;


/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class DDRecord implements importRecord {
  
  //TODO: Determine way to store vendor specific data in the vendorRecor itself
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
  public DDRecord(String[] dateArray, String src, String med, String camp, String ntwk, String adc,
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
 
  
  
  @Override
  public String toString() {
    String printThis = "";
    printThis += "Start Date: " + this.getStartDate() + "\n";
    printThis += "End Date: " + this.getEndDate() + "\n";
    printThis += "Source: " + this.getSource() + "\n";
    printThis += "Medium: " + this.getMedium() + "\n";
    printThis += "Campaign: " + this.getCampaign() + "\n";
    printThis += "Network: " + this.getNetwork() + "\n";
    printThis += "Clicks: " + this.getClicks() + "\n";
    printThis += "Impressions " + this.getImpressions() + "\n";
    printThis += "CTR: " + this.getCTR() + "\n";
    printThis += "Average CPC: " + this.getAvgCPC() + "\n";
    printThis += "Spend: " + this.getSpend() + "\n";
    printThis += "Total Conversions: " + this.getTotalConversions() + "\n";
    printThis += "Post Click Conversions: " + this.getPCConversions() + "\n";
    printThis += "Post Impressions Conversions: " + this.getPIConversions() + "\n";
    printThis += "Visits: " + this.getVisits() + "\n";
    printThis += "Pages Per Visit: " + this.getPagesPerVisit() + "\n";
    printThis += "Average Duration: " + this.getAvgDuration() + "\n";
    printThis += "Percent New Visits: " + this.getPercentNewVisits() + "\n";
    printThis += "Bounce Rate: " + this.getBounceRate() + "\n";
    printThis += "Partial Week: " + this.getPartialWeek() + "\n";
    printThis += "Days Active: " + this.getDaysActive() + "\n";
    return printThis;
  }
  

  /**
   * @param args
   */
  public static void main(String[] args) {
    System.out.println("Testing method for DDRecord.");

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

  public String getAdContent() {
    return adContent;
  }

  public void setAdContent(String adContent) {
    this.adContent = adContent;
  }
  
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

}
