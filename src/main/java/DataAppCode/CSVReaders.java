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

import java.io.BufferedReader;

import au.com.bytecode.opencsv.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;


/*
 * Reference http://www.beingjavaguys.com/2013/09/read-and-parse-csv-file-in-java.html
 */
public class CSVReaders {
  
  /*
   * readCSV accepts a filepath as a parameter. The file
   * is then parsed line by line and added to an array
   * of strings for later processing
   */
  public static ArrayList<String[]> readCsv(String filePath) {
    BufferedReader br = null;
    String line = "";
    String splitBy = ",";
    
    //returnedDataStructure
    ArrayList<String[]> rawData = new ArrayList<String[]>();
    try {
      br = new BufferedReader(new FileReader(filePath));
      //read lines
      while ((line = br.readLine()) != null) {
        String[] csvData = line.split(splitBy);
        //TODO:This is where we test for correct dates
        
        rawData.add(csvData); //add line to scv
      } // end of while
      
    } catch(FileNotFoundException e) {
      e.printStackTrace();
    } catch(IOException e) {
      e.printStackTrace();
    } finally {
      //Close the buffered reader
      if (br != null) {
        try {
          br.close();
        } catch(IOException e) {
          e.printStackTrace();
        }//end of catch
      }//end of if
    }//end of finally
    
    return rawData;
  }// end of CSVReaders
  
  
  /*
   * Print raw data is a convenience method that is used 
   * to view the raw data after it has been read into the
   * string array. This method is only used for debugging.
   */
  public static void printRawData(ArrayList<String[]> raw) {
    for (String[] row : raw) {
      for (int i = 0; i < row.length; i++) {
        System.out.print(row[i] + " ");
      }//end of inner loop
      System.out.println ();
    }// end of outer loop
  } //end of printRawData
  
  /*
   * removeHeader removes the first row from the file
   */
  //TODO: This method can be generalized if a parameter
  //containing the number of rows to remove is passed. 
  //Each vendor record could contain this static variable
  //PreCondition: Header is first Array in ArrayList
  public static void removeHeader(ArrayList<String[]> raw) {
    raw.remove(0);
  }
  

  /*
   * Exported Double Click reports contain report meta data in 
   * the first 9 rows of the file. Thus, requiring a different
   * method to trim the header. This method also removes the
   * last row which contains totals.
   */
  public static void formatDCMData(ArrayList<String[]> raw) {
    //Remove first 10 rows (header)
    for (int i = 0; i < 10; i++) {
      //This is inefficient and should be optimized
      raw.remove(0);
    }
    //Remove Grand Total (footer)
    raw.remove(raw.size()-1);
  }
  
  /*
   * removeInvalidDates parses files that contain data for more than one reporting cycle. When
   * a row from outside of the specified reporting cycle is found, the row is removed.
   */
  public static void removeInvalidDates(ArrayList<String[]> raw, String vendor, LocalDate startDate) {
  
    //if start date positions and format are inconsistent create either a dictionary that 
    //contains all startDate indexes or make this a final field in the object
    //if this is the case medium will likely need to be passed as a parameter
    HashMap<String,LinkedList<String>> vendorConfig = new HashMap<String, LinkedList<String>>();
    
    //TODO: this is currently going to build the vendorConfig library everytime the 
    //method is run. It would be more efficient to build this once per application
    //run time.
    
    vendorConfig.put("Centro", new LinkedList<String>(Arrays.asList("M/d/yyyy","0"))); // mm/dd/yyyy
    vendorConfig.put("Facebook", new LinkedList<String>(Arrays.asList("yyyy-MM-dd","2"))); // mm/dd/yyyy
    vendorConfig.put("Twitter", new LinkedList<String>(Arrays.asList("yyyy-M-d H:m Z","5"))); // yyyy-mm-dd hh:mm -0400(timezone)
    vendorConfig.put("LinkedIn", new LinkedList<String>(Arrays.asList("L dd, yyyy","0")));// Mar dd, yyyy
    vendorConfig.put("DoubleClick", new LinkedList<String>(Arrays.asList("yyyy-M-d","0")));// mm/dd/yyyy yyyy-M-d
    
    
    
    //iterate through array
    for (Iterator<String[]> it = raw.iterator(); it.hasNext();) {
      String[] currRow = it.next();
      //accesses the correct row for the date string from the vendorConfig dictionary
      String dateString = currRow[Integer.parseInt(vendorConfig.get(vendor).get(1))];
      
      //access the format pattern from the vendor config dict
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(vendorConfig.get(vendor).get(0));
      LocalDate currDate = LocalDate.parse(dateString, formatter);
      
      //compare date to startDate provided from user input
      //remove values outside startDate + 6
      if (currDate.isBefore(startDate) 
          || currDate.isAfter(startDate.plusDays(6))) {
       it.remove();
      }
    } 
  }//end of remove invalid dates
  
  public static ArrayList<String[]> readLICsv(String filePath) throws IOException {
    ArrayList<String[]> rawData = new ArrayList<String[]>();
    //Build reader instance
    //Read data.csv
    //Default separator is comma
    //Default quote character is double double quote
    //Start reading from line number 2 (line numbers start from zero)
    try {
      CSVReader reader = new CSVReader(new FileReader(filePath));
      //returnedDataStructure
      String[] line;
      while ((line = reader.readNext()) != null) {
        String[] csvData = line;
        rawData.add(csvData);
      }
      reader.close();
    } catch (FileNotFoundException e) {
      System.out.println("The file was not found");
    }
    return rawData;
  }// end of CSVReaders
  
  /*
   * The correctDate method generates a map which contains
   * the relevant indexes that contain the dates in each
   * vendors import file.
   */
  public static boolean correctDate(String filePath) {
    Map<String,Integer[]> dateIndexes = new HashMap<String,Integer[]>();
    //If there are two integers in the array this indicates that both the
    //start and end dates are relevant
    dateIndexes.put("Google Adwords", new Integer[] {0,0});
    
    dateIndexes.put("Centro Digital Display", new Integer[] {0,1});
    
    dateIndexes.put("Centro Mobile Display", new Integer[] {0,1});
    
    dateIndexes.put("Centro Video Display", new Integer[] {0,1});
    
    dateIndexes.put("Centro Rich Media", new Integer[] {0,1});
    
    dateIndexes.put("Facebook", new Integer[] {0,1});
    
    dateIndexes.put("Twitter", new Integer[] {5});
    
    dateIndexes.put("LinkedIn", new Integer[] {0});
    
    return true;
  }
  
  
  //Used for testing
  public static void main(String[] args) {
    ArrayList<String[]> rawData = new ArrayList<String[]>();
    try {
      rawData = readLICsv("src/main/resources/testTwitter.csv");
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("Printing raw data: ");
    printRawData(rawData);
    System.out.println();
    printRawData(rawData);

  }//end of main

}//end of csv reader





