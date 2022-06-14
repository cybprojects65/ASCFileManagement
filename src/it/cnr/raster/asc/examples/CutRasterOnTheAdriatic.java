package it.cnr.raster.asc;

import java.io.File;

public class MainFixAdriatic {

	
	public static void main(String[] args) throws Exception{
		File inputF = new File("D:\\WorkFolder\\Experiments\\ENM in Adriatic Sea v2\\Step 3 - Produce ASC\\asc\\");
		File outputF = new File("D:\\WorkFolder\\Experiments\\ENM in Adriatic Sea v2\\Step 3 - Produce ASC\\ascAdriatic\\");
		
		File [] allFiles = inputF.listFiles();
		
		for (File input:allFiles) {
			AscRaster raster = new AscRasterReader().readRaster(input.getAbsolutePath());
			AscFixAdriaticSea fixer = new AscFixAdriaticSea();
			fixer.fix(raster);
			fixer.save(new File(outputF,input.getName()));
		}
	}
	
}
