package it.cnr.raster.asc.examples;

import java.io.File;

import it.cnr.raster.asc.filemanagement.AscRaster;
import it.cnr.raster.asc.filemanagement.AscRasterReader;
import it.cnr.raster.asc.processing.generalpurpose.AscNormalize;

public class NormalizeASC {

	public static void main(String[] args) throws Exception {
		File folder = new File("D:\\WorkFolder\\Experiments\\ENM in Adriatic Sea\\MaxEntModels\\all models 05\\");
		File[] allFiles = folder.listFiles();

		for (File f : allFiles) {
			if (!f.getName().contains("_norm")) {
				System.out.println("Normalizing "+f.getName());
				AscRaster rasters = new AscRasterReader().readRaster(f.getAbsolutePath());
				File output = new File(f.getAbsolutePath().replace(".asc", "_norm.asc"));
				AscNormalize norm = new AscNormalize();
				norm.normalize(rasters);
				norm.save(output);
				//break;
			}
		}

	}

}
