package it.cnr.raster.asc;

import java.util.List;

public class AscRasterManager {

	public static double indexToCoordinate(int index,double delta,double coordinateinit, int offset) {
		
		if (offset>0)
			index = offset-index;
		
		double coordinate = index * Math.abs(delta) + coordinateinit;
		
		return coordinate;
	}

	public static int coordinateToIndex(double coordinate,double delta,double coordinateinit, int offset) {
		
		
		int index = (int) ((coordinate-coordinateinit)/Math.abs(delta));//(int) Math.round((coordinate-coordinateinit)/Math.abs(delta));
		if (offset>0)
			index = offset-index;
		
		return index;
	}
	
	public static void inject(double longitude,double latitude, double value, AscRaster raster) {
		int longIndx = coordinateToIndex(longitude, raster.cellsize, raster.xll, 0);
		int latIndx = coordinateToIndex(latitude, raster.cellsize, raster.yll, raster.data.length-1);
//		System.out.println("Lo "+longIndx);
//		System.out.println("La "+latIndx);
		if (longIndx<0)
			System.out.println("Error: longitude of the injection is lower than minimum:"+longitude+"<"+raster.xll);
		if (latIndx<0)
			System.out.println("Error: latitude of the injection is lower than minimum:"+latitude+"<"+raster.yll);
		try {
		raster.data[latIndx][longIndx] = value;
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Error: coordinates are over the bounding box");
		}
	}
	
	public static void inject(List<Triple> triples, AscRaster raster) {
		for (Triple t:triples) {
			inject(t.x,t.y,t.v, raster);
		}
	}
	
	public static double getValue(double longitude,double latitude, AscRaster raster) {
		int longIndx = coordinateToIndex(longitude, raster.cellsize, raster.xll, 0);
		int latIndx = coordinateToIndex(latitude, raster.cellsize, raster.yll, raster.data.length-1);
		
		if (longIndx<0)
			System.out.println("Error: longitude of the injection is lower than minimum:"+longitude+"<"+raster.xll);
		if (latIndx<0)
			System.out.println("Error: latitude of the injection is lower than minimum:"+latitude+"<"+raster.yll);
		
		try {
			double v = raster.data[latIndx][longIndx];
			return v;
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Error: coordinates are over the bounding box - returning "+raster.NDATA);
		}
		
		return Double.parseDouble(raster.NDATA);
		
	}
	
	
	public static void associate(List<Triple> triples, AscRaster raster) {
		for (Triple t:triples) {
			double v = getValue(t.x,t.y,raster);
			t.v = v;
		}
	}
	
}
