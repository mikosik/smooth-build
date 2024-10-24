package org.smoothbuild.virtualmachine.testing;

import static com.google.common.base.Suppliers.memoize;
import static java.lang.ClassLoader.getSystemClassLoader;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.concurrent.Promise.promise;
import static org.smoothbuild.common.log.base.Log.containsFailure;

import com.google.common.base.Supplier;
import org.mockito.Mockito;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.common.bucket.base.SubBucket;
import org.smoothbuild.common.bucket.base.SynchronizedBucket;
import org.smoothbuild.common.bucket.mem.MemoryBucket;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Scheduler;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BPick;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb;
import org.smoothbuild.virtualmachine.bytecode.kind.BKindDb;
import org.smoothbuild.virtualmachine.bytecode.load.BytecodeLoader;
import org.smoothbuild.virtualmachine.bytecode.load.BytecodeMethodLoader;
import org.smoothbuild.virtualmachine.bytecode.load.FileContentReader;
import org.smoothbuild.virtualmachine.bytecode.load.JarClassLoaderFactory;
import org.smoothbuild.virtualmachine.bytecode.load.MethodLoader;
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

public class TestingVm extends TestingBytecode {
  private final Supplier<BytecodeFactory> bytecodeFactory = memoize(this::newBytecodeFactory);
  private final Supplier<BExprDb> exprDb = memoize(this::newExprDb);
  private final Supplier<BKindDb> kindDb = memoize(this::newKindDb);
  private final Supplier<HashedDb> hashedDb = memoize(this::createHashDb);
  private final Supplier<Bucket> projectBucket = memoize(() -> synchronizedMemoryBucket());
  private final Supplier<Bucket> hashedDbBucket = memoize(() -> synchronizedMemoryBucket());

  private final Supplier<StepEvaluator> stepEvaluator = memoize(this::newStepEvaluator);

  public Vm vm() {
    return vm(scheduler());
  }

  public Vm vm(NativeMethodLoader nativeMethodLoader) {
    return new Vm(scheduler(), stepEvaluator(nativeMethodLoader), bytecodeF(), bReferenceInliner());
  }

  public Vm vm(Scheduler scheduler) {
    return new Vm(scheduler, stepEvaluator(scheduler), bytecodeF(), bReferenceInliner());
  }

  public BReferenceInliner bReferenceInliner() {
    return new BReferenceInliner(bytecodeF());
  }

  public Vm vm(StepEvaluator stepEvaluator) {
    return new Vm(scheduler(), stepEvaluator, bytecodeF(), bReferenceInliner());
  }

  public NativeMethodLoader nativeMethodLoader() {
    return new NativeMethodLoader(methodLoader());
  }

  public FileContentReader fileContentReader() {
    return Mockito.mock(FileContentReader.class);
  }

  public BytecodeLoader bytecodeLoader() {
    return new BytecodeLoader(bytecodeMethodLoader(), bytecodeF());
  }

  public BytecodeMethodLoader bytecodeMethodLoader() {
    return new BytecodeMethodLoader(methodLoader());
  }

  private MethodLoader methodLoader() {
    return new MethodLoader(jarClassLoaderFactory());
  }

  private JarClassLoaderFactory jarClassLoaderFactory() {
    return new JarClassLoaderFactory(bytecodeF(), getSystemClassLoader());
  }

  public StepEvaluator stepEvaluator() {
    return stepEvaluator.get();
  }

  private StepEvaluator newStepEvaluator() {
    return new StepEvaluator(
        computationHashFactory(), this::container, computationCache(), scheduler(), bytecodeF());
  }

  public StepEvaluator stepEvaluator(NativeMethodLoader nativeMethodLoader) {
    return new StepEvaluator(
        computationHashFactory(),
        () -> container(nativeMethodLoader),
        computationCache(),
        scheduler(),
        bytecodeF());
  }

  private StepEvaluator stepEvaluator(Scheduler scheduler) {
    return new StepEvaluator(
        computationHashFactory(), this::container, computationCache(), scheduler, bytecodeF());
  }

