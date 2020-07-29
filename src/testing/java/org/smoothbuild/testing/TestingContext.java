package org.smoothbuild.testing;

import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.util.Lists.list;

import java.io.IOException;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.outputs.ComputationCache;
import org.smoothbuild.exec.task.base.Computer;
import org.smoothbuild.exec.task.base.Container;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.SynchronizedFileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.util.TempManager;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.record.base.Array;
import org.smoothbuild.record.base.ArrayBuilder;
import org.smoothbuild.record.base.Blob;
import org.smoothbuild.record.base.BlobBuilder;
import org.smoothbuild.record.base.Bool;
import org.smoothbuild.record.base.RString;
import org.smoothbuild.record.base.Record;
import org.smoothbuild.record.base.Tuple;
import org.smoothbuild.record.db.RecordDb;
import org.smoothbuild.record.db.RecordFactory;
import org.smoothbuild.record.spec.ArraySpec;
import org.smoothbuild.record.spec.BlobSpec;
import org.smoothbuild.record.spec.BoolSpec;
import org.smoothbuild.record.spec.NothingSpec;
import org.smoothbuild.record.spec.Spec;
import org.smoothbuild.record.spec.SpecSpec;
import org.smoothbuild.record.spec.StringSpec;
import org.smoothbuild.record.spec.TupleSpec;

import com.google.common.collect.ImmutableList;
import com.google.inject.util.Providers;

import okio.ByteString;

public class TestingContext {
  private Computer computer;
  private Container container;
  private RecordFactory recordFactory;
  private ComputationCache computationCache;
  private FileSystem outputDbFileSystem;
  private RecordDb recordDb;
  private HashedDb hashedDb;
  private FileSystem hashedDbFileSystem;
  private FileSystem fullFileSystem;
  private TempManager tempManager;
  private FileSystem tempManagerFileSystem;

  public NativeApi nativeApi() {
    return container();
  }

  public Computer computer() {
    if (computer == null) {
      computer = new Computer(outputDb(), Hash.of(123), Providers.of(newContainer()));
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
    return new Container(fullFileSystem(), recordFactory(), tempManager());
  }

  public RecordFactory recordFactory() {
    if (recordFactory == null) {
      recordFactory = new RecordFactory(recordDb());
    }
    return recordFactory;
  }

  public RecordDb recordDb() {
    if (recordDb == null) {
      recordDb = RecordDb.recordDb(hashedDb());
    }
    return recordDb;
  }

  public ComputationCache outputDb() {
    if (computationCache == null) {
      computationCache = new ComputationCache(outputDbFileSystem(), recordDb(), recordFactory());
    }
    return computationCache;
  }

  public FileSystem outputDbFileSystem() {
    if (outputDbFileSystem == null) {
      outputDbFileSystem = new MemoryFileSystem();
    }
    return outputDbFileSystem;
  }

  public RecordDb recordDbOther() {
    return RecordDb.recordDb(hashedDb());
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
      tempManager = new TempManager(tempManagerFileSystem());
    }
    return tempManager;
  }

  public FileSystem tempManagerFileSystem() {
    if (tempManagerFileSystem == null) {
      tempManagerFileSystem = new SynchronizedFileSystem(new MemoryFileSystem());
    }
    return tempManagerFileSystem;
  }

  public FileSystem fullFileSystem() {
    if (fullFileSystem == null) {
      fullFileSystem = new SynchronizedFileSystem(new MemoryFileSystem());
    }
    return fullFileSystem;
  }

  public SpecSpec specSpec() {
    return recordDb().specSpec();
  }

  public BoolSpec boolSpec() {
    return recordDb().boolSpec();
  }

  public StringSpec stringSpec() {
    return recordDb().stringSpec();
  }

  public BlobSpec blobSpec() {
    return recordDb().blobSpec();
  }

  public NothingSpec nothingSpec() {
    return recordDb().nothingSpec();
  }

  public ArraySpec arraySpec(Spec elementSpec) {
    return recordDb().arraySpec(elementSpec);
  }

  public TupleSpec tupleSpec(Iterable<? extends Spec> elementSpecs) {
    return recordDb().tupleSpec(elementSpecs);
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
    return recordDb().bool(value);
  }

  public RString string(String string) {
    return recordDb().string(string);
  }

  public BlobBuilder blobBuilder() {
    return recordDb().blobBuilder();
  }

  public ArrayBuilder arrayBuilder(Spec elemSpec) {
    return recordDb().arrayBuilder(elemSpec);
  }

  public Tuple tuple(TupleSpec spec, Iterable<? extends Record> elements) {
    return recordDb().tuple(spec, elements);
  }

  public Tuple empty() {
    return tuple(emptySpec(), ImmutableList.of());
  }

  public Tuple person(String firstName, String lastName) {
    return tuple(personSpec(), ImmutableList.of(string(firstName), string(lastName)));
  }

  public Array messageArrayWithOneError() {
    return array(recordFactory().errorMessage("error message"));
  }

  public Array emptyMessageArray() {
    return array(recordFactory().messageSpec());
  }

  public Array array(Record... elements) {
    return array(elements[0].spec(), elements);
  }

  public Array array(Spec elementSpec, Record... elements) {
    return recordDb().arrayBuilder(elementSpec).addAll(list(elements)).build();
  }

  public Record errorMessage(String text) {
    return recordFactory().errorMessage(text);
  }

  public Record warningMessage(String text) {
    return recordFactory().warningMessage(text);
  }

  public Record infoMessage(String text) {
    return recordFactory().infoMessage(text);
  }

  public Tuple file(Path path) {
    return file(path, ByteString.encodeString(path.toString(), CHARSET));
  }

  public Tuple file(Path path, ByteString content) {
    RString string = recordFactory().string(path.toString());
    Blob blob = blob(content);
    return recordFactory().file(string, blob);
  }

  public Blob blob(ByteString bytes) {
    try {
      return recordFactory().blob(sink -> sink.write(bytes));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
