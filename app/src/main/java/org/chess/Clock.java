package org.chess;

public class Clock {
  private long timeLeftNanosecs;
  private long resumedTimestamp;
  private boolean paused;

  public Clock(long timeLeftNanosecs) {
    this.timeLeftNanosecs = timeLeftNanosecs;
  }

  public void pause(){
    if (paused) {
      return;
    }
    timeLeftNanosecs -= Math.max(0, System.nanoTime() - resumedTimestamp);
    if (timeLeftNanosecs < 0) {
      timeLeftNanosecs = 0;
    }
    paused = true;
    
  }

  public void resume() {
    if (!paused) {
      return;
    }
    resumedTimestamp = System.nanoTime();
    paused = false;
  }

  public long getTimeLeftNanosecs() {
    if (paused) {
      return timeLeftNanosecs;
    }
    return Math.max(0, timeLeftNanosecs - System.nanoTime() + resumedTimestamp);
  }

  public String formatTimeLeft() {
    // format getTimeLeft, which is nanoseconds, into string.
  }

}
