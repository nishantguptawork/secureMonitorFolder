package com.nishant.code;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

public class FileCreationTimeComparator implements Comparator<File> {

	public int compare(File f0, File f1) {
		long date1 = 0;
		long date2 = 0;
		try {
			date1 = FileCreationTime.getCreationTime(f0).toMillis();
			date2 = FileCreationTime.getCreationTime(f1).toMillis();
		} catch (IOException e) {
			System.out.println("Not able to get the file creation time");
			e.printStackTrace();
		}

		if (date1 > date2)
			return 1;
		else if (date2 > date1)
			return -1;

		return 0;
	}
}
