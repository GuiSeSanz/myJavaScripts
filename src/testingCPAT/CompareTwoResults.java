package testingCPAT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompareTwoResults {
	public enum Paths {
		inPathUntrained("/home/guillermo/Files/Coding_Potential/Comparing/BOAR/CPAT/Untrained/ClassifiedResults.txt"),
		inPathTrained("/home/guillermo/Files/Coding_Potential/Comparing/BOAR/CPAT/Trained/ClassifiedResults.txt");
		private String path;

		private Paths(String path) {
			this.path = path;
		}

		public String getPath() {
			return path;
		}
	}

	public static void main(String[] args) throws IOException {

		BufferedReader br;
		Set<String> untrainedNonCoding = new HashSet<String>();
		Set<String> untrainedCoding = new HashSet<String>();
		Set<String> trainedNonCoding = new HashSet<String>();
		Set<String> trainedCoding = new HashSet<String>();
		/*
		 * We read the classification files. First lines are statistics coding sequences are found after
		 * ------------>"==================Coding sequences==================" non coding sequences are found after
		 * -------->"=================nonCoding sequences=================" example of line: "ID: 2777 With a CP score: 0.12416411"
		 */
		br = new BufferedReader(new FileReader(new File(Paths.inPathUntrained.getPath())));
		retrieveIDs(br, untrainedNonCoding, untrainedCoding);
		br = new BufferedReader(new FileReader(new File(Paths.inPathTrained.getPath())));
		retrieveIDs(br, trainedNonCoding, trainedCoding);
		System.out.println("caraculo");
		
		
	}

	private static void retrieveIDs(BufferedReader br, Set<String> untrainedNonCoding, Set<String> untrainedCoding) throws IOException {
		String line;
		String pattern = "ID: (\\d+) With";
		Pattern r = Pattern.compile(pattern);
		String ID = "NA";
		boolean Coding = true;
		while ((line = br.readLine()) != null) {
			if (line.contains("=======nonCoding sequences=========")) {
				Coding = false;
				continue;
			}
			Matcher m = r.matcher(line);
			if (m.find()) {
				ID = m.group(1);
			}
			if (Coding && ID != "NA") {
				untrainedCoding.add(ID);
			}else if(ID != "NA"){
				untrainedNonCoding.add(ID);
			}
		}

		br.close();
	}

}
