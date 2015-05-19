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

//TODO: add getter and setter methods
public class GroupID {
  private String campaign;
  private String source;
  private String medium;
  private boolean hasNetwork = false;
  private String network;
  private boolean hasGroup = false;
  private String adGroup;
  private String placement;
  
  //This should be populated at the same time as the vendor specific object
  //Overloading method with different signatures for different vendors
  
  public GroupID(String source, String medium,String campaign) {
    this.source = source;
    this.medium = medium;
    this.campaign = campaign;
  }
  
  //overloaded method accounting for additional matching criteria used with Adwords and vendors
  //that split by the network dimension
  public GroupID(String source, String medium, String campaign, String additionalCriteria) {
    this.campaign = campaign;
    this.source = source;
    this.medium = medium;
    
    //Only adwords and facebook have additional criteria
    if (this.source == "Adwords") {
      this.adGroup = additionalCriteria;
    } else if (source == "Facebook") {
      this.setPlacement(additionalCriteria);
    }//end of else
  }
  
  //If there is a matching issue here it may be due to the addition of placement, however,
  //comparing nulls should cause issue
  public boolean equals(GroupID id) {
    if (this.source.equals(id.getSource()) && this.medium.equals(id.getMedium()) && this.campaign.equals(id.getCampaign())) {
      return true;
    }
    return false;
  }
  
  @Override
  public String toString() {
    String formattedString = "Source: " + this.source +", Medium: " + this.medium + ", Campaign: " + this.campaign;
    return formattedString;
  }



  public static void main(String[] args) {
    // TODO Auto-generated method stub

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

}
