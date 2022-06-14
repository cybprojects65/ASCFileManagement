package it.cnr.raster.asc;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class BatchConvertionCSVToASC {

	public static void main(String args[]) throws Exception {
		
		String foldercsv,folderasc,anno,rcp,tipologia;
		File timeSeriesTable;
		File[] allFiles;
		double offsetX = 0;
		double offsetY = 0;
		anno="2100";
		rcp="85";
		tipologia="Nofish";

		//int i;
		//for(i=2020;i<2021;i++) {
		foldercsv = "E:\\MassiveMaxEnt\\probability\\rcp "+rcp+"\\"+anno+"\\"+tipologia+"\\Csv\\";
		folderasc = "E:\\MassiveMaxEnt\\probability\\rcp "+rcp+"\\"+anno+"\\"+tipologia+"\\Asc\\";
		
		//foldercsv = foldercsv + i + "\\";
		
		timeSeriesTable = new File(foldercsv);
		allFiles = timeSeriesTable.listFiles();
		if(timeSeriesTable.exists())
			System.out.println("Estraggo dalla cartella "+timeSeriesTable.getName());
		else
			System.out.println("non esiste ");
		for (File f : allFiles) {
			if (f.getName().contains(".csv")) {
				File referenceFile = new File(foldercsv+f.getName());
				File outputASCFile = new File (folderasc+f.getName().replace(".csv", ".asc"));
				System.out.println(f.getName());
				//System.out.println("Reading parameter rows");
				
				List<String>allLines = Files.readAllLines(referenceFile.toPath());
				
				List<Triple> triples = new ArrayList<Triple>();
				double resolution = 0.5;
				double minX = Double.MAX_VALUE;
				double minY = Double.MAX_VALUE;
				double maxX = 0;
				double maxY = 0;

				
				double min = Double.MAX_VALUE;
				//System.out.println("Reading from CSV file");
				for(String line:allLines) {
					if (!line.contains("latitude")) {
						String coordString [] = line.split(",");
						double longitude = Double.parseDouble(coordString[1])+offsetX-resolution/2;
						double latitude = Double.parseDouble(coordString[0])+offsetY-resolution/2;
						double value = Double.parseDouble(coordString[2]);
						if (longitude<minX) {
							minX = longitude;
						}
						if (latitude<minY) {
							minY = latitude;
						}
						if (longitude>maxX) {
							maxX = longitude;
						}
						if (latitude>maxY) {
							maxY = latitude;
						}
						if (value<min) {
							min = value;
						}
						Triple t = new Triple(longitude, latitude, value);
						triples.add(t);
					}
					
				}
				//System.out.println("Building ASC file");
				//System.out.println("Reading reference raster");
				int nCols = (int)((maxX-minX)/resolution)+1;
				int nRows = (int)((maxY-minY)/resolution)+1;
				
				double data [][] = new double[nRows][nCols];
				AscRaster asc = new AscRaster(data,resolution, resolution, resolution, minX, minY);
				asc.initData(-9999);
				asc.setNDATA("-9999");
				AscRasterManager.inject(triples,asc);
				System.out.println("Saving raster");
				AscRasterWriter rw = new AscRasterWriter();
				rw.writeRaster(outputASCFile.getAbsolutePath(), asc);
				System.out.println("Done with "+ f.getName());
		}// <- file contains .asc
		} //<- for all file
	//	} //<- for per gli anni
		System.out.println("End code");
	} 

}
