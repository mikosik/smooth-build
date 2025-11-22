package org.smoothbuild.virtualmachine.evaluate.job;

import jakarta.inject.Inject;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.schedule.Scheduler;
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
import org.smoothbuild.virtualmachine.dagger.PerVm;
import org.smoothbuild.virtualmachine.evaluate.base.BExprAttributes;
import org.smoothbuild.virtualmachine.evaluate.base.BReferenceInliner;
import org.smoothbuild.virtualmachine.evaluate.cache.CachingOperatorEvaluator;

@PerVm
public class JobContext {
  private final BReferenceInliner referenceInliner;
  private final BytecodeFactory bytecodeFactory;
  private final CachingOperatorEvaluator cachingOperatorEvaluator;
  private final Scheduler scheduler;
  private final BExprAttributes exprAttributes;

  @Inject
  public JobContext(
      BExprAttributes exprAttributes,
      Scheduler scheduler,
      CachingOperatorEvaluator cachingOperatorEvaluator,
      BytecodeFactory bytecodeFactory,
      BReferenceInliner referenceInliner) {
    this.referenceInliner = referenceInliner;
    this.bytecodeFactory = bytecodeFactory;
    this.cachingOperatorEvaluator = cachingOperatorEvaluator;
    this.scheduler = scheduler;
    this.exprAttributes = exprAttributes;
  }

  @SuppressWarnings("NullAway")
  public Job newJob(BExpr expr, List<Job> environment, Trace trace) {
    return switch (expr) {
      case BChoose choose -> new BChooseJob(this, choose, environment, trace);
      case BOrder order -> new BOrderJob(this, order, environment, trace);
      case BSelect select -> new BSelectJob(this, select, environment, trace);
      case BPick pick -> new BPickJob(this, pick, environment, trace);
      case BInvoke invoke -> new BInvokeJob(this, invoke, environment, trace);
      case BCombine combine -> new BCombineJob(this, combine, environment, trace);
      case BSwitch switch_ -> new BSwitchJob(this, switch_, environment, trace);
      case BCall call -> new BCallJob(this, call, environment, trace);
      case BIf if_ -> new BIfJob(this, if_, environment, trace);
      case BMap map -> new BMapJob(this, map, environment, trace);
      case BFold fold -> new BFoldJob(this, fold, environment, trace);
      case BLambda lambda -> new BLambdaJob(this, lambda, environment, trace);
      case BReference reference -> new BReferenceJob(this, reference, environment, trace);
      case BValue value -> new BValueJob(this, value, environment, trace);
    };
  }

  public BReferenceInliner referenceInliner() {
    return referenceInliner;
  }

  public BytecodeFactory bytecodeFactory() {
    return bytecodeFactory;
  }

  public CachingOperatorEvaluator cachingOperatorEvaluator() {
    return cachingOperatorEvaluator;
  }

  public Scheduler scheduler() {
    return scheduler;
  }

  public BExprAttributes exprAttributes() {
    return exprAttributes;
  }
}
