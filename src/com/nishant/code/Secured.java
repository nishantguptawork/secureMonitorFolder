package com.nishant.code;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.IOException;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 
 */

/**
 * @author nishant.gupta23
 *
 */

public class Secured {

	private static final String inputFolder = "temp";
	private static final String outputFolder = "secured";
	private static final long thresholdSize = 25;// 100 MB = 104857600 bytes

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) {
		// Move moveObj = new Move(inputFolder, outputFolder);
		// Monitor monitorObj = new Monitor(outputFolder);
		// try {
		// moveObj.moveFiles();
		// // moveObj.printFiles();
		// // monitorObj.makeSecure();
		// // monitorObj.recieveFileNames();
		// // moveObj.emitFileName();
		//
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		try {
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

			// Starting scheduler
			scheduler.start();

			JobDetail moveJob = newJob(Move.class).withIdentity("moveJob", "secureGroup").build();
			JobDetail monitorJob = newJob(Monitor.class).withIdentity("monitorJob", "secureGroup").build();

			// Trigger the job to run now, and then repeat every 10 seconds
			Trigger moveTrigger = newTrigger().withIdentity("moveTrigger", "secureGroup").startNow()
					.withSchedule(simpleSchedule().withIntervalInSeconds(10).repeatForever()).build();

			// Trigger the job to run now, and then repeat every 15 seconds
			Trigger monitorTrigger = newTrigger().withIdentity("mointorTrigger", "secureGroup").startNow()
					.withSchedule(simpleSchedule().withIntervalInSeconds(15).repeatForever()).build();

			// adding variables to context to be passed on to relevant jobs
			scheduler.getContext().put("inputFolder", inputFolder);
			scheduler.getContext().put("outputFolder", outputFolder);

			scheduler.getContext().put("monitorFolder", outputFolder);
			scheduler.getContext().put("monitorThreshold", thresholdSize);

			// schedule the relevant jobs
			scheduler.scheduleJob(moveJob, moveTrigger);
			scheduler.scheduleJob(monitorJob, monitorTrigger);

			// Commenting out shutdown because we dont want the scheduler to be
			// shut down

			// scheduler.shutdown();

		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

}
