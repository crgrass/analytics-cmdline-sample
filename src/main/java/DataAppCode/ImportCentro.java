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
import java.util.ArrayList;
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
    
    
    
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
