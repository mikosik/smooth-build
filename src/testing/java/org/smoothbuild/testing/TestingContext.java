package org.smoothbuild.testing;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Optional.empty;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.ALL;
import static org.smoothbuild.lang.base.define.ItemS.toTypes;
import static org.smoothbuild.lang.base.define.TestingLoc.loc;
import static org.smoothbuild.lang.base.define.TestingModPath.modPath;
import static org.smoothbuild.lang.base.type.api.VarBounds.varBounds;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.smoothbuild.bytecode.ObjFactory;
import org.smoothbuild.bytecode.obj.ByteDb;
import org.smoothbuild.bytecode.obj.ByteDbImpl;
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
import org.smoothbuild.bytecode.type.TypeFactoryB;
import org.smoothbuild.bytecode.type.TypingB;
import org.smoothbuild.bytecode.type.base.TypeB;
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
import org.smoothbuild.bytecode.type.val.CallableTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.IntTB;
import org.smoothbuild.bytecode.type.val.MethodTB;
import org.smoothbuild.bytecode.type.val.NothingTB;
import org.smoothbuild.bytecode.type.val.StringTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.VarTB;
import org.smoothbuild.cli.console.Console;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.db.Hash;
import org.smoothbuild.db.HashedDb;
import org.smoothbuild.eval.compile.CompilerProv;
import org.smoothbuild.eval.compile.TypeShConv;
import org.smoothbuild.install.TempManager;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.SynchronizedFileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.lang.base.define.DefFuncS;
import org.smoothbuild.lang.base.define.DefValS;
import org.smoothbuild.lang.base.define.IfFuncS;
import org.smoothbuild.lang.base.define.InternalModLoader;
import org.smoothbuild.lang.base.define.ItemS;
import org.smoothbuild.lang.base.define.ItemSigS;
import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.define.MapFuncS;
import org.smoothbuild.lang.base.define.ModPath;
import org.smoothbuild.lang.base.define.ModS;
import org.smoothbuild.lang.base.define.NatFuncS;
import org.smoothbuild.lang.base.define.TopEvalS;
import org.smoothbuild.lang.base.type.api.Bounded;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.api.VarBounds;
import org.smoothbuild.lang.base.type.impl.AnyTS;
import org.smoothbuild.lang.base.type.impl.ArrayTS;
import org.smoothbuild.lang.base.type.impl.BlobTS;
import org.smoothbuild.lang.base.type.impl.BoolTS;
import org.smoothbuild.lang.base.type.impl.FuncTS;
import org.smoothbuild.lang.base.type.impl.IntTS;
import org.smoothbuild.lang.base.type.impl.NothingTS;
import org.smoothbuild.lang.base.type.impl.StringTS;
import org.smoothbuild.lang.base.type.impl.StructTS;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.base.type.impl.TypingS;
import org.smoothbuild.lang.base.type.impl.VarTS;
import org.smoothbuild.lang.expr.AnnS;
import org.smoothbuild.lang.expr.BlobS;
import org.smoothbuild.lang.expr.CallS;
import org.smoothbuild.lang.expr.CombineS;
import org.smoothbuild.lang.expr.ExprS;
import org.smoothbuild.lang.expr.IntS;
import org.smoothbuild.lang.expr.OrderS;
import org.smoothbuild.lang.expr.ParamRefS;
import org.smoothbuild.lang.expr.SelectS;
import org.smoothbuild.lang.expr.StringS;
import org.smoothbuild.lang.expr.TopRefS;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.vm.Vm;
import org.smoothbuild.vm.VmProv;
import org.smoothbuild.vm.compute.ComputationCache;
import org.smoothbuild.vm.compute.Computer;
import org.smoothbuild.vm.compute.Container;
import org.smoothbuild.vm.java.FileLoader;
import org.smoothbuild.vm.java.MethodLoader;
import org.smoothbuild.vm.job.JobCreatorProvider;
import org.smoothbuild.vm.parallel.ExecutionReporter;
import org.smoothbuild.vm.parallel.ParallelJobExecutor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.inject.util.Providers;

import okio.ByteString;

public class TestingContext {
  private Computer computer;
  private Container container;
  private ObjFactory objFactory;
  private ComputationCache computationCache;
  private FileSystem computationCacheFileSystem;
  private ByteDb byteDb;
  private TypingS typingS;
  private TypingB typingB;
  private CatDb catDb;
  private HashedDb hashedDb;
  private FileSystem hashedDbFileSystem;
  private FileSystem fullFileSystem;
  private TempManager tempManager;
  private ModS internalMod;
  private TypeFactoryS typeFactoryS;

