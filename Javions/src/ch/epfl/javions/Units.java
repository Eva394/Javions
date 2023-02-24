package ch.epfl.javions;
/*
 *  Author :        Mangano Eva
 *  Date :          21/02/2023
 */




public final class Units {
	public static final double CENTI = 1e-2 ;
	public static final double KILO = 1e3 ;
	
	
	private Units() {
	
	}
	
	
	public static double convert(double value, double fromUnit, double toUnit) {
		return value*(fromUnit/toUnit) ;
	}
	
	public static double convertFrom(double value, double fromUnit) {
		return value*fromUnit ;
	}
	
	public static double convertTo(double value, double toUnit) {
		return value*(1/toUnit) ;
	}
	
	
	
	
	
	
	public final class Angle {
		
		public final static double RADIAN = 1.;
		public final static double TURN = 2 * Math.PI;
		public final static double DEGREE = TURN / (360);
		public final static double T32 = TURN / Math.scalb(1, 32);
		
		
		private Angle() {
		
		}
	}
	
	public final class Length {
		public final static double METER = 1 ;
		public final static double CENTIMETER = CENTI*METER ;
		public final static double KILOMETER = KILO*METER ;
		public final static double INCH = 2.54*CENTIMETER ;
		public final static double FOOT = 12*INCH ;
		public final static double NAUTICAL_MILE = 1852*METER ;
		
		private Length() {}
	}
	
	public final class Time {
		public final static double SECOND = 1 ;
		public final static double MINUTE = 60*SECOND ;
		public final static double HOUR = 60*MINUTE ;
		
		private Time() {}
	}
	
	public final class Speed {
		public final static double KNOT = Length.NAUTICAL_MILE/Time.HOUR ;
		public final static double KILOMETER_PER_HOUR = Length.KILOMETER/Time.HOUR ;
	}
	
	
}
