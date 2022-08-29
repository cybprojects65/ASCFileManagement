package it.cnr.raster.asc.processing.specialpurpose;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class ExtractSpeciesOccurrencesCSV {

	
	public static void main(String [] args) throws Exception{
		
		File inputFile = new File ("C:\\Users\\Utente\\Ricerca\\Experiments\\Q-Quatics Climatic and AquaMaps data\\AquaMaps data\\GTE10_OCCURRENCECELLS\\occurrencecells_gte10.csv");
		String speciesCodes [] = {"Fis-153573","ITS-158595","ITS-99152"};
		String speciesNames [] = {"Mullus barbatus","Leptometra celtica","Squilla mantis"};
		
		int  i =0;
		for (String speciesCode:speciesCodes) {
			String speciesName = speciesNames[i];
			File outputFile = new File ("Presence_"+speciesName.replace(" ", "_")+".csv");
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			String line = br.readLine();
			String header = "species,longitude,latitude";
			
			bw.write(header+"\n");
		while (line!= null) {
			
			String latitude = "";
			String longitude = "";
			String [] lineelems = line.split(",");
			String species = lineelems[2];

			if (species.equalsIgnoreCase(speciesCode)) {
				latitude = lineelems[5];
				longitude = lineelems[6];
				
				String newline = speciesName+","+longitude+","+latitude;
				bw.write(newline+"\n");
			}
			
			line = br.readLine();
		}
		
		br.close();
		bw.close();
		i++;
		}
	}
}
