package netStruct_Hierarchy;
import java.util.HashMap;
import java.util.Map;

public class SampleSite {
	public final String code;
	public Map<Integer, Person> members;
	
	public SampleSite(String code){
		this.code = code;
		members = new HashMap<>();
	}
	
	public double calcF1Score(SampleSite other){
		double sum = 0;
		for(Integer p1Id : members.keySet()){
			for(Integer p2Id : other.members.keySet()){
				// Danger! We may have a numeric mistake here!				
				sum = sum + members.get(p1Id).calcF1Score(other.members.get(p2Id));
			}
		}
		// Take average across all.
		return sum/(members.size()*other.members.size());
	}
	
	@Override
	public String toString(){
		return code + ", num of memebers: " + members.size();		
	}
	
    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof CommId)) {
            return false;
        }

        SampleSite other = (SampleSite) o;

        return other.code == code;
    }
    
    //Idea from effective Java : Item 9
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + code.hashCode();
        return result;
    }
}
