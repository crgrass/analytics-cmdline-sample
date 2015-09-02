package guiCode;


import DataAppCode.DropBoxConnection;

import javafx.event.ActionEvent;

import javafx.scene.layout.AnchorPane;

import javafx.scene.control.DateCell;
import javafx.util.Callback;
import javafx.event.EventHandler;
import javafx.scene.control.DatePicker;

import java.io.ByteArrayOutputStream;

//import java.io.PrintStream;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javafx.scene.Scene;
import javafx.scene.text.Font;

import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.application.Application;
import javafx.stage.Stage;
//import DataAppCode.StdOutErrLevel;
//import DataAppCode.LoggingOutputStream;

public class DataAppTest extends Application {
  public static LocalDate startDate = null;
  public static LocalDate endDate = null;
  public static boolean importCheckSuccess = false;
  public static DataAppTextDisplay outputDisplay;
  public static ByteArrayOutputStream importActivity = new ByteArrayOutputStream();
//  private final PrintStream ps = new PrintStream(importActivity);
  public static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	@Override
	public void start(Stage primaryStage) {
	  
	  VBox primaryVBox = new VBox();
	  primaryVBox.setPadding(new Insets(5,5,5,5));
	  
	  //Based off of current date find most recent valid reporting cycle
      Calendar[] reportingCycle = DateMethods.findLastReportingCycle();
      
      Date sd = null;
      Date ed = null;
      //Convert to LocalDate
      sd = reportingCycle[0].getTime();
      ed = reportingCycle[1].getTime();
  
      Instant sdInstant = Instant.ofEpochMilli(sd.getTime());
      Instant edInstant = Instant.ofEpochMilli(ed.getTime());
      startDate = LocalDateTime.ofInstant(sdInstant, ZoneId.systemDefault()).toLocalDate();
      endDate = LocalDateTime.ofInstant(edInstant, ZoneId.systemDefault()).toLocalDate();
	  
      //Generate component so be added to master scene
      VBox welcome = addWelcome();
      HBox dateSelection = addDateSelection();
	  AnchorPane fullImport = addFullImport();
	  AnchorPane partialImport = addPartialImport();
	  outputDisplay = new DataAppTextDisplay();
	  
	  //VBox that holds all components for primary scene
	  primaryVBox.getChildren().addAll(welcome,dateSelection,fullImport,partialImport,outputDisplay.classSP);
	  
	  
	  Scene scene = new Scene(primaryVBox,800,800);
	  scene.getStylesheets().add("guiCode/DataAppStyle.css"); //css sheet for styling
	  
	  primaryStage.setTitle("Marketing Data App");
	  primaryStage.setScene(scene);
	  primaryStage.show();
	}

	
	
	public static void main(String[] args) throws Exception {
  	  /*
  	   * Initialize Logging
  	   */
	  
      //logger created upon class initialization
      logger.setLevel(Level.ALL); //Show all log levels
	  
	  Date currDate = new Date();
	  //Need to format date to remove spaces for filename
	  DateFormat df = new SimpleDateFormat("MM.dd.yyyy_HH.mm.ss");
	  
	  String logDate = df.format(currDate);
	  Handler fh= new FileHandler("%t/dataAppLog_" + logDate +".log");
	  SimpleFormatter logFileFormatter = new SimpleFormatter();
	  fh.setFormatter(logFileFormatter);
	  
	  logger.addHandler(fh);
	  
	  logger.log(Level.INFO, "Application Started." + System.lineSeparator());
	  
	  
	  /*
	   * Potentially useful code for logging to textbox in GUI
	   */

	  
      //	  //preserve older stdout/stderr streams
      //	  PrintStream stdout = System.out;
      //	  PrintStream stderr = System.err;
      //	  
      //	  //rebind stdout/stderr to logger
      //	  Logger logger;
      //	  LoggingOutputStream los;
      //	  
      //	  logger = Logger.getLogger("stdout");
      //	  los = new LoggingOutputStream(logger, StdOutErrLevel.STDOUT);
      //	  System.setOut(new PrintStream(los,true));
      //	  
      //	  logger = Logger.getLogger("stderr");
      //	  los = new LoggingOutputStream(logger, StdOutErrLevel.STDERR);
      //	  System.setErr(new PrintStream(los,true));
      	  
      	  
      	  
      //    System.setOut(ps); //These redirect standard output to app console
      //    System.setErr(ps);
      
	  //Open connection to dropbox API
	  logger.log(Level.INFO,"Initializing Dropbox Connection" +
	  System.lineSeparator());
	  DropBoxConnection.initializeDropboxConnection();
	  
	  //master method for JavaFX
	  launch(args);
	}
	
	
	
	
	/*
	 * Generate a VBox that is ready to be added to the master VBox.
	 * This VBox contains only a simple welcome message
	 */
	public VBox addWelcome() {
      VBox vbox = new VBox();
      vbox.setPadding(new Insets(10));
      vbox.setSpacing(8);

      Text title = new Text("Welcome to the Marketing Data App.");
      title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
      vbox.getChildren().add(title);
      return vbox;
  }
	
