package org.smoothbuild.virtualmachine.evaluate;

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
import org.smoothbuild.virtualmachine.evaluate.base.BExprAttributes;
import org.smoothbuild.virtualmachine.evaluate.base.BReferenceInliner;
import org.smoothbuild.virtualmachine.evaluate.cache.CachingOperatorEvaluator;
import org.smoothbuild.virtualmachine.evaluate.job.BCallJob;
import org.smoothbuild.virtualmachine.evaluate.job.BChooseJob;
import org.smoothbuild.virtualmachine.evaluate.job.BCombineJob;
import org.smoothbuild.virtualmachine.evaluate.job.BFoldJob;
import org.smoothbuild.virtualmachine.evaluate.job.BIfJob;
import org.smoothbuild.virtualmachine.evaluate.job.BInvokeJob;
import org.smoothbuild.virtualmachine.evaluate.job.BLambdaJob;
import org.smoothbuild.virtualmachine.evaluate.job.BMapJob;
import org.smoothbuild.virtualmachine.evaluate.job.BOrderJob;
import org.smoothbuild.virtualmachine.evaluate.job.BPickJob;
import org.smoothbuild.virtualmachine.evaluate.job.BReferenceJob;
import org.smoothbuild.virtualmachine.evaluate.job.BSelectJob;
import org.smoothbuild.virtualmachine.evaluate.job.BSwitchJob;
import org.smoothbuild.virtualmachine.evaluate.job.BValueJob;
import org.smoothbuild.virtualmachine.evaluate.job.Job;

/**
 * Evaluates BExpr.
 * This class is thread-safe.
 */
public class BEvaluateTask implements Task1<Tuple2<BExpr, BExprAttributes>, BValue> {
  private final Scheduler scheduler;
  private final CachingOperatorEvaluator cachingOperatorEvaluator;
  private final BytecodeFactory bytecodeFactory;
  private final BReferenceInliner bReferenceInliner;

  @Inject
  public BEvaluateTask(
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
      BEvaluateTask bEvaluateTask,
      BReferenceInliner referenceInliner,
      BytecodeFactory bytecodeFactory,
      CachingOperatorEvaluator cachingOperatorEvaluator,
      Scheduler scheduler,
      BExprAttributes exprAttributes) {
    public Job newJob(BExpr expr, List<Job> environment, Trace trace) {
      return bEvaluateTask.newJob(this, expr, environment, trace);
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
      case BChoose choose -> new BChooseJob(jobContext, choose, environment, trace);
      case BOrder order -> new BOrderJob(jobContext, order, environment, trace);
      case BSelect select -> new BSelectJob(jobContext, select, environment, trace);
      case BPick pick -> new BPickJob(jobContext, pick, environment, trace);
      case BInvoke invoke -> new BInvokeJob(jobContext, invoke, environment, trace);
      case BCombine combine -> new BCombineJob(jobContext, combine, environment, trace);
      case BSwitch switch_ -> new BSwitchJob(jobContext, switch_, environment, trace);
      case BCall call -> new BCallJob(jobContext, call, environment, trace);
      case BIf if_ -> new BIfJob(jobContext, if_, environment, trace);
      case BMap map -> new BMapJob(jobContext, map, environment, trace);
      case BFold fold -> new BFoldJob(jobContext, fold, environment, trace);
      case BLambda lambda -> new BLambdaJob(jobContext, lambda, environment, trace);
      case BReference reference -> new BReferenceJob(jobContext, reference, environment, trace);
      case BValue value -> new BValueJob(jobContext, value, environment, trace);
    };
  }
}
