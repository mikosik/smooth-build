package org.smoothbuild.testing;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static java.lang.ClassLoader.getSystemClassLoader;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.common.filesystem.base.PathS.path;
import static org.smoothbuild.common.io.Okios.intToByteString;
import static org.smoothbuild.common.reflect.Classes.saveBytecodeInJar;
import static org.smoothbuild.compile.frontend.lang.base.location.Locations.fileLocation;
import static org.smoothbuild.compile.frontend.lang.define.ItemS.toTypes;
import static org.smoothbuild.compile.frontend.lang.type.AnnotationNames.BYTECODE;
import static org.smoothbuild.compile.frontend.lang.type.AnnotationNames.NATIVE_IMPURE;
import static org.smoothbuild.compile.frontend.lang.type.AnnotationNames.NATIVE_PURE;
import static org.smoothbuild.compile.frontend.lang.type.VarSetS.varSetS;
import static org.smoothbuild.filesystem.project.ProjectSpaceLayout.DEFAULT_MODULE_PATH;
import static org.smoothbuild.filesystem.project.ProjectSpaceLayout.initializeDirs;
import static org.smoothbuild.filesystem.space.Space.PROJECT;
import static org.smoothbuild.out.log.Level.INFO;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.run.eval.report.TaskMatchers.ALL;
import static org.smoothbuild.vm.evaluate.compute.ResultSource.DISK;
import static org.smoothbuild.vm.evaluate.compute.ResultSource.EXECUTION;

