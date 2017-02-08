package testingFEELnc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.bioinfo.commons.utils.StringUtils;


public class RenameChromosome {

	public static void main(String[] args) throws IOException {
		String inPath ="/home/guillermo/Files/Coding_Potential/Comparing/BOAR/FEELnc/FEELnc_Intergenic/Ch1_SusScrofa_Annot.gtf";
		
		File infile = new File(inPath);
		BufferedReader br = new BufferedReader(new FileReader(infile));
		String line;
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(infile.getParent()+File.separator+"Renamedch1_SusScrofa_Annot.gtf")));
		StringBuilder sb = new StringBuilder();
		String newName = "gi|333786679|ref|NW_003534184.1|";
		while ((line=br.readLine())!= null){
			String newLine = "";
			String[] spLine = line.split("\t");
			if (spLine[0].equals("1")) {
				spLine[0]= newName;
				newLine = StringUtils.join(spLine, "\t");
//				System.out.println(newLine);
			}
			bw.write(newLine);
			bw.newLine();
		}
		br.close();
		bw.flush();
		bw.close();
		System.out.println("Completed!!!");

	}

}
