package it.cnr.raster.asc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

//compares two habitat maps over the extension of habitat 1
public class AscComparator {

	public LinkedHashMap<String, String> discrepancyInfo = new LinkedHashMap<>();
	public AscRaster discrepancyRaster = null;

	public void compare(AscRaster r1, AscRaster r2, double tolerance, boolean isRelativeTolerance, boolean toleranceAsAThreshold) throws Exception {

		double[][] data1 = r1.data;
		double[][] data2 = r2.data;

		double cumulativeDiscrepancy = 0;
		int countAll = data1.length * data1[0].length;

		int count1NODATA2DATA = 0;
		int count1DATA2NODATA = 0;
		int count1NODATA2NODATA = 0;
		int count1LOW2HIGH = 0;
		int count1HIGH2LOW = 0;
		int count1DATA2DATA = 0;
		int count1SAME2DATA = 0;

		double noData1 = Double.parseDouble(r1.NDATA);
		double noData2 = Double.parseDouble(r2.NDATA);

		double minDiscrepancy = Double.MAX_VALUE;
		double maxDiscrepancy = 0;

		double discrData[][] = new double[data1.length][data1[0].length];
		HashMap<Integer, Double> allD = new HashMap<>();
		int nonzero = 0;
		for (int i = 0; i < data1.length; i++) {

			for (int j = 0; j < data1[0].length; j++) {
				discrData[i][j] = noData1;
				double d1 = data1[i][j];
				double d2 = Double.NaN;
				if (j < data2[0].length)
					d2 = data2[i][j];

				if ((d1 == noData1) && (d2 == noData2) || (Double.isNaN(d1) && Double.isNaN(d2))) {
					count1NODATA2NODATA++;
				} else if ((d1 == noData1) && (d2 != noData2) || (Double.isNaN(d1) && !Double.isNaN(d2))) {
					count1NODATA2DATA++;
				} else if ((d1 != noData1) && (d2 == noData2) || (!Double.isNaN(d1) && Double.isNaN(d2))) {
					count1DATA2NODATA++;
				} else {
					if (toleranceAsAThreshold) {
						if (d1 >= tolerance) {
							d1 = 1;
						} else
							d1 = 0;
						if (d2 >= tolerance) {
							nonzero++;
							d2 = 1;
						} else
							d2 = 0;
					}
					count1DATA2DATA++;
					double discrepancy = Math.abs(d1 - d2);
					cumulativeDiscrepancy = cumulativeDiscrepancy + discrepancy;
					double d = discrepancy;
					allD.put(count1DATA2DATA, discrepancy);

					if (isRelativeTolerance && !toleranceAsAThreshold)
						d = discrepancy / Math.abs(d1);

					discrData[i][j] = discrepancy;

					if (d <= tolerance) {
						count1SAME2DATA++;

						if (discrepancy < minDiscrepancy)
							minDiscrepancy = discrepancy;
						if (discrepancy > maxDiscrepancy)
							maxDiscrepancy = discrepancy;

					} else if (d1 < d2) {
						count1LOW2HIGH++;
					} else if (d1 > d2) {
						count1HIGH2LOW++;
					} else {
						System.out.println("INCONSISTENCY " + data1[i][j] + ";" + data2[i][j] + " tol " + tolerance + " d " + d);

					}

				}

				countAll++;
			}

		}
		System.out.println("nonzero " + nonzero);
		int count1NOTSAME2DATA = count1DATA2DATA - count1SAME2DATA;
		double avgDiscrepancy = cumulativeDiscrepancy / (double) count1DATA2DATA;
		double accuracy = (double) count1SAME2DATA / (double) (count1DATA2DATA + count1NODATA2DATA + count1DATA2NODATA);
		Collection<Double> values = allD.values();
		Double[] allvalues = new Double[values.size()];
		int vv = 0;
		for (Double v : values) {
			allvalues[vv] = v;
			vv++;
		}

		double relativeError = avgDiscrepancy / maxDiscrepancy;
		String suitability = "";

		double suitabilityCore = (double) (count1HIGH2LOW - count1LOW2HIGH) / (double) (count1HIGH2LOW);
		String suitabilityInterpretation = "";
		if (suitabilityCore > 0) {
			suitability = "First habitat is more suitable in the core regions (of " + Utils.roundDecimal((suitabilityCore * 100), 1) + "%). ";
			suitabilityInterpretation = "Higher suitability";
		} else if (suitabilityCore < 0) {
			suitability = "Second habitat is more suitable in the core regions (of " + Utils.roundDecimal((-1 * suitabilityCore * 100), 1) + "%). ";
			suitabilityInterpretation = "Lower suitability";
		} else {
			suitability = "Habitat suitability is equal in the core regions. ";
			suitabilityInterpretation = "Equal suitability";
		}

		double extension = (double) (count1NODATA2DATA - count1DATA2NODATA) / (double) (count1DATA2NODATA);
		String extensionInterpretation = "";
		if (extension > 0) {
			suitability += "Habitat extension increase: The second habitat is more extended (of " + Utils.roundDecimal((extension * 100), 1) + "%). ";
			extensionInterpretation = "More extended";
		} else if (extension < 0) {
			suitability += "Habitat extension decrease: The second habitat is less extended (of " + Utils.roundDecimal(Math.abs(extension * 100), 1) + "%). ";
			extensionInterpretation = "Less extended";
		} else {
			suitability += "Habitat extension is equal. ";
			extensionInterpretation = "Equally extended";
		}

		double suitabilityScore = (double) (count1DATA2NODATA + count1HIGH2LOW - count1NODATA2DATA - count1LOW2HIGH) / (double) (count1DATA2NODATA + count1HIGH2LOW);

		if (suitabilityScore > 0) {
			suitability += "First habitat is overall more suitable than the second (of " + Utils.roundDecimal((suitabilityScore * 100), 1) + "%). ";
		} else if (suitabilityScore < 0) {
			suitability += "Second habitat is overall more suitable than the first (of " + Utils.roundDecimal((-1 * suitabilityScore * 100), 1) + "%). ";
		} else {
			suitability += "Habitat suitability is equal overall. ";
		}

		double standardDeviation = Utils.standardDeviation(allvalues);
		discrepancyInfo.put("Maximum Discrepancy", "" + maxDiscrepancy);
		discrepancyInfo.put("Minimum Discrepancy", "" + minDiscrepancy);
		discrepancyInfo.put("Average Discrepancy", "" + Utils.roundDecimal(avgDiscrepancy, 3));
		discrepancyInfo.put("Relative Discrepancy", "" + Utils.roundDecimal(relativeError, 3));
		discrepancyInfo.put("Standard Deviation of Discrepancy", "" + Utils.roundDecimal(standardDeviation, 2));
		discrepancyInfo.put("Similarity of habitat 2 to habitat 1 (accuracy)", "" + Utils.roundDecimal(accuracy * 100, 2) + "%");
		discrepancyInfo.put("Number of comparisons", "" + countAll);
		discrepancyInfo.put("Comparison resolution", "" + r1.cellsize);
		discrepancyInfo.put("Relative suitability in the core area", "" + Utils.roundDecimal(suitabilityCore, 2));
		discrepancyInfo.put("Relative expansion ", "" + Utils.roundDecimal(extension, 2));
		discrepancyInfo.put("Relative suitability in the whole area", "" + Utils.roundDecimal(suitabilityScore, 2));
		discrepancyInfo.put("No data 1 vs No data 2", "" + count1NODATA2NODATA);
		discrepancyInfo.put("No data 1 vs data 2", "" + count1NODATA2DATA);
		discrepancyInfo.put("data 1 vs No data 2", "" + count1DATA2NODATA);
		discrepancyInfo.put("data 1 vs data 2", "" + count1DATA2DATA);
		discrepancyInfo.put("data 1 = data 2", "" + count1SAME2DATA);
		discrepancyInfo.put("data 1 != data 2", "" + count1NOTSAME2DATA);
		discrepancyInfo.put("data 1 lower than data 2", "" + count1LOW2HIGH);
		discrepancyInfo.put("data 1 higher than data 2", "" + count1HIGH2LOW);
		double kappaCoverage = Utils.cohensKappaForDichotomy(count1DATA2DATA, count1DATA2NODATA, count1NODATA2DATA, count1NODATA2NODATA);
		discrepancyInfo.put("Kappa - coverage agreement", "" + kappaCoverage);
		discrepancyInfo.put("Kappa - coverage agreement (interpretation Landis-Koch)", Utils.kappaClassificationLandisKoch(kappaCoverage));
		discrepancyInfo.put("Kappa - coverage agreement (interpretation Fleiss)", Utils.kappaClassificationFleiss(kappaCoverage));

		double kappa = Utils.cohensKappaForDichotomy(count1SAME2DATA, count1HIGH2LOW, count1LOW2HIGH, count1NODATA2NODATA);
		discrepancyInfo.put("Kappa - agreement", "" + Utils.roundDecimal(kappa, 2));
		discrepancyInfo.put("Kappa - agreement (interpretation Landis-Koch)", Utils.kappaClassificationLandisKoch(kappa));
		discrepancyInfo.put("Kappa - agreement (interpretation Fleiss)", Utils.kappaClassificationFleiss(kappa));
		discrepancyInfo.put("Assessment", suitabilityInterpretation + "/" + extensionInterpretation);
		discrepancyInfo.put("Comment", suitability);

		discrepancyRaster = new AscRaster(discrData, r1.cellsize, r1.dx, r1.dy, r1.xll, r1.yll);
	}

