package it.cnr.raster.asc.processing.specialpurpose;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.List;

import it.cnr.raster.asc.filemanagement.AscRaster;
import it.cnr.raster.asc.filemanagement.AscRasterReader;
import it.cnr.raster.asc.processing.generalpurpose.AscCompare;

public class BuildComparisonMatrixForEcologicalNicheModels {

	public static void main(String[] args) throws Exception {

		File modelsFolder = new File("D:\\WorkFolder\\Experiments\\ENM in Adriatic Sea v2\\Step 7 - RetrieveAquaMaps");
		File quartilesFolder = new File("D:\\WorkFolder\\Experiments\\ENM in Adriatic Sea v2\\Step 8 - Trace Quartiles");
		File outputFolder = new File("D:\\WorkFolder\\Experiments\\ENM in Adriatic Sea v2\\Step 9 - Comparison Matrix");
		
		boolean calculate = true;

		String[] speciesList = { "Engraulis encrasicolus", "Merluccius merluccius", "Mullus barbatus", "Parapenaeus longirostris", "Sardina pilchardus", "Sepia officinalis", "Solea solea", "Squilla mantis" };

		String[] referenceModelsList = { "2015-2018", "2019", "2020", "AquaMaps2019", "AquaMaps2050" };

		String[] referenceModelsLabels = { "Ensemble Remote Sensing", "2019 Remote Sensing", "2020 Remote Sensing", "AquaMaps 2019", "AquaMaps 2050" };

		
		
/*		
		//String[] speciesList = { "Engraulis encrasicolus"};
		//String[] speciesList = { "Parapenaeus longirostris"};
		String[] speciesList = { "Merluccius merluccius"};
		String[] referenceModelsList = { "2020", "2015-2018"};

		String[] referenceModelsLabels = {  "Remote Sensing", "Ensemble Remote Sensing"};
	*/	
		
		
		int[] quartiles = { 1, 2 };
		//int[] quartiles = {1};
		
		//int[] quartiles = { 2 };
		if (calculate) {
			String accuracies[][] = new String[referenceModelsList.length][referenceModelsList.length];
			String kappas[][] = new String[referenceModelsList.length][referenceModelsList.length];
			String kappaInterprs[][] = new String[referenceModelsList.length][referenceModelsList.length];
			String comments[][] = new String[referenceModelsList.length][referenceModelsList.length];
			String suitabilityExtension[][] = new String[referenceModelsList.length][referenceModelsList.length];
			
			for (int quartile : quartiles) {
				for (String species : speciesList) {

					for (int i = 0; i < referenceModelsList.length; i++) {

						String modelNameA = referenceModelsList[i];
						String modelLabelA = referenceModelsList[i];
						File modelA = getModelFile(species, modelNameA, modelsFolder);
						AscRasterReader rt = new AscRasterReader();
						AscRaster A = rt.readRaster(modelA.getAbsolutePath());
						double thresholdA = getModelThreshold(species, modelNameA, quartilesFolder, quartile);
						double thresholdAH = getModelThreshold(species, modelNameA, quartilesFolder, 2);
						
						for (int j = 0; j < referenceModelsList.length; j++) {
							if (j != i) {

								String modelNameB = referenceModelsList[j];
								String modelLabelB = referenceModelsList[j];
								File modelB = getModelFile(species, modelNameB, modelsFolder);
								AscRaster B = rt.readRaster(modelB.getAbsolutePath());

								System.out.println("Comparing " + modelNameA + " vs " + modelNameB + " Quartile " + quartile + " (" + species + ")");

								double thresholdB = getModelThreshold(species, modelNameB, quartilesFolder, quartile);
								double thresholdBH = getModelThreshold(species, modelNameB, quartilesFolder, 2);
								
								System.out.println("A thr: " + thresholdA + " vs B thr: " + thresholdB);

								AscCompare comparator = new AscCompare();

								//comparator.compareWithoutNoData(A, B, thresholdA, thresholdB, thresholdAH, thresholdBH, 0, false, true);
								
								//comparator.compareWithoutNoData(A, B, thresholdA, thresholdB, 0, false, true);
								
								comparator.compareDico(A, B, thresholdA, thresholdB,thresholdAH,thresholdBH);
								
								/*
								accuracies[i][j] = comparator.discrepancyInfo.get("Discrepancy of habitat 2 to habitat 1 (discrepancy)");

								kappaInterprs[i][j] = comparator.discrepancyInfo.get("Kappa - agreement (interpretation Landis-Koch)") + "/" + comparator.discrepancyInfo.get("Kappa - agreement (interpretation Fleiss)");

								kappas[i][j] = comparator.discrepancyInfo.get("Kappa - agreement") + " (" + kappaInterprs[i][j] + ")";

								String core = comparator.discrepancyInfo.get("Suitability Score (Core)");
								if (core.length()>0 && !modelNameA.contains("AquaMaps") && !modelNameB.contains("AquaMaps")) {
									if (core.startsWith("-"))
										suitabilityExtension[i][j] = comparator.discrepancyInfo.get("Suitability Assessment") +"  ("+core+")";
									else
										suitabilityExtension[i][j] = comparator.discrepancyInfo.get("Suitability Assessment") +" ("+core+")";
								}
								else
									suitabilityExtension[i][j] = comparator.discrepancyInfo.get("Suitability Assessment");
								
								comments[i][j] = comparator.showStats().trim().replace("\n", "; ");
								 */
								
								accuracies[i][j] = comparator.discrepancyInfo.get("discrepancy")+"%";
								
								double core = Double.parseDouble(comparator.discrepancyInfo.get("suitability"));
								
								if (!modelNameA.contains("AquaMaps") && !modelNameB.contains("AquaMaps")) {
									if (core>0)
										suitabilityExtension[i][j] = "Gain (+"+core+"%)";
									else if (core<0)
										suitabilityExtension[i][j] = "Loss ("+core+"%)";
									else
										suitabilityExtension[i][j] = "Stable";
								}else {
									if (core>0)
										suitabilityExtension[i][j] = "Gain";
									else if (core<0)
										suitabilityExtension[i][j] = "Loss";
									else
										suitabilityExtension[i][j] = "Stable";
								}
									
								
								comments[i][j] = comparator.showStats().trim().replace("\n", "; ");
								kappas[i][j] = comparator.discrepancyInfo.get("kappa") + " (" + comparator.discrepancyInfo.get("kappa interp") + ") "+comparator.discrepancyInfo.get("student");
								
								File discrepancyFile = new File(outputFolder, species + "_" + modelLabelA + "_vs_" + modelLabelB + "_q" + quartile + ".asc");
								
								//DECOMMENT TO SAVE DISCREPANCY FILES
								//comparator.save(discrepancyFile);
								System.out.println();
								//System.exit(0);
							} else {
								accuracies[i][j] = "-";
								kappas[i][j] = "-";
								comments[i][j] = "-";
								suitabilityExtension[i][j] = "-";
								kappaInterprs[i][j] = "-";
							}
						}
					}

					System.out.println("Writing files for " + species);
					writeFile(referenceModelsLabels, "accuracy", species, quartile, outputFolder, accuracies);
					writeFile(referenceModelsLabels, "kappa", species, quartile, outputFolder, kappas);
					writeFile(referenceModelsLabels, "suitability_extension", species, quartile, outputFolder, suitabilityExtension);
					//writeFile(referenceModelsLabels, "comments", species, quartile, outputFolder, comments);
					
					System.out.println("Writing files - done.");
					// break;
				}
			}
		}
		String label = "accuracy";
		merge(outputFolder, label, quartiles, speciesList);
		label = "kappa";
		merge(outputFolder, label, quartiles, speciesList);
		label = "suitability_extension";
		merge(outputFolder, label, quartiles, speciesList);
		//label = "comments";
		//merge(outputFolder, label, quartiles, speciesList);
	}

