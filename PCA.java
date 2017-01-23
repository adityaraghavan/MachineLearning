import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;


public class PCA {

	Matrix matrixA;
	Matrix matrixL;
	Matrix u;
	Matrix v;
	Matrix	scoringMatrix;
	double[][] eigVecArray;
	double[][] sMatrix;
	ArrayList<Matrix> listOfVectors;
	ArrayList<Matrix> listOfEigenVectors;
	
	public static void main(String[] args) {
		PCA obj = new PCA();
		obj.process();
	}

public int process(){
		
		
		getTrainingVectors();
		getEigenVectors();
		getScoringMatrix();
		getVectorsToBeScored();
		scoreVectors();

		return 1;
	}

	public void getTrainingVectors(){
		
		//Vector Initialization
		ArrayList<double[]> trainingVectors = new ArrayList<double[]>();
		//double v1[] = {2, -1, 0, 1, 1, -3, 5, 2};
		double v1[] = {2, 1, 0, 3, 1, 1};
		//double v1[] = {-1, 2, 1, 2, -1, 0};
		trainingVectors.add(v1);
		//double v2[] = {-2, 3, 2, 3, 0, 2, -1, 1};
		double v2[] = {2, 3, 1, 2, 3, 0};
		//double v2[] = {-2, 1, 2, 3, 2, 1};
		trainingVectors.add(v2);
		//double v3[] = {-1, 3, 3, 1, -1, 4, 5, 2};
		double v3[] = {1, 0, 3, 3, 1, 1};
		//double v3[] = {-1, 3, 0, 1, 3, -1};
		trainingVectors.add(v3);
		//double v4[] = {3, -1, 0, 3, 2, -1, 3, 0};
		double v4[] = {2, 3, 1, 0, 3, 2};
		//double v4[] = {0, 2, 3, 1, 1, -2};
		trainingVectors.add(v4);
		
		//A Matrix
		int iSizeOfTVector = trainingVectors.get(0).length;
		int iNoOfTVector = trainingVectors.size();
		
		double A[][] = new double[iSizeOfTVector][iNoOfTVector];
		for(int j=0; j<iNoOfTVector; j++){
			double temp[] = trainingVectors.get(j);
			for(int i=0; i<iSizeOfTVector; i++){
				A[i][j] = temp[i];
			}
		}
		
		matrixA = new Matrix(A);
		System.out.println("Matrix A - ");
		matrixA.print(2, 6);	
	}
	
	public void getEigenVectors(){
		
		listOfVectors = new ArrayList<Matrix>();
		listOfEigenVectors = new ArrayList<Matrix>();
		
		//L Matrix
		matrixL = matrixA.transpose().times(matrixA);
		System.out.println("Matrix L - ");
		matrixL.print(2, 6);	
		
		//Eigen Values of L
		EigenvalueDecomposition eigL = matrixL.eig();
		double eigVal[] = eigL.getRealEigenvalues();
		System.out.println("Eigen Values of L - ");
		for(int j=0; j<eigVal.length; j++){
			System.out.println(eigVal[j]);
		}
		
		System.out.println();
		//Eigen Vectors of L
		v = eigL.getV();
		System.out.println("Eigen Vectors of Matrix L - ");
		v.print(2, 6);
		
		//Eigen Vectors of C
		u = matrixA.times(v);
		System.out.println("Eigen Vectors of Matrix C - ");
		u.print(2, 6);
	}
	
