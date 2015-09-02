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

import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;

import DataAppCode.DropBoxConnection;
import javafx.stage.WindowEvent;
import javafx.concurrent.Worker;
import javafx.scene.control.ProgressBar;
import DataAppCode.FilePathBuilder;
import javafx.concurrent.Task;
import javafx.application.Platform;
import DataAppCode.ImportFunctionLauncher;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;



import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * @author cgrass@google.com (Your Name Here)
 *
 */
public class ButtonEvents {

  
  public static EventHandler<ActionEvent> evntFullImport() {
    EventHandler<ActionEvent> evnt = new EventHandler<ActionEvent>() {
      @SuppressWarnings("unchecked")
      @Override
      public void handle(ActionEvent event) {
        Stage stgFullImport = new Stage();
        stgFullImport.setTitle("Full Import");

        //Create Table
        //This should be refactored into a loop
        TableView<FilePrep> table = new TableView<FilePrep>();
        table.setEditable(true);
        table.setMaxWidth(Double.MAX_VALUE);
        



        /*
         * Table Columns below here
         */

        TableColumn<FilePrep,String> col0 = new TableColumn<>("Vendor"); //Heading
        col0.setMinWidth(250);
        col0.setCellValueFactory(
            new PropertyValueFactory<FilePrep,String>("vendorName"));//This string is used as a reference to the FilePrep variable

        TableColumn<FilePrep,Image> col1 = new TableColumn<>("File Found");
        col1.setCellValueFactory(new PropertyValueFactory<>("fileFoundImage"));
        col1.setMinWidth(100);

        //Testing Images in the table
        col1.setCellFactory(new Callback<TableColumn<FilePrep,Image>,
            TableCell<FilePrep,Image>>() {
          @Override
          public TableCell<FilePrep,Image> call (TableColumn<FilePrep,Image> param) {
            TableCell<FilePrep,Image> cell = new TableCell<FilePrep,Image>() {
              @Override
              public void updateItem(Image img, boolean empty) {
                if (img != null) {
                  VBox imgVBox = new VBox();
                  imgVBox.setAlignment (Pos.CENTER);
                  ImageView imgVw = new ImageView();
                  imgVw.setImage(img);
                  imgVw.setFitHeight(50);
                  imgVw.setFitWidth(50);
                  imgVBox.getChildren().addAll(imgVw);
                  setGraphic(imgVBox);
                }
              }
            };
            return cell;

          }
        });



        TableColumn<FilePrep,Image> col2 = new TableColumn<>("File Scanned");
        col2.setMinWidth(100);
        col2.setCellValueFactory(
            new PropertyValueFactory<FilePrep,Image>("fileScannedImage"));

        //Testing Images in the table
        col2.setCellFactory(new Callback<TableColumn<FilePrep,Image>,
            TableCell<FilePrep,Image>>() {
          @Override
          public TableCell<FilePrep,Image> call (TableColumn<FilePrep,Image> param) {
            TableCell<FilePrep,Image> cell = new TableCell<FilePrep,Image>() {
              @Override
              public void updateItem(Image img, boolean empty) {
                if (img != null) {
                  VBox imgVBox = new VBox();
                  imgVBox.setAlignment (Pos.CENTER);
                  ImageView imgVw = new ImageView();
                  imgVw.setImage(img);
                  imgVw.setFitHeight(50);
                  imgVw.setFitWidth(50);
                  imgVBox.getChildren().addAll(imgVw);
                  setGraphic(imgVBox);
                }
              }
            };
            return cell;

          }
        });

        TableColumn<FilePrep,Image> col3 = new TableColumn<>("File Ready");
        col3.setMinWidth(100);
        col3.setCellValueFactory(
            new PropertyValueFactory<FilePrep,Image>("fileReadyImage"));

        //Testing Images in the table
        col3.setCellFactory(new Callback<TableColumn<FilePrep,Image>,
            TableCell<FilePrep,Image>>() {
          @Override
          public TableCell<FilePrep,Image> call (TableColumn<FilePrep,Image> param) {
            TableCell<FilePrep,Image> cell = new TableCell<FilePrep,Image>() {
              @Override
              public void updateItem(Image img, boolean empty) {
                if (img != null) {
                  VBox imgVBox = new VBox();
                  imgVBox.setAlignment (Pos.CENTER);
                  ImageView imgVw = new ImageView();
                  imgVw.setImage(img);
                  imgVw.setFitHeight(50);
                  imgVw.setFitWidth(50);
                  imgVBox.getChildren().addAll(imgVw);
                  setGraphic(imgVBox);
                }//end of if
              }//end of updateItem
            };//end of cell initialization
            return cell;

          }// end of call
        });
        
        //Populate Labels for checking vendor files
        final String[] vendorNames = new String[]{"Google Adwords",
                "Centro Digital Display","Centro Mobile Display","Centro Video Display","Centro Rich Media",
                "Facebook","Twitter","LinkedIn"};
        final ObservableList<FilePrep> addNames = FXCollections.observableArrayList(); //holds filePrep objects for table
        
        for (int i=0; i < vendorNames.length; i++) {
            FilePrep f = new FilePrep("testFile.txt",vendorNames[i],false,false,false,"1");
            addNames.add(f);
        }
        
        
        
        table.setItems(addNames);
        table.getColumns().addAll(col0,col1,col2,col3);
        
        GridPane fullImportGrid = new GridPane();
        
        fullImportGrid.setHgap(10);
        fullImportGrid.setVgap(10);
        fullImportGrid.setPadding(new Insets(25,25,25,25));
        
        Label lblFullImportNotification = new Label("Note: Files must be scanned before import");
        
        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10,0,0,10));
        vbox.getChildren().addAll(lblFullImportNotification,table,fullImportGrid);
        
      //This should launch import and progress bar
        final Button btnStartFullImport = new Button("Launch Full Import");
        if (DataAppTest.importCheckSuccess == false) {
          btnStartFullImport.setDisable(true);
        }
        EventHandler<ActionEvent> evntInitiateFullImport = evntCommenceFullImport(btnStartFullImport);
        btnStartFullImport.setOnAction(evntInitiateFullImport);
        fullImportGrid.add(btnStartFullImport,1,3);
        
        Button btnCheckFiles = new Button("Pre-Import Check");
        fullImportGrid.add(btnCheckFiles, 0, 3);
        
        stgFullImport.setOnHiding(new EventHandler<WindowEvent>() {
          @Override
          public void handle(WindowEvent we) {
            //This should probably be put into a task
            //Lock import button until import check is run
            btnStartFullImport.setDisable(true);
            DataAppTest.importCheckSuccess = false;
            DataAppTest.importActivity.reset();
          }
        });
        
        /*
         * Need write method to check files here
         */
        
        EventHandler<ActionEvent> evntInitiateFileCheck = evntCheckFiles(addNames,table,btnStartFullImport);
        btnCheckFiles.setOnAction(evntInitiateFileCheck);
        
        
        
        
        
        
        
        stgFullImport.setScene(new Scene(vbox,800,600));
        stgFullImport.show();
      }
      
    };
    return evnt;
  }//end of evntFullImport
  
  
  
  
  
  
  
  
  
  public static EventHandler<ActionEvent> evntPartialImport(){
    EventHandler<ActionEvent> evnt = new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        Stage stgPartialImport = new Stage();
        stgPartialImport.setTitle("Partial Import");
        GridPane partImportGrid = new GridPane();
        partImportGrid.setHgap(10);
        partImportGrid.setVgap(10);
        partImportGrid.setPadding(new Insets(25,25,25,25));
        stgPartialImport.setScene(new Scene(partImportGrid,450,450));
        stgPartialImport.show();

        Label lblSelectImports = new Label("Which vendors would you like to import?");
        partImportGrid.add(lblSelectImports, 0, 0);

        final String[] cbNames = new String[]{"Google Adwords","Centro Digital Display",
                                              "Centro Mobile Display","Centro Video Display","Centro Rich Media","Facebook",
                                              "Twitter","LinkedIn"};

        final ArrayList<String> importList = new ArrayList<String>();

        for (int i=0; i< cbNames.length; i++) {
          final String name = cbNames[i];
          final CheckBox cb = new CheckBox(cbNames[i]);
          cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed (ObservableValue<? extends Boolean> ov,
                                 Boolean old_val, Boolean new_val) {
              if (new_val) {
                importList.add(name);
              } else {
                importList.remove(name);
              }
            }// end of changed
          });//end of add Listener

          partImportGrid.add(cb, 0, i +1);

        }

        Button btnBeginImport = new Button("Begin Partial Import");
        partImportGrid.add(btnBeginImport,1,13);

        btnBeginImport.setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {

            Map<String, Method> methodMap = null;
            try {
              methodMap = ImportFunctionLauncher.generateMethodMap();
            } catch (Exception exception) {
              exception.printStackTrace();
            }


            String[] params = null;

            //Run methods by pulling them as values from the
            //Array
            for (int i = 0; i < importList.size(); i++) {
              //execute method in arrray
              String currVendor = importList.get(i);
              try {
                methodMap.get(currVendor).invoke(null, (Object) params);
              } catch (IllegalAccessException | IllegalArgumentException
                  | InvocationTargetException exception) {
                exception.printStackTrace();
              }
            }


          }
        });



        //      //hide this current window
        //      ((Node)(event.getSource())).getScene().getWindow().hide();
      }//end of handle
    };//end of setOnAction
    return evnt;
  }// end of method
  
  
  
  public static EventHandler<ActionEvent> evntCommenceFullImport(Button commenceImportButton){
    EventHandler<ActionEvent> evnt = new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        Stage stgFullImportStatus = new Stage();
        stgFullImportStatus.setTitle("Import Progress");
        GridPane fullImportStatusGrid = new GridPane();
//        ColumnConstraints column1 = new ColumnConstraints();
//        column1.setPercentWidth(80);
//        fullImportStatusGrid.getColumnConstraints().addAll(column1);
        fullImportStatusGrid.setHgap(10);
        fullImportStatusGrid.setVgap(10);
        fullImportStatusGrid.setPadding(new Insets(25,25,25,25));
        final ProgressBar pb = new ProgressBar(0.1);
        pb.setPrefWidth(400);
        pb.setPrefHeight(30);
        fullImportStatusGrid.add(pb, 0, 0);
        Label lblProgress = new Label();
        lblProgress.setText("Progress: ");
        lblProgress.setPrefHeight(30);
        fullImportStatusGrid.add(lblProgress,0,1);
        fullImportStatusGrid.setGridLinesVisible(false);
        stgFullImportStatus.setScene(new Scene(fullImportStatusGrid,450,200));
        stgFullImportStatus.show();
        
        
        final Task<ObservableList<String>> fullImportTask = new Task<ObservableList<String>>() {
            @Override protected ObservableList<String> call() throws InterruptedException {
                updateMessage("Importing Vendors...");
                
                try{
                
                String[] testArgs = {};
                guiCode.DataAppTest.outputDisplay.write(OutputMessages.divider());
                updateMessage("Importing Adwords");
                updateProgress(0.10,1.0);
                DataAppCode.ImportAdwords.main(testArgs);
                
                guiCode.DataAppTest.outputDisplay.write(OutputMessages.divider());
                updateMessage("Importing Centro Video");
                updateProgress(0.30,1.0);
                DataAppCode.ImportCentroVid.main(testArgs);
                
                guiCode.DataAppTest.outputDisplay.write(OutputMessages.divider());
                updateMessage("Importing Centro Mobile");
                updateProgress(0.5,1.0);
                DataAppCode.ImportMob.main(testArgs);
                
                guiCode.DataAppTest.outputDisplay.write(OutputMessages.divider());
                updateMessage("Importing Centro Digital Display");
                updateProgress(0.7,1.0);
                DataAppCode.ImportDD.main(testArgs);
                
                guiCode.DataAppTest.outputDisplay.write(OutputMessages.divider());
                updateMessage("Importing LinkedIn");
                updateProgress(0.80,1.0);
                DataAppCode.ImportLinkedIn.main(testArgs);
                
                guiCode.DataAppTest.outputDisplay.write(OutputMessages.divider());
                updateMessage("Importing Facebook");
                updateProgress(0.87,1.0);
                DataAppCode.ImportFB.main(testArgs);
                
                guiCode.DataAppTest.outputDisplay.write(OutputMessages.divider());
                updateMessage("Importing Twitter");
                updateProgress(0.95,1.0);
                DataAppCode.ImportTwitter.main(testArgs); 
                
                updateMessage("Import Complete");
                updateProgress(1.0,1.0);
                } catch (Exception e) {
                  e.printStackTrace();
                } finally {
                  //update pb and close
                }
                
                return FXCollections.observableArrayList("Vendors Imported");
            } // end of call
        }; //end of task
//        btnStartFullImport.disableProperty().bind(task.runningProperty());
        pb.progressProperty().bind(fullImportTask.progressProperty());
        lblProgress.textProperty().bind(fullImportTask.messageProperty());
        
        fullImportTask.stateProperty().addListener(new ChangeListener<Worker.State>() {
        @Override public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State oldState, Worker.State newState) {
            if (newState == Worker.State.SUCCEEDED) {
//                btnStartFullImport.setText("Update Complete.");
            }
        }
        });
        
        new Thread(fullImportTask).start();
        
        
      }//end of handle
    };//end of setOnAction
    return evnt;
  }// end of method
  
  
  
  
  /*
   * Checks the files in the table
   */
  public static EventHandler<ActionEvent> evntCheckFiles(ObservableList<FilePrep> files, TableView<FilePrep> table, Button commenceImport){
    EventHandler<ActionEvent> evnt = new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        Task<Void> task = new Task<Void>() {
          @Override
          public Void call() throws Exception {
            //Iterate through ArrayList
            for (FilePrep file: files) {
              Platform.runLater(new Runnable() {
                @Override
                public void run() {
                  
                Map<String,String> fileLocations = FilePathBuilder.buildFilePathMapDropBox(DataAppTest.startDate);  
                //update occurs in here
         
                  
                  boolean fileExists = false; //flag to determine if file exists in dbx
                  //attempt to retrieve metadata proving that the file exists
                  try {
                    DbxEntry metaData = DropBoxConnection.client.getMetadata(fileLocations.get(file.getVendorName()));
                    if (metaData != null) { //if metadata exists set flag to true
                      fileExists = true;
                    }
                  } catch (DbxException exception) {
                    exception.printStackTrace();
                  }
                  
                  //if the file exists set the file found image from an X to a checkmark
                  if(fileExists ||
                      file.getVendorName().equals("Google Adwords")) {
                    file.setFileFoundImage();
                    file.setFileFound(true);
                    //workaround for updating image
                    table.getColumns().get(0).setVisible(false);
                    table.getColumns().get(0).setVisible(true);
                  } else {
                    System.out.println(file.getVendorName() + " file was not found.");
                    System.out.println("Expecting " + fileLocations.get(file.getVendorName()) + "\n");
                  }
                  
                  //There should be a method here checking the files but for now we
                  //we will simply update the icon
                  file.setFileScannedImage();
                  file.setFileScanned(true);
                  table.getColumns().get(0).setVisible(false);
                  table.getColumns().get(0).setVisible(true);
                  
                  if (file.getFileFound() && file.getFileScanned()) {
                    file.setFileReadyImage();
                    file.setFileReady(true);
                    table.getColumns().get(0).setVisible(false);
                    table.getColumns().get(0).setVisible(true);
                  }
                  
                  
                }//end of run
              });//end of run later
              Thread.sleep(100);
            }//end of while
            
            boolean noIssues = true;
            for (FilePrep file: files) {
              if (file.getFileFound() == false) {
                noIssues = false;
              }//end of if
            }//end of loop
            
            if (noIssues) {
              DataAppTest.importCheckSuccess = true;
              commenceImport.setDisable(false);
            } else {
              DataAppTest.importCheckSuccess = false;
              commenceImport.setDisable(true);
            }
            return null;
          }//end of call 
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();    
        
        
       
      }//end of handle
    };//end of setOnAction
    return evnt;
  }// end of method
  
  
  
  
  
  public static EventHandler<ActionEvent> evntCommencePartialImport(ArrayList<String> importList){
    EventHandler<ActionEvent> evnt = new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        //need to open a Python interpreter and for each
        //value provied in the multi-select array run the 
        //corresponding script
      
      Map<String, Method> methodMap = null;
      try {
        methodMap = ImportFunctionLauncher.generateMethodMap();
      } catch (Exception exception) {
        exception.printStackTrace();
      }


      String[] params = null;

      //Run methods by pulling them as values from the
      //Array
      for (int i = 0; i < importList.size(); i++) {
        //execute method in arrray
        String currVendor = importList.get(i);
        try {
          methodMap.get(currVendor).invoke(null, (Object) params);
        } catch (IllegalAccessException | IllegalArgumentException
            | InvocationTargetException exception) {
          exception.printStackTrace();
        }
      }
        
        




////hide this current window
//((Node)(event.getSource())).getScene().getWindow().hide();
}//end of handle
};//end of setOnAction
    return evnt;
  }// end of method
  
  
  public static void main(String[] args) {

  }

}
