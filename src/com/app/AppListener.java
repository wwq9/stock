package com.app;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
@WebListener
public class AppListener implements ServletContextListener {
	Timer timer = new Timer();
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		timer.cancel();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		runQuarter();
	}

	private void runQuarter() {
		TimerTask quarter = new TimerTask() {
			public void run() {
				Calendar c = Calendar.getInstance();
				int week = c.get(Calendar.DAY_OF_WEEK);
				if(week!=Calendar.SUNDAY||week!=Calendar.SATURDAY){
					Stock stock = new Stock();
					stock.runQuarter();
					stock.runIndustry();
				}
			}
		};
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 15);
		c.set(Calendar.MINUTE, 10);
		
		timer.scheduleAtFixedRate(quarter, c.getTime(), 24*60*60*1000);
	}

}
