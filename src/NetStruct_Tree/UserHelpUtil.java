package NetStruct_Tree;

public class UserHelpUtil {
	public final static String helpMsg = 
			"******************************************\n"
			+"       Welcome to NetStruct_Tree! \n"
			+"******************************************\n\n"
			+"---   This program will break a dense network to communities, in an hirarcial manner.\n"
			+"---   The code can be found at TDOD. \n"
			+"---   For full details please refer to our paper TODO.\n"
			+"---   If you use this code in your work, please cite TODO. \n"
			+"\n\n"
			+" Running the jar with a single param '-demo' will run a dummy sample, generating sample inputs and outputs in ./sample/ \n"
			+"-----------------------------------------------------\n"
			+"\n\n"
			+"Input params:\n"
			+"-----------------------------------------------------\n"
			+ "stepSize (-ss) - \n\t\t a double. For a given community, this value is added to the minimum edge weight in the community, to construct the threshold which any edge below it will be removed when looking for subcommunites. @stepSize is added until we break the given community. Default - 0.0001. \n"
			+ "dynamicChoose (-dy) -\n\t\t a boolean, controls if NECTAR chooses dynamicly between WOCC and Modularity to find communities. Default - false. \n" 
			+ "useModularityAsDefaultMetric (-mod) -\n\t\t a boolean, controls the default metric NECTAR uses to find modularity (incase @dynamicChoose is set to false). Default - true. \n" 
			+ "minSizeOfCommToBrake (-minb) -\n\t\t an int, the minimum amount of nodes in a community in order to brake it to sub-communities. Default - 3. \n"
			+ "minSizeOfCommToOutput (-mino) -\n\t\t an int, the minimum amount of nodes in a community in order to output it. Default - 3. \n"			
			+ "beta (-b) -\n\t\t a double, at least 1.0 (e.g. '1.0'). Used by NECTAR to find communities (when given 1.0, there is no overlap between communities). \n"			
			+ "pathToRootOutputDir (-pro) -\n\t\t full path to the directory where the output will be written. Will overwrite existing files! A sub directory in this location will be created, including all the params used in its name.\n"
			+ "skipBrakeComms (-skip) -\n\t\t internal use. a boolean, when true, will read the file in @pathToCommAnalysisFile and perfom merge by Chi-squere and F1 and structure calculation. Default - false \n"
			+ "pathToCommAnalysisFile (-pca) -\n\t\t Internal use. full path to CommAnalysisFile - an existing output of this program. used when @skipBrakeComms is true. \n"			
			+ "pathToMatrixFile (-pm) -\n\t\t full path to the the file containing a matrix of weights between nodes, with respect to the list of nodes in @pathToMapNode2SampleSite. \n"			
			+ "pathToEdgesFile (-pe) -\n\t\t full path to the the file containing a list of weighted edges (format is <node> <node> <weight>), with respect to the list of nodes in @pathToMapNode2SampleSite. \n\t\t -------- PLEASE NOTE THAT ALL NUMBERS BETWEEN 0 AND n-1(number of nodes) SHOULD APPEAR! \n\t\t -------- If the paramter given in @pathToMatrixFile is not a valid file, the list of edges will be used. \n"
			+ "pathToMapNode2SampleSite (-pmn) -\n\t\t full path to file containing in each line i a sampleSite code indicating the sampleSite of node i.\n"
			+ "pathToSampleSites (-pss) -\n\t\t full path to file containing in each line a list of comma separated sample sites (which match the values in @pathToMapNode2SampleSite.) In each line you can put several sample sites, they will be considered to be in the same area. This is non mandatory - you may put all sample sites in a single line. \n"
			+ "nectarVerboseLevel (-nvl) -\n\t\t one of 0,1,2 - verbose level of nectar (higher is more vebose).  Default - 0. \n"
			+ "useWeighted (-w) -\n\t\t boolean. When true - only Modularity is used. Default - true. \n"
			/*
			+ "useLeafSizesForStructure (-ls) -\n\t\t boolean. When true - sizes of leafs decide the proportions of the STRUCTURE output, otherwise the split in the tree are used. Default - false. \n"
			+ "useProportionalTreeSplitsForStructure (-pts) -\n\t\t boolean. Effective only when @useLeafSizesForStructure is false. When true - the STRUCTURE output splits in the tree are used with respect to each branch on its own. So if the node has two childrens, one with one child and the other with two - the . Default - false. \n"
			*/
			+ "pathToIndividulasToExclude (-indtoex) -\n\t\t string. When supplied must be a path to a file containing a comma separated list of nodes. Note that if a matrix of edges is given, the first node index is 0! Default - null. \n"			
			+ "\n"
			+"-----------------------------------------------------\n"
			+"Output: \n"
			+"-----------------------------------------------------\n"
			+ "1_CommAnalysis_...txt -\n\t\t UNMERGED comm stats found.\n"
			+ "2_Leafs_NoOverlap.txt -\n\t\t Leafs found in the tree. Dropped individuals are assigned to a singleton leaf.\n"
			+ "2_Leafs_WithOverlap.txt -\n\t\t Leafs found in the tree. Dropped individuals are assigned to a all leafs under the node in which they appear.\n"			
			/*
			+ "3_MergedCommAnalysis_....txt -\n\t\t MERGED comm stats found.\n"
			+ "4_F1Score_....txt -\n\t\t F1 score between sample sites using the communities found.\n"
			+ "5_Structure_....txt -\n\t\t Structure analysis based on the communities found.\n"			
			+ "6_Leafs_NoOverlap.txt -\n\t\t Leafs found in the MERGED tree. Dropped individuals are assigned to a singleton leaf.\n"
			+ "6_Leafs_WithOverlap.txt -\n\t\t Leafs found in the MERGED tree. Dropped individuals are assigned to a all leafs under the node in which they appear.\n"
			*/
			+ "log_<@matrixFileName>_<HH-mm_d--MM-YYYY>.log -\n\t\t log of the process.\n"
			+ "Level_#_Entry_#_ParentLevel_#_ParentEntry_#_ParentLine_#_TH__Comms.txt -\n\t\t a list of comms in the given level and entry.\n"
			+ "Level_#_Entry_#_ParentLevel_#_ParentEntry_#_ParentLine_#_TH__Edges.txt -\n\t\t a list of edges in the given level and entry.\n"
			;
	public final static String paramMsg = 
			"******************************************\n"
			+"       Welcom to NetStruct_Tree! \n"
			+"******************************************\n\n"
			+ "Use -h to get full help.\n\n"			
			+" Running the jar with a single param '-demo' will run a dummy sample, generating sample inputs and outputs in ./sample/ \n"			
			+"\n\n"
			+ "Input parameteres for NetStruct_Tree: \n"			
			+ "stepSize = 0.0001 \n"
			+ "dynamicChoose = false \n" 
			+ "useModularityAsDefaultMetric = true \n" 
			+ "minSizeOfCommToBrake = 3 \n"
			+ "minSizeOfCommToOutput = 3 \n"
			+ "beta = 1.0 \n"
			+ "pathToRootOutputDir = C:/Data/\n"
			+ "skipBrakeComms = false \n"
			+ "pathToCommAnalysisFile placeholder (full path is required if @skipBrakeComms)\n"			
			+ "pathToMatrixDir = C:/Data/Matrix.txt \n"
			+ "pathToEdgesFile = C:/Data/ListOfEdges.txt \n"					
			+ "pathToMapNode2SampleSiteCode = C:/Data/indlist.csv \n"
			+ "pathToSampleSites = C:/Data/SampleSites.txt \n"			
			+ "nectarVerboseLevel = 0 \n"
			+ "useWeighted = true \n"
			/*
			+ "useLeafSizesForStructure = false \n"			
			+ "useProportionalTreeSplitsForStructure = false \n"
			*/
			+ "pathToIndividulasToExclude = null \n"
			+ "\n For your use - a sample full command line: \n"
			+ "java -jar NetStruct_Tree_v1.jar -ss 0.0001 -dy true -mod true -minb 3 -mino 3 -b 1.0"
			+ " -pro C:/Data/ -skip false -pca placeholder -pm C:/Data/Matrix.txt"
			+ " -pe placeholder -pmn C:/Data/indlist.txt -pss C:/Data/SampleSites.txt -nvl 0 -w true -indtoex placeholder\n";
			
	public final static String DUMMY_All_chrome_M = 
			"0.5	0.5	0.0001	0.0001 0.0001\n" +
			"0.5	0.0001	0.0001 0.0001\n" +
			"0.0001	0.0001 0.0001\n" +
			"0.5 0.5\n" +
			"0.5";
	public final static String DUMMY_indlist = "Africa\nAfrica\nAfrica\nAmerica\nAmerica\nAmerica";
	public final static String DUMMY_SampleSites = "Africa,America";
}
