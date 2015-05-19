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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class helpers {


  public static Calendar stringToCalendar(String date) throws ParseException {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-mm-dd");
    cal.setTime(sdf1.parse(date));
    System.out.print(cal.getTime());
    return cal;
  }
  
  
  public static void main(String[] args) {
    System.out.println(System.getProperty("java.runtime.version"));
    
    try {
      stringToCalendar("2015");
    } catch (ParseException e) {
     e.printStackTrace(); 
    }

  }

}
