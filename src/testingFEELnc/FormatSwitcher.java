package testingFEELnc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.biobam.blast2go.api.utils.FileUtils;

public class FormatSwitcher {
	public static void main(String[] args) throws IOException {
		String inPath = "/home/guillermo/Files/Coding_Potential/Comparing/BOAR/FEELnc/FEELnc_Intergenic/ContigAnnotations.gff";
		gff2GTF(inPath);
	}

	private static void gff2GTF(String inPath) throws IOException {
		File inFile = new File(inPath);
		BufferedReader br = new BufferedReader(new FileReader(inFile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(inFile.getParent() + File.separator + inFile.getName().substring(0, inFile.getName().length()-4) + ".gtf")));
		String line ;
		while ((line = br.readLine()) != null){
			line = line.replaceAll("=", " \"");
			line = line.replaceAll(";", "\";");
			bw.write(line);
			bw.newLine();
		}
		br.close();
		bw.close();
		
	}
}
