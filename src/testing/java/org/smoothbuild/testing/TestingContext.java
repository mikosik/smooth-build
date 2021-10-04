package org.smoothbuild.testing;

import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.lang.base.type.BoundsMap.boundsMap;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.math.BigInteger;
import java.util.List;

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
import org.smoothbuild.db.object.obj.expr.Null;
import org.smoothbuild.db.object.obj.expr.RecExpr;
import org.smoothbuild.db.object.obj.expr.Ref;
import org.smoothbuild.db.object.obj.expr.Select;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.BlobBuilder;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.DefinedLambda;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.NativeLambda;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.expr.ArrayExprSpec;
import org.smoothbuild.db.object.spec.expr.CallSpec;
import org.smoothbuild.db.object.spec.expr.ConstSpec;
import org.smoothbuild.db.object.spec.expr.NullSpec;
import org.smoothbuild.db.object.spec.expr.RecExprSpec;
import org.smoothbuild.db.object.spec.expr.RefSpec;
import org.smoothbuild.db.object.spec.expr.SelectSpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;
import org.smoothbuild.db.object.spec.val.BlobSpec;
import org.smoothbuild.db.object.spec.val.BoolSpec;
import org.smoothbuild.db.object.spec.val.DefinedLambdaSpec;
import org.smoothbuild.db.object.spec.val.IntSpec;
import org.smoothbuild.db.object.spec.val.NativeLambdaSpec;
import org.smoothbuild.db.object.spec.val.NothingSpec;
import org.smoothbuild.db.object.spec.val.RecSpec;
import org.smoothbuild.db.object.spec.val.StrSpec;
import org.smoothbuild.exec.compute.ComputationCache;
import org.smoothbuild.exec.compute.Computer;
import org.smoothbuild.exec.compute.Container;
import org.smoothbuild.install.TempManager;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.SynchronizedFileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.lang.base.type.Bounded;
import org.smoothbuild.lang.base.type.BoundsMap;
import org.smoothbuild.lang.base.type.Sides;
import org.smoothbuild.lang.base.type.Sides.Side;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.Typing;
import org.smoothbuild.lang.base.type.Variable;
import org.smoothbuild.plugin.NativeApi;

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
  private SpecDb specDb;
  private HashedDb hashedDb;
  private FileSystem hashedDbFileSystem;
  private FileSystem fullFileSystem;
  private TempManager tempManager;

  public NativeApi nativeApi() {
    return container();
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
      typing = new Typing(specDb());
    }
    return typing;
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

  public ArraySpec arraySpec(ValSpec elementSpec) {
    return specDb().arraySpec(elementSpec);
  }

  public BlobSpec blobSpec() {
    return specDb().blobSpec();
  }

  public BoolSpec boolSpec() {
    return specDb().boolSpec();
  }

  public DefinedLambdaSpec definedLambdaSpec() {
    return definedLambdaSpec(intSpec(), blobSpec(), strSpec());
  }

  public DefinedLambdaSpec definedLambdaSpec(ValSpec result, ValSpec... parameters) {
    return definedLambdaSpec(result, recSpec(list(parameters)));
  }

  public DefinedLambdaSpec definedLambdaSpec(ValSpec result, RecSpec parameters) {
    return specDb().definedLambdaSpec(result, parameters);
  }

  public IntSpec intSpec() {
    return specDb().intSpec();
  }

  public NativeLambdaSpec nativeLambdaSpec() {
    return nativeLambdaSpec(intSpec(), blobSpec(), strSpec());
  }

  public NativeLambdaSpec nativeLambdaSpec(ValSpec result, ValSpec... parameters) {
    return nativeLambdaSpec(result, recSpec(list(parameters)));
  }

  public NativeLambdaSpec nativeLambdaSpec(ValSpec result, RecSpec parameters) {
    return specDb().nativeLambdaSpec(result, parameters);
  }

  public NothingSpec nothingSpec() {
    return specDb().nothingSpec();
  }

  public StrSpec strSpec() {
    return specDb().strSpec();
  }

  public RecSpec recSpec(Iterable<? extends ValSpec> itemSpecs) {
    return specDb().recSpec(itemSpecs);
  }

  public RecSpec emptyRecSpec() {
    return recSpec(list());
  }

  public RecSpec recWithStrSpec() {
    return recSpec(list(strSpec()));
  }

  public RecSpec personSpec() {
    ValSpec string = strSpec();
    return recSpec(list(string, string));
  }

  public RecSpec fileSpec() {
    return recSpec(list(blobSpec(), strSpec()));
  }

  // Expr Spec-s

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

  public ArrayExprSpec arrayExprSpec() {
    return arrayExprSpec(intSpec());
  }

  public ArrayExprSpec arrayExprSpec(ValSpec elementSpec) {
    return specDb().arrayExprSpec(elementSpec);
  }

  public RecExprSpec recExprSpec() {
    return recExprSpec(list(intSpec(), strSpec()));
  }

  public RecExprSpec recExprSpec(Iterable<? extends ValSpec> itemSpecs) {
    return specDb().recExprSpec(itemSpecs);
  }

  public SelectSpec selectSpec() {
    return selectSpec(intSpec());
  }

  public SelectSpec selectSpec(ValSpec evaluationSpec) {
    return specDb().selectSpec(evaluationSpec);
  }

  public NullSpec nullSpec() {
    return specDb().nullSpec();
  }

  public RefSpec refSpec() {
    return refSpec(intSpec());
  }

  public RefSpec refSpec(ValSpec evaluationSpec) {
    return specDb().refSpec(evaluationSpec);
  }

  // Obj-s (values)

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

  public DefinedLambda definedLambdaVal() {
    return definedLambdaVal(intExpr());
  }

  public DefinedLambda definedLambdaVal(Expr body) {
    DefinedLambdaSpec spec = definedLambdaSpec(body.evaluationSpec(), strSpec());
    return definedLambdaVal(spec, body, list(strExpr()));
  }

  public DefinedLambda definedLambdaVal(
      DefinedLambdaSpec spec, Expr body, List<Expr> defaultArguments) {
    return objectDb().definedLambdaVal(spec, body, eRecExpr(defaultArguments));
  }

  public Int intVal() {
    return intVal(17);
  }

  public Int intVal(int value) {
    return objectDb().intVal(BigInteger.valueOf(value));
  }

  public NativeLambda nativeLambdaVal(
      NativeLambdaSpec spec, Str classBinaryName, Blob nativeJar, List<Expr> defaultArguments) {
    return objectDb().nativeLambdaVal(spec, classBinaryName, nativeJar, eRecExpr(defaultArguments));
  }

  public Str strVal() {
    return objectDb().strVal("abc");
  }

  public Str strVal(String string) {
    return objectDb().strVal(string);
  }

  public Rec recVal(List<? extends Val> items) {
    var recSpec = recSpec(map(items, Val::spec));
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

  public Rec errorMessageV(String text) {
    return objectFactory().errorMessage(text);
  }

  public Rec warningMessageV(String text) {
    return objectFactory().warningMessage(text);
  }

  public Rec infoMessageV(String text) {
    return objectFactory().infoMessage(text);
  }

  public Rec fileVal(Path path) {
    return fileVal(path, ByteString.encodeString(path.toString(), CHARSET));
  }

  public Rec fileVal(Path path, ByteString content) {
    return fileVal(path.toString(), blobVal(content));
  }

  public Rec fileVal(String path, Blob blob) {
    Str string = objectFactory().string(path);
    return objectFactory().file(string, blob);
  }

  // Expr-s

  public Const boolExpr() {
    return constExpr(boolVal(true));
  }

  public Call callExpr(Expr function, List<? extends Expr> arguments) {
    return objectDb().callExpr(function, eRecExpr(arguments));
  }

  public Const constExpr(Val val) {
    return objectDb().constExpr(val);
  }

  public ArrayExpr arrayExpr(Iterable<? extends Expr> elements) {
    return objectDb().arrayExpr(elements);
  }

  public RecExpr eRecExpr(Iterable<? extends Expr> items) {
    return objectDb().eRecExpr(items);
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

  public Sides.Side lower() {
    return typing().lower();
  }

  public Sides.Side upper() {
    return typing().upper();
  }

  public BoundsMap bm(
      Variable var1, Side side1, Type bound1,
      Variable var2, Side side2, Type bound2) {
    return
        typing().mergeWith(
            bm(var1, side1, bound1),
            list(new Bounded(var2, typing().oneSideBound(side2, bound2))));
  }

  public BoundsMap bm(Variable var, Side side, Type bound) {
    return boundsMap(new Bounded(var, typing().oneSideBound(side, bound)));
  }

  public BoundsMap bm() {
    return boundsMap();
  }
}
