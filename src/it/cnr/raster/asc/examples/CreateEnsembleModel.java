package it.cnr.raster.asc.examples;

import java.io.File;

import it.cnr.raster.asc.filemanagement.AscRaster;
import it.cnr.raster.asc.filemanagement.AscRasterReader;
import it.cnr.raster.asc.processing.generalpurpose.AscEnsemble;

public class CreateEnsembleModel {

	
	public static void main(String[] args) throws Exception{
		
			AscRaster[] rasters = {
					//new AscRasterReader().readRaster("Engraulis_encrasicolus.asc"),
					//new AscRasterReader().readRaster("Engraulis_encrasicolus.asc"),
					//new AscRasterReader().readRaster("Merluccius_merluccius.asc"),
					//new AscRasterReader().readRaster("avg_chl_2018.asc"),
					new AscRasterReader().readRaster("surface_temp_2018.asc"),
					//new AscRasterReader().readRaster("Merluccius_merluccius.asc"),
					//new AscRasterReader().readRaster("Squilla_mantis.asc")
			};
		AscEnsemble ensemble = new AscEnsemble();
		ensemble.ensemble(rasters);
		ensemble.save(new File("ensemble.asc"));
	}
	
}
