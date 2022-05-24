package org.smoothbuild.testing;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.io.ByteStreams.nullOutputStream;
import static java.lang.ClassLoader.getSystemClassLoader;
import static java.util.Arrays.stream;
import static java.util.Optional.empty;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.bytecode.type.val.VarBoundsB.varBoundsB;
import static org.smoothbuild.fs.base.PathS.path;
import static org.smoothbuild.fs.space.Space.PRJ;
import static org.smoothbuild.install.ProjectPaths.PRJ_MOD_FILE_NAME;
import static org.smoothbuild.lang.define.ItemS.toTypes;
import static org.smoothbuild.lang.type.AnnotationNames.BYTECODE;
import static org.smoothbuild.lang.type.AnnotationNames.NATIVE_IMPURE;
import static org.smoothbuild.lang.type.AnnotationNames.NATIVE_PURE;
import static org.smoothbuild.lang.type.VarBoundsS.varBoundsS;
import static org.smoothbuild.out.log.Level.INFO;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.out.report.TaskMatchers.ALL;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nList;
import static org.smoothbuild.util.io.Okios.intToByteString;
import static org.smoothbuild.util.reflect.Classes.saveBytecodeInJar;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.ObjDb;
import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.expr.CallB;
import org.smoothbuild.bytecode.obj.expr.CombineB;
import org.smoothbuild.bytecode.obj.expr.IfB;
import org.smoothbuild.bytecode.obj.expr.InvokeB;
import org.smoothbuild.bytecode.obj.expr.MapB;
import org.smoothbuild.bytecode.obj.expr.OrderB;
import org.smoothbuild.bytecode.obj.expr.ParamRefB;
import org.smoothbuild.bytecode.obj.expr.SelectB;
import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.obj.val.BlobBBuilder;
import org.smoothbuild.bytecode.obj.val.BoolB;
import org.smoothbuild.bytecode.obj.val.FuncB;
import org.smoothbuild.bytecode.obj.val.IntB;
import org.smoothbuild.bytecode.obj.val.MethodB;
import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.bytecode.type.CatDb;
import org.smoothbuild.bytecode.type.TypeBF;
import org.smoothbuild.bytecode.type.TypingB;
import org.smoothbuild.bytecode.type.expr.CallCB;
import org.smoothbuild.bytecode.type.expr.CombineCB;
import org.smoothbuild.bytecode.type.expr.IfCB;
import org.smoothbuild.bytecode.type.expr.InvokeCB;
import org.smoothbuild.bytecode.type.expr.MapCB;
import org.smoothbuild.bytecode.type.expr.OrderCB;
import org.smoothbuild.bytecode.type.expr.ParamRefCB;
import org.smoothbuild.bytecode.type.expr.SelectCB;
import org.smoothbuild.bytecode.type.val.AnyTB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.BlobTB;
import org.smoothbuild.bytecode.type.val.BoolTB;
import org.smoothbuild.bytecode.type.val.BoundedB;
import org.smoothbuild.bytecode.type.val.CallableTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.IntTB;
import org.smoothbuild.bytecode.type.val.MethodTB;
import org.smoothbuild.bytecode.type.val.NothingTB;
import org.smoothbuild.bytecode.type.val.StringTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.bytecode.type.val.VarB;
import org.smoothbuild.bytecode.type.val.VarBoundsB;
import org.smoothbuild.compile.BytecodeLoader;
import org.smoothbuild.compile.BytecodeMethodLoader;
import org.smoothbuild.compile.CompilerProv;
import org.smoothbuild.compile.TypeSbConv;
import org.smoothbuild.db.Hash;
import org.smoothbuild.db.HashedDb;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.PathS;
import org.smoothbuild.fs.base.SynchronizedFileSystem;
import org.smoothbuild.fs.mem.MemoryFileSystem;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.fs.space.Space;
import org.smoothbuild.install.TempManager;
import org.smoothbuild.lang.define.AnnFuncS;
import org.smoothbuild.lang.define.AnnS;
import org.smoothbuild.lang.define.AnnValS;
import org.smoothbuild.lang.define.DefFuncS;
import org.smoothbuild.lang.define.DefValS;
import org.smoothbuild.lang.define.InternalModLoader;
import org.smoothbuild.lang.define.ItemS;
import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.define.ModFiles;
import org.smoothbuild.lang.define.ModPath;
import org.smoothbuild.lang.define.ModS;
import org.smoothbuild.lang.define.SyntCtorS;
import org.smoothbuild.lang.define.TopEvalS;
import org.smoothbuild.lang.expr.BlobS;
import org.smoothbuild.lang.expr.CallS;
import org.smoothbuild.lang.expr.ExprS;
import org.smoothbuild.lang.expr.IntS;
import org.smoothbuild.lang.expr.OrderS;
import org.smoothbuild.lang.expr.ParamRefS;
import org.smoothbuild.lang.expr.SelectS;
import org.smoothbuild.lang.expr.StringS;
import org.smoothbuild.lang.expr.TopRefS;
import org.smoothbuild.lang.type.AnyTS;
import org.smoothbuild.lang.type.ArrayTS;
import org.smoothbuild.lang.type.BlobTS;
import org.smoothbuild.lang.type.BoolTS;
import org.smoothbuild.lang.type.BoundedS;
import org.smoothbuild.lang.type.FuncTS;
import org.smoothbuild.lang.type.IntTS;
import org.smoothbuild.lang.type.NothingTS;
import org.smoothbuild.lang.type.StringTS;
import org.smoothbuild.lang.type.StructTS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.TypeSF;
import org.smoothbuild.lang.type.TypingS;
import org.smoothbuild.lang.type.VarBoundsS;
import org.smoothbuild.lang.type.VarS;
import org.smoothbuild.load.FileLoader;
import org.smoothbuild.load.JarClassLoaderProv;
import org.smoothbuild.load.MethodLoader;
import org.smoothbuild.out.console.Console;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.report.ConsoleReporter;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.util.type.Bounds;
import org.smoothbuild.util.type.Side;
import org.smoothbuild.vm.Vm;
import org.smoothbuild.vm.VmProv;
import org.smoothbuild.vm.algorithm.NativeMethodLoader;
import org.smoothbuild.vm.compute.ComputationCache;
import org.smoothbuild.vm.compute.Computer;
import org.smoothbuild.vm.compute.Container;
import org.smoothbuild.vm.job.JobCreatorProv;
import org.smoothbuild.vm.parallel.ExecutionReporter;
import org.smoothbuild.vm.parallel.ParallelJobExecutor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.util.Providers;

