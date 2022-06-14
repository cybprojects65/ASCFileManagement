package it.cnr.raster.asc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//compares two habitat maps over the extension of habitat 1
public class AscEnsemble {

	AscRaster ensembleRaster = null;

	public void ensemble(AscRaster[] rList) throws Exception {

		int n = rList.length;
		int rows = rList[0].data.length;
		int cols = rList[0].data[0].length;
		double cellSize = rList[0].cellsize;
		double dx = rList[0].dx;
		double dy = rList[0].dy;
		double xll = rList[0].xll;
		double yll = rList[0].yll;
		double dataEns [][] = new double[rows][cols];
		double noData = Double.parseDouble(rList[0].NDATA);
		
		for (int i = 0; i < rows; i++) {

			for (int j = 0; j < cols; j++) {
				List<Double> correspondingData = new ArrayList<Double>();
				
				for (int d = 0; d < n; d++) {
					
					double dat = rList[d].data[i][j];
					if ((dat == noData) || Double.isNaN(dat)) {
						
					}else {
						correspondingData.add(dat);
					}
					
				}

				if (correspondingData.size()==0) {
					dataEns[i][j] = noData;			
				}
				else {
					double ens = 0;
					for (Double c:correspondingData) {
						ens+=c;					
					}
					ens = ens/(double)correspondingData.size();
					dataEns[i][j] = ens;
				}
			}
		}

		
		ensembleRaster = new AscRaster(dataEns, cellSize, dx, dy, xll, yll);
	}

	public void save(File outputFile) throws Exception {
		AscRasterWriter writer = new AscRasterWriter();
		writer.writeRaster(outputFile.getAbsolutePath(), ensembleRaster);
	}

}
