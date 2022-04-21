package it.cnr.raster.asc;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class CSV2ASC {

	public static void main(String args[]) throws Exception {

		File referenceFile = new File("C:\\Users\\Utente\\Downloads\\bso_2019_0p100_apr.csv");
		File outputASCFile = new File (referenceFile.getAbsolutePath().replace(".csv", ".asc"));
		
		System.out.println("Reading parameter rows");
		
		List<String>allLines = Files.readAllLines(referenceFile.toPath());
		int i = 0;
		List<Triple> triples = new ArrayList<Triple>();
		double resolution = 0.1;
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxX = 0;
		double maxY = 0;
		double offsetX = -0.05;
		double offsetY = -0.05;
		
		double min = Double.MAX_VALUE;
		System.out.println("Reading from CSV file");
		for(String line:allLines) {
			
				String coordString [] = line.split(",");
				double longitude = Double.parseDouble(coordString[0])+offsetX;
				double latitude = Double.parseDouble(coordString[1])+offsetY;
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
			
			i++;
		}
		System.out.println("Building ASC file");
		System.out.println("Reading reference raster");
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
		System.out.println("Done.");
	}

}
