package org.smoothbuild.common.log.report;

import static com.google.common.collect.Maps.toImmutableEnumMap;
import static java.util.Arrays.stream;

import jakarta.inject.Inject;
import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.smoothbuild.common.dagger.PerCommand;
import org.smoothbuild.common.log.base.Level;

/**
 * Holds count of each reported log in each log level.
 * This class is thread-safe.
 */
@PerCommand
public class LogCounters {
  private final EnumMap<Level, AtomicInteger> counters = createCountersMap();

  @Inject
  public LogCounters() {}

  public void increment(Level level) {
    counters.get(level).getAndIncrement();
  }

  public int get(Level level) {
    return counters.get(level).get();
  }

  private static EnumMap<Level, AtomicInteger> createCountersMap() {
    var map = stream(Level.values()).collect(toImmutableEnumMap(v -> v, v -> new AtomicInteger()));
    return new EnumMap<>(map);
  }
}
