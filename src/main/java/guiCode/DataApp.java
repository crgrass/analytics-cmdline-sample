package guiCode;

import javafx.scene.control.DateCell;
import javafx.event.Event;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextAreaBuilder;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.scene.image.Image;
import DataAppCode.*;

public class DataApp extends Application {

	@Override
	public void start(Stage primaryStage) throws IOException {
		
	/*
	 * Set initial structure and layout
	 */
	primaryStage.setTitle("USM Marketing Data App V.0.1");
	GridPane grid = new GridPane();
	grid.setAlignment(Pos.TOP_LEFT);
	grid.setHgap(10);
	grid.setVgap(10);
	grid.setPadding(new Insets(25,25,25,25));
	Text sceneTitle = new Text("Welcome");
	grid.add(sceneTitle,0,0);
	
	//Label and Button for Full Import
	Label lblFullImport = new Label("Full Import");
	grid.add(lblFullImport,0,2);
	Button btnFullImport = new Button("Begin Full Import");
	grid.add(btnFullImport,1,2);
	grid.setGridLinesVisible(true);
	
	//Get reporting cycle
	Calendar[] reportingCycle = DateMethods.findLastReportingCycle();
	
	//Convert to LocalDate
	Date sd = reportingCycle[0].getTime();
	Instant sdInstant = Instant.ofEpochMilli(sd.getTime());
	LocalDate startDate = LocalDateTime.ofInstant(sdInstant, ZoneId.systemDefault()).toLocalDate();
	
	//Convert to LocalDate
    Date ed = reportingCycle[1].getTime();
    Instant edInstant = Instant.ofEpochMilli(ed.getTime());
    LocalDate endDate = LocalDateTime.ofInstant(edInstant, ZoneId.systemDefault()).toLocalDate();
    
    //Date Picker Cell Factorys
    //Only allows selection of Tuesday
    final Callback<DatePicker, DateCell> startDateDayCellFactory = new Callback<DatePicker, DateCell>() {
      public DateCell call(final DatePicker datePicker) {
          return new DateCell() {
              @Override public void updateItem(LocalDate item, boolean empty) {
                  super.updateItem(item, empty);
                  if (!item.getDayOfWeek().equals(DayOfWeek.TUESDAY)) {
                    setDisable(true);
                  }
              }
          };
      }
  };
	
  
  
	//Date Pickers
	Label lblSelectStartDate = new Label("Select Start Date: ");
	final DatePicker startDatePicker = new DatePicker(startDate);
	startDatePicker.setDayCellFactory(startDateDayCellFactory);
	 startDatePicker.setOnAction(new EventHandler() {
	     @Override
      public void handle(Event t) {
	         LocalDate date = startDatePicker.getValue();
	         System.err.println("Selected date: " + date);
	     }
	 });
	 
	 Label lblSelectEndDate = new Label("Select End Date: ");
	 final DatePicker endDatePicker = new DatePicker(endDate);
     endDatePicker.setOnAction(new EventHandler() {
         @Override
      public void handle(Event t) {
             LocalDate date = endDatePicker.getValue();
             System.err.println("Selected date: " + date);
         }
     });
	 
     grid.add(lblSelectStartDate, 0, 1);
     grid.add(lblSelectEndDate, 2, 1);
     grid.add(startDatePicker, 1, 1);
	 grid.add(endDatePicker, 3, 1);
	
	//Window for full import
	btnFullImport.setOnAction(new EventHandler<ActionEvent>() {
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
			
			TableColumn col0 = new TableColumn("Vendor"); 
			col0.setMinWidth(100);
			col0.setCellValueFactory(
					new PropertyValueFactory<FilePrep,String>("vendorName"));//This string is used as a reference to the FilePrep variable
			
			TableColumn<FilePrep,Image> col1 = new TableColumn<>("File Found");
			col1.setCellValueFactory(new PropertyValueFactory("fileFoundImage"));
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
							}
						}
					};
					return cell;
					
				}
			});
			
			//Populate Labels for checking vendor files
			final String[] vendorNames = new String[]{"Google Adwords",
					"Centro Digital Display","Centro Mobile Display","Centro Video Display","Centro Rich Media",
					"Facebook","Twitter","LinkedIn"};
			final ObservableList<FilePrep> addNames = FXCollections.observableArrayList(); //holds filePrep objects for table
			
			for (int i=0; i < vendorNames.length; i++) {
				FilePrep f = new FilePrep("testFile.txt",vendorNames[i],true,true,true,"1");
				addNames.add(f);
			}
			
			System.out.println(addNames.toString());
			System.out.println(addNames.get(0).getVendorName());
			
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
			
			stgFullImport.setScene(new Scene(vbox,800,600));
			
			

			/*
			 * Progress Bar below here
			 */
			final double[] progressValues = new double[100];
			//populate progressValues
			for (int i = 1; i < 100; i++) {
				progressValues[i] = (double)i/100;
			}
			
			stgFullImport.show();
			
			//This should launch import and progress bar
			final Button btnStartFullImport = new Button("Launch Full Import");
			fullImportGrid.add(btnStartFullImport,1,3);
			final Label statusLabel = new Label("Status");
			
			//TODO: Create button that starts import check
			
			Button btnCheckFiles = new Button("Pre-Import Check");
			fullImportGrid.add(btnCheckFiles, 0, 3);
			
			btnStartFullImport.setOnAction(new EventHandler<ActionEvent>() { //ActionEvent applies to button clicking
				//The handle method is what is executed when the button is pushed
				@Override public void handle(ActionEvent actionEvent) {
					Stage stgFullImportStatus = new Stage();
					stgFullImportStatus.setTitle("Import Progress");
					GridPane fullImportStatusGrid = new GridPane();
					fullImportStatusGrid.setHgap(10);
					fullImportStatusGrid.setVgap(10);
					fullImportStatusGrid.setPadding(new Insets(25,25,25,25));
					final ProgressBar pb = new ProgressBar(0.1);
					fullImportStatusGrid.add(pb, 0, 13);
					Label lblProgress = new Label();
					lblProgress.setText("Progress: ");
					fullImportStatusGrid.add(lblProgress,0,14);
					stgFullImportStatus.setScene(new Scene(fullImportStatusGrid,450,200));
					stgFullImportStatus.show();
					final Task<ObservableList<String>> task = new Task<ObservableList<String>>() {
						@Override protected ObservableList<String> call() throws InterruptedException {
							updateMessage("Importing Vendors...");
							for (int i = 0; i < 10; i++) {
				              Thread.sleep(200);
				              updateProgress(i+1, 10);
				              updateMessage("Progess: " + (i*10) + "%");
				            }
							updateMessage("Finished.");
							return FXCollections.observableArrayList("Vendors Imported");
						} // end of call
					}; //end of task
					btnStartFullImport.disableProperty().bind(task.runningProperty());
					pb.progressProperty().bind(task.progressProperty());
					lblProgress.textProperty().bind(task.messageProperty());
					
					task.stateProperty().addListener(new ChangeListener<Worker.State>() {
					@Override public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State oldState, Worker.State newState) {
						if (newState == Worker.State.SUCCEEDED) {
							System.out.println("This is ok, this thread" + Thread.currentThread() + "is the JavaFX Application thread.");
							btnStartFullImport.setText("Update Complete.");
						}
					}
					});
					
					new Thread(task).start();
					stgFullImportStatus.show();
					//Pausing thread to allow progress bar to load
					try {
					  Thread.sleep(1000);
					} catch (InterruptedException exception) {
					  // TODO Auto-generated catch block
					  exception.printStackTrace();
					}
					
					
					String[] testArgs = {};
					DataAppCode.ImportAdwords.main(testArgs);
					DataAppCode.ImportCentroVid.main(testArgs);
					DataAppCode.ImportMob.main(testArgs);
					DataAppCode.ImportDD.main(testArgs);
					DataAppCode.ImportLinkedIn.main(testArgs);
					DataAppCode.ImportFB.main(testArgs);
					DataAppCode.ImportTwitter.main(testArgs);
				} // end of handle
				
			});// end of btnStartFullImport
		}
	});// end of FullImport button event
	

	//TODO:Provide navigation ability in all scenes
	
	//TODO: Label and Button for Terminating Import
	Label lblTerminateImport = new Label("Terminate Import");
