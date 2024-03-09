package org.smoothbuild.app.run.eval.report;

import java.util.List;
import org.smoothbuild.common.log.Label;
import org.smoothbuild.common.log.Log;

@FunctionalInterface
public interface TaskMatcher {
  public boolean matches(Label label, List<Log> logs);
}
