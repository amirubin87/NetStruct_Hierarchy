package NetStruct_Tree;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TextConvertor {
	
	public static Set<Integer> getListOfNodes(String pathToListOfComms, int line) throws IOException{
		Set<Integer> nodes = new HashSet<Integer>();
		List<String> lines= Files.readAllLines(Paths.get(pathToListOfComms));
		String lineS = lines.get(line);
		String[] parts = lineS.split(" ");
		for (String part : parts){
			nodes.add(new Integer(part));
		}		
		return nodes;
	}
	
	public static Set<Edge> getListOfEdges(String pathToListOfEdges) throws IOException{
		Set<Edge> edges = new HashSet<Edge>();
		List<String> lines= Files.readAllLines(Paths.get(pathToListOfEdges));
		for (String line :lines){
			String[] parts = line.split("\t");			
			edges.add(new Edge(Integer.parseInt(parts[0]),Integer.parseInt(parts[1]),Double.parseDouble(parts[2])));			
		}
		return edges;
	}
	
	public static Map<String,String[]> DEPRECTAEDgetMapCode2SampleSites(String pathToMapCode2SampleSites) throws IOException{
		Map<String,String[]> map = new HashMap<String, String[]>();
		List<String> lines= Files.readAllLines(Paths.get(pathToMapCode2SampleSites));
		for (String line : lines){
			String[] parts = line.split("[,\\s]+");
			map.put(parts[0], new String[]{parts[1],parts[2]});
		}
		return map;
	}
	
	public static Map<Integer,String> getMapNode2SampleSite(String pathToMapNode2SampleSiteCode, Set<Integer> individulasToExclude) throws IOException{
		Map<Integer,String> map = new HashMap<Integer,String>();
		List<String> lines= Files.readAllLines(Paths.get(pathToMapNode2SampleSiteCode));
		Integer nodeId = 0;
		for (String line : lines){
			//String[] parts = line.split("[,\\s]+");
			if(!individulasToExclude.contains(nodeId))
				map.put(nodeId, line.trim());//parts[0]);
			nodeId = nodeId +1;
		}
		return map;
	}

	/*
	 * schema is:
	 * <sample site 1>,<sample site 2>,<sample site 3>,<sample site 4>
	 * <sample site 5>,<sample site 6>,<sample site 7>
	 * 
	 * Each line indicates a different sample area (not mandatory, you can use a single line.)
	 */
	public static String[][] getSampleSites(String pathToSampleSites) throws IOException {
		List<String> lines= Files.readAllLines(Paths.get(pathToSampleSites));
		int numOfAreas = lines.size();
		String[][] sampleSites = new String[numOfAreas][];
		for (int i = 0; i < numOfAreas ; i++) {
			String[] sampleSitesInArea = lines.get(i).trim().split("[,\\s]+");
			sampleSites[i] = sampleSitesInArea;			
		}
		return sampleSites;
	}

	public static double[] createInputs(boolean inputAsMatrix, String pathToMatrixFile, String pathToEdgesFile,
			String edgesFileName, String commsFileName, Set<Integer> individulasToExclude) throws IOException {
		if(inputAsMatrix)
			return createInputsFromMatrix(pathToMatrixFile, edgesFileName, commsFileName, individulasToExclude);		
		else{
			return createInputsFromEdgeList(pathToEdgesFile, edgesFileName, commsFileName, individulasToExclude);
		}
	}

	public static Set<Integer> getIndividulasToExclude(String pathToIndividulasToExclude) throws IOException{
		Set<Integer> ans = new HashSet<Integer>();
		if (pathToIndividulasToExclude!=null && pathToIndividulasToExclude!=""){
			String line= Files.readAllLines(Paths.get(pathToIndividulasToExclude)).get(0);
			for (String ind : line.trim().split("[,\\s]+")){
				ans.add(Integer.parseInt(ind));
			}
		}
		
		return ans;
	}
	
	private static double[] createInputsFromMatrix(String pathToMatrix, String pathToListOfEdges, String pathToListOfComms, Set<Integer> individulasToExclude) throws IOException{
		double minEdgeWeight = Double.MAX_VALUE;
		double maxEdgeWeight = Double.MIN_VALUE;
		List<String> lines= Files.readAllLines(Paths.get(pathToMatrix));
		PrintWriter writerEdges = new PrintWriter(pathToListOfEdges, "UTF-8");
		Set<Integer> nodes = new HashSet<>();
		Integer nodeFrom = 0;
		for (String line :lines){
			if (!individulasToExclude.contains(nodeFrom))
				nodes.add(nodeFrom);			
			String[] weights = line.split("[,\\s]+");
			Integer nodeTo = nodeFrom+1;
			for (String weightS : weights){
				double weight = Double.parseDouble(weightS);
				if (weight<minEdgeWeight)minEdgeWeight=weight;
				if (weight>maxEdgeWeight)maxEdgeWeight=weight;
				if (!individulasToExclude.contains(nodeFrom) && !individulasToExclude.contains(nodeTo)){
					writerEdges.println(new Edge(nodeFrom, nodeTo, Double.parseDouble(weightS)));
				}					 
				nodeTo++;
			}						
			nodeFrom++;
		}
		if (!individulasToExclude.contains(nodeFrom))
			nodes.add(nodeFrom);
		writerEdges.close();
		PrintWriter writerComms = new PrintWriter(pathToListOfComms, "UTF-8");
		for (int node : nodes){
			writerComms.print(node + " "); 
		}
		writerComms.close();
		
		return new double[]{minEdgeWeight,maxEdgeWeight};
	}
	
	private static double[] createInputsFromEdgeList(String pathToEdgesFile, String pathToListOfEdges,
			String pathToListOfComms, Set<Integer> individulasToExclude) throws IOException {
		double minEdgeWeight = Double.MAX_VALUE;
		double maxEdgeWeight = Double.MIN_VALUE;
		Set<Integer> nodes = new HashSet<>();
		List<String> edges= Files.readAllLines(Paths.get(pathToEdgesFile));
		PrintWriter writerEdges = new PrintWriter(pathToListOfEdges, "UTF-8");
		Integer nodeFrom;
		Integer nodeTo;
		double weight;
		for (String edge : edges){
			String[] edgeParts = edge.trim().split("[,\\s]+");
			nodeFrom = Integer.parseInt(edgeParts[0]);
			nodeTo = Integer.parseInt(edgeParts[1]);
			if (!individulasToExclude.contains(nodeFrom))
				nodes.add(nodeFrom);
			if (!individulasToExclude.contains(nodeTo))
				nodes.add(nodeTo);
			weight = Double.parseDouble(edgeParts[2]);
			if (weight<minEdgeWeight)minEdgeWeight=weight;
			if (weight>maxEdgeWeight)maxEdgeWeight=weight;
			if (!individulasToExclude.contains(nodeFrom) && !individulasToExclude.contains(nodeTo)){
				writerEdges.println(new Edge(nodeFrom, nodeTo, weight));
			}
		}	
		writerEdges.close();
		PrintWriter writerComms = new PrintWriter(pathToListOfComms, "UTF-8");
		for (int node : nodes){
			writerComms.print(node + " "); 
		}
		writerComms.close();
		
		return new double[]{minEdgeWeight,maxEdgeWeight};
	}
}

