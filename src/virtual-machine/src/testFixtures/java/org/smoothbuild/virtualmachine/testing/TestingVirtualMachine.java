package org.smoothbuild.virtualmachine.testing;

import static java.lang.ClassLoader.getSystemClassLoader;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.ResultSource.DISK;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.synchronizedMemoryBucket;

import jakarta.inject.Provider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import org.mockito.Mockito;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.common.bucket.base.SubBucket;
import org.smoothbuild.common.log.base.ResultSource;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BPick;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BNativeFunc;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb;
import org.smoothbuild.virtualmachine.bytecode.load.BytecodeLoader;
import org.smoothbuild.virtualmachine.bytecode.load.BytecodeMethodLoader;
import org.smoothbuild.virtualmachine.bytecode.load.FilePersister;
import org.smoothbuild.virtualmachine.bytecode.load.JarClassLoaderFactory;
import org.smoothbuild.virtualmachine.bytecode.load.MethodLoader;
import org.smoothbuild.virtualmachine.bytecode.load.NativeMethodLoader;
import org.smoothbuild.virtualmachine.bytecode.type.BKindDb;
import org.smoothbuild.virtualmachine.evaluate.BEvaluator;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationCache;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationResult;
import org.smoothbuild.virtualmachine.evaluate.compute.Computer;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;
import org.smoothbuild.virtualmachine.evaluate.execute.BReferenceInliner;
import org.smoothbuild.virtualmachine.evaluate.execute.BScheduler;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;
import org.smoothbuild.virtualmachine.evaluate.execute.Job;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskExecutor;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskReporter;
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
  private BytecodeFactory bytecodeFactory;
  private BExprDb exprDb;
  private BKindDb kindDb;
  private HashedDb hashedDb;
  private Bucket projectBucket;
  private Bucket hashedDbBucket;
  private ByteArrayOutputStream systemOut;

  public BEvaluator bEvaluator(TaskReporter taskReporter) {
    return bEvaluator(taskExecutor(taskReporter));
  }

  public BEvaluator bEvaluator() {
    return bEvaluator(() -> bScheduler());
  }

  public BEvaluator bEvaluator(Provider<BScheduler> schedulerB) {
    return new BEvaluator(schedulerB, taskReporter());
  }

  public BEvaluator bEvaluator(Provider<BScheduler> schedulerB, TaskReporter taskReporter) {
    return new BEvaluator(schedulerB, taskReporter);
  }

  public BEvaluator bEvaluator(NativeMethodLoader nativeMethodLoader) {
    return bEvaluator(() -> bScheduler(nativeMethodLoader));
  }

  public BEvaluator bEvaluator(TaskExecutor taskExecutor) {
    return bEvaluator(() -> bScheduler(taskExecutor));
  }

  public BScheduler bScheduler() {
    return bScheduler(taskExecutor());
  }

  public BScheduler bScheduler(NativeMethodLoader nativeMethodLoader) {
    return new BScheduler(taskExecutor(nativeMethodLoader), bytecodeF(), bReferenceInliner());
  }

  public BScheduler bScheduler(TaskExecutor taskExecutor) {
    return new BScheduler(taskExecutor, bytecodeF(), bReferenceInliner());
  }

  public BReferenceInliner bReferenceInliner() {
    return new BReferenceInliner(bytecodeF());
  }

  public BScheduler bScheduler(int threadCount) {
    return bScheduler(computer(), taskReporter(), threadCount);
  }

  public BScheduler bScheduler(TaskReporter reporter, int threadCount) {
    return bScheduler(computer(), reporter, threadCount);
  }

  public BScheduler bScheduler(Computer computer, TaskReporter reporter, int threadCount) {
    return bScheduler(taskExecutor(computer, reporter, threadCount));
  }

  public NativeMethodLoader nativeMethodLoader() {
    return new NativeMethodLoader(methodLoader());
  }

  public TaskExecutor taskExecutor() {
    return taskExecutor(taskReporter());
  }

  public TaskExecutor taskExecutor(NativeMethodLoader nativeMethodLoader) {
    return taskExecutor(taskReporter(), nativeMethodLoader);
  }

  public TaskExecutor taskExecutor(TaskReporter taskReporter) {
    return taskExecutor(taskReporter, nativeMethodLoader());
  }

  public TaskExecutor taskExecutor(
      TaskReporter taskReporter, NativeMethodLoader nativeMethodLoader) {
    return new TaskExecutor(computer(nativeMethodLoader), taskReporter);
  }

  public TaskExecutor taskExecutor(Computer computer, TaskReporter taskReporter, int threadCount) {
    return new TaskExecutor(computer, taskReporter, threadCount);
  }

  public FilePersister filePersister() {
    return Mockito.mock(FilePersister.class);
  }

  public BytecodeLoader bytecodeLoader() {
    return new BytecodeLoader(bytecodeMethodLoader(), bytecodeF());
  }

  private BytecodeMethodLoader bytecodeMethodLoader() {
    return new BytecodeMethodLoader(methodLoader());
  }

  private MethodLoader methodLoader() {
    return new MethodLoader(jarClassLoaderFactory());
  }

  private JarClassLoaderFactory jarClassLoaderFactory() {
    return new JarClassLoaderFactory(bytecodeF(), getSystemClassLoader());
  }

  public TaskReporter taskReporter() {
    return new SystemOutTaskReporter(new PrintWriter(inMemorySystemOut()));
  }

  public ByteArrayOutputStream inMemorySystemOut() {
    if (systemOut == null) {
      systemOut = new ByteArrayOutputStream();
    }
    return systemOut;
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
    return new Container(hashedDbBucket(), bytecodeF(), nativeMethodLoader);
  }

  @Override
  public BytecodeFactory bytecodeF() {
    if (bytecodeFactory == null) {
      bytecodeFactory = new BytecodeFactory(exprDb(), kindDb());
    }
    return bytecodeFactory;
  }

  @Override
  public BKindDb kindDb() {
    if (kindDb == null) {
      kindDb = new BKindDb(hashedDb());
    }
    return kindDb;
  }

  public BExprDb exprDb() {
    if (exprDb == null) {
      exprDb = new BExprDb(hashedDb(), kindDb());
    }
    return exprDb;
  }

  public ComputationCache computationCache() {
    return new ComputationCache(computationCacheBucket(), exprDb(), bytecodeF());
  }

  public Bucket computationCacheBucket() {
    return new SubBucket(projectBucket(), Path.path("cache"));
  }

  public Bucket projectBucket() {
    if (projectBucket == null) {
      projectBucket = synchronizedMemoryBucket();
      try {
        initializeDirs(projectBucket);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return projectBucket;
  }

  // TODO This will not be needed once HashedDb, ComputationCache, ArtifactSaver have initialize()
  // method that creates those directories
  public static void initializeDirs(Bucket projectBucket) throws IOException {
    var dirs =
        list(path(".smooth/hashed"), path(".smooth/computations"), path(".smooth/artifacts"));
    for (Path path : dirs) {
      initializeDir(projectBucket, path);
    }
  }

  public static void initializeDir(Bucket bucket, Path dir) throws IOException {
    switch (bucket.pathState(dir)) {
      case DIR -> {}
      case FILE -> throw new IOException(
          "Cannot create directory at " + dir.q() + " because it is a file.");
      case NOTHING -> bucket.createDir(dir);
    }
  }

  public BExprDb exprDbOther() {
    return new BExprDb(hashedDb(), kindDbOther());
  }

  public BKindDb kindDbOther() {
    return new BKindDb(hashedDb());
  }

  public HashedDb hashedDb() {
    if (hashedDb == null) {
      hashedDb = new HashedDb(hashedDbBucket());
    }
    return hashedDb;
  }

  public Bucket hashedDbBucket() {
    if (hashedDbBucket == null) {
      hashedDbBucket = projectBucket();
    }
    return hashedDbBucket;
  }

  // Job related

  public static Job job(BExpr expr, BExpr... environment) {
    return new Job(expr, list(environment).map(TestingVirtualMachine::job), new BTrace());
  }

  public static Job job(BExpr expr, Job... environment) {
    return new Job(expr, list(environment), new BTrace());
  }

  public static Job job(BExpr expr) {
    return new Job(expr, list(), new BTrace());
  }

  // Task, Computation, Output

  public Task task() throws BytecodeException {
    return orderTask();
  }

  public InvokeTask invokeTask() throws BytecodeException {
    return invokeTask(bCall(), bNativeFunc(), bTrace());
  }

  public InvokeTask invokeTask(BCall call, BNativeFunc nativeFunc) throws BytecodeException {
    return invokeTask(call, nativeFunc, null);
  }

  public InvokeTask invokeTask(BCall call, BNativeFunc nativeFunc, BTrace trace)
      throws BytecodeException {
    return new InvokeTask(call, nativeFunc, trace);
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

  public ComputationResult computationResultWithMessages(BArray messages) throws BytecodeException {
    return computationResult(output(bInt(), messages), EXECUTION);
  }

  public Output output(BValue value) throws BytecodeException {
    return output(value, bLogArrayEmpty());
  }

  public Output output(BValue value, BArray messages) {
    return new Output(value, messages);
  }
}
