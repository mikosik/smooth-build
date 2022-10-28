package org.smoothbuild.out.log;

import java.util.List;
import java.util.stream.Stream;

public interface Logs {
  public Stream<Log> stream();

  public default boolean containsAtLeast(Level level) {
    return stream().anyMatch(l -> l.level().hasPriorityAtLeast(level));
  }

  public List<Log> toList();

  public ImmutableLogs toImmutableLogs();
}
