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

package guiCode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;



/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class DateMethods {
  
  
  //Find most recent complete Tuesday-Monday reporting cycle
  public static Calendar[] findLastReportingCycle() {
    Calendar today = Calendar.getInstance();
    
    today.add(Calendar.DAY_OF_MONTH,-7); //Go back one week
    
    while (today.get(Calendar.DAY_OF_WEEK) != 3) { //If day is not Tuesday subtract a day until Tuesday is found
      today.add(Calendar.DAY_OF_MONTH, -1);
    }
    
    Calendar startDate = today;
    Calendar endDate = Calendar.getInstance();//Add six days to find the end of reporting cycle
    endDate.set(Calendar.YEAR, startDate.get(Calendar.YEAR));
    endDate.set(Calendar.MONTH, startDate.get(Calendar.MONTH));
    endDate.set(Calendar.DAY_OF_MONTH, startDate.get(Calendar.DAY_OF_MONTH));
    endDate.add(Calendar.DAY_OF_MONTH, 6);
    
    Calendar[] repCycle = {startDate,endDate};
    
    return repCycle;
  }
  
  //Since Centro reporting lags behind a week the dataImport for this
  //vendor will be one week behind
  public static Calendar[] findLastCentroCycle(Calendar[] repCycle) {
    //Subtract a week from the startDate
    Calendar centroStart = Calendar.getInstance();
    centroStart.set(Calendar.YEAR, repCycle[0].get(Calendar.YEAR));
    centroStart.set(Calendar.MONTH, repCycle[0].get(Calendar.MONTH));
    centroStart.set(Calendar.DAY_OF_MONTH, repCycle[0].get(Calendar.DAY_OF_MONTH));
    centroStart.add(Calendar.DAY_OF_MONTH, -7);
    
    //Subtract a week form the endDate
    Calendar centroEnd = Calendar.getInstance();
    centroEnd.set(Calendar.YEAR, repCycle[1].get(Calendar.YEAR));
    centroEnd.set(Calendar.MONTH, repCycle[1].get(Calendar.MONTH));
    centroEnd.set(Calendar.DAY_OF_MONTH, repCycle[1].get(Calendar.DAY_OF_MONTH));
    centroEnd.add(Calendar.DAY_OF_MONTH, -7);
    
    Calendar[] centroRepCycle = {centroStart,centroEnd};
  
    return centroRepCycle;
  }
  
  /*
   * Overloaded testing method that accepts a date
   */
  public static Calendar[] findLastReportingCycle(Calendar date) {
    //Go back one week
    date.add(Calendar.DAY_OF_MONTH,-7);
    
    //If day is not Tuesday subtract a day until Tuesday is found
    while (date.get(Calendar.DAY_OF_WEEK) != 3) {
      date.add(Calendar.DAY_OF_MONTH, -1);
    }
    
    Calendar startDate = date;
    
    //Add six days to find the end of reporting cycle
    Calendar endDate = Calendar.getInstance();
    endDate.set(Calendar.YEAR, startDate.get(Calendar.YEAR));
    endDate.set(Calendar.MONTH, startDate.get(Calendar.MONTH));
    endDate.set(Calendar.DAY_OF_MONTH, startDate.get(Calendar.DAY_OF_MONTH));
    endDate.add(Calendar.DAY_OF_MONTH, 6);
    
    Calendar[] repCycle = {startDate,endDate};
    
    return repCycle;
  }
  
  public static boolean isTuesday(Calendar date) {
    //return true if selected date is a Tuesday
    return (date.get(Calendar.DAY_OF_WEEK) == 3);
  }


  public static void main(String[] args) {
    Calendar test1 = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdfDOW = new SimpleDateFormat("EE");
    
    //Don't forget that these are zero indexed
    test1.set(Calendar.YEAR, 2015);
    test1.set(Calendar.MONTH, 1);
    test1.set(Calendar.DAY_OF_MONTH, 15);
    System.out.println(sdf.format(test1.getTime()));
   
    Calendar[] reportingCycle = findLastReportingCycle(test1);
    Calendar[] centroReportingCycle = findLastCentroCycle(reportingCycle);
    
    System.out.println("Start Date: " + sdf.format(reportingCycle[0].getTime()) + ","
        + " End Date: " + sdf.format(reportingCycle[1].getTime()));
    System.out.println("Centro Start Date: " + sdf.format(centroReportingCycle[0].getTime()) + ","
        + " Centro End Date: " + sdf.format(centroReportingCycle[1].getTime()));
    
    System.out.println("\n Testing isTuesday method");
    
    for (int i = 0; i < 10; i++) {
      Calendar test = Calendar.getInstance();
      test.set(Calendar.DAY_OF_MONTH, i+1);
      System.out.println("Test Date: " + sdf.format(test.getTime()));
      System.out.println("Day of Week: " + sdfDOW.format(test.getTime()));
      System.out.print("isTuesday Result -->");
      System.out.println(isTuesday(test) +"\n");
    }
    


  }

}
