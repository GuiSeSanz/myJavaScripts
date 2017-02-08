package patternScannerCRISPR;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CrisprScanner {

	public static void main(String[] args) throws IOException {
		
		String filePath = "/home/guillermo/Downloads/sequence(6).fsa";
		File file = File.createTempFile("temp_", ".fsa");
		Path pathToDelete = Paths.get(file.toString());
		BufferedWriter bw = new BufferedWriter( new FileWriter(file));
		
		List<File> fastaPathList = new ArrayList<>();
		String line;
		BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
		while((line = br.readLine())!= null){
			if (line.startsWith(">")) {
				bw.close();
				String fastaId = line.replaceAll(" ", "").trim().substring(1);
				file = File.createTempFile(fastaId, ".fsa");
				fastaPathList.add(file);
				//file.deleteOnExit();
				System.out.println(file.toString());
				bw = new BufferedWriter( new FileWriter(file));
				bw.write(line);
				bw.newLine();
			} else {
				bw.write(line);
				bw.newLine();
			}
		}
		bw.close();
		br.close();
		Files.delete(pathToDelete);
		System.out.println(fastaPathList.size());
		
		for (File file2 : fastaPathList) {
			
			System.out.println("Launching here the CRISPR tool with " + file2.toString());
		}
	}

}
