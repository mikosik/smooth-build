package org.smoothbuild.testing;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.io.ByteStreams.nullOutputStream;
import static java.lang.ClassLoader.getSystemClassLoader;
import static java.util.Optional.empty;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.compile.lang.base.ValidNamesS.structNameToCtorName;
import static org.smoothbuild.compile.lang.define.ItemS.toTypes;
import static org.smoothbuild.compile.lang.define.PolyValS.polyValS;
import static org.smoothbuild.compile.lang.type.AnnotationNames.BYTECODE;
import static org.smoothbuild.compile.lang.type.AnnotationNames.NATIVE_IMPURE;
import static org.smoothbuild.compile.lang.type.AnnotationNames.NATIVE_PURE;
import static org.smoothbuild.fs.base.PathS.path;
import static org.smoothbuild.fs.space.Space.PRJ;
import static org.smoothbuild.install.ProjectPaths.PRJ_MOD_FILE_NAME;
import static org.smoothbuild.out.log.Level.INFO;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nlist;
import static org.smoothbuild.util.io.Okios.intToByteString;
import static org.smoothbuild.util.reflect.Classes.saveBytecodeInJar;
import static org.smoothbuild.vm.report.TaskMatchers.ALL;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.inst.ArrayB;
import org.smoothbuild.bytecode.expr.inst.BlobB;
import org.smoothbuild.bytecode.expr.inst.BlobBBuilder;
import org.smoothbuild.bytecode.expr.inst.BoolB;
import org.smoothbuild.bytecode.expr.inst.DefFuncB;
import org.smoothbuild.bytecode.expr.inst.IfFuncB;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.IntB;
import org.smoothbuild.bytecode.expr.inst.MapFuncB;
import org.smoothbuild.bytecode.expr.inst.NatFuncB;
import org.smoothbuild.bytecode.expr.inst.StringB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.bytecode.expr.oper.PickB;
import org.smoothbuild.bytecode.expr.oper.RefB;
import org.smoothbuild.bytecode.expr.oper.SelectB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.hashed.HashedDb;
import org.smoothbuild.bytecode.type.CategoryDb;
import org.smoothbuild.bytecode.type.inst.ArrayTB;
import org.smoothbuild.bytecode.type.inst.BlobTB;
import org.smoothbuild.bytecode.type.inst.BoolTB;
import org.smoothbuild.bytecode.type.inst.DefFuncCB;
import org.smoothbuild.bytecode.type.inst.FuncTB;
import org.smoothbuild.bytecode.type.inst.IfFuncCB;
import org.smoothbuild.bytecode.type.inst.IntTB;
import org.smoothbuild.bytecode.type.inst.MapFuncCB;
import org.smoothbuild.bytecode.type.inst.NatFuncCB;
import org.smoothbuild.bytecode.type.inst.StringTB;
import org.smoothbuild.bytecode.type.inst.TupleTB;
import org.smoothbuild.bytecode.type.inst.TypeB;
import org.smoothbuild.bytecode.type.oper.CallCB;
import org.smoothbuild.bytecode.type.oper.CombineCB;
import org.smoothbuild.bytecode.type.oper.OrderCB;
import org.smoothbuild.bytecode.type.oper.PickCB;
import org.smoothbuild.bytecode.type.oper.RefCB;
import org.smoothbuild.bytecode.type.oper.SelectCB;
import org.smoothbuild.compile.lang.base.LabeledLoc;
import org.smoothbuild.compile.lang.base.LabeledLocImpl;
import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.Trace;
import org.smoothbuild.compile.lang.define.AnnFuncS;
import org.smoothbuild.compile.lang.define.AnnS;
import org.smoothbuild.compile.lang.define.AnnValS;
import org.smoothbuild.compile.lang.define.BlobS;
import org.smoothbuild.compile.lang.define.CallS;
import org.smoothbuild.compile.lang.define.DefFuncS;
import org.smoothbuild.compile.lang.define.DefValS;
import org.smoothbuild.compile.lang.define.ExprS;
import org.smoothbuild.compile.lang.define.FuncS;
import org.smoothbuild.compile.lang.define.IntS;
import org.smoothbuild.compile.lang.define.ItemS;
import org.smoothbuild.compile.lang.define.ItemSigS;
import org.smoothbuild.compile.lang.define.ModFiles;
import org.smoothbuild.compile.lang.define.ModPath;
import org.smoothbuild.compile.lang.define.MonoizeS;
import org.smoothbuild.compile.lang.define.OrderS;
import org.smoothbuild.compile.lang.define.PolyEvaluableS;
import org.smoothbuild.compile.lang.define.PolyFuncS;
import org.smoothbuild.compile.lang.define.PolyValS;
import org.smoothbuild.compile.lang.define.RefS;
import org.smoothbuild.compile.lang.define.SelectS;
import org.smoothbuild.compile.lang.define.StringS;
import org.smoothbuild.compile.lang.define.SyntCtorS;
import org.smoothbuild.compile.lang.define.UnnamedPolyValS;
import org.smoothbuild.compile.lang.define.ValS;
import org.smoothbuild.compile.lang.type.ArrayTS;
import org.smoothbuild.compile.lang.type.BlobTS;
import org.smoothbuild.compile.lang.type.BoolTS;
import org.smoothbuild.compile.lang.type.FuncSchemaS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.IntTS;
import org.smoothbuild.compile.lang.type.SchemaS;
import org.smoothbuild.compile.lang.type.StringTS;
import org.smoothbuild.compile.lang.type.StructTS;
import org.smoothbuild.compile.lang.type.TempVarS;
import org.smoothbuild.compile.lang.type.TupleTS;
import org.smoothbuild.compile.lang.type.TypeFS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;
import org.smoothbuild.compile.sb.BytecodeLoader;
import org.smoothbuild.compile.sb.BytecodeMethodLoader;
import org.smoothbuild.compile.sb.SbTranslatorProv;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.PathS;
import org.smoothbuild.fs.base.SynchronizedFileSystem;
import org.smoothbuild.fs.mem.MemoryFileSystem;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.fs.space.Space;
import org.smoothbuild.install.TempManager;
import org.smoothbuild.load.FileLoader;
import org.smoothbuild.load.JarClassLoaderProv;
import org.smoothbuild.load.MethodLoader;
import org.smoothbuild.out.console.Console;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.report.ConsoleReporter;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.vm.Vm;
import org.smoothbuild.vm.compute.ComputationCache;
import org.smoothbuild.vm.compute.Computer;
import org.smoothbuild.vm.compute.Container;
import org.smoothbuild.vm.execute.ExecutionReporter;
import org.smoothbuild.vm.execute.TaskExecutor;
import org.smoothbuild.vm.execute.TaskReporter;
import org.smoothbuild.vm.job.ExecutionContext;
import org.smoothbuild.vm.job.JobCreator;
import org.smoothbuild.vm.task.NativeMethodLoader;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;

