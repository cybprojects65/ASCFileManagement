package it.cnr.raster.asc.examples;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import it.cnr.raster.asc.filemanagement.AscRaster;
import it.cnr.raster.asc.filemanagement.AscRasterReader;
import it.cnr.raster.asc.processing.specialpurpose.ASCvsCSVConsistencyChecker;
public class ASCvsCSVConsistency {
	
	
	public static void main(String[] args) throws Exception{
		
		String csvfile = "./testfiles/RCP4.5/2005/environmental/CSV/npp_05_Adriatic.csv";
		String ascfile = "./testfiles/RCP4.5/2005/environmental/ASC/npp_05_Adriatic.asc";
		double offsetX = 0;
		double offsetY = 0;
		double resolution = 0.5;
		
		ASCvsCSVConsistencyChecker checker = new ASCvsCSVConsistencyChecker();
		checker.ConsistencyCheckCsvAsc(csvfile, ascfile, offsetX,offsetY,resolution);
		
	}
	
	}

