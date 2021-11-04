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
import org.smoothbuild.db.object.db.ObjFactory;
import org.smoothbuild.db.object.obj.ObjDb;
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
import org.smoothbuild.db.object.type.ObjTypeDb;
import org.smoothbuild.db.object.type.TypeFactoryO;
import org.smoothbuild.db.object.type.base.TypeV;
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
import org.smoothbuild.lang.base.type.api.Bounded;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.BoundsMap;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.api.Variable;
import org.smoothbuild.lang.base.type.impl.AnySType;
import org.smoothbuild.lang.base.type.impl.ArraySType;
import org.smoothbuild.lang.base.type.impl.BlobSType;
import org.smoothbuild.lang.base.type.impl.BoolSType;
import org.smoothbuild.lang.base.type.impl.FunctionSType;
import org.smoothbuild.lang.base.type.impl.IntSType;
import org.smoothbuild.lang.base.type.impl.NothingSType;
import org.smoothbuild.lang.base.type.impl.StringSType;
import org.smoothbuild.lang.base.type.impl.StructSType;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.base.type.impl.TypingS;
import org.smoothbuild.lang.base.type.impl.VariableSType;
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

public class TestingContext {
  private Computer computer;
  private Container container;
  private ObjFactory objFactory;
  private ComputationCache computationCache;
  private FileSystem computationCacheFileSystem;
  private ObjDb objDb;
  private TypingS typingS;
  private ObjTypeDb objTypeDb;
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

  public TypeFactoryO typeFactoryO() {
    return objTypeDb();
  }

  public TypeFactoryS typeFactoryS() {
    if (typeFactoryS == null) {
      typeFactoryS = new TypeFactoryS();
    }
    return typeFactoryS;
  }

  public ObjTypeDb objTypeDb() {
    if (objTypeDb == null) {
      objTypeDb = new ObjTypeDb(hashedDb());
    }
    return objTypeDb;
  }