import jakarta.inject.Provider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.function.Function;
import okio.ByteString;
import org.smoothbuild.common.bindings.ImmutableBindings;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.common.collect.Named;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.PathS;
import org.smoothbuild.common.filesystem.base.SynchronizedFileSystem;
import org.smoothbuild.common.filesystem.mem.MemoryFileSystem;
import org.smoothbuild.compile.backend.BackendCompile;
import org.smoothbuild.compile.backend.BsMapping;
import org.smoothbuild.compile.backend.SbTranslator;
import org.smoothbuild.compile.frontend.compile.ast.define.CallP;
import org.smoothbuild.compile.frontend.compile.ast.define.ExplicitTP;
import org.smoothbuild.compile.frontend.compile.ast.define.ExprP;
import org.smoothbuild.compile.frontend.compile.ast.define.ImplicitTP;
import org.smoothbuild.compile.frontend.compile.ast.define.InstantiateP;
import org.smoothbuild.compile.frontend.compile.ast.define.IntP;
import org.smoothbuild.compile.frontend.compile.ast.define.ItemP;
import org.smoothbuild.compile.frontend.compile.ast.define.LambdaP;
import org.smoothbuild.compile.frontend.compile.ast.define.ModuleP;
import org.smoothbuild.compile.frontend.compile.ast.define.NamedEvaluableP;
import org.smoothbuild.compile.frontend.compile.ast.define.NamedFuncP;
import org.smoothbuild.compile.frontend.compile.ast.define.NamedValueP;
import org.smoothbuild.compile.frontend.compile.ast.define.PolymorphicP;
import org.smoothbuild.compile.frontend.compile.ast.define.ReferenceP;
import org.smoothbuild.compile.frontend.compile.ast.define.StructP;
import org.smoothbuild.compile.frontend.lang.base.location.Location;
import org.smoothbuild.compile.frontend.lang.define.AnnotatedFuncS;
import org.smoothbuild.compile.frontend.lang.define.AnnotatedValueS;
import org.smoothbuild.compile.frontend.lang.define.AnnotationS;
import org.smoothbuild.compile.frontend.lang.define.BlobS;
import org.smoothbuild.compile.frontend.lang.define.CallS;
import org.smoothbuild.compile.frontend.lang.define.CombineS;
import org.smoothbuild.compile.frontend.lang.define.ConstructorS;
import org.smoothbuild.compile.frontend.lang.define.ExprS;
import org.smoothbuild.compile.frontend.lang.define.InstantiateS;
import org.smoothbuild.compile.frontend.lang.define.IntS;
import org.smoothbuild.compile.frontend.lang.define.ItemS;
import org.smoothbuild.compile.frontend.lang.define.ItemSigS;
import org.smoothbuild.compile.frontend.lang.define.LambdaS;
import org.smoothbuild.compile.frontend.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.frontend.lang.define.NamedExprFuncS;
import org.smoothbuild.compile.frontend.lang.define.NamedExprValueS;
import org.smoothbuild.compile.frontend.lang.define.NamedValueS;
import org.smoothbuild.compile.frontend.lang.define.OrderS;
import org.smoothbuild.compile.frontend.lang.define.PolymorphicS;
import org.smoothbuild.compile.frontend.lang.define.ReferenceS;
import org.smoothbuild.compile.frontend.lang.define.SelectS;
import org.smoothbuild.compile.frontend.lang.define.StringS;
import org.smoothbuild.compile.frontend.lang.define.TraceS;
import org.smoothbuild.compile.frontend.lang.define.TraceS.Element;
import org.smoothbuild.compile.frontend.lang.type.ArrayTS;
import org.smoothbuild.compile.frontend.lang.type.BlobTS;
import org.smoothbuild.compile.frontend.lang.type.BoolTS;
import org.smoothbuild.compile.frontend.lang.type.FuncSchemaS;
import org.smoothbuild.compile.frontend.lang.type.FuncTS;
import org.smoothbuild.compile.frontend.lang.type.IntTS;
import org.smoothbuild.compile.frontend.lang.type.InterfaceTS;
import org.smoothbuild.compile.frontend.lang.type.SchemaS;
import org.smoothbuild.compile.frontend.lang.type.StringTS;
import org.smoothbuild.compile.frontend.lang.type.StructTS;
import org.smoothbuild.compile.frontend.lang.type.TempVarS;
import org.smoothbuild.compile.frontend.lang.type.TupleTS;
import org.smoothbuild.compile.frontend.lang.type.TypeFS;
import org.smoothbuild.compile.frontend.lang.type.TypeS;
import org.smoothbuild.compile.frontend.lang.type.VarS;
import org.smoothbuild.compile.frontend.lang.type.VarSetS;
import org.smoothbuild.filesystem.space.FilePath;
import org.smoothbuild.filesystem.space.Space;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.out.report.SystemOutReporter;
import org.smoothbuild.run.eval.report.TaskReporterImpl;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.ExprDb;
import org.smoothbuild.vm.bytecode.expr.exc.IoBytecodeException;
import org.smoothbuild.vm.bytecode.expr.oper.CallB;
import org.smoothbuild.vm.bytecode.expr.oper.CombineB;
import org.smoothbuild.vm.bytecode.expr.oper.OrderB;
import org.smoothbuild.vm.bytecode.expr.oper.PickB;
import org.smoothbuild.vm.bytecode.expr.oper.SelectB;
import org.smoothbuild.vm.bytecode.expr.oper.VarB;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.BlobB;
import org.smoothbuild.vm.bytecode.expr.value.BlobBBuilder;
import org.smoothbuild.vm.bytecode.expr.value.BoolB;
import org.smoothbuild.vm.bytecode.expr.value.IfFuncB;
import org.smoothbuild.vm.bytecode.expr.value.IntB;
import org.smoothbuild.vm.bytecode.expr.value.LambdaB;
import org.smoothbuild.vm.bytecode.expr.value.MapFuncB;
import org.smoothbuild.vm.bytecode.expr.value.NativeFuncB;
import org.smoothbuild.vm.bytecode.expr.value.StringB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.hashed.HashedDb;
import org.smoothbuild.vm.bytecode.load.BytecodeLoader;
import org.smoothbuild.vm.bytecode.load.BytecodeMethodLoader;
import org.smoothbuild.vm.bytecode.load.FilePersister;
import org.smoothbuild.vm.bytecode.load.JarClassLoaderFactory;
import org.smoothbuild.vm.bytecode.load.MethodLoader;
import org.smoothbuild.vm.bytecode.load.NativeMethodLoader;
import org.smoothbuild.vm.bytecode.type.CategoryDb;
import org.smoothbuild.vm.bytecode.type.oper.CallCB;
import org.smoothbuild.vm.bytecode.type.oper.CombineCB;
import org.smoothbuild.vm.bytecode.type.oper.OrderCB;
import org.smoothbuild.vm.bytecode.type.oper.PickCB;
import org.smoothbuild.vm.bytecode.type.oper.SelectCB;
import org.smoothbuild.vm.bytecode.type.oper.VarCB;
import org.smoothbuild.vm.bytecode.type.value.ArrayTB;
import org.smoothbuild.vm.bytecode.type.value.BlobTB;
import org.smoothbuild.vm.bytecode.type.value.BoolTB;
import org.smoothbuild.vm.bytecode.type.value.FuncTB;
import org.smoothbuild.vm.bytecode.type.value.IfFuncCB;
import org.smoothbuild.vm.bytecode.type.value.IntTB;
import org.smoothbuild.vm.bytecode.type.value.LambdaCB;
import org.smoothbuild.vm.bytecode.type.value.MapFuncCB;
import org.smoothbuild.vm.bytecode.type.value.NativeFuncCB;
import org.smoothbuild.vm.bytecode.type.value.StringTB;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;
import org.smoothbuild.vm.evaluate.EvaluatorB;
import org.smoothbuild.vm.evaluate.compute.ComputationCache;
import org.smoothbuild.vm.evaluate.compute.ComputationResult;
import org.smoothbuild.vm.evaluate.compute.Computer;
import org.smoothbuild.vm.evaluate.compute.Container;
import org.smoothbuild.vm.evaluate.compute.ResultSource;
import org.smoothbuild.vm.evaluate.execute.Job;
import org.smoothbuild.vm.evaluate.execute.SchedulerB;
import org.smoothbuild.vm.evaluate.execute.TaskExecutor;
import org.smoothbuild.vm.evaluate.execute.TaskReporter;
import org.smoothbuild.vm.evaluate.execute.TraceB;
import org.smoothbuild.vm.evaluate.execute.VarReducerB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;
import org.smoothbuild.vm.evaluate.task.CombineTask;
import org.smoothbuild.vm.evaluate.task.ConstTask;
import org.smoothbuild.vm.evaluate.task.InvokeTask;
import org.smoothbuild.vm.evaluate.task.OrderTask;
import org.smoothbuild.vm.evaluate.task.Output;
import org.smoothbuild.vm.evaluate.task.PickTask;
import org.smoothbuild.vm.evaluate.task.SelectTask;
import org.smoothbuild.vm.evaluate.task.Task;

