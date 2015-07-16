import java.io.*;
import java.math.*;
import java.util.*;

import Jama.*;

public class Algorithm {
	public static void main( String[] args ) {
		File file = new File( "src\\data.txt" );
		double[][] dataValues = new double [3][2000];
		try {
			Scanner sc = new Scanner( file );
			int count = 0;
			while( sc.hasNextLine() && count < 2000 ) {
				sc.useDelimiter( ", |\\n" );
				Double x = Double.valueOf( sc.next() );
				Double y = Double.valueOf( sc.next() );
				Double z = Double.valueOf( sc.next() );
				dataValues[0][count] = x;
				dataValues[1][count] = y;
				dataValues[2][count] = z;
				count++;
				
			}
			sc.close();
		}
		catch( FileNotFoundException e ) {
			e.printStackTrace();
		}
		Matrix matrix = new Matrix( dataValues );
		SingularValueDecomposition svdMatrix = matrix.svd();
		Matrix uMatrix = svdMatrix.getU();
		
		double[] uVector = { uMatrix.get( 0 ,  0 ) , uMatrix.get( 0 ,  1 ) , uMatrix.get( 0 ,  2 ) };
		double[] vVector = { uMatrix.get( 1 ,  0 ) , uMatrix.get( 1 ,  1 ) , uMatrix.get( 1 ,  2 ) };
		double[] normalVector = { uMatrix.get( 2 , 0 ) , uMatrix.get( 2 , 1 ) , uMatrix.get( 2 ,  2 ) };
		
		normalVector = getUnitVector( normalVector );
		
		Plane plane = new Plane( uVector , vVector , normalVector );
		plane.setPoint( getIntersectionBetweenThreeVectors( plane.getU(), plane.getV(), plane.getNormal() ) );
		
		double[][] points = matrix.getArrayCopy();
		double[] point = { points[0][0] , points[1][0] , points[2][0] };
		double[] pointOnPlane = getCoordsOf3DPointOnPlane( point , plane );
		
		System.out.println( Arrays.toString( plane.getU() ) );
		System.out.println( Arrays.toString( plane.getV() ) );
		System.out.println( Arrays.toString( plane.getNormal() ) );
		System.out.println( Arrays.toString( plane.getPoint() ) );
		System.out.println( Arrays.toString( point ) );
		System.out.println( Arrays.toString( pointOnPlane ) );
		
		
	}
	public static double[] getCoordsOf3DPointOnPlane( double[] point , Plane plane ) {
		double dist = getDistanceFromPointToPlane( point , plane );
		double[] normal = plane.getNormal();
		double[] vector = { dist*normal[0] , dist*normal[1] , dist*normal[2] };
		double[] pointOnPlane = { point[0] - vector[0] , point[1] - vector[1] , point[2] - vector[2] };
		return pointOnPlane;
	}
	public static double getDistanceFromPointToPlane( double[] point , Plane plane ) {
		double[] vector = getVector( point , plane.getPoint() );
		double[] normal = plane.getNormal();
		
		double dist = ( Math.abs( getDotProduct( normal , vector ) ) / getMagnitude( normal ) );
		
		return dist;
	}
	public static double[] getIntersectionBetweenThreeVectors( double[] v1 , double[] v2 , double[] v3 ) {
		double[][] vectors = { v1 , v2 , v3 };
		
		double[][] case1 = { {0,v1[1],v1[2]} , {0,v2[1],v2[2]} , {0,v3[1],v3[2]} };
		double[][] case2 = { {v1[0],0,v1[2]} , {v2[0],0,v2[2]} , {v3[0],0,v3[2]} };
		double[][] case3 = { {v1[0],v1[1],0} , {v2[0],v2[1],0} , {v3[0],v3[1],0} };
		
		Matrix matrix  = new Matrix( vectors );
		Matrix matrix1 = new Matrix( case1 );
		Matrix matrix2 = new Matrix( case2 );
		Matrix matrix3 = new Matrix( case3 );
		
		double totalDet = matrix.det();
		double firstDet = matrix1.det();
		double secDet = matrix2.det();
		double thirdDet = matrix3.det();
		
		double xCoord = firstDet / totalDet;
		double yCoord = secDet / totalDet;
		double zCoord = thirdDet / totalDet;
		
		double[] point = { xCoord , yCoord , zCoord };
		
		return point;
	}
	public static double getDotProduct( double[] u , double[] v ) {
		return u[0]*v[0] + u[1]*v[1] + u[2]*v[2];
	}
	public double[] getCrossProduct( double[] v1 , double[] v2 ) {
		double[] crossProduct = new double[3];
		crossProduct[0] = v1[1]*v2[2] - v2[0]*v1[1];
		crossProduct[1] = v1[2]*v2[0] - v2[1]*v1[2];
		crossProduct[2] = v1[0]*v2[1] - v2[2]*v1[0];
		return crossProduct;
	}
	public static double getMagnitude( double[] vector ) {
		return Math.sqrt( getDotProduct( vector , vector ) );
	}
	public static double[] getUnitVector( double[] vector ) {
		double[] newVector = new double[3];
		double magnitude = getMagnitude( vector );
		
		newVector[0] = vector[0] / magnitude;
		newVector[1] = vector[1] / magnitude;
		newVector[2] = vector[2] / magnitude;
		
		return newVector;
	}
	public static double[] getVector( double[] point1 , double[] point2 ) {
		double[] vector = new double[3];
		vector[0] = point2[0] - point1[0];
		vector[1] = point2[1] - point1[1];
		vector[2] = point2[2] - point1[2];
		return vector;
	}
}