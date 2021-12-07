package org.smoothbuild.testing;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Optional.empty;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.lang.base.define.ItemS.toTypes;
import static org.smoothbuild.lang.base.define.TestingLoc.loc;
import static org.smoothbuild.lang.base.define.TestingModPath.modPath;
import static org.smoothbuild.lang.base.type.api.BoundsMap.boundsMap;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import java.math.BigInteger;
import java.util.Optional;
import java.util.stream.IntStream;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.object.db.ObjFactory;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.obj.expr.CallH;
import org.smoothbuild.db.object.obj.expr.CombineH;
import org.smoothbuild.db.object.obj.expr.IfH;
import org.smoothbuild.db.object.obj.expr.InvokeH;
import org.smoothbuild.db.object.obj.expr.MapH;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.ParamRefH;
import org.smoothbuild.db.object.obj.expr.SelectH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BlobHBuilder;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.FuncH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.db.object.type.CatDb;
import org.smoothbuild.db.object.type.TypeFactoryH;
import org.smoothbuild.db.object.type.TypingH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.expr.CallCH;
import org.smoothbuild.db.object.type.expr.CombineCH;
import org.smoothbuild.db.object.type.expr.IfCH;
import org.smoothbuild.db.object.type.expr.InvokeCH;
import org.smoothbuild.db.object.type.expr.MapCH;
import org.smoothbuild.db.object.type.expr.OrderCH;
import org.smoothbuild.db.object.type.expr.ParamRefCH;
import org.smoothbuild.db.object.type.expr.SelectCH;
import org.smoothbuild.db.object.type.val.AnyTH;
import org.smoothbuild.db.object.type.val.ArrayTH;
import org.smoothbuild.db.object.type.val.BlobTH;
import org.smoothbuild.db.object.type.val.BoolTH;
import org.smoothbuild.db.object.type.val.FuncTH;
import org.smoothbuild.db.object.type.val.IntTH;
import org.smoothbuild.db.object.type.val.NothingTH;
import org.smoothbuild.db.object.type.val.StringTH;
import org.smoothbuild.db.object.type.val.TupleTH;
import org.smoothbuild.db.object.type.val.VarH;
import org.smoothbuild.exec.compute.ComputationCache;
import org.smoothbuild.exec.compute.Computer;
import org.smoothbuild.exec.compute.Container;
import org.smoothbuild.exec.plan.TypeShConv;
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
import org.smoothbuild.lang.base.type.api.BoundsMap;
import org.smoothbuild.lang.base.type.api.Sides.Side;
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
import org.smoothbuild.lang.base.type.impl.VarS;
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
  private ObjDb objDb;
  private TypingS typingS;
  private TypingH typingH;
  private CatDb catDb;
  private HashedDb hashedDb;
  private FileSystem hashedDbFileSystem;
  private FileSystem fullFileSystem;
  private TempManager tempManager;
  private ModS internalMod;
  private TypeFactoryS typeFactoryS;

  public NativeApi nativeApi() {
    return container();
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
      objFactory = new ObjFactory(objDb(), catDb(), typingH());
    }
    return objFactory;
  }

  public TypingS typingS() {
    if (typingS == null) {
      typingS = new TypingS(typeFactoryS());
    }
    return typingS;
  }

  public TypingH typingH() {
    if (typingH == null) {
      typingH = new TypingH(catDb());
    }
    return typingH;
  }

  public TypeFactoryH typeFactoryH() {
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

  public ObjDb objDb() {
    if (objDb == null) {
      objDb = new ObjDb(hashedDb(), catDb(), typingH());
    }
    return objDb;
  }

  public ComputationCache computationCache() {
    if (computationCache == null) {
      computationCache = new ComputationCache(
          computationCacheFileSystem(), objDb(), objFactory());
    }
    return computationCache;
  }

  public FileSystem computationCacheFileSystem() {
    if (computationCacheFileSystem == null) {
      computationCacheFileSystem = new MemoryFileSystem();
    }
    return computationCacheFileSystem;
  }

  public ObjDb objDbOther() {
    return new ObjDb(hashedDb(), catDbOther(), typingH());
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

  public TupleTH animalTH() {
    return catDb().tuple(list(stringTH(), intTH()));
  }

  public AnyTH anyTH() {
    return catDb().any();
  }

  public ArrayTH arrayTH() {
    return arrayTH(stringTH());
  }

  public ArrayTH arrayTH(TypeH elemT) {
    return catDb().array(elemT);
  }

  public BlobTH blobTH() {
    return catDb().blob();
  }

  public BoolTH boolTH() {
    return catDb().bool();
  }

  public TupleTH fileTH() {
    return tupleTH(list(blobTH(), stringTH()));
  }

  public FuncTH funcTH() {
    return funcTH(intTH(), list(blobTH(), stringTH()));
  }

  public FuncTH funcTH(TypeH resT, ImmutableList<TypeH> paramTs) {
    return catDb().func(resT, paramTs);
  }

  public IntTH intTH() {
    return catDb().int_();
  }

  public NothingTH nothingTH() {
    return catDb().nothing();
  }

  public TupleTH personTH() {
    return tupleTH(list(stringTH(), stringTH()));
  }

  public StringTH stringTH() {
    return catDb().string();
  }

  public TupleTH tupleTH() {
    return catDb().tuple(list(intTH()));
  }

  public TupleTH tupleTH(ImmutableList<TypeH> itemTs) {
    return catDb().tuple(itemTs);
  }

  public TupleTH tupleEmptyTH() {
    return tupleTH(list());
  }

  public TupleTH tupleWithStrTH() {
    return tupleTH(list(stringTH()));
  }

  public VarH varTH(String name) {
    return catDb().var(name);
  }

  public Side<TypeH> lowerH() {
    return typeFactoryH().lower();
  }

  public Side<TypeH> upperH() {
    return typeFactoryH().upper();
  }

  // Expr types

  public CallCH callCH() {
    return callCH(intTH());
  }

  public CallCH callCH(TypeH evalT) {
    return catDb().call(evalT);
  }

  public CombineCH combineCH() {
    return combineCH(list(intTH(), stringTH()));
  }

  public CombineCH combineCH(ImmutableList<TypeH> itemTs) {
    return catDb().combine(tupleTH(itemTs));
  }

  public IfCH ifCH() {
    return ifCH(intTH());
  }

  public IfCH ifCH(TypeH evalT) {
    return catDb().if_(evalT);
  }

  public InvokeCH invokeCH() {
    return invokeCH(blobTH(), list(boolTH()));
  }

  public InvokeCH invokeCH(TypeH resT, ImmutableList<TypeH> paramTs) {
    return catDb().invoke(resT, paramTs);
  }

  public MapCH mapCH() {
    return mapCH(arrayTH(intTH()));
  }

  public MapCH mapCH(ArrayTH evalT) {
    return catDb().map(evalT);
  }

  public OrderCH orderCH() {
    return orderCH(intTH());
  }

  public OrderCH orderCH(TypeH elemT) {
    return catDb().order(elemT);
  }

  public ParamRefCH paramRefCH() {
    return paramRefCH(intTH());
  }

  public ParamRefCH paramRefCH(TypeH evalT) {
    return catDb().ref(evalT);
  }

  public SelectCH selectCH() {
    return selectCH(intTH());
  }

  public SelectCH selectCH(TypeH evalT) {
    return catDb().select(evalT);
  }

  // Obj-s (values)

  public TupleH animalH() {
    return animalH("rabbit", 7);
  }

  public TupleH animalH(String species, int speed) {
    return animalH(stringH(species), intH(speed));
  }

  public TupleH animalH(StringH species, IntH speed) {
    return tupleH(animalTH(), list(species, speed));
  }

  public ArrayH arrayH(ValH... elems) {
    return arrayH(elems[0].type(), elems);
  }

  public ArrayH arrayH(TypeH elemT, ValH... elems) {
    return objDb()
        .arrayBuilder(arrayTH(elemT))
        .addAll(list(elems))
        .build();
  }

  public BlobH blobH() {
    return objFactory().blob(sink -> sink.writeUtf8("blob data"));
  }

  public BlobH blobH(int data) {
    return blobH(ByteString.of((byte) data));
  }

  public BlobH blobH(ByteString bytes) {
    return objFactory().blob(sink -> sink.write(bytes));
  }

  public BlobHBuilder blobHBuilder() {
    return objDb().blobBuilder();
  }

  public BoolH boolH() {
    return boolH(true);
  }

  public BoolH boolH(boolean value) {
    return objDb().bool(value);
  }

  public TupleH fileH(Path path) {
    return fileH(path, ByteString.encodeString(path.toString(), CHARSET));
  }

  public TupleH fileH(Path path, ByteString content) {
    return fileH(path.toString(), blobH(content));
  }

  public TupleH fileH(String path, BlobH blob) {
    StringH string = objFactory().string(path);
    return objFactory().file(string, blob);
  }

  public FuncH funcH() {
    return funcH(intH());
  }

  public FuncH funcH(ObjH body) {
    return funcH(list(), body);
  }

  public FuncH funcH(ImmutableList<TypeH> paramTs, ObjH body) {
    var type = funcTH(body.type(), paramTs);
    return funcH(type, body);
  }

  public FuncH funcH(FuncTH type, ObjH body) {
    return objDb().func(type, body);
  }

  public IntH intH() {
    return intH(17);
  }

  public IntH intH(int value) {
    return objDb().int_(BigInteger.valueOf(value));
  }

  public InvokeH invokeH(BlobH jarFile, StringH classBinaryName) {
    return invokeH(invokeCH(), jarFile, classBinaryName);
  }

  public InvokeH invokeH(InvokeCH type, BlobH jarFile, StringH classBinaryName) {
    var args = combineH(createParamRefsH(type.params()));
    return objDb().invoke(type, jarFile, classBinaryName, boolH(true), args);
  }

  private ImmutableList<ObjH> createParamRefsH(ImmutableList<TypeH> paramTs) {
    Builder<ObjH> builder = ImmutableList.builder();
    for (int i = 0; i < paramTs.size(); i++) {
      builder.add(paramRefH(paramTs.get(i), i));
    }
    return builder.build();
  }

  public TupleH personH(String firstName, String lastName) {
    return tupleH(list(stringH(firstName), stringH(lastName)));
  }

  public StringH stringH() {
    return objDb().string("abc");
  }

  public StringH stringH(String string) {
    return objDb().string(string);
  }

  public TupleH tupleH(ImmutableList<ValH> items) {
    var type = tupleTH(map(items, ValH::type));
    return tupleH(type, items);
  }

  public TupleH tupleH(TupleTH tupleT, ImmutableList<ValH> items) {
    return objDb().tuple(tupleT, items);
  }

  public TupleH tupleHEmpty() {
    return tupleH(list());
  }

  public TupleH tupleHWithStr() {
    return tupleH(list(stringH("abc")));
  }

  public TupleH tupleHWithStr(StringH str) {
    return tupleH(list(str));
  }

  public ArrayH messageArrayWithOneError() {
    return arrayH(objFactory().errorMessage("error message"));
  }

  public ArrayH messageArrayEmtpy() {
    return arrayH(objFactory().messageT());
  }

  public TupleH errorMessage(String text) {
    return objFactory().errorMessage(text);
  }

  public TupleH warningMessage(String text) {
    return objFactory().warningMessage(text);
  }

  public TupleH infoMessage(String text) {
    return objFactory().infoMessage(text);
  }

  // Expr-s

  public CallH callH(ObjH func, ImmutableList<ObjH> args) {
    return objDb().call(func, combineH(args));
  }

  public CombineH combineH(ImmutableList<ObjH> items) {
    return objDb().combine(items);
  }

  public IfH ifH(ObjH condition, ObjH then, ObjH else_) {
    return objDb().if_(condition, then, else_);
  }

  public MapH mapH(ObjH array, ObjH func) {
    return objDb().map(array, func);
  }

  public OrderH orderH(ImmutableList<ObjH> elems) {
    return objDb().order(elems);
  }

  public ParamRefH paramRefH(int index) {
    return paramRefH(intTH(), index);
  }

  public ParamRefH paramRefH(TypeH evalT, int index) {
    return objDb().newParamRef(BigInteger.valueOf(index), evalT);
  }

  public SelectH selectH(ObjH tuple, IntH index) {
    return objDb().select(tuple, index);
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

  public FuncTS funcTS(TypeS resT, ItemS... params) {
    return funcTS(resT, toTypes(list(params)));
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

  public VarS varS(String name) {
    return typeFactoryS().var(name);
  }

  public Side<TypeS> lowerS() {
    return typeFactoryS().lower();
  }

  public Side<TypeS> upperS() {
    return typeFactoryS().upper();
  }

  public BoundsMap<TypeS> bmS(
      VarS var1, Side<TypeS> side1, TypeS bound1,
      VarS var2, Side<TypeS> side2, TypeS bound2) {
    Bounds<TypeS> bounds1 = oneSideBoundS(side1, bound1);
    Bounds<TypeS> bounds2 = oneSideBoundS(side2, bound2);
    if (var1.equals(var2)) {
      return boundsMap(new Bounded<>(var1, typingS().merge(bounds1, bounds2)));
    } else {
      return new BoundsMap<>(ImmutableMap.of(
          var1, new Bounded<>(var1, bounds1),
          var2, new Bounded<>(var2, bounds2)
      ));
    }
  }

  public BoundsMap<TypeS> bmS(VarS var, Side<TypeS> side, TypeS bound) {
    return boundsMap(new Bounded<>(var, oneSideBoundS(side, bound)));
  }

  public BoundsMap<TypeS> bmS() {
    return boundsMap();
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

  public CombineS combineS(int line, StructTS type, ExprS... expr) {
    return combineS(line, type, ImmutableList.copyOf(expr));
  }

  public CombineS combineS(int line, StructTS type, ImmutableList<ExprS> exprs) {
    return new CombineS(type, exprs, loc(line));
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

  public NatFuncS natFuncS(TypeS type, String name, ItemS... params) {
    return natFuncS(1, type, name, annS(1, stringS(1, "Impl.met")), params);
  }

  public NatFuncS natFuncS(int line, TypeS type, String name, AnnS annS, ItemS... params) {
    return natFuncS(line, funcTS(type, params), modPath(), name, nList(params), annS);
  }
  public NatFuncS natFuncS(FuncTS type, String name, NList<ItemS> params) {
    return natFuncS(type, name, params, annS());
  }

  public NatFuncS natFuncS(FuncTS type, String name, NList<ItemS> params, AnnS ann) {
    return natFuncS(1, type, name, params, ann);
  }

  public NatFuncS natFuncS(int line, FuncTS type, String name, NList<ItemS> params, AnnS ann) {
    return natFuncS(line, type, modPath(), name, params, ann);
  }

  public NatFuncS natFuncS(int line, FuncTS type, ModPath modPath, String name, NList<ItemS> params,
      AnnS ann) {
    return new NatFuncS(type, modPath, name, params, ann, loc(line));
  }

  public DefFuncS defFuncS(TypeS type, String name, ExprS body, ItemS... params) {
    return defFuncS(1, type, name, body, params);
  }

  public DefFuncS defFuncS(int line, TypeS type, String name, ExprS body, ItemS... params) {
    return new DefFuncS(funcTS(type, params), modPath(), name, nList(params), body, loc(line));
  }

  public DefFuncS defFuncS(String name, NList<ItemS> params, ExprS expr) {
    return new DefFuncS(funcTS(expr.type(), toTypes(params)), modPath(), name, params, expr, loc(1));
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
