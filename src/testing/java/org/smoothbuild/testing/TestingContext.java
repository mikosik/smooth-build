package org.smoothbuild.testing;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Arrays.stream;
import static java.util.Collections.nCopies;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.lang.base.define.Item.toTypes;
import static org.smoothbuild.lang.base.define.TestingLocation.loc;
import static org.smoothbuild.lang.base.define.TestingModulePath.modulePath;
import static org.smoothbuild.lang.base.type.api.BoundsMap.boundsMap;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.db.object.db.SpecDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.expr.ArrayExpr;
import org.smoothbuild.db.object.obj.expr.Call;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.obj.expr.Invoke;
import org.smoothbuild.db.object.obj.expr.Null;
import org.smoothbuild.db.object.obj.expr.RecExpr;
import org.smoothbuild.db.object.obj.expr.Ref;
import org.smoothbuild.db.object.obj.expr.Select;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.BlobBuilder;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.Lambda;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.expr.AbsentSpec;
import org.smoothbuild.db.object.spec.expr.ArrayExprSpec;
import org.smoothbuild.db.object.spec.expr.CallSpec;
import org.smoothbuild.db.object.spec.expr.ConstSpec;
import org.smoothbuild.db.object.spec.expr.InvokeSpec;
import org.smoothbuild.db.object.spec.expr.NullSpec;
import org.smoothbuild.db.object.spec.expr.RecExprSpec;
import org.smoothbuild.db.object.spec.expr.RefSpec;
import org.smoothbuild.db.object.spec.expr.SelectSpec;
import org.smoothbuild.db.object.spec.val.AnySpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;
import org.smoothbuild.db.object.spec.val.BlobSpec;
import org.smoothbuild.db.object.spec.val.BoolSpec;
import org.smoothbuild.db.object.spec.val.IntSpec;
import org.smoothbuild.db.object.spec.val.LambdaSpec;
import org.smoothbuild.db.object.spec.val.NothingSpec;
import org.smoothbuild.db.object.spec.val.RecSpec;
import org.smoothbuild.db.object.spec.val.StrSpec;
import org.smoothbuild.db.object.spec.val.StructSpec;
import org.smoothbuild.db.object.spec.val.VariableSpec;
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
import org.smoothbuild.lang.base.define.Location;
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
import org.smoothbuild.lang.base.type.api.ItemSignature;
import org.smoothbuild.lang.base.type.api.NothingType;
import org.smoothbuild.lang.base.type.api.Sides;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.api.StringType;
import org.smoothbuild.lang.base.type.api.StructType;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.TypeFactory;
import org.smoothbuild.lang.base.type.api.Variable;
import org.smoothbuild.lang.base.type.impl.TypeFactoryImpl;
import org.smoothbuild.lang.expr.AnnotationExpression;
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.util.Providers;

import okio.ByteString;

public class TestingContext {
  private Computer computer;
  private Container container;
  private ObjectFactory objectFactory;
  private ComputationCache computationCache;
  private FileSystem computationCacheFileSystem;
  private ObjectDb objectDb;
  private Typing typing;
  private TypeFactory typeFactory;
  private SpecDb specDb;
  private HashedDb hashedDb;
  private FileSystem hashedDbFileSystem;
  private FileSystem fullFileSystem;
  private TempManager tempManager;
  private SModule internalModule;

  public NativeApi nativeApi() {
    return container();
  }

  public TestingModuleLoader module(String sourceCode) {
    return new TestingModuleLoader(this, sourceCode);
  }

  public SModule internalModule() {
    if (internalModule == null) {
      internalModule = new InternalModuleLoader(typing()).loadModule();
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
      objectFactory = new ObjectFactory(objectDb(), specDb());
    }
    return objectFactory;
  }

  public Typing typing() {
    if (typing == null) {
      typing = new Typing(typeFactory());
    }
    return typing;
  }

  public TypeFactory typeFactory() {
    if (typeFactory == null) {
      typeFactory = new TypeFactoryImpl();
    }
    return typeFactory;
  }

  public SpecDb specDb() {
    if (specDb == null) {
      specDb = new SpecDb(hashedDb());
    }
    return specDb;
  }

