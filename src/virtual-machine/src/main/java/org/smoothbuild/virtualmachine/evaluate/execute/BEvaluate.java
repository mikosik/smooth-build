package org.smoothbuild.virtualmachine.evaluate.execute;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.schedule.Output.successOutput;
import static org.smoothbuild.virtualmachine.VmConstants.VM_LABEL;

import jakarta.inject.Inject;
import org.jspecify.annotations.Nullable;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Scheduler;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.common.tuple.Tuple2;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BChoose;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BFold;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BIf;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambda;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMap;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BPick;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BReference;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSwitch;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.compute.CachingOperatorEvaluator;
import org.smoothbuild.virtualmachine.evaluate.job.BReferenceInliner;
import org.smoothbuild.virtualmachine.evaluate.job.CallJob;
import org.smoothbuild.virtualmachine.evaluate.job.ChooseJob;
import org.smoothbuild.virtualmachine.evaluate.job.CombineJob;
import org.smoothbuild.virtualmachine.evaluate.job.FoldJob;
import org.smoothbuild.virtualmachine.evaluate.job.IfJob;
import org.smoothbuild.virtualmachine.evaluate.job.InvokeJob;
import org.smoothbuild.virtualmachine.evaluate.job.Job;
import org.smoothbuild.virtualmachine.evaluate.job.LambdaJob;
import org.smoothbuild.virtualmachine.evaluate.job.MapJob;
import org.smoothbuild.virtualmachine.evaluate.job.OrderJob;
import org.smoothbuild.virtualmachine.evaluate.job.PickJob;
import org.smoothbuild.virtualmachine.evaluate.job.ReferenceJob;
import org.smoothbuild.virtualmachine.evaluate.job.SelectJob;
import org.smoothbuild.virtualmachine.evaluate.job.SwitchJob;
import org.smoothbuild.virtualmachine.evaluate.job.ValueJob;

/**
 * Evaluates BExpr.
 * This class is thread-safe.
 */
public class BEvaluate implements Task1<Tuple2<BExpr, BExprAttributes>, BValue> {
  private final Scheduler scheduler;
  private final CachingOperatorEvaluator cachingOperatorEvaluator;
  private final BytecodeFactory bytecodeFactory;
  private final BReferenceInliner bReferenceInliner;

  @Inject
  public BEvaluate(
      Scheduler scheduler,
      CachingOperatorEvaluator cachingOperatorEvaluator,
      BytecodeFactory bytecodeFactory,
      BReferenceInliner bReferenceInliner) {
    this.scheduler = scheduler;
    this.cachingOperatorEvaluator = cachingOperatorEvaluator;
    this.bytecodeFactory = bytecodeFactory;
    this.bReferenceInliner = bReferenceInliner;
  }

  @Override
  public Output<BValue> execute(Tuple2<BExpr, BExprAttributes> expr) {
    var jobContext = new JobContext(
        this,
        bReferenceInliner,
        bytecodeFactory,
        cachingOperatorEvaluator,
        scheduler,
        expr.element2());
    var label = VM_LABEL.append(":schedule");
    var job = jobContext.newJob(expr.element1(), list(), new Trace());
    return successOutput(job.evaluate(), label);
  }

  public record JobContext(
      BEvaluate bEvaluate,
      BReferenceInliner referenceInliner,
      BytecodeFactory bytecodeFactory,
      CachingOperatorEvaluator cachingOperatorEvaluator,
      Scheduler scheduler,
      BExprAttributes exprAttributes) {
    public Job newJob(BExpr expr, List<Job> environment, Trace trace) {
      return bEvaluate.newJob(this, expr, environment, trace);
    }
  }

  // Visible for testing
  public Job newJob(JobContext jobContext, BExpr expr, List<Job> environment, Trace trace) {
    return newJobStatic(jobContext, expr, environment, trace);
  }

  @SuppressWarnings("NullAway")
  public static Job newJobStatic(
      @Nullable JobContext jobContext, BExpr expr, List<Job> environment, Trace trace) {
    return switch (expr) {
      case BChoose choose -> new ChooseJob(jobContext, choose, environment, trace);
      case BOrder order -> new OrderJob(jobContext, order, environment, trace);
      case BSelect select -> new SelectJob(jobContext, select, environment, trace);
      case BPick pick -> new PickJob(jobContext, pick, environment, trace);
      case BInvoke invoke -> new InvokeJob(jobContext, invoke, environment, trace);
      case BCombine combine -> new CombineJob(jobContext, combine, environment, trace);
      case BSwitch switch_ -> new SwitchJob(jobContext, switch_, environment, trace);
      case BCall call -> new CallJob(jobContext, call, environment, trace);
      case BIf if_ -> new IfJob(jobContext, if_, environment, trace);
      case BMap map -> new MapJob(jobContext, map, environment, trace);
      case BFold fold -> new FoldJob(jobContext, fold, environment, trace);
      case BLambda lambda -> new LambdaJob(jobContext, lambda, environment, trace);
      case BReference reference -> new ReferenceJob(jobContext, reference, environment, trace);
      case BValue value -> new ValueJob(jobContext, value, environment, trace);
    };
  }
}
