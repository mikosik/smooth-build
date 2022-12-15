package org.smoothbuild.vm.evaluate.task;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.vm.bytecode.expr.oper.SelectB;
import org.smoothbuild.vm.bytecode.expr.value.IntB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.compute.Container;
import org.smoothbuild.vm.evaluate.execute.TraceB;

import com.google.common.collect.ImmutableList;

public final class SelectTask extends Task {
  public SelectTask(SelectB selectB, TraceB trace) {
    super(selectB, trace);
  }

  @Override
  public Output run(TupleB input, Container container) {
    var components = input.items();
    checkArgument(components.size() == 2);
    var tuple = selectable(components);
    var index = index(components);
    return new Output(tuple.get(index.toJ().intValue()), container.messages());
  }

  private TupleB selectable(ImmutableList<ValueB> components) {
    return (TupleB) components.get(0);
  }

  private IntB index(ImmutableList<ValueB> components) {
    return (IntB) components.get(1);
  }
}
