package org.smoothbuild.virtualmachine.testing;

import static java.lang.ClassLoader.getSystemClassLoader;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.filesystem.base.PathS.path;
import static org.smoothbuild.virtualmachine.evaluate.compute.ResultSource.DISK;
import static org.smoothbuild.virtualmachine.evaluate.compute.ResultSource.EXECUTION;

import jakarta.inject.Provider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import okio.ByteString;
import org.mockito.Mockito;
import org.smoothbuild.common.Constants;
import org.smoothbuild.common.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.PathS;
import org.smoothbuild.common.io.Okios;
import org.smoothbuild.common.reflect.Classes;
import org.smoothbuild.testing.TestExpressionS;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeF;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.IoBytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CallB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CombineB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.OrderB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.PickB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.SelectB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.VarB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BlobB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BlobBBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BoolB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.IfFuncB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.IntB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.LambdaB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.MapFuncB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.NativeFuncB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.StringB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb;
import org.smoothbuild.virtualmachine.bytecode.load.BytecodeLoader;
import org.smoothbuild.virtualmachine.bytecode.load.BytecodeMethodLoader;
import org.smoothbuild.virtualmachine.bytecode.load.FilePersister;
import org.smoothbuild.virtualmachine.bytecode.load.JarClassLoaderFactory;
import org.smoothbuild.virtualmachine.bytecode.load.MethodLoader;
import org.smoothbuild.virtualmachine.bytecode.load.NativeMethodLoader;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryDb;
import org.smoothbuild.virtualmachine.bytecode.type.oper.CallCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.CombineCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.OrderCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.PickCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.SelectCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.VarCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.ArrayTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.BlobTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.BoolTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.FuncTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.IfFuncCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.IntTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.LambdaCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.MapFuncCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.NativeFuncCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.StringTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TupleTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;
import org.smoothbuild.virtualmachine.evaluate.EvaluatorB;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationCache;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationCacheConfig;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationResult;
import org.smoothbuild.virtualmachine.evaluate.compute.Computer;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;
import org.smoothbuild.virtualmachine.evaluate.compute.ResultSource;
import org.smoothbuild.virtualmachine.evaluate.execute.Job;
import org.smoothbuild.virtualmachine.evaluate.execute.SchedulerB;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskExecutor;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskReporter;
import org.smoothbuild.virtualmachine.evaluate.execute.TraceB;
import org.smoothbuild.virtualmachine.evaluate.execute.VarReducerB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;
import org.smoothbuild.virtualmachine.evaluate.task.CombineTask;
import org.smoothbuild.virtualmachine.evaluate.task.ConstTask;
import org.smoothbuild.virtualmachine.evaluate.task.InvokeTask;
import org.smoothbuild.virtualmachine.evaluate.task.OrderTask;
import org.smoothbuild.virtualmachine.evaluate.task.Output;
import org.smoothbuild.virtualmachine.evaluate.task.PickTask;
import org.smoothbuild.virtualmachine.evaluate.task.SelectTask;
import org.smoothbuild.virtualmachine.evaluate.task.Task;

public class TestVirtualMachine extends TestExpressionS {
  private BytecodeF bytecodeF;
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

  public VarReducerB varReducerB() {
    return new VarReducerB(bytecodeF());
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
    return new SystemOutTaskReporter();
  }

  public ByteArrayOutputStream systemOut() {
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

  public BytecodeF bytecodeF() {
    if (bytecodeF == null) {
      bytecodeF = new BytecodeF(exprDb(), categoryDb());
    }
    return bytecodeF;
  }

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
    return new ComputationCache(
        new ComputationCacheConfig(projectFileSystem(), PathS.path("cache")),
        exprDb(),
        bytecodeF());
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
    for (PathS pathS : dirs) {
      initializeDir(projectFileSystem, pathS);
    }
  }

  public static void initializeDir(FileSystem fileSystem, PathS dir) throws IOException {
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
    return new Job(exprB, list(environment).map(TestVirtualMachine::job), new TraceB());
  }