import okio.ByteString;

public class TestContext {
  public static final String BUILD_FILE_PATH = "myBuild.smooth";
  private static final String IMPORTED_FILE_PATH = "imported.smooth";

  private Computer computer;
  private Container container;
  private BytecodeF bytecodeF;
  private ComputationCache computationCache;
  private FileSystem computationCacheFileSystem;
  private BytecodeDb bytecodeDb;
  private CategoryDb categoryDb;
  private HashedDb hashedDb;
  private FileSystem hashedDbFileSystem;
  private FileSystem fullFileSystem;
  private TempManager tempManager;
  private ConsoleReporter consoleReporter;
  private BytecodeLoader bytecodeLoader;
  private JarClassLoaderProv jarClassLoaderProv;
  private MethodLoader methodLoader;
  private BytecodeMethodLoader bytecodeMethodLoader;

  public Vm vm() {
    return new Vm(this::executionContext);
  }

  public Vm vm(NativeMethodLoader nativeMethodLoader) {
    return new Vm(() -> executionContext(nativeMethodLoader));
  }

  public Vm vm(JobCreator jobCreator) {
    return new Vm(() -> executionContext(jobCreator));
  }

  public Vm vm(Reporter reporter) {
    return vm(taskExecutor(executionReporter(new TaskReporter(ALL, reporter))));
  }

  public Vm vm(TaskReporter taskReporter) {
    return vm(taskExecutor(executionReporter(taskReporter)));
  }

  public Vm vm(TaskExecutor taskExecutor) {
    return new Vm(() -> executionContext(taskExecutor));
  }

  public ExecutionContext executionContext() {
    return executionContext(taskExecutor());
  }

  public ExecutionContext executionContext(NativeMethodLoader nativeMethodLoader) {
    return executionContext(taskExecutor(), nativeMethodLoader);
  }

  public ExecutionContext executionContext(TaskExecutor taskExecutor) {
    return executionContext(taskExecutor, null);
  }

  public ExecutionContext executionContext(TaskExecutor taskExecutor,
      NativeMethodLoader nativeMethodLoader) {
    return new ExecutionContext(
        taskExecutor, executionReporter(), bytecodeF(), nativeMethodLoader, new JobCreator());
  }

  public ExecutionContext executionContext(ExecutionReporter reporter, int threadCount) {
    return executionContext(computer(), reporter, threadCount);
  }

  public ExecutionContext executionContext(
      Computer computer, ExecutionReporter reporter, int threadCount) {
    return executionContext(reporter, taskExecutor(computer, reporter, threadCount));
  }

  public ExecutionContext executionContext(ExecutionReporter reporter, TaskExecutor taskExecutor) {
    NativeMethodLoader nativeMethodLoader = null;
    return new ExecutionContext(
        taskExecutor, reporter, bytecodeF(), nativeMethodLoader, new JobCreator());
  }

  public ExecutionContext executionContext(JobCreator jobCreator) {
    NativeMethodLoader nativeMethodLoader = null;
    return new ExecutionContext(
        taskExecutor(), executionReporter(), bytecodeF(), nativeMethodLoader, jobCreator);
  }

  public TaskExecutor taskExecutor() {
    return taskExecutor(executionReporter());
  }

  public TaskExecutor taskExecutor(ExecutionReporter reporter) {
    return new TaskExecutor(computer(), reporter);
  }

  private TaskExecutor taskExecutor(
      Computer computer, ExecutionReporter reporter, int threadCount) {
    return new TaskExecutor(computer, reporter, threadCount);
  }

  public SbTranslatorProv sbTranslatorProv(FileLoader fileLoader) {
    return sbTranslatorProv(fileLoader, bytecodeLoader());
  }

  public SbTranslatorProv sbTranslatorProv(FileLoader fileLoader, BytecodeLoader bytecodeLoader) {
    return new SbTranslatorProv(bytecodeF(), fileLoader, bytecodeLoader);
  }

