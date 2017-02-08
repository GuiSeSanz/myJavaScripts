package retrieveFromNCBI;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.biobam.blast2go.basic_utilities.Utilities;
import com.biobam.blast2go.preferences.proxy.ProxyAware;

public class Download {

	private static enum ncbiTool {
		post("epost.fcgi"),
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
		// TODO Auto-generated method stub

		String filePath = "/home/guillermo/Downloads/user_gene_ids_salmo.txt";
		List<String> idList = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));

		String line;

		while ((line = br.readLine()) != null) {
			idList.add(line.trim());
		}
		br.close();

		if (idList.isEmpty()) {
			return;
		}

		String searchList = String.join(",", idList);
		System.out.println(searchList);
		String BASE = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/";
		Map<String, String> searchParameters = new HashMap<String, String>();
		searchParameters.put("tool", "blast2go");
		searchParameters.put("email", "support@blast2go.com");
		searchParameters.put("db", "gene");
		searchParameters.put("id", searchList);
		searchParameters.put("usehistory", "y");

		BufferedInputStream seqSearch = new ProxyAware(BASE + ncbiTool.post.getTool()).setConnectTimeout(15000)
		        .setParameters(searchParameters)
		        .setReadTimeout(15000)
		        .inputStream();

		String line1;
		BufferedReader br1 = new BufferedReader(new InputStreamReader(seqSearch));
		/*
		 * Now we parse the search results to download the sequences We use the WebEnv as the second web search (uses the previous search history)
		 */

		String WebEnvPattern = "<WebEnv>(\\S+)<\\/WebEnv>";
		String QueryKeyPattern = "<QueryKey>(\\d+)<\\/QueryKey>";
		Pattern r1 = Pattern.compile(WebEnvPattern);
		Pattern r2 = Pattern.compile(QueryKeyPattern);
		String WebEnv = "";
		String QueryKey = "";
		while ((line1 = br1.readLine()) != null) {
			System.out.println(line1);
			//			System.out.println(line);
			Matcher m1 = r1.matcher(line1);
			Matcher m2 = r2.matcher(line1);
			if (m1.find()) {
				WebEnv = m1.group(1);
			}
			if (m2.find()) {
				QueryKey = m2.group(1);
			}
		}
		br1.close();

		Map<String, String> DownloadParameters = new HashMap<>();
		DownloadParameters.put("WebEnv", WebEnv);
		DownloadParameters.put("db", "gene");
		DownloadParameters.put("query_key", QueryKey);
		DownloadParameters.put("retmax", Integer.toString(1000));
		DownloadParameters.put("rettype", "fasta");
		DownloadParameters.put("retmode", "text");
		DownloadParameters.put("usehistory", "y");

		File output = File.createTempFile("Downloaded_seqs", ".fsa");
		//		output.deleteOnExit();
		//		File output = new File(outPath + File.separator + "Downloaded_seqs" + type.getFileExt());
		BufferedWriter bw = new BufferedWriter(new FileWriter(output));
		BufferedInputStream seqDownlaod = new ProxyAware(BASE + ncbiTool.download.getTool()).setConnectTimeout(15000)
		        .setParameters(DownloadParameters)
		        .setReadTimeout(15000)
		        .inputStream();

		String results = Utilities.convertStreamToString(seqDownlaod);
		bw.write(results);

		bw.close();

	}

}
