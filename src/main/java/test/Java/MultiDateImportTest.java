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

import DataAppCode.MultiDateImport;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class MultiDateImportTest {
  
  LocalDate startDate = LocalDate.of(2015,03,03);
  LocalDate endDate = LocalDate.of(2015,04,07);


  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for {@link DataAppCode.MultiDateImport#generateStartDates(java.time.LocalDate, java.time.LocalDate)}.
   */
  @Test
  public void testGenerateStartDates() {
    System.out.println("Testing that appropriate number of dates exists");
    System.out.println("For the startDates 03-03 to 04-07 there should be six startDates");
    ArrayList<LocalDate> dates = MultiDateImport.generateStartDates(startDate, endDate);
    for (LocalDate e : dates) {
      System.out.println(e.toString());
    }
    assertTrue("There was an unexpected number of events.(" + dates.size() +")", dates.size() == 6);
    System.out.println("\nTesting that all values are Tuesdays");
    for (int i=0; i < dates.size(); i++) {
      LocalDate currDate = dates.get(i);
      assertTrue("A date other than Tuesday was found.", currDate.getDayOfWeek() == DayOfWeek.TUESDAY);
    }
  }

}
