package testingCPAT;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClassifyResults {
	//	public enum Column{
	//		ID, mRNA_size, ORF_size, Fickett_score, Hexamer_score, coding_prob;
	//		
	//		public static int size() {
	//			return Column.values().length;
	//		} 
	//	}

	public enum Column {
		ID(0),
		mRNA_size(1),
		ORF_size(2),
		Fickett_score(3),
		Hexamer_score(4),
		coding_prob(5);
		private int position;

		private Column(int position) {
			this.position = position;
		}

		public int getPos() {
			return position;
		}

		public static int size() {
			return Column.values().length;
		}
	}

	public enum CodingScore {
		customPig(0.392),
		Human(0.364),
		Mouse(0.44),
		Fly(0.39),
		Zebrafish(0.38);
		private double codingCutOFF;

		private CodingScore(double codingCutOFF) {
			this.codingCutOFF = codingCutOFF;
		}

		public double getCutOFF() {
			return codingCutOFF;
		}
	}

	public static void main(String[] args) throws IOException {

		//		String inPath = "/home/guillermo/Documents/CPAT/CPAT-1.2.2/test/output2";
		String inPath = "/home/guillermo/Files/Coding_Potential/Comparing/BOAR/CPAT/Untrained/Results";
		File inFile = new File(inPath);
		List<RNAentry> codingRNAlist = new ArrayList<>();
		List<RNAentry> nonCodingRNAlist = new ArrayList<>();

		BufferedReader br = new BufferedReader(new FileReader(inFile));
		String line;
		boolean header = true;
		while ((line = br.readLine()) != null) {
			if (header) {
				header = false;
				continue;
			}
			String[] sLine = line.split("\t");

			RNAentry entry = new RNAentry(sLine[Column.ID.getPos()], Float.parseFloat(sLine[Column.coding_prob.getPos()]));

			if (entry.score < CodingScore.Human.getCutOFF()) {
				System.out.println(entry.score + "----> nonCoding");
				nonCodingRNAlist.add(entry);
			} else if (entry.score > CodingScore.Human.getCutOFF()) {
				codingRNAlist.add(entry);
				System.out.println(entry.score + "----> Coding");
			}

		}
		br.close();
		String outPath = inFile.getParent();
		File outfile = new File(outPath + File.separator + "ClassifiedResultswithoutScore.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
		bw.write("Number of coding RNAs: " + codingRNAlist.size() + "/" + (codingRNAlist.size() + nonCodingRNAlist.size()));
		bw.newLine();
		bw.write("Number of nonCoding RNAs: " + nonCodingRNAlist.size() + "/" + (codingRNAlist.size() + nonCodingRNAlist.size()));
		bw.newLine();
		bw.write("==================Coding sequences==================");
		bw.newLine();
		for (RNAentry rnaEntry : codingRNAlist) {
			bw.write("ID: " + rnaEntry.getId() + " With a CP score: " + rnaEntry.getScore());
			bw.newLine();
		}
		//		System.out.println("Number of coding RNAs: " + codingRNAlist.size() + "/" + (codingRNAlist.size() + nonCodingRNAlist.size()));
		bw.write("=================nonCoding sequences=================");
		bw.newLine();
		for (RNAentry rnaEntry : nonCodingRNAlist) {
			//			if (rnaEntry.getId().equals("677")) {
			//				System.out.println("wololo");
			//			}
			bw.write("ID: " + rnaEntry.getId() + " With a CP score: " + rnaEntry.getScore());
			bw.newLine();
		}
		System.out.println("Number of nonCoding RNAs: " + nonCodingRNAlist.size() + "/" + (codingRNAlist.size() + nonCodingRNAlist.size()));
		bw.close();

	}

}
