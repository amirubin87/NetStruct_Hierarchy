package netStruct_Hierarchy;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

//TODO - color the help msg
//TODO - add more to the log

public class NetStruct_Hierarchy {
	static boolean debug = false;
	static boolean dummy = false;
	static String[] varFlags = {"-ss","-dy","-mod","-minb","-mino","-b","-pro","-skip","-pca","-pm","-pe","-pmn","-pss","-nvl","-w","-ls","-pts","-indtoex"};
	static String[] varValues = 
		{"0.0001",
		"false",
		"true",
		"3",
		"3",
		"1.0",
		"C:/Data/",
		"false",
		"",
		"C:/Data/Matrix.txt",
		"",
		"C:/Data/indlist.csv",
		"C:/Data/SampleSites.txt",
		"0",
		"true",
		"false",
		"false",
		null
		};
	public static void main(String[] args) throws Exception {		
		singleRun(args);
	}
	
	public static void singleRun(String[] args) throws Exception {		
		if (!debug && args.length ==0){
			System.out.println(UserHelpUtil.paramMsg);
			return;
		}
		if(!debug && (args[0].equals("-h") || args[0].equals("-H") )){
			System.out.println(UserHelpUtil.helpMsg);
			System.out.println(UserHelpUtil.paramMsg);
			return;
		}
		if(!debug && args[0].equals("-demo")){			
			dummy=true;			
		}
		if (!debug & !dummy)	populateVarValues(args);
		double stepSize =  Double.parseDouble(varValues[0]); 
		boolean dynamicChoose =  Boolean.parseBoolean(varValues[1]);
		boolean useModularityAsDefaultMetric =  Boolean.parseBoolean(varValues[2]);
		int minSizeOfCommToBrake =  Integer.parseInt(varValues[3]);
		int minSizeOfCommToOutput =  Integer.parseInt(varValues[4]);
		String beta = varValues[5];
		String pathToRootOutputDir = varValues[6];  
		boolean skipBrakeComms =  Boolean.parseBoolean(varValues[7]); 
		String pathToCommAnalysisFile =  varValues[8]; 	
		String pathToMatrixFile = varValues[9];		
		String pathToEdgesFile = varValues[10];	
		
		
		String pathToMapNode2SampleSite = varValues[11];
		String pathToSampleSites = varValues[12];		
		int nectarVerboseLevel = Integer.parseInt(varValues[13]);
		boolean useWeighted = Boolean.parseBoolean(varValues[14]);
		boolean useLeafSizesForStructure = Boolean.parseBoolean(varValues[15]);
		boolean useProportionalTreeSplitsForStructure = Boolean.parseBoolean(varValues[16]);
		String pathToIndividulasToExclude = varValues[17];
		
		if(useWeighted){
			dynamicChoose = false;
			useModularityAsDefaultMetric = true;
		}
		if(!skipBrakeComms){
			pathToRootOutputDir = pathToRootOutputDir+"Weighted_" + useWeighted + "_Dynamic_" + dynamicChoose + "_minSize_" + minSizeOfCommToBrake + "_StepSize_" + stepSize + "_Beta_" + beta +  "/";
		}
		if(dummy){			
			stepSize = 0.01;			
			pathToRootOutputDir = "./sample/DUMMYStepSize_" + stepSize + "_Beta_" + beta + "_DynamicChoose_" + dynamicChoose + "_minSizeOfCommToBrake_" + minSizeOfCommToBrake + "/";
			deleteDirectory(pathToRootOutputDir);
			pathToMatrixFile = "./sample/DUMMY_All_chrome_M.txt";
			Common.writeToFile(pathToMatrixFile, UserHelpUtil.DUMMY_All_chrome_M);
			pathToMapNode2SampleSite = "./sample/DUMMY_indlist.txt";
			Common.writeToFile(pathToMapNode2SampleSite, UserHelpUtil.DUMMY_indlist);
			pathToSampleSites = "./sample/DUMMY_SampleSites.txt";
			Common.writeToFile(pathToSampleSites, UserHelpUtil.DUMMY_SampleSites);
			minSizeOfCommToBrake=2;
			minSizeOfCommToOutput=2;
		}
		if(!(new File(pathToMatrixFile).exists()) & !(new File(pathToEdgesFile).exists())){
			throw new InputMismatchException("You must supply @pathToMatrixfile or @pathToEdgesfile - paths to files who exist on the system.");
		}
		boolean inputAsMatrix = true;
		if(!(new File(pathToMatrixFile).exists())){
			inputAsMatrix = false;
		}
		new File(pathToRootOutputDir).mkdirs();
		int firstLevel = 0;
		int firstEntry = 0;
		int firstLine = 0;				
		
		System.out.println("stepSize:                      "+stepSize);
		System.out.println("dynamicChoose:                 "+dynamicChoose);
		System.out.println("useModularityAsDefaultMetric:  "+useModularityAsDefaultMetric);
		System.out.println("minSizeOfCommToBrake:          "+minSizeOfCommToBrake);
		System.out.println("minSizeOfCommToOutput:         "+minSizeOfCommToOutput);		
		System.out.println("beta:                          "+beta);      
		System.out.println("pathToRootOutputDir:           "+pathToRootOutputDir);		
		if(inputAsMatrix){
			System.out.println("pathToMatrixFile:              "+pathToMatrixFile);
		}
		else{
			System.out.println("pathToEdgesFile:               "+pathToEdgesFile);
		}
		System.out.println("pathToMapNode2SampleSite:      "+pathToMapNode2SampleSite);		
		System.out.println("pathToSampleSites:             "+pathToSampleSites);
		System.out.println("nectarVerboseLevel:            "+nectarVerboseLevel);
		System.out.println("useWeighted:                   "+useWeighted);
		System.out.println("pathToIndividulasToExclude:    "+pathToIndividulasToExclude);
		
						
		System.out.println("");
		
		Set<Integer> individulasToExclude = TextConvertor.getIndividulasToExclude(pathToIndividulasToExclude);
		CommAnalyzer commAnalyzer = new CommAnalyzer(pathToMapNode2SampleSite,pathToSampleSites, minSizeOfCommToOutput,individulasToExclude); 
		String pathToInputFile = inputAsMatrix? pathToMatrixFile : pathToEdgesFile;
		String inputFileName = pathToInputFile.split("/")[pathToInputFile.split("/").length-1];
			
		String pathToLog = pathToRootOutputDir + "log_" + inputFileName +"_" + Common.getDate() + ".log";
		
		Common.writeToLog(pathToLog, "------------------\n",debug);
		Common.writeToLog(pathToLog, "------------------\n",debug);
		Common.writeToLog(pathToLog, "Input file" + inputFileName + "\n",debug);			
		String pathToOutputDir = pathToRootOutputDir;
		if(!skipBrakeComms){
			pathToOutputDir = pathToRootOutputDir + inputFileName +"/";
		}
		new File(pathToOutputDir).mkdirs();
		// The first comm is a single one containing all nodes.
		CommId rootComm = new CommId(pathToOutputDir,firstLevel,firstEntry, firstLine);
		double minThresholdToUse;
		double maxThresholdToUse;
		List<CommId> comms;
		if(!skipBrakeComms){
			double[] minAndMaxEdgesWeights = TextConvertor.createInputs(inputAsMatrix, pathToMatrixFile, pathToEdgesFile, rootComm.edgesFileName, rootComm.commsFileName, individulasToExclude);
			minThresholdToUse = minAndMaxEdgesWeights[0] + stepSize;
			maxThresholdToUse = minAndMaxEdgesWeights[1];				
			Common.writeToLog(pathToLog, "\t\tFirst level inputs created.\n",debug);						
			pathToCommAnalysisFile = pathToOutputDir + "1_CommAnalysis_" + inputFileName + "_dynamicChoose-" + dynamicChoose + "_useModularity-" + useModularityAsDefaultMetric + "_minSizeOfCommToBrake-" +minSizeOfCommToBrake +"_"+ stepSize + ".txt";
			List<CommId> firstLevelComms = new LinkedList<CommId> ();
			firstLevelComms.add(rootComm);
			
			// Find communities
			comms = CommBraker.brakeRec(pathToOutputDir,firstLevelComms,minThresholdToUse,stepSize,maxThresholdToUse, minSizeOfCommToBrake, beta, pathToLog, commAnalyzer, pathToCommAnalysisFile, dynamicChoose, useModularityAsDefaultMetric, -1, nectarVerboseLevel, useWeighted);			
			comms.add(rootComm);
			Common.writeToLog(pathToLog, "\t\tDone with brakeRec\n",debug);
		}
		else{
			if(pathToCommAnalysisFile == null){
				throw new RuntimeException("You must supply pathToCommAnalysisFile");				
			}
			comms = commAnalyzer.getCommsFromFiles(pathToOutputDir, pathToCommAnalysisFile, rootComm);
		}
		
		/*
		 * 					2
		 * Output leafs for NMI - commAnalyzer holds all the data needed - it was calculated in WriteStructureOutputToFile
		 * First build structure output for the unmerged tree as a preperation to the 
		 */		
		Map<CommId,Map<String,Integer>> mapCommToMapCodeToCount = commAnalyzer.commsToMapSampleSiteToCount(comms);
		commAnalyzer.WriteStructureOutputToFile(rootComm, mapCommToMapCodeToCount.keySet(), "", minSizeOfCommToOutput, useLeafSizesForStructure, useProportionalTreeSplitsForStructure);		
		Common.writeToLog(pathToLog, "\t\tDone with WriteStructureOutputToFile without output\n",debug);
		String pathToLeafsBeforeMergeNoOverlapFile = pathToOutputDir + "2_Leafs_NoOverlap.txt";
		String pathToLeafsBeforeMergeWithOverlapFile = pathToOutputDir + "2_Leafs_WithOverlap.txt";
		commAnalyzer.LeafsAsCommunities();	
		commAnalyzer.WriteLeafsNoOverlapAsCommunitiesToFile(pathToLeafsBeforeMergeNoOverlapFile);
		commAnalyzer.WriteLeafsWithOverlapAsCommunitiesToFile(pathToLeafsBeforeMergeWithOverlapFile);
		Common.writeToLog(pathToLog, "\t\tDone with WriteLeafs BeforeMerge AsCommunitiesToFile\n",debug);
						
		/*
		 * 				3
		 *  Merge the comms, using chi-squere test.
		 *  TODO - when !@skipBrakeComms, we hold in @comms all comms, including those smaller than @minCommSizrToOutput.
		 *  We dont need to check them for merge, as they wont be outputed...
		 */
		/* String pathToMergedCommAnalysisFile = pathToOutputDir + "3_MergedCommAnalysis_" + inputFileName + "_dynamicChoose-" + dynamicChoose + "_useModularity-" + useModularityAsDefaultMetric + "_minSizeOfCommToBrake-" +minSizeOfCommToBrake +"_"+ stepSize + ".txt";			
			
		Common.writeToLog(pathToLog, "\t\tDone with CommsToMapCodeToCount\n",debug);
		Map<CommId,Map<String,Integer>> mapMergedCommToMapCodeToCount = MergeComms.MergeCommsByPVal(rootComm, mapCommToMapCodeToCount, 0.05);
		Common.writeToLog(pathToLog, "\t\tDone with MergeCommsByPVal.\n",debug);
		commAnalyzer.WriteCommAnalysisToFile(mapMergedCommToMapCodeToCount, pathToMergedCommAnalysisFile);
		Common.writeToLog(pathToLog, "\t\tDone with WriteCommAnalysisToFile. Output: " + pathToMergedCommAnalysisFile +"\n",debug);
		*/
		// Calculate F1 score between the sample sites.
		
		/*Map<String, SampleSite> sampleSites = commAnalyzer.InitSampleSites();
		Common.writeToLog(pathToLog, "\t\tDone with InitSampleSites\n",debug);
		commAnalyzer.SetPathFromRootToAll(sampleSites, mapMergedCommToMapCodeToCount.keySet());
		Common.writeToLog(pathToLog, "\t\tDone with SetPathFromRootToAll\n",debug);
		Map<String,Map<String, Double>> f1ScoreMatrix = commAnalyzer.CalcF1ScoresMatrix(sampleSites);
		String pathToF1ScoreMatrixFile = pathToOutputDir + "4_F1ScoreMatrix_" + inputFileName + "_dynamicChoose-" + dynamicChoose + "_useModularity-" + useModularityAsDefaultMetric + "_minSizeOfCommToBrake-" +minSizeOfCommToBrake +"_"+ stepSize + ".txt";
		commAnalyzer.WriteF1ScoreMatrixToFile(f1ScoreMatrix, pathToF1ScoreMatrixFile);
		Common.writeToLog(pathToLog, "\t\tDone with WriteF1ScoreMatrixToFile: " + pathToF1ScoreMatrixFile +"\n",debug); 
		
		// Build structure output
		String pathToStructureFile = pathToOutputDir + "5_Structure_LeafSizes-" + useLeafSizesForStructure + "_proportional-" + useProportionalTreeSplitsForStructure +"_"+ inputFileName + "_dynamic-" + dynamicChoose + "_useModularity-" + useModularityAsDefaultMetric + "_minSizeOfComm-" +minSizeOfCommToBrake +"_"+ stepSize + ".txt";
		commAnalyzer.WriteStructureOutputToFile(rootComm, mapMergedCommToMapCodeToCount.keySet(), pathToStructureFile, minSizeOfCommToOutput, useLeafSizesForStructure, useProportionalTreeSplitsForStructure);
		Common.writeToLog(pathToLog, "\t\tDone with WriteStructureOutputToFile: " + pathToStructureFile +"\n",debug);
		*/
		
		// Output leafs for NMI - commAnalyzer holds all the data needed - it was calculated in WriteStructureOutputToFile  
		/*String pathToLeafsNoOverlapFile = pathToOutputDir + "6_Leafs_NoOverlap.txt";
		String pathToLeafsWithOverlapFile = pathToOutputDir + "6_Leafs_WithOverlap.txt";
		commAnalyzer.LeafsAsCommunities();		
		commAnalyzer.WriteLeafsNoOverlapAsCommunitiesToFile(pathToLeafsNoOverlapFile);
		commAnalyzer.WriteLeafsWithOverlapAsCommunitiesToFile(pathToLeafsWithOverlapFile);
		Common.writeToLog(pathToLog, "\t\tDone with WriteLeaf AfterMerge AsCommunitiesToFile\n",debug);
		*/
				
	}

	private static void deleteDirectory(String dir) throws IOException {
		Path directory = Paths.get(dir);
		if (Files.exists(directory)){
		Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
		   @Override
		   public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		       Files.delete(file);
		       return FileVisitResult.CONTINUE;
		   }

		   @Override
		   public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		       Files.delete(dir);
		       return FileVisitResult.CONTINUE;
		   }
		});
		}		
	}

	private static void populateVarValues(String[] args) {
		int length = args.length;
		for (int i = 0; i < length; i=i+2) {
			String flag = args[i].toLowerCase();
			if(i+1 > length) throw new InputMismatchException("Bad input for NetStruct_Tree. The flag:"+flag + " is missing a value,");
			boolean foundFlag=false;
			for (int j = 0; j < varFlags.length; j++) {
				if (varFlags[j].equals(flag)) {
					varValues[j] = args[i+1];
					foundFlag=true;
					break;
				}				
			}
			if(!foundFlag) throw new InputMismatchException("Bad input for NetStruct_Tree. The flag:"+flag + " is not found. Use -h to see available flags.");
		}		
	}
	
	
}
