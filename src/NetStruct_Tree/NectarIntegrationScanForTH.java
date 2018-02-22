package NetStruct_Tree;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

public class NectarIntegrationScanForTH {	
	// Input:
	// TODO
	// Output:
	//  - The threshold (higher than the one given) which splits the nodes to (at lest) two communities.
	// 
	// By-product: files for next step-
	// 1. list of edges with weight above the used threshold
	// 2. nectar output. 
	public static CommId findNextThreshold(
			// When !useModularityAsDefaultMetric running on each TH with WOCC takes too much time.
			// Therfor we always use modularity, and when we do find a split, we use WOCC on the same input.
			Boolean shouldUseModularity,
			String pathToWorkingDir,
			CommId parentComm,
			int entry,		
			double minThresholdToUse,
			double stepSize,
			double maxThresholdToUse,
			int minSizeOfCommToBrake,
			String betas,
			int nectarVerboseLevel
			) throws Exception{	
		
		// if we should use WOCC - we first need to scan
		boolean isScanningForTH = !shouldUseModularity;
		// if we need to scan, we first find the comm where the split occured in when using modularity.
		if(isScanningForTH) parentComm = InternalFindNextThreshold(true, true, pathToWorkingDir, parentComm, entry, minThresholdToUse, stepSize, maxThresholdToUse, minSizeOfCommToBrake,betas, nectarVerboseLevel);
		if(parentComm==null)return null;
		// we now wont scan, but simply look for the next split.
		// Note that it is possible that using modularity we found a split, but now using WOCC we wont - so we will continue with the scan using WOCC (or we find a bigger split..)
		return InternalFindNextThreshold(false, shouldUseModularity, pathToWorkingDir, parentComm, entry, minThresholdToUse, stepSize, maxThresholdToUse, minSizeOfCommToBrake,betas, nectarVerboseLevel);		
		
		
	}
	
	private static CommId InternalFindNextThreshold(
			// When !useModularityAsDefaultMetric running on each TH with WOCC takes too much time.
			// Therfor we always use modularity, and when we do find a split, we use WOCC on the same input.
			Boolean isScanningForTH,
			Boolean shouldUseModularity,
			String pathToWorkingDir,
			CommId parentComm,
			int entry,		
			double minThresholdToUse,
			double stepSize,
			double maxThresholdToUse,
			int minSizeOfCommToBrake,
			String betas,
			int nectarVerboseLevel
			) throws Exception{		
		double th = -1;
		for (th = Math.max(minThresholdToUse,parentComm.th + stepSize) ; th <= maxThresholdToUse ; th = th + stepSize){
			CommId newComms = null;
			String pathToEdgesForNectar = null;
			// If we are usign WOCC (and thus not scanning for the TH), we should start with the parentComm TH (without the stepSize)			
			if (!shouldUseModularity){
				th =th-stepSize;
				newComms = new CommId(parentComm.pathToWorkingDir, parentComm.level, parentComm.entry, -1, th,parentComm.parentComm);
				pathToEdgesForNectar = createFileForNectarWeighted(th, newComms, pathToWorkingDir, minSizeOfCommToBrake);
			}
			else{
				newComms = new CommId(parentComm.pathToWorkingDir, parentComm.level+1, entry, -1, th,parentComm);
				pathToEdgesForNectar = createFileForNectarWeighted(th, newComms, pathToWorkingDir, minSizeOfCommToBrake);
			}
			// Not enough nodes in comm.
			if (pathToEdgesForNectar == null){				
				return null;
			}			
			runNectar(pathToEdgesForNectar, pathToWorkingDir, betas, shouldUseModularity, nectarVerboseLevel); //TODO params
			String[] betasArray = betas.split(",");
			File[] nectarOutputs = new File[betasArray.length];
			int counter = 0;
			for (String beta : betasArray){
				File nectarOutput = new File (pathToWorkingDir + beta + ".txt");
				nectarOutputs[counter] = nectarOutput;
				counter++;
			}
			for (File nectarOutput : nectarOutputs){
				//TODO - if output has NO communities -we can stop looking!
				if (fileHasMoreThanNLines(nectarOutput, 1)){ 
					if(isScanningForTH){
						// We only scanned for finding a TH, so we need to delete all outputs
						deleteFilesWithException(nectarOutputs,null);
						// We will genrate this file when we run
						(new File (pathToEdgesForNectar)).delete();
						// We return the comm which the split occured in
						return newComms;
					}
					deleteFilesWithException(nectarOutputs,nectarOutput);
					String pathToOutputComms = newComms.commsFileName;
					if(!nectarOutput.renameTo(new File(pathToOutputComms))){
						System.out.println("Failed to rename NECTAR output to: " + pathToOutputComms 
								+ ". \n This is probably as the file already exists.\n"
								+ "Existing file is renamed.");
						Common.renameFile(pathToOutputComms);
						if(!nectarOutput.renameTo(new File(pathToOutputComms))){
							throw new IOException("Failed to rename NECTAR output to: " + pathToOutputComms);
						}
					}
					return newComms;
				}
				deleteFilesWithException(nectarOutputs,null);
				(new File(pathToEdgesForNectar)).delete();
			}	
		}
		// When there is no option to brake the community - return null
		return null; 
		
	}

