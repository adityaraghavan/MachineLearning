import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HMM_Model {

	private double A[][];
	private double B[][];
	private double pi[];

	private int N;
	private int M;
	private int T;
	
	private int minIters;
	private int iters;
	
	private double alpha[][];
	private double beta[][];
	private double c[];
	private double gamma[][];
	private double diGamma[][][];

	public static void main(String[] args) throws IOException {

		HMM_Model obj = new HMM_Model();
		obj.process();
	}

	private int process() throws IOException{

		String strSequence = initializations();

		getAlphaBetaGamma(strSequence);

		return 1;
	}

	private String initializations()throws IOException{

		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		System.out.print("Enter the Size of N - ");
		N = Integer.parseInt(br.readLine());
		//initializeStateTransMatrix();
		initializeStateTransMatrix(br);

		System.out.print("Enter the Size of M - ");
		M = Integer.parseInt(br.readLine());
		
		System.out.print("Enter Min Number of Iterations - ");
		minIters = Integer.parseInt(br.readLine());
		iters = 0;
		
		System.out.print("Enter length of Observation Sequence - ");
		T = Integer.parseInt(br.readLine());
		initializeObsProbabMatrix();
		
		br.close();
		
		br = new BufferedReader(new FileReader("C:/Users/Aditya Raghavan/Desktop/BrownCorpus.txt"));
		
	    StringBuilder strBuilder = new StringBuilder();
	    String line = br.readLine();
	    while (line != null) {
	    	
	    	for(int i=0; i< line.length();i++){
	    		int tempChar = line.charAt(i);
	    		if(tempChar == 32 || (tempChar>96 && tempChar<123)){
	    			strBuilder.append((char)tempChar);
	    	   	}else if((tempChar>64 && tempChar<91)){
	    	   		strBuilder.append((char)(tempChar+32));
	    	   	}
	    	}
	    	if(strBuilder.length() > T)
	    		break;
	        line = br.readLine();
	    }
		String strSequence = strBuilder.toString();
		System.out.println(strSequence);
		br.close();		
		
		return strSequence;
	}

/*	private void initializeStateTransMatrix(){

		A = new double[N][N];

		pi = new double[N];

		double a[] = new double[N+1];
		double distribSum = 0;
		for(int i=1;i<N+1;i++){
			double temp = (17+10*i);
			a[i] = temp;
			distribSum += temp;
		}
		
		for(int i=0 ; i<N ; i++){
			pi[i] = a[i+1]/distribSum;
			for(int j=0 ; j<N ; j++){
				A[i][j] = a[j+1]/distribSum;
				System.out.println(A[i][j]);
			}
		}		
	}
*/
	private void initializeStateTransMatrix(BufferedReader br)throws IOException{

		pi = new double[N];
		System.out.println("Enter Initial distribution matrix values");
		for(int i=0;i<N;i++){
			pi[i] = Double.parseDouble(br.readLine());
		}
		
		A = new double[N][N];
		System.out.println("Enter State distribution matrix values");
		for(int i=0;i<N;i++){
			for(int j=0;j<N;j++){
				A[i][j] = Double.parseDouble(br.readLine());	
			}			
		}
	}

	
	private void initializeObsProbabMatrix(){

		B = new double[N][M];

		double b[] = new double[M+1];
		double distribSum = 0;
		for(int i=1;i<M+1;i++){
			double temp = (17+10*i);
			b[i] = temp;
			distribSum += temp;
		}

		for(int i=0 ; i<N ; i++){
			for(int j=0 ; j<M ; j++){
				B[i][j] = b[j+1]/distribSum;
				System.out.println(B[i][j]);
			}
		}
	}

	private void getAlphaBetaGamma(String strSequence){
		
		//double alphaSum = 0;
	
		getAlpha(strSequence);	
		getBeta(strSequence);
		getGamma(strSequence);
		
		reEstimate(strSequence);
		
		//System.out.println(getLogOfProb();
		double logProb = getLogOfProb();
	//	itrCheck(logProb, strSequence);
		
		iters++;
		if(iters < minIters){
			getAlphaBetaGamma(strSequence);
		}else{
			System.out.println("A matrix");
			for(int i=0;i<N;i++){
				for(int j=0;j<N;j++){
					System.out.println(A[i][j]);
				}	
			}
			System.out.println("B matrix");
			for(int i=0;i<N;i++){
				for(int j=0;j<M;j++){
					System.out.println(B[i][j]);
				}	
			}
		}
	}
	
	private double getLogOfProb(){
		
		double logProb = 0;
		
		for(int i=0;i<T-1;i++){
			logProb += Math.log(c[i]);
		}
		logProb = -logProb;
		
		return logProb;
	}
	
	private int getTempIndex(String strSequence, int index){
		
		int tempIndex = strSequence.charAt(index) - 97;
		if(tempIndex < 0){
			tempIndex = M-1;
		}
		//System.out.println("index - " + tempIndex);
		return tempIndex;
	}
	
	private void getAlpha(String strSequence){

		alpha = new double[T][N];

		c = new double[T];

		//Aplha 0's
		c[0] = 0;
		for(int i=0; i<N; i++){
			int tempIndex = getTempIndex(strSequence, 0);
			alpha[0][i] = pi[i] * B[i][tempIndex];
			c[0] = c[0] + alpha[0][i];
		}

		//Scale alpha 0
		c[0] = 1/c[0];
		for(int i=0; i<N ; i++){
			alpha[0][i] = c[0]*alpha[0][i];
		}

		//Aplha from 1 to T-1
		for(int t=1; t<T; t++){
			c[t] = 0;
			for(int i=0; i<N; i++){
				int tempIndex = getTempIndex(strSequence, t);
				alpha[t][i] = 0;
				for(int j=0;j<N;j++){
					alpha[t][i] += alpha[t-1][j] * A[j][i]; 
				}						  
				alpha[t][i] = alpha[t][i] * B[i][tempIndex];
				c[t] = c[t] + alpha[t][i];
			}
			c[t] = 1/c[t];
			//Scaling Alpha's
			for(int i=0; i<N; i++){
				alpha[t][i] = c[t] * alpha[t][i];
			}
		}
	//	double alphaTotal = alpha[T-1][0] + alpha[T-1][1];
	}

	private void getBeta(String strSequence){

		beta = new double[T][N];

		//Scaling beta T-1 as Beta T-1 is 1
		for(int i=0; i<N; i++){
			beta[T-1][i] = c[T-1]; 
		}

		//Aplha from T-2 to 0
		for(int t=T-2; t>=0; t--){

			for(int i=0; i<N; i++){
				int tempIndex = getTempIndex(strSequence, (t+1));
				beta[t][i]=0;
				for(int j=0;j<N;j++){
					beta[t][i] += A[i][j] * B[j][tempIndex] * beta[t+1][j]; 
				}						  
				beta[t][i] = c[t] * beta[t][i];
				//System.out.println("Beta[" +t+"]["+i+"]= " + beta[t][i]);
			}
		}
	}


	private void getGamma(String strSequence){

		gamma = new double[T][N];
		diGamma = new double[T][N][N];
		
		for(int t=0; t<T-1 ; t++){
			double denom = 0;
			int tempIndex = getTempIndex(strSequence, (t+1));
			for(int i=0; i<N ; i++){
				for(int j=0; j<N ; j++){
					denom += alpha[t][i] * A[i][j] * B[j][tempIndex] * beta[t+1][j];
				}
			}
			for(int i=0; i<N; i++){
				gamma[t][i] = 0;
				for(int j=0; j<N ; j++){
					diGamma[t][i][j] = (alpha[t][i] * A[i][j] * B[j][tempIndex] * beta[t+1][j])/denom;
					gamma[t][i] = gamma[t][i] + diGamma[t][i][j];
				}
			}
		}
		
		//For Gamma T-1
		double denom = 0;
		for(int i=0; i<N ; i++){
			denom = denom + alpha[T-1][i];
		}
		for(int i=0; i<N ; i++){
			gamma[T-1][i] = alpha[T-1][i]/denom;
		}
	}
	
	private void reEstimate(String strSequence){
		
		//Re-estimate Pi
		//System.out.println("Re-estiamted Pi");
		for(int i=0; i<N; i++){
			pi[i] = gamma[0][i];
			//System.out.println(pi[i]);
		}
		
		//Re-estimate A
		//System.out.println("Re-estiamted A");
		for(int i=0; i<N; i++){
			for(int j=0; j<N; j++){
				double num = 0;
				double denom = 0;
				for(int t=0; t< T-1; t++){
					num = num + diGamma[t][i][j];
					denom = denom + gamma[t][i];
				}
				A[i][j] = num/denom;
				//System.out.println(A[i][j]);
			}
		}
		
		//Re-estimate B
		//System.out.println("Re-estiamted B");
		for(int i=0; i<N; i++){
			for(int j=0; j<M; j++){
				double num = 0;
				double denom = 0;
				for(int t=0; t< T; t++){
					int tempIndex = getTempIndex(strSequence, t);
					if(tempIndex==j){
						num = num + gamma[t][i];
					}
					denom = denom + gamma[t][i];
				}
				B[i][j] = num/denom;
				//System.out.println(B[i][j]);
			}
		}
	}
}