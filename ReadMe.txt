
******************************************
       Welcome to NetStruct_Tree!
******************************************

---   This program will break a dense network to communities, in an hirarcial manner.

---   For full details please refer to our paper TODO.
---   If you use this code in your work, please cite TODO.

-----------------------------------------------------
Input params:
-----------------------------------------------------
stepSize (-ss) -
                 a double. For a given community, this value is added to the minimum edge weight in the commuity, to construct the threshold which any edge below it will be removed when looking for subcommunites. #stepSize is added untill we break the given community. Default - 0.0001.
dynamicChoose (-dy) -
                 a boolean, controls if NECTAR chooses dynamicly between WOCC and Modularity to find communities. Default - false.
useModularityAsDefaultMetric (-mod) -
                 a boolean, controls the default metric NECTAR uses to find modularity (incase @dynamicChoose is set to false). Default - true.
minSizeOfCommToBrake (-minb) -
                 an int, the minimum amount of nodes in a community inorder to brake it to sub-communities. Default - 20.
minSizeOfCommToOutput (-mino) -
                 an int, the minimum amount of nodes in a community inorder to output it. Default - 20.
beta (-b) -
                 a double, at least 1.0 (e.g. '1.0'). Used by NECTAR to find communities (when given 1.0, there is no overlap between communities).
pathToRootOutputDir (-pro) -
                 full path to the directory where the output will be written. Will overwrite existing files! A sub directory in this location will be created, including all the params used in its name.
skipBrakeComms (-skip) -
                 internal use. a boolean, when true, will read the file in @pathToCommAnalysisFile and perfom merge by Chi-squere and F1 and structure calculation. Default - false
pathToCommAnalysisFile (-pca) -
                 Internal use. full path to CommAnalysisFile - an existing output of this program. used when @skipBrakeComms is true.
pathToMatrixFile (-pm) -
                 full path to the the file containing a matrix of weights between nodes, with respect to the list of nodes in @pathToMapNode2GroundTruthCode.
pathToEdgesFile (-pe) -
                 full path to the the file containing a list of weighted edges (format is <node> <node> <weight>), with respect to the list of nodes in @pathToMapNode2GroundTruthCode.
                 -------- PLEASE NOTE THAT ALL NUMBERS BETWEEN 0 AND n-1(number of nodes) SHOULD APPEAR!
                 -------- If the paramter given in @pathToMatrixFile is not a valid file, the list of edges will be used.
pathToMapNode2SampleSite (-pmn) -
                 full path to file containing in each line i a sampleSite code indicating the sampleSite of node i. Each line should have <code><space> in it.
pathToSampleSites (-pss) -
                 full path to file containing in each line a list of comma seperated sample sites (which match the values in @pathToMapNode2SampleSite.) In each line you can put several sample sites, they will be considered to be in the same area. This is non mandatory - you may put all sample sites in a single line.
nectarVerboseLevel (-nvl) -
                 one of 0,1,2 - verbose level of nectar (higher is more vebose).  Default - 0.
useWeighted (-w) -
                 boolean. When true - only Modularity is used. Default - true.
useLeafSizesForStructure (-ls) -
                 boolean. When true - sizes of leafs decide the proportions of the STRUCTURE output, otherwise the split in the tree are used. Default - false.
useProportionalTreeSplitsForStructure (-pts) -
                 boolean. Effective only when @useLeafSizesForStructure is false. When true - the STRUCTURE output splits in the tree are used with respect to each branch on its own. So if the node has two childrens, one with one child and the other with two - the . Default - false.
pathToIndividulasToExclude (-indtoex) -
                 string. When supplied must be a path to a file containing a comma seperated list of nodes. Note that if a matix of edges is given, the first node index is 0! Default - null.

-----------------------------------------------------
Output:
-----------------------------------------------------
1_CommAnalysis_...txt -
                 UNMERGED comm stats found.
2_Leafs_BeforeMerge_NoOverlap.txt -
                 Leafs found in the UNMERGED tree. Dropped individuals are assigned to a singleton leaf.
2_Leafs_BeforeMerge_WithOverlap.txt -
                 Leafs found in the UNMERGED tree. Dropped individuals are assigned to a all leafs under the node in which they appear.
3_MergedCommAnalysis_....txt -
                 MERGED comm stats found.
4_F1Score_....txt -
                 F1 score between sample sites using the communities found.
5_Structure_....txt -
                 Structure analysis based on the communities found.
6_Leafs_NoOverlap.txt -
                 Leafs found in the MERGED tree. Dropped individuals are assigned to a singleton leaf.
6_Leafs_WithOverlap.txt -
                 Leafs found in the MERGED tree. Dropped individuals are assigned to a all leafs under the node in which they appear.
log_<@matrixFileName>_<HH-mm_d--MM-YYYY>.log -
                 log of the process.
Level_#_Entry_#_ParentLevel_#_ParentEntry_#_ParentLine_#_TH__Comms.txt -
                 a list of comms in the given level and entry.
Level_#_Entry_#_ParentLevel_#_ParentEntry_#_ParentLine_#_TH__Edges.txt -
                 a list of edges in the given level and entry.


Input parameteres for NetStruct_Tree:
stepSize = 0.0001
dynamicChoose = false
useModularityAsDefaultMetric = true
minSizeOfCommToBrake = 20
minSizeOfCommToOutput = 20
beta = 1.0
pathToRootOutputDir = C:/Data/
skipBrakeComms = false
pathToCommAnalysisFile placeholder (full path is required if @skipBrakeComms)
pathToMatrixDir = C:/Data/Matrix.txt
pathToEdgesFile = C:/Data/ListOfEdges.txt
pathToMapNode2SampleSiteCode = C:/Data/indlist.csv
pathToSampleSites = C:/Data/SampleSites.txt
nectarVerboseLevel = 0
useWeighted = true
useLeafSizesForStructure = false
useProportionalTreeSplitsForStructure = false
pathToIndividulasToExclude = null

 For your use - a sample full command line:
java -jar NetStruct_Tree_v1.jar -ss 0.0001 -dy true -mod true -minb 20 -mino 20 -b 1.0 -pro C:/Data/ -skip false -pca placeholder -pm C:/Data/Matrix.txt -pe placeholder -pmn C:/Data/indlist.txt -pss C:/Data/SampleSites.txt -nvl 0 -w true -ls false -pts false -indtoex placeholder