	public void compareWithoutNoData(AscRaster r1, AscRaster r2, double threshold1, double threshold2, double tolerance, boolean isRelativeTolerance, boolean toleranceAsAThreshold) throws Exception {
		compareWithoutNoData(r1, r2, threshold1, threshold1, threshold2, threshold2, tolerance, isRelativeTolerance, toleranceAsAThreshold);
	}

	public void compareWithoutNoData(AscRaster r1, AscRaster r2, double threshold1, double threshold1H, double threshold2, double threshold2H, double tolerance, boolean isRelativeTolerance, boolean toleranceAsAThreshold) throws Exception {

		double[][] data1 = r1.data;
		double[][] data2 = r2.data;

		double cumulativeDiscrepancy = 0;
		int countAll = data1.length * data1[0].length;

		int count1NODATA2DATA = 0;
		int count1DATA2NODATA = 0;
		int count1NODATA2NODATA = 0;
		int count1LOW2HIGH = 0;
		int count1HIGH2LOW = 0;
		int count1DATA2DATA = 0;
		int count1SAME2DATA = 0;
		int count1HIGH2HIGH = 0;
		int count1LOW2LOW = 0;
		int count1HIGH = 0;
		int count2HIGH = 0;

		double noData1 = Double.parseDouble(r1.NDATA);
		double noData2 = Double.parseDouble(r2.NDATA);

		double minDiscrepancy = Double.MAX_VALUE;
		double maxDiscrepancy = 0;

		double discrData[][] = new double[data1.length][data1[0].length];
		HashMap<Integer, Double> allD = new HashMap<>();
		int nonzero = 0;
		List<Double> distribution1 = new ArrayList<Double>();
		List<Double> distribution2 = new ArrayList<Double>();
		double maxd1 = 0;
		double maxd2 = 0;

		int count1HIGHINDISCREPANCY = 0;
		int count2HIGHINDISCREPANCY = 0;

		for (int i = 0; i < data1.length; i++) {

			for (int j = 0; j < data1[0].length; j++) {
				discrData[i][j] = noData1;
				double d1 = data1[i][j];
				double d2 = Double.NaN;
				if (j < data2[0].length && i < data2.length)
					d2 = data2[i][j];

				if ((d1 == noData1) && (d2 == noData2) || (Double.isNaN(d1) && Double.isNaN(d2))) {
					count1NODATA2NODATA++;
				} else if ((d1 == noData1) && (d2 != noData2) || (Double.isNaN(d1) && !Double.isNaN(d2))) {
					count1NODATA2DATA++;
				} else if ((d1 != noData1) && (d2 == noData2) || (!Double.isNaN(d1) && Double.isNaN(d2))) {
					count1DATA2NODATA++;
				} else {
					if (toleranceAsAThreshold) {
						if (d1 > threshold1) {
							// System.out.println("D1>"+d1+" > "+threshold1);
							distribution1.add(d1);
							if (d1 > maxd1)
								maxd1 = d1;

							d1 = 1;

							count1HIGH++;
						} else
							d1 = 0;
						if (d2 > threshold2) {
							// System.out.println("D2>"+d2+" > "+threshold2);
							distribution2.add(d2);
							if (d2 > maxd2)
								maxd2 = d2;

							nonzero++;
							d2 = 1;
							count2HIGH++;
						} else
							d2 = 0;
					}
					count1DATA2DATA++;

					double discrepancy = (d1 - d2);
					cumulativeDiscrepancy = cumulativeDiscrepancy + Math.abs(discrepancy);

					double d = discrepancy;
					allD.put(count1DATA2DATA, discrepancy);

					if (isRelativeTolerance && !toleranceAsAThreshold)
						d = discrepancy / Math.abs(d1);

					discrData[i][j] = discrepancy;

					if (d1 < d2) {
						count1LOW2HIGH++;
						if (d2 > threshold1H)
							count2HIGHINDISCREPANCY++;
					} else if (d1 > d2) {
						count1HIGH2LOW++;
						if (d1 > threshold1H)
							count1HIGHINDISCREPANCY++;
					}

					if (Math.abs(d) <= tolerance) {
						count1SAME2DATA++;

						if (discrepancy < minDiscrepancy)
							minDiscrepancy = discrepancy;
						if (discrepancy > maxDiscrepancy)
							maxDiscrepancy = discrepancy;

						if (d1 > threshold1 && d2 > threshold2)
							count1HIGH2HIGH++;
						else
							count1LOW2LOW++;

					} else {
						/*
						 * if (d1>threshold1H) count1HIGHINDISCREPANCY++; if
						 * (d2>threshold2H) count2HIGHINDISCREPANCY++;
						 */
					}

				}

				countAll++;
			}

		}

		// integrate
		int l1 = distribution1.size();
		for (int i = 0; i < l1; i++) {
			distribution1.set(i, distribution1.get(i) / maxd1);
		}
		int l2 = distribution2.size();
		for (int i = 0; i < l2; i++) {
			distribution2.set(i, distribution2.get(i) / maxd2);
		}
		double integral1 = 0;
		for (double d : distribution1) {
			integral1 += d;
		}
		double integral2 = 0;
		for (double d : distribution2) {
			integral2 += d;
		}

		System.out.println("nonzero " + nonzero);
		int count1NOTSAME2DATA = count1DATA2DATA - count1SAME2DATA;
		double avgDiscrepancy = cumulativeDiscrepancy / (double) count1DATA2DATA;
		double accuracy = (double) count1SAME2DATA / (double) (count1DATA2DATA);
		double discrepancy = 1 - accuracy;
		Collection<Double> values = allD.values();
		Double[] allvalues = new Double[values.size()];
		int vv = 0;
		for (Double v : values) {
			allvalues[vv] = v;
			vv++;
		}

		double relativeError = avgDiscrepancy / maxDiscrepancy;
		String suitability = "";

		// double suitabilityCore = (double)(count1HIGH2LOW-count1LOW2HIGH); //
		// /(double)(count1HIGH2LOW);
		// double suitabilityCore = (double)(integral1-integral2); //
		// /(double)(count1HIGH2LOW);
		double suitabilityCore = (double) (count1HIGHINDISCREPANCY - count2HIGHINDISCREPANCY);

		String suitabilityInterpretation = "";
		if (suitabilityCore > 0) {
			suitability = "First habitat is more suitable in the core regions (of " + Utils.roundDecimal((suitabilityCore * 100), 1) + "%). ";
			suitabilityInterpretation = "Higher suitability";
			// suitabilityCore=suitabilityCore/(double)integral1;
			suitabilityCore = suitabilityCore / (double) count1DATA2DATA;
		} else if (suitabilityCore < 0) {
			suitability = "Second habitat is more suitable in the core regions (of " + Utils.roundDecimal((-1 * suitabilityCore * 100), 1) + "%). ";
			suitabilityInterpretation = "Lower suitability";
			// suitabilityCore=suitabilityCore/(double)integral2;
			suitabilityCore = suitabilityCore / (double) count1DATA2DATA;
		} else {
			suitability = "Habitat suitability is equal in the core regions. ";
			suitabilityInterpretation = "Equal suitability";
		}

		double extension = (double) (count1NODATA2DATA - count1DATA2NODATA); // /(double)(count1DATA2NODATA);
		String extensionInterpretation = "";
		if (extension > 0) {
			suitability += "Habitat extension increase: The second habitat is more extended (of " + Utils.roundDecimal((extension * 100), 1) + "%). ";
			extensionInterpretation = "More extended";
		} else if (extension < 0) {
			suitability += "Habitat extension decrease: The second habitat is less extended (of " + Utils.roundDecimal(Math.abs(extension * 100), 1) + "%). ";
			extensionInterpretation = "Less extended";
		} else {
			suitability += "Habitat extension is equal. ";
			extensionInterpretation = "Equally extended";
		}

		double suitabilityScore = (double) (count1DATA2NODATA + count1HIGH2LOW - count1NODATA2DATA - count1LOW2HIGH) / (double) (count1DATA2NODATA + count1HIGH2LOW);

		if (suitabilityScore > 0) {
			suitability += "First habitat is overall more suitable than the second (of " + Utils.roundDecimal((suitabilityScore * 100), 1) + "%). ";
		} else if (suitabilityScore < 0) {
			suitability += "Second habitat is overall more suitable than the first (of " + Utils.roundDecimal((-1 * suitabilityScore * 100), 1) + "%). ";
		} else {
			suitability += "Habitat suitability is equal overall. ";
		}

		double standardDeviation = Utils.standardDeviation(allvalues);
		discrepancyInfo.put("Maximum Discrepancy", "" + maxDiscrepancy);
		discrepancyInfo.put("Minimum Discrepancy", "" + minDiscrepancy);
		discrepancyInfo.put("Average Discrepancy", "" + Utils.roundDecimal(avgDiscrepancy, 3));
		discrepancyInfo.put("Relative Discrepancy", "" + Utils.roundDecimal(relativeError, 3));
		discrepancyInfo.put("Standard Deviation of Discrepancy", "" + Utils.roundDecimal(standardDeviation, 2));
		discrepancyInfo.put("Similarity of habitat 2 to habitat 1 (accuracy)", "" + Utils.roundDecimal(accuracy * 100, 2) + "%");
		discrepancyInfo.put("Discrepancy of habitat 2 to habitat 1 (discrepancy)", "" + Utils.roundDecimal(discrepancy * 100, 2) + "%");
		discrepancyInfo.put("Number of comparisons", "" + countAll);
		discrepancyInfo.put("Comparison resolution", "" + r1.cellsize);
		discrepancyInfo.put("Relative suitability in the core area", "" + Utils.roundDecimal(suitabilityCore, 2));
		discrepancyInfo.put("Relative expansion ", "" + Utils.roundDecimal(extension, 2));
		discrepancyInfo.put("Relative suitability in the whole area", "" + Utils.roundDecimal(suitabilityScore, 2));
		discrepancyInfo.put("No data 1 vs No data 2", "" + count1NODATA2NODATA);
		discrepancyInfo.put("No data 1 vs data 2", "" + count1NODATA2DATA);
		discrepancyInfo.put("data 1 vs No data 2", "" + count1DATA2NODATA);
		discrepancyInfo.put("data 1 vs data 2", "" + count1DATA2DATA);
		discrepancyInfo.put("data 1 = data 2", "" + count1SAME2DATA);
		discrepancyInfo.put("data 1 != data 2", "" + count1NOTSAME2DATA);
		discrepancyInfo.put("data 1 lower than data 2", "" + count1LOW2HIGH);
		discrepancyInfo.put("data 1 higher than data 2", "" + count1HIGH2LOW);
		discrepancyInfo.put("data 1 low and data 2 low", "" + count1LOW2LOW);
		discrepancyInfo.put("data 1 high and data 2 high", "" + count1HIGH2HIGH);
		discrepancyInfo.put("data 1 high", "" + count1HIGH);
		discrepancyInfo.put("data 2 high", "" + count2HIGH);
		discrepancyInfo.put("data 1 very high in discrepancy locations", "" + count1HIGHINDISCREPANCY);
		discrepancyInfo.put("data 2 very high in discrepancy locations", "" + count2HIGHINDISCREPANCY);
		discrepancyInfo.put("data 1 integral", "" + integral1);
		discrepancyInfo.put("data 2 integral", "" + integral2);
		double kappaCoverage = Utils.cohensKappaForDichotomy(count1DATA2DATA, count1DATA2NODATA, count1NODATA2DATA, count1NODATA2NODATA);
		discrepancyInfo.put("Kappa - coverage agreement", "" + kappaCoverage);
		discrepancyInfo.put("Kappa - coverage agreement (interpretation Landis-Koch)", Utils.kappaClassificationLandisKoch(kappaCoverage));
		discrepancyInfo.put("Kappa - coverage agreement (interpretation Fleiss)", Utils.kappaClassificationFleiss(kappaCoverage));

		double kappa = Utils.cohensKappaForDichotomy(count1HIGH2HIGH, count1HIGH2LOW, count1LOW2HIGH, count1LOW2LOW);
		discrepancyInfo.put("Kappa - agreement", "" + Utils.roundDecimal(kappa, 2));
		discrepancyInfo.put("Kappa - agreement (interpretation Landis-Koch)", Utils.kappaClassificationLandisKoch(kappa));
		discrepancyInfo.put("Kappa - agreement (interpretation Fleiss)", Utils.kappaClassificationFleiss(kappa));
		discrepancyInfo.put("Suitability Assessment", suitabilityInterpretation);
		double sc = Utils.roundDecimal(suitabilityCore * 100, 2);
		if (sc > 0)
			discrepancyInfo.put("Suitability Score (Core)", "+" + sc + "%");
		else if (sc < 0)
			discrepancyInfo.put("Suitability Score (Core)", sc + "%");
		else
			discrepancyInfo.put("Suitability Score (Core)", "");
		discrepancyInfo.put("Extension Assessment", extensionInterpretation);
		discrepancyInfo.put("Comment", suitability);

		discrepancyRaster = new AscRaster(discrData, r1.cellsize, r1.dx, r1.dy, r1.xll, r1.yll);
	}