	/*
	 * Generate a fully populated date selection HBox.
	 * Once this is generated it simply needs to be added to a parent node.
	 * The HBox contains an instructional message as well as a date selector.
	 */
	public HBox addDateSelection(){
      HBox hboxDateSelection = new HBox();
      hboxDateSelection.setPadding(new Insets(15,12,15,12));
      hboxDateSelection.setSpacing(10);
      
      Text txtSelectStartDate = new Text("Please Select Reporting Cycle Start Date.");
      DatePicker dpStartDate = dpDate("start");

      hboxDateSelection.getChildren().addAll(txtSelectStartDate,dpStartDate);
      
      return hboxDateSelection;
    }
	
	
	
	   public AnchorPane addFullImport(){
	     AnchorPane apFullImport = new AnchorPane();
	      HBox hboxLeft = new HBox();
	      HBox hboxRight = new HBox();
	      hboxLeft.setPadding(new Insets(15,12,15,12));
	      hboxRight.setPadding(new Insets(15,12,15,12));
	      hboxLeft.setSpacing(10);
	      hboxRight.setSpacing(10);
	      
	      Text txtImportAllVendors = new Text("Import All Vendors.");
	      Button btnImportAllVendors = new Button("Begin");
	      EventHandler<ActionEvent> evntFullImport = ButtonEvents.evntFullImport();
	      btnImportAllVendors.setOnAction(evntFullImport);
	      
	      
	      hboxLeft.getChildren().add(txtImportAllVendors);
	      hboxRight.getChildren().add(btnImportAllVendors);
	      
	      apFullImport.getChildren().addAll(hboxLeft,hboxRight);
	      AnchorPane.setLeftAnchor(hboxLeft, 0.0);
	      AnchorPane.setRightAnchor(hboxRight, 0.0);
	      
	      return apFullImport;
	    }
	   
	   public AnchorPane addPartialImport(){
         AnchorPane apFullImport = new AnchorPane();
          HBox hboxLeft = new HBox();
          HBox hboxRight = new HBox();
          hboxLeft.setPadding(new Insets(15,12,15,12));
          hboxRight.setPadding(new Insets(15,12,15,12));
          hboxLeft.setSpacing(10);
          hboxRight.setSpacing(10);
          
          Text txtImportPartialVendors = new Text("Select which vendors to import.");
          Button btnImportPartialVendors = new Button("Begin");
          EventHandler<ActionEvent> evntPartialImport = ButtonEvents.evntPartialImport();
          btnImportPartialVendors.setOnAction(evntPartialImport);
          
          
          hboxLeft.getChildren().add(txtImportPartialVendors);
          hboxRight.getChildren().add(btnImportPartialVendors);
          
          apFullImport.getChildren().addAll(hboxLeft,hboxRight);
          AnchorPane.setLeftAnchor(hboxLeft, 0.0);
          AnchorPane.setRightAnchor(hboxRight, 0.0);
          
          return apFullImport;
        }
	   
	
	
	   
	   public DatePicker dpDate(String whichDate) {
	    
	    LocalDate initialDate = null;
	    //Convert to LocalDate
	    if (whichDate.equals("start")) {
	      initialDate = startDate;
	    } else {
	      initialDate = endDate;
	    }
	  
	//Date Picker Cell Factorys
	    //Only allows selection of Tuesday
	    final Callback<DatePicker, DateCell> startDateDayCellFactory = new Callback<DatePicker, DateCell>() {
	      @Override
        public DateCell call(final DatePicker datePicker) {
	          return new DateCell() {
	              @Override public void updateItem(LocalDate item, boolean empty) {
	                  super.updateItem(item, empty);
	                  if (!item.getDayOfWeek().equals(DayOfWeek.TUESDAY) ||
	                      item.isAfter(LocalDate.now().minusDays(6))) {
	                    setDisable(true);
	                  }
	              }
	          };
	      }
	  };
	  final DatePicker startDatePicker = new DatePicker(initialDate);
	    startDatePicker.setDayCellFactory(startDateDayCellFactory);
	     startDatePicker.setOnAction(new EventHandler<ActionEvent>() {
	         @Override
	      public void handle(ActionEvent t) {
	             startDate = startDatePicker.getValue();
	             endDate = startDate.plusDays(6);
	         }
	     });
	     
	     return startDatePicker;
	}
	   
	   
	  
	
	
}
