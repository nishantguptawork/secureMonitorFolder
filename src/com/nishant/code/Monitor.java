package com.nishant.code;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;

public class Monitor implements Job {

	private String monitorFolder;
	private long thresholdSize;

	public void makeSecure() throws IOException {
		System.out.println("executing MONITOR");
		removeExecutables();
		System.out.println("monitorFolder size is : " + getfolderSize());
		archive();

	}

	private long getfolderSize() {
		try {
			return Files.walk(Paths.get(monitorFolder)).mapToLong(p -> p.toFile().length()).sum();
		} catch (IOException e) {
			System.out.println("Unable to size of monitorFolder: secured");
			e.printStackTrace();
		}
		return 0;
	}

	private void removeExecutables() throws IOException {
		Files.walk(Paths.get(monitorFolder)).filter(
				p -> p.toString().endsWith(".exe") || p.toString().endsWith(".sh") || p.toString().endsWith(".bash"))
				.forEach(execeutableFile -> {
					try {
						Files.delete(execeutableFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
	}

	private Queue<File> filesSortedCreationTime() {
		File monitorFolderFile = new File(monitorFolder);
		File[] filesList = monitorFolderFile.listFiles();
		Arrays.sort(filesList, new Comparator<File>() {
			@Override
			public int compare(File firstFile, File secondFile) {
				long date1 = 0;
				long date2 = 0;
				try {
					date1 = getCreationTime(firstFile).toMillis();
					date2 = getCreationTime(secondFile).toMillis();
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
		});
		// converting array to queue using linked list implementation
		return new LinkedList<File>(Arrays.asList(filesList));
	}

	private void archive() {
		if (getfolderSize() > thresholdSize) {
			Queue<File> sortedFilesQueue = filesSortedCreationTime();
			while (getfolderSize() > thresholdSize) {
				try {
					Files.delete(Paths.get(sortedFilesQueue.remove().getAbsolutePath()));
				} catch (IOException e) {
					System.out.println("Unable to delete file to reduce size from threshold size");
					e.printStackTrace();
				}
				;
			}
		}

	}

	private FileTime getCreationTime(File file) throws IOException {
		Path p = Paths.get(file.getAbsolutePath());
		BasicFileAttributes view = Files.getFileAttributeView(p, BasicFileAttributeView.class).readAttributes();
		FileTime fileTime = view.creationTime();
		// also available view.lastAccessTine and view.lastModifiedTime
		return fileTime;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		this.loadContextVariables(context);
		try {
			this.makeSecure();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadContextVariables(JobExecutionContext context) {
		SchedulerContext schedulerContext = null;
		try {
			schedulerContext = context.getScheduler().getContext();
		} catch (SchedulerException e1) {
			e1.printStackTrace();
		}
		this.monitorFolder = (String) schedulerContext.get("monitorFolder");
		this.thresholdSize = (long) schedulerContext.get("monitorThreshold");
	}

}
