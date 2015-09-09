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
import java.util.List;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
/*
 * The Vendor Record is the superclass of all individual vendor methods
 */

public class VendorRecord implements importRecord {
  
  //Acquisition metrics relevant to all vendors
  LocalDate startDate;
  LocalDate endDate;
  String source;
  String medium;
  String campaign;
  String clicks;
  Integer impressions;
  Float CTR;
  Float avgCPC;
  Float spend;
  Integer visits;
  Float pagesPerVisit;
  Float avgDuration;
  Float percentNewVisits;
  Float bounceRate;
  
  //Behavior metrics relevant to all vendors
  
  
  
  @Override
  public Integer getImpressions() {
    return impressions;
  }
  
  @Override
  public void setVisits(Integer visits) {
    this.visits = visits;
  }
  
  
  @Override
  public void setPagesPerVisit(Float pagesPerVisit) {
   this.pagesPerVisit = pagesPerVisit; 
  }
  
  @Override
  public void setAvgDuration(Float avgDuration){
    this.avgDuration = avgDuration;
  }
  
  
  @Override
  public void setPercentNewVisits(Float percentNewVisits){
    this.percentNewVisits = percentNewVisits;
  }
  
  
  @Override
  public void setBounceRate(Float bounceRate) {
    this.bounceRate = bounceRate;
  }
  
  @Override
  public boolean match(List<String> e){
    return true;
  }
  
  
  @Override
  public boolean matchDebug(List<String> e){
    return true;
  }

}
