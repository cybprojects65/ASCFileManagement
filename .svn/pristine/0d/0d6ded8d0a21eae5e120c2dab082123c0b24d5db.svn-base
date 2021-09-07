package it.cnr.raster.asc;

import java.io.File;

public class MainEnsemble {

	
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
