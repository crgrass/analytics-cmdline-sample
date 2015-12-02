package guiCode;


import DataAppCode.LoggingOutputStream;

import DataAppCode.DropBoxConnection;
import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.DateCell;
import javafx.util.Callback;
import javafx.event.EventHandler;
import javafx.scene.control.DatePicker;

import java.io.ByteArrayOutputStream;

//import java.io.PrintStream;

import java.io.PrintStream;
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
import DataAppCode.StdOutErrLevel;
import DataAppCode.LoggingOutputStream;

public class DataAppTest extends Application {
  public static LocalDate startDate = null;
  public static LocalDate endDate = null;
  public static boolean importCheckSuccess = false;
  public static DataAppTextDisplay outputDisplay;
  public static ByteArrayOutputStream importActivity = new ByteArrayOutputStream();
  public static String individualFilePath;
  private static final PrintStream ps = new PrintStream(importActivity);
  public static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	@Override
	public void start(Stage primaryStage) {
	  
	  //Initialize dropbox connection so that data files can be pulled for import
      logger.log(Level.INFO,"Initializing Dropbox Connection" +
      System.lineSeparator());
	  DropBoxConnection.initializeDropboxConnection();
	  
	  //??
	  TextDisplayHandler displayHandler = new TextDisplayHandler();
	  outputDisplay = new DataAppTextDisplay();
      displayHandler.setTextDisplay(outputDisplay);
      logger.addHandler(displayHandler);
	  
	  VBox primaryVBox = new VBox();
	  primaryVBox.setPadding(new Insets(5,5,5,5));
	  
	  //Based off of current date find most recent valid reporting cycle
      Calendar[] reportingCycle = DateMethods.findLastReportingCycle();
      
      //Convert reporting cycle dates to LocalDates
      Date sd = reportingCycle[0].getTime();
      Date ed = reportingCycle[1].getTime();
  
      //TODO: Determine if the following four lines of code are necessary
      Instant sdInstant = Instant.ofEpochMilli(sd.getTime());
      Instant edInstant = Instant.ofEpochMilli(ed.getTime());
      startDate = LocalDateTime.ofInstant(sdInstant, ZoneId.systemDefault()).toLocalDate();
      endDate = LocalDateTime.ofInstant(edInstant, ZoneId.systemDefault()).toLocalDate();
	  
      //Generate key components to be added to the master scene
      VBox welcome = addWelcome();
      HBox dateSelection = addDateSelection();
	  AnchorPane fullImport = addFullImport();
	  AnchorPane partialImport = addPartialImport();
	  AnchorPane indivFileImport = addIndividualFileImport();
	  
	  //Parent VBox that holds all components for primary scene created above
	  primaryVBox.getChildren().addAll(welcome,dateSelection,fullImport,partialImport,
	      indivFileImport,outputDisplay.classSP);
	  
	  
	  Scene scene = new Scene(primaryVBox,800,800);
	  scene.getStylesheets().add("guiCode/DataAppStyle.css"); //css sheet for styling
	  
	  primaryStage.setTitle("Marketing Data App");
	  primaryStage.setScene(scene);
	  primaryStage.show();
	}

	
	
	public static void main(String[] args) throws Exception {
	  
      //logger created upon class initialization showing all log levels
      logger.setLevel(Level.ALL);
	  
	  //Create date to use as string in log filename.
      Date currDate = new Date();
	  
      //Create date format that will be acceptable to include in filename
	  DateFormat df = new SimpleDateFormat("MM.dd.yyyy_HH.mm.ss");
	  
	  String logDate = df.format(currDate);
	  Handler fh= new FileHandler("%t/dataAppLog_" + logDate +".log");
	  SimpleFormatter logFileFormatter = new SimpleFormatter();
	  fh.setFormatter(logFileFormatter);
	  
	  logger.addHandler(fh);
	  
	  logger.log(Level.INFO, "Application Started." + System.lineSeparator());
	  
	  //master method for JavaFX which launches the start method
	  launch(args);
	}
	
	
	
	
	/*
	 * Generate a VBox that is ready to be added to the primaryVBox.
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
	 * Once this is generated it simply needs to be added to a primaryVBox.
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
	
	
	
	/*
	 * Generate a fully populated AnchorPane that includes and instructional message for
	 * importing all files and a button that launches the fullImport window.
	 */
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
	  
	  //evntFullImport launches the fullImport window
	  EventHandler<ActionEvent> evntFullImport = ButtonEvents.evntFullImport();
	  btnImportAllVendors.setOnAction(evntFullImport);

	  hboxLeft.getChildren().add(txtImportAllVendors);
	  hboxRight.getChildren().add(btnImportAllVendors);

