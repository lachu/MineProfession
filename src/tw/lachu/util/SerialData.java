package tw.lachu.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;

public class SerialData<T> {
	public boolean save(T data, File file){
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(data);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean save(T data, File file, boolean backup){
		if(backup){
			save(data, getBackupFile(file.getName(), file));
		}
		return save(data, file);
	}
	
	public File getBackupFile(String extendName, File originalFile){
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
		File backDir = new File(originalFile.getParentFile(),"Backup");
		backDir.mkdir();
		return new File(backDir, sb.toString() );
	}

	@SuppressWarnings("unchecked")
	public T load(File file){
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			T obj = (T)ois.readObject();
			ois.close();
			return obj;
		} catch (Exception e) {
			return null;
		} 
	}
}
