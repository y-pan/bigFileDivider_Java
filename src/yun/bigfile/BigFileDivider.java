package yun.bigfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BigFileDivider {

	private String sourceFile = null;
	
	private String outDir = null;
	private String nameWithoutExt = null;
	private String ext = null;
	
	private Integer partCount = null;
	private Integer maxDataRowCountInPart = null; 	// header doesn't count, every parts will have the same header
	
	private Path sourceFile_Path = null;
	
	public BigFileDivider(String sourceFullPath, String outDirFullPath) throws FileNotFoundException {
		
		this.maxDataRowCountInPart = 1000;
		this.sourceFile = sourceFullPath;
		this.sourceFile_Path = Paths.get(this.sourceFile);

		if(outDirFullPath != null && outDirFullPath.length() > 0) {
			this.outDir = outDirFullPath.replace("\\", "/").replace("/" , File.separator);
		}

		this.partCount = 0;
		parseSourceFileName(); // parse source file name to get, also if outDir is null, set it to the parent of sourceFile, so sourceFile and partFiles are in the same directory
		// I know it is bad design to modify variable in multiple places, just leave it for now
	}
	
	public void setMaxDataRowCountInPart(int max) {
		if(max<1) return;
		this.maxDataRowCountInPart = max;
	}

	public void Run() {
		boolean isHeadLine = true;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(this.sourceFile), "utf-8"))) {
			String headerRow = "", line = "";
			
			StringBuilder content = new StringBuilder();
			int lineCount = 0;

			while ((line = br.readLine()) != null) {
				if(isHeadLine) {
					headerRow = line;
					isHeadLine = false;
				}else {
					content.append(line+"\n");
					if(++lineCount >= maxDataRowCountInPart) {  // here ensures only maxDataRowCountInPart lines plus a header row in a part file, at most
						String c = headerRow + "\n"+ content.toString();
						new Thread(new WriterRunnable(getPartFileName(++partCount),c,true)).start();
						content.setLength(0);
						lineCount = 0;
					};
				}
					
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	
	/* ------------------ private helper -------------------------- */
	private String getPartFileName(int partCount) {
		return this.outDir + File.separator+ this.nameWithoutExt + "_" + partCount + this.ext;		
	}

	private void parseSourceFileName() throws FileNotFoundException{
		File f = new File(this.sourceFile);
		if(!f.isFile()) throw new FileNotFoundException();
		String name = f.getName();
		int dotIndex = name.lastIndexOf('.');
	    if (dotIndex == -1) {
	    	this.nameWithoutExt = name;
	    	this.ext = "";
	    }else {
	    	this.nameWithoutExt = name.substring(0, dotIndex-1);
	    	this.ext = name.substring(dotIndex); // would be like:".csv"
	    }
	    
	    if(this.outDir == null) {
	    	this.outDir = f.getParent().toString();
	    }else {
	    	File o = new File(this.outDir);
	    	if(!o.exists()) {
	    		o.mkdirs();
	    	}
	    	
	    }
	}
}
