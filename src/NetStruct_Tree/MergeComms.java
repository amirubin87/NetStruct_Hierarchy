package NetStruct_Tree;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.inference.ChiSquareTest;

public class MergeComms {	
	
	// Starting from root (level 0). 
	// Each community looks at its children. 
	// If (according to ChiSquare test) a child is similar to the parent, he is removed.
	// The child's children become the parent's children.
	public static Map<CommId,Map<String,Integer>> MergeCommsByPVal(CommId root, Map<CommId,Map<String,Integer>> mapCommToMapCodeToCount, double pVal) {
		Map<CommId,Map<String,Integer>> ans = new HashMap<CommId, Map<String,Integer>>();
		List<CommId> commsToInspectCurrentLevel = new ArrayList<CommId>();
		List<CommId> commsToInspectNextLevel = new ArrayList<CommId>();
		commsToInspectNextLevel.add(root);
		while(!commsToInspectNextLevel.isEmpty()){
			commsToInspectCurrentLevel =  new ArrayList<CommId>();
			commsToInspectCurrentLevel.addAll(commsToInspectNextLevel);
			// We are done with these.
			for(CommId comm : commsToInspectCurrentLevel){
				ans.put(comm, mapCommToMapCodeToCount.get(comm));
			}
			commsToInspectNextLevel =  new ArrayList<CommId>();
			for(CommId commToInspect: commsToInspectCurrentLevel){
				commsToInspectNextLevel.addAll(MergeChildrensOfComm(commToInspect, mapCommToMapCodeToCount, pVal));
			}
		}
		
		return ans;
		
		}
	
		private static Collection<? extends CommId> MergeChildrensOfComm(CommId commToInspect,
			Map<CommId, Map<String, Integer>> mapCommToMapCodeToCount, double pVal) {
			
			List<CommId> ans = new ArrayList<>();			
			List<CommId> newChildComms = commToInspect.childComms;
			List<CommId> childsToScan = new ArrayList<CommId>();
			while (!newChildComms.isEmpty()){
				childsToScan = new ArrayList<CommId>();
				childsToScan.addAll(newChildComms);
				newChildComms = new ArrayList<CommId>();
				for (CommId child : childsToScan)
					if (ShouldMergeComms(commToInspect,child, mapCommToMapCodeToCount,pVal)){
						// Remove child from parent
						commToInspect.childComms.remove(child);
						// Make the child's kids point to parent
						for (CommId grandson : child.childComms){
							grandson.parentComm = commToInspect;
							
						}
						// Make the child's kids be the parent's kids.
						commToInspect.childComms.addAll(child.childComms);
						// Make sure to scan them also
						newChildComms.addAll(child.childComms);
					}
					else{
						// Make sure the child get scaneed as a parent in the next iteration
						ans.add(child);
					}
					
			}
		return ans;
	}


	private static boolean ShouldMergeComms(CommId commToInspect, CommId child,
				Map<CommId, Map<String, Integer>> mapCommToMapCodeToCount, double pVal) {		
		
		Map<String, Integer> parentMapCodeToCount = mapCommToMapCodeToCount.get(commToInspect);
			long[] parentCounts = getCountsFromMap(parentMapCodeToCount);
			Map<String, Integer> childMapCodeToCount = mapCommToMapCodeToCount.get(child);
			long[] childCounts = getCountsFromMap(childMapCodeToCount);
			return calcPVal(parentCounts,childCounts) > pVal;
		}

	/*private static double[] CastToDouble(long[] longA) {
		double[] d = new double[longA.length];
		int index = 0; 
		for ( long l : longA){
			d[index] =(double)l;
			index++;
		}
		return d;
	}*/

	private static long[] getCountsFromMap(Map<String, Integer> mapCodeToCount) {
		long[] ans = new long[CommAnalyzer.amountOfSampleSites];		
		String[][] codes = CommAnalyzer.samplesSites;
		int counter = -1;
		for (String[] area : codes){
			for (String code : area){
				counter++;
				Integer count = mapCodeToCount.get(code);
				if(count == null){
					ans[counter] = 0;
				}
				else{
					ans[counter] = count;
				}
			}
		}
		return ans;
	}

	public static double calcPVal(long[] parentCounts, long[] childCounts){
	// Only keep entries in the expected that have value larger than 0.
	List<Integer> nonZeroIndexs = new ArrayList<>();
	
	for (int i = 0 ; i < parentCounts.length ; i++){
		if(parentCounts[i]>0){nonZeroIndexs.add(new Integer(i));} 
	}
	long[] newParentCounts = new long[nonZeroIndexs.size()];
	long[] newChildCounts = new long[nonZeroIndexs.size()];
	int counter = 0;
	for (Integer nonZeroI : nonZeroIndexs){		
		newParentCounts[counter] = parentCounts[nonZeroI];
		newChildCounts[counter] = childCounts[nonZeroI];
		counter++;		
	}		
	return chiTestWrapper(newParentCounts,newChildCounts);
	
	
	}
	private static double chiTestWrapper(long[] observed1, long[] observed2) {		
		// if parent had a single entry, we merge.
		if(observed1.length==1)return 1;
		ChiSquareTest chiSquareTest = new ChiSquareTest();
		return chiSquareTest.chiSquareTestDataSetsComparison(observed1,observed2);
		 
	}

	public static void chiTestCheck(){
		// Size_0091_|BEB:6	GIH:1	ITU:53	PJL:2	STU:29	|
		// Size_0044_|BEB:5	GIH:0	ITU:33	PJL:1	STU:5	|
		// 0.04162
		long[] expected = new long[] {8,8};		
		long[] observed = new long[]     {8,0};
		System.out.println(chiTestWrapper(expected, observed));				
		
	}
	


}