  public static Job job(ExprB exprB, Job... environment) {
    return new Job(exprB, list(environment), new TraceB());
  }

  public static Job job(ExprB exprB) {
    return new Job(exprB, list(), new TraceB());
  }

  // InstB types

  public TupleTB animalTB() throws BytecodeException {
    return tupleTB(stringTB(), intTB());
  }

  public ArrayTB arrayTB() throws BytecodeException {
    return arrayTB(stringTB());
  }

  public ArrayTB arrayTB(TypeB elemT) throws BytecodeException {
    return categoryDb().array(elemT);
  }

  public BlobTB blobTB() throws BytecodeException {
    return categoryDb().blob();
  }

  public BoolTB boolTB() throws BytecodeException {
    return categoryDb().bool();
  }

  public TupleTB fileTB() throws BytecodeException {
    return tupleTB(stringTB(), blobTB());
  }

  public LambdaCB lambdaCB() throws BytecodeException {
    return lambdaCB(blobTB(), stringTB(), intTB());
  }

  public LambdaCB lambdaCB(TypeB resultT) throws BytecodeException {
    return categoryDb().lambda(funcTB(resultT));
  }

  public LambdaCB lambdaCB(TypeB param, TypeB resultT) throws BytecodeException {
    return categoryDb().lambda(funcTB(param, resultT));
  }

  public LambdaCB lambdaCB(TypeB param1, TypeB param2, TypeB resultT) throws BytecodeException {
    return categoryDb().lambda(funcTB(param1, param2, resultT));
  }

  public FuncTB funcTB() throws BytecodeException {
    return funcTB(blobTB(), stringTB(), intTB());
  }

  public FuncTB funcTB(TypeB resultT) throws BytecodeException {
    return funcTB(list(), resultT);
  }

  public FuncTB funcTB(TypeB param1, TypeB resultT) throws BytecodeException {
    return funcTB(list(param1), resultT);
  }

  public FuncTB funcTB(TypeB param1, TypeB param2, TypeB resultT) throws BytecodeException {
    return funcTB(list(param1, param2), resultT);
  }

  public FuncTB funcTB(List<TypeB> paramTs, TypeB resultT) throws BytecodeException {
    return categoryDb().funcT(paramTs, resultT);
  }

  public IntTB intTB() throws BytecodeException {
    return categoryDb().int_();
  }

  public NativeFuncCB nativeFuncCB() throws BytecodeException {
    return nativeFuncCB(boolTB(), blobTB());
  }

  public NativeFuncCB nativeFuncCB(TypeB resultT) throws BytecodeException {
    return categoryDb().nativeFunc(funcTB(resultT));
  }

  public NativeFuncCB nativeFuncCB(TypeB param, TypeB resultT) throws BytecodeException {
    return categoryDb().nativeFunc(funcTB(param, resultT));
  }

  public NativeFuncCB nativeFuncCB(TypeB param1, TypeB param2, TypeB resultT)
      throws BytecodeException {
    return categoryDb().nativeFunc(funcTB(param1, param2, resultT));
  }

  public TupleTB personTB() throws BytecodeException {
    return tupleTB(stringTB(), stringTB());
  }

  public StringTB stringTB() throws BytecodeException {
    return categoryDb().string();
  }

  public TupleTB tupleTB(TypeB... itemTs) throws BytecodeException {
    return categoryDb().tuple(itemTs);
  }

  // OperB categories

  public CallCB callCB() throws BytecodeException {
    return callCB(intTB());
  }

  public CallCB callCB(TypeB evaluationT) throws BytecodeException {
    return categoryDb().call(evaluationT);
  }

  public CombineCB combineCB(TypeB... itemTs) throws BytecodeException {
    return categoryDb().combine(tupleTB(itemTs));
  }

  public IfFuncCB ifFuncCB() throws BytecodeException {
    return ifFuncCB(intTB());
  }

