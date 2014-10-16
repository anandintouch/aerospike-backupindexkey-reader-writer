/**
 * 
 */
package com.aerospike.indexkeyreaderwriter;

import gnu.crypto.util.Base64;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * This class parse and process the incoming file and write the keys to the new 
 * file "IndexKeyFile.txt"
 *  
 * @author anandprakash
 *
 */
public class FileProcessor {
	
	// Default location of IndexKeyFile is the current directory if the path is not passed
	public static String fileToWrite = "./IndexKeyFile.txt";
	private static final String INDEXKEYFILE = "IndexKeyFile.txt";
	
    public static String processFile(String line) {
    	String hashKey = null;

	   	 if(line != null && !line.isEmpty()) {
				if(line.charAt(2)=='d'){
					String tokens[] =line.split(" ");
					hashKey = tokens[2];
				}
	   	 }
		return hashKey;
    }
    
    /**
     * Decode Key data by processing Base64 encoded data .
     */
    private static String decodeKey(String key) throws UnsupportedEncodingException{
		byte[] valueDecoded= Base64.decode(key );
		String decodedValue = utf8ToString(valueDecoded,0,valueDecoded.length);
		//String stringFromBase = new String(Base64.decode(valueDecoded, Base64.DEFAULT));
		System.out.println("Decoded value is " +decodedValue );
		return decodedValue;

    }
    
    /**
     * Write the line to new file.
     * 
     * @param line
     * @throws Exception
     */
    public static void writeTofile(String line) throws Exception {
    	BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(new File(fileToWrite), true));
	       //bw.write(decodeKey(line));
	        bw.write(line);
	        bw.newLine();
	        bw.close();
		} catch (IOException e) {
			throw new Exception("Error while writing to a file\n"+e.getMessage());
		}

    }
    
    public static String utf8ToString(byte[] buf, int offset, int length) {
    	// A Thread local implementation does not help here, so  
    	// allocate character buffer each time.  
		char[] charBuffer = new char[length];
    	int charCount = 0;
        int limit = offset + length;
    	int origoffset = offset;

        while (offset < limit ) {
        	int b1 = buf[offset];
        	
        	if (b1 >= 0) {
                charBuffer[charCount++] = (char)b1;
                offset++;        		
        	}
        	else if ((b1 >> 5) == -2) {
        		int b2 = buf[offset + 1];
        		charBuffer[charCount++] = (char) (((b1 << 6) ^ b2) ^ 0x0f80);
                offset += 2;
        	}
		    else {
		    	// Encountered an UTF encoding which uses more than 2 bytes. 
		    	// Use a native function to do the conversion.
		    	try {
		    		return new String(buf, origoffset, length, "UTF8");
		    	}
		    	catch (UnsupportedEncodingException uee) {
            		throw new RuntimeException("UTF8 decoding is not supported.");
		    	}
		    }
        }
        return new String(charBuffer, 0, charCount);
    }

}