  private BytecodeLoader bytecodeLoader() {
    if (bytecodeLoader == null) {
      bytecodeLoader = new BytecodeLoader(bytecodeMethodLoader(), bytecodeF());
    }
    return bytecodeLoader;
  }

  private BytecodeMethodLoader bytecodeMethodLoader() {
    if (bytecodeMethodLoader == null) {
      bytecodeMethodLoader = new BytecodeMethodLoader(methodLoader());
    }
    return bytecodeMethodLoader;
  }

  private MethodLoader methodLoader() {
    if (methodLoader == null) {
      methodLoader = new MethodLoader(jarClassLoaderProv());
    }
    return methodLoader;
  }

  private JarClassLoaderProv jarClassLoaderProv() {
    if (jarClassLoaderProv == null) {
      jarClassLoaderProv = new JarClassLoaderProv(bytecodeF(), getSystemClassLoader());
    }
    return jarClassLoaderProv;
  }

  public TestingModLoader module(String code) {
    return new TestingModLoader(code);
  }

  public ExecutionReporter executionReporter() {
    return executionReporter(taskReporter());
  }

  public ExecutionReporter executionReporter(TaskReporter reporter) {
    return new ExecutionReporter(reporter);
  }

  public TaskReporter taskReporter() {
    return new TaskReporter(ALL, reporter());
  }

  public ConsoleReporter reporter() {
    if (consoleReporter == null) {
      consoleReporter = new ConsoleReporter(console(), INFO);
    }
    return consoleReporter;
  }

  private Console console() {
    // Use System.out if you want to see smooth logs in junit output
    // var outputStream = System.out;
    var outputStream = nullOutputStream();
    return new Console(new PrintWriter(outputStream, true));
  }

  public Computer computer() {
    if (computer == null) {
      computer = new Computer(computationCache(), Hash.of(123), this::newContainer);
    }
    return computer;
  }

  public NativeApi nativeApi() {
    return container();
  }

  public NativeApi newNativeApi() {
    return newContainer();
  }

  public Container container() {
    if (container == null) {
      container = newContainer();
    }
    return container;
  }

  private Container newContainer() {
    return new Container(fullFileSystem(), bytecodeF());
  }

  public BytecodeF bytecodeF() {
    if (bytecodeF == null) {
      bytecodeF = new BytecodeF(bytecodeDb(), categoryDb());
    }
    return bytecodeF;
  }

  public CategoryDb categoryDb() {
    if (categoryDb == null) {
      categoryDb = new CategoryDb(hashedDb());
    }
    return categoryDb;
  }

  public BytecodeDb bytecodeDb() {
    if (bytecodeDb == null) {
      bytecodeDb = new BytecodeDb(hashedDb(), categoryDb());
    }
    return bytecodeDb;
  }

  public ComputationCache computationCache() {
    if (computationCache == null) {
      computationCache = new ComputationCache(
          computationCacheFileSystem(), bytecodeDb(), bytecodeF());
    }
    return computationCache;
  }

  public FileSystem computationCacheFileSystem() {
    if (computationCacheFileSystem == null) {
      computationCacheFileSystem = synchronizedMemoryFileSystem();
    }
    return computationCacheFileSystem;
  }

  public BytecodeDb bytecodeDbOther() {
    return new BytecodeDb(hashedDb(), categoryDbOther());
  }

  public CategoryDb categoryDbOther() {
    return new CategoryDb(hashedDb());
  }

  public HashedDb hashedDb() {
    if (hashedDb == null) {
      hashedDb = new HashedDb(
          hashedDbFileSystem(), PathS.root(), tempManager());
    }
    return hashedDb;
  }

  public FileSystem hashedDbFileSystem() {
    if (hashedDbFileSystem == null) {
      hashedDbFileSystem = synchronizedMemoryFileSystem();
    }
    return hashedDbFileSystem;
  }

  public TempManager tempManager() {
    if (tempManager == null) {
      tempManager = new TempManager();
    }
    return tempManager;
  }

  public FileSystem fullFileSystem() {
    if (fullFileSystem == null) {
      fullFileSystem = synchronizedMemoryFileSystem();
    }
    return fullFileSystem;
  }

  // InstB types

  public TupleTB animalTB() {
    return tupleTB(stringTB(), intTB());
  }

  public ArrayTB arrayTB() {
    return arrayTB(stringTB());
  }

  public ArrayTB arrayTB(TypeB elemT) {
    return categoryDb().array(elemT);
  }

  public BlobTB blobTB() {
    return categoryDb().blob();
  }

  public BoolTB boolTB() {
    return categoryDb().bool();
  }

  public TupleTB fileTB() {
    return tupleTB(stringTB(), blobTB());
  }

  public DefFuncCB defFuncCB() {
    return defFuncCB(intTB(), blobTB(), stringTB());
  }

  public DefFuncCB defFuncCB(TypeB resT, TypeB... paramTs) {
    return categoryDb().defFunc(funcTB(resT, paramTs));
  }

  public FuncTB funcTB() {
    return funcTB(intTB(), blobTB(), stringTB());
  }

  public FuncTB funcTB(TypeB resT, TypeB... paramTs) {
    return funcTB(resT, list(paramTs));
  }

  private FuncTB funcTB(TypeB resT, ImmutableList<TypeB> paramTs) {
    return categoryDb().funcT(resT, paramTs);
  }

  public IntTB intTB() {
    return categoryDb().int_();
  }

