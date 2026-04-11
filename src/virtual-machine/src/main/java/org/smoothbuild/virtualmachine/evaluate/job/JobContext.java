package org.smoothbuild.virtualmachine.evaluate.job;

import jakarta.inject.Inject;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.schedule.Scheduler;
import org.smoothbuild.virtualmachine.VmConfig;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArrayGet;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BConstructArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BConstructTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BConstructVariant;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BFold;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BIf;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambda;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMap;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BRef;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSwitch;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTupleGet;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.dagger.PerVm;
import org.smoothbuild.virtualmachine.evaluate.base.BRefInliner;
import org.smoothbuild.virtualmachine.evaluate.base.DebugSymbols;
import org.smoothbuild.virtualmachine.evaluate.cache.CachingOperatorEvaluator;

@PerVm
public class JobContext {
  private final BRefInliner bRefInliner;
  private final BytecodeFactory bytecodeFactory;
  private final CachingOperatorEvaluator cachingOperatorEvaluator;
  private final Scheduler scheduler;
  private final Map<Hash, DebugSymbols> debugSymbols;
  private final VmConfig vmConfig;

  @Inject
  public JobContext(
      Map<Hash, DebugSymbols> debugSymbols,
      Scheduler scheduler,
      CachingOperatorEvaluator cachingOperatorEvaluator,
      BytecodeFactory bytecodeFactory,
      BRefInliner bRefInliner,
      VmConfig vmConfig) {
    this.bRefInliner = bRefInliner;
    this.bytecodeFactory = bytecodeFactory;
    this.cachingOperatorEvaluator = cachingOperatorEvaluator;
    this.scheduler = scheduler;
    this.debugSymbols = debugSymbols;
    this.vmConfig = vmConfig;
  }

  @SuppressWarnings("NullAway")
  public Job newJob(BExpr expr, List<Job> environment, Trace trace) {
    return switch (expr) {
      case BConstructVariant choose -> new BConstructVariantJob(this, choose, environment, trace);
      case BConstructArray constructArray ->
        new BConstructArrayJob(this, constructArray, environment, trace);
      case BTupleGet tupleGet -> new BTupleGetJob(this, tupleGet, environment, trace);
      case BArrayGet arrayGet -> new BArrayGetJob(this, arrayGet, environment, trace);
      case BInvoke invoke -> new BInvokeJob(this, invoke, environment, trace);
      case BConstructTuple constructTuple ->
        new BConstructTupleJob(this, constructTuple, environment, trace);
      case BSwitch switch_ -> new BSwitchJob(this, switch_, environment, trace);
      case BCall call -> new BCallJob(this, call, environment, trace);
      case BIf if_ -> new BIfJob(this, if_, environment, trace);
      case BMap map -> new BMapJob(this, map, environment, trace);
      case BFold fold -> new BFoldJob(this, fold, environment, trace);
      case BLambda lambda -> new BLambdaJob(this, lambda, environment, trace);
      case BRef ref -> new BRefJob(this, ref, environment, trace);
      case BValue value -> new BValueJob(this, value, environment, trace);
    };
  }

  public BRefInliner refInliner() {
    return bRefInliner;
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

  public Map<Hash, DebugSymbols> debugSymbols() {
    return debugSymbols;
  }

  public VmConfig vmConfig() {
    return vmConfig;
  }
}
