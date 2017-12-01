package yun.bigfile.rowColumnFilter;

import java.io.FileNotFoundException;
import java.util.HashMap;

import yun.bigfile.rowColumnFilter.RowFilter.Option;

public class RowColumnFilter_main {

	public static void main(String[] args) {
		String source = "s:\\Documents\\@Centennial College\\Centennial #5\\myStudy\\sample.csv";
		String outDir = null;
		HashMap selects = new HashMap<Integer, String>();
		selects.put(6, "Student");  // columnIndex(0-base) 6 is RoleName, have to be Student
		try {
			RowFilter rf = new RowFilter(source,null,selects, Option.SELECT); // SELECT or REMOVE any matches, 
			rf.Run();
			
			System.out.println("Main end");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}
}
