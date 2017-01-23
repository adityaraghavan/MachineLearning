
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;
public class SVM {

	public static void main(String[] args) {
		SVM s = new SVM();
		try {
			s.process();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	ArrayList<Double> bx = new ArrayList<Double>();
	ArrayList<Double> by = new ArrayList<Double>();
	ArrayList<Double> bz = new ArrayList<Double>();

	ArrayList<Double> mx = new ArrayList<Double>();
	ArrayList<Double> my = new ArrayList<Double>();
	ArrayList<Double> mz = new ArrayList<Double>();

	private int[] yTrain = new int[40];
	private double[][] xTrain = new double[40][3];
	private double[][] xTest = new double[40][3];

	public void process() throws Exception{
		
		getXYZ();
		svm_model model = training();
		double result[] = scoring(model);
		for(int k=0;k<40;k++){
			System.out.println(result[k]);	
		}
		
		
	}

	private svm_model training() throws Exception{

		svm_problem prob = new svm_problem();
		prob.l = 40;
		prob.x = new svm_node[40][3];     
		prob.y = new double[40];

		svm_parameter param = new svm_parameter();
		param.kernel_type = svm_parameter.RBF;
		param.C = 0;
		//param.degree = 2;
		param.svm_type = svm_parameter.C_SVC;
			/*
		param.coef0 = 1;
		param.probability = 0;
		param.gamma = 1;
		param.shrinking = 0;
		param.nr_weight = 0;
		*/param.weight_label = null;
		param.weight = null;
		
		for (int i = 0; i < 40; i++){            
			prob.x[i] = new svm_node[3];
			for (int j = 0; j < 3; j++){
				svm_node node = new svm_node();
				node.index = j+1;
				node.value = xTrain[i][j];
				prob.x[i][j] = node;
			}           
			prob.y[i] = yTrain[i];
		}               

		svm_model model = svm.svm_train(prob, param);

		return model;

	}

	private double[] scoring(svm_model model){

		double[] result = new double[40];
		for(int i = 0; i < 40; i++){

			svm_node[] nodes = new svm_node[3];
			for (int j = 0; j < 3; j++)
			{
				svm_node node = new svm_node();
				node.value = xTest[i][j];
				node.index = j+1;
				nodes[j] = node;
			}

			int totalClasses = 2;       
			int[] labels = new int[totalClasses];
			svm.svm_get_labels(model,labels);

			double[] prob_estimates = new double[totalClasses];
			result[i] = svm.svm_predict_probability(model, nodes, prob_estimates);
		}
		return result;
	}
	
	public void getXYZ() throws Exception{

		BufferedReader br = new BufferedReader(new FileReader("//FileName"));
		BufferedReader br1 = new BufferedReader(new FileReader("//FileName"));


		String line = br.readLine();
		line = br.readLine();

		String line1 = br.readLine();
		line1 = br1.readLine();

		for(int i =0; i<20 || null!=line; i++){
			String temp[] = line.split("\t");
			if(temp.length != 4){
				break;
			}
			//yTrain[i] = Integer.parseInt(temp[0]);
			yTrain[i] = 1;
			xTrain[i][0] = Double.parseDouble(temp[1]);
			xTrain[i][1] = Double.parseDouble(temp[2]);
			xTrain[i][2] = Double.parseDouble(temp[3]);

			String temp1[] = line1.split("\t");
			if(temp1.length != 4){
				break;
			}
			//yTrain[i+20] = Integer.parseInt(temp1[0]);
			yTrain[i+20] = -1;
			xTrain[i+20][0] = Double.parseDouble(temp1[1]);
			xTrain[i+20][1] = Double.parseDouble(temp1[2]);
			xTrain[i+20][2] = Double.parseDouble(temp1[3]);
		}

		for(int i =20; i<40 || null!=line; i++){
			String temp[] = line.split("\t");
			if(temp.length != 4){
				break;
			}
			xTest[i][0] = Double.parseDouble(temp[1]);
			xTest[i][1] = Double.parseDouble(temp[2]);
			xTest[i][2] = Double.parseDouble(temp[3]);

			String temp1[] = line1.split("\t");
			if(temp1.length != 4){
				break;
			}
			xTest[i+20][0] = Double.parseDouble(temp1[1]);
			xTest[i+20][1] = Double.parseDouble(temp1[2]);
			xTest[i+20][2] = Double.parseDouble(temp1[3]);
		}

		br.close();
		br1.close();

	}
}