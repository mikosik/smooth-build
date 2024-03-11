package org.smoothbuild.common.log;

import java.util.List;

@FunctionalInterface
public interface ReportMatcher {
  public boolean matches(Label label, List<Log> logs);
}
