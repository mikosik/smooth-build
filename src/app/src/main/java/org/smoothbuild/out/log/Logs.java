package org.smoothbuild.out.log;

import org.smoothbuild.common.collect.List;

public interface Logs extends Iterable<Log> {
  public default boolean containsAtLeast(Level level) {
    return toList().anyMatches(l -> l.level().hasPriorityAtLeast(level));
  }

  public List<Log> toList();

  public ImmutableLogs toImmutableLogs();
}
