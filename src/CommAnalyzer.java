import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.management.RuntimeErrorException;

public class CommAnalyzer {	
	
	public Map<Integer,String> mapNode2SampleSite;
	public int minSizeOfCommToOutput;
	public Map<Integer,CommId> mapNode2CommID;	
	public Map<Integer,Set<Integer>> mapLeaf2NodesNoOverlap;
	public Map<Integer,Set<Integer>> mapLeaf2NodesWithOverlap;
	public Map<Integer,Set<Integer>> mapNode2LeafsInSubTree;
	public Set<Integer> droppedIndividuals;
	public Set<Integer> individulasToExclude;
	
	public static String[][] samplesSites;
	private static Set<String> samplesSitesSet;
	public static int amountOfSampleSites;
	
	public CommAnalyzer(String pathToMapNode2SampleSite, String pathToSampleSites, int minSizeOfCommToOutput, Set<Integer> individulasToExclude) throws IOException{		
		this.individulasToExclude = individulasToExclude;
		InitSamplesSitesList(pathToSampleSites);
		mapNode2SampleSite = TextConvertor.getMapNode2SampleSite(pathToMapNode2SampleSite, individulasToExclude);
		VerifySampleSitesList();
		this.minSizeOfCommToOutput = minSizeOfCommToOutput;
		this.mapNode2CommID = new HashMap<>();		
	}
		
	
	private void VerifySampleSitesList() {
		for (Entry<Integer, String> entry : mapNode2SampleSite.entrySet()){
			if (!samplesSitesSet.contains(entry.getValue())){
				throw new InputMismatchException(" Not all sample sites are listed in the sampleSitesFile. SampleSite " + entry.getValue() 
				+ " is missing. It is assigned to individual id " + entry.getKey() );
			}
		}		
	}


	private void InitSamplesSitesList(String pathToSampleSites) throws IOException {	
		samplesSitesSet = new HashSet<>();
		samplesSites = TextConvertor.getSampleSites(pathToSampleSites);
		amountOfSampleSites = 0;
		for (String[] sampleSiteArea : samplesSites){			
			for(String sampleSite : sampleSiteArea){
				amountOfSampleSites++;
				samplesSitesSet.add(sampleSite);
			}			
		}
		if(amountOfSampleSites != samplesSitesSet.size()){
			throw new InputMismatchException(" Sample sites must all be unique names. Please verify - " + pathToSampleSites );		
		}
	}




	public Map<String,Integer> perSampleSiteCounts(CommId commId) throws IOException{
		Map<String,Integer> ans = new HashMap<String,Integer>();
		for(String[] sampleSitesArea : samplesSites){
			for(String sampleSite : sampleSitesArea){
				ans.put(sampleSite, 0);
			}
		}
		Set<Integer> nodes = commId.GetNodes();
		for (Integer node:nodes){
			String sampleSite = mapNode2SampleSite.get(node);
			ans.put(sampleSite, ans.get(sampleSite)+1);			
		}
		return ans;
	}
	
	public void WriteCommAnalysisToFile(List<CommId> comms, String pathS) throws IOException {
		Map<CommId,Map<String,Integer>> mapCommToMapSampleSiteToCount = new HashMap<CommId,Map<String,Integer>>();
		for (CommId commId : comms){
			Map<String,Integer> mapSampleSiteToCount = perSampleSiteCounts(commId);
			mapCommToMapSampleSiteToCount.put(commId, mapSampleSiteToCount);
		}
		WriteCommAnalysisToFile(mapCommToMapSampleSiteToCount, pathS);
	}
	
