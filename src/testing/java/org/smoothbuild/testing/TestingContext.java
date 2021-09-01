package org.smoothbuild.testing;

import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.util.Lists.list;

import java.math.BigInteger;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.expr.Call;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.obj.expr.EArray;
import org.smoothbuild.db.object.obj.expr.FieldRead;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.BlobBuilder;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.obj.val.Tuple;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.expr.CallSpec;
import org.smoothbuild.db.object.spec.expr.ConstSpec;
import org.smoothbuild.db.object.spec.expr.EArraySpec;
import org.smoothbuild.db.object.spec.expr.FieldReadSpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;
import org.smoothbuild.db.object.spec.val.BlobSpec;
import org.smoothbuild.db.object.spec.val.BoolSpec;
import org.smoothbuild.db.object.spec.val.IntSpec;
import org.smoothbuild.db.object.spec.val.NothingSpec;
import org.smoothbuild.db.object.spec.val.StrSpec;
import org.smoothbuild.db.object.spec.val.TupleSpec;
import org.smoothbuild.exec.compute.ComputationCache;
import org.smoothbuild.exec.compute.Computer;
import org.smoothbuild.exec.compute.Container;
import org.smoothbuild.install.TempManager;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.SynchronizedFileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
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
      objectFactory = new ObjectFactory(objectDb());
    }
    return objectFactory;
  }

  public ObjectDb objectDb() {
    if (objectDb == null) {
      objectDb = ObjectDb.objectDb(hashedDb());
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
    return ObjectDb.objectDb(hashedDb());
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
    return objectDb().arrayS(elementSpec);
  }

  public BlobSpec blobSpec() {
    return objectDb().blobS();
  }

  public BoolSpec boolSpec() {
    return objectDb().boolS();
  }

  public IntSpec intSpec() {
    return objectDb().intS();
  }

  public NothingSpec nothingSpec() {
    return objectDb().nothingS();
  }

  public StrSpec strSpec() {
    return objectDb().strS();
  }

  public TupleSpec tupleSpec(Iterable<? extends ValSpec> elementSpecs) {
    return objectDb().tupleS(elementSpecs);
  }

  public TupleSpec emptyTupleSpec() {
    return tupleSpec(list());
  }

  public TupleSpec tupleWithStrSpec() {
    return tupleSpec(list(strSpec()));
  }

  public TupleSpec personSpec() {
    ValSpec string = strSpec();
    return tupleSpec(list(string, string));
  }

  public TupleSpec fileSpec() {
    return tupleSpec(list(blobSpec(), strSpec()));
  }

  // Expr Spec-s

  public CallSpec callSpec() {
    return objectDb().callS();
  }

  public ConstSpec constSpec() {
    return objectDb().constS();
  }

  public EArraySpec eArraySpec() {
    return objectDb().eArrayS();
  }

  public FieldReadSpec fieldReadSpec() {
    return objectDb().fieldReadS();
  }

  // Obj-s (values)

  public Array arrayVal(Val... elements) {
    return arrayVal(elements[0].spec(), elements);
  }

  public Array arrayVal(ValSpec elementSpec, Obj... elements) {
    return objectDb().arrayBuilder(elementSpec).addAll(list(elements)).build();
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

  public Int intVal(int value) {
    return objectDb().intVal(BigInteger.valueOf(value));
  }

  public Str strVal(String string) {
    return objectDb().strVal(string);
  }

  public Tuple tupleVal(TupleSpec spec, Iterable<? extends Obj> elements) {
    return objectDb().tupleVal(spec, elements);
  }

  public Tuple emptyTupleVal() {
    return tupleVal(emptyTupleSpec(), list());
  }

  public Tuple tupleWithStrVal() {
    return tupleVal(tupleWithStrSpec(), list(strVal("abc")));
  }

  public Tuple tupleWithStrVal(Str str) {
    return tupleVal(tupleWithStrSpec(), list(str));
  }

  public Tuple personVal(String firstName, String lastName) {
    return tupleVal(personSpec(), list(strVal(firstName), strVal(lastName)));
  }

  public Array messageArrayWithOneError() {
    return arrayVal(objectFactory().errorMessage("error message"));
  }

  public Array emptyMessageArray() {
    return arrayVal(objectFactory().messageSpec());
  }

  public Tuple errorMessageV(String text) {
    return objectFactory().errorMessage(text);
  }

  public Tuple warningMessageV(String text) {
    return objectFactory().warningMessage(text);
  }

  public Tuple infoMessageV(String text) {
    return objectFactory().infoMessage(text);
  }

  public Tuple fileVal(Path path) {
    return fileVal(path, ByteString.encodeString(path.toString(), CHARSET));
  }

  public Tuple fileVal(Path path, ByteString content) {
    return fileVal(path.toString(), blobVal(content));
  }

  public Tuple fileVal(String path, Blob blob) {
    Str string = objectFactory().string(path);
    return objectFactory().file(string, blob);
  }

  // Expr-s

  public Call callExpr(Expr function, Iterable<? extends Expr> arguments) {
    return objectDb().callExpr(function, arguments);
  }

  public Const constExpr() {
    return objectDb().constExpr(intVal(123));
  }

  public Const constExpr(Val val) {
    return objectDb().constExpr(val);
  }

  public EArray eArrayExpr(Iterable<? extends Expr> elements) {
    return objectDb().eArrayExpr(elements);
  }

  public FieldRead fieldReadExpr(Expr tuple, Int index) {
    return objectDb().fieldReadExpr(tuple, index);
  }
}
