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

import com.dropbox.core.DbxException;

import guiCode.DataAppTest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author cgrass@google.com (Your Name Here)
 *The purpose of ImportCentro is to create a set of generalized import methods that 
 * will handle all Centro mediums including Display, Video and Mobile.
 */



public class ImportCentro {
  
  
  
  /*
   * importCentro imports data from the Centro vendor file.
   * The mediums parameter should contain one of four string
   * values (Display, Video, Mobile, or Rich). This parameter indicates
   * which data should be imported.
   * 
   */
  public static void importCentro(String medium){
    
    //Holds data
    ArrayList<String[]> data = null;
    
    //pull down data from dropbox and write to file that will be read by readCSV method
    try {
      DropBoxConnection.pullCSV(medium, DataAppTest.startDate, DataAppTest.endDate);
    } catch (DbxException e) {
      DataAppTest.logger.log(Level.SEVERE, "There was a database exception.", e);
    } catch (IOException e){
      DataAppTest.logger.log(Level.SEVERE,"There was an IO exception.", e);
    }
    
    DataAppTest.logger.log(Level.INFO, "Reading Centro " + medium + " File.");
    
    //Read csv that was generated from the Dropbox connection
    data = CSVReaders.readCsv("read" + medium + ".csv");
    
    //Remove the header
    CSVReaders.removeHeader(data);
    
    //Remove any dates in the array that are outside the specified reporting cycle
    CSVReaders.removeInvalidDates(data,  "Centro", DataAppTest.startDate);
    
    //Now that we have the raw data in ArrayList<String[]> form
    //We need to group appropriately into a hashmap
    //We will then iterate through the HashMap and aggregate each entry
    
    DataAppTest.logger.log(Level.INFO,"Grouping Data by Source, Medium, Campaign and AdContent.");
    HashMap<GroupID, ArrayList<String[]>> groupedData = importUtils.groupCentroRawData(data, DataAppTest.startDate);
    
    DataAppTest.logger.log(Level.INFO,"Aggregating " + medium + " data.");
    
    
    
  }
  
  public void aggregateCentro(HashMap<GroupID,ArrayList<String[]>> rawData,String medium, LocalDate sDate,
                              LocalDate eDate) {
    
    DataAppTest.logger.log(Level.INFO, "Aggregating rows based on Source, Network, Campaing and AdContent");
    
    //filter raw data so that only data for one medium remains
    HashMap<GroupID, ArrayList<String[]>> filteredData = filterMedium(medium, rawData);
    
    //Need to generalize DDRecord, Vid Record, Rich Record and Mobile record
    
    
  }
  
  
  
  
  public HashMap<GroupID, ArrayList<String[]>> filterMedium(String medium, HashMap<GroupID, ArrayList<String[]>> rawData) {
    
  //Iterate through HashMap and place only entries for specified medium into HashMap
    HashMap<GroupID,ArrayList<String[]>> onlyMedium = new HashMap<GroupID,ArrayList<String[]>>();
    Iterator<Map.Entry<GroupID, ArrayList<String[]>>> itr = rawData.entrySet().iterator();
    while (itr.hasNext()) {
      Map.Entry<GroupID, ArrayList<String[]>> pairs = itr.next();
      GroupID currID = pairs.getKey();
      ArrayList<String[]> currArray = pairs.getValue();
      //This is the sorting that should be removed
      if (currID.getMedium().equals(medium)) {
        onlyMedium.put(currID, currArray);
      }//end of if
    }//end of while
    
    return onlyMedium;
    
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