  public NatFuncCB natFuncCB() {
    return natFuncCB(blobTB(), boolTB());
  }

  public NatFuncCB natFuncCB(TypeB resT, TypeB... paramTs) {
    return categoryDb().natFunc(funcTB(resT, paramTs));
  }

  public TupleTB personTB() {
    return tupleTB(stringTB(), stringTB());
  }

  public StringTB stringTB() {
    return categoryDb().string();
  }

  public TupleTB tupleTB(TypeB... itemTs) {
    return categoryDb().tuple(itemTs);
  }

  // OperB categories

  public CallCB callCB() {
    return callCB(intTB());
  }

  public CallCB callCB(TypeB evalT) {
    return categoryDb().call(evalT);
  }

  public CombineCB combineCB(TypeB... itemTs) {
    return categoryDb().combine(tupleTB(itemTs));
  }

  public IfFuncCB ifFuncCB() {
    return ifFuncCB(intTB());
  }

  public IfFuncCB ifFuncCB(TypeB t) {
    return categoryDb().ifFunc(t);
  }

  public MapFuncCB mapFuncCB() {
    return mapFuncCB(intTB(), boolTB());
  }

  public MapFuncCB mapFuncCB(TypeB r, TypeB s) {
    return categoryDb().mapFunc(r, s);
  }

  public OrderCB orderCB() {
    return orderCB(intTB());
  }

  public OrderCB orderCB(TypeB elemT) {
    return categoryDb().order(arrayTB(elemT));
  }

  public PickCB pickCB() {
    return pickCB(intTB());
  }

  public PickCB pickCB(TypeB evalT) {
    return categoryDb().pick(evalT);
  }

  public RefCB refCB() {
    return refCB(intTB());
  }

  public RefCB refCB(TypeB evalT) {
    return categoryDb().ref(evalT);
  }

  public SelectCB selectCB() {
    return selectCB(intTB());
  }

  public SelectCB selectCB(TypeB evalT) {
    return categoryDb().select(evalT);
  }

  // InstB-s

  public TupleB animalB() {
    return animalB("rabbit", 7);
  }

  public TupleB animalB(String species, int speed) {
    return animalB(stringB(species), intB(speed));
  }

  public TupleB animalB(StringB species, IntB speed) {
    return tupleB(species, speed);
  }

  public ArrayB arrayB(InstB... elems) {
    return arrayB(elems[0].evalT(), elems);
  }

  public ArrayB arrayB(TypeB elemT, InstB... elems) {
    return bytecodeDb()
        .arrayBuilder(arrayTB(elemT))
        .addAll(list(elems))
        .build();
  }

  public BlobB blobBJarWithPluginApi(Class<?>... classes) throws IOException {
    return blobBWith(
        ImmutableList.<Class<?>>builder()
            .addAll(list(classes))
            .add(BlobB.class)
            .add(NativeApi.class)
            .add(ExprB.class)
            .add(StringB.class)
            .add(TupleB.class)
            .add(InstB.class)
            .build());
  }

  public BlobB blobBJarWithJavaByteCode(Class<?>... classes) throws IOException {
    return blobBWith(list(classes));
  }

  private BlobB blobBWith(List<Class<?>> list) throws IOException {
    var blobBBuilder = bytecodeDb().blobBuilder();
    try (var outputStream = blobBBuilder.sink()) {
      saveBytecodeInJar(outputStream, list);
    }
    return blobBBuilder.build();
  }

  public BlobB blobB() {
    return bytecodeF().blob(sink -> sink.writeUtf8("blob data"));
  }

  public BlobB blobB(int data) {
    return blobB(intToByteString(data));
  }

  public BlobB blobB(ByteString bytes) {
    return bytecodeF().blob(sink -> sink.write(bytes));
  }

  public BlobBBuilder blobBBuilder() {
    return bytecodeDb().blobBuilder();
  }

  public BoolB boolB() {
    return boolB(true);
  }

  public BoolB boolB(boolean value) {
    return bytecodeDb().bool(value);
  }

  public TupleB fileB(PathS path) {
    return fileB(path, path.toString());
  }

  public TupleB fileB(PathS path, String content) {
    return fileB(path.toString(), content);
  }

  public TupleB fileB(String path, String content) {
    return fileB(path, ByteString.encodeString(content, CHARSET));
  }

  public TupleB fileB(PathS path, ByteString content) {
    return fileB(path.toString(), content);
  }

  public TupleB fileB(String path, ByteString content) {
    return fileB(path, blobB(content));
  }

  public TupleB fileB(String path, BlobB blob) {
    StringB string = bytecodeF().string(path);
    return bytecodeF().file(blob, string);
  }

  public DefFuncB defFuncB() {
    return defFuncB(intB());
  }

  public DefFuncB defFuncB(ExprB body) {
    return defFuncB(list(), body);
  }

  public DefFuncB defFuncB(ImmutableList<TypeB> paramTs, ExprB body) {
    var funcTB = funcTB(body.evalT(), paramTs);
    return defFuncB(funcTB, body);
  }

  public DefFuncB defFuncB(FuncTB type, ExprB body) {
    return bytecodeDb().defFunc(type, body);
  }

  public DefFuncB idFuncB() {
    return defFuncB(list(intTB()), refB(intTB(), 0));
  }

  public DefFuncB returnAbcFuncB() {
    return defFuncB(stringB("abc"));
  }

