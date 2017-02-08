package testingFEELnc;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.biobam.blast2go.preferences.proxy.ProxyAware;

public class DownloadNCBIcontigAnnotation {

	public enum ncbiTool {
		search("esearch.fcgi"),
		download("efetch.fcgi");
		private String tool;

		private ncbiTool(String tool) {
			this.tool = tool;
		}

		public String getTool() {
			return tool;
		}
	}

	public static void main(String[] args) throws IOException {

		String base = "http://togows.org/entry/ncbi-nuccore/";

		String inPath = "/home/guillermo/Files/Coding_Potential/Comparing/BOAR/FEELnc/FEELnc_Intergenic/ContigNames";
		File inFile = new File(inPath);
		BufferedReader br0 = new BufferedReader(new FileReader(inFile));
		List<String> idList = new ArrayList<>();
		String line0;
		while ((line0 = br0.readLine()) != null) {
			idList.add(line0);
		}
		br0.close();
		File output = new File(inFile.getParent() + File.separator + "ContigAnnotations.gff");
		if (output.exists()) {
			output.delete();
		}
		int counter = 0;
		FileWriter fw = new FileWriter(output, true);
		BufferedWriter bw = new BufferedWriter(fw);
		BufferedInputStream inputStream = null;
		String line;
		String[] splitedLine;
		for (String id : idList) {
			inputStream = new ProxyAware(base + id + ".gff").inputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			while ((line = br.readLine()) != null){
			if (line.startsWith(">")) {
				break;
			}	
			if (!line.startsWith("#") && ((line.contains("gene") || line.contains("mRNA")) && !line.contains("ncRNA"))) {
				splitedLine = line.split("note=");
				bw.write(splitedLine[0]);
				bw.newLine();
			}
			
			}
			counter ++;
			System.out.println(counter + "/" + idList.size());

		}
		bw.close();

		System.out.println("FINISHED!!! :D");
	}

}