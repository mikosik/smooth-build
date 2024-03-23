package org.smoothbuild.virtualmachine.evaluate.execute;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;

public record JobContext(List<Job> environment, BTrace trace) {
  public JobContext() {
    this(list(), null);
  }
}
