public class Plane {
	double[] u;
	double[] v;
	double[] normal;
	double[] point;
	
	public Plane( double[] uVector , double[] vVector , double[] normalVector ) {
		u = uVector;
		v = vVector;
		normal = normalVector;
	}
	public void setPoint( double[] coords ) {
		point = coords;
	}
	public double[] getPoint() {
		return point;
	}
	public double[] getU() {
		return u;
	}
	public double[] getV() {
		return v;
	}
	public double[] getNormal() {
		return normal;
	}
}