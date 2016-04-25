package scheduler;

public interface SchedulerInterface {
	
	public abstract Event getNextEvent() throws Exception;
	public abstract boolean hasNextEvent();

}
