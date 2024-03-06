package org.smoothbuild.virtualmachine.testing;

import static java.lang.ClassLoader.getSystemClassLoader;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.filesystem.base.Path.path;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.synchronizedMemoryFileSystem;
import static org.smoothbuild.virtualmachine.evaluate.compute.ResultSource.DISK;
import static org.smoothbuild.virtualmachine.evaluate.compute.ResultSource.EXECUTION;

import jakarta.inject.Provider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import org.mockito.Mockito;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.filesystem.base.SubFileSystem;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CallB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CombineB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.OrderB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.PickB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.SelectB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.NativeFuncB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb;
import org.smoothbuild.virtualmachine.bytecode.load.BytecodeLoader;
import org.smoothbuild.virtualmachine.bytecode.load.BytecodeMethodLoader;
import org.smoothbuild.virtualmachine.bytecode.load.FilePersister;
import org.smoothbuild.virtualmachine.bytecode.load.JarClassLoaderFactory;
import org.smoothbuild.virtualmachine.bytecode.load.MethodLoader;
import org.smoothbuild.virtualmachine.bytecode.load.NativeMethodLoader;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryDb;
import org.smoothbuild.virtualmachine.evaluate.EvaluatorB;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationCache;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationResult;
import org.smoothbuild.virtualmachine.evaluate.compute.Computer;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;
import org.smoothbuild.virtualmachine.evaluate.compute.ResultSource;
import org.smoothbuild.virtualmachine.evaluate.execute.Job;
import org.smoothbuild.virtualmachine.evaluate.execute.ReferenceInlinerB;
import org.smoothbuild.virtualmachine.evaluate.execute.SchedulerB;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskExecutor;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskReporter;
import org.smoothbuild.virtualmachine.evaluate.execute.TraceB;
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
  private ExprDb exprDb;
  private CategoryDb categoryDb;
  private HashedDb hashedDb;
  private FileSystem projectFileSystem;
  private FileSystem hashedDbFileSystem;
  private ByteArrayOutputStream systemOut;

  public EvaluatorB evaluatorB(TaskReporter taskReporter) {
    return evaluatorB(taskExecutor(taskReporter));
  }

  public EvaluatorB evaluatorB() {
    return evaluatorB(() -> schedulerB());
  }

  public EvaluatorB evaluatorB(Provider<SchedulerB> schedulerB) {
    return new EvaluatorB(schedulerB, taskReporter());
  }

  public EvaluatorB evaluatorB(Provider<SchedulerB> schedulerB, TaskReporter taskReporter) {
    return new EvaluatorB(schedulerB, taskReporter);
  }

  public EvaluatorB evaluatorB(NativeMethodLoader nativeMethodLoader) {
    return evaluatorB(() -> schedulerB(nativeMethodLoader));
  }

  public EvaluatorB evaluatorB(TaskExecutor taskExecutor) {
    return evaluatorB(() -> schedulerB(taskExecutor));
  }

  public SchedulerB schedulerB() {
    return schedulerB(taskExecutor());
  }

  public SchedulerB schedulerB(NativeMethodLoader nativeMethodLoader) {
    return new SchedulerB(taskExecutor(nativeMethodLoader), bytecodeF(), varReducerB());
  }

  public SchedulerB schedulerB(TaskExecutor taskExecutor) {
    return new SchedulerB(taskExecutor, bytecodeF(), varReducerB());
  }

  public ReferenceInlinerB varReducerB() {
    return new ReferenceInlinerB(bytecodeF());
  }

  public SchedulerB schedulerB(int threadCount) {
    return schedulerB(computer(), taskReporter(), threadCount);
  }

  public SchedulerB schedulerB(TaskReporter reporter, int threadCount) {
    return schedulerB(computer(), reporter, threadCount);
  }

  public SchedulerB schedulerB(Computer computer, TaskReporter reporter, int threadCount) {
    return schedulerB(taskExecutor(computer, reporter, threadCount));
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
    return new Container(hashedDbFileSystem(), bytecodeF(), nativeMethodLoader);
  }

  @Override
  public BytecodeFactory bytecodeF() {
    if (bytecodeFactory == null) {
      bytecodeFactory = new BytecodeFactory(exprDb(), categoryDb());
    }
    return bytecodeFactory;
  }

  @Override
  public CategoryDb categoryDb() {
    if (categoryDb == null) {
      categoryDb = new CategoryDb(hashedDb());
    }
    return categoryDb;
  }

  public ExprDb exprDb() {
    if (exprDb == null) {
      exprDb = new ExprDb(hashedDb(), categoryDb());
    }
    return exprDb;
  }

  public ComputationCache computationCache() {
    return new ComputationCache(computationCacheFileSystem(), exprDb(), bytecodeF());
  }

  public FileSystem computationCacheFileSystem() {
    return new SubFileSystem(projectFileSystem(), Path.path("cache"));
  }

  public FileSystem projectFileSystem() {
    if (projectFileSystem == null) {
      projectFileSystem = synchronizedMemoryFileSystem();
      try {
        initializeDirs(projectFileSystem);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return projectFileSystem;
  }

  // TODO This will not be needed once HashedDb, ComputationCache, ArtifactSaver have initialize()
  // method that creates those directories
  public static void initializeDirs(FileSystem projectFileSystem) throws IOException {
    var dirs =
        list(path(".smooth/hashed"), path(".smooth/computations"), path(".smooth/artifacts"));
    for (Path path : dirs) {
      initializeDir(projectFileSystem, path);
    }
  }

  public static void initializeDir(FileSystem fileSystem, Path dir) throws IOException {
    switch (fileSystem.pathState(dir)) {
      case DIR -> {}
      case FILE -> throw new IOException(
          "Cannot create directory at " + dir.q() + " because it is a file.");
      case NOTHING -> fileSystem.createDir(dir);
    }
  }

  public ExprDb exprDbOther() {
    return new ExprDb(hashedDb(), categoryDbOther());
  }

  public CategoryDb categoryDbOther() {
    return new CategoryDb(hashedDb());
  }

  public HashedDb hashedDb() {
    if (hashedDb == null) {
      hashedDb = new HashedDb(hashedDbFileSystem());
    }
    return hashedDb;
  }

  public FileSystem hashedDbFileSystem() {
    if (hashedDbFileSystem == null) {
      hashedDbFileSystem = projectFileSystem();
    }
    return hashedDbFileSystem;
  }

  // Job related

  public static Job job(ExprB exprB, ExprB... environment) {
    return new Job(exprB, list(environment).map(TestingVirtualMachine::job), new TraceB());
  }

  public static Job job(ExprB exprB, Job... environment) {
    return new Job(exprB, list(environment), new TraceB());
  }

  public static Job job(ExprB exprB) {
    return new Job(exprB, list(), new TraceB());
  }

  // Task, Computation, Output

  public Task task() throws BytecodeException {
    return orderTask();
  }

  public InvokeTask invokeTask() throws BytecodeException {
    return invokeTask(callB(), nativeFuncB(), traceB());
  }

  public InvokeTask invokeTask(CallB callB, NativeFuncB nativeFuncB) throws BytecodeException {
    return invokeTask(callB, nativeFuncB, null);
  }

  public InvokeTask invokeTask(CallB callB, NativeFuncB nativeFuncB, TraceB trace)
      throws BytecodeException {
    return new InvokeTask(callB, nativeFuncB, trace);
  }

  public CombineTask combineTask() throws BytecodeException {
    return combineTask(combineB(), traceB());
  }

  public CombineTask combineTask(CombineB combineB, TraceB trace) {
    return new CombineTask(combineB, trace);
  }

  public SelectTask selectTask() throws BytecodeException {
    return selectTask(selectB(), traceB());
  }

  public SelectTask selectTask(SelectB selectB, TraceB trace) {
    return new SelectTask(selectB, trace);
  }

  public PickTask pickTask() throws BytecodeException {
    return pickTask(pickB(), traceB());
  }

  public PickTask pickTask(PickB pickB, TraceB trace) {
    return new PickTask(pickB, trace);
  }

  public OrderTask orderTask() throws BytecodeException {
    return orderTask(orderB(), traceB(Hash.of(7), Hash.of(9)));
  }

  public OrderTask orderTask(OrderB orderB, TraceB trace) {
    return new OrderTask(orderB, trace);
  }

  public ConstTask constTask() throws BytecodeException {
    return constTask(intB(7));
  }

  public static ConstTask constTask(ValueB valueB) {
    return constTask(valueB, traceB());
  }

  public static ConstTask constTask(ValueB valueB, TraceB trace) {
    return new ConstTask(valueB, trace);
  }

  public ComputationResult computationResult(ValueB valueB) throws BytecodeException {
    return computationResult(output(valueB), DISK);
  }

  public ComputationResult computationResult(ValueB valueB, ResultSource source)
      throws BytecodeException {
    return computationResult(output(valueB), source);
  }

  public static ComputationResult computationResult(Output output, ResultSource source) {
    return new ComputationResult(output, source);
  }

  public ComputationResult computationResultWithMessages(ArrayB messages) throws BytecodeException {
    return computationResult(output(intB(), messages), EXECUTION);
  }

  public Output output(ValueB valueB) throws BytecodeException {
    return output(valueB, logArrayEmpty());
  }

  public Output output(ValueB valueB, ArrayB messages) {
    return new Output(valueB, messages);
  }
}