	public void WriteCommAnalysisToFile(Map<CommId,Map<String,Integer>> mapCommToMapSampleSiteToCount, String pathS) throws IOException {
		Path path = Paths.get(pathS);	    	    
		
		int prevLevel=-1;
		List<CommId> comms = SortCommsByLevelAndEntry(mapCommToMapSampleSiteToCount.keySet());
		for (CommId commId : comms){
			Map<String,Integer> mapSampleSiteToCount = mapCommToMapSampleSiteToCount.get(commId);
			String commLine ="";
			int commSize = SumAmountOfNodes(mapSampleSiteToCount);			
			if(commSize>=minSizeOfCommToOutput){
				int commLevel = commId.level;
				if (commLevel>prevLevel){
					Files.write(path, Arrays.asList(" ----------- LEVEL " + commLevel + " ----------- "), StandardCharsets.UTF_8, Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);					
					prevLevel = commLevel;
				}				
				commLine = commLine + "Size_" + String.format("%04d", commSize) + "_" + commId.toString() + "\t";			
				
				for(String[] sampleSitesArea : samplesSites){
					commLine = commLine + "|";					
					for(String sampleSite : sampleSitesArea){
						commLine = commLine + sampleSite + ":" + mapSampleSiteToCount.get(sampleSite) + "\t";					
					}
				}
				Files.write(path, Arrays.asList(commLine), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
			}			
		}
	}

	private int SumAmountOfNodes(Map<String, Integer> map) {
		int sum = 0;
		for (int f : map.values()) {
		    sum += f;
		}
		return sum;
	}

	private List<CommId> SortCommsByLevelAndEntry(Set<CommId> keySet) {
		List<CommId> ans = new ArrayList<CommId>(keySet);	
		Collections.sort(ans, new CommIdComperator());
		return ans;
	}

	public Map<CommId, Map<String, Integer>> commsToMapSampleSiteToCount(List<CommId> comms) throws IOException {
		Map<CommId,Map<String,Integer>> ans = new HashMap<CommId,Map<String,Integer>>();
		for(CommId comm : comms){
			ans.put(comm, perSampleSiteCounts(comm));
		}
		return ans;
	}
	
	private static class CommIdComperator implements Comparator<CommId>{

		@Override
		public int compare(CommId o1, CommId o2) {
			if (o1.level != o2.level) return o1.level - o2.level;			
			if (o1.entry != o2.entry) return o1.entry - o2.entry;
			if (o1.line != o2.line) return o1.line - o2.line;			
			return 0;
		}
		
	}
	
	
	/*
	 *  For f1 score
	 * 
	 */
	public Map<String, SampleSite> InitSampleSites() {		
		Map<String, SampleSite> sampleSites = new HashMap<>();
		for(String[] sampleSitesInArea : samplesSites){
			for(String sampleSite : sampleSitesInArea){
				sampleSites.put(sampleSite, new SampleSite(sampleSite));
			}
		}
		
		for (Entry<Integer, String> IdSampleSite : mapNode2SampleSite.entrySet()){
			SampleSite sampleSite = sampleSites.get(IdSampleSite.getValue());
			Integer id = IdSampleSite.getKey();
			sampleSite.members.put(id, new Person(id, sampleSite));
		}		
		return sampleSites;
	}
	
	public void SetPathFromRootToAll(Map<String, SampleSite> sampleSites, Set<CommId> comms) throws IOException {		
		List<CommId> sortedComms = SortCommsByLevelAndEntry(comms);	
		// We use this to verify there is no bug.
		int previousLevel = -1;
		for ( CommId comm : sortedComms){
			if (comm.level < previousLevel){
				throw new RuntimeException("sortedComms is not sorted!");
			}
			UpdatePathFromRoot(comm, sampleSites);			
		}
	}

	private void UpdatePathFromRoot(CommId comm, Map<String, SampleSite> sampleSites) throws IOException {
		String levelEntry = " " + comm.level + "-" + comm.entry + "-" + comm.line; 
		for (Integer personId : comm.GetNodes()){
			String sampleSiteS = mapNode2SampleSite.get((Integer)personId);
			SampleSite sampleSite = sampleSites.get(sampleSiteS);
			String newPathFromRoot = sampleSite.members.get(personId).pathFromRoot + levelEntry;
			sampleSite.members.get(personId).pathFromRoot = newPathFromRoot;
		}
		
	}

	public void WriteF1ScoreMatrixToFile(Map<String, Map<String, Double>> f1ScoreMatrix,
		String pathToF1ScoreMatrixFile) throws IOException {
		Path path = Paths.get(pathToF1ScoreMatrixFile);
		for(String[] sampleSitesInArea1 : samplesSites){
			for(String sampleSite1 : sampleSitesInArea1){		
				Map<String, Double> map1 = f1ScoreMatrix.get(sampleSite1);
				for(String[] sampleSitesInArea2 : samplesSites){
					for(String sampleSite2 : sampleSitesInArea2){	
						if (sampleSite1.compareTo(sampleSite2)<0){	
							String commLine =sampleSite1 +" " + sampleSite2 + " " + map1.get(sampleSite2);
							Files.write(path, Arrays.asList(commLine), StandardCharsets.UTF_8, Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
						}					
					}
				}				
			}
		}					
	}
	

	public Map<String, Map<String, Double>> CalcF1ScoresMatrix(Map<String, SampleSite> sampleSites) {
		Map<String, Map<String, Double>> f1ScoresMatrix = InitF1ScoresMatrix();		
		for(String sampleSiteS1 : sampleSites.keySet()){
			for(String sampleSiteS2 : sampleSites.keySet()){
				if (sampleSiteS1.compareTo(sampleSiteS2)<0){
					double f1Score = sampleSites.get(sampleSiteS1).calcF1Score(sampleSites.get(sampleSiteS2));
					f1ScoresMatrix.get(sampleSiteS1).put(sampleSiteS2, f1Score);
				}
			}			
		}
		return f1ScoresMatrix;
	}

	private Map<String, Map<String, Double>> InitF1ScoresMatrix() {
		Map<String, Map<String, Double>> f1ScoresMatrix = new HashMap<>();
		for(String[] sampleSitesInArea : samplesSites){
			for(String sampleSite : sampleSitesInArea){
				f1ScoresMatrix.put(sampleSite, GenerateSampleSitesMap());
			}
		}
		return f1ScoresMatrix;
	}

	private Map<String, Double> GenerateSampleSitesMap() {
		Map<String, Double> sampleSitesMap = new HashMap<>();
		for(String[] sampleSitesInArea : samplesSites){
			for(String sampleSite : sampleSitesInArea){
				sampleSitesMap.put(sampleSite, 0.0);
			}
		}
		return sampleSitesMap;
	}
	
	
	/*
	 * 
	 * For structure output
	 * 
	 */
	private void SetCommIdsForNodes(Set<CommId> comms) throws IOException{
		List<CommId> sortedComms = SortCommsByLevelAndEntry(comms);		
		for (CommId comm:sortedComms){
			if(comm.Size()>=minSizeOfCommToOutput){
				SetSingleCommIdForNodes(comm);			
			}	
		}
	}
	
	private void SetSingleCommIdForNodes(CommId commId) throws IOException{
		Set<Integer> nodes = commId.GetNodes();
		for (Integer node:nodes){
			mapNode2CommID.put(node, commId);			
		}
	}
	
	public void WriteStructureOutputToFile(CommId rootComm, Set<CommId> comms, String pathToStructureFile, int minSizeOfCommToOutput, boolean useLeafSizesForStructure, boolean useProportionalTreeSplitsForStructure) throws IOException {
		Path path = pathToStructureFile=="" ? null : Paths.get(pathToStructureFile);
		rootComm.resetLeafMap();
		rootComm.PopulateLeafMap(minSizeOfCommToOutput,useLeafSizesForStructure,useProportionalTreeSplitsForStructure);
		SetLeafsVectorAsString(rootComm,comms);		
		SetCommIdsForNodes(comms);	
		if(path!=null){
			for ( Entry<Integer, CommId> nodeCommId: mapNode2CommID.entrySet()){
				Integer node = nodeCommId.getKey();				
				CommId commId = nodeCommId.getValue();
				String sampleSite = mapNode2SampleSite.get((Integer)node);
				String leafsVectorAsString = commId.leafsVectorAsString;
				String line = sampleSite + " " + node + " " + leafsVectorAsString;
				
				Files.write(path, Arrays.asList(line), StandardCharsets.UTF_8, Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
			}
		}
	}

	// The root holds all leafs, enabiling us to discover the amount of leafs, and the communities in them.
	// We use it to generate the LeafsVector per comm in comms.
	private void SetLeafsVectorAsString(CommId rootComm, Set<CommId> comms) {
		Set<CommId> allLeafs = rootComm.leafs.keySet();
		List<CommId> sortedAllLeafs = SortCommsByLevelAndEntry(allLeafs);
		for (CommId  comm : comms){
			comm.SetLeafsVectorAsString(sortedAllLeafs);
		}
		
	}

	public List<CommId> getCommsFromFiles(String pathToWorkingDir, String pathToCommAnalysisFile, CommId rootComm) throws IOException {
		List<CommId> comms= new ArrayList<>();
		comms.add(rootComm);
		List<String> lines = Files.readAllLines(Paths.get(pathToCommAnalysisFile));
		for (String lineInFile : lines){
			if(!lineInFile.contains("---")){
				String[] parts = lineInFile.split("_");				
				int level = Integer.parseInt(parts[3]);
				if (level==0){continue;} //level 0 has no parents..
				int entry = Integer.parseInt(parts[5]);
				int line = Integer.parseInt(parts[7]);
				int parentLevel = Integer.parseInt(parts[9]);
				int parentEntry = Integer.parseInt(parts[11]);
				int parentLine = Integer.parseInt(parts[13]);				
				double th = FindLongThreshold(pathToWorkingDir, level,entry,parentLevel,parentEntry,parentLine);
				CommId parentComm = FindComm(comms,parentLevel,parentEntry,parentLine);				
				CommId comm = new CommId(pathToWorkingDir, level, entry, line, th , parentComm);
				comms.add(comm);
				parentComm.childComms.add(comm);
			}
		}
		return comms;
	}

	public static double FindLongThreshold(String pathToWorkingDir, int level, int entry, int parentLevel, int parentEntry,
			int parentLine) {
			for (File fileEntry : new File(pathToWorkingDir).listFiles()) {	        
				if(fileEntry.getName().contains("Level_" +level +"_Entry_" +entry  +"_ParentLevel_" +parentLevel +"_ParentEntry_" +parentEntry +"_ParentLine_" +parentLine)){	  
				//*** if(fileEntry.getName().contains("Level_" +level +"_Entry_" +entry +"_ParentEntry_" +parentEntry +"_ParentLine_" +parentLine)){
					String[] parts = fileEntry.getName().split("_");
					return Double.parseDouble(parts[parts.length-2]);
				}
			}
		return 0;
	}

	private CommId FindComm(List<CommId> comms, int level, int entry, int line) {
		for (CommId comm : comms){
			if (comm.level == level && comm.entry == entry && comm.line == line){
				return comm;
			}
		}
		throw new RuntimeException("Comm not found! Level-" + level + " Entry-" + entry + " Line-" + line);
	}

	
	/*
	 * 
	 * Leafs as communites for NMI
	 * 
	 */
	public void LeafsAsCommunities() throws IOException {		
		mapNode2LeafsInSubTree = new HashMap<Integer, Set<Integer>>();
		for ( Entry<Integer, CommId> nodeCommId: mapNode2CommID.entrySet()){			
			Integer node = nodeCommId.getKey();
			mapNode2LeafsInSubTree.put(node, new HashSet<Integer>());
			CommId commId = nodeCommId.getValue();
			String leafs = commId.leafsVectorAsString;
			String[] leafsStringA = leafs.split(",");
			// First we assign the leafs for each node.
			Set<Integer> leafsInSubTree = mapNode2LeafsInSubTree.get(node);			
			for(Integer i = 0 ; i < leafsStringA.length ; i++){
				if(!leafsStringA[i].equals("0.0")){
					leafsInSubTree.add(i);
				}
			}
		}
		// now, only for nodes with a single comm, we place them in the community
		mapLeaf2NodesNoOverlap = new HashMap<Integer, Set<Integer>>();
		mapLeaf2NodesWithOverlap = new HashMap<Integer, Set<Integer>>();
		droppedIndividuals = new HashSet<Integer>();
		for (Integer individual : mapNode2LeafsInSubTree.keySet()){
			Set<Integer> leafsInSubTree = mapNode2LeafsInSubTree.get(individual);
			if(mapNode2CommID.get(individual).IsLeaf(minSizeOfCommToOutput)){
				Integer leafIndex = (Integer)leafsInSubTree.toArray()[0];
				if(mapLeaf2NodesNoOverlap.get(leafIndex) == null){
					mapLeaf2NodesNoOverlap.put(leafIndex, new HashSet<>());					
				}
				if(mapLeaf2NodesWithOverlap.get(leafIndex) == null){
					mapLeaf2NodesWithOverlap.put(leafIndex, new HashSet<>());
				}
				mapLeaf2NodesNoOverlap.get(leafIndex).add(individual);
				mapLeaf2NodesWithOverlap.get(leafIndex).add(individual);
			}
			else{
				droppedIndividuals.add(individual);
				for (Integer leafIndex : mapNode2LeafsInSubTree.get(individual)){
					if(mapLeaf2NodesWithOverlap.get(leafIndex) == null){						
						mapLeaf2NodesWithOverlap.put(leafIndex, new HashSet<>());
					}
					mapLeaf2NodesWithOverlap.get(leafIndex).add(individual);
				}
			}
		}
	}


	public void WriteLeafsNoOverlapAsCommunitiesToFile(String pathToLeafsFile) throws IOException {		
		Path path = Paths.get(pathToLeafsFile);
		for (Set<Integer> individuals : mapLeaf2NodesNoOverlap.values()){
			String line = "";
			for ( Integer ind: individuals){
				line = line + ind + " ";
			}
			line = line.substring(0,line.length()-1);
			Files.write(path, Arrays.asList(line), StandardCharsets.UTF_8, Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
		}
		for (Integer ind : droppedIndividuals){
			String line = ind +"";
			Files.write(path, Arrays.asList(line), StandardCharsets.UTF_8, Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
		}
	}
	
	public void WriteLeafsWithOverlapAsCommunitiesToFile(String pathToLeafsFile) throws IOException {		
		Path path = Paths.get(pathToLeafsFile);
		for (Set<Integer> individuals : mapLeaf2NodesWithOverlap.values()){
			String line = "";
			for ( Integer ind: individuals){
				line = line + ind + " ";
			}
			line = line.substring(0,line.length()-1);
			Files.write(path, Arrays.asList(line), StandardCharsets.UTF_8, Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
		}		
	}
}
