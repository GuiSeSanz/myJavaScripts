package my.tests.example1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class RNAspName {
	public static void main(String[] args) throws IOException {

		String filePath = "/home/guillermo/Files/Coding_Potential/Data/NCBI/Viridiplantae.fasta";
		File fastaFile = new File(filePath);
		String newFilePath = fastaFile.getParent();

		writeOnFiles(newFilePath, readAndSplit(fastaFile));
	}

	private static List<FastaRNA> readAndSplit(File fastaFile) throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(fastaFile));
		String line = null;
		List<FastaRNA> RNAlist = new ArrayList<>();
		String lastName = "";
		String name = "";
		FastaRNA fastaRNA = null;
		while ((line = br.readLine()) != null) {
			if (line.startsWith(">")) {
				name = line.substring(1, line.length() - 6);
				//if first line of the list
				if (!lastName.equals(name)) {
					if (fastaRNA != null) {
						RNAlist.add(fastaRNA);
					}
					lastName = name;
					System.out.println("Parsing entry: "+name);
					fastaRNA = new FastaRNA(name, line);
				} else {
					fastaRNA.addLine(line);
				}
			} else {
				fastaRNA.addLine(line);
			}
		}
		br.close();
		return RNAlist;
	}

	private static void writeOnFiles(String newFilePath, List<FastaRNA> RNAlist) throws IOException {
		for (FastaRNA RNApack : RNAlist) {
			String newFilePathName = newFilePath + File.separator + RNApack.getName().replace(" ", "_").replace("/", "_") + ".fsa";
			File RNAfile = new File(newFilePathName);
			System.out.println("Writing file: " + newFilePathName);
			FileWriter fw = new FileWriter(RNAfile);
			BufferedWriter bw = new BufferedWriter(fw);
			for (String entry : RNApack.getNamesAndSeqs()) {
				bw.write(entry + "\n");
			}
			bw.close();
		}
	}
}
