package tw.lachu.MineProfession;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;

public class SerialData {
	protected boolean save(Object data, File file){
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(data);
			oos.flush();
			oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	protected Object load(File file){
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			Object obj = ois.readObject();
			ois.close();
			return obj;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
	}
	
	protected File getBackupFile(String extendName, File originalFile){
		Calendar currentTime = Calendar.getInstance();
		StringBuilder sb = new StringBuilder();
		sb.append(currentTime.get(Calendar.YEAR));
		sb.append("_");
		sb.append(currentTime.get(Calendar.MONTH+1));
		sb.append("_");
		sb.append(currentTime.get(Calendar.DATE));
		sb.append("_");
		sb.append(currentTime.get(Calendar.HOUR_OF_DAY));
		sb.append("_");
		sb.append(currentTime.get(Calendar.MINUTE));
		sb.append("_");
		sb.append(currentTime.get(Calendar.SECOND));
		sb.append(".");
		sb.append(extendName);
		File backDir = new File(originalFile.getParentFile(),"backup");
		backDir.mkdir();
		return new File(backDir, sb.toString() );
	}
}
