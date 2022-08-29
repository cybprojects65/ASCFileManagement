package it.cnr.raster.asc.processing.specialpurpose;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class ExtractAquaMapsSpeciesCSV {

	
	public static void main(String [] args) throws Exception{
		
		//File inputFile = new File ("C:\\Users\\Utente\\Ricerca\\Experiments\\Q-Quatics Climatic and AquaMaps data\\AquaMaps data\\rcp45_hcaf_species_native_2050_gte10.csv");
		//File inputFile = new File ("C:\\Users\\Utente\\Ricerca\\Experiments\\Q-Quatics Climatic and AquaMaps data\\AquaMaps data\\hcaf_species_native_2050_gte10.csv");
		//File inputFile = new File ("C:\\Users\\Utente\\Ricerca\\Experiments\\Q-Quatics Climatic and AquaMaps data\\AquaMaps data\\rcp45_hcaf_species_native_2100_gte10.csv");
		//File inputFile = new File ("C:\\Users\\Utente\\Ricerca\\Experiments\\Q-Quatics Climatic and AquaMaps data\\AquaMaps data\\hcaf_species_native_2100_gte10.csv");
		//File inputFile = new File ("C:\\Users\\Utente\\Ricerca\\Experiments\\Q-Quatics Climatic and AquaMaps data\\AquaMaps data\\rcp26_hcaf_species_native_2100_gte10.csv");

		String rootFolder = "C:\\Users\\Utente\\Ricerca\\Experiments\\Q-Quatics Climatic and AquaMaps data\\AquaMaps data\\";
		String speciesCodes [] = {"Fis-153573","ITS-158595","ITS-99152"};
		String speciesNames [] = {"Mullus barbatus","Leptometra celtica","Squilla mantis"};
		String inputFiles []= {
				"rcp26_hcaf_species_native_2100_gte10.csv",
				"rcp45_hcaf_species_native_2100_gte10.csv",
				"rcp85_hcaf_species_native_2100_gte10.csv",
				"rcp45_hcaf_species_native_2050_gte10.csv",
				"rcp85_hcaf_species_native_2050_gte10.csv"
		};
		int i=0;
		for (String speciesCode:speciesCodes) {
			
		for (String inputFile:inputFiles) {	
		File outputFile = new File (speciesNames[i].replace(" ", "_")+"-"+inputFile+".csv");
		
		System.out.println(speciesNames[i]+"->"+inputFile);
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		BufferedReader br = new BufferedReader(new FileReader(new File(rootFolder,inputFile)));
		
		String line = br.readLine();
		String header = "latitude,longitude,probability";
		bw.write(header+"\n");
		while (line!= null) {
			
			String latitude = "";
			String longitude = "";
			String probability = "";
			String [] lineelems = line.split(",");
			String species = lineelems[0];
			if (species.equalsIgnoreCase(speciesCode)) {
				latitude = lineelems[2];
				longitude = lineelems[3];
				probability = lineelems[4];
				
				String newline = latitude+","+longitude+","+probability;
				bw.write(newline+"\n");
			}
			
			line = br.readLine();
		}
		
		br.close();
		bw.close();
		}
		i++;
	}
		
	}
}