import okio.ByteString;

public class TestingContext {
  public static final String BUILD_FILE_PATH = "myBuild.smooth";
  private static final String IMPORTED_FILE_PATH = "imported.smooth";

  private Computer computer;
  private Container container;
  private BytecodeF bytecodeF;
  private ComputationCache computationCache;
  private FileSystem computationCacheFileSystem;
  private ObjDb objDb;
  private TypingS typingS;
  private TypingB typingB;
  private CatDb catDb;
  private HashedDb hashedDb;
  private FileSystem hashedDbFileSystem;
  private FileSystem fullFileSystem;
  private TempManager tempManager;
  private ModS internalMod;
  private TypeSF typeSF;
  private ConsoleReporter consoleReporter;
  private BytecodeLoader bytecodeLoader;
  private JarClassLoaderProv jarClassLoaderProv;
  private MethodLoader methodLoader;
  private BytecodeMethodLoader bytecodeMethodLoader;

  public Vm vm() {
    return vmProv().get(ImmutableMap.of());
  }

  public VmProv vmProv() {
    return vmProv((NativeMethodLoader) null);
  }

  public VmProv vmProv(NativeMethodLoader nativeMethodLoader) {
    return vmProv(new JobCreatorProv(nativeMethodLoader, typingB(), bytecodeF()));
  }