public class TestContext {
  public static final String BUILD_FILE_PATH = "build.smooth";
  private static final String IMPORTED_FILE_PATH = "imported.smooth";

  private BytecodeF bytecodeF;
  private ExprDb exprDb;
  private CategoryDb categoryDb;
  private HashedDb hashedDb;
  private FileSystem projectFileSystem;
  private FileSystem hashedDbFileSystem;
  private ByteArrayOutputStream systemOut;

  public EvaluatorB evaluatorB(Reporter reporter) {
    return evaluatorB(taskExecutor(reporter));
  }

  public EvaluatorB evaluatorB(TaskReporter taskReporter) {
    return evaluatorB(taskExecutor(taskReporter));
  }

  public EvaluatorB evaluatorB() {
    return evaluatorB(() -> schedulerB());
  }

  public EvaluatorB evaluatorB(Provider<SchedulerB> schedulerB) {
    return new EvaluatorB(schedulerB, reporter());
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
    return schedulerB(computer(), reporter(), threadCount);
  }

  public SchedulerB schedulerB(TaskReporter reporter, int threadCount) {
    return schedulerB(computer(), reporter, threadCount);
  }

  public SchedulerB schedulerB(Computer computer, TaskReporter reporter, int threadCount) {
    return schedulerB(taskExecutor(computer, reporter, threadCount));
  }

  public SchedulerB schedulerB(Computer computer, Reporter reporter, int threadCount) {
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
    return new TaskExecutor(computer(nativeMethodLoader), reporter(), taskReporter);
  }

  public TaskExecutor taskExecutor(Computer computer, TaskReporter taskReporter, int threadCount) {
    return new TaskExecutor(computer, reporter(), taskReporter, threadCount);
  }

  public TaskExecutor taskExecutor(Reporter reporter) {
    return new TaskExecutor(computer(), reporter, taskReporter(reporter));
  }

  public TaskExecutor taskExecutor(Computer computer, Reporter reporter, int threadCount) {
    return new TaskExecutor(computer, reporter, taskReporter(reporter), threadCount);
  }

  public BackendCompile sbTranslatorFacade(
      FilePersister filePersister, BytecodeLoader bytecodeLoader) {
    return new BackendCompile(bytecodeF(), filePersister, bytecodeLoader);
  }

  public SbTranslator sbTranslator(ImmutableBindings<NamedEvaluableS> evaluables) {
    return sbTranslator(filePersister(), evaluables);
  }

  public SbTranslator sbTranslator(
      FilePersister filePersister, ImmutableBindings<NamedEvaluableS> evaluables) {
    return sbTranslator(filePersister, bytecodeLoader(), evaluables);
  }

  private SbTranslator sbTranslator(
      FilePersister filePersister,
      BytecodeLoader bytecodeLoader,
      ImmutableBindings<NamedEvaluableS> evaluables) {
    return new SbTranslator(bytecodeF(), filePersister, bytecodeLoader, evaluables);
  }

  private FilePersister filePersister() {
    return mock(FilePersister.class);
  }

  private BytecodeLoader bytecodeLoader() {
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

  public TestingModuleLoader module(String code) {
    return new TestingModuleLoader(code);
  }

  public TaskReporterImpl taskReporter() {
    return taskReporter(reporter());
  }

  public TaskReporterImpl taskReporter(Reporter reporter) {
    return new TaskReporterImpl(ALL, reporter, bsMapping());
  }

  public SystemOutReporter reporter() {
    return new SystemOutReporter(new PrintWriter(systemOut(), true), INFO);
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
    return new ComputationCache(projectFileSystem(), exprDb(), bytecodeF());
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
    return new Job(exprB, list(environment).map(TestContext::job), new TraceB());
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
      saveBytecodeInJar(blobBBuilder, list);
      return blobBBuilder.build();
    } catch (IOException e) {
      throw new IoBytecodeException(e);
    }
  }

  public BlobB blobB() throws BytecodeException {
    return bytecodeF().blob(sink -> sink.writeUtf8("blob data"));
  }

