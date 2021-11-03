package org.smoothbuild.testing;

import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.lang.base.define.Item.toTypes;
import static org.smoothbuild.lang.base.define.TestingLocation.loc;
import static org.smoothbuild.lang.base.define.TestingModulePath.modulePath;
import static org.smoothbuild.lang.base.type.api.BoundsMap.boundsMap;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Named.named;
import static org.smoothbuild.util.collect.NamedList.namedList;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.expr.Call;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.obj.expr.Construct;
import org.smoothbuild.db.object.obj.expr.Order;
import org.smoothbuild.db.object.obj.expr.Ref;
import org.smoothbuild.db.object.obj.expr.Select;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.BlobBuilder;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.Lambda;
import org.smoothbuild.db.object.obj.val.NativeMethod;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.db.object.obj.val.Tuple;
import org.smoothbuild.db.object.type.OTypeFactory;
import org.smoothbuild.db.object.type.ObjTypeDb;
import org.smoothbuild.db.object.type.base.ValType;
import org.smoothbuild.db.object.type.expr.CallOType;
import org.smoothbuild.db.object.type.expr.ConstOType;
import org.smoothbuild.db.object.type.expr.ConstructOType;
import org.smoothbuild.db.object.type.expr.OrderOType;
import org.smoothbuild.db.object.type.expr.RefOType;
import org.smoothbuild.db.object.type.expr.SelectOType;
import org.smoothbuild.db.object.type.expr.StructExprOType;
import org.smoothbuild.db.object.type.val.AnyOType;
import org.smoothbuild.db.object.type.val.ArrayOType;
import org.smoothbuild.db.object.type.val.BlobOType;
import org.smoothbuild.db.object.type.val.BoolOType;
import org.smoothbuild.db.object.type.val.IntOType;
import org.smoothbuild.db.object.type.val.LambdaOType;
import org.smoothbuild.db.object.type.val.NativeMethodOType;
import org.smoothbuild.db.object.type.val.NothingOType;
import org.smoothbuild.db.object.type.val.StringOType;
import org.smoothbuild.db.object.type.val.StructOType;
import org.smoothbuild.db.object.type.val.TupleOType;
import org.smoothbuild.db.object.type.val.VariableOType;
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
import org.smoothbuild.lang.base.type.Typing;
import org.smoothbuild.lang.base.type.api.AnyType;
import org.smoothbuild.lang.base.type.api.ArrayType;
import org.smoothbuild.lang.base.type.api.BlobType;
import org.smoothbuild.lang.base.type.api.BoolType;
import org.smoothbuild.lang.base.type.api.Bounded;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.BoundsMap;
import org.smoothbuild.lang.base.type.api.FunctionType;
import org.smoothbuild.lang.base.type.api.IntType;
import org.smoothbuild.lang.base.type.api.NothingType;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.api.StringType;
import org.smoothbuild.lang.base.type.api.StructType;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.TypeFactory;
import org.smoothbuild.lang.base.type.api.Variable;
import org.smoothbuild.lang.base.type.impl.STypeFactory;
import org.smoothbuild.lang.expr.Annotation;
import org.smoothbuild.lang.expr.ArrayLiteralExpression;
import org.smoothbuild.lang.expr.BlobLiteralExpression;
import org.smoothbuild.lang.expr.CallExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.IntLiteralExpression;
import org.smoothbuild.lang.expr.ParameterReferenceExpression;
import org.smoothbuild.lang.expr.ReferenceExpression;
import org.smoothbuild.lang.expr.SelectExpression;
import org.smoothbuild.lang.expr.StringLiteralExpression;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.util.Providers;

import okio.ByteString;

public abstract class AbstractTestingContext {
  private Computer computer;
  private Container container;
  private ObjectFactory objectFactory;
  private ComputationCache computationCache;
  private FileSystem computationCacheFileSystem;
  private ObjectDb objectDb;
  private Typing typing;
  private ObjTypeDb objTypeDb;
  private HashedDb hashedDb;
  private FileSystem hashedDbFileSystem;
  private FileSystem fullFileSystem;
  private TempManager tempManager;
  private SModule internalModule;
  private STypeFactory sTypeFactory;

