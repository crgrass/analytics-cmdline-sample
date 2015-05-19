package guiCode;

import java.io.File;

import javafx.scene.image.Image;


public class FilePrep {
	
	private String filePath;
	private final String vendorName;
	private boolean fileFound;
	Image fileFoundImage = new Image("/guiCode/xIcon.jpg");
	private boolean fileScanned;
	private Image fileScannedImage = new Image("/guiCode/xIcon.jpg");
	private boolean fileReady;
	private Image fileReadyImage = new Image("/guiCode/xIcon.jpg");
	private final String reportingCycleDate;
	
	
	public FilePrep(String filePath, String vendorName, boolean fileFound, boolean fileScanned,
			boolean fileReady, String reportingCycleDate) {
		this.filePath = new String(filePath);
		this.vendorName = new String(vendorName);
		this.fileFound = fileFound;
		this.fileScanned = fileScanned;
		this.fileReady = fileReady;
		this.reportingCycleDate = new String(reportingCycleDate);
	}
	
	
	/*
	 * 
	 * Naming Conventions:
	 * vendor_startDate(2014-12-30)_[corrected].csv
	 * If a file is corrected a text file should accompany it explaining the corrections
	 */
	
	
	/*
	 * The file found method checks to see if the file specified file exists.
	 * If the file does exist the fileFound flag is raised and the x icon is 
	 * replaced with the checkMark Icon.If the file is not found false is this info
	 * is printed to the console and false is returned.
	 * The method takes the full absolute filepath as a parameter.
	 */
	public boolean fileFound () {
		File f = new File(filePath);
		//if path change fileFound attribute and fileFoundImage Attribute exists return true
		if(f.exists() && !f.isDirectory()) {
			fileFound = true;
			fileFoundImage = new Image("/mainPackage/checkMark.jpg");
			System.out.println("The file " + filePath + " was found.");
			return true;
		} else {
			//file is not found
			System.out.println("The file " + filePath + " was not found.");
			return false;
		}
	}// end of fileFoundMethod
	
	/*
	 * FileScanned method
	 * This method checks the following conditions as of 01/21/15.
	 * As additional potential errors with .csvs are identified more may
	 * be added.
	 * 1. The file is a .csv file.
	 * 2. The file contains the appropriate amount of columns.
	 * 3. Each column contains its appropriate data type.
	 * 
	 * Pre: the filefound method has been called to ensure that the
	 * file exists.
	 * Post: The file is ready for import
	 */
	
	public boolean fileScanned() {
		File f = new File(filePath);
		boolean extPassed = false; // file passes the extension checked test
		int pathLength = filePath.length();
		String fileExtension = filePath.substring(pathLength - 4);
		String desiredFileExtension = new String(".csv");
		//put this in a separate file
		if (fileExtension.equals(desiredFileExtension)) {
			fileScanned = extPassed = true;
			fileScannedImage = new Image("/mainPackage/checkMark.jpg");
			return true;
		} else {
			System.out.println("The file provided does not have a .csv file extension");
			return false;
		}
		
	}// end of fileScanned Method
	
	/*
	 * fileReady method
	 */
	public boolean fileReady() {
		if (fileFound && fileScanned ) {
			System.out.println("The file " + filePath + " is ready for import");
			fileReady = true;
			fileReadyImage = new Image("/mainPackage/checkMark.jpg");
			return true;
		} else {
			System.out.println("The file " + filePath + " is not ready for import ");
			return false;
		}
	}

	
	/*
	 * Getter and Setter Methods Below
	 */
	
	public String getVendorName() {
		return vendorName;
	}


	public String getReportingCycleDate() {
		return reportingCycleDate;
	}


	public boolean getFileFound() {
		return fileFound;
	}
	
	public void setFileFound(boolean b) {
	  this.fileFound = b;
	}


	public Image getFileFoundImage() {
		return fileFoundImage;
	}
	
	//sets image to checkmark
	public void setFileFoundImage() {
		fileFoundImage = new Image("/guiCode/checkMark.jpg");
	}



	public boolean getFileScanned() {
		return fileScanned;
	}
	
	public void setFileScanned(boolean b) {
      this.fileScanned = b;
    }


	public Image getFileScannedImage() {
		return fileScannedImage;
	}
	
	//sets image to checkmark
	public void setFileScannedImage() {
		fileScannedImage = new Image("/guiCode/checkMark.jpg");
	}


	public boolean getFileReady() {
		return fileReady;
	}
	
	public void setFileReady(boolean b) {
      this.fileReady = b;
    }



	public Image getFileReadyImage() {
		return fileReadyImage;
	}
	
	//sets image to checkmark
	public void setFileReadyImage() {
		fileReadyImage = new Image("/guiCode/checkMark.jpg");
	}


	
	/*
	 * Testing methods
	 */
	public static void main(String[] args) {
		System.out.println("Begin main testing method.\n");
		System.out.println("Testing: fileFound Method\n");
		
		//create dummy filePrep
		FilePrep testFP = new FilePrep("testFile.csv", "Centro", false, false, false, "01/01/2015");
		
		System.out.println("ASSERT FALSE: File does not exist on fileSystem.");
		System.out.println(testFP.fileFound()+ "\n\n");
		
		//create dummy filePrep with actual file
		FilePrep testFP2 = new FilePrep("C:\\Users\\cgrass\\Desktop\\testText.txt", "Centro", false, false, false, "12/01/2014");
		
		System.out.println("ASSERT TRUE: File does exist on fileSystem");
		System.out.println(testFP2.fileFound());
		
		System.out.println("\nTesting: fileScanned Method.\n");
		System.out.println("ASSERT FALSE: File passed is a .txt file");
		System.out.println(testFP2.fileScanned()+"\n");
		
		FilePrep testFP3 = new FilePrep("C:\\Users\\cgrass\\Desktop\\testCSV.csv","Centro",false,false,false, "12/01/2014");
		System.out.println("ASSERT TRUE: File passed is a .csv file");
		System.out.println(testFP3.fileScanned());
		
		System.out.println("\nTesting: fileScanned method.");
		System.out.println("\nASSERT FALSE: FilePrep objects do not pass test");
		System.out.println(testFP.fileReady());
		System.out.println(testFP2.fileReady());
		
		System.out.println("\nASSERT TRUE: FilePrep object has passed fileFound and FileScanned Methods");
		testFP3.fileFound();
		System.out.println(testFP3.fileReady());
		
		

	}

}
