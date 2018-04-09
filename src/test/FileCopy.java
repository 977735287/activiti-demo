package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class FileCopy {

	public static void main(String[] args) {
		File f = new File("D:\\basic file\\test\\test.txt");
		if (f.exists()) {
			File cf = new File("D:\\basic file\\test\\copy_test.txt");
			try {
				if (cf.exists())
					cf.createNewFile();
				copy(f,cf);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("file not exists!");
		}
	}
	
	public static void copy(File file1,File file2) {
		try {
			FileInputStream fis = new FileInputStream(file1);
			FileOutputStream fos = new FileOutputStream(file2);
			InputStreamReader isr = new InputStreamReader(fis,"utf8");
			OutputStreamWriter ior = new OutputStreamWriter(fos,"utf8");
			
			BufferedReader br = new BufferedReader(isr);
			PrintWriter pw = new PrintWriter(ior,true);
			
			String line;
			while((line = br.readLine()) != null) {
				pw.println(line);
			}
			pw.flush();
			br.close();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
