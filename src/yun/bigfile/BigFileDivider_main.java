package yun.bigfile;

import java.io.FileNotFoundException;

public class BigFileDivider_main {

	public static void main(String[] args) {
 		String source = "C:\\Users\\yun\\Desktop\\source\\sourceSample.csv";
 		String outDir =  "C:\\Users\\yun\\Desktop\\source\\parts1";
		
		try {
			BigFileDivider div = new BigFileDivider(source, outDir);
			//div.setMaxDataRowCountInPart(5); // default 1000, so parts files will have maximum 1000 data lines plus a header line
			div.Run();
			
			System.out.println("main end");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
