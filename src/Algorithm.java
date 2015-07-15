import java.io.*;
import java.math.*;
import java.util.*;

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
		
	}
}