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

package test.Java;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import guiCode.DataAppTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import DataAppCode.CSVReaders;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class CSVReadersTest {
  
  LocalDate startDate;
  LocalDate nextReportingCycle;
  LocalDate prevReportingCycle;

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    //Load csv using dropbox methods
  }

  @Before
  public void setUp() throws Exception {
  //Load relevant dates
    startDate = LocalDate.of(2015, 3, 17);
    nextReportingCycle = LocalDate.of(2015, 3, 24);
    prevReportingCycle = LocalDate.of(2015, 3, 16);
  }

  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for {@link DataAppCode.CSVReaders#removeInvalidDates(java.util.ArrayList, java.lang.String)}.
   */
  @Test
  public void testRemoveInvalidDatesCentro() {
    System.out.println("Testing method for CSVReaders.removeInvalidDates Centro\n");
    System.out.println("Reading data from resources folder...\n");
    ArrayList<String[]> testData = CSVReaders.readCsv("C:/Users/cgrass/workspace/analytics-cmdline-sample/src/main/java/test/Resources/CSVReadersTest_Summer_3.1.15-5.27.15_Daily.csv");
    CSVReaders.removeHeader(testData);
    System.out.println("File read complete.\n");
    System.out.println("The initial size of the testData is: " + testData.size());
  
    //Need to read data from file into an array list
    CSVReaders.removeInvalidDates(testData,"Centro",startDate);
    System.out.println("Method Completed");
    System.out.println(testData.size() + " records remain in testData array\n");
    
    
    for (String[] row: testData) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
      LocalDate currDate = LocalDate.parse(row[0], formatter); //date is housed in first row of Centro file
      assertTrue("Date after reporting cycle found.",currDate.compareTo(nextReportingCycle) < 0);
      assertTrue("Date before reporting cycle found.",currDate.compareTo(prevReportingCycle) > 0);
    }
    
    System.out.println("Testing method complete.");
  }
  
  
  /**
   * Test method for {@link DataAppCode.CSVReaders#removeInvalidDates(java.util.ArrayList, java.lang.String)}.
   */
  @Test
  public void testRemoveInvalidDatesFacebook() {
    System.out.println("\n\nTesting method for CSVReaders.removeInvalidDates Facebook\n");
    System.out.println("Reading data from resources folder...\n");
    ArrayList<String[]> testData = CSVReaders.readCsv("C:/Users/cgrass/workspace/analytics-cmdline-sample/src/main/java/test/Resources/removeInvalidDates_Test_3.9.2015-4.15-2015.csv");
    CSVReaders.removeHeader(testData);
    System.out.println("File read complete.\n");
    System.out.println("The initial size of the testData is: " + testData.size());
  
    //Need to read data from file into an array list
    CSVReaders.removeInvalidDates(testData,"Facebook",startDate);
    System.out.println("Method Completed");
    System.out.println(testData.size() + " records remain in testData array\n");
    
    
    for (String[] row: testData) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      LocalDate currDate = LocalDate.parse(row[2], formatter); //date is housed in first row of Centro file
      assertTrue("Date after reporting cycle found.",currDate.compareTo(nextReportingCycle) < 0);
      assertTrue("Date before reporting cycle found.",currDate.compareTo(prevReportingCycle) > 0);
    }
    
    System.out.println("Testing method complete.");
  }
  
  /**
   * Test method for {@link DataAppCode.CSVReaders#removeInvalidDates(java.util.ArrayList, java.lang.String)}.
   */
  @Test
  public void testRemoveInvalidDatesTwitter() {
    System.out.println("\n\nTesting method for CSVReaders.removeInvalidDates Twitter\n");
    System.out.println("Reading data from resources folder...\n");
    ArrayList<String[]> testData = CSVReaders.readCsv("C:/Users/cgrass/workspace/analytics-cmdline-sample/src/main/java/test/Resources/removeInvalidDates_Test_3.12015-3.31.2015_Twitter.csv");
    CSVReaders.removeHeader(testData);
    System.out.println("File read complete.\n");
    System.out.println("The initial size of the testData is: " + testData.size());
  
    //Need to read data from file into an array list
    CSVReaders.removeInvalidDates(testData,"Twitter",startDate);
    System.out.println("Method Completed");
    System.out.println(testData.size() + " records remain in testData array\n");
    
    for (String[] record : testData) {
      System.out.println(record[5]);
    }
    
    
    for (String[] row: testData) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d H:m Z");
      LocalDate currDate = LocalDate.parse(row[5], formatter); //date is housed in first row of Centro file
      assertTrue("Date after reporting cycle found.",currDate.compareTo(nextReportingCycle) < 0);
      assertTrue("Date before reporting cycle found.",currDate.compareTo(prevReportingCycle) > 0);
    }
    
    System.out.println("Testing method complete.");
  }
  
}