	public void compareDico(AscRaster r1, AscRaster r2, double threshold1, double threshold2, double threshold1H, double threshold2H) throws Exception {

		double[][] data1 = r1.data;
		double[][] data2 = r2.data;
		int maxh = Math.max(data1.length, data2.length);
		int maxw = Math.max(data1[0].length, data2[0].length);

		double noData1 = Double.parseDouble(r1.NDATA);
		double noData2 = Double.parseDouble(r2.NDATA);

		double discrData[][] = new double[maxh][maxw];
		int count1NODATA2NODATA = 0;
		int count12COMPARISONS = 0;
		int count1DATA2DATA = 0;
		int count1HIGH2LOW = 0;
		int count1LOW2HIGH = 0;
		int count1HIGH2HIGH = 0;
		int count1LOW2LOW = 0;
		int count1SAME2 = 0;
		int count1HIGH = 0;
		int count2HIGH = 0;
		int count1VERYHIGH = 0;
		int count2VERYHIGH = 0;
		double integral1=0;
		double integral2=0;
		double max1 = 0;
		double max2 = 0;
		
		for (int i = 0; i < maxh; i++) {
			for (int j = 0; j < maxw; j++) {
				discrData[i][j] = noData1;
				double d1 = 0;
				double d2 = 0;

				if (j < data1[0].length && i < data1.length)
					d1 = data1[i][j];

				if (j < data2[0].length && i < data2.length)
					d2 = data2[i][j];

				if ((d1 == noData1) && (d2 == noData2) || (Double.isNaN(d1) && Double.isNaN(d2))) {
					count1NODATA2NODATA++;

				} else {
					count12COMPARISONS++;
					
					if ((d1 == noData1) || Double.isNaN(d1))
						d1 = 0;
					else if ((d2 == noData1) || Double.isNaN(d2))
						d2 = 0;
					else
						count1DATA2DATA++;
					
					double d1o = d1;
					if (d1>threshold1) {
						integral1=integral1+d1;
						if (max1<d1)
							max1=d1;
						d1 = 1;
						count1HIGH++;
					}else {
						d1 = 0;
					}
					
					double d2o = d2;
					if (d2>threshold2) {
						integral2=integral2+d2;
						d2 = 1;
						if (max2<d2)
							max2=d2;
						count2HIGH++;
					}else {
						d2 = 0;
					}
						
					
					discrData[i][j] = d1-d2;
					//if (d1>d2) {
					if (d1 == 1 && d2==0) {
						count1HIGH2LOW++;
						if (d1o>threshold1H)
							count1VERYHIGH++;
						
					}else if (d1 == 0 && d2==1) {//if (d1<d2) {
						count1LOW2HIGH++;
						if (d2o>threshold2H)
							count2VERYHIGH++;
					}else {
						if (d1==0 && d2 ==0)
							count1LOW2LOW++;
						else if (d1==1 && d2 ==1)
							count1HIGH2HIGH++;
						
						count1SAME2++;
					}
				}
			}
		}
		
		
		System.out.println("count1NODATA2NODATA "+count1NODATA2NODATA );
		System.out.println("count12COMPARISONS "+count12COMPARISONS );
		System.out.println("count1DATA2DATA "+count1DATA2DATA );
		System.out.println("count1HIGH2LOW "+count1HIGH2LOW );
		System.out.println("count1LOW2HIGH "+count1LOW2HIGH );
		System.out.println("count1HIGH "+count1HIGH );
		System.out.println("count2HIGH "+count2HIGH );
		System.out.println("count1SAME2 "+count1SAME2 );
		System.out.println("count1HIGH2HIGH "+count1HIGH2HIGH );
		System.out.println("count1LOW2LOW "+count1LOW2LOW );
		System.out.println("count1VERYHIGH "+count1VERYHIGH );
		System.out.println("count2VERYHIGH "+count2VERYHIGH );
		System.out.println("integral 1 "+integral1);
		System.out.println("integral 2 "+integral2);
		
		integral1 = integral1/(max1);
		integral2 = integral2/(max2);
		
		System.out.println("integral 1 norm "+integral1);
		System.out.println("integral 2 norm "+integral2);
		
		
		double discrepancy = (double)(count1HIGH2LOW+count1LOW2HIGH)*100d/(double)count12COMPARISONS;
		//double suitability = (double)(count1HIGH2LOW-count1LOW2HIGH)*100d/(double)count12COMPARISONS;
		double suitability = (double)(count1VERYHIGH-count2VERYHIGH)*100d/(double)count12COMPARISONS;
		double kappa = Utils.cohensKappaForDichotomy(count1HIGH2HIGH, count1HIGH2LOW, count1LOW2HIGH, count1LOW2LOW);
		
		double student = Utils.studentT(count1HIGH, count2HIGH, count12COMPARISONS, count12COMPARISONS); 
				
		System.out.println("discrepancy "+discrepancy);
		System.out.println("suitability "+suitability );
		System.out.println("kappa "+kappa);
		System.out.println("student "+student);
		System.out.println("kappa interp "+Utils.kappaClassificationLandisKoch(kappa));
		
		discrepancyInfo.put("discrepancy", ""+Utils.roundDecimal(discrepancy,2));
		discrepancyInfo.put("suitability", ""+Utils.roundDecimal(suitability,2));
		discrepancyInfo.put("kappa", ""+Utils.roundDecimal(kappa,2));
		discrepancyInfo.put("kappa interp", ""+Utils.kappaClassificationLandisKoch(kappa));
		discrepancyInfo.put("student", ""+Utils.roundDecimal(student,2));
		
		discrepancyRaster = new AscRaster(discrData, r1.cellsize, r1.dx, r1.dy, r1.xll, r1.yll);
	}

	public String showStats() {
		String info = discrepancyInfo.toString();
		info = info.replace(", ", "\n").replace("{", "").replace("}", "");

		System.out.println(info);
		return info;
	}

	public void save(File outputFile) throws Exception {
		AscRasterWriter writer = new AscRasterWriter();
		writer.writeRaster(outputFile.getAbsolutePath(), discrepancyRaster);
	}

}
