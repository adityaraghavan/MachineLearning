import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.clustering.mcl.MarkovClustering;


public class EMClustering {

	private double[] theta = null;
	private double[] tau = null;
	private double[] mu = null;
	private int[] X = null;
	private int j = 0;
	private int n = 0;
	private int N = 0;
	double p[][] = null;

	public static void main(String[] args) {

		EMClustering obj = new EMClustering();
		try {	
			obj.process();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int process() throws Exception{

		int iCheck = 0;
		iCheck = initialize();
		
		if(iCheck != 0){
			return -1;
		}
		for(int i=0; i<50; i++){
			System.out.println("Iteration - " + (i+1));
			eStep();
			mStep();
		}

		return 0;
	}
	
	private int initialize()throws Exception{

		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		System.out.print("Enter Number of Parameters - ");
		j = Integer.parseInt(br.readLine());

		theta = new double[j];
		tau = new double[j];
		mu = new double[j];

		System.out.println("Enter initial probability distribution values - ");
		for(int i=0; i<j; i++){
			theta[i] = Double.parseDouble(br.readLine());
		}

		System.out.println("Enter initial mixture parameters values - ");
		for(int i=0; i<j; i++){
			tau[i] = Double.parseDouble(br.readLine());
		}

		System.out.print("Enter number of trials per experiment - ");
		N = Integer.parseInt(br.readLine());

		System.out.print("Enter number of experiments - ");
		n = Integer.parseInt(br.readLine());
		X = new int[n];

		System.out.println("Enter results - ");
		for(int i=0; i<n; i++){
			X[i] = Integer.parseInt(br.readLine());
		}

		return 0;
	}

	private void eStep(){

		System.out.println("Performing E-STEP");
		p = new double[j][n];

		HashMap<Integer, ArrayList<Double>> pValues = new HashMap<Integer, ArrayList<Double>>();

		for(int i=0; i<n; i++){
			ArrayList<Double> pList = new ArrayList<Double>();
			for(int k=0; k<j; k++){
				pList.add(tau[k] * Math.pow(theta[k], X[i]) * Math.pow((1 - theta[k]), (N - X[i])));   
			}
			pValues.put(i, pList);
		}

		for(int i=0; i<n; i++){
			ArrayList<Double> pList  = pValues.get(i);
			double denom = 0;
			for(int k=0; k<j; k++){
				denom = denom + pList.get(k);
			}
			for(int k=0; k<j; k++){
				p[k][i] = (tau[k] * Math.pow(theta[k], X[i]) * Math.pow((1 - theta[k]), (N - X[i])))/denom;
			}   
		}	

		/*for(int i=0; i<n; i++){
			for(int k=0; k<j; k++){
				System.out.println("p["+(k+1)+"]["+(i+1)+"] = "+p[k][i]);
			}
		}*/
	}

	private void mStep(){

		System.out.println("Performing M-STEP");
		System.out.println("Re-estimated mixture parameters - ");
		for(int k=0; k<j; k++){
			double tempSum = 0;
			for(int i=0; i<n; i++){
				tempSum = tempSum + p[k][i];
			}
			tau[k] = tempSum/n;
			System.out.println("tau["+(k+1)+"] = "+tau[k]);
		}

		System.out.println("Re-estimated distribution probabilities - ");
		for(int k=0; k<j; k++){
			double tempSum = 0;
			for(int i=0; i<n; i++){
				tempSum = tempSum + (p[k][i] * X[i]);
			}
			mu[k] = tempSum/(tau[k] * n);
			theta[k] = mu[k]/N;
			System.out.println("theta["+(k+1)+"] = "+theta[k]);
		}
	}
}