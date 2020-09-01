package dao;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class csvReader {
	public ArrayList<String> readFile(String link) throws Exception{
		ArrayList<String> results = null;
		try {
//			InputStream input = socket.getInputStream();
//			BufferedReader reader = new BufferedReader ( new InputStreamReader(input,StandardCharsets.UTF_8));
			FileInputStream fis = new FileInputStream(link);
			//DataInputStream myInput = new DataInputStream(fis);
			BufferedReader input =new BufferedReader (new InputStreamReader(fis, StandardCharsets.UTF_8));
			input.readLine();
			String thisLine;
			//myInput.readLine();
			while((thisLine = input.readLine()) != null){
				if(results == null) results = new ArrayList<String>();
				results.add(thisLine);
			}
			input.close();
		}catch(IOException e) {
			e.getStackTrace();
		}
		return results;
	}
}
