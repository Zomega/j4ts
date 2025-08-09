package javaemul.internal.stream;

public class QuiteRunnable implements Runnable {
  private final Runnable loudRunnable;

  public QuiteRunnable(Runnable loudRunnable) {
    this.loudRunnable = loudRunnable;
  }

  @Override
  public void run() {
    try {
      loudRunnable.run();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
