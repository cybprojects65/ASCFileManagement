package it.cnr.raster.asc.processing.specialpurpose;

import java.io.File;

import it.cnr.raster.asc.filemanagement.AscRaster;
import it.cnr.raster.asc.filemanagement.AscRasterManager;
import it.cnr.raster.asc.filemanagement.AscRasterWriter;


public class AscCutOutAdriaticSea {

	AscRaster fixedRaster = null;

	public double middlelLineItaly(double x){
		  
		  double y1 = 46;
		  double x1=9.5;
		  double y2 = 39.9;
		  double x2=15.9;
		  double m = (y1-y2)/(x1-x2);
		  double q = ((x1*y2)-(x2*y1))/(x1-x2);
		  
		  double y = m*x+q;
		  
		  return(y);
		}

	
	public void fix(AscRaster r) throws Exception {

		int rows = r.data.length;
		int cols = r.data[0].length;
		double cellSize = r.cellsize;
		double dx = r.dx;
		double dy = r.dy;
		double xll = r.xll;
		double yll = r.yll;
		double dataFiltered [][] = new double[rows][cols];
		double noData = Double.parseDouble(r.NDATA);
		double miny = 40.5;
		
		for (int i = 0; i < cols; i++) {
			double xcoord = AscRasterManager.indexToCoordinate(i, r.cellsize, r.xll, 0);
			double ylim = middlelLineItaly(xcoord);
			//System.out.println("x:"+xcoord+" ylim:"+ylim);
			for (int j = 0; j < rows; j++) {
				//System.out.println(AscRasterManager.coordinateToIndex(44.95, r.cellsize, r.yll, r.data.length-1));
				//System.exit(0);
				double ycoord = AscRasterManager.indexToCoordinate(j, r.cellsize, r.yll, r.data.length-1);
				//System.out.print(Utils.roundDecimal(ycoord,2)+" ");
				if (ycoord>ylim && ycoord > miny) {
					dataFiltered[j][i] = r.data[j][i];
					//System.out.print(Utils.roundDecimal(ycoord,2)+" ");
				}
				else
					dataFiltered[j][i] = noData;
			}
			
			//System.out.println(" ");
		}

		
		fixedRaster = new AscRaster(dataFiltered, cellSize, dx, dy, xll, yll);
	}

	public void save(File outputFile) throws Exception {
		AscRasterWriter writer = new AscRasterWriter();
		writer.writeRaster(outputFile.getAbsolutePath(), fixedRaster);
	}

}
