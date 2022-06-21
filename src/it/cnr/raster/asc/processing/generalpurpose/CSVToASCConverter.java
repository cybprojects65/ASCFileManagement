package it.cnr.raster.asc.processing.generalpurpose;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import it.cnr.raster.asc.filemanagement.AscRaster;
import it.cnr.raster.asc.filemanagement.AscRasterManager;
import it.cnr.raster.asc.filemanagement.AscRasterWriter;
import it.cnr.raster.asc.filemanagement.utils.Triple;

public class CSVToASCConverter {

	// files should be organised with the following folder path
	// RCP/year/speciesType/CSV
	// output will be in RCP/year/speciesType/ASC
	// NOTE: all text files should be transformed a CSV file with the following
	// structure (see the example in testfiles/RCP4.5/2005/environmental/CSV):
	// latitude,longitude,value
	// ...,..,...
	// ...,...,...
	public static void CSV2ASC(String basepath, String year, String rcp, String dataType,
			double offsetX, double offsetY, double resolution,boolean usestructuredpath) throws Exception {
		
		String foldercsv = basepath + "/" + rcp + "/" + year + "/" + dataType + "/CSV/";
		String folderasc = basepath + "/" + rcp + "/" + year + "/" + dataType + "/ASC/";
		
		
		if (!usestructuredpath) {
			foldercsv = basepath + "/";
			folderasc = basepath + "/" ;
		}
		File[] allFiles = new File(foldercsv).listFiles();

		for (File f : allFiles) {
			if (f.getName().contains(".csv")) {
				File referenceFile = new File(foldercsv + f.getName());
				File outputASCFile = new File(folderasc + f.getName().replace(".csv", ".asc"));
				System.out.println("Analysing:" + f.getName());
				List<String> allLines = Files.readAllLines(referenceFile.toPath());
				List<Triple> triples = new ArrayList<Triple>();

				double minX = Double.MAX_VALUE;
				double minY = Double.MAX_VALUE;
				double maxX = 0;
				double maxY = 0;
				double min = Double.MAX_VALUE;
				// Reading from CSV file
				for (String line : allLines) {
					if (!line.contains("latitude")) {
						String coordString[] = line.split(",");
						//this formula account for the fact that the ASC file starts from xll and yll corner and not from the center of the lowest cell
						double latitude = Double.parseDouble(coordString[0]) + offsetY - resolution / 2; 						
						double longitude = Double.parseDouble(coordString[1]) + offsetX - resolution / 2;
						double value = Double.parseDouble(coordString[2]);
						if (longitude < minX) {
							minX = longitude;
						}
						if (latitude < minY) {
							minY = latitude;
						}
						if (longitude > maxX) {
							maxX = longitude;
						}
						if (latitude > maxY) {
							maxY = latitude;
						}
						if (value < min) {
							min = value;
						}
						Triple t = new Triple(longitude, latitude, value);
						triples.add(t);
					}

				}

				int nCols = (int) ((maxX - minX) / resolution) + 1;
				int nRows = (int) ((maxY - minY) / resolution) + 1;

				double data[][] = new double[nRows][nCols];
				AscRaster asc = new AscRaster(data, resolution, resolution, resolution, minX, minY);
				asc.initData(-9999);
				asc.setNDATA("-9999");
				AscRasterManager.inject(triples, asc);
				System.out.println("Saving raster");
				AscRasterWriter rw = new AscRasterWriter();
				rw.writeRaster(outputASCFile.getAbsolutePath(), asc);
				System.out.println("Done with " + f.getName());
			}
		}
		System.out.println("All files converted.");
		
	}
	
	public static void CSV2ASC(String basepath, String year, String rcp, String dataType,
			double offsetX, double offsetY, double resolution) throws Exception {
		CSV2ASC( basepath,  year,  rcp,  dataType,
				 offsetX,  offsetY,  resolution, true);
		
	}
	

}
