package org.smoothbuild.out.log;

import static org.smoothbuild.out.log.Level.ERROR;

import org.smoothbuild.common.collect.List;

public interface Logs extends Iterable<Log> {
  public default boolean containsFailure() {
    return toList().anyMatches(l -> l.level().hasPriorityAtLeast(ERROR));
  }

  public List<Log> toList();

  public ImmutableLogs toImmutableLogs();
}
