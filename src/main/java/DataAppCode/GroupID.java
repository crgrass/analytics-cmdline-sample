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

public class GroupID {
  private String campaign;
  private String source;
  private String medium;
  private boolean hasNetwork = false;
  private String network;
  private boolean hasGroup = false;
  private String adGroup; //only used for Adwords
  private String placement; //only used for Facebook
  //this field is used when creative with different messaging is run in the same campaign
  private String adContent;
  
  
  /*
   * The following three methods are constructs that are overloaded with additional parameters
   * when they are needed. The simplest matching of acquisition data requires only three parameters
   * the source, mediume and campaign. However adwords and facebook have an additional criteria
   * (AdGroup and Placement). A third constructor was created when two different sub-campaigns
   * were run under the same campaign in Spring 2015. This means that Digital Display, Twitter and
   * Facebook additional needed to be split by another criteria ad content. (The time is now campaign vs.
   * the )
   */
  

  //This is used for Twitter as we don't split this medium by adContent
  public GroupID(String source, String medium,String campaign) {
    this.source = source;
    this.medium = medium;
    this.campaign = campaign;
  }
  
  //Constructor
  public GroupID(String source, String medium, String campaign, String adContent) {
    this.campaign = campaign;
    this.source = source;
    this.medium = medium;
    this.adContent = adContent;
  }
  
  //Overloaded constructor used for Facebook to account for Placement identifier (Newsfeed/Right Rail)
  //Note: adContent is used for Ad Group with SEM
  public GroupID(String source, String medium, String campaign,String adContent, String placement) {
    this.campaign = campaign;
    this.source = source;
    this.medium = medium;
    this.adContent = adContent;
    this.placement = placement;
  }
  
  
  //If there is a matching issue here it may be due to the addition of placement, however,
  //comparing nulls shouldn't cause issue
  public boolean equals(GroupID id) {
    if (this.source.equals(id.getSource()) && this.medium.equals(id.getMedium()) && this.campaign.equals(id.getCampaign())
        && this.adContent.equals(id.getAdContent())) {
      return true;
    }
    return false;
  }
  
  //Twitter does not split by adContent
  public boolean twitterEquals(GroupID id) {
    if (this.source.equals(id.getSource()) && this.medium.equals(id.getMedium()) && this.campaign.equals(id.getCampaign())) {
      return true;
    }
    return false;
  }
  
  
  
  
  
  
  @Override
  public String toString() {
    String formattedString = "Source: " + this.source +", Medium: " + this.medium + ", Campaign: " + this.campaign + ", AdContent:  " + this.adContent;
    return formattedString;
  }



  public static void main(String[] args) {

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
   * @return the hasNetwork
   */
  public boolean getHasNetwork() {
    return hasNetwork;
  }

  /**
   * @param hasNetwork the hasNetwork to set
   */
  public void setHasNetwork(boolean hasNetwork) {
    this.hasNetwork = hasNetwork;
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
   * @return the hasGroup
   */
  public boolean getHasGroup() {
    return hasGroup;
  }

  /**
   * @param hasGroup the hasGroup to set
   */
  public void setHasGroup(boolean hasGroup) {
    this.hasGroup = hasGroup;
  }

  /**
   * @return the adGroup
   */
  public String getAdGroup() {
    return adGroup;
  }

  /**
   * @param adGroup the adGroup to set
   */
  public void setAdGroup(String adGroup) {
    this.adGroup = adGroup;
  }

  public String getPlacement() {
    return placement;
  }

  public void setPlacement(String placement) {
    this.placement = placement;
  }

  public String getAdContent() {
    return adContent;
  }

  public void setAdContent(String adContent) {
    this.adContent = adContent;
  }

}
