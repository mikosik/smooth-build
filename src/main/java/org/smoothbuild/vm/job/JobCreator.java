package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.list;

import javax.inject.Inject;

import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.bytecode.expr.oper.RefB;
import org.smoothbuild.bytecode.expr.oper.SelectB;
import org.smoothbuild.bytecode.expr.val.ValB;

import com.google.common.collect.ImmutableList;

public class JobCreator {
  private final ImmutableList<Job> bindings;

  @Inject
  public JobCreator() {
    this(list());
  }

  protected JobCreator(ImmutableList<Job> bindings) {
    this.bindings = bindings;
  }

  public Job jobFor(ExprB expr, ExecutionContext context) {
    return switch (expr) {
      case ValB val -> new ConstJob(val, context);
      case CallB call -> new CallJob(call, context);
      case CombineB combine -> new CombineJob(combine, context);
      case OrderB order -> new OrderJob(order, context);
      case RefB ref -> bindings.get(ref.value().intValue());
      case SelectB select -> new SelectJob(select, context);
      // `default` is needed because ExprB is not sealed because it is in different package
      // than its subclasses and code is not modularized.
      default -> throw new RuntimeException("shouldn't happen");
    };
  }

  public JobCreator withBindings(ImmutableList<Job> args) {
    return new JobCreator(args);
  }
}