	private static void runNectar(String pathToFileForNecter, String pathToWorkingDir, String betas, Boolean shouldUseModularity, int nectarVerboseLevel) throws Exception {
		/*String shouldUseModularityS = shouldUseModularity ? "true" : "false";
		String[]args = {
				pathToFileForNecter
				, pathToWorkingDir
				, betas
				, "0.8" // alpha
				, "5" // iteratioNumToStartMerge
				, "20" // maxIterationsToRun
				, "0" // firstPartMode (0=CC, 3=clique 3, 4=clique 4)
				, "99" // percentageOfStableNodes
				, "false" // dynamicChoose 
				, shouldUseModularityS // useModularity
				, shouldUseModularityS == "true" ? "false" : "true" // useWOCC
				, "false" //useConductance
				, "" + nectarVerboseLevel // verbose
				};
				*/
		// TODO - add "minAmountOfIterationsToRun"
		// TODO - output sorted nodes!
		//NECTAR_Beta.RunNectar_Joined.main(args);
	}

	private static String createFileForNectarWeighted(double th, CommId newComms, String pathToWorkingDir, int minSizeOfCommToBrake) throws IOException {
		// Pointer to the communities we create now (the breakdown of previousComm)		// 
		String pathToFileForNecter = newComms.edgesFileName;
		try{
		    			
			Set<Integer> nodes = TextConvertor.getListOfNodes(newComms.parentComm.commsFileName,newComms.parentComm.line);
			if (nodes.size() < minSizeOfCommToBrake){
				return null;
			}
			PrintWriter writerNectar = new PrintWriter(pathToFileForNecter, "UTF-8");
			Set<Edge> edges = TextConvertor.getListOfEdges(newComms.parentComm.edgesFileName);
			for (Edge edge : edges){
				if (edge.weight >= th & nodes.contains(edge.from) & nodes.contains(edge.to)){
					writerNectar.println(edge); 
				}
			}
			writerNectar.close();
		} catch (IOException e) {
		   throw new IOException("Problems writing file for nectar. Exception :" + e.getMessage());
		}		
		return pathToFileForNecter;
	}

	private static void deleteFilesWithException(File[] filesToDeleter, File filetoKeep) {
		for (File toDelete : filesToDeleter){
			if (!(toDelete.equals(filetoKeep))){
				toDelete.delete();
			}
		}		
	}

	private static boolean fileHasMoreThanNLines(File nectarOutput, int i) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(nectarOutput));
		int lines = 0;
		while (reader.readLine() != null) {
			lines++;
			if (lines >i){
				reader.close();
				return true;
			}
		}
		reader.close();
		return false;
	}

	public static boolean ShouldUseModularity(CommId comm, boolean useModularityAsDefaultMetric) throws Exception {
		
		// String pathToFileForNecter = comm.edgesFileName;
		// if k>50 use WOCC. So, if E > N*50/2 => WOCC
		int N = comm.GetNodes().size();
		int E = comm.GetEdges().size();		
		if (E>N*25) return useModularityAsDefaultMetric;
		return  true;//TODO NECTAR_Beta.RunNectar_Joined.ShouldUseModularity(pathToFileForNecter);	
		
	}
	

}
