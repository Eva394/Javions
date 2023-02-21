package ch.epfl.javions;
/*
 *  Author :        Mangano Eva
 *  Date :          21/02/2023
 */




public final class Units {
	public static final double CENTI = Math.pow(10,-2) ;
	public static final double KILO = Math.pow(10,3) ;
	
	
	private Units() {
	
	}
	
	public final class Angle {
		public final static double RADIAN = 1. ;
		public final static double TURN = 2*Math.PI ;
	}
}
