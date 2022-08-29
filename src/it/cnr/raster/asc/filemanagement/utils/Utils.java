package it.cnr.raster.asc.filemanagement.utils;

public class Utils {

	
	public static double studentT(double q1, double q2, int n1, int n2) {
		
		double s_sqr = (double) (n1-1)+(n2-1)/ (double) (n1+n2-2);
		
		double T = (q1-q2)/Math.sqrt(s_sqr*s_sqr*((1d/(double)n1)+(1d/(double)n2)));
		return T;
	}
	
	public static double mean(double[] data) {
		
		// The mean average
				double mean = 0.0;
				for (int i = 0; i < data.length; i++) {
				        mean += data[i];
				}
				mean /= (double)data.length;
				return mean;
	}
	public static double standardDeviation(Double [] data) {
			
		
		// The mean average
		double mean = 0.0;
		for (int i = 0; i < data.length; i++) {
		        mean += data[i];
		}
		mean /= (double)data.length;

		// The variance
		double variance = 0;
		for (int i = 0; i < data.length; i++) {
		    variance += Math.pow(data[i] - mean, 2);
		}
		variance /= (double) (data.length-1);

		// Standard Deviation
		double std = Math.sqrt(variance);
		return std;
	}
	
	public static double cohensKappaForDichotomy(long NumOf_A1_B1, long NumOf_A1_B0, long NumOf_A0_B1, long NumOf_A0_B0){
		long  T = NumOf_A1_B1+NumOf_A1_B0+NumOf_A0_B1+NumOf_A0_B0;
		
		double Pra = (double)(NumOf_A1_B1+NumOf_A0_B0)/(double) T ;
		double Pre1 = (double) (NumOf_A1_B1+NumOf_A1_B0) * (double) (NumOf_A1_B1+NumOf_A0_B1)/(double) (T*T);
		double Pre2 = (double) (NumOf_A0_B0+NumOf_A0_B1) * (double) (NumOf_A0_B0+NumOf_A1_B0)/(double) (T*T);
		double Pre = Pre1+Pre2;
		double Kappa = (Pra-Pre)/(1d-Pre);
		return roundDecimal(Kappa,3);
	}
	
	//rounds to the xth decimal position
		public static double roundDecimal(double number,int decimalposition){
			
			double n = (double)Math.round(number * Math.pow(10.00,decimalposition))/Math.pow(10.00,decimalposition);
			return n;
		}
		
		
	public static String kappaClassificationLandisKoch(double kappa){
		if (kappa<0)
			return "Poor";
		else if ((kappa>=0)&&(kappa<=0.20))
			return "Slight";
		else if ((kappa>=0.20)&&(kappa<=0.40))
			return "Fair";
		else if ((kappa>0.40)&&(kappa<=0.60))
			return "Moderate";
		else if ((kappa>0.60)&&(kappa<=0.80))
			return "Substantial";
		else if (kappa>0.80)
			return "Almost Perfect";
		else
			return "Not Applicable";
	}
	
	public static String kappaClassificationFleiss(double kappa){
		if (kappa<0)
			return "Poor";
		else if ((kappa>=0)&&(kappa<=0.40))
			return "Marginal";
		else if ((kappa>0.4)&&(kappa<=0.75))
			return "Good";
		else if (kappa>0.75)
			return "Excellent";
		else
			return "Not Applicable";
	}
	
}
