package com.shilc.cacher.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class StepLogLoader {

	private StepLib stepLib;
	private SimpleDateFormat sdf;
	
	public StepLogLoader(StepLib stepLib, String sdfFormat) {
		init(stepLib, sdfFormat);
	}
	
	public StepLogLoader(StepLib stepLib) {
		init(stepLib, "yyyy-MM-dd HH:mm:ss.SSS");
	}
	
	private void init(StepLib stepLib, String sdfFormat) {
		this.stepLib = stepLib;
		this.sdf = new SimpleDateFormat(sdfFormat);
	}
	
	/**
	 * 从csv文件中载入stepLogs
	 * @param csvFile
	 * @return
	 */
	public List<StepLog> loadFromCSV(File csvFile) {
		try {
			if(csvFile != null && csvFile.canRead()) {
				BufferedReader reader = new BufferedReader(new FileReader(csvFile));
				try {
					List<StepLog> logs = new ArrayList<StepLog>();

					String line;
					while((line = reader.readLine()) != null) {
						String[] args = line.split(",");
						if(args.length == 3) {
							try {
								logs.add(loadOne(args[0], args[1], sdf.parse(args[2]).getTime()));
							} catch (ParseException e) {
								System.out.println("Load from CSV: Error format for date: "+args[2]);
							}
						}else{
							System.out.println("Load from CSV: Error format for line: "+line);
						}
					}
					
					return logs;
				}finally {
					reader.close();
				}
			}else {
				throw new RuntimeException("File "+csvFile+" can't be read.");
			}
		}catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private StepLog loadOne(String stepID, String userID, long logTime) {
		
		if(stepLib.getStep(stepID) == null) {
			throw new RuntimeException("Invalid stepID: "+stepID);
		}
		return new StepLog(stepID, userID, logTime);
		
	}
	
}
