package it.cnr.raster.asc.processing.generalpurpose;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import it.cnr.raster.asc.filemanagement.AscRaster;
import it.cnr.raster.asc.filemanagement.AscRasterManager;
import it.cnr.raster.asc.filemanagement.AscRasterReader;
import it.cnr.raster.asc.filemanagement.AscRasterWriter;
import it.cnr.raster.asc.filemanagement.utils.Gaussian;
import it.cnr.raster.asc.filemanagement.utils.Triple;
import it.cnr.raster.asc.filemanagement.utils.Utils;

public class NativeSpeciesDistribution {

	public static void main(String[] args) throws Exception {
		/*
		File presenceLocations = new File("testfiles/presencelocations/Presence_Merluccius_merluccius.csv");
		File ascDistribution = new File("testfiles/speciesdistributions/Merluccius_merluccius.asc");
		File outputAscDistribution = new File("testfiles/speciesdistributions/Merluccius_merluccius_native.asc");
		*/
		/*
		File presenceLocations = new File("testfiles/presencelocations/Presence_Carcharodon_carcharias.csv");
		File ascDistribution = new File("testfiles/speciesdistributions/Carcharodon_carcharias.asc");
		File outputAscDistribution = new File("testfiles/speciesdistributions/Carcharodon_carcharias_native.asc");
		*/
		/*
		File presenceLocations = new File("testfiles/presencelocations/Presence_Cetorhinus_maximus.csv");
		File ascDistribution = new File("testfiles/speciesdistributions/Cetorhinus_maximus.asc");
		File outputAscDistribution = new File("testfiles/speciesdistributions/Cetorhinus_maximus_native.asc");
		*/
		/*
		File presenceLocations = new File("testfiles/presencelocations/Presence_Clupea_harengus.csv");
		File ascDistribution = new File("testfiles/speciesdistributions/Clupea_harengus.asc");
		File outputAscDistribution = new File("testfiles/speciesdistributions/Clupea_harengus_native.asc");
		*/
		File presenceLocations = new File("testfiles/presencelocations/Presence_Huso_huso.csv");
		File ascDistribution = new File("testfiles/speciesdistributions/Huso_huso.asc");
		File outputAscDistribution = new File("testfiles/speciesdistributions/Huso_huso_native.asc");
		
		NativeSpeciesDistribution nspd = new NativeSpeciesDistribution();
		nspd.estimateNativeDistribution(presenceLocations, ascDistribution, outputAscDistribution);

		
	}

	public void estimateNativeDistribution(File presenceLocations, File ascDistribution, File outputAscDistribution)
			throws Exception {

		// read presence locations: spp, long, lat
		List<Triple> presenceList = new ArrayList<>();
		List<String> pointLines = Files.readAllLines(presenceLocations.toPath());
		for (String pointLine : pointLines) {
			if (pointLine.contains("latitude"))
				continue;
			else {
				String split[] = pointLine.split(",");
				double longitude = Double.parseDouble(split[1]);
				double latitude = Double.parseDouble(split[2]);
				Triple t = new Triple(longitude, latitude, 0);
				presenceList.add(t);
			}
		}
		Triple presencePoints[] = new Triple[presenceList.size()];
		presencePoints = presenceList.toArray(presencePoints);

		// calculate maximum distance between the observations = standard deviation
		double maxDistance = 0;
		Double meanDistances [] = new Double[presencePoints.length];
		int j = 0;
		for (Triple t1 : presencePoints) {
			double distances [] = new double[presencePoints.length];
			int i = 0;
			for (Triple t2 : presencePoints) {
				double distance = Math.sqrt(((t1.x - t2.x) * (t1.x - t2.x)) + ((t1.y - t2.y) * (t1.y - t2.y)));
				distances[i] = distance;
				if (distance > maxDistance) {
					maxDistance = distance;
				}
				i++;
			}
			meanDistances[j] = Utils.mean(distances);
			j++;
		}
		
		//double sigma = maxDistance/4;
		//we use the standard deviation of the mean distances to estimate a maximum orientative mean distance
		double sigma = Utils.standardDeviation(meanDistances);
		System.out.println("Max distance: "+maxDistance);
		System.out.println("Sigma distance: "+sigma);
		
		AscRasterReader spd = new AscRasterReader();
		AscRaster ascSpd = spd.readRaster(ascDistribution.getAbsolutePath());
		double data[][] = ascSpd.data;
		double newdata[][] = new double [ascSpd.data.length][ascSpd.data[0].length];
		double nodata = Double.parseDouble(ascSpd.getNDATA());
		int latitudeIdxs = data.length;
		int longitudeIdxs = data[0].length;
		// weight the ascDistribution by the Gaussian function
		// search the closest presence location
		// calculate the Gaussian value in the analised location
		// multiply the SPD by the Gaussian value
		for (int la = 0; la < latitudeIdxs; la++) {
			double y = AscRasterManager.indexToCoordinate(la, ascSpd.cellsize, ascSpd.yll, ascSpd.data.length);
			y = y-(ascSpd.cellsize/2d);
			for (int lo = 0; lo < longitudeIdxs; lo++) {
				double oldValue = data[la][lo];
				double newValue = oldValue;
				double x = AscRasterManager.indexToCoordinate(lo, ascSpd.cellsize, ascSpd.xll, 0);
				x = x-(ascSpd.cellsize/2d);
				System.out.println(la+","+lo+"->"+x+","+y+ ": "+"Original V: " + oldValue);
				//System.exit(0);
				if (oldValue != nodata && !Double.isNaN(oldValue)) {
									
					double minDist = Double.MAX_VALUE;
					Triple optimalt = null;
					for (Triple t : presencePoints) {
						double distance = Math.sqrt(((t.x - x) * (t.x - x)) + ((t.y - y) * (t.y - y)));
						if (distance < minDist) {
							minDist = distance;
							optimalt = t;
						}
					}
					 
					if (minDist<sigma)
						newValue = oldValue;
					 else{
						 double gaussianValue = Gaussian.normPdf(minDist, 0, sigma);
						 newValue = oldValue * gaussianValue;
					 }
					//if (x == 122.5 && y == 39.5)
					if (x >-56  && x <-54 && y >-35 && y <-33) {
						System.out.println(la+","+lo+"->"+x+","+y+ " [" + optimalt.x+","+optimalt.y+"] (d="+minDist+")"+ 
								": "+"Original V: " + oldValue+ " New V: "+newValue);
					
					}
					//newValue = minDist;
					data[la][lo] = newValue;
				}				
				
				//int eX = AscRasterManager.coordinateToIndex(x, ascSpd.cellsize,ascSpd.xll, 0);
				//int eY = AscRasterManager.coordinateToIndex(y, ascSpd.cellsize,ascSpd.yll, ascSpd.data.length);
				//System.out.println("<-"+eY+","+eX);
				newdata[la][lo] = newValue;
				
					
			}
			//System.exit(0);
			//System.out.println("-----------------------------");
		}

		//System.exit(0);
		// save the output ASC file
		AscRaster ascSpdout = new AscRaster(ascSpd.cellsize,ascSpd.dx,ascSpd.dy,ascSpd.xll,ascSpd.yll); 
		ascSpdout.NDATA = ascSpd.NDATA;
		ascSpdout.setData(newdata);
		ascSpd.data = data;
		AscRasterWriter writer = new AscRasterWriter();
		writer.writeRaster(outputAscDistribution.getAbsolutePath(), ascSpdout);

	}
}
