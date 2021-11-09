package org.smoothbuild.testing;

import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.lang.base.define.Item.toTypes;
import static org.smoothbuild.lang.base.define.TestingLocation.loc;
import static org.smoothbuild.lang.base.define.TestingModulePath.modulePath;
import static org.smoothbuild.lang.base.type.api.BoundsMap.boundsMap;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;

import java.math.BigInteger;
import java.util.Optional;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.object.db.ObjFactory;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.expr.CallH;
import org.smoothbuild.db.object.obj.expr.ConstH;
import org.smoothbuild.db.object.obj.expr.ConstructH;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.RefH;
import org.smoothbuild.db.object.obj.expr.SelectH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BlobHBuilder;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.FunctionH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.NativeMethodH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.type.TypeFactoryH;
import org.smoothbuild.db.object.type.TypeHDb;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.db.object.type.expr.CallTypeH;
import org.smoothbuild.db.object.type.expr.ConstTypeH;
import org.smoothbuild.db.object.type.expr.ConstructTypeH;
import org.smoothbuild.db.object.type.expr.InvokeTypeH;
import org.smoothbuild.db.object.type.expr.OrderTypeH;
import org.smoothbuild.db.object.type.expr.RefTypeH;
import org.smoothbuild.db.object.type.expr.SelectTypeH;
import org.smoothbuild.db.object.type.val.AnyTypeH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.db.object.type.val.BlobTypeH;
import org.smoothbuild.db.object.type.val.BoolTypeH;
import org.smoothbuild.db.object.type.val.FunctionTypeH;
import org.smoothbuild.db.object.type.val.IntTypeH;
import org.smoothbuild.db.object.type.val.NativeMethodTypeH;
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
import org.smoothbuild.lang.base.define.Constructor;
import org.smoothbuild.lang.base.define.DefinedFunction;
import org.smoothbuild.lang.base.define.DefinedValue;
import org.smoothbuild.lang.base.define.GlobalReferencable;
import org.smoothbuild.lang.base.define.InternalModuleLoader;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.ItemSignature;
import org.smoothbuild.lang.base.define.NativeFunction;
import org.smoothbuild.lang.base.define.NativeValue;
import org.smoothbuild.lang.base.define.SModule;
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
import org.smoothbuild.util.collect.NamedList;

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
  private TypeHDb typeHDb;
  private HashedDb hashedDb;
  private FileSystem hashedDbFileSystem;
  private FileSystem fullFileSystem;
  private TempManager tempManager;
  private SModule internalModule;
  private TypeFactoryS typeFactoryS;

  public NativeApi nativeApi() {
    return container();
  }

  public TestingModuleLoader module(String sourceCode) {
    return new TestingModuleLoader(this, sourceCode);
  }

  public SModule internalModule() {
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
    return new Container(fullFileSystem(), objectFactory());
  }

  public ObjFactory objectFactory() {
    if (objFactory == null) {
      objFactory = new ObjFactory(objectDb(), objTypeDb());
    }
    return objFactory;
  }

  public TypingS typingS() {
    if (typingS == null) {
      typingS = new TypingS(typeFactoryS());
    }
    return typingS;
  }

  public TypeFactoryH typeFactoryO() {
    return objTypeDb();
  }

  public TypeFactoryS typeFactoryS() {
    if (typeFactoryS == null) {
      typeFactoryS = new TypeFactoryS();
    }
    return typeFactoryS;
  }

  public TypeHDb objTypeDb() {
    if (typeHDb == null) {
      typeHDb = new TypeHDb(hashedDb());
    }
    return typeHDb;
  }

  public ObjectHDb objectDb() {
    if (objectHDb == null) {
      objectHDb = new ObjectHDb(hashedDb(), objTypeDb());
    }
    return objectHDb;
  }

  public ComputationCache computationCache() {
    if (computationCache == null) {
      computationCache = new ComputationCache(
          computationCacheFileSystem(), objectDb(), objectFactory());
    }
    return computationCache;
  }

  public FileSystem computationCacheFileSystem() {
    if (computationCacheFileSystem == null) {
      computationCacheFileSystem = new MemoryFileSystem();
    }
    return computationCacheFileSystem;
  }

  public ObjectHDb objectDbOther() {
    return new ObjectHDb(hashedDb(), objTypeDbOther());
  }

  public TypeHDb objTypeDbOther() {
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

  // Obj types

  public TupleTypeH animalOT() {
    return typeFactoryO().tuple(list(stringOT(), intOT()));
  }

  public AnyTypeH anyOT() {
    return typeFactoryO().any();
  }

  public ArrayTypeH arrayOT(TypeHV elementSpec) {
    return typeFactoryO().array(elementSpec);
  }

  public BlobTypeH blobOT() {
    return typeFactoryO().blob();
  }

  public BoolTypeH boolOT() {
    return typeFactoryO().bool();
  }

  public TupleTypeH fileOT() {
    return tupleOT(list(blobOT(), stringOT()));
  }

  public IntTypeH intOT() {
    return typeFactoryO().int_();
  }

  public FunctionTypeH functionOT() {
    return functionOT(intOT(), list(blobOT(), stringOT()));
  }

  public FunctionTypeH functionOT(TypeHV result, ImmutableList<TypeHV> parameters) {
    return typeFactoryO().function(result, parameters);
  }

  public NativeMethodTypeH nativeMethodOT() {
    return objTypeDb().nativeMethod();
  }

  public NothingTypeH nothingOT() {
    return typeFactoryO().nothing();
  }

  public TupleTypeH personOT() {
    return tupleOT(list(stringOT(), stringOT()));
  }

  public StringTypeH stringOT() {
    return typeFactoryO().string();
  }

  public TupleTypeH tupleOT() {
    return objTypeDb().tuple(list(intOT()));
  }

  public TupleTypeH tupleOT(ImmutableList<TypeHV> itemSpecs) {
    return objTypeDb().tuple(itemSpecs);
  }

  public TupleTypeH tupleEmptyOT() {
    return tupleOT(list());
  }

  public TupleTypeH tupleWithStrOT() {
    return tupleOT(list(stringOT()));
  }

  public VariableH variableOT(String name) {
    return typeFactoryO().variable(name);
  }

  public Side<TypeHV> lowerOT() {
    return typeFactoryO().lower();
  }

  public Side<TypeHV> upperOT() {
    return typeFactoryO().upper();
  }

  // Expr types

  public CallTypeH callOT() {
    return callOT(intOT());
  }

  public CallTypeH callOT(TypeHV evaluationType) {
    return objTypeDb().call(evaluationType);
  }

  public ConstTypeH constOT() {
    return constOT(intOT());
  }

  public ConstTypeH constOT(TypeHV evaluationType) {
    return objTypeDb().const_(evaluationType);
  }

  public ConstructTypeH constructOT() {
    return constructOT(list(intOT(), stringOT()));
  }

  public ConstructTypeH constructOT(ImmutableList<TypeHV> itemSpecs) {
    return objTypeDb().construct(tupleOT(itemSpecs));
  }

  public InvokeTypeH invokeOT(TypeHV evaluationType) {
    return objTypeDb().invoke(evaluationType);
  }

  public OrderTypeH orderOT() {
    return orderOT(intOT());
  }

  public OrderTypeH orderOT(TypeHV elementSpec) {
    return objTypeDb().order(elementSpec);
  }

  public RefTypeH refOT() {
    return refOT(intOT());
  }

  public RefTypeH refOT(TypeHV evaluationType) {
    return objTypeDb().ref(evaluationType);
  }

  public SelectTypeH selectOT() {
    return selectOT(intOT());
  }

  public SelectTypeH selectOT(TypeHV evaluationType) {
    return objTypeDb().select(evaluationType);
  }

  // Obj-s (values)

  public TupleH animal() {
    return animal("rabbit", 7);
  }

  public TupleH animal(String species, int speed) {
    return animal(string(species), int_(speed));
  }

  public TupleH animal(StringH species, IntH speed) {
    return tuple(animalOT(), list(species, speed));
  }

  public ArrayH array(ValueH... elements) {
    return array(elements[0].type(), elements);
  }

  public ArrayH array(TypeHV elementSpec, ValueH... elements) {
    return objectDb().arrayBuilder(elementSpec).addAll(list(elements)).build();
  }

  public BlobH blob() {
    return objectFactory().blob(sink -> sink.writeUtf8("blob data"));
  }

  public BlobH blob(ByteString bytes) {
    return objectFactory().blob(sink -> sink.write(bytes));
  }

  public BlobHBuilder blobBuilder() {
    return objectDb().blobBuilder();
  }

  public BoolH bool(boolean value) {
    return objectDb().bool(value);
  }

  public TupleH file(Path path) {
    return file(path, ByteString.encodeString(path.toString(), CHARSET));
  }

  public TupleH file(Path path, ByteString content) {
    return file(path.toString(), blob(content));
  }

  public TupleH file(String path, BlobH blob) {
    StringH string = objectFactory().string(path);
    return objectFactory().file(string, blob);
  }

  public IntH int_() {
    return int_(17);
  }

  public IntH int_(int value) {
    return objectDb().int_(BigInteger.valueOf(value));
  }

  public FunctionH function() {
    return function(intExpr());
  }

  public FunctionH function(ExprH body) {
    FunctionTypeH spec = functionOT(body.evaluationType(), list(stringOT()));
    return function(spec, body);
  }

  public FunctionH function(FunctionTypeH spec, ExprH body) {
    return objectDb().function(spec, body);
  }

  public NativeMethodH nativeMethod(BlobH jarFile, StringH classBinaryName) {
    return objectDb().nativeMethod(jarFile, classBinaryName);
  }

  public TupleH person(String firstName, String lastName) {
    return tuple(list(string(firstName), string(lastName)));
  }

  public StringH string() {
    return objectDb().string("abc");
  }

  public StringH string(String string) {
    return objectDb().string(string);
  }

  public TupleH tuple(ImmutableList<ValueH> items) {
    var spec = tupleOT(map(items, ValueH::type));
    return tuple(spec, items);
  }

  public TupleH tuple(TupleTypeH tupleType, ImmutableList<ValueH> items) {
    return objectDb().tuple(tupleType, items);
  }

  public TupleH tupleEmpty() {
    return tuple(list());
  }

  public TupleH tupleWithStr() {
    return tuple(list(string("abc")));
  }

  public TupleH tupleWithStr(StringH str) {
    return tuple(list(str));
  }

  public ArrayH messageArrayWithOneError() {
    return array(objectFactory().errorMessage("error message"));
  }

  public ArrayH messageArrayEmtpy() {
    return array(objectFactory().messageType());
  }

  public TupleH errorMessage(String text) {
    return objectFactory().errorMessage(text);
  }

  public TupleH warningMessage(String text) {
    return objectFactory().warningMessage(text);
  }

  public TupleH infoMessage(String text) {
    return objectFactory().infoMessage(text);
  }

  // Expr-s

  public CallH call(ExprH function, ImmutableList<ExprH> arguments) {
    return objectDb().call(function, construct(arguments));
  }

  public ConstH const_(ValueH val) {
    return objectDb().const_(val);
  }

  public ConstructH construct(ImmutableList<ExprH> items) {
    return objectDb().construct(items);
  }

  public OrderH order(ImmutableList<ExprH> elements) {
    return objectDb().order(elements);
  }

  public RefH ref(int value) {
    return objectDb().ref(BigInteger.valueOf(value), intOT());
  }

  public RefH ref(TypeHV evaluationType, int pointer) {
    return objectDb().ref(BigInteger.valueOf(pointer), evaluationType);
  }

  public SelectH select(ExprH tuple, IntH index) {
    return objectDb().select(tuple, index);
  }

  // Expr with specific evaluation type

  public ConstH boolExpr() {
    return const_(bool(true));
  }

  public ConstH intExpr() {
    return intExpr(17);
  }

  public ConstH intExpr(int i) {
    return const_(int_(i));
  }

  public ConstH stringExpr() {
    return stringExpr("abc");
  }

  public ConstH stringExpr(String string) {
    return const_(string(string));
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

  public FunctionTypeS functionST(TypeS resultType, Item... parameters) {
    return typeFactoryS().function(resultType, toTypes(list(parameters)));
  }

  public FunctionTypeS functionST(TypeS resultType, Iterable<ItemSignature> parameters) {
    return typeFactoryS().function(resultType, map(parameters, ItemSignature::type));
  }

  public IntTypeS intST() {
    return typeFactoryS().int_();
  }

  public NothingTypeS nothingST() {
    return typeFactoryS().nothing();
  }

  public StringTypeS stringST() {
    return typeFactoryS().string();
  }

  public StructTypeS structST(String name, NamedList<TypeS> fields) {
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

  public OrderS arrayExpression(
      int line, TypeS elemType, ExprS... expr) {
    return new OrderS(
        arrayST(elemType), ImmutableList.copyOf(expr), loc(line));
  }

  public BlobS blobExpression(int data) {
    return blobExpression(1, data);
  }

  public BlobS blobExpression(int line, int data) {
    return new BlobS(blobST(), ByteString.of((byte) data), loc(line));
  }

  public CallS callExpression(
      TypeS type, ExprS expr, ExprS... arguments) {
    return callExpression(1, type, expr, arguments);
  }

  public CallS callExpression(
      int line, TypeS type, ExprS expr, ExprS... arguments) {
    return new CallS(type, expr, list(arguments), loc(line));
  }

  public NativeFunction functionExpression(TypeS type, String name, Item... parameters) {
    return functionExpression(
        1, type, name, annotation(1, stringExpression(1, "Impl.met")), parameters);
  }

  public NativeFunction functionExpression(int line, TypeS type, String name,
      Annotation annotation, Item... parameters) {
    return new NativeFunction(functionST(type, parameters), modulePath(), name, list(parameters),
        annotation, loc(line)
    );
  }

  public DefinedFunction functionExpression(TypeS type, String name, ExprS body,
      Item... parameters) {
    return functionExpression(1, type, name, body, parameters);
  }

  public DefinedFunction functionExpression(
      int line, TypeS type, String name, ExprS body, Item... parameters) {
    return new DefinedFunction(functionST(type, parameters), modulePath(), name, list(parameters),
        body, loc(line)
    );
  }

  public IntS intExpression(int value) {
    return intExpression(1, value);
  }

  public IntS intExpression(int line, int value) {
    return new IntS(intST(), BigInteger.valueOf(value), loc(line));
  }

  public ParamRefS parameterRefExpression(TypeS type, String name) {
    return parameterRefExpression(1, type, name);
  }

  public ParamRefS parameterRefExpression(
      int line, TypeS type, String name) {
    return new ParamRefS(type, name, loc(line));
  }

  public RefS referenceExpression(GlobalReferencable referencable) {
    return referenceExpression(1, referencable.type(), referencable.name());
  }

  public RefS referenceExpression(int line, TypeS type, String name) {
    return new RefS(type, name, loc(line));
  }

  public SelectS selectExpression(
      int line, TypeS field, int index, ExprS expr) {
    return new SelectS(field, index, expr, loc(line));
  }

  public StringS stringExpression(int line, String data) {
    return new StringS(stringST(), data, loc(line));
  }

  // other smooth language thingies

  public Annotation annotation(int line, StringS implementedBy) {
    return annotation(line, implementedBy, true);
  }

  public Annotation annotation(int line, StringS implementedBy, boolean pure) {
    return new Annotation(implementedBy, pure, loc(line));
  }

  public Constructor constructor(int line, TypeS resultType, String name, Item... parameters) {
    return new Constructor(
        functionST(resultType, parameters), modulePath(), name, list(parameters), loc(line));
  }

  public Item field(TypeS type, String name) {
    return new Item(type, modulePath(), name, Optional.empty(), loc(1));
  }

  public Item parameter(TypeS type, String name) {
    return parameter(1, type, name);
  }

  public Item parameter(int line, TypeS type, String name) {
    return parameter(line, type, name, Optional.empty());
  }

  public Item parameter(int line, TypeS type, String name, ExprS defaultArg) {
    return parameter(line, type, name, Optional.of(defaultArg));
  }

  private Item parameter(int line, TypeS type, String name,
      Optional<ExprS> defaultArg) {
    return new Item(type, modulePath(), name, defaultArg, loc(line));
  }

  public DefinedValue value(int line, TypeS type, String name, ExprS expr) {
    return new DefinedValue(type, modulePath(), name, expr, loc(line));
  }

  public NativeValue value(int line, TypeS type, String name, Annotation annotation) {
    return new NativeValue(type, modulePath(), name, annotation, loc(line));
  }
}