	  apFullImport.getChildren().addAll(hboxLeft,hboxRight);
	  AnchorPane.setLeftAnchor(hboxLeft, 0.0);
	  AnchorPane.setRightAnchor(hboxRight, 0.0);

	  return apFullImport;
	}
	   
	
	/*
     * Generate a fully populated AnchorPane that includes and instructional message for
     * completing a partial import and a button that launches the partialImport window.
     */
	public AnchorPane addPartialImport(){
	  AnchorPane apFullImport = new AnchorPane();
	  HBox hboxLeft = new HBox();
	  HBox hboxRight = new HBox();
	  hboxLeft.setPadding(new Insets(15,12,15,12));
	  hboxRight.setPadding(new Insets(15,12,15,12));
	  hboxLeft.setSpacing(10);
	  hboxRight.setSpacing(10);

	  Text txtImportPartialVendors = new Text("Select which vendors to import from a list.");
	  Button btnImportPartialVendors = new Button("Begin");
	  
	  //This event launches the partialImport window 
	  EventHandler<ActionEvent> evntPartialImport = ButtonEvents.evntPartialImport();
	  btnImportPartialVendors.setOnAction(evntPartialImport);

	  hboxLeft.getChildren().add(txtImportPartialVendors);
	  hboxRight.getChildren().add(btnImportPartialVendors);

	  apFullImport.getChildren().addAll(hboxLeft,hboxRight);
	  AnchorPane.setLeftAnchor(hboxLeft, 0.0);
	  AnchorPane.setRightAnchor(hboxRight, 0.0);

	  return apFullImport;
	}
	   
	/*
     * Generate a fully populated AnchorPane that includes and instructional message for
     * importing an individual vendor file that does not require the filename contain the
     * [vendor]_YYYY-MM-DD.csv convention. This method has been created to accomodate the
     * import of updated files that are provided by vendors when errors are identified. 
     * This is designed to reduce confusion over which file is accurate after the import
     * process which can be difficult when filenames are being changed back and forth to
     * meet to preconditions required for the data app to import data through the normal
     * import process.
     */
	public AnchorPane addIndividualFileImport() {
	  AnchorPane apIndivFile = new AnchorPane();
	  HBox hboxLeft = new HBox();
	  HBox hboxRight = new HBox();
	  hboxLeft.setPadding(new Insets(15,12,15,12));
	  hboxRight.setPadding(new Insets(15,12,15,12));
	  hboxLeft.setSpacing(10);
	  hboxRight.setSpacing(10);

	  Text txtImportPartialVendors = new Text("Import an ad hoc file.");
	  Button btnImportPartialVendors = new Button("Begin");

	  //This event launches the individual file import window.
	  EventHandler<ActionEvent> evntIndivFileImport = ButtonEvents.evntIndivFileImport();
	  btnImportPartialVendors.setOnAction(evntIndivFileImport);

	  hboxLeft.getChildren().add(txtImportPartialVendors);
	  hboxRight.getChildren().add(btnImportPartialVendors);

	  apIndivFile.getChildren().addAll(hboxLeft,hboxRight);
	  AnchorPane.setLeftAnchor(hboxLeft, 0.0);
	  AnchorPane.setRightAnchor(hboxRight, 0.0);

	  return apIndivFile;
	}
	   
	/*
	 * This method generates a datePicker which allows for easy selection of available
	 * reporting cycle start dates. The returned date picker is added into the dateSelection
	 * hBox.  
	 */
	public DatePicker dpDate(String whichDate) {

	  LocalDate initialDate = null;
	  
	  //TODO: The parameter being passed here is a string that is being used as a switch
	  //to determine whether the start date or end date should be used. Is there actually an
	  //instance where the endDate is used. It's possible that this was created with the intent
	  //that the selection of startDate would auto-populate the end date for display, however,
	  //this was not deemed necessary for the final design. Consider removing.
	  if (whichDate.equals("start")) {
	    initialDate = startDate;
	  } else {
	    initialDate = endDate;
	  }

	  //Date Picker Cell Factory only allows selection of Tuesday's
	  //Creates the cells within the datepicker and restricts selection.
	  final Callback<DatePicker, DateCell> startDateDayCellFactory = new Callback<DatePicker, DateCell>() {
	    @Override
	    public DateCell call(final DatePicker datePicker) {
	      return new DateCell() {
	        @Override public void updateItem(LocalDate item, boolean empty) {
	          super.updateItem(item, empty);
	          //calculation that determines which cells are selectable.
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
	      //On selection of startDate is is assigned to the classes static method and
	      //endDate is calculated and assigned as well.
	      startDate = startDatePicker.getValue();
	      endDate = startDate.plusDays(6);
	    }
	  });

	  return startDatePicker;
	} 
	
}//end of DataAppTest
