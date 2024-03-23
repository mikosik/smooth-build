package org.smoothbuild.virtualmachine.evaluate.execute;

import static org.smoothbuild.common.collect.List.list;

import java.util.concurrent.atomic.AtomicBoolean;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.concurrent.PromisedValue;
import org.smoothbuild.virtualmachine.bytecode.expr.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;

public record Job(
    BExpr expr,
    List<Job> environment,
    BTrace trace,
    AtomicBoolean started,
    PromisedValue<BValue> promisedValue) {

  public Job(BExpr expr) {
    this(expr, list(), new BTrace(), new AtomicBoolean(false), new PromisedValue<>());
  }

  public Job(BExpr expr, List<Job> environment, BTrace trace) {
    this(expr, environment, trace, new AtomicBoolean(false), new PromisedValue<>());
  }
}
