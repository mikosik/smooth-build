package org.smoothbuild.common.log.report;

import java.util.List;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Log;

@FunctionalInterface
public interface ReportMatcher {
  public boolean matches(Label label, List<Log> logs);
}
