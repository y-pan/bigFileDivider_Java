package yun.bigfile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class WriterRunnable implements Runnable{

	private String file;
	private String content;
	private boolean isAppend;
	
	public WriterRunnable(String file, String content, boolean isAppend) {
		this.file = file;
		this.content = content;
		this.isAppend = isAppend;
	}
	
	@Override
	public void run() {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, isAppend))) {
			bw.write(content);
			System.out.println("Done -> "+ file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