  public VmProv vmProv(JobCreatorProv jobCreatorProv) {
    var parallelExecutor = new ParallelJobExecutor(computer(), executionReporter());
    return new VmProv(jobCreatorProv, parallelExecutor);
  }

  public CompilerProv compilerProv() {
    return compilerProv(null);
  }

  public CompilerProv compilerProv(FileLoader fileLoader) {
    return compilerProv(fileLoader, bytecodeLoader());
  }

  public CompilerProv compilerProv(FileLoader fileLoader, BytecodeLoader bytecodeLoader) {
    return new CompilerProv(typeShConv(), bytecodeF(), typingB(), fileLoader, bytecodeLoader);
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

  public ParallelJobExecutor parallelJobExecutor() {
    return new ParallelJobExecutor(computer(), executionReporter());
  }

  public TestingModLoader module(String code) {
    return new TestingModLoader(this, code);
  }

  private ExecutionReporter executionReporter() {
    return new ExecutionReporter(reporter());
  }

  public ConsoleReporter reporter() {
    if (this.consoleReporter == null) {
      this.consoleReporter = new ConsoleReporter(console(), ALL, INFO);
    }
    return this.consoleReporter;
  }

  private Console console() {
    // Use System.out if you want to see smooth logs in junit output
    // var outputStream = System.out;
    var outputStream = nullOutputStream();
    return new Console(new PrintWriter(outputStream, true));
  }

  public ModS internalMod() {
    if (internalMod == null) {
      internalMod = new InternalModLoader(typeFS()).load();
    }
    return internalMod;
  }

  public Computer computer() {
    if (computer == null) {
      computer = new Computer(computationCache(), Hash.of(123), Providers.of(newContainer()));
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
    return new Container(fullFileSystem(), bytecodeF(), typingB());
  }

  public TypeSbConv typeShConv() {
    return new TypeSbConv(bytecodeF());
  }

  public BytecodeF bytecodeF() {
    if (bytecodeF == null) {
      bytecodeF = new BytecodeF(objDb(), catDb());
    }
    return bytecodeF;
  }

  public TypingS typingS() {
    if (typingS == null) {
      typingS = new TypingS(typeFS());
    }
    return typingS;
  }

  public TypingB typingB() {
    if (typingB == null) {
      typingB = new TypingB(catDb());
    }
    return typingB;
  }

  public TypeBF typeFB() {
    return catDb();
  }

  public TypeSF typeFS() {
    if (typeSF == null) {
      typeSF = new TypeSF();
    }
    return typeSF;
  }

  public CatDb catDb() {
    if (catDb == null) {
      catDb = new CatDb(hashedDb());
    }
    return catDb;
  }

  public ObjDb objDb() {
    if (objDb == null) {
      objDb = new ObjDbImpl(hashedDb(), catDb(), typingB());
    }
    return objDb;
  }

  public ComputationCache computationCache() {
    if (computationCache == null) {
      computationCache = new ComputationCache(
          computationCacheFileSystem(), objDb(), bytecodeF());
    }
    return computationCache;
  }

  public FileSystem computationCacheFileSystem() {
    if (computationCacheFileSystem == null) {
      computationCacheFileSystem = synchronizedMemoryFileSystem();
    }
    return computationCacheFileSystem;
  }

  public ObjDb objDbOther() {
    return new ObjDbImpl(hashedDb(), catDbOther(), typingB());
  }

  public CatDb catDbOther() {
    return new CatDb(hashedDb());
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

  // H types

  public TupleTB animalTB() {
    return catDb().tuple(list(stringTB(), intTB()));
  }

  public AnyTB anyTB() {
    return catDb().any();
  }

  public ArrayTB arrayTB() {
    return arrayTB(stringTB());
  }

  public ArrayTB arrayTB(TypeB elemT) {
    return catDb().array(elemT);
  }

  public BlobTB blobTB() {
    return catDb().blob();
  }

  public BoolTB boolTB() {
    return catDb().bool();
  }

  public TupleTB fileTB() {
    return tupleTB(stringTB(), blobTB());
  }

  public FuncTB funcTB() {
    return funcTB(intTB(), list(blobTB(), stringTB()));
  }

 public FuncTB funcTB(TypeB resT, ImmutableList<TypeB> paramTs) {
    return catDb().func(resT, paramTs);
  }

  public IntTB intTB() {
    return catDb().int_();
  }

  public MethodTB methodTB() {
    return methodTB(blobTB(), list(boolTB()));
  }

  public MethodTB methodTB(TypeB resT, ImmutableList<TypeB> paramTs) {
    return catDb().method(resT, paramTs);
  }

  public NothingTB nothingTB() {
    return catDb().nothing();
  }

  public TupleTB personTB() {
    return tupleTB(stringTB(), stringTB());
  }

  public StringTB stringTB() {
    return catDb().string();
  }

  public TupleTB tupleTB(TypeB... itemTs) {
    return catDb().tuple(ImmutableList.copyOf(itemTs));
  }

  public VarB varB(String name) {
    return catDb().var(name);
  }

  public VarBoundsB vbS(
      VarB var1, Side side1, TypeB bound1,
      VarB var2, Side side2, TypeB bound2) {
    var bounds1 = oneSideBoundB(side1, bound1);
    var bounds2 = oneSideBoundB(side2, bound2);
    if (var1.equals(var2)) {
      return varBoundsB(new BoundedB(var1, typingB().merge(bounds1, bounds2)));
    } else {
      return new VarBoundsB(ImmutableMap.of(
          var1, new BoundedB(var1, bounds1),
          var2, new BoundedB(var2, bounds2)
      ));
    }
  }

  public VarBoundsB vbB(VarB var, Side side, TypeB bound) {
    return varBoundsB(new BoundedB(var, oneSideBoundB(side, bound)));
  }

  public VarBoundsB vbB() {
    return varBoundsB();
  }

  public Bounds<TypeB> oneSideBoundB(Side side, TypeB type) {
    return typeFB().oneSideBound(side, type);
  }

  // Expr types

  public CallCB callCB() {
    return callCB(intTB());
  }

  public CallCB callCB(TypeB evalT) {
    return catDb().call(evalT);
  }

  public CombineCB combineCB(TypeB... itemTs) {
    return catDb().combine(tupleTB(itemTs));
  }

  public IfCB ifCB() {
    return ifCB(intTB());
  }

  public IfCB ifCB(TypeB evalT) {
    return catDb().if_(evalT);
  }

  public InvokeCB invokeCB() {
    return invokeCB(blobTB());
  }

  public InvokeCB invokeCB(TypeB evalT) {
    return catDb().invoke(evalT);
  }

  public MapCB mapCB() {
    return mapCB(arrayTB(intTB()));
  }

  public MapCB mapCB(ArrayTB evalT) {
    return catDb().map(evalT);
  }

  public OrderCB orderCB() {
    return orderCB(intTB());
  }

  public OrderCB orderCB(TypeB elemT) {
    return catDb().order(arrayTB(elemT));
  }

  public ParamRefCB paramRefCB() {
    return paramRefCB(intTB());
  }

  public ParamRefCB paramRefCB(TypeB evalT) {
    return catDb().paramRef(evalT);
  }

  public SelectCB selectCB() {
    return selectCB(intTB());
  }

  public SelectCB selectCB(TypeB evalT) {
    return catDb().select(evalT);
  }

  // Obj (values)

  public TupleB animalB() {
    return animalB("rabbit", 7);
  }

  public TupleB animalB(String species, int speed) {
    return animalB(stringB(species), intB(speed));
  }

  public TupleB animalB(StringB species, IntB speed) {
    return tupleB(animalTB(), species, speed);
  }

  public ArrayB arrayB(ValB... elems) {
    return arrayB(elems[0].type(), elems);
  }

  public ArrayB arrayB(TypeB elemT, ValB... elems) {
    return objDb()
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
            .add(ObjB.class)
            .add(StringB.class)
            .add(TupleB.class)
            .add(ValB.class)
            .build());
  }

  public BlobB blobBJarWithJavaByteCode(Class<?>... classes) throws IOException {
    return blobBWith(list(classes));
  }

  private BlobB blobBWith(List<Class<?>> list) throws IOException {
    var blobBBuilder = objDb().blobBuilder();
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
    return objDb().blobBuilder();
  }

  public BoolB boolB() {
    return boolB(true);
  }

  public BoolB boolB(boolean value) {
    return objDb().bool(value);
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
    return bytecodeF().file(string, blob);
  }

  public FuncB funcB() {
    return funcB(intB());
  }

  public FuncB funcB(ObjB body) {
    return funcB(list(), body);
  }

  public FuncB funcB(ImmutableList<TypeB> paramTs, ObjB body) {
    return funcB(body.type(), paramTs, body);
  }

  public FuncB funcB(TypeB resT, ImmutableList<TypeB> paramTs, ObjB body) {
    var type = funcTB(resT, paramTs);
    return funcB(type, body);
  }

  public FuncB funcB(FuncTB type, ObjB body) {
    return objDb().func(type, body);
  }

  public IntB intB() {
    return intB(17);
  }

  public IntB intB(int value) {
    return intB(BigInteger.valueOf(value));
  }

  public IntB intB(BigInteger value) {
    return objDb().int_(value);
  }

  public MethodB methodB(Class<?> clazz) throws IOException {
    return methodB(blobBJarWithPluginApi(clazz), stringB(clazz.getCanonicalName()));
  }

  public MethodB methodB() {
    return methodB(methodTB());
  }

  public MethodB methodB(MethodTB methodTB) {
    return methodB(methodTB, blobB(7), stringB("class binary name"), boolB(true));
  }

  public MethodB methodB(BlobB jar, StringB classBinaryName) {
    return methodB(methodTB(), jar, classBinaryName);
  }

  public MethodB methodB(MethodTB type, BlobB jar, StringB classBinaryName) {
    return methodB(type, jar, classBinaryName, boolB(true));
  }

  public MethodB methodB(MethodTB type, BlobB jar, StringB classBinaryName, BoolB isPure) {
    return objDb().method(type, jar, classBinaryName, isPure);
  }

  public TupleB personB(String firstName, String lastName) {
    return tupleB(stringB(firstName), stringB(lastName));
  }

  public StringB stringB() {
    return objDb().string("abc");
  }

  public StringB stringB(String string) {
    return objDb().string(string);
  }

  public TupleB tupleB(ValB... items) {
    var tupleTB = tupleTB(stream(items).map(ValB::type).toArray(TypeB[]::new));
    return tupleB(tupleTB, items);
  }

  public TupleB tupleB(TupleTB tupleT, ValB... items) {
    return objDb().tuple(tupleT, ImmutableList.copyOf(items));
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

  // Expr-s

  public CallB callB(ObjB func, ObjB... args) {
    var combineB = combineB(args);
    var evalT = inferResT(func, combineB);
    return callBImpl(evalT, func, combineB);
  }

  public CallB callB(TypeB evalT, ObjB func, ObjB... args) {
    return callBImpl(evalT, func, combineB(args));
  }

  public CallB callBImpl(TypeB evalT, ObjB func, CombineB args) {
    return objDb().call(evalT, func, args);
  }

  private TypeB inferResT(ObjB func, CombineB args) {
    var callableT = (CallableTB) func.type();
    var argsT = args.type();
    return typingB().inferCallResT(callableT, argsT.items(), () -> illegalArgs(callableT, argsT));
  }

  private IllegalArgumentException illegalArgs(CallableTB callableTB, TypeB argsT) {
    return new IllegalArgumentException(
        "Arguments evaluation type %s should be equal to callable type parameters %s."
            .formatted(argsT.q(), callableTB.paramsTuple().q()));
  }

  public CombineB combineB(ObjB... items) {
    var evalT = tupleTB(stream(items).map(ObjB::type).toArray(TypeB[]::new));
    return combineB(evalT, items);
  }

  public CombineB combineB(TupleTB evalT, ObjB... items) {
    return objDb().combine(evalT, ImmutableList.copyOf(items));
  }

  public IfB ifB(ObjB condition, ObjB then, ObjB else_) {
    return objDb().if_(condition, then, else_);
  }

  public InvokeB invokeB(ObjB method, ObjB... args) {
    var combineB = combineB(args);
    return invokeBImpl(inferResT(method, combineB), method, combineB);
  }

  public InvokeB invokeB(TypeB evalT, ObjB method, ObjB... args) {
    return invokeBImpl(evalT, method, combineB(args));
  }

  private InvokeB invokeBImpl(TypeB evalT, ObjB method, CombineB args) {
    return objDb().invoke(evalT, method, args);
  }

  public MapB mapB(ObjB array, ObjB func) {
    return objDb().map(array, func);
  }

  public OrderB orderB(ObjB... elems) {
    return orderB(elems[0].type(), elems);
  }

  public OrderB orderB(TypeB elemT, ObjB... elems) {
    var elemList = ImmutableList.copyOf(elems);
    return objDb().order(arrayTB(elemT), elemList);
  }

  public ParamRefB paramRefB(int index) {
    return paramRefB(intTB(), index);
  }

  public ParamRefB paramRefB(TypeB evalT, int index) {
    return objDb().paramRef(evalT, BigInteger.valueOf(index));
  }

  public SelectB selectB(ObjB tuple, IntB index) {
    var evalT = ((TupleTB) tuple.type()).items().get(index.toJ().intValue());
    return selectB(evalT, tuple, index);
  }

  public SelectB selectB(TypeB evalT, ObjB tuple, IntB index) {
    return objDb().select(evalT, tuple, index);
  }

  // Types Smooth

  public AnyTS anyTS() {
    return typeFS().any();
  }

  public ArrayTS arrayTS(TypeS elemT) {
    return typeFS().array(elemT);
  }

  public BlobTS blobTS() {
    return typeFS().blob();
  }

  public BoolTS boolTS() {
    return typeFS().bool();
  }

  public FuncTS funcTS(TypeS resT) {
    return funcTS(resT, ImmutableList.<TypeS>of());
  }

  public FuncTS funcTS(TypeS resT, List<? extends ItemS> params) {
    return funcTS(resT, toTypes(params));
  }

  public FuncTS funcTS(TypeS resT, ImmutableList<TypeS> paramTs) {
    return typeFS().func(resT, paramTs);
  }

  public IntTS intTS() {
    return typeFS().int_();
  }

  public NothingTS nothingTS() {
    return typeFS().nothing();
  }

  public StructTS personTS() {
    return typeFS().struct("Person",
        nList(sigS(stringTS(), "firstName"), sigS(stringTS(), "lastName")));
  }

  public StringTS stringTS() {
    return typeFS().string();
  }

  public StructTS structTS(String name, NList<ItemSigS> fields) {
    return typeFS().struct(name, fields);
  }

  public VarS varS(String name) {
    return typeFS().var(name);
  }

  public VarBoundsS vbS(
      VarS var1, Side side1, TypeS bound1,
      VarS var2, Side side2, TypeS bound2) {
    var bounds1 = oneSideBoundS(side1, bound1);
    var bounds2 = oneSideBoundS(side2, bound2);
    if (var1.equals(var2)) {
      return varBoundsS(new BoundedS(var1, typingS().merge(bounds1, bounds2)));
    } else {
      return new VarBoundsS(ImmutableMap.of(
          var1, new BoundedS(var1, bounds1),
          var2, new BoundedS(var2, bounds2)
      ));
    }
  }

  public VarBoundsS vbS(VarS var, Side side, TypeS bound) {
    return varBoundsS(new BoundedS(var, oneSideBoundS(side, bound)));
  }

  public VarBoundsS vbS() {
    return varBoundsS();
  }

  public Bounds<TypeS> oneSideBoundS(Side side, TypeS type) {
    return typeFS().oneSideBound(side, type);
  }

  // Expressions

  public BlobS blobS(int data) {
    return blobS(1, data);
  }

  public BlobS blobS(int line, int data) {
    return new BlobS(blobTS(), intToByteString(data), loc(line));
  }

  public CallS callS(TypeS type, ExprS callable, ExprS... args) {
    return callS(1, type, callable, args);
  }

  public CallS callS(int line, TypeS type, ExprS callable, ExprS... args) {
    return new CallS(type, callable, list(args), loc(line));
  }

  public IntS intS(int value) {
    return intS(1, value);
  }

  public IntS intS(int line, int value) {
    return new IntS(intTS(), BigInteger.valueOf(value), loc(line));
  }

  public OrderS orderS(TypeS elemT, ExprS... exprs) {
    return orderS(1, elemT, exprs);
  }

  public OrderS orderS(int line, TypeS elemT, ExprS... exprs) {
    return new OrderS(arrayTS(elemT), ImmutableList.copyOf(exprs), loc(line));
  }

  public ParamRefS paramRefS(TypeS type) {
    return paramRefS(type, "refName");
  }

  public ParamRefS paramRefS(TypeS type, String name) {
    return paramRefS(1, type, name);
  }

  public ParamRefS paramRefS(int line, TypeS type, String name) {
    return new ParamRefS(type, name, loc(line));
  }

  public TopRefS topRefS(TopEvalS topEval) {
    return topRefS(1, topEval.type(), topEval.name());
  }

  public TopRefS topRefS(int line, TypeS type, String name) {
    return new TopRefS(type, name, loc(line));
  }

  public SelectS selectS(TypeS type, ExprS selectable, String field) {
    return selectS(1, type, selectable, field);
  }

  public SelectS selectS(int line, TypeS type, ExprS selectable, String field) {
    return new SelectS(type, selectable, field, loc(line));
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

  public AnnS bytecodeS(int line, String path) {
    return bytecodeS(line, stringS(path));
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

  public AnnS nativeS() {
    return nativeS(1, stringS("implementation.Class"));
  }

  public AnnS nativeS(int line, StringS classBinaryName) {
    return nativeS(line, classBinaryName, true);
  }

  public AnnS nativeS(int line, StringS classBinaryName, boolean pure) {
    return nativeS(loc(line), classBinaryName, pure);
  }

  public AnnS nativeS(Loc loc, StringS classBinaryName) {
    return nativeS(loc, classBinaryName, true);
  }

  public AnnS nativeS(Loc loc, StringS classBinaryName, boolean pure) {
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

  private ItemS itemS(int line, TypeS type, String name, Optional<ExprS> body) {
    return new ItemS(type, modPath(), name, body, loc(line));
  }

  public AnnFuncS byteFuncS(String path, TypeS resT, String name, NList<ItemS> params) {
    return byteFuncS(1, new AnnS(BYTECODE, stringS(path), loc(1)), resT, name, params);
  }

  public AnnFuncS byteFuncS(int line, AnnS ann, TypeS resT, String name, NList<ItemS> params) {
    return byteFuncS(ann, funcTS(resT, params.list()), modPath(), name, params, loc(line));
  }

  public AnnFuncS byteFuncS(AnnS ann, FuncTS type, ModPath modPath, String name,
      NList<ItemS> params, Loc loc) {
    return new AnnFuncS(ann, type, modPath, name, params, loc);
  }

  public AnnValS annValS(int line, AnnS ann, TypeS type, String name) {
    return new AnnValS(ann, type, modPath(), name, loc(line));
  }

  public AnnValS annValS(AnnS ann, TypeS type, ModPath modPath, String name, Loc loc) {
    return new AnnValS(ann, type, modPath, name, loc);
  }

  public DefValS defValS(String name, ExprS expr) {
    return defValS(1, expr.type(), name, expr);
  }

  public DefValS defValS(int line, TypeS type, String name, ExprS expr) {
    return new DefValS(type, modPath(), name, expr, loc(line));
  }

  public SyntCtorS syntCtorS(StructTS structT) {
    return syntCtorS(1, structT, UPPER_CAMEL.to(LOWER_CAMEL, structT.name()));
  }

  public SyntCtorS syntCtorS(int line, StructTS structT, String name) {
    var fields = structT.fields();
    var params = fields.map(f -> new ItemS(f.type(), modPath(), f.nameSane(), empty(), loc(line)));
    return syntCtorS(line, funcTS(structT, params.list()), modPath(), name, params);
  }

  public SyntCtorS syntCtorS(int line, FuncTS funcT, ModPath modPath, String name,
      NList<ItemS> params) {
    return new SyntCtorS(funcT, modPath, name, params, loc(line));
  }

  public AnnFuncS natFuncS(TypeS resT, String name, NList<ItemS> params) {
    return natFuncS(resT, name, params, nativeS(1, stringS(1, "Impl.met")));
  }

  public AnnFuncS natFuncS(TypeS resT, String name, NList<ItemS> params, AnnS ann) {
    return natFuncS(1, resT, name, params, ann);
  }

  public AnnFuncS natFuncS(int line, TypeS resT, String name, NList<ItemS> params, AnnS ann) {
    return natFuncS(line, funcTS(resT, params.list()), modPath(), name, params, ann);
  }

  public AnnFuncS natFuncS(FuncTS funcT, String name, NList<ItemS> params) {
    return natFuncS(funcT, name, params, nativeS());
  }

  public AnnFuncS natFuncS(FuncTS funcT, String name, NList<ItemS> params, AnnS ann) {
    return natFuncS(1, funcT, name, params, ann);
  }

  public AnnFuncS natFuncS(int line, FuncTS funcT, String name, NList<ItemS> params, AnnS ann) {
    return natFuncS(line, funcT, modPath(), name, params, ann);
  }

  public AnnFuncS natFuncS(int line, FuncTS funcT, ModPath modPath, String name,
      NList<ItemS> params, AnnS ann) {
    return new AnnFuncS(ann, funcT, modPath, name, params, loc(line));
  }

  public DefFuncS defFuncS(TypeS type, String name, ExprS body, NList<ItemS> params) {
    return defFuncS(1, type, name, body, params);
  }

  public DefFuncS defFuncS(int line, TypeS type, String name, ExprS body, NList<ItemS> params) {
    return new DefFuncS(funcTS(type, params), modPath(), name, params, body, loc(line));
  }

  public DefFuncS defFuncS(String name, NList<ItemS> params, ExprS expr) {
    return defFuncS(expr.type(), name, params, expr);
  }

  public DefFuncS defFuncS(TypeS resT, String name, NList<ItemS> params, ExprS expr) {
    return new DefFuncS(funcTS(resT, toTypes(params)), modPath(), name, params, expr, loc(1));
  }

  public ItemSigS sigS(TypeS type, String name) {
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
    return fatal(userFileMessagge(line, message));
  }

  public static Log userError(int line, String message) {
    return error(userFileMessagge(line, message));
  }

  private static String userFileMessagge(int line, String message) {
    return PRJ_MOD_FILE_NAME + ":" + line + ": " + message;
  }

  public static SynchronizedFileSystem synchronizedMemoryFileSystem() {
    return new SynchronizedFileSystem(new MemoryFileSystem());
  }
}
