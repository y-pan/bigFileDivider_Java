package yun.bigfile.rowColumnFilter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Writer { // not intend to be a runnable, because only one file in one operation, content must be output to file in order.

	private String file;
	private String content;
	private boolean isAppend;
	

	public Writer(String writeToFile, boolean isAppend) {
		this.file = writeToFile;
		this.isAppend = isAppend;
	}
	
	public Writer(String writeToFile, String content, boolean isAppend) {
		this.file = writeToFile;
		this.content = content;
		this.isAppend = isAppend;
	}
	
	public void run() {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, isAppend))) {
			bw.write(content);
			System.out.println("Done -> "+ file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getFile() {
		return file;
	}

	public String getContent() {
		return content;
	}

	public boolean isAppend() {
		return isAppend;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public Writer setContent(String content) {
		this.content = content;
		return this;
	}

	public void setAppend(boolean isAppend) {
		this.isAppend = isAppend;
	}


}