  public ObjDb objectDb() {
    if (objDb == null) {
      objDb = new ObjDb(hashedDb(), objTypeDb());
    }
    return objDb;
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

  public ObjDb objectDbOther() {
    return new ObjDb(hashedDb(), objTypeDbOther());
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

  public StructOType animalOT() {
    return typeFactoryO().struct(
        "Animal", namedList(list(named("species", stringOT()), named("speed", intOT()))));
  }

  public AnyOType anyOT() {
    return typeFactoryO().any();
  }

  public ArrayOType arrayOT(TypeV elementSpec) {
    return typeFactoryO().array(elementSpec);
  }

  public BlobOType blobOT() {
    return typeFactoryO().blob();
  }

  public BoolOType boolOT() {
    return typeFactoryO().bool();
  }

  public TupleOType fileOT() {
    return tupleOT(list(blobOT(), stringOT()));
  }

  public IntOType intOT() {
    return typeFactoryO().int_();
  }

  public LambdaOType lambdaOT() {
    return lambdaOT(intOT(), list(blobOT(), stringOT()));
  }

  public LambdaOType lambdaOT(TypeV result, ImmutableList<? extends TypeV> parameters) {
    return typeFactoryO().function(result, parameters);
  }

  public NativeMethodOType nativeMethodOT() {
    return objTypeDb().nativeMethod();
  }

  public NothingOType nothingOT() {
    return typeFactoryO().nothing();
  }

  public TupleOType perso_OT() {
    return tupleOT(list(stringOT(), stringOT()));
  }

  public StructOType personOT() {
    return structOT("Person",
        namedList(list(named("firstName", stringOT()), named("lastName", stringOT()))));
  }

  public StringOType stringOT() {
    return typeFactoryO().string();
  }

  public TupleOType tupleOT(ImmutableList<TypeV> itemSpecs) {
    return objTypeDb().tuple(itemSpecs);
  }

  public TupleOType tupleEmptyOT() {
    return tupleOT(list());
  }

  public TupleOType tupleWithStrOT() {
    return tupleOT(list(stringOT()));
  }

  public StructOType structOT() {
    return structOT(namedList(list(named("field", intOT()))));
  }

  public StructOType structOT(NamedList<? extends TypeV> fields) {
    return structOT("MyStruct", fields);
  }

  public StructOType structOT(String name, NamedList<? extends TypeV> fields) {
    return typeFactoryO().struct(name, fields);
  }

  public VariableOType variableOT(String name) {
    return typeFactoryO().variable(name);
  }

  public Side lowerOT() {
    return typeFactoryO().lower();
  }

  public Side upperOT() {
    return typeFactoryO().upper();
  }

  // Expr types

  public CallOType callOT() {
    return callOT(intOT());
  }

  public CallOType callOT(TypeV evaluationType) {
    return objTypeDb().call(evaluationType);
  }

  public ConstOType constOT() {
    return constOT(intOT());
  }

  public ConstOType constOT(TypeV evaluationType) {
    return objTypeDb().const_(evaluationType);
  }

  public ConstructOType constructOT() {
    return constructOT(list(intOT(), stringOT()));
  }

  public ConstructOType constructOT(ImmutableList<TypeV> itemSpecs) {
    return objTypeDb().construct(tupleOT(itemSpecs));
  }

  public OrderOType orderOT() {
    return orderOT(intOT());
  }

  public OrderOType orderOT(TypeV elementSpec) {
    return objTypeDb().order(elementSpec);
  }

  public RefOType refOT() {
    return refOT(intOT());
  }

  public RefOType refOT(TypeV evaluationType) {
    return objTypeDb().ref(evaluationType);
  }

  public SelectOType selectOT() {
    return selectOT(intOT());
  }

  public SelectOType selectOT(TypeV evaluationType) {
    return objTypeDb().select(evaluationType);
  }

  public StructExprOType structExprOT(StructOType structType) {
    return objTypeDb().structExpr(structType);
  }

  // Obj-s (values)

  public Struc_ animal() {
    return animal("rabbit", 7);
  }

  public Struc_ animal(String species, int speed) {
    return animal(string(species), int_(speed));
  }

  public Struc_ animal(Str species, Int speed) {
    return struct(animalOT(), list(species, speed));
  }

  public Array array(Val... elements) {
    return array(elements[0].type(), elements);
  }

  public Array array(TypeV elementSpec, Obj... elements) {
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

  public Int int_() {
    return int_(17);
  }

  public Int int_(int value) {
    return objectDb().int_(BigInteger.valueOf(value));
  }

  public Lambda lambda() {
    return lambda(intExpr());
  }

  public Lambda lambda(Expr body) {
    LambdaOType spec = lambdaOT(body.evaluationType(), list(stringOT()));
    return lambda(spec, body);
  }

  public Lambda lambda(LambdaOType spec, Expr body) {
    return objectDb().lambda(spec, body);
  }

  public NativeMethod nativeMethod(Blob jarFile, Str classBinaryName) {
    return objectDb().nativeMethod(jarFile, classBinaryName);
  }

  public Tuple person(String firstName, String lastName) {
    return tuple(list(string(firstName), string(lastName)));
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
    var spec = tupleOT(map(items, Val::type));
    return tuple(spec, items);
  }

  public Tuple tuple(TupleOType tupleType, List<? extends Val> items) {
    return objectDb().tuple(tupleType, items);
  }

  public Tuple tupleEmpty() {
    return tuple(list());
  }

  public Tuple tupleWithStr() {
    return tuple(list(string("abc")));
  }

  public Tuple tupleWithStr(Str str) {
    return tuple(list(str));
  }

  public Array messageArrayWithOneError() {
    return array(objectFactory().errorMessage("error message"));
  }

  public Array messageArrayEmtpy() {
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
    return objectDb().ref(BigInteger.valueOf(value), intOT());
  }

  public Ref ref(TypeV evaluationType, int pointer) {
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

  // Types Smooth

  public AnySType anyST() {
    return typeFactoryS().any();
  }

  public ArraySType arrayST(TypeS elemType) {
    return typeFactoryS().array(elemType);
  }

  public BlobSType blobST() {
    return typeFactoryS().blob();
  }

  public BoolSType boolST() {
    return typeFactoryS().bool();
  }

  public FunctionSType functionST(TypeS resultType, Item... parameters) {
    return typeFactoryS().function(resultType, toTypes(list(parameters)));
  }

  public FunctionSType functionST(TypeS resultType, Iterable<ItemSignature> parameters) {
    return typeFactoryS().function(resultType, map(parameters, ItemSignature::type));
  }

  public IntSType intST() {
    return typeFactoryS().int_();
  }

  public NothingSType nothingST() {
    return typeFactoryS().nothing();
  }

  public StringSType stringST() {
    return typeFactoryS().string();
  }

  public StructSType structST(String name, NamedList<? extends TypeS> fields) {
    return typeFactoryS().struct(name, fields);
  }

  public VariableSType variableST(String name) {
    return typeFactoryS().variable(name);
  }

  public Side lowerST() {
    return typeFactoryS().lower();
  }

  public Side upperST() {
    return typeFactoryS().upper();
  }

  public BoundsMap bmST(
      VariableSType var1, Side side1, TypeS bound1,
      VariableSType var2, Side side2, TypeS bound2) {
    Bounds bounds1 = oneSideBoundST(side1, bound1);
    Bounds bounds2 = oneSideBoundST(side2, bound2);
    if (var1.equals(var2)) {
      return boundsMap(new Bounded(var1, typingS().merge(bounds1, bounds2)));
    } else {
      return new BoundsMap(ImmutableMap.of(
          var1, new Bounded(var1, bounds1),
          var2, new Bounded(var2, bounds2)
      ));
    }
  }

  public BoundsMap bmST(Variable var, Side side, TypeS bound) {
    return boundsMap(new Bounded(var, oneSideBoundST(side, bound)));
  }

  public BoundsMap bmST() {
    return boundsMap();
  }

  public Bounds oneSideBoundST(Side side, TypeS type) {
    return typeFactoryS().oneSideBound(side, type);
  }

  // Expressions

  public ArrayLiteralExpression arrayExpression(
      int line, TypeS elemType, Expression... expressions) {
    return new ArrayLiteralExpression(
        arrayST(elemType), ImmutableList.copyOf(expressions), loc(line));
  }

  public BlobLiteralExpression blobExpression(int data) {
    return blobExpression(1, data);
  }

  public BlobLiteralExpression blobExpression(int line, int data) {
    return new BlobLiteralExpression(blobST(), ByteString.of((byte) data), loc(line));
  }

  public CallExpression callExpression(
      TypeS type, Expression expression, Expression... arguments) {
    return callExpression(1, type, expression, arguments);
  }

  public CallExpression callExpression(
      int line, TypeS type, Expression expression, Expression... arguments) {
    return new CallExpression(type, expression, list(arguments), loc(line));
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

  public DefinedFunction functionExpression(TypeS type, String name, Expression body,
      Item... parameters) {
    return functionExpression(1, type, name, body, parameters);
  }

  public DefinedFunction functionExpression(
      int line, TypeS type, String name, Expression body, Item... parameters) {
    return new DefinedFunction(functionST(type, parameters), modulePath(), name, list(parameters),
        body, loc(line)
    );
  }

  public IntLiteralExpression intExpression(int value) {
    return intExpression(1, value);
  }

  public IntLiteralExpression intExpression(int line, int value) {
    return new IntLiteralExpression(intST(), BigInteger.valueOf(value), loc(line));
  }

  public ParameterReferenceExpression parameterRefExpression(TypeS type, String name) {
    return parameterRefExpression(1, type, name);
  }

  public ParameterReferenceExpression parameterRefExpression(
      int line, TypeS type, String name) {
    return new ParameterReferenceExpression(type, name, loc(line));
  }

  public ReferenceExpression referenceExpression(GlobalReferencable referencable) {
    return referenceExpression(1, referencable.type(), referencable.name());
  }

  public ReferenceExpression referenceExpression(int line, TypeS type, String name) {
    return new ReferenceExpression(type, name, loc(line));
  }

  public SelectExpression selectExpression(
      int line, TypeS field, int index, Expression expression) {
    return new SelectExpression(field, index, expression, loc(line));
  }

  public StringLiteralExpression stringExpression(int line, String data) {
    return new StringLiteralExpression(stringST(), data, loc(line));
  }

  // other smooth language thingies

  public Annotation annotation(int line, StringLiteralExpression implementedBy) {
    return annotation(line, implementedBy, true);
  }

  public Annotation annotation(int line, StringLiteralExpression implementedBy, boolean pure) {
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

  public Item parameter(int line, TypeS type, String name, Expression defaultArg) {
    return parameter(line, type, name, Optional.of(defaultArg));
  }

  private Item parameter(int line, TypeS type, String name,
      Optional<Expression> defaultArg) {
    return new Item(type, modulePath(), name, defaultArg, loc(line));
  }

  public DefinedValue value(int line, TypeS type, String name, Expression expression) {
    return new DefinedValue(type, modulePath(), name, expression, loc(line));
  }

  public NativeValue value(int line, TypeS type, String name, Annotation annotation) {
    return new NativeValue(type, modulePath(), name, annotation, loc(line));
  }
}
