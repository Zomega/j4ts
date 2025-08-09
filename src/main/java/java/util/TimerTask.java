package java.util;

public abstract class TimerTask implements Runnable {
  static final int VIRGIN = 0;
  static final int SCHEDULED = 1;
  static final int EXECUTED = 2;
  static final int CANCELLED = 3;
  int state = VIRGIN;
  long nextExecutionTime;
  long period = 0L;
  int handle;

  protected TimerTask() {}

  public abstract void run();

  public boolean cancel() {
    boolean success = this.state == SCHEDULED;

    this.state = CANCELLED;
    this.nextExecutionTime = 0L;
    this.period = 0L;

    return success;
  }

  public long scheduledExecutionTime() {
    return this.period < 0L
        ? this.nextExecutionTime + this.period
        : this.nextExecutionTime - this.period;
  }
}