	public static void merge(File modelsFolder, String label, int[] quartiles, String[] speciesList) throws Exception{

		for (int quartile : quartiles) {

			FileWriter o = new FileWriter(new File(modelsFolder, label + "_ALL_q" + quartile + ".csv"));

			for (String species : speciesList) {
				o.write(species + "\n");
				File f = new File(modelsFolder, label + "_" + species + "_q" + quartile + ".csv");
				String completef = new String(Files.readAllBytes(f.toPath()));
				o.write(completef + "\n");
				f.delete();
			}

			o.close();
		}

	}

	public static void writeFile(String[] referenceModelsList, String filelabel, String species, int quartile, File modelsFolder, String[][] metrics) throws Exception {
		FileWriter fw = new FileWriter(new File(modelsFolder, filelabel + "_" + species + "_q" + quartile + ".csv"));

		for (int i = -1; i < referenceModelsList.length; i++) {
			if (i == -1) {
				fw.write(",");
			} else {
				String modelLabelA = referenceModelsList[i];
				fw.write(modelLabelA);
				if (i < referenceModelsList.length - 1)
					fw.write(",");
				else
					fw.write("\n");
			}
		}

		for (int i = 0; i < metrics.length; i++) {
			String modelLabelA = referenceModelsList[i];
			fw.write(modelLabelA + ",");

			for (int j = 0; j < metrics.length; j++) {
				fw.write(metrics[i][j]);
				if (j < metrics.length - 1)
					fw.write(",");
				else
					fw.write("\n");
			}

		}

		fw.close();
	}

	public static double getModelThreshold(String species, String model, File folder, int quartile) throws Exception {

		if (model.contains("AquaMaps")) {
			if (quartile == 1)
				return 0.2;
			else
				return 0.8;
		} else {
			List<String> thresholds = Files.readAllLines(new File(folder, "models_quartiles.csv").toPath());
			
			for (String threshold : thresholds) {
				threshold= threshold.replace("\"", "");
				String[] el = threshold.split(",");
				String s = el[0];
				String m = el[1];
				if (s.equals(species) && m.equals(model)) {

					if (quartile == 1)
						return Double.parseDouble(el[3]);
					else
						return Double.parseDouble(el[4]);

				}

			}

		}
		return 0;
	}

	public static File getModelFile(String species, String model, File folder) {

		File modelFile = new File(folder, species + "_" + model + ".asc");
		return modelFile;
	}

}