  public IntB intB() {
    return intB(17);
  }

  public IntB intB(int value) {
    return intB(BigInteger.valueOf(value));
  }

  public IntB intB(BigInteger value) {
    return bytecodeDb().int_(value);
  }

  public NatFuncB natFuncB(Class<?> clazz) throws IOException {
    return natFuncB(blobBJarWithPluginApi(clazz), stringB(clazz.getCanonicalName()));
  }

  public NatFuncB natFuncB() {
    return natFuncB(funcTB());
  }

  public NatFuncB natFuncB(FuncTB funcTB) {
    return natFuncB(funcTB, blobB(7), stringB("class binary name"), boolB(true));
  }

  public NatFuncB natFuncB(BlobB jar, StringB classBinaryName) {
    return natFuncB(funcTB(), jar, classBinaryName);
  }

  public NatFuncB natFuncB(FuncTB type, BlobB jar, StringB classBinaryName) {
    return natFuncB(type, jar, classBinaryName, boolB(true));
  }

  public NatFuncB natFuncB(FuncTB type, BlobB jar, StringB classBinaryName, BoolB isPure) {
    return bytecodeDb().natFunc(type, jar, classBinaryName, isPure);
  }

  public TupleB personB(String firstName, String lastName) {
    return tupleB(stringB(firstName), stringB(lastName));
  }

  public StringB stringB() {
    return bytecodeDb().string("abc");
  }

  public StringB stringB(String string) {
    return bytecodeDb().string(string);
  }

  public TupleB tupleB(InstB... items) {
    return bytecodeDb().tuple(list(items));
  }

  public ArrayB messageArrayWithOneError() {
    return arrayB(bytecodeF().errorMessage("error message"));
  }

  public ArrayB messageArrayEmtpy() {
    return arrayB(bytecodeF().messageT());
  }

  public TupleB errorMessage(String text) {
    return bytecodeF().errorMessage(text);
  }

  public TupleB warningMessage(String text) {
    return bytecodeF().warningMessage(text);
  }

  public TupleB infoMessage(String text) {
    return bytecodeF().infoMessage(text);
  }

  // OperB-s

  public CallB callB(ExprB func, ExprB... args) {
    return callBImpl(func, combineB(args));
  }

  public CallB callBImpl(ExprB func, CombineB args) {
    return bytecodeDb().call(func, args);
  }

  public CombineB combineB(ExprB... items) {
    return bytecodeDb().combine(list(items));
  }

  public IfFuncB ifFuncB(TypeB t) {
    return bytecodeDb().ifFunc(t);
  }

  public MapFuncB mapFuncB(TypeB r, TypeB s) {
    return bytecodeDb().mapFunc(r, s);
  }

  public OrderB orderB(ExprB... elems) {
    return orderB(elems[0].evalT(), elems);
  }

  public OrderB orderB(TypeB elemT, ExprB... elems) {
    var elemList = list(elems);
    return bytecodeDb().order(arrayTB(elemT), elemList);
  }

  public PickB pickB(ExprB array, ExprB index) {
    return bytecodeDb().pick(array, index);
  }

  public RefB refB(int index) {
    return refB(intTB(), index);
  }

  public RefB refB(TypeB evalT, int index) {
    return bytecodeDb().ref(evalT, BigInteger.valueOf(index));
  }

  public SelectB selectB(ExprB tuple, IntB index) {
    return bytecodeDb().select(tuple, index);
  }

  // ValS types

  public static ArrayTS arrayTS(TypeS elemT) {
    return new ArrayTS(elemT);
  }

  public static BlobTS blobTS() {
    return TypeFS.BLOB;
  }

  public static BoolTS boolTS() {
    return TypeFS.BOOL;
  }

  public static FuncTS funcTS(TypeS resT, TypeS... paramTs) {
    return funcTS(resT, list(paramTs));
  }

  public static FuncTS funcTS(TypeS resT, ImmutableList<TypeS> paramTs) {
    return new FuncTS(resT, tupleTS(paramTs));
  }

  public static TupleTS tupleTS(TypeS... itemTs) {
    return tupleTS(list(itemTs));
  }

  public static TupleTS tupleTS(ImmutableList<TypeS> paramTs) {
    return new TupleTS(paramTs);
  }

  public static IntTS intTS() {
    return TypeFS.INT;
  }

  public FuncSchemaS funcSchemaS(TypeS resT, TypeS... paramTs) {
    return newFuncSchema(funcTS(resT, paramTs));
  }

  private static FuncSchemaS newFuncSchema(FuncTS funcTS) {
    return new FuncSchemaS(funcTS);
  }

  public static StructTS personTS() {
    return structTS("Person",
        nlist(sigS(stringTS(), "firstName"), sigS(stringTS(), "lastName")));
  }

  public static StructTS animalTS() {
    return structTS("Animal",
        nlist(sigS(stringTS(), "name"), sigS(intTS(), "size")));
  }

  public static StringTS stringTS() {
    return TypeFS.STRING;
  }

  public static StructTS structTS(TypeS... fieldTs) {
    Builder<ItemSigS> builder = ImmutableList.builder();
    for (int i = 0; i < fieldTs.length; i++) {
      builder.add(sigS(fieldTs[i], "param" + i));
    }
    return structTS("MyStruct", nlist(builder.build()));
  }

  public static StructTS structTS(String name, NList<ItemSigS> fields) {
    return new StructTS(name, fields);
  }