//	grid.add(lblTerminateImport,0,5);
	Button btnTerminateImport = new Button("Terminate");
//	grid.add(btnTerminateImport,1,5);
	
	btnTerminateImport.setOnAction(new EventHandler<ActionEvent>() {
		public void handle(ActionEvent event) {
			System.out.println("Import Terminated");
		}
	});//end of setOnActionMethod
	
	//Label and Button for Partial Import
	Label lblPartialImport = new Label("Partial Import");
	grid.add(lblPartialImport,0,5);
	Button btnPartialImport = new Button("Partial Import");
	grid.add(btnPartialImport,1,5);
	
	//TODO: Window containing multi-picklist that triggers when Partial Import is selected
	btnPartialImport.setOnAction(new EventHandler<ActionEvent>() {
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
				
				//TODO: Create a method that on check a specified String is added to the import Array
				final ArrayList<String> importList = new ArrayList<String>();
				
				for (int i=0; i< cbNames.length; i++) {
					final String name = cbNames[i];
					final CheckBox cb = new CheckBox(cbNames[i]);
					cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
						public void changed (ObservableValue<? extends Boolean> ov,
								Boolean old_val, Boolean new_val) {
							if (new_val) {
								importList.add(name);
							} else {
								importList.remove(name);
							}
							System.out.println(importList.toString());
						}// end of changed
					});//end of add Listener
					
					partImportGrid.add(cb, 0, i +1);
					
				}
				
				
				
				
				Button btnBeginImport = new Button("Begin Partial Import");
				partImportGrid.add(btnBeginImport,1,13);
				
				btnBeginImport.setOnAction(new EventHandler<ActionEvent>() {
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
						
						
					}
				});
				
				
				
//				//hide this current window
//				((Node)(event.getSource())).getScene().getWindow().hide();
		}//end of handle
	});//end of setOnAction
	
	//TODO: Progress Bar that indicates import process
	
	//TODO: Window Label and Button for Opening Import Log after import is complete
	
	//TODO: Status Bar
	
	//TODO: Add Text Area for console output
	TextArea ta = TextAreaBuilder.create().prefWidth(600).prefHeight(400).wrapText(true).build();
	Console console = new Console(ta);
	PrintStream ps = new PrintStream(console,true);
	System.setOut(ps);
	System.setErr(ps);
	
	grid.add(ta,0,6); // add console to grid
	
	grid.setGridLinesVisible(true);
	
	Scene scene = new Scene(grid, 800,600);
	primaryStage.setScene(scene);
	scene.getStylesheets().add(DataApp.class.getResource("DataAppStyle.css").toExternalForm());
	
	primaryStage.show();
	
	
	
		
	}

	public static void main(String[] args) {
	  launch(args);
	}
}