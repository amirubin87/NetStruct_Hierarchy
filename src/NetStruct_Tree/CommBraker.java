package NetStruct_Tree;
import java.util.LinkedList;
import java.util.List;

public class CommBraker {

	public static List<CommId> brakeRec(String pathToWorkingDir, List<CommId> commsInCurrentLevel, double minThresholdToUse, double stepSize, double maxThresholdToUse, int minSizeOfCommToBrake, String beta, String pathToLog,CommAnalyzer commAnalyzer, String pathToCommAnalysisFile, boolean dynamicChoose, boolean useModularityAsDefaultMetric, int maxLevels, int nectarVerboseLevel, boolean useWeighted) throws Exception{
		
		List<CommId> ans = new LinkedList<CommId>();
		if (commsInCurrentLevel.size() == 0 || maxLevels == 0)
			return ans;	
		Common.writeToLog(pathToLog, "Level " + commsInCurrentLevel.get(0).level + " has " + commsInCurrentLevel.size() + " comms." +"\n", true);
		
		List<CommId> commsInNextLevel = new LinkedList<CommId>();
		int entry  = 0;
		for (CommId comm : commsInCurrentLevel){		
			if (comm!=null){	
				boolean shouldUseModularity = dynamicChoose ?  NectarIntegration.ShouldUseModularity(comm, useModularityAsDefaultMetric) : useModularityAsDefaultMetric;
				if (shouldUseModularity){
					comm.metricUsed = "Modularity";
				}
				else{
					comm.metricUsed = "WOCC";
				}
				List<CommId> commsCreated =  brakeSingleComm(shouldUseModularity, pathToWorkingDir,comm, entry, minThresholdToUse, stepSize, maxThresholdToUse, minSizeOfCommToBrake, beta, nectarVerboseLevel, useWeighted);				
				comm.childComms.addAll(commsCreated);
				commsInNextLevel.addAll(commsCreated);
				ans.addAll(commsCreated);
				entry++;
			}
		}
		// WAS commAnalyzer.WriteCommAnalysisToFile(commsInNextLevel, pathToCommAnalysisFile);
		commAnalyzer.WriteCommAnalysisToFile(commsInCurrentLevel, pathToCommAnalysisFile);
		// Run on the next level
		ans.addAll(brakeRec(pathToWorkingDir, commsInNextLevel, minThresholdToUse, stepSize, maxThresholdToUse, minSizeOfCommToBrake, beta, pathToLog,commAnalyzer, pathToCommAnalysisFile, dynamicChoose, useModularityAsDefaultMetric, maxLevels-1,nectarVerboseLevel,useWeighted));
		return ans;
	}
	
	// takes the parentComm and brakes it to comms.
	// Return a list of comms which are the output og the split.
	private static List<CommId> brakeSingleComm(Boolean shouldUseModularity, String pathToWorkingDir, CommId parentComm, int entry, double minThresholdToUse, double stepSize, double maxThresholdToUse, int minSizeOfCommToBrake, String beta, int nectarVerboseLevel, boolean useWeighted) throws Exception {
		CommId commGenerated= NectarIntegration.findNextThreshold(
				shouldUseModularity,
				pathToWorkingDir,
				parentComm,
				entry,
				minThresholdToUse,
				stepSize, 
				maxThresholdToUse,
				minSizeOfCommToBrake,
				beta,
				nectarVerboseLevel,
				useWeighted
				);
		List<CommId> CommsToBrake = new LinkedList<CommId>();
		if (commGenerated != null){
			int numOfLines = Common.numOfLinesInFile(commGenerated.commsFileName);
			for (int line = 0 ; line < numOfLines ; line++){
				CommsToBrake.add(new CommId(commGenerated.pathToWorkingDir, commGenerated.level, commGenerated.entry, line, commGenerated.th, commGenerated.parentComm));
			}
		}
		return CommsToBrake;
		}
}
