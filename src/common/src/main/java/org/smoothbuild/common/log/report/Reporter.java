package org.smoothbuild.common.log.report;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.base.ResultSource;

public interface Reporter {
  public void report(Label label, String details, ResultSource source, List<Log> logs);
}