  public IfFuncCB ifFuncCB(TypeB t) throws BytecodeException {
    return categoryDb().ifFunc(t);
  }

  public MapFuncCB mapFuncCB() throws BytecodeException {
    return mapFuncCB(intTB(), boolTB());
  }

  public MapFuncCB mapFuncCB(TypeB r, TypeB s) throws BytecodeException {
    return categoryDb().mapFunc(r, s);
  }

  public OrderCB orderCB() throws BytecodeException {
    return orderCB(intTB());
  }

  public OrderCB orderCB(TypeB elemT) throws BytecodeException {
    return categoryDb().order(arrayTB(elemT));
  }

  public PickCB pickCB() throws BytecodeException {
    return pickCB(intTB());
  }

  public PickCB pickCB(TypeB evaluationT) throws BytecodeException {
    return categoryDb().pick(evaluationT);
  }

  public VarCB varCB() throws BytecodeException {
    return varCB(intTB());
  }

  public VarCB varCB(TypeB evaluationT) throws BytecodeException {
    return categoryDb().var(evaluationT);
  }

  public SelectCB selectCB() throws BytecodeException {
    return selectCB(intTB());
  }

  public SelectCB selectCB(TypeB evaluationT) throws BytecodeException {
    return categoryDb().select(evaluationT);
  }

  // ValueB-s

  public TupleB animalB() throws BytecodeException {
    return animalB("rabbit", 7);
  }

  public TupleB animalB(String species, int speed) throws BytecodeException {
    return animalB(stringB(species), intB(speed));
  }

  public TupleB animalB(StringB species, IntB speed) throws BytecodeException {
    return tupleB(species, speed);
  }

  public ArrayB arrayB(ValueB... elems) throws BytecodeException {
    return arrayB(elems[0].evaluationT(), elems);
  }

  public ArrayB arrayB(TypeB elemT, ValueB... elems) throws BytecodeException {
    return exprDb().arrayBuilder(arrayTB(elemT)).addAll(list(elems)).build();
  }

  public BlobB blobBJarWithPluginApi(Class<?>... classes) throws BytecodeException {
    return blobBWith(list(classes)
        .append(
            BlobB.class,
            NativeApi.class,
            ExprB.class,
            StringB.class,
            TupleB.class,
            ValueB.class,
            BytecodeException.class));
  }

  public BlobB blobBJarWithJavaByteCode(Class<?>... classes) throws BytecodeException {
    return blobBWith(list(classes));
  }

  private BlobB blobBWith(java.util.List<Class<?>> list) throws BytecodeException {
    try (var blobBBuilder = exprDb().blobBuilder()) {
      Classes.saveBytecodeInJar(blobBBuilder, list);
      return blobBBuilder.build();
    } catch (IOException e) {
      throw new IoBytecodeException(e);
    }
  }

  public BlobB blobB() throws BytecodeException {
    return bytecodeF().blob(sink -> sink.writeUtf8("blob data"));
  }

  public BlobB blobB(int data) throws BytecodeException {
    return blobB(Okios.intToByteString(data));
  }

  public BlobB blobB(ByteString bytes) throws BytecodeException {
    return bytecodeF().blob(sink -> sink.write(bytes));
  }

  public BlobBBuilder blobBBuilder() throws BytecodeException {
    return exprDb().blobBuilder();
  }

  public BoolB boolB() throws BytecodeException {
    return boolB(true);
  }

  public BoolB boolB(boolean value) throws BytecodeException {
    return exprDb().bool(value);
  }

  public TupleB fileB(PathS path) throws BytecodeException {
    return fileB(path, path.toString());
  }

  public TupleB fileB(PathS path, String content) throws BytecodeException {
    return fileB(path.toString(), content);
  }

  public TupleB fileB(String path, String content) throws BytecodeException {
    return fileB(path, ByteString.encodeString(content, Constants.CHARSET));
  }

