package org.smoothbuild.out.log;

import java.util.List;

public interface Logs {
  public boolean containsProblem();

  public List<Log> toList();

  public ImmutableLogs toImmutableLogs();
}
