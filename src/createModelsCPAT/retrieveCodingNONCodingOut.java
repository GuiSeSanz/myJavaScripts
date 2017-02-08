
package createModelsCPAT;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import com.biobam.blast2go.basic_utilities.Utilities;
import com.biobam.blast2go.preferences.proxy.ProxyAware;

public class retrieveCodingNONCodingOut {

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

	public enum searchType {
		nonCoding("AND (ncrna[Filter] OR rrna[filter] OR trna[filter] OR snrna[filter] OR snorna[filter] )", "_NonCoding.fsa"),
		Coding_cds("AND (cds[Feature key])", "_Codingcds.fsa"),
		Coding_mrna("AND (mrna[Filter])", "_Codingmrna.fsa");
		private String type;
		private String fileExt;

		private searchType(String type, String fileExt) {
			this.type = type;
			this.fileExt = fileExt;
		}

		public String getType() {
			return type;
		}

		public String getFileExt() {
			return fileExt;
		}
	}

	public static void main(String[] args) throws IOException {
		String Sp = ""; //<- given by user
		String outPath = "";
		if (args.length == 1 || (args.length == 2 && args[1].equals("."))) {
			Sp = args[0];
			System.out.println("Selected directory: " + System.getProperty("user.dir"));
			outPath = System.getProperty("user.dir");
		} else if (args.length == 2){
			Sp = args[0];
			outPath = args[1];
		}
		
//		outPath = "/home/guillermo/Files/Coding_Potential/Comparing/ComparingPrebuildModels/"; //<- to tmp folder
		String ORGANISM = "\"" + Sp + "\"[Organism])";
		ifExistDelete(outPath);
		/*
		 * First: Download the ncRNA and keep the number of sequences
		 */

		final int maxSeqs = searchAndDownload(searchType.nonCoding, ORGANISM, outPath, 0);
		
		searchAndDownload(searchType.Coding_cds, ORGANISM, outPath, maxSeqs);
			
		searchAndDownload(searchType.Coding_mrna, ORGANISM, outPath, maxSeqs);
			
	}

	private static int searchAndDownload(searchType type, String organism, String outPath, int maxSeqs) throws MalformedURLException, IOException {
		Thread.currentThread()
		        .setName(type + "Seeker&Downloader");
		String BASE = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/";
		int retmax = 100;
		Map<String, String> searchParameters = new HashMap<String, String>();
		searchParameters.put("tool", "blast2go");
		searchParameters.put("email", "support@blast2go.com");
		searchParameters.put("db", "nuccore");
		searchParameters.put("term", organism + type.getType());
		System.out.println(organism + type.getType());
		searchParameters.put("retmax", Integer.toString(retmax));
		searchParameters.put("usehistory", "y");

		BufferedInputStream seqSearch = new ProxyAware(BASE + ncbiTool.search.getTool()).setConnectTimeout(15000)
		        .setParameters(searchParameters)
		        .setReadTimeout(15000)
		        .inputStream();

		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(seqSearch));
		/*
		 * Now we parse the search results to download the sequences We use the WebEnv as the second web search (uses the previous search history)
		 */

		String WebEnvPattern = "<WebEnv>(\\S+)<\\/WebEnv>";
		String QueryKeyPattern = "<QueryKey>(\\d+)<\\/QueryKey>";
		String CountPattern = "<Count>(\\d+)<\\/Count>";
		Pattern r1 = Pattern.compile(WebEnvPattern);
		Pattern r2 = Pattern.compile(QueryKeyPattern);
		Pattern r3 = Pattern.compile(CountPattern);
		String WebEnv = "";
		String QueryKey = "";
		int Sequences = 0;
		while ((line = br.readLine()) != null) {
			//			System.out.println(line);
			Matcher m1 = r1.matcher(line);
			Matcher m2 = r2.matcher(line);
			Matcher m3 = r3.matcher(line);
			if (m1.find()) {
				WebEnv = m1.group(1);
			}
			if (m2.find()) {
				QueryKey = m2.group(1);
			}
			if (m3.find() && Sequences == 0) {
				Sequences = Integer.parseInt(m3.group(1));
			}
		}
		System.out.println("Nº of sequences founded: " + Sequences);
		System.out.println("Limit: " + maxSeqs);

		if (maxSeqs != 0) {
			Sequences = maxSeqs;
		}
		if (Sequences > 50000) {//set a maximum of 50.000 seqs
			Sequences = 50000;
			System.out.println("limited to " + Sequences);
		}
		int maxRet = 1000;
		Map<String, String> DownloadParameters = new HashMap<>();
		DownloadParameters.put("WebEnv", WebEnv);
		DownloadParameters.put("db", "nuccore");
		DownloadParameters.put("query_key", QueryKey);
		DownloadParameters.put("retmax", Integer.toString(maxRet));
		DownloadParameters.put("rettype", "fasta");
		DownloadParameters.put("retmode", "text");
		DownloadParameters.put("usehistory", "true");
		File output = new File(outPath + File.separator + "Downloaded_seqs" + type.getFileExt());
		FileWriter fw = new FileWriter(output, true);
		BufferedWriter bw = new BufferedWriter(fw);
		for (int i = 0; i < Sequences; i += maxRet) {
			DownloadParameters.put("retstart", Integer.toString(i));
			System.out.println("Downloading");
			BufferedInputStream seqDownlaod = new ProxyAware(BASE + ncbiTool.download.getTool()).setConnectTimeout(15000)
			        .setParameters(DownloadParameters)
			        .setReadTimeout(15000)
			        .inputStream();
			System.out.println("Converting to String");
			String results = Utilities.convertStreamToString(seqDownlaod);
			System.out.println("Writing");

			bw.write(results);
			fw.flush();
			System.out.println("Done i: " + i);
//			try {
//				// wait 3 seconds between NCBI calls 
//				Thread.sleep(3000);
//				System.out.println("waiting...");
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
		bw.close();
		System.out.println("FINISHED!!! :D");
		return Sequences;
	}

	private static void ifExistDelete(String outpath) {
		String fileName = "Downloaded_seqs";
		for (searchType type : searchType.values()) {
			File oldFile = new File(outpath + File.separator + fileName + type.getFileExt());
			oldFile.delete();
		}
	}

}
