package org.smoothbuild.virtualmachine.evaluate.execute;

import static org.smoothbuild.common.collect.List.list;

import java.util.concurrent.atomic.AtomicBoolean;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.concurrent.PromisedValue;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;

public record Job(
    ExprB exprB,
    List<Job> environment,
    TraceB trace,
    AtomicBoolean started,
    PromisedValue<ValueB> promisedValue) {

  public Job(ExprB exprB) {
    this(exprB, list(), new TraceB(), new AtomicBoolean(false), new PromisedValue<>());
  }

  public Job(ExprB exprB, List<Job> environment, TraceB trace) {
    this(exprB, environment, trace, new AtomicBoolean(false), new PromisedValue<>());
  }
}
