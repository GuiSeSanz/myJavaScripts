package my.tests.example1;

import java.util.ArrayList;
import java.util.List;

public class FastaRNA {

	public String SpeciesName;
	public List<String> NameANDSeqs;

	public FastaRNA(String SpeciesName, String NameANDSeqs) {
		this.SpeciesName = SpeciesName;
		this.NameANDSeqs = new ArrayList<String>();
		this.NameANDSeqs.add(NameANDSeqs);
	}

	public void addLine(String line) {
		this.NameANDSeqs.add(line);
	}

	public String getName() {
		return SpeciesName;
	}

	public List<String> getNamesAndSeqs() {
		return NameANDSeqs;
	}
}
