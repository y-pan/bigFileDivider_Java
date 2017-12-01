package yun.bigfile.rowColumnFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import yun.bigfile.divider.WriterRunnable;


public class RowFilter {
	public enum Option{SELECT,REMOVE};

	private String sourceFile = null;
	private String outDir = null;
	
	private String nameWithoutExt = null;
	private String ext = null;
	private String outFile = null;
	
	private Option option;
	
	private Map<Integer, String> filters; // <columnIndex, columnTextTriggerRowSelectionOrRemoval>, 
	
	private int maxCachedLineCount;
	
	public RowFilter(String sourceFullPath, String outDirFullPath, HashMap<Integer, String> filters, Option option) throws FileNotFoundException {
		
		this.sourceFile = sourceFullPath;
		if(outDirFullPath != null && outDirFullPath.length() > 0) {
			this.outDir = outDirFullPath.replace("\\", "/").replace("/" , File.separator);
		}

		this.option = option;
		this.filters = filters;
		maxCachedLineCount = 800;
		
		parseSourceFileName();
	}
	
	public void setMaxCachedLineCount(int maxCachedLineCount) {
		if(maxCachedLineCount > 1 ) {
			this.maxCachedLineCount = maxCachedLineCount;
		}
	}
	
	public void Run() {
		
		boolean isHeadLine = true;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(this.sourceFile), "utf-8"))) {
			//String headerRow = "";
			
			String line = "";
			StringBuilder content = new StringBuilder();
			int lineCount = 0;
			int[] columnIndexs = new int[this.filters.size()];
			
			Writer writer = new Writer(this.outFile,true);
			
			int _i = 0;
			for(Integer colIndex : this.filters.keySet()) {
				columnIndexs[_i++] = colIndex;
				System.out.println("col="+colIndex);
			}
			
			while ((line = br.readLine()) != null) {
				lineCount++;
				if(isHeadLine) {
					isHeadLine = false;
					content.append(line+"\n");
				}else {
					String[] arr = line.split(",");
					
					// -------------------------------------
					if(this.option == Option.REMOVE) {		// filter is REMOVE, 1 occurs then exclude the line
						boolean shouldReomve = false; 
						for(int i : columnIndexs) {
							String filterText = this.filters.get(i);
							if(arr[i].equalsIgnoreCase(filterText) || arr[i].equalsIgnoreCase("\""+filterText+"\"")) {
								// equal -> to remove -> not to be included into result file
								shouldReomve = true;
								break; // skip this line, 
							}
						}
						// no filter triggered, then this line should be included into result file
						if(!shouldReomve) {
							content.append(line+"\n");
						}
						
					} else {									// filter is select, 1 occurs then include the line
						for(int i: columnIndexs) {
							String filterText = this.filters.get(i);
							if(arr[i].equalsIgnoreCase(filterText) || arr[i].equalsIgnoreCase("\""+filterText+"\"")) {
								content.append(line+"\n"); 		// if someone selector appears, then select the line to be included into result file
								break;
							}
						}
					}
					// ------------------------------------
					if(lineCount >= this.maxCachedLineCount) {
						writer.setContent(content.toString()).run();
						content.setLength(0);
						lineCount = 0;
					}
				}	
			}
			// end of file, whatever in the content (StringBuilder), put into result file
			if(content.length() > 0) {
				writer.setContent(content.toString()).run();
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	

	/*------------------ private helper --------------------*/
	private void parseSourceFileName() throws FileNotFoundException{
		File f = new File(this.sourceFile);
		if(!f.isFile()) throw new FileNotFoundException();
		String name = f.getName();
		int dotIndex = name.lastIndexOf('.');
	    if (dotIndex == -1) {
	    	this.nameWithoutExt = name;
	    	this.ext = "";
	    }else {
	    	this.nameWithoutExt = name.substring(0, dotIndex);
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
	    
	    this.outFile = this.outDir + File.separator + this.nameWithoutExt + "_"+this.option.toString() + this.ext;
	}
}
