package cn.jlu.ge.getup.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.os.Environment;

public class FileIOTools extends Activity {
	
	private String sdPath;//sd卡的目录path
	
	public String getSdPath() {
		//得到存储设备的目录
		return sdPath = Environment.getExternalStorageDirectory() + "/";
	}
	
	public boolean checkFileExists(String filePath) {
		//判断文件是否存在
		File file = new File(sdPath + filePath);//文件的真实目录结构
		return file.exists();
	}
	
	public File createDIR(String dirPath) {
		//在SD卡上创建目录，通过File对象的mkdir()方法实现
		File dir = new File(sdPath + dirPath);
		dir.mkdir();
		return dir;
	}
	
	public File createFile(String filePath) throws IOException {
		//在SD卡上创建文件，通过File对象的createNewFile()方法实现
		File file = new File(sdPath + filePath);
		file.createNewFile();
		return file;
	}
	
	
	/*
//	public void writeFile(String fileName,String writeStr){
//		try {
//			FileOutputStream fout;
//			fout = openFileOutput(fileName, MODE_PRIVATE|MODE_APPEND);
//			byte [] bytes = writeStr.getBytes();
//			try {
//				fout.write(bytes);
//				fout.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} 
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public String readFile(String fileName){
		String res="";
		try {
			FileInputStream fin = openFileInput(fileName);
			int length;
			try {
				length = fin.available();
				byte [] buffer = new byte[length];
				fin.read(buffer);
				res = EncodingUtils.getString(buffer, "UTF-8");
				fin.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return res; 
	}
	*/
	
	public File writeStreamToSDCard(String dirPath, String fileName, InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			//创建目录
			createDIR(dirPath);
			
			//在创建的目录上创建文件
			file = createFile(dirPath + fileName);
			output = new FileOutputStream(file);
			byte[]bt = new byte[4 * 1024];
			while(input.read(bt) != -1) {
				output.write(bt);
			}
			
			//刷新缓存
			output.flush();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			
		} finally {
			try {
				output.close();//关闭输出流
				
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		return file;
	}
	
}
