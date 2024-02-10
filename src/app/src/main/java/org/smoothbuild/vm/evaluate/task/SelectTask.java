package org.smoothbuild.vm.evaluate.task;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.oper.SelectB;
import org.smoothbuild.vm.bytecode.expr.value.IntB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.compute.Container;
import org.smoothbuild.vm.evaluate.execute.TraceB;

public final class SelectTask extends Task {
  public SelectTask(SelectB selectB, TraceB trace) {
    super(selectB, trace);
  }

  @Override
  public Output run(TupleB input, Container container) throws BytecodeException {
    var components = input.elements();
    checkArgument(components.size() == 2);
    var tuple = selectable(components);
    var index = index(components);
    return new Output(tuple.get(index.toJ().intValue()), container.messages());
  }

  private TupleB selectable(List<ValueB> components) {
    return (TupleB) components.get(0);
  }

  private IntB index(List<ValueB> components) {
    return (IntB) components.get(1);
  }
}
