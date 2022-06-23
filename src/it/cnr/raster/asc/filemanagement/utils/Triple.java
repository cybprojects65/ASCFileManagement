package it.cnr.raster.asc.filemanagement.utils;

import java.io.Serializable;

public class Triple implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public double x; 
	public double y; 
	public double v;
	
	public Triple(double x, double y, double v) {
		this.x=x;
		this.y=y;
		this.v=v;
		
	}
	
	
}
