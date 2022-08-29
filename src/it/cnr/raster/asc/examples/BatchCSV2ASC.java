package it.cnr.raster.asc.examples;

import it.cnr.raster.asc.processing.generalpurpose.CSVToASCConverter;

public class BatchCSV2ASC {

	
	// files should be organised with the following folder path
		// RCP/year/speciesType/CSV
		// output will be in RCP/year/speciesType/ASC
		// NOTE: all text files should be transformed a CSV file with the following
		// structure (see the example in testfiles/RCP4.5/2005/environmental/CSV):
		// latitude,longitude,value
		// ...,..,...
		// ...,...,...
	
	public static void main(String args[]) throws Exception {
		//known data offset on longitude and latitude
		double offsetX = 0;
		double offsetY = 0;
		//reference year
		String year = "2019";
		//reference emission scenario, e.g., RCP4.5, RCP8.5, SRES_A2, HISTORICAL
		String rcp = "RCP26";
		//files' spatial resolution
		double resolution = 0.5;
		//data type, e.g., environmental, geophysical, socioeconomic
		String dataType = "sdm";
		String basepath = "C:\\Users\\Utente\\Downloads\\sdm\\";
		CSVToASCConverter.CSV2ASC(basepath, year, rcp, dataType,
				offsetX, offsetY, resolution);
		
	}
	
	
}
