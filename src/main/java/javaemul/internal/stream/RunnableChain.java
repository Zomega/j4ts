package javaemul.internal.stream;

public class RunnableChain {
  private final Runnable run;
  private RunnableChain next;

  public RunnableChain(Runnable run) {
    this.run = run;
  }

  public void chain(RunnableChain next) {
    if (this.next == null) {
      this.next = next;
      return;
    }
    this.next.chain(next);
  }

  public void runChain() {
    run.run();
    if (next == null) {
      return;
    }
    next.runChain();
  }
}
