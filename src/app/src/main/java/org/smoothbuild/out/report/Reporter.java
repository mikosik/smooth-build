package org.smoothbuild.out.report;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.out.log.Log;

public interface Reporter {
  public void startNewPhase(String name);

  public void report(Log log);

  public default void report(String header, List<Log> logs) {
    report(true, header, logs);
  }

  public void report(boolean visible, String header, List<Log> logs);

  public void printSummary();

  public void reportResult(String string);
}