	public void getScoringMatrix(){
		
		eigVecArray = u.getArray();
		
		//Unit EigenVectors of C
		int iVRows = u.getRowDimension();
		int iVColumns = u.getColumnDimension();
		double[] normalFactor = new double[iVColumns];
		
		for(int j=0; j<iVColumns; j++){
			double temp = 0;
			for(int i=0; i<iVRows; i++){
				temp += eigVecArray[i][j]*eigVecArray[i][j];
			}
			normalFactor[j] = Math.sqrt(temp);
		}
		
		for(int j=0; j<iVColumns; j++){
			double nFactor = normalFactor[j];
			for(int i=0; i<iVRows; i++){
				eigVecArray[i][j] = eigVecArray[i][j] / nFactor;
			}
		}
		
		//Normalized Eigen Vector Matrix of C
		u = new Matrix(eigVecArray);
		System.out.println("Normalized Eigen Vectors of Matrix C - ");
		u.print(2, 6);

		//Scoring Matrix
		scoringMatrix = matrixA.transpose().times(u);
		sMatrix = scoringMatrix.getArray();
		int iScoringRows = scoringMatrix.getRowDimension();
		int iScoringColumns = scoringMatrix.getColumnDimension();
		double[][] temp1 = new double[iScoringRows][iScoringColumns];
		for(int i=0; i<iScoringRows; i++){	
			for(int j=0; j<iScoringColumns; j++){
				temp1[i][j] = sMatrix[j][iScoringRows - i - 1];
			}
		}
		
		System.out.println("Scoring Matrix - ");
		scoringMatrix = new Matrix(temp1);
		scoringMatrix.print(2, 6);
		sMatrix = scoringMatrix.getArray();
	}
	
	public void getVectorsToBeScored(){
		
		System.out.println("EigenVectors");
		
		int iVRows = u.getRowDimension();
		int iVColumns = u.getColumnDimension();
		
		double t2[][] = new double[iVRows][iVColumns];
		for(int i=0; i<iVRows; i++){
			for(int j=0; j<iVColumns; j++){
				t2[i][iVColumns - j -1] =eigVecArray[i][j];
			}
		}
		for(int i=0; i<iVRows; i++){
			for(int j=0; j<iVColumns; j++){
				eigVecArray[i][j] = t2[i][j];
			}
		}
		System.out.println();
		for(int j=0; j<iVColumns; j++){
			double[][] tempArray = new double[iVRows][1];
			for(int i=0; i<iVRows; i++){
				tempArray[i][0] = eigVecArray[i][j];
			}
			listOfEigenVectors.add(new Matrix(tempArray));
		}
		
		Matrix a = new Matrix(new double[]{1, -1, 1, -1, -1, 1}, 1); 
		listOfVectors.add(a);
		Matrix b = new Matrix(new double[]{-2, 2, 2, -1, -2, 2}, 1);
		listOfVectors.add(b);
		Matrix c = new Matrix(new double[]{1, 3, 0, 1, 3, 1}, 1);
		listOfVectors.add(c);
		Matrix d = new Matrix(new double[]{2, 3, 1, 1, -2, 0}, 1);
		listOfVectors.add(d);
	}
	
	public void scoreVectors(){
		
		Iterator<Matrix> itr = listOfVectors.iterator();
		while(itr.hasNext()){
			
			Matrix vectorMatrix = itr.next();
			System.out.println("Vector - ");
			vectorMatrix.print(2, 6);
			
			Iterator<Matrix> itr1 = listOfEigenVectors.iterator();
			ArrayList<Double> W = new ArrayList<Double>();
			while(itr1.hasNext()){
				Matrix eigenVMatrix = itr1.next();
				double[][] res = vectorMatrix.times(eigenVMatrix).getArray();
				W.add(res[0][0]);
			}
			W.remove(W.size()-1);
			System.out.println("Eigen Space - " + W);
			
			int iScoringRows = scoringMatrix.getRowDimension();
			int iScoringColumns = scoringMatrix.getColumnDimension();
			
			ArrayList<Double> dist = new ArrayList<Double>();
			for(int j=0; j<iScoringColumns; j++){
				double distance = 0;
				for(int i=0; i<iScoringRows-1; i++){
						
					double temp = sMatrix[i][j] - W.get(i);
					distance += temp*temp;
				}
				distance = Math.sqrt(distance);
				dist.add(distance);
			}
			System.out.println("Distance = " + dist);
			Double[] eDistance = dist.toArray(new Double[0]);
			Arrays.sort(eDistance);
			System.out.println("Score for given vector = " + eDistance[0]);
			System.out.println();
		}		
	}
}
