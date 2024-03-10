package org.smoothbuild.common.log;

import org.smoothbuild.common.collect.List;

public interface Reporter {
  public void report(Label label, String details, ResultSource source, List<Log> logs);
}
