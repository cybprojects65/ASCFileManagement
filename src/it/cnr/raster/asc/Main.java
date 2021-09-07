package it.cnr.raster.asc;

import java.io.File;
import java.util.List;

public class Main {

	public static void main(String args[]) throws Exception {

		File referenceFile = new File("avg_chl_2018.asc");
		File occurrenceRecords = new File("Squilla mantis_all_occ.csv.filtered.csv");
		File rasterFileToModify = new File("avg_chl_2020.asc");
		File outputRasterFile = new File("avg_chl_2020_injected.asc");

		System.out.println("Reading occurrence records");
		List<Triple> coordinates = OccurrenceRecordsManager.getCoordinates(occurrenceRecords);
		System.out.println("Reading reference raster");
		AscRasterReader ref = new AscRasterReader();
		AscRaster refraster = ref.readRaster(referenceFile.getAbsolutePath());
		
		System.out.println("Associating reference values to occurrence records");
		AscRasterManager.associate(coordinates, refraster);
		
		System.out.println("Reading raster to modify");
		AscRasterReader rt = new AscRasterReader();
		AscRaster raster = rt.readRaster(rasterFileToModify.getAbsolutePath());
		System.out.println("Modifying raster");
		AscRasterManager.inject(coordinates,raster);
		System.out.println("Saving raster");
		AscRasterWriter rw = new AscRasterWriter();
		rw.writeRaster(outputRasterFile.getAbsolutePath(), raster);
		System.out.println("Done.");
	}

	public static void main1(String args[]) throws Exception {

		File sourceFile = new File("avg_chl_2018.asc");
		File occurrenceRecords = new File("Squilla mantis_all_occ.csv.filtered.csv");

		File rasterFileToModify = new File("avg_chl_2020.asc");
		File outputRasterFile = new File("avg_chl_2020_injected.asc");

		double longitude = 13.1;
		double latitude = 40;
		double valueToInject = 1000;

		System.out.println("Reading raster to modify");
		AscRasterReader rt = new AscRasterReader();
		AscRaster raster = rt.readRaster(rasterFileToModify.getAbsolutePath());
		System.out.println("Modifying raster");
		AscRasterManager.inject(longitude, latitude, valueToInject, raster);
		System.out.println("Saving raster");
		AscRasterWriter rw = new AscRasterWriter();
		rw.writeRaster(outputRasterFile.getAbsolutePath(), raster);
		System.out.println("Done.");
	}

}
