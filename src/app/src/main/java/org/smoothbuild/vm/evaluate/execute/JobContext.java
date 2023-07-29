package org.smoothbuild.vm.evaluate.execute;

import static org.smoothbuild.common.collect.Lists.list;

import com.google.common.collect.ImmutableList;

public record JobContext(ImmutableList<Job> environment, TraceB trace) {
  public JobContext() {
    this(list(), null);
  }
}
