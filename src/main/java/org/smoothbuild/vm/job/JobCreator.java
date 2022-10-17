package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.list;

import javax.inject.Inject;

import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.bytecode.expr.oper.PickB;
import org.smoothbuild.bytecode.expr.oper.RefB;
import org.smoothbuild.bytecode.expr.oper.SelectB;
import org.smoothbuild.compile.lang.define.TraceS;
import org.smoothbuild.vm.task.CombineTask;
import org.smoothbuild.vm.task.OrderTask;
import org.smoothbuild.vm.task.PickTask;
import org.smoothbuild.vm.task.SelectTask;

import com.google.common.collect.ImmutableList;

public class JobCreator {
  private final ImmutableList<Job> environment;
  private final TraceS trace;

  @Inject
  public JobCreator() {
    this(list(), null);
  }

  protected JobCreator(ImmutableList<Job> environment, TraceS trace) {
    this.environment = environment;
    this.trace = trace;
  }

  public Job jobFor(ExprB expr, ExecutionContext context) {
    return switch (expr) {
      case CallB call -> new CallJob(call, context);
      case CombineB combine -> new OperJob(CombineTask::new, combine, context);
      case InstB inst -> new ConstJob(inst, context);
      case OrderB order -> new OperJob(OrderTask::new, order, context);
      case PickB pick -> new OperJob(PickTask::new, pick, context);
      case RefB ref -> environment.get(ref.value().intValue());
      case SelectB select -> new OperJob(SelectTask::new, select, context);
      // `default` is needed because ExprB is not sealed because it is in different package
      // than its subclasses and code is not modularized.
      default -> throw new RuntimeException("shouldn't happen");
    };
  }

  public JobCreator withEnvironment(ImmutableList<Job> environment, TraceS trace) {
    return new JobCreator(environment, trace);
  }

  public TraceS trace() {
    return trace;
  }
}
