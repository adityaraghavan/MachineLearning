import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;


public class KMeansClustering {

	public static void main(String[] args) {
		
		KMeansClustering obj = new KMeansClustering();
		try {	
		obj.process();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ArrayList<Double> durationList = null;
	private ArrayList<Integer> waitTimeList = null;
	private HashMap<Integer, HashMap<String, Double>> centroidsMap = null;
	private HashMap<Integer, Integer> dataMapping = null;
	private boolean iterationCheck = true;
	private int iterationCount = 0;
	private int process() throws Exception{
		
		int iCheck = 0;
		iCheck = initializations();
		if(iCheck != 0){
			return -1;
		}
		
		iCheck = getData();
		if(iCheck != 0){
			return -1;
		}
		
		clusterData(3);
		return 0;
	}
	
	private int initializations(){
		
		durationList = new ArrayList<Double>();
		waitTimeList = new ArrayList<Integer>();
		centroidsMap = new HashMap<Integer, HashMap<String, Double>>();
		dataMapping = new HashMap<Integer, Integer>();
		return 0;
	}
	
	private int getData()throws Exception{
		
		BufferedReader br = new BufferedReader(new FileReader("C:/Users/Aditya Raghavan/Desktop/Test.txt"));
		String strLine = br.readLine();
		while(strLine != null){
							
			String[] strData = strLine.split("\t");
			if(null == strData || strData.length != 3){
				br.close();
				return -1;
			}
			
			durationList.add(Double.parseDouble(strData[1]));
			waitTimeList.add(Integer.parseInt(strData[2]));
			strLine = br.readLine();
		}
		
		br.close();
		return 0;
	}
	
	private void clusterData(int k){
		
		ArrayList<Integer> dataCaseList = new ArrayList<Integer>();
		
		int iDataSize = durationList.size();
		for(int i=0; i<iDataSize; i++){
			dataCaseList.add(i);
		}
		
		for(int l=1; l<=k; l++){
			calculateInitialCentroids(dataCaseList, k, l);
		}
		System.out.println(centroidsMap);
		calculateDistances(centroidsMap, dataCaseList);
		System.out.println(dataMapping);
		
		while(iterationCheck && iterationCount<5){
			for(int j=1; j<=k; j++){
				ArrayList<Integer> clusterCaseList = new ArrayList<Integer>();
				for(int i=0; i<iDataSize; i++){
					int iCaseMapping = dataMapping.get(i);
					if(iCaseMapping == j){
						clusterCaseList.add(i);
					}
				}
				calculateCentroids(clusterCaseList, j);
			}
			System.out.println(centroidsMap);
			calculateDistances(centroidsMap, dataCaseList);
			System.out.println(dataMapping);
			iterationCount++;
		}
	}
	
	private void calculateDistances(HashMap<Integer, HashMap<String, Double>> centroidsMap, ArrayList<Integer> dataCaseList){
		
		int iDataSize = dataCaseList.size();
		
		for(int i=0; i<iDataSize; i++){
			
			int iCase = dataCaseList.get(i);
			double tempDuration = durationList.get(iCase);
			double tempTimeWait = waitTimeList.get(iCase);
		
			Set<Integer> centroidKeys = centroidsMap.keySet();
			Iterator<Integer> itr = centroidKeys.iterator();
			double minDistance = Double.MAX_VALUE;
			int clusterId = -1;
	
			while(itr.hasNext()){
				
				int listKey = itr.next();
				HashMap<String, Double> centroidMap = centroidsMap.get(listKey);
				double duration = centroidMap.get("x");
				double waitTime = centroidMap.get("y");
				double tempDistance = Math.sqrt(Math.pow((tempDuration - duration), 2) + Math.pow((tempTimeWait - waitTime), 2));
				if(minDistance > tempDistance){
					minDistance = tempDistance;
					clusterId = listKey;
				}
			}	
			
			dataMapping.put(iCase, clusterId);
		}
	}
	
	private void calculateCentroids(ArrayList<Integer> dataCaseList, int iClusterId){
		
		int iDataSize = dataCaseList.size();
		double durationSum = 0;
		double timeWaitSum = 0;
		for(int i=0; i<iDataSize; i++){
			int iCase = dataCaseList.get(i);
			durationSum = durationSum + durationList.get(iCase);
			timeWaitSum = timeWaitSum + waitTimeList.get(iCase);			
		}
		double centroidX = durationSum/iDataSize;
		double centroidY = timeWaitSum/iDataSize;
		
		HashMap<String, Double> centroidCoordinates = centroidsMap.get(iClusterId);
		
		if(null == centroidCoordinates){
			centroidCoordinates = new HashMap<String, Double>();
		}else{
			if((centroidCoordinates.get("x") == centroidX) && (centroidCoordinates.get("y") == centroidY))
				iterationCheck = false;
		}
		
		centroidCoordinates.put("x", centroidX);
		centroidCoordinates.put("y", centroidY);
		centroidsMap.put(iClusterId, centroidCoordinates);
	}
	
	private void calculateInitialCentroids(ArrayList<Integer> dataCaseList, int k, int l){
		
		Random r = new Random();
		int n = r.nextInt(20);
		double centroidX = durationList.get(n);
		double centroidY = waitTimeList.get(n);
		HashMap<String, Double> centroidCoordinates = centroidsMap.get(l);
		
		if(null == centroidCoordinates){
			centroidCoordinates = new HashMap<String, Double>();
		}else{
			if((centroidCoordinates.get("x") == centroidX) && (centroidCoordinates.get("y") == centroidY))
				iterationCheck = false;
		}
		
		centroidCoordinates.put("x", centroidX);
		centroidCoordinates.put("y", centroidY);
		centroidsMap.put(l, centroidCoordinates);
	}
}