  public ImmutableMap<VarS, TypeS> aToIntVarMapS() {
    return ImmutableMap.of(varA(), intTS());
  }

  public static VarS tempVarA() {
    return tempVar("A");
  }

  private static TempVarS tempVar(String name) {
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

  public BlobS blobS(int data) {
    return blobS(1, data);
  }

  public BlobS blobS(int line, int data) {
    return new BlobS(blobTS(), intToByteString(data), loc(line));
  }

  public CallS callS(ExprS callable, ExprS... args) {
    return callS(1, callable, args);
  }

  public CallS callS(int line, ExprS callable, ExprS... args) {
    return new CallS(callable, list(args), loc(line));
  }

  public IntS intS(int value) {
    return intS(1, value);
  }

  public IntS intS(int line, int value) {
    return new IntS(intTS(), BigInteger.valueOf(value), loc(line));
  }

  public ImmutableMap<VarS, TypeS> varMap() {
    return ImmutableMap.of();
  }

  public ImmutableMap<VarS, TypeS> varMap(VarS var, TypeS type) {
    return ImmutableMap.of(var, type);
  }

  public MonoizeS monoizeS(ImmutableMap<VarS, TypeS> varMap, PolyEvaluableS evaluable) {
    return monoizeS(1, varMap, evaluable);
  }

  public static MonoizeS monoizeS(int loc, ImmutableMap<VarS, TypeS> varMap,
      PolyEvaluableS evaluable) {
    return new MonoizeS(varMap, evaluable, loc(loc));
  }

  public OrderS orderS(int line, ExprS firstElem, ExprS... restElems) {
    return new OrderS(arrayTS(firstElem.evalT()), concat(firstElem, list(restElems)), loc(line));
  }

  public OrderS orderS(TypeS elemT, ExprS... exprs) {
    return orderS(1, elemT, exprs);
  }

  public OrderS orderS(int line, TypeS elemT, ExprS... exprs) {
    return new OrderS(arrayTS(elemT), list(exprs), loc(line));
  }

  public RefS refS(TypeS type) {
    return refS(type, "refName");
  }

  public RefS refS(TypeS type, String name) {
    return refS(1, type, name);
  }

  public RefS refS(int line, TypeS type, String name) {
    return new RefS(type, name, loc(line));
  }

  public SelectS selectS(ExprS selectable, String field) {
    return selectS(1, selectable, field);
  }

  public SelectS selectS(int line, ExprS selectable, String field) {
    return new SelectS(selectable, field, loc(line));
  }

  public StringS stringS() {
    return stringS("abc");
  }

  public StringS stringS(String string) {
    return stringS(1, string);
  }

  public StringS stringS(int line, String data) {
    return new StringS(stringTS(), data, loc(line));
  }

  // other smooth language thingies

  private AnnS bytecodeS(String path) {
    return bytecodeS(1, path);
  }

  public AnnS bytecodeS(int line, String path) {
    return bytecodeS(line, stringS(line, path));
  }

  public AnnS bytecodeS(int line, StringS path) {
    return bytecodeS(path, loc(line));
  }

  public AnnS bytecodeS(String path, Loc loc) {
    return bytecodeS(stringS(path), loc);
  }

  public AnnS bytecodeS(StringS path, Loc loc) {
    return new AnnS(BYTECODE, path, loc);
  }

  public AnnS natAnnS() {
    return natAnnS(1, stringS("impl"));
  }

  public AnnS natAnnS(int line, StringS classBinaryName) {
    return natAnnS(line, classBinaryName, true);
  }

  public AnnS natAnnS(int line, StringS classBinaryName, boolean pure) {
    return natAnnS(loc(line), classBinaryName, pure);
  }

  public AnnS natAnnS(Loc loc, StringS classBinaryName) {
    return natAnnS(loc, classBinaryName, true);
  }

  public AnnS natAnnS(Loc loc, StringS classBinaryName, boolean pure) {
    var name = pure ? NATIVE_PURE : NATIVE_IMPURE;
    return new AnnS(name, classBinaryName, loc);
  }

  public ItemS itemS(TypeS type, String name) {
    return itemS(1, type, name);
  }

  public ItemS itemS(int line, TypeS type, String name) {
    return itemS(line, type, name, empty());
  }

  public ItemS itemS(int line, TypeS type, String name, ExprS body) {
    return itemS(line, type, name, Optional.of(body));
  }

  public ItemS itemS(TypeS type, String name, Optional<ExprS> body) {
    return itemS(1, type, name, body);
  }

  public ItemS itemS(int line, TypeS type, String name, Optional<ExprS> body) {
    return itemSPoly(line, type, name, body.map(UnnamedPolyValS::new));
  }

  public static ItemS itemSPoly(int line, TypeS type, String name, Optional<PolyEvaluableS> body) {
    return new ItemS(type, name, body, loc(line));
  }

  public PolyFuncS polyByteFuncS(AnnS ann, int line, TypeS resT, String name, NList<ItemS> params) {
    return polyS(annFuncS(line, ann, resT, name, params));
  }

  public PolyFuncS polyByteFuncS(int line, TypeS resT, String name, NList<ItemS> params) {
    return polyS(byteFuncS(line, resT, name, params));
  }

  public PolyFuncS polyByteFuncS(String path, TypeS resT, String name, NList<ItemS> params) {
    return polyS(byteFuncS(path, resT, name, params));
  }

  public PolyFuncS polyByteFuncS(int line, String path, TypeS resT, String name,
      NList<ItemS> params) {
    return polyS(byteFuncS(line, path, resT, name, params));
  }

  public AnnFuncS byteFuncS(String path, TypeS resT, String name, NList<ItemS> params) {
    return byteFuncS(1, path, resT, name, params);
  }

  public AnnFuncS byteFuncS(int line, TypeS resT, String name, NList<ItemS> params) {
    return annFuncS(line, bytecodeS(line - 1, "impl"), resT, name, params);
  }

  public AnnFuncS byteFuncS(int line, String path, TypeS resT, String name, NList<ItemS> params) {
    return annFuncS(line, bytecodeS(path), resT, name, params);
  }

  public AnnFuncS annFuncS(int line, AnnS ann, TypeS resT, String name, NList<ItemS> params) {
    return annFuncS(ann, funcTS(resT, toTypes(params.list())), modPath(), name, params, loc(line));
  }

  public AnnFuncS annFuncS(AnnS ann, FuncTS type, ModPath modPath, String name,
      NList<ItemS> params, Loc loc) {
    return new AnnFuncS(ann, type, modPath, name, params, loc);
  }

  public PolyValS polyByteValS(int line, TypeS type, String name) {
    return polyS(byteValS(line, type, name));
  }

  public PolyValS polyByteValS(int line, AnnS ann, TypeS type, String name) {
    return polyS(annValS(line, ann, type, name));
  }

  public AnnValS byteValS(int line, TypeS type, String name) {
    return annValS(line, bytecodeS(line - 1, "impl"), type, name);
  }

  public AnnValS annValS(int line, AnnS ann, TypeS type, String name) {
    return new AnnValS(ann, type, modPath(), name, loc(line));
  }

  public AnnValS annValS(AnnS ann, TypeS type, ModPath modPath, String name, Loc loc) {
    return new AnnValS(ann, type, modPath, name, loc);
  }

  public PolyValS polyDefValS(String name, ExprS body) {
    return polyS(defValS(name, body));
  }

  public PolyValS polyDefValS(int line, String name, ExprS body) {
    return polyS(defValS(line, name, body));
  }

  public PolyValS polyDefValS(int line, TypeS type, String name, ExprS body) {
    return polyS(defValS(line, type, name, body));
  }

  public DefValS defValS(String name, ExprS body) {
    return defValS(1, name, body);
  }

  public DefValS defValS(int loc, String name, ExprS body) {
    return defValS(loc, body.evalT(), name, body);
  }

  public DefValS defValS(int line, TypeS type, String name, ExprS body) {
    return new DefValS(type, modPath(), name, body, loc(line));
  }

  public PolyValS emptyArrayValS() {
    return polyS(defValS("emptyArray", orderS(varA())));
  }

  private PolyValS polyS(ValS valS) {
    return polyValS(schemaS(valS.type()), valS);
  }

  public SchemaS schemaS(TypeS typeS) {
    return new SchemaS(typeS);
  }

  public PolyFuncS polySyntCtorS(StructTS structT) {
    return polyS(syntCtorS(structT));
  }

  public PolyFuncS polySyntCtorS(int line, StructTS structT) {
    return polyS(syntCtorS(line, structT));
  }

  public PolyFuncS polySyntCtorS(int line, StructTS structT, String name) {
    return polyS(syntCtorS(line, structT, name));
  }

  public PolyFuncS polySyntCtorS(int line, FuncTS type, ModPath modPath, String name,
      NList<ItemS> params) {
    return polyS(syntCtorS(line, type, modPath, name, params));
  }

  public SyntCtorS syntCtorS(StructTS structT) {
    return syntCtorS(1, structT, UPPER_CAMEL.to(LOWER_CAMEL, structT.name()));
  }

  public SyntCtorS syntCtorS(int line, StructTS structT) {
    return syntCtorS(line, structT, structNameToCtorName(structT.name()));
  }

  public SyntCtorS syntCtorS(int line, StructTS structT, String name) {
    var fields = structT.fields();
    var params = fields.map(f -> new ItemS(f.type(), f.nameSane(), empty(), loc(2)));
    return syntCtorS(line, funcTS(structT, toTypes(params.list())), modPath(), name, params);
  }

  public SyntCtorS syntCtorS(int line, FuncTS type, ModPath modPath, String name,
      NList<ItemS> params) {
    return new SyntCtorS(type, modPath, name, params, loc(line));
  }

  public PolyFuncS polyNatFuncS(TypeS resT, String name, NList<ItemS> params) {
    return polyS(natFuncS(resT, name, params));
  }

  public PolyFuncS polyNatFuncS(TypeS resT, String name, NList<ItemS> params, AnnS ann) {
    return polyS(natFuncS(resT, name, params, ann));
  }

  public PolyFuncS polyNatFuncS(int line, TypeS resT, String name, NList<ItemS> params, AnnS ann) {
    return polyS(natFuncS(line, resT, name, params, ann));
  }

  public PolyFuncS polyNatFuncS(FuncTS type, String name, NList<ItemS> params) {
    return polyS(natFuncS(type, name, params));
  }

  public PolyFuncS polyNatFuncS(FuncTS type, String name, NList<ItemS> params, AnnS ann) {
    return polyS(natFuncS(type, name, params, ann));
  }

  public PolyFuncS polyNatFuncS(int line, FuncTS type, String name, NList<ItemS> params, AnnS ann) {
    return polyS(natFuncS(line, type, name, params, ann));
  }

  public AnnFuncS natFuncS(TypeS resT, String name, NList<ItemS> params) {
    return natFuncS(resT, name, params, natAnnS());
  }

  public AnnFuncS natFuncS(TypeS resT, String name, NList<ItemS> params, AnnS ann) {
    return natFuncS(1, resT, name, params, ann);
  }

  public AnnFuncS natFuncS(int line, TypeS resT, String name, NList<ItemS> params, AnnS ann) {
    return natFuncS(line, funcTS(resT, toTypes(params.list())), modPath(), name, params, ann);
  }

  public AnnFuncS natFuncS(FuncTS type, String name, NList<ItemS> params) {
    return natFuncS(type, name, params, natAnnS());
  }

  public AnnFuncS natFuncS(FuncTS type, String name, NList<ItemS> params, AnnS ann) {
    return natFuncS(1, type, name, params, ann);
  }

  public AnnFuncS natFuncS(int line, FuncTS type, String name, NList<ItemS> params, AnnS ann) {
    return natFuncS(line, type, modPath(), name, params, ann);
  }

  public AnnFuncS natFuncS(int line, FuncTS type, ModPath modPath, String name,
      NList<ItemS> params, AnnS ann) {
    return new AnnFuncS(ann, type, modPath, name, params, loc(line));
  }

  public PolyFuncS polyDefFuncS(int line, String name, NList<ItemS> params, ExprS body) {
    return polyS(defFuncS(line, name, params, body));
  }

  public PolyFuncS polyDefFuncS(String name, NList<ItemS> params, ExprS body) {
    return polyS(defFuncS(name, params, body));
  }

  public PolyFuncS polyDefFuncS(TypeS resT, String name, NList<ItemS> params, ExprS body) {
    return polyS(defFuncS(resT, name, params, body));
  }

  public PolyFuncS polyDefFuncS(int line, TypeS resT, String name, NList<ItemS> params,
      ExprS body) {
    return polyS(defFuncS(line, resT, name, params, body));
  }

  public DefFuncS defFuncS(int line, String name, NList<ItemS> params, ExprS body) {
    return defFuncS(line, body.evalT(), name, params, body);
  }

  public DefFuncS defFuncS(String name, NList<ItemS> params, ExprS body) {
    return defFuncS(body.evalT(), name, params, body);
  }

  public DefFuncS defFuncS(TypeS resT, String name, NList<ItemS> params, ExprS body) {
    return defFuncS(1, resT, name, params, body);
  }

  public DefFuncS defFuncS(int loc, TypeS resT, String name, NList<ItemS> params, ExprS body) {
    return new DefFuncS(funcTS(resT, toTypes(params)), modPath(), name, params, body, loc(loc));
  }

  public PolyFuncS idFuncS() {
    var a = varA();
    return polyS(defFuncS(a, "myId", nlist(itemS(a, "a")), refS(a, "a")));
  }

  public DefFuncS intIdFuncS() {
    return defFuncS(intTS(), "myIntId", nlist(itemS(intTS(), "i")), refS(intTS(), "i"));
  }

  public DefFuncS returnIntFuncS() {
    return defFuncS(intTS(), "myReturnInt", nlist(), intS(1, 3));
  }

  private PolyFuncS polyS(FuncS funcS) {
    return PolyFuncS.polyFuncS(newFuncSchema(funcS.type()), funcS);
  }

  public static ItemSigS sigS(TypeS type, String name) {
    return new ItemSigS(type, name);
  }

  public static ModFiles modFiles() {
    return modFiles(smoothFilePath());
  }

  public static ModFiles importedModFiles() {
    return modFiles(importedFilePath());
  }

  private static ModFiles modFiles(FilePath filePath) {
    return new ModFiles(filePath, empty());
  }

  public static ModPath modPath() {
    return modPath(smoothFilePath());
  }

  public static ModPath importedModPath() {
    return modPath(importedFilePath());
  }

  public static ModPath modPath(FilePath filePath) {
    return ModPath.of(filePath);
  }

  public static Trace trace(String code, int line) {
    return new Trace(code, loc(line), null);
  }

  public static Trace trace(String code2, int line2, String code1, int line1) {
    return new Trace(code2, loc(line2), new Trace(code1, loc(line1)));
  }

  public static Loc loc() {
    return loc(11);
  }

  public static Loc loc(int line) {
    return loc(filePath(), line);
  }

  public static Loc loc(FilePath filePath, int line) {
    return Loc.loc(filePath, line);
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
    return new FilePath(PRJ, path(filePath));
  }

  public static Log userFatal(int line, String message) {
    return fatal(userFileMessage(line, message));
  }

  public static Log userError(int line, String message) {
    return error(userFileMessage(line, message));
  }

  private static String userFileMessage(int line, String message) {
    return PRJ_MOD_FILE_NAME + ":" + line + ": " + message;
  }

  public static SynchronizedFileSystem synchronizedMemoryFileSystem() {
    return new SynchronizedFileSystem(new MemoryFileSystem());
  }

  public static LabeledLoc labeledLoc() {
    return labeledLoc("label");
  }

  public static LabeledLoc labeledLoc(String label) {
    return new LabeledLocImpl(label, Loc.unknown());
  }

  public static LabeledLoc labeledLoc(String label, int line) {
    return new LabeledLocImpl(label, loc(line));
  }
}