  public ComputationHashFactory computationHashFactory() {
    return new ComputationHashFactory(Hash.of(123));
  }

  public NativeApi nativeApi() {
    return container();
  }

  public Container container() {
    return container(nativeMethodLoader());
  }

  public Container container(NativeMethodLoader nativeMethodLoader) {
    return new Container(projectBucket(), fileContentReader(), bytecodeF(), nativeMethodLoader);
  }

  @Override
  public BytecodeFactory bytecodeF() {
    return bytecodeFactory.get();
  }

  private BytecodeFactory newBytecodeFactory() {
    return new BytecodeFactory(exprDb(), kindDb());
  }

  @Override
  public BKindDb kindDb() {
    return kindDb.get();
  }

  private BKindDb newKindDb() {
    return new BKindDb(hashedDb());
  }

  public BExprDb exprDb() {
    return exprDb.get();
  }

  private BExprDb newExprDb() {
    return new BExprDb(hashedDb(), kindDb());
  }

  public ComputationCache computationCache() {
    var computationCache = new ComputationCache(computationCacheBucket(), exprDb(), bytecodeF());
    throwExceptionOnFailure(computationCache.execute());
    return computationCache;
  }

  public Bucket computationCacheBucket() {
    return new SubBucket(projectBucket(), Path.path("cache"));
  }

  public Bucket projectBucket() {
    return projectBucket.get();
  }

  public BExprDb exprDbOther() {
    return new BExprDb(hashedDb(), kindDbOther());
  }

  public BKindDb kindDbOther() {
    return newKindDb();
  }

  public HashedDb hashedDb() {
    return hashedDb.get();
  }

  private HashedDb createHashDb() {
    var hashedDb = new HashedDb(hashedDbBucket());
    throwExceptionOnFailure(hashedDb.execute());
    return hashedDb;
  }

  public Bucket hashedDbBucket() {
    return hashedDbBucket.get();
  }

  // Job related

  public static Job job(BExpr expr, BExpr... environment) {
    return job(expr, list(environment));
  }

  public static Job job(BExpr expr, List<BExpr> list) {
    return new Job(expr, list.map(TestingVm::job), new BTrace(), (j) -> promise());
  }

  public static Job job(BExpr expr) {
    return job(expr, list());
  }

  // Task, Computation, Output

  public Step task() throws BytecodeException {
    return orderTask();
  }

  public InvokeStep invokeTask() throws BytecodeException {
    return invokeTask(bInvoke(), bTrace());
  }

  public InvokeStep invokeTask(BInvoke invoke) {
    return invokeTask(invoke, null);
  }

  public InvokeStep invokeTask(BInvoke invoke, BTrace trace) {
    return new InvokeStep(invoke, trace);
  }

  public CombineStep combineTask() throws BytecodeException {
    return combineTask(bCombine(), bTrace());
  }

  public CombineStep combineTask(BCombine combine, BTrace trace) {
    return new CombineStep(combine, trace);
  }

  public SelectStep selectTask() throws BytecodeException {
    return selectTask(bSelect(), bTrace());
  }

  public SelectStep selectTask(BSelect select, BTrace trace) {
    return new SelectStep(select, trace);
  }

  public PickStep pickTask() throws BytecodeException {
    return pickTask(bPick(), bTrace());
  }

  public PickStep pickTask(BPick pick, BTrace trace) {
    return new PickStep(pick, trace);
  }

  public OrderStep orderTask() throws BytecodeException {
    return orderTask(bOrder(), bTrace());
  }

  public OrderStep orderTask(BOrder order, BTrace trace) {
    return new OrderStep(order, trace);
  }

  public BOutput output(BValue value) throws BytecodeException {
    return output(value, bLogArrayEmpty());
  }

  public BOutput output(BValue value, BArray messages) {
    return new BOutput(value, messages);
  }

  private static void throwExceptionOnFailure(Output<Tuple0> output) {
    if (containsFailure(output.report().logs())) {
      throw new RuntimeException(output.toString());
    }
  }

  private static SynchronizedBucket synchronizedMemoryBucket() {
    return new SynchronizedBucket(new MemoryBucket());
  }
}
