package org.smoothbuild.cli.taskmatcher;

import java.util.List;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.vm.job.job.JobInfo;

@FunctionalInterface
public interface TaskMatcher {
  public boolean matches(JobInfo jobInfo, List<Log> logs);
}
