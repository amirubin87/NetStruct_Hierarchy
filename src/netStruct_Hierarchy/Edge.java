package netStruct_Hierarchy;
public class Edge {
		public final Integer from;
		public final Integer to;
		public final double weight;
		
		public Edge(Integer from, Integer to, double weight){
			this.from = from;
			this.to = to;
			this.weight = weight;
		}
		
		@Override
		public String toString(){
			return ((from!=null) ? from.toString() : null) + '\t' + ((to!=null) ? to.toString() : null) + '\t' + weight;
		}
	}