package org.smoothbuild.vm.evaluate.execute;

import static org.smoothbuild.util.collect.Lists.list;

import java.util.concurrent.atomic.AtomicBoolean;

import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;

import com.google.common.collect.ImmutableList;

public record Job(
    ExprB exprB,
    ImmutableList<Job> environment,
    TraceB trace,
    AtomicBoolean started,
    PromisedValue<ValueB> promisedValue) {

  public Job(ExprB exprB) {
    this(exprB, list(), new TraceB(), new AtomicBoolean(false), new PromisedValue<>());
  }

  public Job(ExprB exprB, ImmutableList<Job> environment, TraceB trace) {
    this(exprB, environment, trace, new AtomicBoolean(false), new PromisedValue<>());
  }
}