  public Vm vm() {
    return vmProv().get(ImmutableMap.of());
  }

  public VmProv vmProv() {
    return vmProv(null);
  }

  public VmProv vmProv(MethodLoader methodLoader) {
    var jobCreatorProvider = new JobCreatorProvider(methodLoader, typeFactoryB(), typingB());
    var console = new Console(new PrintWriter(System.out, true));
    var reporter = new ExecutionReporter(new Reporter(console, ALL, INFO));
    var parallelExecutor = new ParallelJobExecutor(computer(), reporter);
    return new VmProv(jobCreatorProvider, parallelExecutor);
  }

  public CompilerProv compilerProv() {
    return compilerProv(null);
  }

  public CompilerProv compilerProv(FileLoader fileLoader) {
    return new CompilerProv(typeShConv(), objFactory(), fileLoader);
  }

  public TestingModLoader mod(String sourceCode) {
    return new TestingModLoader(this, sourceCode);
  }

  public ModS internalMod() {
    if (internalMod == null) {
      internalMod = new InternalModLoader(typeFactoryS()).load();
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
    return new Container(fullFileSystem(), objFactory());
  }

  public TypeShConv typeShConv() {
    return new TypeShConv(objFactory());
  }

  public ObjFactory objFactory() {
    if (objFactory == null) {
      objFactory = new ObjFactory(byteDb(), catDb(), typingB());
    }
    return objFactory;
  }

  public TypingS typingS() {
    if (typingS == null) {
      typingS = new TypingS(typeFactoryS());
    }
    return typingS;
  }

  public TypingB typingB() {
    if (typingB == null) {
      typingB = new TypingB(catDb());
    }
    return typingB;
  }

  public TypeFactoryB typeFactoryB() {
    return catDb();
  }

  public TypeFactoryS typeFactoryS() {
    if (typeFactoryS == null) {
      typeFactoryS = new TypeFactoryS();
    }
    return typeFactoryS;
  }

  public CatDb catDb() {
    if (catDb == null) {
      catDb = new CatDb(hashedDb());
    }
    return catDb;
  }

  public ByteDb byteDb() {
    if (byteDb == null) {
      byteDb = new ByteDbImpl(hashedDb(), catDb(), typingB());
    }
    return byteDb;
  }

  public ComputationCache computationCache() {
    if (computationCache == null) {
      computationCache = new ComputationCache(
          computationCacheFileSystem(), byteDb(), objFactory());
    }
    return computationCache;
  }

  public FileSystem computationCacheFileSystem() {
    if (computationCacheFileSystem == null) {
      computationCacheFileSystem = new MemoryFileSystem();
    }
    return computationCacheFileSystem;
  }

  public ByteDb byteDbOther() {
    return new ByteDbImpl(hashedDb(), catDbOther(), typingB());
  }

  public CatDb catDbOther() {
    return new CatDb(hashedDb());
  }

  public HashedDb hashedDb() {
    if (hashedDb == null) {
      hashedDb = new HashedDb(
          hashedDbFileSystem(), Path.root(), tempManager());
    }
    return hashedDb;
  }

  public FileSystem hashedDbFileSystem() {
    if (hashedDbFileSystem == null) {
      hashedDbFileSystem = new SynchronizedFileSystem(new MemoryFileSystem());
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
      fullFileSystem = new SynchronizedFileSystem(new MemoryFileSystem());
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
    return tupleTB(list(blobTB(), stringTB()));
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
    return tupleTB(list(stringTB(), stringTB()));
  }

  public StringTB stringTB() {
    return catDb().string();
  }

  public TupleTB tupleTB() {
    return catDb().tuple(list(intTB()));
  }

  public TupleTB tupleTB(ImmutableList<TypeB> itemTs) {
    return catDb().tuple(itemTs);
  }

  public TupleTB tupleEmptyTB() {
    return tupleTB(list());
  }

  public TupleTB tupleWithStrTB() {
    return tupleTB(list(stringTB()));
  }

  public VarTB varTB(String name) {
    return catDb().var(name);
  }

  public Side<TypeB> lowerB() {
    return typeFactoryB().lower();
  }

  public Side<TypeB> upperB() {
    return typeFactoryB().upper();
  }

  // Expr types

  public CallCB callCB() {
    return callCB(intTB());
  }

  public CallCB callCB(TypeB evalT) {
    return catDb().call(evalT);
  }

  public CombineCB combineCB() {
    return combineCB(list(intTB(), stringTB()));
  }

  public CombineCB combineCB(ImmutableList<TypeB> itemTs) {
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
    return catDb().order(elemT);
  }

  public ParamRefCB paramRefCB() {
    return paramRefCB(intTB());
  }

  public ParamRefCB paramRefCB(TypeB evalT) {
    return catDb().ref(evalT);
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
    return tupleB(animalTB(), list(species, speed));
  }

  public ArrayB arrayB(ValB... elems) {
    return arrayB(elems[0].type(), elems);
  }

  public ArrayB arrayB(TypeB elemT, ValB... elems) {
    return byteDb()
        .arrayBuilder(arrayTB(elemT))
        .addAll(list(elems))
        .build();
  }

  public BlobB blobB() {
    return objFactory().blob(sink -> sink.writeUtf8("blob data"));
  }

  public BlobB blobB(int data) {
    return blobB(ByteString.of((byte) data));
  }

  public BlobB blobB(ByteString bytes) {
    return objFactory().blob(sink -> sink.write(bytes));
  }

  public BlobBBuilder blobBBuilder() {
    return byteDb().blobBuilder();
  }

  public BoolB boolB() {
    return boolB(true);
  }

  public BoolB boolB(boolean value) {
    return byteDb().bool(value);
  }

  public TupleB fileB(Path path) {
    return fileB(path, ByteString.encodeString(path.toString(), CHARSET));
  }

  public TupleB fileB(Path path, ByteString content) {
    return fileB(path.toString(), blobB(content));
  }

  public TupleB fileB(String path, BlobB blob) {
    StringB string = objFactory().string(path);
    return objFactory().file(string, blob);
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
    return byteDb().func(type, body);
  }

  public IntB intB() {
    return intB(17);
  }

  public IntB intB(int value) {
    return byteDb().int_(BigInteger.valueOf(value));
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
    return byteDb().method(type, jar, classBinaryName, isPure);
  }

  public TupleB personB(String firstName, String lastName) {
    return tupleB(list(stringB(firstName), stringB(lastName)));
  }

  public StringB stringB() {
    return byteDb().string("abc");
  }

  public StringB stringB(String string) {
    return byteDb().string(string);
  }

  public TupleB tupleB(ImmutableList<ValB> items) {
    var type = tupleTB(map(items, ValB::type));
    return tupleB(type, items);
  }

  public TupleB tupleB(TupleTB tupleT, ImmutableList<ValB> items) {
    return byteDb().tuple(tupleT, items);
  }

  public TupleB tupleBEmpty() {
    return tupleB(list());
  }

  public ArrayB messageArrayWithOneError() {
    return arrayB(objFactory().errorMessage("error message"));
  }

  public ArrayB messageArrayEmtpy() {
    return arrayB(objFactory().messageT());
  }

  public TupleB errorMessage(String text) {
    return objFactory().errorMessage(text);
  }

  public TupleB warningMessage(String text) {
    return objFactory().warningMessage(text);
  }

  public TupleB infoMessage(String text) {
    return objFactory().infoMessage(text);
  }

  // Expr-s

  public CallB callB(ObjB func, ImmutableList<ObjB> args) {
    return callB(func, combineB(args));
  }

  public CallB callB(ObjB func, CombineB args) {
    var evalT = inferResT(func, args);
    return callB(evalT, func, args);
  }

  public CallB callB(TypeB evalT, ObjB func, ImmutableList<ObjB> args) {
    return callB(evalT, func, combineB(args));
  }

  public CallB callB(TypeB evalT, ObjB func, CombineB args) {
    return byteDb().call(evalT, func, args);
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

  public CombineB combineB(ImmutableList<ObjB> items) {
    var evalT = tupleTB(map(items, ObjB::type));
    return combineB(evalT, items);
  }

  public CombineB combineB(TupleTB evalT, ImmutableList<ObjB> items) {
    return byteDb().combine(evalT, items);
  }

  public IfB ifB(ObjB condition, ObjB then, ObjB else_) {
    return byteDb().if_(condition, then, else_);
  }

  public InvokeB invokeB(MethodB method) {
    var args = combineB(createParamRefsB(method.type().params()));
    return invokeB(method, args);
  }

  public InvokeB invokeB(ObjB method, ImmutableList<ObjB> args) {
    return invokeB(method, combineB(args));
  }

  public InvokeB invokeB(ObjB method, CombineB args) {
    var resT = inferResT(method, args);
    return byteDb().invoke(resT, method, args);
  }

  public InvokeB invokeB(TypeB evalT, ObjB method, ImmutableList<ObjB> args) {
    return invokeB(evalT, method, combineB(args));
  }

  public InvokeB invokeB(TypeB evalT, ObjB method, CombineB args) {
    return byteDb().invoke(evalT, method, args);
  }

  private ImmutableList<ObjB> createParamRefsB(ImmutableList<TypeB> paramTs) {
    Builder<ObjB> builder = ImmutableList.builder();
    for (int i = 0; i < paramTs.size(); i++) {
      builder.add(paramRefB(paramTs.get(i), i));
    }
    return builder.build();
  }

  public MapB mapB(ObjB array, ObjB func) {
    return byteDb().map(array, func);
  }

  public OrderB orderB(ImmutableList<ObjB> elems) {
    var elemT = elems.get(0).type();
    return orderB(elemT, elems);
  }

  public OrderB orderB(TypeB elemT, ImmutableList<ObjB> elems) {
    return byteDb().order(arrayTB(elemT), elems);
  }

  public ParamRefB paramRefB(int index) {
    return paramRefB(intTB(), index);
  }

  public ParamRefB paramRefB(TypeB evalT, int index) {
    return byteDb().paramRef(evalT, BigInteger.valueOf(index));
  }

  public SelectB selectB(ObjB tuple, IntB index) {
    var evalT = ((TupleTB) tuple.type()).items().get(index.toJ().intValue());
    return byteDb().select(evalT, tuple, index);
  }

  public SelectB selectB(TypeB evalT, ObjB tuple, IntB index) {
    return byteDb().select(evalT, tuple, index);
  }

  // Types Smooth

  public AnyTS anyTS() {
    return typeFactoryS().any();
  }

  public ArrayTS arrayTS(TypeS elemT) {
    return typeFactoryS().array(elemT);
  }

  public BlobTS blobTS() {
    return typeFactoryS().blob();
  }

  public BoolTS boolTS() {
    return typeFactoryS().bool();
  }

  public FuncTS funcTS(TypeS resT) {
    return funcTS(resT, ImmutableList.<TypeS>of());
  }

  public FuncTS funcTS(TypeS resT, List<? extends ItemS> params) {
    return funcTS(resT, toTypes(params));
  }

  public FuncTS funcTS(TypeS resT, ImmutableList<TypeS> paramTs) {
    return typeFactoryS().func(resT, paramTs);
  }

  public IntTS intTS() {
    return typeFactoryS().int_();
  }

  public NothingTS nothingTS() {
    return typeFactoryS().nothing();
  }

  public StructTS personTS() {
    return typeFactoryS().struct("Person",
        nList(sigS(stringTS(), "firstName"), sigS(stringTS(), "lastName")));
  }

  public StringTS stringTS() {
    return typeFactoryS().string();
  }

  public StructTS structTS(String name, NList<ItemSigS> fields) {
    return typeFactoryS().struct(name, fields);
  }

  public VarTS varTS(String name) {
    return typeFactoryS().var(name);
  }

  public Side<TypeS> lowerS() {
    return typeFactoryS().lower();
  }

  public Side<TypeS> upperS() {
    return typeFactoryS().upper();
  }

  public VarBounds<TypeS> vbS(
      VarTS var1, Side<TypeS> side1, TypeS bound1,
      VarTS var2, Side<TypeS> side2, TypeS bound2) {
    Bounds<TypeS> bounds1 = oneSideBoundS(side1, bound1);
    Bounds<TypeS> bounds2 = oneSideBoundS(side2, bound2);
    if (var1.equals(var2)) {
      return varBounds(new Bounded<>(var1, typingS().merge(bounds1, bounds2)));
    } else {
      return new VarBounds<>(ImmutableMap.of(
          var1, new Bounded<>(var1, bounds1),
          var2, new Bounded<>(var2, bounds2)
      ));
    }
  }

  public VarBounds<TypeS> vbS(VarTS var, Side<TypeS> side, TypeS bound) {
    return varBounds(new Bounded<>(var, oneSideBoundS(side, bound)));
  }

  public VarBounds<TypeS> vbS() {
    return varBounds();
  }

  public Bounds<TypeS> oneSideBoundS(Side<TypeS> side, TypeS type) {
    return typeFactoryS().oneSideBound(side, type);
  }

  // Expressions

  public BlobS blobS(int data) {
    return blobS(1, data);
  }

  public BlobS blobS(int line, int data) {
    return new BlobS(blobTS(), ByteString.of((byte) data), loc(line));
  }

  public CallS callS(TypeS type, ExprS callable, ExprS... args) {
    return callS(1, type, callable, args);
  }

  public CallS callS(int line, TypeS type, ExprS callable, ExprS... args) {
    return new CallS(type, callable, list(args), loc(line));
  }

  public CombineS combineS(ExprS... expr) {
    var exprs = ImmutableList.copyOf(expr);
    return combineS(1, structTS("MyStruct", nList(exprsToItemSigs(exprs))), exprs);
  }

  private ImmutableList<ItemSigS> exprsToItemSigs(ImmutableList<ExprS> exprs) {
    return IntStream.range(0, exprs.size())
        .mapToObj(i -> new ItemSigS(exprs.get(i).type(), "field" + i, empty()))
        .collect(toImmutableList());
  }

  public CombineS combineS(int line, StructTS type, ExprS... items) {
    return combineS(line, type, ImmutableList.copyOf(items));
  }

  public CombineS combineS(StructTS type, ImmutableList<ExprS> items) {
    return combineS(1, type, items);
  }

  public CombineS combineS(int line, StructTS type, ImmutableList<ExprS> items) {
    return new CombineS(type, items, loc(line));
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

  public AnnS annS() {
    return annS(1, stringS("implementation.Class"));
  }

  public AnnS annS(int line, StringS implementedBy) {
    return annS(line, implementedBy, true);
  }

  public AnnS annS(int line, StringS implementedBy, boolean pure) {
    return annS(loc(line), implementedBy, pure);
  }

  public AnnS annS(Loc loc, StringS implementedBy) {
    return annS(loc, implementedBy, true);
  }

  public AnnS annS(Loc loc, StringS implementedBy, boolean pure) {
    return new AnnS(implementedBy, pure, loc);
  }

  public ItemS itemS(TypeS type, String name) {
    return itemS(1, type, name);
  }

  public ItemS itemS(int line, TypeS type, String name) {
    return itemS(line, type, name, empty());
  }

  public ItemS itemS(int line, TypeS type, String name, ExprS defaultArg) {
    return itemS(line, type, name, Optional.of(defaultArg));
  }

  private ItemS itemS(int line, TypeS type, String name, Optional<ExprS> defaultArg) {
    return new ItemS(type, modPath(), name, defaultArg, loc(line));
  }

  public DefValS defValS(String name, ExprS expr) {
    return defValS(1, expr.type(), name, expr);
  }

  public DefValS defValS(int line, TypeS type, String name, ExprS expr) {
    return new DefValS(type, modPath(), name, expr, loc(line));
  }

  public NatFuncS natFuncS(TypeS resT, String name, NList<ItemS> params) {
    return natFuncS(resT, name, params, annS(1, stringS(1, "Impl.met")));
  }

  public NatFuncS natFuncS(TypeS resT, String name, NList<ItemS> params, AnnS annS) {
    return natFuncS(1, resT, name, params, annS);
  }

  public NatFuncS natFuncS(int line, TypeS resT, String name, NList<ItemS> params, AnnS annS) {
    return natFuncS(line, funcTS(resT, params.list()), modPath(), name, params, annS);
  }

  public NatFuncS natFuncS(FuncTS funcT, String name, NList<ItemS> params) {
    return natFuncS(funcT, name, params, annS());
  }

  public NatFuncS natFuncS(FuncTS funcT, String name, NList<ItemS> params, AnnS ann) {
    return natFuncS(1, funcT, name, params, ann);
  }

  public NatFuncS natFuncS(int line, FuncTS funcT, String name, NList<ItemS> params, AnnS ann) {
    return natFuncS(line, funcT, modPath(), name, params, ann);
  }

  public NatFuncS natFuncS(int line, FuncTS funcT, ModPath modPath, String name, NList<ItemS> params,
      AnnS ann) {
    return new NatFuncS(funcT, modPath, name, params, ann, loc(line));
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

  public IfFuncS ifFuncS() {
    return new IfFuncS(modPath(), typeFactoryS());
  }

  public MapFuncS mapFuncS() {
    return new MapFuncS(modPath(), typeFactoryS());
  }

  public ItemSigS sigS(TypeS type, String name) {
    return new ItemSigS(type, name, empty());
  }
}
