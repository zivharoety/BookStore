package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.messages.Tick;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {
	private int duration;
	private int speed;
	private TimerTask timerTask;
	private Timer timer;
	private CountDownLatch countDown;

	private int currTick;

	public TimeService(int speed, int duration, CountDownLatch countD) {
		super("time");
		this.countDown = countD;
		this.duration = duration;
		this.speed = speed;
		this.currTick = 0;
		this.timer = new Timer();
		this.timerTask = new TimerTask() {
			@Override
			public void run() {
				currTick++;
				if (currTick == duration) {
					sendBroadcast(new Tick(currTick, duration));
					timerTask.cancel();
					timer.cancel();
					terminate();
				}
				else{
					sendBroadcast(new Tick(currTick, duration));
				}
			}

		};
	}


	@Override
	protected void initialize() {
		timer.schedule(timerTask,speed,speed);
		subscribeBroadcast(Tick.class,(Tick message)->{
			if(message.getTick()==message.getDuration()) {
				terminate();
			}
		});
	}
}

