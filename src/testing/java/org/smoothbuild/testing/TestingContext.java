package org.smoothbuild.testing;

import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.util.Lists.list;

import java.io.IOException;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.ArrayBuilder;
import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.base.BlobBuilder;
import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.db.object.spec.ArraySpec;
import org.smoothbuild.db.object.spec.BlobSpec;
import org.smoothbuild.db.object.spec.BoolSpec;
import org.smoothbuild.db.object.spec.NothingSpec;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.db.object.spec.StringSpec;
import org.smoothbuild.db.object.spec.TupleSpec;
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

  public BoolSpec boolSpec() {
    return objectDb().boolSpec();
  }

  public StringSpec stringSpec() {
    return objectDb().stringSpec();
  }

  public BlobSpec blobSpec() {
    return objectDb().blobSpec();
  }

  public NothingSpec nothingSpec() {
    return objectDb().nothingSpec();
  }

  public ArraySpec arraySpec(Spec elementSpec) {
    return objectDb().arraySpec(elementSpec);
  }

  public TupleSpec tupleSpec(Iterable<? extends Spec> elementSpecs) {
    return objectDb().tupleSpec(elementSpecs);
  }

  public TupleSpec emptySpec() {
    return tupleSpec(list());
  }

  public TupleSpec personSpec() {
    Spec string = stringSpec();
    return tupleSpec(list(string, string));
  }

  public TupleSpec fileSpec() {
    return tupleSpec(list(blobSpec(), stringSpec()));
  }

  public Bool bool(boolean value) {
    return objectDb().bool(value);
  }

  public Str string(String string) {
    return objectDb().string(string);
  }

  public BlobBuilder blobBuilder() {
    return objectDb().blobBuilder();
  }

  public ArrayBuilder arrayBuilder(Spec elemSpec) {
    return objectDb().arrayBuilder(elemSpec);
  }

  public Tuple tuple(TupleSpec spec, Iterable<? extends Obj> elements) {
    return objectDb().tuple(spec, elements);
  }

  public Tuple empty() {
    return tuple(emptySpec(), list());
  }

  public Tuple person(String firstName, String lastName) {
    return tuple(personSpec(), list(string(firstName), string(lastName)));
  }

  public Array messageArrayWithOneError() {
    return array(objectFactory().errorMessage("error message"));
  }

  public Array emptyMessageArray() {
    return array(objectFactory().messageSpec());
  }

  public Array array(Obj... elements) {
    return array(elements[0].spec(), elements);
  }

  public Array array(Spec elementSpec, Obj... elements) {
    return objectDb().arrayBuilder(elementSpec).addAll(list(elements)).build();
  }

  public Obj errorMessage(String text) {
    return objectFactory().errorMessage(text);
  }

  public Obj warningMessage(String text) {
    return objectFactory().warningMessage(text);
  }

  public Obj infoMessage(String text) {
    return objectFactory().infoMessage(text);
  }

  public Tuple file(Path path) {
    return file(path, ByteString.encodeString(path.toString(), CHARSET));
  }

  public Tuple file(Path path, ByteString content) {
    return file(path.toString(), blob(content));
  }

  public Tuple file(String path, Blob blob) {
    Str string = objectFactory().string(path);
    return objectFactory().file(string, blob);
  }

  public Blob blob(ByteString bytes) {
    try {
      return objectFactory().blob(sink -> sink.write(bytes));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
