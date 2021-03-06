package it.cnr.raster.asc.processing.specialpurpose;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import it.cnr.raster.asc.filemanagement.AscRaster;
import it.cnr.raster.asc.filemanagement.AscRasterManager;
import it.cnr.raster.asc.filemanagement.AscRasterReader;
public class ASCvsCSVConsistencyChecker {
	AscRaster extractedRaster = null;

	public void ConsistencyCheckCsvAsc(String csvfile,String ascfile, double offsetX,double offsetY,double resolution) throws IOException {
		int seed=0;
		int min=2;
		String line = ""; 
		String splitBy = ","; 
		double latcsv,loncsv,valueCSV,valueASC;
		int miss = 0;
		int hit = 0;
		
		AscRaster raster;
		String[] linearray;
		raster = new AscRasterReader().readRaster(ascfile);
		//seed=(int)Math.floor(Math.random()*(max-min+1)+min);
		BufferedReader br = new BufferedReader(new FileReader(csvfile));
		line = br.readLine();
		AscRasterReader arr = new AscRasterReader();
		AscRaster rasterASC = arr.readRaster(ascfile);
		
		while ((line = br.readLine()) != null) {
			//if(seed==0) {
				if(line.contains("latitude"))
					continue;
				linearray = line.split(splitBy);
				double latitude = Double.parseDouble(linearray[0]) + offsetY - resolution / 2; 						
				double longitude = Double.parseDouble(linearray[1]) + offsetX - resolution / 2;
				
				valueCSV = Double.parseDouble(linearray[2]);
				valueASC=AscRasterManager.getValue(longitude,latitude,raster);
				if(valueASC==valueCSV) {
					hit++;
				}else {
					miss++;
				}
				//seed=(int)Math.floor(Math.random()*(max-min+1)+min);	
			//}else {
				//seed=seed-1;					
			//}			
		}
		br.close();
		System.out.println("hit = "+hit+" ,miss = "+miss);
	}

	public void valueConsistencyCheck(String folder) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(folder+"filename.txt"));
		BufferedReader br2 = new BufferedReader(new FileReader(folder+"bottom dissolved oxygen_DO_annuals_0p100_2017edited.csv"));
		int i,id,j,jd;
		double xx,yy;
		String[] linearray;
		String line = ""; 
		String splitBy = ","; 
		double offx=-17995;
		double offy=-8995;
		double dx=0.1,dy=0.1;
		int stepx = (int)(dx *100);
		int stepy = (int)(dy *100);
		int error=0;
		FileWriter myWriter = new FileWriter(folder+"i e j generate.txt");
		line = br2.readLine();
			for(j=1799;j>=0;j--) {
				for(i=0;i<3600;i++) {
				//myWriter.write(i+","+j+"\n");
				line = br2.readLine();
				linearray = line.split(splitBy);				

				xx = Double.parseDouble(linearray[0]);
				//id= ((int)(xx*100)- (int)offx)/stepx;
				id= (int)((Math.round(xx*100) - offx)/stepx);
				yy = Double.parseDouble(linearray[1]);
				//jd= ((int)(yy*100) - (int)offy)/stepy;
				jd= (int)((Math.round(yy*100) - offy)/stepy);
				if(i!=id||j!=jd) {error++;
				myWriter.write("(i/j)=("+i+"/"+j+"),(id/jd)=("+id+"/"+jd+"),(xx/yy)=("+xx+"/"+yy+")<---------------errore\n");}
			else{myWriter.write("(i/j)= ("+i+"/"+j+"), (id/jd)= ("+id+"/"+jd+"),(xx/yy)=("+xx+"/"+yy+")\n");}
		}
			}
		System.out.println("ho "+error+" errori\n");
		br.close();
		myWriter.close();
	}
	
	public static int coordinateToIndex(double coordinate,double delta,double coordinateinit, int offset) {
		
		double difference;
		if(delta<1) {
			double step = 1/delta;
			double coordinate2 = coordinate*step;
			coordinateinit=coordinateinit*step;
			int index = (int)Math.round(coordinate2-coordinateinit);
		if (offset>0)
			index = offset-index;			
		//System.out.println("coordinate="+coordinate+" ,index="+index);
			return index;
		}
		difference = coordinate-coordinateinit;
		double absdelta=Math.abs(delta);
		int index = (int) ((difference)/absdelta);
		//(int) Math.round((coordinate-coordinateinit)/Math.abs(delta));
		if (offset>0)
			index = offset-index;			
			
			return index;
	
	}
	
	
	
}