  public ObjectDb objectDb() {
    if (objectDb == null) {
      objectDb = new ObjectDb(hashedDb(), specDb());
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
    return new ObjectDb(hashedDb(), specDbOther());
  }

  public SpecDb specDbOther() {
    return new SpecDb(hashedDb());
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

  // Obj Spec-s

  public StructSpec animalSpec() {
    return specDb().struct("Animal", list(strSpec(), intSpec()), list("species", "speed"));
  }

  public ArraySpec arraySpec(ValSpec elementSpec) {
    return specDb().array(elementSpec);
  }

  public AbsentSpec absentSpec() {
    return specDb().absentSpec();
  }

  public AnySpec anySpec() {
    return specDb().any();
  }

  public BlobSpec blobSpec() {
    return specDb().blob();
  }

  public BoolSpec boolSpec() {
    return specDb().bool();
  }

  public LambdaSpec lambdaSpec() {
    return lambdaSpec(intSpec(), list(blobSpec(), strSpec()));
  }

  public LambdaSpec lambdaSpec(ValSpec result, ImmutableList<? extends Type> parameters) {
    return specDb().function(result, parameters);
  }

  public IntSpec intSpec() {
    return specDb().int_();
  }

  public NothingSpec nothingSpec() {
    return specDb().nothing();
  }

  public StrSpec strSpec() {
    return specDb().string();
  }

  public RecSpec recSpec(ImmutableList<ValSpec> itemSpecs) {
    return specDb().recSpec(itemSpecs);
  }

  public RecSpec recSpecWithAbsents(int size) {
    return specDb().recSpec(ImmutableList.copyOf(nCopies(size, absentSpec())));
  }

  public RecSpec emptyRecSpec() {
    return recSpec(list());
  }

  public RecSpec recWithStrSpec() {
    return recSpec(list(strSpec()));
  }

  public RecSpec perso_Spec() {
    return recSpec(list(strSpec(), strSpec()));
  }

  public StructSpec personSpec() {
    return structSpec("Person", list(strSpec(), strSpec()), list("firstName", "lastName"));
  }

  public RecSpec fileSpec() {
    return recSpec(list(blobSpec(), strSpec()));
  }

  public StructSpec structSpec() {
    return structSpec(list(intSpec()), list("field"));
  }

  public StructSpec structSpec(
      ImmutableList<? extends ValSpec> fields, ImmutableList<String> names) {
    return structSpec("MyStruct", fields, names);
  }

  public StructSpec structSpec(String name, ImmutableList<? extends ValSpec> fields,
      ImmutableList<String> names) {
    return specDb().struct(name, fields, names);
  }

  public VariableSpec variableSpec(String name) {
    return specDb().variable(name);
  }

  // Expr Spec-s

  public ArrayExprSpec arrayExprSpec() {
    return arrayExprSpec(intSpec());
  }

  public ArrayExprSpec arrayExprSpec(ValSpec elementSpec) {
    return specDb().arrayExprSpec(elementSpec);
  }

  public CallSpec callSpec() {
    return callSpec(intSpec());
  }

  public CallSpec callSpec(ValSpec evaluationSpec) {
    return specDb().callSpec(evaluationSpec);
  }

  public ConstSpec constSpec() {
    return constSpec(intSpec());
  }

  public ConstSpec constSpec(ValSpec evaluationSpec) {
    return specDb().constSpec(evaluationSpec);
  }

  public InvokeSpec invokeSpec() {
    return invokeSpec(intSpec());
  }

  public InvokeSpec invokeSpec(ValSpec evaluationSpec) {
    return specDb().invokeSpec(evaluationSpec);
  }

  public NullSpec nullSpec() {
    return specDb().nullSpec();
  }

  public RecExprSpec recExprSpec() {
    return recExprSpec(list(intSpec(), strSpec()));
  }

  public RecExprSpec recExprSpec(ImmutableList<ValSpec> itemSpecs) {
    return specDb().recExprSpec(itemSpecs);
  }

  public SelectSpec selectSpec() {
    return selectSpec(intSpec());
  }

  public SelectSpec selectSpec(ValSpec evaluationSpec) {
    return specDb().selectSpec(evaluationSpec);
  }

  public RefSpec refSpec() {
    return refSpec(intSpec());
  }

  public RefSpec refSpec(ValSpec evaluationSpec) {
    return specDb().refSpec(evaluationSpec);
  }

  // Obj-s (values)

  public Struc_ animalVal() {
    return animalVal("rabbit", 7);
  }

  public Struc_ animalVal(String species, int speed) {
    return animalVal(strVal(species), intVal(speed));
  }

  public Struc_ animalVal(Str species, Int speed) {
    return structVal(animalSpec(), list(species, speed));
  }

  public Array arrayVal(Val... elements) {
    return arrayVal(elements[0].spec(), elements);
  }

  public Array arrayVal(ValSpec elementSpec, Obj... elements) {
    return objectDb().arrayBuilder(elementSpec).addAll(list(elements)).build();
  }

  public Blob blobVal() {
    return objectFactory().blob(sink -> sink.writeUtf8("blob data"));
  }

  public Blob blobVal(ByteString bytes) {
    return objectFactory().blob(sink -> sink.write(bytes));
  }

  public BlobBuilder blobBuilder() {
    return objectDb().blobBuilder();
  }

  public Bool boolVal(boolean value) {
    return objectDb().boolVal(value);
  }

  public Lambda lambdaVal() {
    return lambdaVal(intExpr());
  }

  public Lambda lambdaVal(Expr body) {
    LambdaSpec spec = lambdaSpec(body.evaluationSpec(), list(strSpec()));
    return lambdaVal(spec, body);
  }

  public Lambda lambdaVal(LambdaSpec spec, Expr body) {
    return objectDb().lambdaVal(spec, body);
  }

  public Int intVal() {
    return intVal(17);
  }

  public Int intVal(int value) {
    return objectDb().intVal(BigInteger.valueOf(value));
  }

  public Str strVal() {
    return objectDb().strVal("abc");
  }

  public Str strVal(String string) {
    return objectDb().strVal(string);
  }

  public Struc_ structVal(StructSpec recSpec, ImmutableList<Val> items) {
    return objectDb().structVal(recSpec, items);
  }

  public Rec recVal(List<? extends Val> items) {
    var recSpec = recSpec(map(items, Val::spec));
    return recVal(recSpec, items);
  }

  public Rec recVal(RecSpec recSpec, List<? extends Val> items) {
    return objectDb().recVal(recSpec, items);
  }

  public Rec emptyRecVal() {
    return recVal(list());
  }

  public Rec recWithStrVal() {
    return recVal(list(strVal("abc")));
  }

  public Rec recWithStrVal(Str str) {
    return recVal(list(str));
  }

  public Rec personVal(String firstName, String lastName) {
    return recVal(list(strVal(firstName), strVal(lastName)));
  }

  public Array messageArrayWithOneError() {
    return arrayVal(objectFactory().errorMessage("error message"));
  }

  public Array emptyMessageArray() {
    return arrayVal(objectFactory().messageSpec());
  }

  public Struc_ errorMessageV(String text) {
    return objectFactory().errorMessage(text);
  }

  public Struc_ warningMessageV(String text) {
    return objectFactory().warningMessage(text);
  }

  public Struc_ infoMessageV(String text) {
    return objectFactory().infoMessage(text);
  }

  public Struc_ fileVal(Path path) {
    return fileVal(path, ByteString.encodeString(path.toString(), CHARSET));
  }

  public Struc_ fileVal(Path path, ByteString content) {
    return fileVal(path.toString(), blobVal(content));
  }

  public Struc_ fileVal(String path, Blob blob) {
    Str string = objectFactory().string(path);
    return objectFactory().file(string, blob);
  }

  // Expr-s

  public Const boolExpr() {
    return constExpr(boolVal(true));
  }

  public Call callExpr(Expr function, ImmutableList<? extends Expr> arguments) {
    return objectDb().callExpr(function, recExpr(arguments));
  }

  public Const constExpr(Val val) {
    return objectDb().constExpr(val);
  }

  public ArrayExpr arrayExpr(List<? extends Expr> elements) {
    return objectDb().arrayExpr(elements);
  }

  public RecExpr recExpr(ImmutableList<? extends Expr> items) {
    return objectDb().recExpr(items);
  }

  public Select selectExpr(Expr rec, Int index) {
    return objectDb().selectExpr(rec, index);
  }

  public Const intExpr() {
    return intExpr(17);
  }

  public Const intExpr(int i) {
    return constExpr(intVal(i));
  }

  public Invoke invokeExpr(Blob jarFile, Str classBinaryName, ValSpec evaluationSpec) {
    return objectDb().invokeExpr(jarFile, classBinaryName, evaluationSpec);
  }

  public Null nullExpr() {
    return objectDb().nullExpr();
  }

  public Ref refExpr(int value) {
    return objectDb().refExpr(BigInteger.valueOf(value), intSpec());
  }

  public Ref refExpr(ValSpec evaluationSpec, int pointer) {
    return objectDb().refExpr(BigInteger.valueOf(pointer), evaluationSpec);
  }

  public Const strExpr() {
    return strExpr("abc");
  }

  public Const strExpr(String string) {
    return constExpr(strVal(string));
  }

  public Variable variable(String name) {
    return typing().variable(name);
  }

  public AnyType anyT() {
    return typing().any();
  }

  public ArrayType arrayT(Type elemType) {
    return typing().array(elemType);
  }

  public BlobType blobT() {
    return typing().blob();
  }

  public BoolType boolT() {
    return typing().bool();
  }

  public IntType intT() {
    return typing().int_();
  }

  public NothingType nothingT() {
    return typing().nothing();
  }

  public StringType stringT() {
    return typing().string();
  }

  public StructType structT(String name, ImmutableList<Type> fields) {
    var names = IntStream.range(0, fields.size())
        .mapToObj(Objects::toString)
        .collect(toImmutableList());
    return typing().struct(name, fields, names);
  }

  public StructType structT(String name, ImmutableList<Type> fields, ImmutableList<String> names) {
    return typing().struct(name, fields, names);
  }

  public FunctionType functionT(Type resultType, Item... parameters) {
    return typing().function(resultType, toTypes(list(parameters)));
  }

  public FunctionType functionT(Type resultType, Iterable<ItemSignature> parameters) {
    return typing().function(resultType, map(parameters, ItemSignature::type));
  }

  public Sides.Side lower() {
    return typing().lower();
  }

  public Sides.Side upper() {
    return typing().upper();
  }

  public BoundsMap bm(
      Variable var1, Side side1, Type bound1,
      Variable var2, Side side2, Type bound2) {
    Bounds bounds1 = typing().oneSideBound(side1, bound1);
    Bounds bounds2 = typing().oneSideBound(side2, bound2);
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
    return boundsMap(new Bounded(var, typing().oneSideBound(side, bound)));
  }

  public BoundsMap bm() {
    return boundsMap();
  }

  public BlobLiteralExpression blobExpression(int data) {
    return blobExpression(1, data);
  }

  public BlobLiteralExpression blobExpression(int line, int data) {
    return new BlobLiteralExpression(typing().blob(), ByteString.of((byte) data), loc(line));
  }

  public IntLiteralExpression intExpression(int value) {
    return intExpression(1, value);
  }

  public IntLiteralExpression intExpression(int line, int value) {
    return new IntLiteralExpression(typing().int_(), BigInteger.valueOf(value), loc(line));
  }

  public StringLiteralExpression stringExpression(int line, String data) {
    return new StringLiteralExpression(typing().string(), data, loc(line));
  }

  public ArrayLiteralExpression arrayExpression(
      int line, Type elemType, Expression... expressions) {
    return new ArrayLiteralExpression(
        typing().array(elemType), ImmutableList.copyOf(expressions), loc(line));
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
    Location loc = loc(line);
    var args = stream(arguments).map(Optional::of).collect(toImmutableList());
    return new CallExpression(type, expression, args, loc);
  }

  public NativeFunction functionExpression(Type type, String name, Item... parameters) {
    return functionExpression(
        1, type, name, annotationExpression(1, stringExpression(1, "Impl.met")), parameters);
  }

  public NativeFunction functionExpression(int line, Type type, String name,
      AnnotationExpression nativ, Item... parameters) {
    return new NativeFunction(functionT(type, parameters), modulePath(), name, list(parameters),
        nativ, loc(line)
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
      int line, Type type, String name, AnnotationExpression nativ) {
    return new NativeValue(type, modulePath(), name, nativ, loc(line));
  }

  public AnnotationExpression annotationExpression(
      int line, StringLiteralExpression implementedBy) {
    return annotationExpression(line, implementedBy, true);
  }

  public AnnotationExpression annotationExpression(
      int line, StringLiteralExpression implementedBy, boolean pure) {
    StructType type = typing().struct("Native",
        list(typing().string(), typing().blob()),
        list("path", "content"));
    return new AnnotationExpression(type, implementedBy, pure, loc(line));
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
