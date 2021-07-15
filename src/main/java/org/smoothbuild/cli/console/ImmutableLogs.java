package org.smoothbuild.cli.console;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class ImmutableLogs implements Logs {
  private final ImmutableList<Log> logs;
  private final boolean containsProblem;

  public static ImmutableLogs logs(Log... logs) {
    return new ImmutableLogs(ImmutableList.copyOf(logs));
  }

  public ImmutableLogs(List<Log> logs) {
    this.logs = ImmutableList.copyOf(logs);
    this.containsProblem = this.logs.stream().anyMatch(l -> l.level().isAProblem());
  }

  @Override
  public boolean containsProblem() {
    return containsProblem;
  }

  @Override
  public ImmutableList<Log> toList() {
    return logs;
  }

  @Override
  public ImmutableLogs toImmutableLogs() {
    return this;
  }
}
