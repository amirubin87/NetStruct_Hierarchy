import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.text.*;
// A community is a list of nodes.
// We have a "level".
// In that "lavel" we have several "entries" - each contains several comms.
// In each "entry" we have several comms - each in its own line - the id is the "line".
// To generate the comm, a ceratin threshold was used. We store it in "th.
// Each "entry" was derived from a ceratin comm - "parentComm" points to it.
public class CommId {	
	public String pathToWorkingDir;
	public int level;
	public int entry;
	public int line;
	public double th;
	public CommId parentComm;
	public List<CommId> childComms;
	public String metricUsed;
	public String edgesFileName;
	public String commsFileName;
	public Map<CommId, Double> leafs;
	public String leafsVectorAsString;
	public boolean leafPopulated = false;
	private int size; 
	
	public CommId(String pathToWorkingDir, int level, int entry, int line, double th , CommId parentComm) throws IOException{		
		this(pathToWorkingDir,level,entry,line);
		this.th = th;
		this.parentComm = parentComm;
		this.size = -1; 
		this.edgesFileName = generateFileName("Edges");
		this.commsFileName = generateFileName("Comms");
	}
	
	public CommId(String pathToWorkingDir, int level, int entry, int line){
		this.pathToWorkingDir = pathToWorkingDir;
		this.level = level;
		this.entry = entry;
		this.line = line;	
		this.metricUsed = "NotSet";		
		this.childComms = new ArrayList<>();
		// These wont be changed, regardless of any future change in parent comm.
		this.edgesFileName = generateFileName("Edges");
		this.commsFileName = generateFileName("Comms");
		this.leafs = new HashMap<CommId, Double>();
		this.size=-1;
	}
	
	public int Size() throws IOException{
		if (size==-1){			
			size = GetNodes().size();
		}
		return size;
	}
	private String generateFileName(String type) {
		if (parentComm == null)
			return pathToWorkingDir + "Level_" + level + "_Entry_" + entry + "_TH_" + th + "_" + type + ".txt";
		//*** return pathToWorkingDir + "Level_" + level + "_Entry_" + entry + "_ParentEntry_" + parentComm.entry + "_ParentLine_" + parentComm.line + "_TH_" + th + "_" + type + ".txt";
		return pathToWorkingDir + "Level_" + level + "_Entry_" + entry + "_ParentLevel_" + parentComm.level + "_ParentEntry_" + parentComm.entry + "_ParentLine_" + parentComm.line + "_TH_" + th + "_" + type + ".txt";
	}
	
	public Set<Integer> GetNodes() throws IOException{
		return TextConvertor.getListOfNodes(commsFileName, line);
	}
	
	public Set<Edge> GetEdges() throws IOException{
		return TextConvertor.getListOfEdges(edgesFileName);
	}
	

	public void PopulateLeafMap(int minSizeOfCommToOutput, boolean useLeafSizesForStructure, boolean useProportionalTreeSplitForStructure) throws IOException{		
		if (useLeafSizesForStructure) PopulateLeafMapByLeafSize(minSizeOfCommToOutput);
		else PopulateLeafMapByTreeSplit(minSizeOfCommToOutput, useProportionalTreeSplitForStructure);
	}
	
	// A recursive method.
	// If we are a leaf, we simply return ourself.
	// If we have kids, we get the Leafs from them, and use them.
	private void PopulateLeafMapByLeafSize(int minSizeOfCommToOutput) throws IOException{		
		if (leafPopulated) return;
		leafPopulated = true;
		if (IsLeaf(minSizeOfCommToOutput)) {
			if(Size() >= minSizeOfCommToOutput){
				leafs.put(this, (double)(GetNodes().size()));	
			}
		}
		else{
			for (CommId child : childComms) {
				child.PopulateLeafMapByLeafSize(minSizeOfCommToOutput);
				Map<CommId, Double> childLeafs = child.leafs;
				leafs.putAll(childLeafs);
			}
		}		
	}
	
