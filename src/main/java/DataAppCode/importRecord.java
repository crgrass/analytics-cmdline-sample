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
public interface importRecord {
  
  public Integer getImpressions();
  public void setVisits(Integer e);
  public void setPagesPerVisit(Float e);
  public void setAvgDuration(Float e);
  public void setPercentNewVisits(Float e);
  public void setBounceRate(Float e);
  public boolean match(List<String> e);
  public boolean matchDebug(List<String> e);
}