  public NativeApi nativeApi() {
    return container();
  }

  public TestingModuleLoader module(String sourceCode) {
    return new TestingModuleLoader(this, sourceCode);
  }

  public SModule internalModule() {
    if (internalModule == null) {
      internalModule = new InternalModuleLoader(sTypeFactory()).loadModule();
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

  public ObjectFactory objectFactory() {
    if (objectFactory == null) {
      objectFactory = new ObjectFactory(objectDb(), objTypeDb());
    }
    return objectFactory;
  }

  public Typing typing() {
    if (typing == null) {
      typing = new Typing(typeFactory());
    }
    return typing;
  }

  public abstract TypeFactory typeFactory();

  public OTypeFactory oTypeFactory() {
    return objTypeDb();
  }

  public STypeFactory sTypeFactory() {
    if (sTypeFactory == null) {
      sTypeFactory = new STypeFactory();
    }
    return sTypeFactory;
  }

  public ObjTypeDb objTypeDb() {
    if (objTypeDb == null) {
      objTypeDb = new ObjTypeDb(hashedDb());
    }
    return objTypeDb;
  }

  public ObjectDb objectDb() {
    if (objectDb == null) {
      objectDb = new ObjectDb(hashedDb(), objTypeDb());
    }
    return objectDb;
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

  public ObjectDb objectDbOther() {
    return new ObjectDb(hashedDb(), objTypeDbOther());
  }

  public ObjTypeDb objTypeDbOther() {
    return new ObjTypeDb(hashedDb());
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

  public StructOType animalSpec() {
    return objTypeDb().struct(
        "Animal", namedList(list(named("species", stringSpec()), named("speed", intSpec()))));
  }

  public ArrayOType arraySpec(ValType elementSpec) {
    return objTypeDb().array(elementSpec);
  }

  public AnyOType anySpec() {
    return objTypeDb().any();
  }

  public BlobOType blobSpec() {
    return objTypeDb().blob();
  }

  public BoolOType boolSpec() {
    return objTypeDb().bool();
  }

  public LambdaOType lambdaSpec() {
    return lambdaSpec(intSpec(), list(blobSpec(), stringSpec()));
  }

  public LambdaOType lambdaSpec(ValType result, ImmutableList<? extends Type> parameters) {
    return objTypeDb().function(result, parameters);
  }

  public IntOType intSpec() {
    return objTypeDb().int_();
  }

  public NativeMethodOType nativeMethodSpec() {
    return objTypeDb().nativeMethod();
  }

  public NothingOType nothingSpec() {
    return objTypeDb().nothing();
  }

  public StringOType stringSpec() {
    return objTypeDb().string();
  }

  public TupleOType tupleSpec(ImmutableList<ValType> itemSpecs) {
    return objTypeDb().tuple(itemSpecs);
  }

  public TupleOType emptyTupleSpec() {
    return tupleSpec(list());
  }

  public TupleOType tupleWithStrSpec() {
    return tupleSpec(list(stringSpec()));
  }

  public TupleOType perso_Spec() {
    return tupleSpec(list(stringSpec(), stringSpec()));
  }

  public StructOType personSpec() {
    return structSpec("Person",
        namedList(list(named("firstName", stringSpec()), named("lastName", stringSpec()))));
  }

  public TupleOType fileSpec() {
    return tupleSpec(list(blobSpec(), stringSpec()));
  }

  public StructOType structSpec() {
    return structSpec(namedList(list(named("field", intSpec()))));
  }

  public StructOType structSpec(NamedList<? extends Type> fields) {
    return structSpec("MyStruct", fields);
  }

  public StructOType structSpec(String name, NamedList<? extends Type> fields) {
    return objTypeDb().struct(name, fields);
  }

  public VariableOType variableSpec(String name) {
    return objTypeDb().variable(name);
  }

  // Expr types

  public OrderOType orderSpec() {
    return orderSpec(intSpec());
  }

  public OrderOType orderSpec(ValType elementSpec) {
    return objTypeDb().order(elementSpec);
  }

  public CallOType callSpec() {
    return callSpec(intSpec());
  }

  public CallOType callSpec(ValType evaluationType) {
    return objTypeDb().call(evaluationType);
  }

  public ConstOType constSpec() {
    return constSpec(intSpec());
  }

  public ConstOType constSpec(ValType evaluationType) {
    return objTypeDb().const_(evaluationType);
  }

  public ConstructOType constructSpec() {
    return constructSpec(list(intSpec(), stringSpec()));
  }

  public ConstructOType constructSpec(ImmutableList<ValType> itemSpecs) {
    return objTypeDb().construct(tupleSpec(itemSpecs));
  }

  public SelectOType selectSpec() {
    return selectSpec(intSpec());
  }

  public SelectOType selectSpec(ValType evaluationType) {
    return objTypeDb().select(evaluationType);
  }

  public StructExprOType structExprSpec(StructOType structType) {
    return objTypeDb().structExpr(structType);
  }

  public RefOType refSpec() {
    return refSpec(intSpec());
  }

  public RefOType refSpec(ValType evaluationType) {
    return objTypeDb().ref(evaluationType);
  }

  // Obj-s (values)

  public Struc_ animal() {
    return animal("rabbit", 7);
  }

  public Struc_ animal(String species, int speed) {
    return animal(string(species), int_(speed));
  }

  public Struc_ animal(Str species, Int speed) {
    return struct(animalSpec(), list(species, speed));
  }

  public Array array(Val... elements) {
    return array(elements[0].type(), elements);
  }

  public Array array(ValType elementSpec, Obj... elements) {
    return objectDb().arrayBuilder(elementSpec).addAll(list(elements)).build();
  }

  public Blob blob() {
    return objectFactory().blob(sink -> sink.writeUtf8("blob data"));
  }

  public Blob blob(ByteString bytes) {
    return objectFactory().blob(sink -> sink.write(bytes));
  }

  public BlobBuilder blobBuilder() {
    return objectDb().blobBuilder();
  }

  public Bool bool(boolean value) {
    return objectDb().bool(value);
  }

  public Lambda lambda() {
    return lambda(intExpr());
  }

  public Lambda lambda(Expr body) {
    LambdaOType spec = lambdaSpec(body.evaluationType(), list(stringSpec()));
    return lambda(spec, body);
  }

  public Lambda lambda(LambdaOType spec, Expr body) {
    return objectDb().lambda(spec, body);
  }

  public Int int_() {
    return int_(17);
  }

  public Int int_(int value) {
    return objectDb().int_(BigInteger.valueOf(value));
  }

  public NativeMethod nativeMethod(Blob jarFile, Str classBinaryName) {
    return objectDb().nativeMethod(jarFile, classBinaryName);
  }

  public Str string() {
    return objectDb().string("abc");
  }

  public Str string(String string) {
    return objectDb().string(string);
  }

  public Struc_ struct(StructOType spec, ImmutableList<Val> items) {
    return objectDb().struct(spec, items);
  }

  public Tuple tuple(List<? extends Val> items) {
    var spec = tupleSpec(map(items, Val::type));
    return tuple(spec, items);
  }

  public Tuple tuple(TupleOType tupleType, List<? extends Val> items) {
    return objectDb().tuple(tupleType, items);
  }

  public Tuple emptyTuple() {
    return tuple(list());
  }

  public Tuple tupleWithStr() {
    return tuple(list(string("abc")));
  }

  public Tuple tupleWithStr(Str str) {
    return tuple(list(str));
  }

  public Tuple person(String firstName, String lastName) {
    return tuple(list(string(firstName), string(lastName)));
  }

  public Array messageArrayWithOneError() {
    return array(objectFactory().errorMessage("error message"));
  }

  public Array emptyMessageArray() {
    return array(objectFactory().messageType());
  }

  public Struc_ errorMessage(String text) {
    return objectFactory().errorMessage(text);
  }

  public Struc_ warningMessage(String text) {
    return objectFactory().warningMessage(text);
  }

  public Struc_ infoMessage(String text) {
    return objectFactory().infoMessage(text);
  }

  public Struc_ file(Path path) {
    return file(path, ByteString.encodeString(path.toString(), CHARSET));
  }

  public Struc_ file(Path path, ByteString content) {
    return file(path.toString(), blob(content));
  }

  public Struc_ file(String path, Blob blob) {
    Str string = objectFactory().string(path);
    return objectFactory().file(string, blob);
  }

  // Expr-s

  public Call call(Expr function, ImmutableList<? extends Expr> arguments) {
    return objectDb().call(function, construct(arguments));
  }

  public Const const_(Val val) {
    return objectDb().const_(val);
  }

  public Construct construct(ImmutableList<? extends Expr> items) {
    return objectDb().construct(items);
  }

  public Order order(List<? extends Expr> elements) {
    return objectDb().order(elements);
  }

  public Ref ref(int value) {
    return objectDb().ref(BigInteger.valueOf(value), intSpec());
  }

  public Ref ref(ValType evaluationType, int pointer) {
    return objectDb().ref(BigInteger.valueOf(pointer), evaluationType);
  }

  public Select select(Expr tuple, Int index) {
    return objectDb().select(tuple, index);
  }

  // Expr with specific evaluation type

  public Const boolExpr() {
    return const_(bool(true));
  }

  public Const intExpr() {
    return intExpr(17);
  }

  public Const intExpr(int i) {
    return const_(int_(i));
  }

  public Const stringExpr() {
    return stringExpr("abc");
  }

  public Const stringExpr(String string) {
    return const_(string(string));
  }

  // Types

  public Variable variable(String name) {
    return typeFactory().variable(name);
  }

  public AnyType anyT() {
    return typeFactory().any();
  }

  public ArrayType arrayT(Type elemType) {
    return typeFactory().array(elemType);
  }

  public BlobType blobT() {
    return typeFactory().blob();
  }

  public BoolType boolT() {
    return typeFactory().bool();
  }

  public IntType intT() {
    return typeFactory().int_();
  }

  public NothingType nothingT() {
    return typeFactory().nothing();
  }

  public StringType stringT() {
    return typeFactory().string();
  }

  public StructType structT(String name, NamedList<? extends Type> fields) {
    return typeFactory().struct(name, fields);
  }

  public FunctionType functionT(Type resultType, Item... parameters) {
    return typeFactory().function(resultType, toTypes(list(parameters)));
  }

  public FunctionType functionT(Type resultType, Iterable<ItemSignature> parameters) {
    return typeFactory().function(resultType, map(parameters, ItemSignature::type));
  }

  public Side lower() {
    return typeFactory().lower();
  }

  public Side upper() {
    return typeFactory().upper();
  }

  public BoundsMap bm(
      Variable var1, Side side1, Type bound1,
      Variable var2, Side side2, Type bound2) {
    Bounds bounds1 = typeFactory().oneSideBound(side1, bound1);
    Bounds bounds2 = typeFactory().oneSideBound(side2, bound2);
    if (var1.equals(var2)) {
      return boundsMap(new Bounded(var1, typing().merge(bounds1, bounds2)));
    } else {
      return new BoundsMap(ImmutableMap.of(
          var1, new Bounded(var1, bounds1),
          var2, new Bounded(var2, bounds2)
      ));
    }
  }

  public BoundsMap bm(Variable var, Side side, Type bound) {
    return boundsMap(new Bounded(var, typeFactory().oneSideBound(side, bound)));
  }

  public BoundsMap bm() {
    return boundsMap();
  }

  // Expressions

  public BlobLiteralExpression blobExpression(int data) {
    return blobExpression(1, data);
  }

  public BlobLiteralExpression blobExpression(int line, int data) {
    return new BlobLiteralExpression(typeFactory().blob(), ByteString.of((byte) data), loc(line));
  }

  public IntLiteralExpression intExpression(int value) {
    return intExpression(1, value);
  }

  public IntLiteralExpression intExpression(int line, int value) {
    return new IntLiteralExpression(typeFactory().int_(), BigInteger.valueOf(value), loc(line));
  }

  public StringLiteralExpression stringExpression(int line, String data) {
    return new StringLiteralExpression(typeFactory().string(), data, loc(line));
  }

  public ArrayLiteralExpression arrayExpression(
      int line, Type elemType, Expression... expressions) {
    return new ArrayLiteralExpression(
        typeFactory().array(elemType), ImmutableList.copyOf(expressions), loc(line));
  }

  public ReferenceExpression referenceExpression(GlobalReferencable referencable) {
    return referenceExpression(1, referencable.type(), referencable.name());
  }

  public ReferenceExpression referenceExpression(int line, Type type, String name) {
    return new ReferenceExpression(type, name, loc(line));
  }

  public ParameterReferenceExpression parameterRefExpression(Type type, String name) {
    return parameterRefExpression(1, type, name);
  }

  public ParameterReferenceExpression parameterRefExpression(
      int line, Type type, String name) {
    return new ParameterReferenceExpression(type, name, loc(line));
  }

  public SelectExpression selectExpression(
      int line, Type field, int index, Expression expression) {
    return new SelectExpression(field, index, expression, loc(line));
  }

  public CallExpression callExpression(
      Type type, Expression expression, Expression... arguments) {
    return callExpression(1, type, expression, arguments);
  }

  public CallExpression callExpression(
      int line, Type type, Expression expression, Expression... arguments) {
    return new CallExpression(type, expression, list(arguments), loc(line));
  }

  public NativeFunction functionExpression(Type type, String name, Item... parameters) {
    return functionExpression(
        1, type, name, annotation(1, stringExpression(1, "Impl.met")), parameters);
  }

  public NativeFunction functionExpression(int line, Type type, String name,
      Annotation annotation, Item... parameters) {
    return new NativeFunction(functionT(type, parameters), modulePath(), name, list(parameters),
        annotation, loc(line)
    );
  }

  public DefinedFunction functionExpression(Type type, String name, Expression body,
      Item... parameters) {
    return functionExpression(1, type, name, body, parameters);
  }

  public DefinedFunction functionExpression(
      int line, Type type, String name, Expression body, Item... parameters) {
    return new DefinedFunction(functionT(type, parameters), modulePath(), name, list(parameters),
        body, loc(line)
    );
  }

  public DefinedValue valueExpression(
      int line, Type type, String name, Expression expression) {
    return new DefinedValue(type, modulePath(), name, expression, loc(line));
  }

  public NativeValue valueExpression(
      int line, Type type, String name, Annotation annotation) {
    return new NativeValue(type, modulePath(), name, annotation, loc(line));
  }

  public Annotation annotation(int line, StringLiteralExpression implementedBy) {
    return annotation(line, implementedBy, true);
  }

  public Annotation annotation(int line, StringLiteralExpression implementedBy, boolean pure) {
    return new Annotation(implementedBy, pure, loc(line));
  }

  public Constructor constrExpression(
      int line, Type resultType, String name, Item... parameters) {
    return new Constructor(functionT(resultType, parameters), modulePath(), name, list(parameters),
        loc(line));
  }

  public Item parameterExpression(Type type, String name) {
    return parameterExpression(1, type, name);
  }

  public Item parameterExpression(int line, Type type, String name) {
    return parameterExpression(line, type, name, Optional.empty());
  }

  public Item parameterExpression(int line, Type type, String name, Expression defaultArg) {
    return parameterExpression(line, type, name, Optional.of(defaultArg));
  }

  private Item parameterExpression(int line, Type type, String name,
      Optional<Expression> defaultArg) {
    return new Item(type, modulePath(), name, defaultArg, loc(line));
  }

  public Item fieldExpression(Type type, String name) {
    return new Item(type, modulePath(), name, Optional.empty(), loc(1));
  }
}
