package it.cnr.raster.asc.enrichment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import it.cnr.raster.asc.filemanagement.AscRaster;
import it.cnr.raster.asc.filemanagement.AscRasterManager;
import it.cnr.raster.asc.filemanagement.AscRasterReader;

public class CSVEnrichment {


	public static void enrich(File inputCSVFile, File rasterFile, String longi, String lati) throws Exception {

		Double valueraster;
		int longcolomn = -1, latcolomn = -1, i;

		String variable = rasterFile.getName().toLowerCase().replace(".asc", "");
		System.out.println("Variable: " + variable);

		String outputCSV = inputCSVFile.getName().toLowerCase().replace(".csv", "_" + variable + ".csv");

		// mi creo gli oggetti per gestire file, buffer e raster
		FileWriter pathToCsvOut = new FileWriter(new File(inputCSVFile.getParentFile(),outputCSV));
		AscRaster raster;

		BufferedReader csvReader = new BufferedReader(new FileReader(inputCSVFile));
		BufferedWriter buffer = new BufferedWriter(pathToCsvOut);

		// legge la prima riga con i nomi delle colonne
		String row = csvReader.readLine();
		String[] datacol = row.split(",");
		// si cerca la colonna più a sinistra che contiene la sottostringa
		// "long" e "lat", dovrebbero essere quelle con le
		// informazioni con latitudine e longitudine
		for (i = datacol.length - 1; i >= 0; i--) {
			if (datacol[i].compareTo(longi) == 0)
				longcolomn = i;
			if (datacol[i].compareTo(lati) == 0)
				latcolomn = i;
		}
		if (longcolomn == -1 || latcolomn == -1) {
			// se non ha trovato una delle due colonne il programma termina
			// senza far altro che chiudere i buffer
			System.out.println("I could not find the information on latitude or longitude");

		} else {
			System.out.println("Longitude index "+longcolomn);
			System.out.println("Latitude index "+latcolomn);
			buffer.write(row + "," + variable + "\n");
			raster = new AscRasterReader().readRaster(rasterFile.getAbsolutePath());

			while ((row = csvReader.readLine()) != null) {
				
				String[] data = row.split(",");
				// invoco la getValue estraendomi dalla riga i valori di
				// longitudine e latitudine
				valueraster = AscRasterManager.getValue(Double.parseDouble(data[longcolomn]), Double.parseDouble(data[latcolomn]), raster);
				// aggiungo alla riga il valore calcolato come ultimo campo
				// e la scrivo nel file di output
				if (valueraster == -9999 || String.valueOf(valueraster).compareTo("NaN") == 0)
					valueraster = -9999d;
				
				row = row + "," + valueraster;
				buffer.write(row + "\n");
			}
			
		}
		// chiudo tutto
		csvReader.close();
		buffer.close();
		System.out.println("All done.");
	}

	public static void main(String[] args) throws Exception {
		//experiment for 2020
		//File CSV= new File("D:\\WorkFolder\\Experiments\\EcologicalModelling Solemon\\Prediction Model\\ctc_index_reconstructed_DIVA.csv");
		//File RasterFile= new File ("D:\\WorkFolder\\Experiments\\EcologicalModelling Solemon\\Prediction Model\\Sepia_officinalis.asc"); 
		//experiment for 2019		
		
		
		File CSV= new File("C:\\Users\\Utente\\Ricerca\\Experiments\\EcologicalModelling Solemon\\Validazione\\PredictionModel\\ctc_index_reconstructed_DIVA.csv");
		File RasterFile= new File ("C:\\Users\\Utente\\Ricerca\\Experiments\\EcologicalModelling Solemon\\Validazione\\MaxEnt\\output\\MaxEnt 2019_7-9_10-12 Sepia Validazione\\Sepia_officinalis.asc"); 
		
		enrich(CSV, RasterFile, "lon", "lat");
	}

}
