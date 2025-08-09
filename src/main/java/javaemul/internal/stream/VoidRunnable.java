package javaemul.internal.stream;

public class VoidRunnable implements Runnable {
  public static final VoidRunnable dryRun = new VoidRunnable();

  public void run() {
    // relax
  }
}
