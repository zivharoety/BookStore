package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */

/*
@INV : if isDone() == false than get() is blocking.
*/

public class Future<T> {
		T val;
		boolean done;
	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
		val = null;
		done = false;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
	 *  * @PRE : none.
	 * 	 * @POST : trivial.
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
     * 	       
     */


	public synchronized T get() {
		while (!done){
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		return val;
	}
	
	/**
     * Resolves the result of this Future object.
	 * @PRE : isDone() == false.
	 * @POST : get is not blocking & isDone == true.
     */
	public synchronized void resolve (T result) {
		val = result ;
		done = true;
		notifyAll();
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
	 * @PRE : none
	 * @POST : none
     */
	public boolean isDone() {
		return done;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
	 * @PRE : none.
	 * @POST : trivial.\
	 *
     * @param timeout 	the maximal amount of time units to wait for the result.
     * @param unit		the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
     */
	public synchronized T get(long timeout, TimeUnit unit) {
		if(isDone()){
			return val;
		}
		try {

			wait(unit.convert(timeout,unit));//wait(timeout);
			//to check how to implement
		} catch (InterruptedException e) {}
		return val;
	}

}
