package cn.jlu.ge.dreamclock.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.os.Environment;

public class FileIOTools extends Activity {
	
	private String sdPath;//sd����Ŀ¼path
	
	public String getSdPath() {
		//�õ��洢�豸��Ŀ¼
		return sdPath = Environment.getExternalStorageDirectory() + "/";
	}
	
	public boolean checkFileExists(String filePath) {
		//�ж��ļ��Ƿ����
		File file = new File(sdPath + filePath);//�ļ�����ʵĿ¼�ṹ
		return file.exists();
	}
	
	public File createDIR(String dirPath) {
		//��SD���ϴ���Ŀ¼��ͨ��File�����mkdir()����ʵ��
		File dir = new File(sdPath + dirPath);
		dir.mkdir();
		return dir;
	}
	
	public File createFile(String filePath) throws IOException {
		//��SD���ϴ����ļ���ͨ��File�����createNewFile()����ʵ��
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
			//����Ŀ¼
			createDIR(dirPath);
			
			//�ڴ�����Ŀ¼�ϴ����ļ�
			file = createFile(dirPath + fileName);
			output = new FileOutputStream(file);
			byte[]bt = new byte[4 * 1024];
			while(input.read(bt) != -1) {
				output.write(bt);
			}
			
			//ˢ�»���
			output.flush();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			
		} finally {
			try {
				output.close();//�ر������
				
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		return file;
	}
	
}
