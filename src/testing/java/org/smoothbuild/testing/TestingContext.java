package org.smoothbuild.testing;

import static java.util.Optional.empty;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.lang.base.define.Item.toTypes;
import static org.smoothbuild.lang.base.define.TestingLocation.loc;
import static org.smoothbuild.lang.base.define.TestingModulePath.modulePath;
import static org.smoothbuild.lang.base.type.api.BoundsMap.boundsMap;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import java.math.BigInteger;
import java.util.Optional;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.object.db.ObjFactory;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.expr.CallH;
import org.smoothbuild.db.object.obj.expr.ConstructH;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.RefH;
import org.smoothbuild.db.object.obj.expr.SelectH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BlobHBuilder;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.DefinedFunctionH;
import org.smoothbuild.db.object.obj.val.IfFunctionH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.MapFunctionH;
import org.smoothbuild.db.object.obj.val.NativeFunctionH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.type.TypeFactoryH;
import org.smoothbuild.db.object.type.TypeHDb;
import org.smoothbuild.db.object.type.TypingH;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.db.object.type.expr.CallTypeH;
import org.smoothbuild.db.object.type.expr.ConstructTypeH;
import org.smoothbuild.db.object.type.expr.OrderTypeH;
import org.smoothbuild.db.object.type.expr.RefTypeH;
import org.smoothbuild.db.object.type.expr.SelectTypeH;
import org.smoothbuild.db.object.type.val.AnyTypeH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.db.object.type.val.BlobTypeH;
import org.smoothbuild.db.object.type.val.BoolTypeH;
import org.smoothbuild.db.object.type.val.DefinedFunctionTypeH;
import org.smoothbuild.db.object.type.val.FunctionTypeH;
import org.smoothbuild.db.object.type.val.IfFunctionTypeH;
import org.smoothbuild.db.object.type.val.IntTypeH;
import org.smoothbuild.db.object.type.val.MapFunctionTypeH;
import org.smoothbuild.db.object.type.val.NativeFunctionTypeH;
import org.smoothbuild.db.object.type.val.NothingTypeH;
import org.smoothbuild.db.object.type.val.StringTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;
import org.smoothbuild.db.object.type.val.VariableH;
import org.smoothbuild.exec.compute.ComputationCache;
import org.smoothbuild.exec.compute.Computer;
import org.smoothbuild.exec.compute.Container;
import org.smoothbuild.install.TempManager;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.SynchronizedFileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.lang.base.define.ConstructorS;
import org.smoothbuild.lang.base.define.DefinedFunctionS;
import org.smoothbuild.lang.base.define.DefinedValueS;
import org.smoothbuild.lang.base.define.InternalModuleLoader;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.ItemSignature;
import org.smoothbuild.lang.base.define.ModuleS;
import org.smoothbuild.lang.base.define.NativeFunctionS;
import org.smoothbuild.lang.base.define.TopEvaluableS;
import org.smoothbuild.lang.base.type.api.Bounded;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.BoundsMap;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.impl.AnyTypeS;
import org.smoothbuild.lang.base.type.impl.ArrayTypeS;
import org.smoothbuild.lang.base.type.impl.BlobTypeS;
import org.smoothbuild.lang.base.type.impl.BoolTypeS;
import org.smoothbuild.lang.base.type.impl.FunctionTypeS;
import org.smoothbuild.lang.base.type.impl.IntTypeS;
import org.smoothbuild.lang.base.type.impl.NothingTypeS;
import org.smoothbuild.lang.base.type.impl.StringTypeS;
import org.smoothbuild.lang.base.type.impl.StructTypeS;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.base.type.impl.TypingS;
import org.smoothbuild.lang.base.type.impl.VariableS;
import org.smoothbuild.lang.expr.Annotation;
import org.smoothbuild.lang.expr.BlobS;
import org.smoothbuild.lang.expr.CallS;
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
  private ObjectHDb objectHDb;
  private TypingS typingS;
  private TypingH typingH;
  private TypeHDb typeHDb;
  private HashedDb hashedDb;
  private FileSystem hashedDbFileSystem;
  private FileSystem fullFileSystem;
  private TempManager tempManager;
  private ModuleS internalModule;
  private TypeFactoryS typeFactoryS;

  public NativeApi nativeApi() {
    return container();
  }

  public TestingModuleLoader module(String sourceCode) {
    return new TestingModuleLoader(this, sourceCode);
  }

  public ModuleS internalModule() {
    if (internalModule == null) {
      internalModule = new InternalModuleLoader(typeFactoryS()).loadModule();
    }
    return internalModule;
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

  public ObjFactory objFactory() {
    if (objFactory == null) {
      objFactory = new ObjFactory(objectHDb(), typeHDb(), typingH());
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
      typingH = new TypingH(typeHDb());
    }
    return typingH;
  }

  public TypeFactoryH typeFactoryH() {
    return typeHDb();
  }

  public TypeFactoryS typeFactoryS() {
    if (typeFactoryS == null) {
      typeFactoryS = new TypeFactoryS();
    }
    return typeFactoryS;
  }

  public TypeHDb typeHDb() {
    if (typeHDb == null) {
      typeHDb = new TypeHDb(hashedDb());
    }
    return typeHDb;
  }

  public ObjectHDb objectHDb() {
    if (objectHDb == null) {
      objectHDb = new ObjectHDb(hashedDb(), typeHDb(), typingH());
    }
    return objectHDb;
  }

  public ComputationCache computationCache() {
    if (computationCache == null) {
      computationCache = new ComputationCache(
          computationCacheFileSystem(), objectHDb(), objFactory());
    }
    return computationCache;
  }

  public FileSystem computationCacheFileSystem() {
    if (computationCacheFileSystem == null) {
      computationCacheFileSystem = new MemoryFileSystem();
    }
    return computationCacheFileSystem;
  }

  public ObjectHDb objectHDbOther() {
    return new ObjectHDb(hashedDb(), typeHDbOther(), typingH());
  }

  public TypeHDb typeHDbOther() {
    return new TypeHDb(hashedDb());
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
    return typeHDb().tuple(list(stringHT(), intHT()));
  }

  public AnyTypeH anyHT() {
    return typeHDb().any();
  }

  public ArrayTypeH arrayHT(TypeHV elemSpec) {
    return typeHDb().array(elemSpec);
  }

  public BlobTypeH blobHT() {
    return typeHDb().blob();
  }

  public BoolTypeH boolHT() {
    return typeHDb().bool();
  }

  public DefinedFunctionTypeH definedFunctionHT() {
    return definedFunctionHT(intHT(), list(blobHT(), stringHT()));
  }

  public DefinedFunctionTypeH definedFunctionHT(TypeHV result, ImmutableList<TypeHV> params) {
    return typeHDb().definedFunction(result, params);
  }

  public TupleTypeH fileHT() {
    return tupleHT(list(blobHT(), stringHT()));
  }

  public FunctionTypeH functionHT() {
    return functionHT(intHT(), list(blobHT(), stringHT()));
  }

  public FunctionTypeH functionHT(TypeHV result, ImmutableList<TypeHV> params) {
    return typeHDb().function(result, params);
  }

  public IfFunctionTypeH ifFunctionHT() {
    return typeHDb().ifFunction();
  }

  public IntTypeH intHT() {
    return typeHDb().int_();
  }

  public MapFunctionTypeH mapFunctionHT() {
    return typeHDb().mapFunction();
  }

  public NativeFunctionTypeH nativeFunctionHT() {
    return typeHDb().nativeFunction(blobHT(), list(boolHT()));
  }

  public NativeFunctionTypeH nativeFunctionHT(TypeHV result, ImmutableList<TypeHV> params) {
    return typeHDb().nativeFunction(result, params);
  }

  public NothingTypeH nothingHT() {
    return typeHDb().nothing();
  }

  public TupleTypeH personHT() {
    return tupleHT(list(stringHT(), stringHT()));
  }

  public StringTypeH stringHT() {
    return typeHDb().string();
  }

  public TupleTypeH tupleHT() {
    return typeHDb().tuple(list(intHT()));
  }

  public TupleTypeH tupleHT(ImmutableList<TypeHV> itemSpecs) {
    return typeHDb().tuple(itemSpecs);
  }

  public TupleTypeH tupleEmptyHT() {
    return tupleHT(list());
  }

  public TupleTypeH tupleWithStrHT() {
    return tupleHT(list(stringHT()));
  }

  public VariableH variableHT(String name) {
    return typeHDb().variable(name);
  }

  public Side<TypeHV> lowerHT() {
    return typeFactoryH().lower();
  }

  public Side<TypeHV> upperHT() {
    return typeFactoryH().upper();
  }

  // Expr types

  public CallTypeH callHT() {
    return callHT(intHT());
  }

  public CallTypeH callHT(TypeHV evaluationType) {
    return typeHDb().call(evaluationType);
  }

  public ConstructTypeH constructHT() {
    return constructHT(list(intHT(), stringHT()));
  }

  public ConstructTypeH constructHT(ImmutableList<TypeHV> itemSpecs) {
    return typeHDb().construct(tupleHT(itemSpecs));
  }

  public OrderTypeH orderHT() {
    return orderHT(intHT());
  }

  public OrderTypeH orderHT(TypeHV elemSpec) {
    return typeHDb().order(elemSpec);
  }

  public RefTypeH refHT() {
    return refHT(intHT());
  }

  public RefTypeH refHT(TypeHV evaluationType) {
    return typeHDb().ref(evaluationType);
  }

  public SelectTypeH selectHT() {
    return selectHT(intHT());
  }

  public SelectTypeH selectHT(TypeHV evaluationType) {
    return typeHDb().select(evaluationType);
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

  public ArrayH arrayH(ValueH... elems) {
    return arrayH(elems[0].type(), elems);
  }

  public ArrayH arrayH(TypeHV elemSpec, ValueH... elems) {
    return objectHDb().arrayBuilder(elemSpec).addAll(list(elems)).build();
  }

  public BlobH blobH() {
    return objFactory().blob(sink -> sink.writeUtf8("blob data"));
  }

  public BlobH blobH(ByteString bytes) {
    return objFactory().blob(sink -> sink.write(bytes));
  }

  public BlobHBuilder blobHBuilder() {
    return objectHDb().blobBuilder();
  }

  public BoolH boolH(boolean value) {
    return objectHDb().bool(value);
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

  public DefinedFunctionH definedFunctionH() {
    return definedFunctionH(intH());
  }

  public DefinedFunctionH definedFunctionH(ObjectH body) {
    var type = definedFunctionHT(body.evaluationType(), list(stringHT()));
    return definedFunctionH(type, body);
  }

  public DefinedFunctionH definedFunctionH(DefinedFunctionTypeH spec, ObjectH body) {
    return objectHDb().definedFunction(spec, body);
  }

  public IfFunctionH ifFunctionH() {
    return objectHDb().ifFunction();
  }

  public IntH intH() {
    return intH(17);
  }

  public IntH intH(int value) {
    return objectHDb().int_(BigInteger.valueOf(value));
  }

  public MapFunctionH mapFunctionH() {
    return objectHDb().mapFunction();
  }

  public NativeFunctionH nativeFunctionH(BlobH jarFile, StringH classBinaryName) {
    return objectHDb().nativeFunction(nativeFunctionHT(), jarFile, classBinaryName, boolH(true));
  }

  public TupleH personH(String firstName, String lastName) {
    return tupleH(list(stringH(firstName), stringH(lastName)));
  }

  public StringH stringH() {
    return objectHDb().string("abc");
  }

  public StringH stringH(String string) {
    return objectHDb().string(string);
  }

  public TupleH tupleH(ImmutableList<ValueH> items) {
    var spec = tupleHT(map(items, ValueH::type));
    return tupleH(spec, items);
  }

  public TupleH tupleH(TupleTypeH tupleType, ImmutableList<ValueH> items) {
    return objectHDb().tuple(tupleType, items);
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

  public CallH callH(ObjectH function, ImmutableList<ObjectH> arguments) {
    return objectHDb().call(function, constructH(arguments));
  }

  public ConstructH constructH(ImmutableList<ObjectH> items) {
    return objectHDb().construct(items);
  }

  public OrderH orderH(ImmutableList<ObjectH> elems) {
    return objectHDb().order(elems);
  }

  public RefH refH(int value) {
    return objectHDb().ref(BigInteger.valueOf(value), intHT());
  }

  public RefH refH(TypeHV evaluationType, int pointer) {
    return objectHDb().ref(BigInteger.valueOf(pointer), evaluationType);
  }

  public SelectH selectH(ObjectH tuple, IntH index) {
    return objectHDb().select(tuple, index);
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

  public FunctionTypeS functionST(TypeS resultType, Item... params) {
    return functionST(resultType, toTypes(list(params)));
  }

  public FunctionTypeS functionST(TypeS resultType, ImmutableList<TypeS> types) {
    return typeFactoryS().function(resultType, types);
  }

  public IntTypeS intST() {
    return typeFactoryS().int_();
  }

  public NothingTypeS nothingST() {
    return typeFactoryS().nothing();
  }

  public StructTypeS personST() {
    return typeFactoryS().struct("Person",
        nList(isig("firstName", stringST()), isig("lastName", stringST())));
  }

  public StringTypeS stringST() {
    return typeFactoryS().string();
  }

  public StructTypeS structST(String name, NList<ItemSignature> fields) {
    return typeFactoryS().struct(name, fields);
  }

  public VariableS variableST(String name) {
    return typeFactoryS().variable(name);
  }

  public Side<TypeS> lowerST() {
    return typeFactoryS().lower();
  }

  public Side<TypeS> upperST() {
    return typeFactoryS().upper();
  }

  public BoundsMap<TypeS> bmST(
      VariableS var1, Side<TypeS> side1, TypeS bound1,
      VariableS var2, Side<TypeS> side2, TypeS bound2) {
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

  public BoundsMap<TypeS> bmST(VariableS var, Side<TypeS> side, TypeS bound) {
    return boundsMap(new Bounded<>(var, oneSideBoundST(side, bound)));
  }

  public BoundsMap<TypeS> bmST() {
    return boundsMap();
  }

  public Bounds<TypeS> oneSideBoundST(Side<TypeS> side, TypeS type) {
    return typeFactoryS().oneSideBound(side, type);
  }

  // Expressions

  public OrderS orderS(int line, TypeS elemType, ExprS... expr) {
    return new OrderS(arrayST(elemType), ImmutableList.copyOf(expr), loc(line));
  }

  public BlobS blobS(int data) {
    return blobS(1, data);
  }

  public BlobS blobS(int line, int data) {
    return new BlobS(blobST(), ByteString.of((byte) data), loc(line));
  }

  public CallS callS(TypeS type, ExprS expr, ExprS... arguments) {
    return callS(1, type, expr, arguments);
  }

  public CallS callS(int line, TypeS type, ExprS expr, ExprS... arguments) {
    return new CallS(type, expr, list(arguments), loc(line));
  }

  public ConstructorS constructorS(TypeS resultType, String name, Item... params) {
    return constructorS(1, resultType, name, params);
  }

  public ConstructorS constructorS(int line, TypeS resultType, String name, Item... params) {
    return new ConstructorS(functionST(resultType, params), modulePath(), name,
        nList(params), loc(line));
  }

  public NativeFunctionS functionS(TypeS type, String name, Item... params) {
    return functionS(1, type, name, annotation(1, stringS(1, "Impl.met")), params);
  }

  public NativeFunctionS functionS(int line, TypeS type, String name, Annotation annotation,
      Item... params) {
    return new NativeFunctionS(functionST(type, params), modulePath(), name,
        nList(params), annotation, loc(line)
    );
  }

  public DefinedFunctionS functionS(TypeS type, String name, ExprS body, Item... params) {
    return functionS(1, type, name, body, params);
  }

  public DefinedFunctionS functionS(
      int line, TypeS type, String name, ExprS body, Item... params) {
    return new DefinedFunctionS(functionST(type, params), modulePath(), name,
        nList(params), body, loc(line)
    );
  }

  public IntS intS(int value) {
    return intS(1, value);
  }

  public IntS intS(int line, int value) {
    return new IntS(intST(), BigInteger.valueOf(value), loc(line));
  }

  public ParamRefS paramRefS(TypeS type, String name) {
    return paramRefS(1, type, name);
  }

  public ParamRefS paramRefS(int line, TypeS type, String name) {
    return new ParamRefS(type, name, loc(line));
  }

  public RefS refS(TopEvaluableS referencable) {
    return refS(1, referencable.type(), referencable.name());
  }

  public RefS refS(int line, TypeS type, String name) {
    return new RefS(type, name, loc(line));
  }

  public SelectS selectS(int line, TypeS field, int index, ExprS expr) {
    return new SelectS(field, expr, index, loc(line));
  }

  public StringS stringS() {
    return stringS(1, "abc");
  }

  public StringS stringS(int line, String data) {
    return new StringS(stringST(), data, loc(line));
  }

  // other smooth language thingies

  public Annotation annotation(int line, StringS implementedBy) {
    return annotation(line, implementedBy, true);
  }

  public Annotation annotation(int line, StringS implementedBy, boolean pure) {
    return new Annotation(implementedBy, pure, loc(line));
  }

  public Item field(TypeS type, String name) {
    return new Item(type, modulePath(), name, empty(), loc(1));
  }

  public Item param(TypeS type, String name) {
    return param(1, type, name);
  }

  public Item param(int line, TypeS type, String name) {
    return param(line, type, name, empty());
  }

  public Item param(int line, TypeS type, String name, ExprS defaultArg) {
    return param(line, type, name, Optional.of(defaultArg));
  }

  private Item param(int line, TypeS type, String name, Optional<ExprS> defaultArg) {
    return new Item(type, modulePath(), name, defaultArg, loc(line));
  }

  public DefinedValueS value(int line, TypeS type, String name, ExprS expr) {
    return new DefinedValueS(type, modulePath(), name, expr, loc(line));
  }

  public DefinedFunctionS defFuncS(int line, FunctionTypeS type, String name, NList<Item> params,
      ExprS expr) {
    return new DefinedFunctionS(type, modulePath(), name, params, expr, loc(line));
  }

  public NativeFunctionS natFuncS(int line, FunctionTypeS type, String name, NList<Item> params,
      Annotation ann) {
    return new NativeFunctionS(type, modulePath(), name, params, ann, loc(line));
  }

  public ItemSignature isig(String name, TypeS type) {
    return new ItemSignature(type, name, empty());
  }
}
