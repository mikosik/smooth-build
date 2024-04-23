package org.smoothbuild.virtualmachine.testing;

import static com.google.common.base.Suppliers.memoize;
import static java.lang.ClassLoader.getSystemClassLoader;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.ResultSource.DISK;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.synchronizedMemoryBucket;
import static org.smoothbuild.virtualmachine.evaluate.task.InvokeTask.newInvokeTask;

import com.google.common.base.Supplier;
import com.google.inject.Guice;
import com.google.inject.Injector;
import jakarta.inject.Provider;
import org.mockito.Mockito;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.common.bucket.base.SubBucket;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.concurrent.PromisedValue;
import org.smoothbuild.common.log.base.ResultSource;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.schedule.Scheduler;
import org.smoothbuild.common.testing.MemoryReporter;
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
import org.smoothbuild.virtualmachine.evaluate.BEvaluator;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationCache;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationResult;
import org.smoothbuild.virtualmachine.evaluate.compute.Computer;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;
import org.smoothbuild.virtualmachine.evaluate.execute.BReferenceInliner;
import org.smoothbuild.virtualmachine.evaluate.execute.BScheduler;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;
import org.smoothbuild.virtualmachine.evaluate.execute.Job;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;
import org.smoothbuild.virtualmachine.evaluate.task.CombineTask;
import org.smoothbuild.virtualmachine.evaluate.task.ConstTask;
import org.smoothbuild.virtualmachine.evaluate.task.InvokeTask;
import org.smoothbuild.virtualmachine.evaluate.task.OrderTask;
import org.smoothbuild.virtualmachine.evaluate.task.Output;
import org.smoothbuild.virtualmachine.evaluate.task.PickTask;
import org.smoothbuild.virtualmachine.evaluate.task.SelectTask;
import org.smoothbuild.virtualmachine.evaluate.task.Task;

public class TestingVirtualMachine extends TestingBytecode {
  private final Supplier<BytecodeFactory> bytecodeFactory = memoize(this::newBytecodeFactory);
  private final Supplier<BExprDb> exprDb = memoize(this::newExprDb);
  private final Supplier<BKindDb> kindDb = memoize(this::newKindDb);
  private final Supplier<HashedDb> hashedDb = memoize(this::createHashDb);
  private final Supplier<Bucket> projectBucket = memoize(() -> synchronizedMemoryBucket());
  private final Supplier<Bucket> hashedDbBucket = memoize(() -> synchronizedMemoryBucket());
  private final Supplier<MemoryReporter> reporter = memoize(MemoryReporter::new);
  private final Supplier<Scheduler> scheduler = memoize(() -> scheduler(reporter()));

  public BEvaluator bEvaluator(Reporter reporter) {
    return bEvaluator(() -> bScheduler(scheduler(reporter)), reporter);
  }

  public BEvaluator bEvaluator() {
    return bEvaluator(() -> bScheduler());
  }

  public BEvaluator bEvaluator(Provider<BScheduler> schedulerB) {
    return bEvaluator(schedulerB, reporter());
  }

  public BEvaluator bEvaluator(Provider<BScheduler> schedulerB, Reporter reporter) {
    return new BEvaluator(schedulerB, reporter);
  }

  public BEvaluator bEvaluator(NativeMethodLoader nativeMethodLoader) {
    return bEvaluator(() -> bScheduler(nativeMethodLoader));
  }

  public BScheduler bScheduler() {
    return bScheduler(scheduler());
  }

  public BScheduler bScheduler(NativeMethodLoader nativeMethodLoader) {
    return new BScheduler(
        scheduler(), computer(nativeMethodLoader), bytecodeF(), bReferenceInliner());
  }

  public BScheduler bScheduler(Scheduler scheduler) {
    return new BScheduler(scheduler, computer(), bytecodeF(), bReferenceInliner());
  }

  public Scheduler scheduler(Reporter reporter) {
    return scheduler(Guice.createInjector(), reporter);
  }

  private static Scheduler scheduler(Injector injector, Reporter reporter) {
    return new Scheduler(injector, reporter);
  }

  public Scheduler scheduler() {
    return scheduler.get();
  }

  public BReferenceInliner bReferenceInliner() {
    return new BReferenceInliner(bytecodeF());
  }

  public BScheduler bScheduler(int threadCount) {
    return bScheduler(computer(), reporter(), threadCount);
  }

  public BScheduler bScheduler(Reporter reporter, int threadCount) {
    return bScheduler(computer(), reporter, threadCount);
  }

  public BScheduler bScheduler(Computer computer, Reporter reporter, int threadCount) {
    var scheduler1 = new Scheduler(Guice.createInjector(), reporter, threadCount);
    return new BScheduler(scheduler1, computer, bytecodeF(), bReferenceInliner());
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

  public MemoryReporter reporter() {
    return reporter.get();
  }

  public Computer computer() {
    return new Computer(Hash.of(123), this::container, computationCache());
  }

  public Computer computer(NativeMethodLoader nativeMethodLoader) {
    return new Computer(Hash.of(123), () -> container(nativeMethodLoader), computationCache());
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
    computationCache.initialize().toMaybe().getOrThrow(RuntimeException::new);
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
    hashedDb.initialize().toMaybe().getOrThrow(RuntimeException::new);
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
    return new Job(
        expr, list.map(TestingVirtualMachine::job), new BTrace(), (j) -> new PromisedValue<>());
  }

  public static Job job(BExpr expr) {
    return job(expr, list());
  }

  // Task, Computation, Output

  public Task task() throws BytecodeException {
    return orderTask();
  }

  public InvokeTask invokeTask() throws BytecodeException {
    return invokeTask(bInvoke(), bTrace());
  }

  public InvokeTask invokeTask(BInvoke invoke) {
    return invokeTask(invoke, null);
  }

  public InvokeTask invokeTask(BInvoke invoke, BTrace trace) {
    return newInvokeTask(invoke, trace);
  }

  public CombineTask combineTask() throws BytecodeException {
    return combineTask(bCombine(), bTrace());
  }

  public CombineTask combineTask(BCombine combine, BTrace trace) {
    return new CombineTask(combine, trace);
  }

  public SelectTask selectTask() throws BytecodeException {
    return selectTask(bSelect(), bTrace());
  }

  public SelectTask selectTask(BSelect select, BTrace trace) {
    return new SelectTask(select, trace);
  }

  public PickTask pickTask() throws BytecodeException {
    return pickTask(bPick(), bTrace());
  }

  public PickTask pickTask(BPick pick, BTrace trace) {
    return new PickTask(pick, trace);
  }

  public OrderTask orderTask() throws BytecodeException {
    return orderTask(bOrder(), bTrace());
  }

  public OrderTask orderTask(BOrder order, BTrace trace) {
    return new OrderTask(order, trace);
  }

  public ConstTask constTask() throws BytecodeException {
    return constTask(bInt(7));
  }

  public static ConstTask constTask(BValue value) {
    return constTask(value, bTrace());
  }

  public static ConstTask constTask(BValue value, BTrace trace) {
    return new ConstTask(value, trace);
  }

  public ComputationResult computationResult(BValue value) throws BytecodeException {
    return computationResult(output(value), DISK);
  }

  public ComputationResult computationResult(BValue value, ResultSource source)
      throws BytecodeException {
    return computationResult(output(value), source);
  }

  public static ComputationResult computationResult(Output output, ResultSource source) {
    return new ComputationResult(output, source);
  }

  public Output output(BValue value) throws BytecodeException {
    return output(value, bLogArrayEmpty());
  }

  public Output output(BValue value, BArray messages) {
    return new Output(value, messages);
  }
}