  public BlobB blobB(int data) throws BytecodeException {
    return blobB(intToByteString(data));
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
    return fileB(path, ByteString.encodeString(content, CHARSET));
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

  public static java.util.List<TypeS> typesToTest() {
    return nonCompositeTypes().stream()
        .flatMap(t -> compositeTypeSFactories().stream().map(f -> f.apply(t)))
        .toList();
  }

  public static List<TypeS> nonCompositeTypes() {
    return TypeFS.baseTs().append(new VarS("A"));
  }

  public static java.util.List<Function<TypeS, TypeS>> compositeTypeSFactories() {
    java.util.List<Function<TypeS, TypeS>> simpleFactories = java.util.List.of(
        TestContext::arrayTS,
        TestContext::funcTS,
        t -> funcTS(t, intTS()),
        TestContext::tupleTS,
        TestContext::structTS,
        TestContext::interfaceTS);
    java.util.List<Function<TypeS, TypeS>> factories = new ArrayList<>();
    factories.addAll(simpleFactories);
    for (var simpleFactory : simpleFactories) {
      for (var simpleFactory2 : simpleFactories) {
        Function<TypeS, TypeS> compositeFactory = t -> simpleFactory.apply(simpleFactory2.apply(t));
        factories.add(compositeFactory);
      }
    }
    return factories;
  }

  public static ArrayTS arrayTS(TypeS elemT) {
    return new ArrayTS(elemT);
  }

  public static BlobTS blobTS() {
    return TypeFS.BLOB;
  }

  public static BoolTS boolTS() {
    return TypeFS.BOOL;
  }

  public static FuncTS funcTS(TypeS resultT) {
    return funcTS(list(), resultT);
  }

  public static FuncTS funcTS(TypeS param1, TypeS resultT) {
    return funcTS(list(param1), resultT);
  }

  public static FuncTS funcTS(TypeS param1, TypeS param2, TypeS resultT) {
    return funcTS(list(param1, param2), resultT);
  }

  public static FuncTS funcTS(List<TypeS> paramTs, TypeS resultT) {
    return new FuncTS(tupleTS(paramTs), resultT);
  }

  public static TupleTS tupleTS(TypeS... itemTs) {
    return tupleTS(list(itemTs));
  }

  public static TupleTS tupleTS(List<TypeS> paramTs) {
    return new TupleTS(paramTs);
  }

  public static IntTS intTS() {
    return TypeFS.INT;
  }

  public static FuncSchemaS funcSchemaS(NList<ItemS> params, TypeS resultT) {
    return funcSchemaS(toTypes(params.list()), resultT);
  }

  public static FuncSchemaS funcSchemaS(TypeS resultT) {
    return funcSchemaS(funcTS(list(), resultT));
  }

  public static FuncSchemaS funcSchemaS(TypeS param1, TypeS resultT) {
    return funcSchemaS(funcTS(list(param1), resultT));
  }

  public static FuncSchemaS funcSchemaS(List<TypeS> paramTs, TypeS resultT) {
    return funcSchemaS(funcTS(paramTs, resultT));
  }

  private static FuncSchemaS funcSchemaS(FuncTS funcTS) {
    return funcSchemaS(funcTS.vars(), funcTS);
  }

  private static FuncSchemaS funcSchemaS(VarSetS quantifiedVars, FuncTS funcTS) {
    return new FuncSchemaS(quantifiedVars, funcTS);
  }

  public static InterfaceTS interfaceTS() {
    return interfaceTS(map());
  }

  public static InterfaceTS interfaceTS(TypeS... fieldTs) {
    return interfaceTS(typesToItemSigsMap(fieldTs));
  }

  public static InterfaceTS interfaceTS(ItemSigS... fieldTs) {
    return interfaceTS(itemSigsToMap(fieldTs));
  }

  public static InterfaceTS interfaceTS(Map<String, ItemSigS> fieldSignatures) {
    return new InterfaceTS(fieldSignatures);
  }

  public static SchemaS schemaS(TypeS typeS) {
    return new SchemaS(typeS.vars(), typeS);
  }

  public static StructTS personTS() {
    return structTS("Person", nlist(sigS(stringTS(), "firstName"), sigS(stringTS(), "lastName")));
  }

  public static StructTS animalTS() {
    return structTS("Animal", nlist(sigS(stringTS(), "name"), sigS(intTS(), "size")));
  }

  public static StringTS stringTS() {
    return TypeFS.STRING;
  }

  public static StructTS structTS(TypeS... fieldTs) {
    return structTS("MyStruct", fieldTs);
  }

  public static StructTS structTS(String myStruct) {
    return structTS(myStruct, nlist());
  }

  public static StructTS structTS(String myStruct, TypeS... fieldTs) {
    return structTS(myStruct, nlist(typesToItemSigs(fieldTs)));
  }

  public static StructTS structTS(String myStruct, ItemSigS... fieldSigs) {
    return structTS(myStruct, nlist(fieldSigs));
  }

  private static List<ItemSigS> typesToItemSigs(TypeS... fieldTs) {
    var builder = new ArrayList<ItemSigS>();
    for (int i = 0; i < fieldTs.length; i++) {
      builder.add(sigS(fieldTs[i], "param" + i));
    }
    return listOfAll(builder);
  }

  public static Map<String, ItemSigS> typesToItemSigsMap(TypeS... types) {
    return itemSigsToMap(typeTsToSigS(types));
  }

  public static Map<String, ItemSigS> itemSigsToMap(ItemSigS... itemSigs) {
    return itemSigsToMap(list(itemSigs));
  }

  public static Map<String, ItemSigS> itemSigsToMap(List<ItemSigS> sigs) {
    return sigs.toMap(ItemSigS::name, f -> f);
  }

  private static List<ItemSigS> typeTsToSigS(TypeS... types) {
    var builder = new ArrayList<ItemSigS>();
    for (int i = 0; i < types.length; i++) {
      builder.add(sigS(types[i], "param" + i));
    }
    return listOfAll(builder);
  }

  public static StructTS structTS(String name, NList<ItemSigS> fields) {
    return new StructTS(name, fields);
  }

  public static VarS tempVarA() {
    return tempVar("1");
  }

  public static TempVarS tempVar(String name) {
    return new TempVarS(name);
  }

  public static VarS varA() {
    return varS("A");
  }

  public static VarS varB() {
    return varS("B");
  }

  public static VarS varC() {
    return varS("C");
  }

  public static VarS varX() {
    return varS("X");
  }

  public static VarS varS(String name) {
    return new VarS(name);
  }

  // ExprS-s

  public static BlobS blobS(int data) {
    return blobS(1, data);
  }

  public static BlobS blobS(int line, int data) {
    return new BlobS(blobTS(), intToByteString(data), location(line));
  }

  public static CallS callS(ExprS callable, ExprS... args) {
    return callS(1, callable, args);
  }

  public static CallS callS(int line, ExprS callable, ExprS... args) {
    return new CallS(callable, combineS(line, args), location(line));
  }

  public static CombineS combineS(ExprS... args) {
    return combineS(13, args);
  }

  private static CombineS combineS(int line, ExprS... args) {
    var argsList = list(args);
    var evaluationT = new TupleTS(argsList.map(ExprS::evaluationT));
    return new CombineS(evaluationT, argsList, location(line));
  }

  public static IntS intS(int value) {
    return intS(1, value);
  }

  public static IntS intS(int line, int value) {
    return new IntS(intTS(), BigInteger.valueOf(value), location(line));
  }

  public static Map<VarS, TypeS> varMap() {
    return map();
  }

  public static Map<VarS, TypeS> varMap(VarS var, TypeS type) {
    return map(var, type);
  }

  public static InstantiateS instantiateS(NamedEvaluableS namedEvaluableS) {
    return instantiateS(17, namedEvaluableS);
  }

  public static InstantiateS instantiateS(int line, NamedEvaluableS namedEvaluableS) {
    return instantiateS(line, referenceS(line, namedEvaluableS));
  }

  public static InstantiateS instantiateS(List<TypeS> typeArgs, NamedEvaluableS namedEvaluableS) {
    return instantiateS(1, typeArgs, namedEvaluableS);
  }

  public static InstantiateS instantiateS(
      int line, List<TypeS> typeArgs, NamedEvaluableS namedEvaluableS) {
    var location = location(line);
    var referenceS = new ReferenceS(namedEvaluableS.schema(), namedEvaluableS.name(), location);
    return instantiateS(typeArgs, referenceS, location);
  }

  public static InstantiateS instantiateS(PolymorphicS polymorphicS) {
    return instantiateS(polymorphicS, polymorphicS.location());
  }

  public static InstantiateS instantiateS(int line, PolymorphicS polymorphicS) {
    return instantiateS(polymorphicS, location(line));
  }

  public static InstantiateS instantiateS(PolymorphicS polymorphicS, Location location) {
    return instantiateS(list(), polymorphicS, location);
  }

  public static InstantiateS instantiateS(List<TypeS> typeArgs, PolymorphicS polymorphicS) {
    return instantiateS(1, typeArgs, polymorphicS);
  }

  public static InstantiateS instantiateS(
      int line, List<TypeS> typeArgs, PolymorphicS polymorphicS) {
    return instantiateS(typeArgs, polymorphicS, location(line));
  }

  public static InstantiateS instantiateS(
      List<TypeS> typeArgs, PolymorphicS polymorphicS, Location location) {
    return new InstantiateS(typeArgs, polymorphicS, location);
  }

  public static OrderS orderS(int line, ExprS headElem, ExprS... tailElems) {
    return new OrderS(
        arrayTS(headElem.evaluationT()), list(headElem).append(tailElems), location(line));
  }

  public static OrderS orderS(TypeS elemT, ExprS... exprs) {
    return orderS(1, elemT, exprs);
  }

  public static OrderS orderS(int line, TypeS elemT, ExprS... exprs) {
    return new OrderS(arrayTS(elemT), list(exprs), location(line));
  }

  public static InstantiateS paramRefS(TypeS type, String name) {
    return paramRefS(1, type, name);
  }

  public static InstantiateS paramRefS(int line, TypeS type, String name) {
    return instantiateS(line, referenceS(line, new SchemaS(varSetS(), type), name));
  }

  public static ReferenceS referenceS(int line, NamedEvaluableS namedEvaluableS) {
    return referenceS(line, namedEvaluableS.schema(), namedEvaluableS.name());
  }

  public static ReferenceS referenceS(int line, SchemaS schema, String name) {
    return new ReferenceS(schema, name, location(line));
  }

  public static SelectS selectS(ExprS selectable, String field) {
    return selectS(1, selectable, field);
  }

  public static SelectS selectS(int line, ExprS selectable, String field) {
    return new SelectS(selectable, field, location(line));
  }

  public static StringS stringS() {
    return stringS("abc");
  }

  public static StringS stringS(String string) {
    return stringS(1, string);
  }

  public static StringS stringS(int line, String data) {
    return new StringS(stringTS(), data, location(line));
  }

  // other smooth language thingies

  private static AnnotationS bytecodeS(String path) {
    return bytecodeS(1, path);
  }

  public static AnnotationS bytecodeS(int line, String path) {
    return bytecodeS(line, stringS(line, path));
  }

  public static AnnotationS bytecodeS(int line, StringS path) {
    return bytecodeS(path, location(line));
  }

  public static AnnotationS bytecodeS(String path, Location location) {
    return bytecodeS(stringS(path), location);
  }

  public static AnnotationS bytecodeS(StringS path, Location location) {
    return new AnnotationS(BYTECODE, path, location);
  }

  public static AnnotationS nativeAnnotationS() {
    return nativeAnnotationS(1, stringS("impl"));
  }

  public static AnnotationS nativeAnnotationS(int line, StringS classBinaryName) {
    return nativeAnnotationS(line, classBinaryName, true);
  }

  public static AnnotationS nativeAnnotationS(int line, StringS classBinaryName, boolean pure) {
    return nativeAnnotationS(location(line), classBinaryName, pure);
  }

  public static AnnotationS nativeAnnotationS(Location location, StringS classBinaryName) {
    return nativeAnnotationS(location, classBinaryName, true);
  }

  public static AnnotationS nativeAnnotationS(
      Location location, StringS classBinaryName, boolean pure) {
    var name = pure ? NATIVE_PURE : NATIVE_IMPURE;
    return new AnnotationS(name, classBinaryName, location);
  }

  public static ItemS itemS(TypeS type) {
    return itemS(1, type, "paramName");
  }

  public static ItemS itemS(TypeS type, String name) {
    return itemS(1, type, name);
  }

  public static ItemS itemS(int line, TypeS type, String name) {
    return itemS(line, type, name, none());
  }

  public static ItemS itemS(int line, TypeS type, String name, ExprS body) {
    return itemS(line, type, name, some(body));
  }

  public static ItemS itemS(String name, ExprS body) {
    return itemS(body.evaluationT(), name, some(body));
  }

  public static ItemS itemS(TypeS type, String name, Maybe<ExprS> body) {
    return itemS(1, type, name, body);
  }

  public static ItemS itemS(int line, TypeS type, String name, Maybe<ExprS> body) {
    return itemSPoly(line, type, name, body.map(b -> valueS(line, name, b)));
  }

  public static ItemS itemS(int line, TypeS type, String name, NamedValueS body) {
    return itemSPoly(line, type, name, some(body));
  }

  public static ItemS itemSPoly(int line, TypeS type, String name, Maybe<NamedValueS> body) {
    return new ItemS(type, name, body, location(line));
  }

  public static AnnotatedValueS bytecodeValueS(int line, TypeS type, String name) {
    return annotatedValueS(line, bytecodeS(line - 1, "impl"), type, name);
  }

  public static AnnotatedValueS annotatedValueS(
      int line, AnnotationS annotationS, TypeS type, String name) {
    return annotatedValueS(annotationS, type, name, location(line));
  }

  public static AnnotatedValueS annotatedValueS(
      AnnotationS annotationS, TypeS type, String name, Location location) {
    return new AnnotatedValueS(annotationS, schemaS(type), name, location);
  }

  public static NamedExprValueS valueS(String name, ExprS body) {
    return valueS(1, name, body);
  }

  public static NamedExprValueS valueS(int line, String name, ExprS body) {
    return valueS(line, body.evaluationT(), name, body);
  }

  public static NamedExprValueS valueS(int line, TypeS type, String name, ExprS body) {
    return new NamedExprValueS(schemaS(type), name, body, location(line));
  }

  public static NamedValueS emptyArrayValueS() {
    return emptyArrayValueS(varA());
  }

  public static NamedValueS emptyArrayValueS(VarS elemT) {
    return valueS("emptyArray", orderS(elemT));
  }

  public static ConstructorS constructorS(StructTS structT) {
    return constructorS(1, structT, UPPER_CAMEL.to(LOWER_CAMEL, structT.name()));
  }

  public static ConstructorS constructorS(int line, StructTS structT) {
    return constructorS(line, structT, structT.name());
  }

  public static ConstructorS constructorS(int line, StructTS structT, String name) {
    var fields = structT.fields();
    var params = fields.map(f -> new ItemS(f.type(), f.name(), none(), location(2)));
    return new ConstructorS(funcSchemaS(params, structT), name, params, location(line));
  }

  public static AnnotatedFuncS bytecodeFuncS(
      String path, TypeS resultT, String name, NList<ItemS> params) {
    return bytecodeFuncS(1, path, resultT, name, params);
  }

  public static AnnotatedFuncS bytecodeFuncS(
      int line, TypeS resultT, String name, NList<ItemS> params) {
    return annotatedFuncS(line, bytecodeS(line - 1, "impl"), resultT, name, params);
  }

  public static AnnotatedFuncS bytecodeFuncS(
      int line, String path, TypeS resultT, String name, NList<ItemS> params) {
    return annotatedFuncS(line, bytecodeS(path), resultT, name, params);
  }

  public static AnnotatedFuncS nativeFuncS(TypeS resultT, String name, NList<ItemS> params) {
    return annotatedFuncS(nativeAnnotationS(), resultT, name, params);
  }

  public static AnnotatedFuncS annotatedFuncS(
      AnnotationS ann, TypeS resultT, String name, NList<ItemS> params) {
    return annotatedFuncS(1, ann, resultT, name, params);
  }

  public static AnnotatedFuncS annotatedFuncS(
      int line, AnnotationS ann, TypeS resultT, String name, NList<ItemS> params) {
    return annotatedFuncS(ann, resultT, name, params, location(line));
  }

  public static AnnotatedFuncS annotatedFuncS(
      AnnotationS ann, TypeS resultT, String name, NList<ItemS> params, Location location) {
    return new AnnotatedFuncS(ann, funcSchemaS(params, resultT), name, params, location);
  }

  public static NamedExprFuncS funcS(int line, String name, NList<ItemS> params, ExprS body) {
    return funcS(line, body.evaluationT(), name, params, body);
  }

  public static NamedExprFuncS funcS(String name, NList<ItemS> params, ExprS body) {
    return funcS(body.evaluationT(), name, params, body);
  }

  public static NamedExprFuncS funcS(TypeS resultT, String name, NList<ItemS> params, ExprS body) {
    return funcS(1, resultT, name, params, body);
  }

  public static NamedExprFuncS funcS(
      int line, TypeS resultT, String name, NList<ItemS> params, ExprS body) {
    var schema = funcSchemaS(params, resultT);
    return new NamedExprFuncS(schema, name, params, body, location(line));
  }

  public static LambdaS lambdaS(VarSetS quantifiedVars, ExprS body) {
    return lambdaS(quantifiedVars, nlist(), body);
  }

  public static LambdaS lambdaS(VarSetS quantifiedVars, NList<ItemS> params, ExprS body) {
    return lambdaS(1, quantifiedVars, params, body);
  }

  public static LambdaS lambdaS(int line, VarSetS quantifiedVars, NList<ItemS> params, ExprS body) {
    var funcTS = funcTS(toTypes(params.list()), body.evaluationT());
    var funcSchemaS = funcSchemaS(quantifiedVars, funcTS);
    return new LambdaS(funcSchemaS, params, body, location(line));
  }

  public static LambdaS lambdaS(ExprS body) {
    return lambdaS(1, nlist(), body);
  }

  public static LambdaS lambdaS(NList<ItemS> params, ExprS body) {
    return lambdaS(1, params, body);
  }

  public static LambdaS lambdaS(int line, NList<ItemS> params, ExprS body) {
    var funcSchemaS = funcSchemaS(toTypes(params.list()), body.evaluationT());
    return new LambdaS(funcSchemaS, params, body, location(line));
  }

  public static NamedExprFuncS idFuncS() {
    var a = varA();
    return funcS(a, "myId", nlist(itemS(a, "a")), paramRefS(a, "a"));
  }

  public static NamedExprFuncS intIdFuncS() {
    return funcS(intTS(), "myIntId", nlist(itemS(intTS(), "i")), paramRefS(intTS(), "i"));
  }

  public static NamedExprFuncS returnIntFuncS() {
    return funcS(intTS(), "myReturnInt", nlist(), intS(1, 3));
  }

  public static ItemSigS sigS(TypeS type, String name) {
    return new ItemSigS(type, name);
  }

  public static TraceS traceS() {
    return new TraceS();
  }

  public static TraceS traceS(String name2, int line2, String name1, int line1) {
    return traceS(name2, location(line2), name1, location(line1));
  }

  public static TraceS traceS(String name2, Location location2, String name1, Location location1) {
    var element1 = new Element(name1, location1, null);
    var element2 = new Element(name2, location2, element1);
    return new TraceS(element2);
  }

  public static TraceS traceS(String name, int line) {
    return traceS(name, location(line));
  }

  public static TraceS traceS(String name, Location location) {
    return new TraceS(new TraceS.Element(name, location, null));
  }

  // P - parsed objects

  public static ModuleP moduleP(List<StructP> structs, List<NamedEvaluableP> evaluables) {
    return new ModuleP("", structs, evaluables);
  }

  public static InstantiateP lambdaP(NList<ItemP> params, ExprP body) {
    return instantiateP(new LambdaP("^1", params, body, location()));
  }

  public static CallP callP(ExprP callee) {
    return callP(callee, location());
  }

  public static CallP callP(ExprP callee, Location location) {
    return new CallP(callee, list(), location);
  }

  public static InstantiateP instantiateP(PolymorphicP polymorphicP) {
    return new InstantiateP(polymorphicP, polymorphicP.location());
  }

  public static NamedFuncP namedFuncP() {
    return namedFuncP(nlist(itemP()));
  }

  public static NamedFuncP namedFuncP(String name) {
    return namedFuncP(name, nlist(itemP()));
  }

  public static NamedFuncP namedFuncP(String name, int line) {
    return namedFuncP(name, nlist(itemP()), none(), location(line));
  }

  public static NamedFuncP namedFuncP(NList<ItemP> params) {
    return namedFuncP("myFunc", params);
  }

  public static NamedFuncP namedFuncP(String name, ExprP body) {
    return namedFuncP(name, nlist(), some(body));
  }

  public static NamedFuncP namedFuncP(String name, NList<ItemP> params) {
    return namedFuncP(name, params, none());
  }

  public static NamedFuncP namedFuncP(String name, NList<ItemP> params, Maybe<ExprP> body) {
    return namedFuncP(name, params, body, location());
  }

  public static NamedFuncP namedFuncP(
      String name, NList<ItemP> params, Maybe<ExprP> body, Location location) {
    var resultT = new ImplicitTP(location);
    return new NamedFuncP(resultT, name, shortName(name), params, body, none(), location);
  }

  public static NamedValueP namedValueP() {
    return namedValueP(intP());
  }

  public static NamedValueP namedValueP(ExprP body) {
    return namedValueP("myValue", body);
  }

  public static NamedValueP namedValueP(String name) {
    return namedValueP(name, intP());
  }

  public static NamedValueP namedValueP(String name, ExprP body) {
    var location = location();
    var type = new ImplicitTP(location);
    return new NamedValueP(type, name, shortName(name), some(body), none(), location);
  }

  public static ItemP itemP() {
    return itemP(some(namedValueP()));
  }

  public static ItemP itemP(Maybe<NamedValueP> defaultValue) {
    return itemP("param1", defaultValue);
  }

  public static ItemP itemP(String name) {
    return itemP(name, none());
  }

  public static ItemP itemP(String name, ExprP defaultValue) {
    return itemP(name, namedValueP(defaultValue));
  }

  public static ItemP itemP(String name, NamedValueP defaultValue) {
    return itemP(name, some(defaultValue));
  }

  public static ItemP itemP(String name, Maybe<NamedValueP> defaultValue) {
    return new ItemP(new ExplicitTP("Int", location()), name, defaultValue, location());
  }

  public static IntP intP() {
    return new IntP("7", location());
  }

  public static InstantiateP referenceP(String name) {
    return referenceP(name, location(7));
  }

  public static InstantiateP referenceP(String name, Location location) {
    return instantiateP(new ReferenceP(name, location));
  }

  // location

  public static Location location() {
    return location(11);
  }

  public static Location location(int line) {
    return location(filePath(), line);
  }

  public static Location location(Space space) {
    return location(filePath(space, path("path")), 17);
  }

  public static Location location(FilePath filePath, int line) {
    return fileLocation(filePath, line);
  }

  public static FilePath filePath(Space space, PathS path) {
    return FilePath.filePath(space, path);
  }

  public static FilePath filePath() {
    return smoothFilePath();
  }

  public static FilePath smoothFilePath() {
    return filePath(BUILD_FILE_PATH);
  }

  public static FilePath nativeFilePath() {
    return smoothFilePath().withExtension("jar");
  }

  public static FilePath importedFilePath() {
    return filePath(IMPORTED_FILE_PATH);
  }

  public static FilePath filePath(String filePath) {
    return new FilePath(PROJECT, path(filePath));
  }

  public static Log userFatal(int line, String message) {
    return fatal(userFileMessage(line, message));
  }

  public static Log userError(int line, String message) {
    return error(userFileMessage(line, message));
  }

  private static String userFileMessage(int line, String message) {
    return DEFAULT_MODULE_PATH.toString() + ":" + line + ": " + message;
  }

  public static SynchronizedFileSystem synchronizedMemoryFileSystem() {
    return new SynchronizedFileSystem(new MemoryFileSystem());
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
    return output(valueB, messageArrayEmpty());
  }

  public Output output(ValueB valueB, ArrayB messages) {
    return new Output(valueB, messages);
  }

  public static BsMapping bsMapping() {
    return new BsMapping(map(), map());
  }

  public static BsMapping bsMapping(Hash hash, String name) {
    return new BsMapping(map(hash, name), map());
  }

  public static BsMapping bsMapping(Hash hash, Location location) {
    return new BsMapping(map(), map(hash, location));
  }

  @SafeVarargs
  public static <T extends Named> ImmutableBindings<T> bindings(T... nameds) {
    return immutableBindings(list(nameds).toMap(Named::name, v -> v));
  }

  private static String shortName(String fullName) {
    return fullName.substring(Math.max(0, fullName.lastIndexOf(':')));
  }

  public static void writeFile(FileSystem fileSystem, PathS path, String content)
      throws IOException {
    try (var bufferedSink = fileSystem.sink(path)) {
      bufferedSink.writeUtf8(content);
    }
  }
}
