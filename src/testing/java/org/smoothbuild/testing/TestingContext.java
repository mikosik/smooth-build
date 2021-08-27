package org.smoothbuild.testing;

import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.util.Lists.list;

import java.math.BigInteger;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.ArrayBuilder;
import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.base.BlobBuilder;
import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.db.object.base.Call;
import org.smoothbuild.db.object.base.Const;
import org.smoothbuild.db.object.base.EArray;
import org.smoothbuild.db.object.base.Expr;
import org.smoothbuild.db.object.base.FieldRead;
import org.smoothbuild.db.object.base.Int;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.db.object.base.Val;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.db.object.spec.ArraySpec;
import org.smoothbuild.db.object.spec.BlobSpec;
import org.smoothbuild.db.object.spec.BoolSpec;
import org.smoothbuild.db.object.spec.CallSpec;
import org.smoothbuild.db.object.spec.ConstSpec;
import org.smoothbuild.db.object.spec.EArraySpec;
import org.smoothbuild.db.object.spec.FieldReadSpec;
import org.smoothbuild.db.object.spec.IntSpec;
import org.smoothbuild.db.object.spec.NothingSpec;
import org.smoothbuild.db.object.spec.StrSpec;
import org.smoothbuild.db.object.spec.TupleSpec;
import org.smoothbuild.db.object.spec.ValSpec;
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

  public ArraySpec arrayS(ValSpec elementSpec) {
    return objectDb().arrayS(elementSpec);
  }

  public BlobSpec blobS() {
    return objectDb().blobS();
  }

  public BoolSpec boolS() {
    return objectDb().boolS();
  }

  public IntSpec intS() {
    return objectDb().intS();
  }

  public NothingSpec nothingS() {
    return objectDb().nothingS();
  }

  public StrSpec strS() {
    return objectDb().strS();
  }

  public TupleSpec tupleS(Iterable<? extends ValSpec> elementSpecs) {
    return objectDb().tupleS(elementSpecs);
  }

  public TupleSpec emptyTupleS() {
    return tupleS(list());
  }

  public TupleSpec tupleWithStrS() {
    return tupleS(list(strS()));
  }

  public TupleSpec personS() {
    ValSpec string = strS();
    return tupleS(list(string, string));
  }

  public TupleSpec fileS() {
    return tupleS(list(blobS(), strS()));
  }

  // Expr Spec-s

  public CallSpec callS() {
    return objectDb().callS();
  }

  public ConstSpec constS() {
    return objectDb().constS();
  }

  public EArraySpec eArrayS() {
    return objectDb().eArrayS();
  }

  public FieldReadSpec fieldReadS() {
    return objectDb().fieldReadS();
  }

  // Obj-s (values)

  public Array arrayV(Val... elements) {
    return arrayV(elements[0].spec(), elements);
  }

  public Array arrayV(ValSpec elementSpec, Obj... elements) {
    return objectDb().arrayBuilder(elementSpec).addAll(list(elements)).build();
  }

  public ArrayBuilder arrayBuilder(ValSpec elemSpec) {
    return objectDb().arrayBuilder(elemSpec);
  }

  public Blob blobV(ByteString bytes) {
    return objectFactory().blob(sink -> sink.write(bytes));
  }

  public BlobBuilder blobBuilder() {
    return objectDb().blobBuilder();
  }

  public Bool boolV(boolean value) {
    return objectDb().boolV(value);
  }

  public Int intV(int value) {
    return objectDb().intV(BigInteger.valueOf(value));
  }

  public Str strV(String string) {
    return objectDb().strV(string);
  }

  public Tuple tupleV(TupleSpec spec, Iterable<? extends Obj> elements) {
    return objectDb().tupleV(spec, elements);
  }

  public Tuple emptyTupleV() {
    return tupleV(emptyTupleS(), list());
  }

  public Tuple tupleWithStrV() {
    return tupleV(tupleWithStrS(), list(strV("abc")));
  }

  public Tuple tupleWithStrV(Str str) {
    return tupleV(tupleWithStrS(), list(str));
  }

  public Tuple personV(String firstName, String lastName) {
    return tupleV(personS(), list(strV(firstName), strV(lastName)));
  }

  public Array messageArrayWithOneError() {
    return arrayV(objectFactory().errorMessage("error message"));
  }

  public Array emptyMessageArray() {
    return arrayV(objectFactory().messageSpec());
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

  public Tuple fileV(Path path) {
    return fileV(path, ByteString.encodeString(path.toString(), CHARSET));
  }

  public Tuple fileV(Path path, ByteString content) {
    return fileV(path.toString(), blobV(content));
  }

  public Tuple fileV(String path, Blob blob) {
    Str string = objectFactory().string(path);
    return objectFactory().file(string, blob);
  }

  // Expr-s

  public Call callE(Expr function, Iterable<? extends Expr> arguments) {
    return objectDb().callExpr(function, arguments);
  }

  public Const constE() {
    return objectDb().constExpr(intV(123));
  }

  public Const constE(Val val) {
    return objectDb().constExpr(val);
  }

  public EArray eArray(Iterable<? extends Expr> elements) {
    return objectDb().eArrayExpr(elements);
  }

  public FieldRead fieldReadE(Expr tuple, Int index) {
    return objectDb().fieldReadExpr(tuple, index);
  }
}
