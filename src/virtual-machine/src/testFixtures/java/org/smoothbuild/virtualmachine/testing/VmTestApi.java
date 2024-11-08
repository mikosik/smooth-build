package org.smoothbuild.virtualmachine.testing;

import static org.smoothbuild.common.bucket.base.FullPath.fullPath;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.common.bucket.base.SubBucket.subBucket;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.log.base.Log.containsFailure;
import static org.smoothbuild.common.testing.TestingAlias.PROJECT;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.Filesystem;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.common.bucket.base.SynchronizedBucket;
import org.smoothbuild.common.bucket.mem.MemoryBucket;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Scheduler;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BPick;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.load.NativeMethodLoader;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationCache;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationHashFactory;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;
import org.smoothbuild.virtualmachine.evaluate.compute.StepEvaluator;
import org.smoothbuild.virtualmachine.evaluate.execute.BReferenceInliner;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;
import org.smoothbuild.virtualmachine.evaluate.execute.Job;
import org.smoothbuild.virtualmachine.evaluate.execute.Vm;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;
import org.smoothbuild.virtualmachine.evaluate.step.BOutput;
import org.smoothbuild.virtualmachine.evaluate.step.CombineStep;
import org.smoothbuild.virtualmachine.evaluate.step.InvokeStep;
import org.smoothbuild.virtualmachine.evaluate.step.OrderStep;
import org.smoothbuild.virtualmachine.evaluate.step.PickStep;
import org.smoothbuild.virtualmachine.evaluate.step.SelectStep;
import org.smoothbuild.virtualmachine.evaluate.step.Step;

public interface VmTestApi extends BytecodeTestApi {
  FullPath PROJECT_PATH = fullPath(PROJECT, Path.root());
  FullPath COMPUTATION_DB_PATH = PROJECT.append(".smooth/computations");
  FullPath BYTECODE_DB_PATH = PROJECT.append(".smooth/bytecode");
  FullPath ARTIFACTS_PATH = PROJECT.append(".smooth/artifacts");

  public Bucket projectBucket();

  public StepEvaluator stepEvaluator();

  public default Job job(BExpr expr, BExpr... environment) {
    return job(expr, list(environment));
  }

  public default Job job(BExpr expr, List<BExpr> list) {
    return new Job(expr, list.map(this::job), new BTrace());
  }

  public default Job job(BExpr expr) {
    return job(expr, list());
  }

  public default void throwExceptionOnFailure(Output<Tuple0> output) {
    if (containsFailure(output.report().logs())) {
      throw new RuntimeException(output.toString());
    }
  }

  public default SynchronizedBucket synchronizedMemoryBucket() {
    return new SynchronizedBucket(new MemoryBucket());
  }

  public default Vm vm() {
    return vm(scheduler());
  }

  public default Vm vm(NativeMethodLoader nativeMethodLoader) {
    return new Vm(scheduler(), stepEvaluator(nativeMethodLoader), bytecodeF(), bReferenceInliner());
  }

  public default Vm vm(Scheduler scheduler) {
    return new Vm(scheduler, stepEvaluator(scheduler), bytecodeF(), bReferenceInliner());
  }

  public default BReferenceInliner bReferenceInliner() {
    return new BReferenceInliner(bytecodeF());
  }

  public default Vm vm(StepEvaluator stepEvaluator) {
    return new Vm(scheduler(), stepEvaluator, bytecodeF(), bReferenceInliner());
  }

  public default StepEvaluator stepEvaluator(NativeMethodLoader nativeMethodLoader) {
    return new StepEvaluator(
        computationHashFactory(),
        () -> container(nativeMethodLoader),
        computationCache(),
        scheduler(),
        bytecodeF());
  }

  public default StepEvaluator stepEvaluator(Scheduler scheduler) {
    return new StepEvaluator(
        computationHashFactory(), this::container, computationCache(), scheduler, bytecodeF());
  }

  public default ComputationHashFactory computationHashFactory() {
    return new ComputationHashFactory(Hash.of(123));
  }

  public default NativeApi nativeApi() {
    return container();
  }

  public default Container container() {
    return container(nativeMethodLoader());
  }

  public default Container container(NativeMethodLoader nativeMethodLoader) {
    return new Container(
        filesystem(), PROJECT_PATH, fileContentReader(), bytecodeF(), nativeMethodLoader);
  }

  public default Filesystem filesystem() {
    return new Filesystem(map(PROJECT, projectBucket()));
  }

  public default ComputationCache computationCache() {
    var computationCache = new ComputationCache(computationCacheBucket(), exprDb(), bytecodeF());
    throwExceptionOnFailure(computationCache.execute());
    return computationCache;
  }

  public default Bucket computationCacheBucket() {
    return subBucket(projectBucket(), path("cache"));
  }

  public default FullPath moduleFullPath() {
    return moduleFullPath("module.smooth");
  }

  public default FullPath moduleFullPath(String path) {
    return fullPath(PROJECT, path(path));
  }

  public default Step task() throws BytecodeException {
    return orderTask();
  }

  public default InvokeStep invokeTask() throws BytecodeException {
    return invokeTask(bInvoke(), bTrace());
  }

  public default InvokeStep invokeTask(BInvoke invoke) {
    return invokeTask(invoke, null);
  }

  public default InvokeStep invokeTask(BInvoke invoke, BTrace trace) {
    return new InvokeStep(invoke, trace);
  }

  public default CombineStep combineTask() throws BytecodeException {
    return combineTask(bCombine(), bTrace());
  }

  public default CombineStep combineTask(BCombine combine, BTrace trace) {
    return new CombineStep(combine, trace);
  }

  public default SelectStep selectTask() throws BytecodeException {
    return selectTask(bSelect(), bTrace());
  }

  public default SelectStep selectTask(BSelect select, BTrace trace) {
    return new SelectStep(select, trace);
  }

  public default PickStep pickTask() throws BytecodeException {
    return pickTask(bPick(), bTrace());
  }

  public default PickStep pickTask(BPick pick, BTrace trace) {
    return new PickStep(pick, trace);
  }

  public default OrderStep orderTask() throws BytecodeException {
    return orderTask(bOrder(), bTrace());
  }

  public default OrderStep orderTask(BOrder order, BTrace trace) {
    return new OrderStep(order, trace);
  }

  public default BOutput output(BValue value) throws BytecodeException {
    return output(value, bLogArrayEmpty());
  }

  public default BOutput output(BValue value, BArray messages) {
    return new BOutput(value, messages);
  }
}
