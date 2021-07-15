package org.smoothbuild.cli.console;

import java.util.List;

public interface Logs {
  public boolean containsProblem();

  public List<Log> toList();

  public ImmutableLogs toImmutableLogs();
}
