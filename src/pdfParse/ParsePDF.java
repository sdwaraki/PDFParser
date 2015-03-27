package pdfParse;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class ParsePDF {

	public static void main(String[] args) {
		String tempTestReportsFolderPath = "C:/Users/Sumanth/Desktop/RDH/TestReports";
		File tempTestReportsFolder = new File(tempTestReportsFolderPath);
		File[] allTestReports = tempTestReportsFolder.listFiles();
		if (allTestReports.length == 0) {
			System.out.println("No files to be renamed");
			System.exit(1);
		}
		String path;
		String labFolder = "C:/Users/Sumanth/Desktop/LabReports";
		ParsePDF x = new ParsePDF();
		PDDocument pd = new PDDocument();
		try {
			for (int i = 0; i < allTestReports.length; i++) {
				path = allTestReports[i].getAbsolutePath();
				String sampledDate = x.extractDate(path, pd);
				String fileName = x.extractName(path, pd);
				x.setFileName(path, fileName);
				x.setFolder(labFolder, sampledDate, path, fileName);
			}
			pd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Function does the file path change
	 * 
	 * @param labFolder
	 *            - Path of the LabFolder
	 * @param sampledDate
	 *            - The date of Sampling for the test
	 * @param path
	 *            - The path of the original file
	 * @param fileName
	 *            - The new filename
	 */
	private void setFolder(String labFolder, String sampledDate, String path,
			String fileName) {

		boolean renamed = false;
		String[] dateContents = sampledDate.split("/");
		String month = dateContents[0];
		String year = dateContents[2];
		String monthCode = getMonthCode(month);
		String monthYearFolderName = monthCode + "20" + year;
		String yearName = "20" + year;
		String yearPath = labFolder + "/" + yearName;
		File yearFolder = new File(yearPath);
		String monthFolderPath = null;
		File oldFile = new File(path);
		String parentPath = oldFile.getParent();
		File monthFolder, src, destn;
		if (!yearFolder.exists()) {
			yearFolder.mkdir();
			monthFolderPath = yearPath + "/" + monthYearFolderName;
			monthFolder = new File(monthFolderPath);
			monthFolder.mkdir();
			src = new File(parentPath + "/" + fileName + ".pdf");
			destn = new File(monthFolderPath + "/" + fileName + ".pdf");
			if (destn.exists()) {
				renamed = false;
			} else {
				if (src.renameTo(destn))
					renamed = true;
			}
		} else {
			monthFolderPath = yearPath + "/" + monthYearFolderName;
			monthFolder = new File(monthFolderPath);
			if (!monthFolder.exists()) {
				monthFolder.mkdir();
				src = new File(parentPath + "/" + fileName + ".pdf");
				destn = new File(monthFolderPath + "/" + fileName + ".pdf");
				if (destn.exists()) {
					renamed = false;
				} else {
					if (src.renameTo(destn))
						renamed = true;
				}
			} else {
				src = new File(parentPath + "/" + fileName + ".pdf");
				destn = new File(monthFolderPath + "/" + fileName + ".pdf");
				if (destn.exists()) {
					renamed = false;
				} else {
					if (src.renameTo(destn))
						renamed = true;
				}
			}
		}

		if (renamed)
			System.out.println("File path change " + fileName + " succcesful.");
		else
			System.out.println("File path change failed. File " + fileName
					+ " may be already present.");
	}

	/**
	 * Function returns the month name given its number
	 * 
	 * @param month
	 *            - The month number
	 * @return
	 */
	private String getMonthCode(String month) {
		int monthNumber = Integer.parseInt(month);
		String folder = null;
		switch (monthNumber) {
		case 1: {
			folder = "Jan";
			break;
		}
		case 2: {
			folder = "Feb";
			break;
		}
		case 3: {
			folder = "Mar";
			break;
		}
		case 4: {
			folder = "Apr";
			break;

		}
		case 5: {
			folder = "May";
			break;
		}
		case 6: {
			folder = "Jun";
			break;
		}
		case 7: {
			folder = "Jul";
			break;
		}
		case 8: {
			folder = "Aug";
			break;
		}
		case 9: {
			folder = "Sep";
			break;
		}
		case 10: {
			folder = "Oct";
			break;
		}
		case 11: {
			folder = "Nov";
			break;
		}
		case 12: {
			folder = "Dec";
			break;
		}
		default: {
			System.out.println("The month number is incorrect");
			break;
		}
		}
		return folder;
	}

	/**
	 * Function extracts the project ID from the PDF file
	 * 
	 * @param path
	 *            - The temporary path of the uploaded file.
	 * @param pd
	 *            - Object that helps in reading the page.
	 * @return
	 */
	public String extractName(String path, PDDocument pd) {
		String extract, fileName = null;
		String regex = "Client Project/Site: .*";
		try {
			pd = PDDocument.load(path);
			PDFTextStripper strip = new PDFTextStripper();
			strip.setStartPage(1);
			strip.setEndPage(1);
			extract = strip.getText(pd);
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(extract);
			if (m.find())
				fileName = m.group(0).substring(21);
			pd.close();

		} catch (Exception e) {
			System.out.println("Exception in reading the PDF document.");
		}

		return fileName;
	}

	/**
	 * This renames the file to the Project ID
	 * 
	 * @param path
	 *            : The path of the file that has to be renamed
	 * @param fileName
	 *            : The new name of the file
	 */
	public void setFileName(String path, String fileName) {
		File pdfFile = new File(path);
		// Get the folder path where the file is initially stored
		String parent = pdfFile.getParent();
		File newPdfFile = new File(parent + "/" + fileName + ".pdf");
		if (pdfFile.renameTo(newPdfFile))
			System.out.println("Rename Successful.");
		else
			System.out.println("Rename Failed.");
	}

	/**
	 * Function extracts the sample date from the PDF file
	 * 
	 * @param path
	 *            - Path of the file
	 * @param pd
	 *            - The object that helps in reading the file content.
	 * @return
	 */
	public String extractDate(String path, PDDocument pd) {
		String extract;
		String regex = "(\\d{2})/(\\d{2})/(\\d{2})";
		String date = null;
		try {
			pd = PDDocument.load(path);
			PDFTextStripper strip = new PDFTextStripper();
			strip.setStartPage(7);
			strip.setEndPage(7);
			extract = strip.getText(pd);
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(extract);
			if (m.find()) {
				date = m.group(0);
			} else {
				System.out
						.println("Could not find the year and the month in the 7th page of the report.");
				return null;
			}
			pd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

}
