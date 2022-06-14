package it.cnr.raster.asc.processing.generalpurpose;

import java.io.File;

import it.cnr.raster.asc.filemanagement.AscRaster;
import it.cnr.raster.asc.filemanagement.AscRasterWriter;

//compares two habitat maps over the extension of habitat 1
public class AscNormalize {

	AscRaster normalizedRaster = null;

	public void normalize(AscRaster r) throws Exception {

		int rows = r.data.length;
		int cols = r.data[0].length;
		double cellSize = r.cellsize;
		double dx = r.dx;
		double dy = r.dy;
		double xll = r.xll;
		double yll = r.yll;
		
		double dataFiltered[][] = new double[rows][cols];
		double noData = Double.parseDouble(r.NDATA);
		
		double vmin = Double.MAX_VALUE;
		double vmax = 0;

		for (int i = 0; i < cols; i++) {

			for (int j = 0; j < rows; j++) {
				double d = r.data[j][i];

				if (!Double.isNaN(d) && d != noData) {
					if (d<vmin)
						vmin=d;
					if (d>vmax)
						vmax= d;
				}

			}

		}


		for (int i = 0; i < cols; i++) {

			for (int j = 0; j < rows; j++) {
				double d = r.data[j][i];

				if (!Double.isNaN(d) && d != noData) {
					dataFiltered[j][i] = (d-vmin)/(vmax-vmin);
				}
				else
					dataFiltered[j][i] = noData;
			}
		}
		
		normalizedRaster = new AscRaster(dataFiltered, cellSize, dx, dy, xll, yll);
	}

	public void save(File outputFile) throws Exception {
		AscRasterWriter writer = new AscRasterWriter();
		writer.writeRaster(outputFile.getAbsolutePath(), normalizedRaster);
	}

}
