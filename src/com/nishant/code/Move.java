package com.nishant.code;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;

public class Move implements Job {

	private String inputFolder;
	private String outputFolder;
	// private String archiveFolder;

	private void listf(String directoryName, ArrayList<File> files) throws IOException {
		File directory = new File(directoryName);

		// get all the files from a directory
		File[] fList = directory.listFiles();

		for (File file : fList) {
			if (file.isFile()) {
				files.add(file);
			} else if (file.isDirectory()) {
				listf(file.getAbsolutePath(), files);
			}

		}
	}

	// private Boolean ifExistsInArchiveFolder(File fileToCheck) {
	// File[] archiveFilesList = new File(archiveFolder).listFiles();
	// return Arrays.asList(archiveFilesList).contains(fileToCheck);
	// }

	private void moveFiles() throws IOException {
		System.out.println("executing MOVE");
		ArrayList<File> filesList = new ArrayList<File>();
		listf(inputFolder, filesList);
		for (File i : filesList) {
			System.out.println(i.getName());
			String fileName = i.getName();
			String source = i.getAbsolutePath();
			// replace with output folder path
			String dest = source.replaceAll(inputFolder, outputFolder);
			// get exact absolute path to the secured output folder
			dest = dest.substring(0, dest.indexOf(outputFolder)).concat(outputFolder).concat("\\").concat(fileName);
			try {
				// if (!ifExistsInArchiveFolder(i)) {
				// System.out.println("File not found in archive folder. Hence,
				// copying file " + i.getName()
				// + " to destination " + dest);
				Files.copy(Paths.get(source), Paths.get(dest));
				// } else {
				// System.out.println("File already exists in archive folder : "
				// + i.getName());
				// }

			} catch (FileAlreadyExistsException e) {
				System.out.println("File " + i.getName() + " already exists in the secured folder");
				// e.printStackTrace();
			}
		}
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		this.loadContextVariables(context);
		try {
			this.moveFiles();
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}

	private void loadContextVariables(JobExecutionContext context) {
		SchedulerContext schedulerContext = null;
		try {
			schedulerContext = context.getScheduler().getContext();
		} catch (SchedulerException e1) {
			e1.printStackTrace();
		}
		this.inputFolder = (String) schedulerContext.get("inputFolder");
		this.outputFolder = (String) schedulerContext.get("outputFolder");
		// this.archiveFolder = (String) schedulerContext.get("archiveFolder");
	}

}
