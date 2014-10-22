/**
 * 
 */
package com.aerospike.indexkeyreaderwriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;


/**
 * File reader class to read digest/hash keys from the AS backup files.
 * 
 * @author anandprakash
 */
public class BackupFileReader {
	public boolean debug;
	int count=0;
	private  static String filesFromDir ;
	
	public BackupFileReader(String[] commandLineArgs) throws Exception {
		Options options = new Options();
		options.addOption("BKF", "backupFilesPath", true, "Backup files path to read the digest/index keys.");
		options.addOption("NF", "newKeyFilePath", true, "New file with digest/index Keys from all the backup files.");
		
		CommandLineParser parser = new PosixParser();
		CommandLine cl = parser.parse(options, commandLineArgs, false);
		
		if (cl.hasOption("u")) {
			logUsage(options);
			throw new UsageException();
		}
		
		if (cl.hasOption("backupFilesPath")) {
			filesFromDir = cl.getOptionValue("backupFilesPath");
		}
		
		if (cl.hasOption("newKeyFilePath")) {
			FileProcessor.fileToWrite = cl.getOptionValue("newKeyFilePath");
		}

	}

	public static void main(String[] args) {
		BackupFileReader fileReaderObj = null;
		
		try {
			
			fileReaderObj = new BackupFileReader(args);
			List<File> fileList = fileReaderObj.listFilesFromDirectory(new File(filesFromDir));
			System.out.println("Number of files in the directory are "+fileList.size());
			
			if(fileList !=null && fileList.size() >0){
				fileReaderObj.readKeysFromFiles(fileList);
				System.out.println("File processing is done successfully !");
			}else{
				System.out.println("File processing is not done,file size is- "+fileList.size());
			}
			
		}
		catch (UsageException ue) {
		}
		catch (ParseException pe) {
			System.out.println(pe.getMessage());
			System.out.println("Use -u option for program usage");		
		}
		catch (Exception e) {		
			System.out.println("Error: " + e.getMessage());
			
			if (fileReaderObj != null && fileReaderObj.debug) {
				e.printStackTrace();
			}
		}
	}
	
/*	public List<Path> readFilesFromDirectory(String folder) throws IOException {
		List<Path>  fileLists = new ArrayList<Path>();
		Path dir = Paths.get(folder);
		try (DirectoryStream<Path> stream =
		     Files.newDirectoryStream(dir,"*.{asb}")) {   //We can set different file extn like-newDirectoryStream(dir, "*.{txt,csv}"))
		    for (Path entry: stream) {
		    	
		        fileLists.add(entry.getFileName());
		    }
		} catch (IOException x) {
			throw new IOException("Directory path '"+folder+"' or file is not available");
		}
		return fileLists;
	}*/
	
	 public List<File> listFilesFromDirectory(final File folder) {
		  List<File> fileList = new ArrayList<File>();
		    File[] filesInFolder = folder.listFiles();
		    if (filesInFolder != null) {
		        for (final File fileEntry : filesInFolder) {
		            if (fileEntry.isDirectory()) {
		            	listFilesFromDirectory(fileEntry);
		            } else {
		            	if(fileEntry.getName().endsWith(".asb"))
		            		fileList.add(fileEntry);
		            }
		        }
		    }
			return fileList;
	 }
	
	/**
	 * Read all the contents from the file and put it in a List.
	 * @throws Exception 
	 */
	private void readKeysFromFiles(List<File> files) throws Exception {
		
		for(File file : files){
			List<String> contents = getContentsFromFile(new File(filesFromDir+"/"+file.getName()));
			count = contents.size();
			for(String line: contents){
				FileProcessor.writeTofile(line);
	    	}
			System.out.println("Total number of keys are '"+count+"' in the file '"+file.getName()+"'");
		}
	}
	
    static public List<String> getContentsFromFile(File aFile) {

    	 List<String>  hashKeyList = new ArrayList<String>();
        
        try {
          BufferedReader input =  new BufferedReader(new FileReader(aFile));
          try {
            String line = null; 
            
            while (( line = input.readLine()) != null){
            	
            	if(FileProcessor.processFile(line) !=null)
            		hashKeyList.add(FileProcessor.processFile(line));
            }
          }
          finally {
        	  try{
          		if(input != null)
          			input.close();
                                 
                  }catch(IOException ioe)
                  {
                      System.out.println("Error while closing the stream : " + ioe);
                  }
          }
        }
        catch (IOException ex){
          ex.printStackTrace();
        }
        
        return hashKeyList;
      }
    
	private static void logUsage(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		String syntax = BackupFileReader.class.getName() + " [<options>]";
		formatter.printHelp(pw, 100, syntax, "options:", options, 0, 2, null);

		System.out.println(sw.toString());
	}
	
	private static class UsageException extends Exception {
		private static final long serialVersionUID = 1L;
	}

}