  public TupleB fileB(PathS path, ByteString content) throws BytecodeException {
    return fileB(path.toString(), content);
  }

  public TupleB fileB(String path, ByteString content) throws BytecodeException {
    return fileB(path, blobB(content));
  }

  public TupleB fileB(String path, BlobB blob) throws BytecodeException {
    StringB string = bytecodeF().string(path);
    return bytecodeF().file(blob, string);
  }

  public LambdaB lambdaB() throws BytecodeException {
    return lambdaB(intB());
  }

  public LambdaB lambdaB(ExprB body) throws BytecodeException {
    return lambdaB(list(), body);
  }

  public LambdaB lambdaB(List<TypeB> paramTs, ExprB body) throws BytecodeException {
    var funcTB = funcTB(paramTs, body.evaluationT());
    return lambdaB(funcTB, body);
  }

  public LambdaB lambdaB(FuncTB type, ExprB body) throws BytecodeException {
    return exprDb().lambda(type, body);
  }

  public LambdaB idFuncB() throws BytecodeException {
    return lambdaB(list(intTB()), varB(intTB(), 0));
  }

  public LambdaB returnAbcFuncB() throws BytecodeException {
    return lambdaB(stringB("abc"));
  }

  public NativeFuncB returnAbcNativeFuncB() throws IOException, BytecodeException {
    return returnAbcNativeFuncB(true);
  }

  public NativeFuncB returnAbcNativeFuncB(boolean isPure) throws IOException, BytecodeException {
    return nativeFuncB(funcTB(stringTB()), ReturnAbcFunc.class, isPure);
  }

  public IntB intB() throws BytecodeException {
    return intB(17);
  }

  public IntB intB(int value) throws BytecodeException {
    return intB(BigInteger.valueOf(value));
  }

  public IntB intB(BigInteger value) throws BytecodeException {
    return exprDb().int_(value);
  }

  public NativeFuncB returnAbcNativeFunc() throws IOException, BytecodeException {
    var funcTB = funcTB(stringTB());
    return nativeFuncB(funcTB, ReturnAbcFunc.class);
  }

  public static class ReturnAbcFunc {
    public static ValueB func(NativeApi nativeApi, TupleB args) throws BytecodeException {
      return nativeApi.factory().string("abc");
    }
  }

  public NativeFuncB nativeFuncB(Class<?> clazz) throws IOException, BytecodeException {
    return nativeFuncB(funcTB(), clazz);
  }

  public NativeFuncB nativeFuncB(FuncTB funcTB, Class<?> clazz)
      throws IOException, BytecodeException {
    return nativeFuncB(funcTB, clazz, true);
  }

  public NativeFuncB nativeFuncB(FuncTB funcTB, Class<?> clazz, boolean isPure)
      throws IOException, BytecodeException {
    return nativeFuncB(
        funcTB, blobBJarWithPluginApi(clazz), stringB(clazz.getName()), boolB(isPure));
  }

  public NativeFuncB nativeFuncB() throws BytecodeException {
    return nativeFuncB(funcTB());
  }

  public NativeFuncB nativeFuncB(FuncTB funcTB) throws BytecodeException {
    return nativeFuncB(funcTB, blobB(7), stringB("class binary name"), boolB(true));
  }

  public NativeFuncB nativeFuncB(FuncTB type, BlobB jar, StringB classBinaryName)
      throws BytecodeException {
    return nativeFuncB(type, jar, classBinaryName, boolB(true));
  }

  public NativeFuncB nativeFuncB(FuncTB type, BlobB jar, StringB classBinaryName, BoolB isPure)
      throws BytecodeException {
    return exprDb().nativeFunc(type, jar, classBinaryName, isPure);
  }

  public TupleB personB(String firstName, String lastName) throws BytecodeException {
    return tupleB(stringB(firstName), stringB(lastName));
  }

  public StringB stringB() throws BytecodeException {
    return exprDb().string("abc");
  }

