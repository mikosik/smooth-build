package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;

import javax.inject.Inject;

import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.inst.DefFuncB;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.expr.oper.ClosurizeB;
import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.bytecode.expr.oper.PickB;
import org.smoothbuild.bytecode.expr.oper.RefB;
import org.smoothbuild.bytecode.expr.oper.SelectB;
import org.smoothbuild.vm.execute.TraceB;
import org.smoothbuild.vm.task.CombineTask;
import org.smoothbuild.vm.task.OrderTask;
import org.smoothbuild.vm.task.PickTask;
import org.smoothbuild.vm.task.SelectTask;

import com.google.common.collect.ImmutableList;

public class JobCreator {
  private final ImmutableList<Job> environment;
  private final TraceB trace;

  @Inject
  public JobCreator() {
    this(list(), null);
  }

  protected JobCreator(ImmutableList<Job> environment, TraceB trace) {
    this.environment = environment;
    this.trace = trace;
  }

  public Job jobFor(ExprB expr, ExecutionContext context) {
    // formatter:off
    return switch (expr) {
      case CallB      call      -> new CallJob(call, context);
      case ClosurizeB closurize -> new ConstJob(newClosure(closurize), context);
      case CombineB   combine   -> new OperJob<>(CombineTask::new, combine, context);
      case InstB      inst      -> new ConstJob(inst, context);
      case OrderB     order     -> new OperJob<>(OrderTask::new, order, context);
      case PickB      pick      -> new OperJob<>(PickTask::new, pick, context);
      case RefB       ref       -> jobForEnv(ref);
      case SelectB    select    -> new OperJob<>(SelectTask::new, select, context);
      // `default` is needed because ExprB is not sealed because it is in different package
      // than its subclasses and code is not modularized.
      default -> throw new RuntimeException("shouldn't happen");
    };
    // formatter:on
  }

  private DefFuncB newClosure(ClosurizeB closurize) {
    return closurize.buildClosure(map(environment, Job::exprB));
  }

  private Job jobForEnv(RefB ref) {
    int index = ref.value().intValue();
    var job = environment.get(index);
    var jobEvalT = job.exprB().evalT();
    if (jobEvalT.equals(ref.evalT())) {
      return job;
    } else {
      throw new RuntimeException("environment(%d) evalT is %s but expected %s."
          .formatted(index, jobEvalT.q(), ref.evalT().q()));
    }
  }

  public JobCreator withEnvironment(ImmutableList<Job> environment, TraceB trace) {
    return new JobCreator(environment, trace);
  }

  public TraceB trace() {
    return trace;
  }
}
