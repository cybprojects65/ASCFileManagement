package it.cnr.raster.asc.examples;

import java.io.File;
import java.io.FileWriter;

import it.cnr.raster.asc.filemanagement.AscRaster;
import it.cnr.raster.asc.filemanagement.AscRasterReader;
import it.cnr.raster.asc.processing.generalpurpose.AscCompare;

public class Compare2ASCFiles {

	public static void main(String [] args) throws Exception{
		
		//File h1 = new File("Merluccius_merluccius.asc");
		//File h2 = new File("Merluccius_merluccius.asc");
		//File h1 = new File("reprojection_nc.asc");
		//File h2 = new File("Squilla_mantis.asc");
		File h1 = new File("D:\\WorkFolder\\Experiments\\ENM in Adriatic Sea\\MaxEntModels\\Maxent result on Ensemble 2015-17 env data\\Squilla mantis\\Squilla_mantis.asc");
		
		//File h2 = new File("D:\\WorkFolder\\Experiments\\ENM in Adriatic Sea\\MaxEntModels\\MaxEnt results on reprojections\\Squilla mantis\\Squilla_mantis_reprojection_2018.asc");
		//File differenceFile = new File("D:\\WorkFolder\\Experiments\\ENM in Adriatic Sea\\MaxEntModels\\MaxEnt results on reprojections\\Squilla mantis\\Squilla_mantis_reprojection_2018_vs_ensemble.asc");
		
		File h2 = new File("D:\\WorkFolder\\Experiments\\ENM in Adriatic Sea\\MaxEntModels\\MaxEnt results on reprojections\\Squilla mantis\\Squilla_mantis_reprojection_2019.asc");
		File differenceFile = new File("D:\\WorkFolder\\Experiments\\ENM in Adriatic Sea\\MaxEntModels\\MaxEnt results on reprojections\\Squilla mantis\\Squilla_mantis_reprojection_2019_vs_ensemble.asc");
		
		//File h2 = new File("D:\\WorkFolder\\Experiments\\ENM in Adriatic Sea\\MaxEntModels\\MaxEnt results on reprojections\\Squilla mantis\\Squilla_mantis_reprojection_2020.asc");
		//File differenceFile = new File("D:\\WorkFolder\\Experiments\\ENM in Adriatic Sea\\MaxEntModels\\MaxEnt results on reprojections\\Squilla mantis\\Squilla_mantis_reprojection_2020_vs_ensemble.asc");
		
		//File h2 = new File("D:\\WorkFolder\\Experiments\\ENM in Adriatic Sea\\MaxEntModels\\MaxEnt results on reprojections\\Squilla mantis\\Squilla_mantis_reprojection_2020_argo_cnr.asc");
		//File differenceFile = new File("D:\\WorkFolder\\Experiments\\ENM in Adriatic Sea\\MaxEntModels\\MaxEnt results on reprojections\\Squilla mantis\\Squilla_mantis_reprojection_2020_argo_cnr_vs_ensemble.asc");
		
		//File h2 = new File("Engraulis_encrasicolus.asc");
		double tolerance = 0.042;//0.042; //admit 0.1 discrepancy on probability
		boolean isRelativeTolerance = false; //if 0.1 should be interpreted as a relative percentage on two numbers
		boolean toleranceAsAThreshold = true;
		
		AscRasterReader rt = new AscRasterReader();
		AscRaster h1r = rt.readRaster(h1.getAbsolutePath());
		AscRaster h2r = rt.readRaster(h2.getAbsolutePath());
		AscCompare comparator = new AscCompare();
		
		comparator.compare(h1r, h2r, tolerance, isRelativeTolerance, toleranceAsAThreshold);
		String stats = comparator.showStats();
		File differenceReport = new File(differenceFile.getAbsolutePath().replace(".asc", "_report.txt"));
		FileWriter fw = new FileWriter(differenceReport);
		fw.write(stats);
		fw.close();
		comparator.save(differenceFile);
	}
	
	
}