  public StringB stringB(String string) throws BytecodeException {
    return exprDb().string(string);
  }

  public TupleB tupleB(ValueB... items) throws BytecodeException {
    return exprDb().tuple(list(items));
  }

  public ArrayB messageArrayWithOneError() throws BytecodeException {
    return arrayB(bytecodeF().errorMessage("error message"));
  }

  public ArrayB messageArrayEmpty() throws BytecodeException {
    return arrayB(bytecodeF().messageT());
  }

  public TupleB fatalMessage() throws BytecodeException {
    return fatalMessage("fatal message");
  }

  public TupleB fatalMessage(String text) throws BytecodeException {
    return bytecodeF().fatalMessage(text);
  }

  public TupleB errorMessage() throws BytecodeException {
    return errorMessage("error message");
  }

  public TupleB errorMessage(String text) throws BytecodeException {
    return bytecodeF().errorMessage(text);
  }

  public TupleB warningMessage() throws BytecodeException {
    return warningMessage("warning message");
  }

  public TupleB warningMessage(String text) throws BytecodeException {
    return bytecodeF().warningMessage(text);
  }

  public TupleB infoMessage() throws BytecodeException {
    return infoMessage("info message");
  }

  public TupleB infoMessage(String text) throws BytecodeException {
    return bytecodeF().infoMessage(text);
  }

  // OperB-s

  public CallB callB() throws BytecodeException {
    return callB(idFuncB(), intB());
  }

  public CallB callB(ExprB func, ExprB... args) throws BytecodeException {
    return callB(func, combineB(args));
  }

  public CallB callB(ExprB func, CombineB args) throws BytecodeException {
    return exprDb().call(func, args);
  }

  public CombineB combineB(ExprB... items) throws BytecodeException {
    return exprDb().combine(list(items));
  }

  public IfFuncB ifFuncB(TypeB t) throws BytecodeException {
    return exprDb().ifFunc(t);
  }

  public MapFuncB mapFuncB(TypeB r, TypeB s) throws BytecodeException {
    return exprDb().mapFunc(r, s);
  }

  public OrderB orderB() throws BytecodeException {
    return orderB(intTB());
  }

  public OrderB orderB(ExprB... elems) throws BytecodeException {
    return orderB(elems[0].evaluationT(), elems);
  }

  public OrderB orderB(TypeB elemT, ExprB... elems) throws BytecodeException {
    var elemList = list(elems);
    return exprDb().order(arrayTB(elemT), elemList);
  }

  public PickB pickB() throws BytecodeException {
    return pickB(arrayB(intB()), intB(0));
  }

  public PickB pickB(ExprB array, ExprB index) throws BytecodeException {
    return exprDb().pick(array, index);
  }

  public VarB varB(int index) throws BytecodeException {
    return varB(intTB(), index);
  }

  public VarB varB(TypeB evaluationT, int index) throws BytecodeException {
    return exprDb().varB(evaluationT, intB(index));
  }

  public SelectB selectB() throws BytecodeException {
    return exprDb().select(tupleB(intB()), intB(0));
  }

  public SelectB selectB(ExprB tuple, IntB index) throws BytecodeException {
    return exprDb().select(tuple, index);
  }

  public static TraceB traceB() {
    return new TraceB();
  }

  public static TraceB traceB(ExprB call, ExprB called) {
    return new TraceB(call.hash(), called.hash());
  }

  public static TraceB traceB(ExprB call, ExprB called, TraceB tail) {
    return traceB(call.hash(), called.hash(), tail);
  }

  public static TraceB traceB(Hash call, Hash called, TraceB tail) {
    return new TraceB(call, called, tail);
  }

  public static TraceB traceB(Hash call, Hash called) {
    return new TraceB(call, called);
  }

  // ValS types

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
    return output(valueB, messageArrayEmpty());
  }

  public Output output(ValueB valueB, ArrayB messages) {
    return new Output(valueB, messages);
  }
}
