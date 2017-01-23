import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class DataAnalysis {

	private int iMatchScores = 0;
	private double matchScores[];
	
	private int iNoMatchScores = 0;
	private double noMatchScores[];
	
	private ArrayList<Double> roc_X;
	private ArrayList<Double> roc_Y;
	
	private ArrayList<Double> pr_X;
	private ArrayList<Double> pr_Y;
	
	public static void main(String[] args) {
		
		DataAnalysis obj = new DataAnalysis();
		obj.process();
	}
	
	private int process(){
		
		try{
			initializations();
			analyzeScores();
		
		}catch(IOException e){
			return -1;
		}
		
		return 0;
	}
	
	private void initializations()throws IOException{
		
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		System.out.print("Enter the size of the match set ");
		iMatchScores = Integer.parseInt(br.readLine());
		initializeMatchSet(br);
		
		System.out.print("Enter the size of no-match set without duplication - ");
		iNoMatchScores = Integer.parseInt(br.readLine());
		initializeNoMatchSet(br);
		
		roc_X = new ArrayList<Double>();
		roc_Y = new ArrayList<Double>();
		pr_X = new ArrayList<Double>();
		pr_Y = new ArrayList<Double>();
		
		br.close();
	}
	
	private void initializeMatchSet(BufferedReader br)throws IOException{
		
		matchScores = new double[iMatchScores];
		System.out.println("Enter Match Set Scores");
		for(int i=0;i<iMatchScores;i++){
			matchScores[i] = Double.parseDouble(br.readLine());
		}
		Arrays.sort(matchScores);
	}
	
	private void initializeNoMatchSet(BufferedReader br)throws IOException{
		
		System.out.print("Enter the factor by which the no match set has to be duplicated - ");
		int iDuplicationFactor = Integer.parseInt(br.readLine()); 
		iNoMatchScores = iNoMatchScores * iDuplicationFactor;
		
		noMatchScores = new double[iNoMatchScores];
		System.out.println("Enter No-Match Set Scores without duplication");
		
		for(int i=0;i<iNoMatchScores;i++){
			noMatchScores[i] = Double.parseDouble(br.readLine());
			
			if(iDuplicationFactor > 1){
				for(int j=1; j<iDuplicationFactor; j++){
					noMatchScores[i+j] = noMatchScores[i];
				}
				i = i + iDuplicationFactor -1;
			}
		}
		Arrays.sort(noMatchScores);
	}
	
	private Double[] getThresholdValues(){
		
		
		Set<Double> thresholdValues = new HashSet<Double>();
		
		for(int i=0; i<iMatchScores; i++)
			thresholdValues.add(matchScores[i]);
				
		for(int i=0; i<iNoMatchScores; i++)
			thresholdValues.add(noMatchScores[i]);
		
		Double thresholdList[] = thresholdValues.toArray(new Double[0]);
		Arrays.sort(thresholdList);
		
		return thresholdList;
	}
	
	private void analyzeScores(){
		
		Double[] thresholdValues = getThresholdValues();
		int iThresholdCount = thresholdValues.length;
		
		for(int i=0; i<iThresholdCount; i++){
			
			double iThreshold = thresholdValues[i] - 0.005;

			int iTPCount = 0;
			int iFPCount = 0;
			
			for(int j=0; j<iMatchScores; j++){
				if(matchScores[j] > iThreshold){
					iTPCount++;
				}
			}
			
			for(int j=0; j<iNoMatchScores; j++){
				if(noMatchScores[j] > iThreshold){
					iFPCount++;
				}
			}
			
			double TPR = (double)iTPCount/iMatchScores;
			roc_Y.add(TPR);
			
			double FPR = (double)iFPCount/iNoMatchScores;
			roc_X.add(FPR);
			
			pr_X.add(TPR);
			double precision = (double) iTPCount/(iTPCount+iFPCount);
			pr_Y.add(precision);
		}
		
		roc_X.add(0.0);
		roc_Y.add(0.0);
		double dROC_AUC = getAUC(roc_X, roc_Y);
		
		pr_X.add(0.0);
		pr_Y.add(1.0);
		double dPR_AUC = getAUC(pr_X, pr_Y);
		
		System.out.println("ROC CURVE coordinates :");
		System.out.println("X-Values - " + roc_X);
		System.out.println("Y-Values - " + roc_Y);
		System.out.println("AUC of ROC CURVE = " + dROC_AUC);
		
		System.out.println("PR CURVE coordinates :");
		System.out.println("X-Values - " + pr_X);
		System.out.println("Y-Values - " + pr_Y);
		System.out.println("AUC of PR CURVE = " + dPR_AUC);
	}
	
	private double getAUC(ArrayList<Double> x_Values, ArrayList<Double> y_Values){
		
		double dAUC = 0;
		
		if(x_Values.size()!=y_Values.size()){
			return -1;
		}
		
		int iPointsCount = x_Values.size();
		for(int i=0; i<iPointsCount-1; i++){
			double temp = (double) 0.5*((double)(x_Values.get(i) - x_Values.get(i+1)))*((double)(y_Values.get(i) + y_Values.get(i+1)));
			dAUC = dAUC + temp;
		}
		return dAUC;
	}
}