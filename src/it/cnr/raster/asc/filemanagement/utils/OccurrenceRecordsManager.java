package it.cnr.raster.asc.filemanagement.utils;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class OccurrenceRecordsManager {

	public static List<Triple> getCoordinates(File occurrenceFile) throws Exception{
		
		List<String>allLines = Files.readAllLines(occurrenceFile.toPath());
		int i = 0;
		List<Triple> triples = new ArrayList<Triple>();
		
		for(String line:allLines) {
			if (i>0) {
				String coordString [] = line.split(",");
				double longitude = Double.parseDouble(coordString[0]);
				double latitude = Double.parseDouble(coordString[1]);
				Triple t = new Triple(longitude, latitude, 0);
				triples.add(t);
			}
			i++;
		}
		
		return triples;
	}
	
	
}
