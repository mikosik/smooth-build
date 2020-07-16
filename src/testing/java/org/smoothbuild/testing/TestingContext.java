package org.smoothbuild.testing;

import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.util.Lists.list;

import java.io.IOException;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.outputs.OutputDb;
import org.smoothbuild.exec.task.base.Computer;
import org.smoothbuild.exec.task.base.Container;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.SynchronizedFileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.util.TempManager;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.ArrayBuilder;
import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.BlobBuilder;
import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.base.Tuple;
import org.smoothbuild.lang.object.db.ObjectDb;
import org.smoothbuild.lang.object.db.ObjectFactory;
import org.smoothbuild.lang.object.type.ArrayType;
import org.smoothbuild.lang.object.type.BinaryType;
import org.smoothbuild.lang.object.type.BlobType;
import org.smoothbuild.lang.object.type.BoolType;
import org.smoothbuild.lang.object.type.NothingType;
import org.smoothbuild.lang.object.type.StringType;
import org.smoothbuild.lang.object.type.TupleType;
import org.smoothbuild.lang.object.type.TypeType;
import org.smoothbuild.lang.plugin.NativeApi;

import com.google.common.collect.ImmutableList;
import com.google.inject.util.Providers;

import okio.ByteString;

public class TestingContext {
  private Computer computer;
  private Container container;
  private ObjectFactory objectFactory;
  private OutputDb outputDb;
  private FileSystem outputDbFileSystem;
  private ObjectDb objectDb;
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
    return new Container(fullFileSystem(), objectFactory(), tempManager());
  }

  /**
   * instance with File and Message types
   */
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

  public OutputDb outputDb() {
    if (outputDb == null) {
      outputDb = new OutputDb(outputDbFileSystem(), objectDb(), objectFactory());
    }
    return outputDb;
  }

  public FileSystem outputDbFileSystem() {
    if (outputDbFileSystem == null) {
      outputDbFileSystem = new MemoryFileSystem();
    }
    return outputDbFileSystem;
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

  public TypeType typeType() {
    return objectDb().typeType();
  }

  public BoolType boolType() {
    return objectDb().boolType();
  }

  public StringType stringType() {
    return objectDb().stringType();
  }

  public BlobType blobType() {
    return objectDb().blobType();
  }

  public NothingType nothingType() {
    return objectDb().nothingType();
  }

  public ArrayType arrayType(BinaryType elementType) {
    return objectDb().arrayType(elementType);
  }

  public TupleType structType(Iterable<? extends BinaryType> fieldTypes) {
    return objectDb().structType(fieldTypes);
  }

  public TupleType emptyType() {
    return structType(list());
  }

  public TupleType personType() {
    BinaryType string = stringType();
    return structType(list(string, string));
  }

  public TupleType fileType() {
    return structType(list(blobType(), stringType()));
  }

  public Bool bool(boolean value) {
    return objectDb().bool(value);
  }

  public SString string(String string) {
    return objectDb().string(string);
  }

  public BlobBuilder blobBuilder() {
    return objectDb().blobBuilder();
  }

  public ArrayBuilder arrayBuilder(BinaryType elemType) {
    return objectDb().arrayBuilder(elemType);
  }

  public Tuple struct(TupleType type, Iterable<? extends SObject> fields) {
    return objectDb().struct(type, fields);
  }

  public Tuple empty() {
    return struct(emptyType(), ImmutableList.of());
  }

  public Tuple person(String firstName, String lastName) {
    return struct(personType(), ImmutableList.of(string(firstName), string(lastName)));
  }

  public Array messageArrayWithOneError() {
    return array(objectFactory().errorMessage("error message"));
  }

  public Array emptyMessageArray() {
    return array(objectFactory().messageType());
  }

  public Array array(SObject... elements) {
    return array(elements[0].type(), elements);
  }

  public Array array(BinaryType elementType, SObject... elements) {
    return objectDb().arrayBuilder(elementType).addAll(list(elements)).build();
  }

  public SObject errorMessage(String text) {
    return objectFactory().errorMessage(text);
  }

  public SObject warningMessage(String text) {
    return objectFactory().warningMessage(text);
  }

  public SObject infoMessage(String text) {
    return objectFactory().infoMessage(text);
  }

  public Tuple file(Path path) {
    return file(path, ByteString.encodeString(path.toString(), CHARSET));
  }

  public Tuple file(Path path, ByteString content) {
    SString string = objectFactory().string(path.toString());
    Blob blob = blob(content);
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
