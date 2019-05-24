package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.messages.Tick;

import java.util.concurrent.CountDownLatch;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{
	private Future<Integer> futureAvailable;
	private Future<OrderResult> futureIsTaken;
	private MoneyRegister moneyRegister;
	private CountDownLatch countDown;
	private int currTick;

	public SellingService(String name, CountDownLatch countD) {
		super(name);
		currTick = 0;
		countDown = countD;
		moneyRegister = MoneyRegister.getInstance();
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(Tick.class,(Tick message)->{
			if(message.getTick()==message.getDuration()) {
				terminate();

			}
			currTick = message.getTick();
		});
		subscribeEvent(BookOrderEvent.class , (BookOrderEvent message)->{
			CheckAvailabilityEvent toCheck = new CheckAvailabilityEvent(message.getBookName());
			futureAvailable = sendEvent(toCheck);
			boolean gotKey = false;
			if(futureAvailable.get()!=null) {
				if (futureAvailable.get() != -1) {
					while (!gotKey) {
						try {
							message.getCustomer().getSem().acquire();
							gotKey = true;
						} catch (InterruptedException igrnored) {
						}
					}
					if (message.getCustomer().getAvailableCreditAmount() >= futureAvailable.get()) {

						TakeBookEvent toTake = new TakeBookEvent(message.getBookName());
						futureIsTaken = sendEvent(toTake);
						if (futureIsTaken.get() != null) {
							if (futureIsTaken.get() == OrderResult.SUCCESSFULLY_TAKEN) {
								OrderReceipt receipt = new OrderReceipt(message.getOrderId(), this.getName(), message.getCustomer().getId(), message.getBookName(),
										message.getOrderTick(), currTick);
								moneyRegister.chargeCreditCard(message.getCustomer(), futureAvailable.get());
								receipt.setPrice(futureAvailable.get());
								receipt.setIssuedTick(currTick);
								message.getCustomer().addOrder(receipt);
								moneyRegister.file(receipt);
								complete(message, receipt);
								message.getCustomer().getSem().release();
							} else {
								complete(message, null);
								message.getCustomer().getSem().release();
							}
						} else {
							complete(message, null);
							message.getCustomer().getSem().release();
						}
					} else {
						complete(message, null);
						message.getCustomer().getSem().release();
					}
				} else {
					complete(message, null);
				}
			}else
				complete(message,null);


		});

		countDown.countDown();

	}

}
