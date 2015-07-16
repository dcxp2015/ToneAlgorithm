import java.io.*;
import java.math.*;
import java.util.*;

import Jama.*;

public class Algorithm {
	public static void main( String[] args ) {
		File file = new File( "src\\data.txt" );
		int numLines = 0;
		try {
			Scanner sc = new Scanner( file );
			while( sc.hasNextLine() ) {
				sc.nextLine();
				numLines++;
			}
			sc.close();
		}
		catch( FileNotFoundException e ) {
			e.printStackTrace();
		}
		double[][] dataValues = new double[3][numLines];
		try {
			Scanner sc = new Scanner( file );
			int count = 0;
			while( sc.hasNextLine() && count < numLines ) {
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
		
		double[][] arrayOf2DPoints = new double[numLines][2];
		
		try {
			FileWriter fw = new FileWriter( "src/dataOutput.txt" );
			double[][] points = matrix.getArrayCopy();
			for( int i = 0; i < points[0].length; i++ ) {
				double[] point = { points[0][i] , points[1][i] , points[2][i] };
				double[] pointOnPlane = getCoordsOf3DPointOnPlane( point , plane );
				double[] constants = rref( pointOnPlane , plane );
				arrayOf2DPoints[i][0] = constants[0];
				arrayOf2DPoints[i][1] = constants[1];
//				System.out.println( constants[0] + ", " + constants[1] );
				fw.write( constants[0] + ", " + constants[1] );
				fw.write( System.getProperty( "line.separator" ) );
			}
			fw.close();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
//		System.out.println( Arrays.toString( plane.getU() ) );
//		System.out.println( Arrays.toString( plane.getV() ) );
//		System.out.println( Arrays.toString( plane.getNormal() ) );
//		System.out.println( Arrays.toString( plane.getPoint() ) );
//		System.out.println( Arrays.toString( point ) );
//		System.out.println( Arrays.toString( pointOnPlane ) );
		System.out.println( Arrays.deepToString( arrayOf2DPoints ) );
	}
	public static double[] rref( double[] pointOnPlane , Plane plane ) {
		double[] constants = new double[2];
		double[] uVector = plane.getU();
		double[] vVector = plane.getV();
		
		double a11 = uVector[0];
		double a12 = vVector[0];
		double a13 = pointOnPlane[0];
		double a21 = uVector[1];
		double a22 = vVector[1];
		double a23 = pointOnPlane[1];
		
//		double[][] matrixA = { {a11,a12,a13} , {a21,a22,a23} }; initialize matrix
//		divide to make first column 1
		double[][] matrixA = { {a11/a11,a12/a11,a13/a11} , {a21/a21,a22/a21,a23/a21} };
//		subtract row1 from row2
		matrixA[1][0] = matrixA[1][0] - matrixA[0][0];
		matrixA[1][1] = matrixA[1][1] - matrixA[0][1];
		matrixA[1][2] = matrixA[1][2] - matrixA[0][2];
//		divide second row to make a22 = 1
		double temp = matrixA[1][1];
		matrixA[1][1] = matrixA[1][1] / temp;
		matrixA[1][2] = matrixA[1][2] / temp;
//		subtract multiple of row2 from row1
		temp = matrixA[0][1];
		matrixA[0][1] = matrixA[0][1] - temp*matrixA[1][1];
		matrixA[0][2] = matrixA[0][2] - temp*matrixA[1][2];
		
		constants[0] = matrixA[0][2];
		constants[1] = matrixA[1][2];
		
		return constants;
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