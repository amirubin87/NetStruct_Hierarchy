public class Person {
	public final int id;
	public final SampleSite sampleSite;
	public String pathFromRoot;	
	
	public Person(int id, SampleSite sampleSite){
		this.id = id;
		this.sampleSite = sampleSite;
		this.pathFromRoot="";
	}
	
	/*public double calcF1Score(Person other){
		int maxLength = Math.max(pathFromRoot.length(), other.pathFromRoot.length());
		int greatestCommonPrefixLength = Common.greatestCommonPrefix(pathFromRoot, other.pathFromRoot).length();
		return (double)greatestCommonPrefixLength/(double)maxLength;
	}*/
	
	public double calcF1Score(Person other){
		int maxLength = Math.max(pathFromRoot.length(), other.pathFromRoot.length());
		int minLength = Math.max(1,Math.min(pathFromRoot.length(), other.pathFromRoot.length()));
		int greatestCommonPrefixLength = Common.greatestCommonPrefix(pathFromRoot, other.pathFromRoot).length();
		double p =  (double)greatestCommonPrefixLength/(double)maxLength;
		double r =  (double)greatestCommonPrefixLength/(double)minLength;
		return (2*p*r)/(p+r);
	}
	
	@Override
	public String toString(){
		return id + ", sampleSite " + sampleSite.code;		
	}

}
