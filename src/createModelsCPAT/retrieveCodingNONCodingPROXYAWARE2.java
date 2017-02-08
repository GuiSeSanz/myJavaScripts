
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.biobam.blast2go.basic_utilities.Utilities;
import com.biobam.blast2go.preferences.proxy.ProxyAware;

public class retrieveCodingNONCodingPROXYAWARE2 {

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
		String Sp = "Gallus gallus"; //<- given by user
		String outPath = "/home/guillermo/Desktop/Temporal"; //<- to tmp folder
		String ORGANISM = "\"" + Sp + "\"[Organism])";
		/*
		 * First: Download the ncRNA and keep the number of sequences
		 */

		final int maxSeqs = searchAndDownload(searchType.nonCoding, ORGANISM, outPath, 0);

		ExecutorService executor = Executors.newFixedThreadPool(2);

		executor.execute(() -> {
			try {
				searchAndDownload(searchType.Coding_cds, ORGANISM, outPath, maxSeqs);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		executor.execute(() -> {
			try {
				searchAndDownload(searchType.Coding_mrna, ORGANISM, outPath, maxSeqs);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
		  
		}
	}

	private static int searchAndDownload(searchType type, String organism, String outPath, int maxSeqs) throws MalformedURLException, IOException {
		Thread.currentThread()
		        .setName(type + "Seeker&Downloader");
		String BASE = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/";
//		int retmax = 100;
		Map<String, String> searchParameters = new HashMap<String, String>();
		searchParameters.put("tool", "blast2go");
		searchParameters.put("email", "support@blast2go.com");
		searchParameters.put("db", "nuccore");
		searchParameters.put("term", organism + type.getType());
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
//		if (maxSeqs != 0) {
//			Sequences = maxSeqs;
//		}
		int maxRet = 1000;
		Map<String, String> DownloadParameters = new HashMap<>();
		DownloadParameters.put("WebEnv", WebEnv);
		DownloadParameters.put("db", "nuccore");
		DownloadParameters.put("query_key", QueryKey);
		DownloadParameters.put("retmax", Integer.toString(maxRet));
		DownloadParameters.put("rettype", "fasta");
		DownloadParameters.put("retmode", "text");
		DownloadParameters.put("usehistory", "y");
		File output = new File(outPath + File.separator + "Downloaded_seqs" + type.getFileExt());
		FileWriter fw = new FileWriter(output, true);
		BufferedWriter bw = new BufferedWriter(fw);
		for (int i = 0; i < Sequences; i += maxRet) {
			DownloadParameters.put("retstart", Integer.toString(i));
			BufferedInputStream seqDownlaod = new ProxyAware(BASE + ncbiTool.download.getTool()).setConnectTimeout(15000)
			        .setParameters(DownloadParameters)
			        .setReadTimeout(15000)
			        .inputStream();

			String results = Utilities.convertStreamToString(seqDownlaod);
			System.out.println("i " + i);
			bw.write(results);
			fw.flush();
			try {
				// wait 3 seconds between NCBI calls 
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		bw.close();
		System.out.println("FINISHED!!! :D");
	return Sequences;
	}

}
