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
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.ParamRefH;
import org.smoothbuild.db.object.obj.expr.SelectH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BlobHBuilder;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.DefFuncH;
import org.smoothbuild.db.object.obj.val.IfFuncH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.MapFuncH;
import org.smoothbuild.db.object.obj.val.NatFuncH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.db.object.type.TypeDb;
import org.smoothbuild.db.object.type.TypeFactoryH;
import org.smoothbuild.db.object.type.TypingH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.expr.CallTypeH;
import org.smoothbuild.db.object.type.expr.CombineTypeH;
import org.smoothbuild.db.object.type.expr.OrderTypeH;
import org.smoothbuild.db.object.type.expr.ParamRefTypeH;
import org.smoothbuild.db.object.type.expr.SelectTypeH;
import org.smoothbuild.db.object.type.val.AnyTypeH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.db.object.type.val.BlobTypeH;
import org.smoothbuild.db.object.type.val.BoolTypeH;
import org.smoothbuild.db.object.type.val.DefFuncTypeH;
import org.smoothbuild.db.object.type.val.FuncTypeH;
import org.smoothbuild.db.object.type.val.IfFuncTypeH;
import org.smoothbuild.db.object.type.val.IntTypeH;
import org.smoothbuild.db.object.type.val.MapFuncTypeH;
import org.smoothbuild.db.object.type.val.NatFuncTypeH;
import org.smoothbuild.db.object.type.val.NothingTypeH;
import org.smoothbuild.db.object.type.val.StringTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;
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
import org.smoothbuild.lang.base.define.MapFuncS;
import org.smoothbuild.lang.base.define.ModS;
import org.smoothbuild.lang.base.define.NatFuncS;
import org.smoothbuild.lang.base.define.TopEvalS;
import org.smoothbuild.lang.base.type.api.Bounded;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.BoundsMap;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.impl.AnyTypeS;
import org.smoothbuild.lang.base.type.impl.ArrayTypeS;
import org.smoothbuild.lang.base.type.impl.BlobTypeS;
import org.smoothbuild.lang.base.type.impl.BoolTypeS;
import org.smoothbuild.lang.base.type.impl.FuncTypeS;
import org.smoothbuild.lang.base.type.impl.IntTypeS;
import org.smoothbuild.lang.base.type.impl.NothingTypeS;
import org.smoothbuild.lang.base.type.impl.StringTypeS;
import org.smoothbuild.lang.base.type.impl.StructTypeS;
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
import org.smoothbuild.lang.expr.RefS;
import org.smoothbuild.lang.expr.SelectS;
import org.smoothbuild.lang.expr.StringS;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;
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
  private TypeDb typeDb;
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
      objFactory = new ObjFactory(objDb(), typeDb(), typingH());
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
      typingH = new TypingH(typeDb());
    }
    return typingH;
  }

  public TypeFactoryH typeFactoryH() {
    return typeDb();
  }

  public TypeFactoryS typeFactoryS() {
    if (typeFactoryS == null) {
      typeFactoryS = new TypeFactoryS();
    }
    return typeFactoryS;
  }

  public TypeDb typeDb() {
    if (typeDb == null) {
      typeDb = new TypeDb(hashedDb());
    }
    return typeDb;
  }

  public ObjDb objDb() {
    if (objDb == null) {
      objDb = new ObjDb(hashedDb(), typeDb(), typingH());
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
    return new ObjDb(hashedDb(), typeDbOther(), typingH());
  }

  public TypeDb typeDbOther() {
    return new TypeDb(hashedDb());
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

  public TupleTypeH animalHT() {
    return typeDb().tuple(list(stringHT(), intHT()));
  }

  public AnyTypeH anyHT() {
    return typeDb().any();
  }

  public ArrayTypeH arrayHT() {
    return arrayHT(stringHT());
  }

  public ArrayTypeH arrayHT(TypeH elemSpec) {
    return typeDb().array(elemSpec);
  }

  public BlobTypeH blobHT() {
    return typeDb().blob();
  }

  public BoolTypeH boolHT() {
    return typeDb().bool();
  }

  public DefFuncTypeH defFuncHT() {
    return defFuncHT(intHT(), list(blobHT(), stringHT()));
  }

  public DefFuncTypeH defFuncHT(TypeH result, ImmutableList<TypeH> params) {
    return typeDb().defFunc(result, params);
  }

  public TupleTypeH fileHT() {
    return tupleHT(list(blobHT(), stringHT()));
  }

  public FuncTypeH abstFuncHT() {
    return abstFuncHT(intHT(), list(blobHT(), stringHT()));
  }

  public FuncTypeH abstFuncHT(TypeH result, ImmutableList<TypeH> params) {
    return typeDb().func(result, params);
  }

  public IfFuncTypeH ifFuncHT() {
    return typeDb().ifFunc();
  }

  public IntTypeH intHT() {
    return typeDb().int_();
  }

  public MapFuncTypeH mapFuncHT() {
    return typeDb().mapFunc();
  }

  public NatFuncTypeH natFuncHT() {
    return typeDb().natFunc(blobHT(), list(boolHT()));
  }

  public NatFuncTypeH natFuncHT(TypeH result, ImmutableList<TypeH> params) {
    return typeDb().natFunc(result, params);
  }

  public NothingTypeH nothingHT() {
    return typeDb().nothing();
  }

  public TupleTypeH personHT() {
    return tupleHT(list(stringHT(), stringHT()));
  }

  public StringTypeH stringHT() {
    return typeDb().string();
  }

  public TupleTypeH tupleHT() {
    return typeDb().tuple(list(intHT()));
  }

  public TupleTypeH tupleHT(ImmutableList<TypeH> itemSpecs) {
    return typeDb().tuple(itemSpecs);
  }

  public TupleTypeH tupleEmptyHT() {
    return tupleHT(list());
  }

  public TupleTypeH tupleWithStrHT() {
    return tupleHT(list(stringHT()));
  }

  public VarH varHT(String name) {
    return typeDb().var(name);
  }

  public Side<TypeH> lowerHT() {
    return typeFactoryH().lower();
  }

  public Side<TypeH> upperHT() {
    return typeFactoryH().upper();
  }

  // Expr types

  public CallTypeH callHT() {
    return callHT(intHT());
  }

  public CallTypeH callHT(TypeH evaluationType) {
    return typeDb().call(evaluationType);
  }

  public CombineTypeH combineHT() {
    return combineHT(list(intHT(), stringHT()));
  }

  public CombineTypeH combineHT(ImmutableList<TypeH> itemSpecs) {
    return typeDb().combine(tupleHT(itemSpecs));
  }

  public OrderTypeH orderHT() {
    return orderHT(intHT());
  }

  public OrderTypeH orderHT(TypeH elemSpec) {
    return typeDb().order(elemSpec);
  }

  public ParamRefTypeH refHT() {
    return refHT(intHT());
  }

  public ParamRefTypeH refHT(TypeH evaluationType) {
    return typeDb().ref(evaluationType);
  }

  public SelectTypeH selectHT() {
    return selectHT(intHT());
  }

  public SelectTypeH selectHT(TypeH evaluationType) {
    return typeDb().select(evaluationType);
  }

  // Obj-s (values)

  public TupleH animalH() {
    return animalH("rabbit", 7);
  }

  public TupleH animalH(String species, int speed) {
    return animalH(stringH(species), intH(speed));
  }

  public TupleH animalH(StringH species, IntH speed) {
    return tupleH(animalHT(), list(species, speed));
  }

  public ArrayH arrayH(ValH... elems) {
    return arrayH(elems[0].spec(), elems);
  }

  public ArrayH arrayH(TypeH elemSpec, ValH... elems) {
    return objDb()
        .arrayBuilder(arrayHT(elemSpec))
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

  public DefFuncH defFuncH() {
    return defFuncH(intH());
  }

  public DefFuncH defFuncH(ObjH body) {
    return defFuncH(list(), body);
  }

  public DefFuncH defFuncH(ImmutableList<TypeH> paramTypes, ObjH body) {
    var type = defFuncHT(body.type(), paramTypes);
    return defFuncH(type, body);
  }

  public DefFuncH defFuncH(DefFuncTypeH spec, ObjH body) {
    return objDb().defFunc(spec, body);
  }

  public IfFuncH ifFuncH() {
    return objDb().ifFunc();
  }

  public IntH intH() {
    return intH(17);
  }

  public IntH intH(int value) {
    return objDb().int_(BigInteger.valueOf(value));
  }

  public MapFuncH mapFuncH() {
    return objDb().mapFunc();
  }

  public NatFuncH natFuncH(BlobH jarFile, StringH classBinaryName) {
    return natFuncH(natFuncHT(), jarFile, classBinaryName);
  }

  public NatFuncH natFuncH(NatFuncTypeH type, BlobH jarFile, StringH classBinaryName) {
    return objDb().natFunc(type, jarFile, classBinaryName, boolH(true));
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
    var spec = tupleHT(map(items, ValH::spec));
    return tupleH(spec, items);
  }

  public TupleH tupleH(TupleTypeH tupleType, ImmutableList<ValH> items) {
    return objDb().tuple(tupleType, items);
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
    return arrayH(objFactory().messageType());
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

  public OrderH orderH(ImmutableList<ObjH> elems) {
    return objDb().order(elems);
  }

  public ParamRefH paramRefH(int value) {
    return objDb().newParamRef(BigInteger.valueOf(value), intHT());
  }

  public ParamRefH paramRefH(TypeH evaluationType, int pointer) {
    return objDb().newParamRef(BigInteger.valueOf(pointer), evaluationType);
  }

  public SelectH selectH(ObjH tuple, IntH index) {
    return objDb().select(tuple, index);
  }

  // Types Smooth

  public AnyTypeS anyST() {
    return typeFactoryS().any();
  }

  public ArrayTypeS arrayST(TypeS elemType) {
    return typeFactoryS().array(elemType);
  }

  public BlobTypeS blobST() {
    return typeFactoryS().blob();
  }

  public BoolTypeS boolST() {
    return typeFactoryS().bool();
  }

  public FuncTypeS funcST(TypeS resultType, ItemS... params) {
    return funcST(resultType, toTypes(list(params)));
  }

  public FuncTypeS funcST(TypeS resultType, ImmutableList<TypeS> types) {
    return typeFactoryS().func(resultType, types);
  }

  public IntTypeS intST() {
    return typeFactoryS().int_();
  }

  public NothingTypeS nothingST() {
    return typeFactoryS().nothing();
  }

  public StructTypeS personST() {
    return typeFactoryS().struct("Person",
        nList(sigS("firstName", stringST()), sigS("lastName", stringST())));
  }

  public StringTypeS stringST() {
    return typeFactoryS().string();
  }

  public StructTypeS structST(String name, NList<ItemSigS> fields) {
    return typeFactoryS().struct(name, fields);
  }

  public VarS varST(String name) {
    return typeFactoryS().var(name);
  }

  public Side<TypeS> lowerST() {
    return typeFactoryS().lower();
  }

  public Side<TypeS> upperST() {
    return typeFactoryS().upper();
  }

  public BoundsMap<TypeS> bmST(
      VarS var1, Side<TypeS> side1, TypeS bound1,
      VarS var2, Side<TypeS> side2, TypeS bound2) {
    Bounds<TypeS> bounds1 = oneSideBoundST(side1, bound1);
    Bounds<TypeS> bounds2 = oneSideBoundST(side2, bound2);
    if (var1.equals(var2)) {
      return boundsMap(new Bounded<>(var1, typingS().merge(bounds1, bounds2)));
    } else {
      return new BoundsMap<>(ImmutableMap.of(
          var1, new Bounded<>(var1, bounds1),
          var2, new Bounded<>(var2, bounds2)
      ));
    }
  }

  public BoundsMap<TypeS> bmST(VarS var, Side<TypeS> side, TypeS bound) {
    return boundsMap(new Bounded<>(var, oneSideBoundST(side, bound)));
  }

  public BoundsMap<TypeS> bmST() {
    return boundsMap();
  }

  public Bounds<TypeS> oneSideBoundST(Side<TypeS> side, TypeS type) {
    return typeFactoryS().oneSideBound(side, type);
  }

  // Expressions

  public BlobS blobS(int data) {
    return blobS(1, data);
  }

  public BlobS blobS(int line, int data) {
    return new BlobS(blobST(), ByteString.of((byte) data), loc(line));
  }

  public CallS callS(TypeS type, ExprS expr, ExprS... args) {
    return callS(1, type, expr, args);
  }

  public CallS callS(int line, TypeS type, ExprS expr, ExprS... args) {
    return new CallS(type, expr, list(args), loc(line));
  }

  public CombineS combineS(ExprS... expr) {
    var exprs = ImmutableList.copyOf(expr);
    return combineS(1, structST("MyStruct", nList(exprsToItemSigs(exprs))), exprs);
  }

  private ImmutableList<ItemSigS> exprsToItemSigs(ImmutableList<ExprS> exprs) {
    return IntStream.range(0, exprs.size())
        .mapToObj(i -> new ItemSigS(exprs.get(i).type(), "field" + i, empty()))
        .collect(toImmutableList());
  }

  public CombineS combineS(int line, StructTypeS type, ExprS... expr) {
    return combineS(line, type, ImmutableList.copyOf(expr));
  }

  public CombineS combineS(int line, StructTypeS type, ImmutableList<ExprS> exprs) {
    return new CombineS(type, exprs, loc(line));
  }

  public NatFuncS funcS(TypeS type, String name, ItemS... params) {
    return funcS(1, type, name, annS(1, stringS(1, "Impl.met")), params);
  }

  public NatFuncS funcS(int line, TypeS type, String name, AnnS annS,
      ItemS... params) {
    return new NatFuncS(funcST(type, params), modPath(), name,
        nList(params), annS, loc(line)
    );
  }

  public DefFuncS funcS(TypeS type, String name, ExprS body, ItemS... params) {
    return funcS(1, type, name, body, params);
  }

  public DefFuncS funcS(
      int line, TypeS type, String name, ExprS body, ItemS... params) {
    return new DefFuncS(funcST(type, params), modPath(), name,
        nList(params), body, loc(line)
    );
  }

  public IntS intS(int value) {
    return intS(1, value);
  }

  public IntS intS(int line, int value) {
    return new IntS(intST(), BigInteger.valueOf(value), loc(line));
  }

  public OrderS orderS(TypeS elemType, ExprS... exprs) {
    return orderS(1, elemType, exprs);
  }

  public OrderS orderS(int line, TypeS elemType, ExprS... exprs) {
    return new OrderS(arrayST(elemType), ImmutableList.copyOf(exprs), loc(line));
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

  public RefS refS(TopEvalS topEval) {
    return refS(1, topEval.type(), topEval.name());
  }

  public RefS refS(int line, TypeS type, String name) {
    return new RefS(type, name, loc(line));
  }

  public SelectS selectS(TypeS type, String field, ExprS expr) {
    return selectS(1, type, field, expr);
  }

  public SelectS selectS(int line, TypeS type, String field, ExprS expr) {
    return new SelectS(type, expr, field, loc(line));
  }

  public StringS stringS() {
    return stringS("abc");
  }

  public StringS stringS(String string) {
    return stringS(1, string);
  }

  public StringS stringS(int line, String data) {
    return new StringS(stringST(), data, loc(line));
  }

  // other smooth language thingies

  public AnnS annS() {
    return annS(1, stringS("implementation.Class"));
  }

  public AnnS annS(int line, StringS implementedBy) {
    return annS(line, implementedBy, true);
  }

  public AnnS annS(int line, StringS implementedBy, boolean pure) {
    return new AnnS(implementedBy, pure, loc(line));
  }

  public ItemS field(TypeS type, String name) {
    return new ItemS(type, modPath(), name, empty(), loc(1));
  }

  public ItemS param(TypeS type, String name) {
    return param(1, type, name);
  }

  public ItemS param(int line, TypeS type, String name) {
    return param(line, type, name, empty());
  }

  public ItemS param(int line, TypeS type, String name, ExprS defaultArg) {
    return param(line, type, name, Optional.of(defaultArg));
  }

  private ItemS param(int line, TypeS type, String name, Optional<ExprS> defaultArg) {
    return new ItemS(type, modPath(), name, defaultArg, loc(line));
  }

  public DefValS defValS(String name, ExprS expr) {
    return defValS(1, expr.type(), name, expr);
  }

  public DefValS defValS(int line, TypeS type, String name, ExprS expr) {
    return new DefValS(type, modPath(), name, expr, loc(line));
  }

  public DefFuncS defFuncS(String name, NList<ItemS> params, ExprS expr) {
    return defFuncS(1, funcST(expr.type(), toTypes(params)), name, params, expr);
  }

  public DefFuncS defFuncS(int line, FuncTypeS type, String name, NList<ItemS> params, ExprS expr) {
    return new DefFuncS(type, modPath(), name, params, expr, loc(line));
  }

  public IfFuncS ifFuncS() {
    return new IfFuncS(modPath(), typeFactoryS());
  }

  public MapFuncS mapFuncS() {
    return new MapFuncS(modPath(), typeFactoryS());
  }

  public NatFuncS natFuncS(FuncTypeS type, String name, NList<ItemS> params) {
    return natFuncS(1, type, name, params, annS());
  }

  public NatFuncS natFuncS(int line, FuncTypeS type, String name, NList<ItemS> params, AnnS ann) {
    return new NatFuncS(type, modPath(), name, params, ann, loc(line));
  }

  public ItemSigS sigS(String name, TypeS type) {
    return new ItemSigS(type, name, empty());
  }
}
