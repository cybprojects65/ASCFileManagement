package it.cnr.raster.asc.examples;

import java.io.File;

import it.cnr.raster.asc.filemanagement.AscRaster;
import it.cnr.raster.asc.filemanagement.AscRasterReader;
import it.cnr.raster.asc.processing.specialpurpose.AscCutOutAdriaticSea;

public class CutRasterOnTheAdriatic {

	
	public static void main(String[] args) throws Exception{
		File inputF = new File("D:\\WorkFolder\\Experiments\\ENM in Adriatic Sea v2\\Step 3 - Produce ASC\\asc\\");
		File outputF = new File("D:\\WorkFolder\\Experiments\\ENM in Adriatic Sea v2\\Step 3 - Produce ASC\\ascAdriatic\\");
		
		File [] allFiles = inputF.listFiles();
		
		for (File input:allFiles) {
			AscRaster raster = new AscRasterReader().readRaster(input.getAbsolutePath());
			AscCutOutAdriaticSea fixer = new AscCutOutAdriaticSea();
			fixer.fix(raster);
			fixer.save(new File(outputF,input.getName()));
		}
	}
	
}
