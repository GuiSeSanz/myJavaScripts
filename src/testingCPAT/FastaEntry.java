package testingCPAT;

public class FastaEntry {
	public String id;
	public String sequence;
	public String sense;
	
	public FastaEntry(String id, String sequence, String sense){
		this.id = id;
		this.sequence = sequence;
		this.sense = sense;
	}

	public String getId() {
		return id;
	}

	public String getSequence() {
		return sequence;
	}

	public String getSense() {
		return sense;
	}

}