	// A recursive method.
	// If we are a leaf, we simply return ourself.
	// If we have kids, we get the Leafs from them, and use them.
	private void PopulateLeafMapByTreeSplit(int minSizeOfCommToOutput, boolean useProportionalTreeSplitForStructure) throws IOException{	
		if (leafPopulated) return;
		leafPopulated = true;
		if (IsLeaf(minSizeOfCommToOutput)) {
			if(Size() >= minSizeOfCommToOutput){
				// to prevent numerical errors.
				// We may want to use something like n(number of nodes in graph).
				leafs.put(this, 10000.0);	
			}
		}
		else{
			for (CommId child : childComms) {
				child.PopulateLeafMapByTreeSplit(minSizeOfCommToOutput, useProportionalTreeSplitForStructure);
				Map<CommId, Double> childLeafs = child.leafs;					
				leafs.putAll(childLeafs);
			}
			// To get the proportional by tree split, we need to devide each value by the number of childs we have.
			if(useProportionalTreeSplitForStructure){
				int numOfChilds = childComms.size();
				for( Entry<CommId, Double> leafAndSize : leafs.entrySet()){
					leafs.put(leafAndSize.getKey(), leafAndSize.getValue()/numOfChilds);
				}
			}
			// If we do not want to preserve the proportonality, we simply assign 1/num of leafs in our subtree.
			else{
				int numOfLeafsInSubTree = leafs.size();
				for( Entry<CommId, Double> leafAndSize : leafs.entrySet()){
					leafs.put(leafAndSize.getKey(), 1.0/(double)numOfLeafsInSubTree);
				}
			}
		}		
	}
	
	public boolean IsLeaf(int minSizeOfCommToOutput) throws IOException {
		int numOfBigEnoughChilds = childComms.size();
		if(numOfBigEnoughChilds == 0) return true;
		for (CommId child : childComms) {
			// If the size is smaller, he is not big enough
			if(child.Size() < minSizeOfCommToOutput){
				numOfBigEnoughChilds--;
			}
		}
		return numOfBigEnoughChilds==0;
	}

	@Override
	public String toString(){
		DecimalFormat df = new DecimalFormat("#.#####");
		if (parentComm == null)
			return "Level_" + level + "_Entry_" + entry + "_Line_" + line + "_TH_" + df.format(th) + "_" + metricUsed + "_";
		return "Level_" + level + "_Entry_" + entry + "_Line_" + line + "_ParentLevel_" + parentComm.level + "_ParentEntry_" + parentComm.entry + "_ParentLine_" + parentComm.line + "_TH_" + df.format(th) + "_" + metricUsed + "_";		
	}
	
    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof CommId)) {
            return false;
        }

        CommId other = (CommId) o;

        return other.level == level &&
        	   other.entry == entry &&
        	   other.line == line;
    }
    
    //Idea from effective Java : Item 9
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + level;
        result = 31 * result + entry;
        result = 31 * result + line;
        return result;
    }

	public void SetLeafsVectorAsString(List<CommId> sortedAllLeafs) {
		leafsVectorAsString = "";
		Double[] v = new Double[sortedAllLeafs.size()];
		int index = 0;
		double sum = 0;
		for (CommId comm : sortedAllLeafs){
			Double val = leafs.get(comm);
			v[index] = (val ==null ? 0.0 : val);
			index++;
			sum = sum + (val ==null ? 0.0 : val);
		}		
		
		for(int a = 0; a < v.length; a++) {
			leafsVectorAsString = leafsVectorAsString + ((double)v[a] / (double)sum) + ',';
		}
		if(leafsVectorAsString.length()>0){
			leafsVectorAsString =  leafsVectorAsString.substring(0,leafsVectorAsString.length()-1);
		}
	}

	public void resetLeafMap() {
		leafPopulated=false;
		this.leafs = new HashMap<CommId, Double>();
		for( CommId child : this.childComms){
			child.resetLeafMap();
		}		
	}


}
