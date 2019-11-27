package org.smoothbuild.testing;

import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.util.Lists.list;

import java.io.IOException;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.outputs.OutputsDb;
import org.smoothbuild.exec.task.Container;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.util.TempManager;
import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.ArrayBuilder;
import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.BlobBuilder;
import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.object.base.StructBuilder;
import org.smoothbuild.lang.object.db.ObjectFactory;
import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.lang.object.type.BlobType;
import org.smoothbuild.lang.object.type.BoolType;
import org.smoothbuild.lang.object.type.ConcreteArrayType;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.object.type.NothingType;
import org.smoothbuild.lang.object.type.StringType;
import org.smoothbuild.lang.object.type.StructType;
import org.smoothbuild.lang.object.type.TypeType;
import org.smoothbuild.lang.plugin.NativeApi;

import okio.ByteString;

public class TestingContext {
  private Container container;
  private ObjectFactory objectFactory;
  private ObjectFactory emptyCacheObjectFactory;
  private OutputsDb outputsDb;
  private FileSystem outputsDbFileSystem;
  private ObjectsDb objectsDb;
  private HashedDb hashedDb;
  private FileSystem hashedDbFileSystem;
  private MemoryFileSystem fullFileSystem;
  private TempManager tempManager;
  private FileSystem tempManagerFileSystem;

  public NativeApi nativeApi() {
    return container();
  }

  public Container container() {
    if (container == null) {
      container = new Container(fullFileSystem(), objectFactory(), tempManager());
    }
    return container;
  }


  /**
   * instance with File and Message types
   */
  public ObjectFactory objectFactory() {
    if (objectFactory == null) {
      objectFactory = new TestingObjectFactory(objectsDb());
    }
    return objectFactory;
  }

  /**
   * instance without File and Message types cached
   */
  public ObjectFactory emptyCacheObjectFactory() {
    if (emptyCacheObjectFactory == null) {
      emptyCacheObjectFactory = new ObjectFactory(objectsDb());
    }
    return emptyCacheObjectFactory;
  }

  public ObjectsDb objectsDb() {
    if (objectsDb == null) {
      objectsDb = ObjectsDb.objectsDb(hashedDb());
    }
    return objectsDb;
  }

  public OutputsDb outputsDb() {
    if (outputsDb == null) {
      outputsDb = new OutputsDb(outputsDbFileSystem(), objectsDb(), objectFactory());
    }
    return outputsDb;
  }

  public FileSystem outputsDbFileSystem() {
    if (outputsDbFileSystem == null) {
      outputsDbFileSystem = new MemoryFileSystem();
    }
    return outputsDbFileSystem;
  }

  public ObjectsDb objectsDbOther() {
    return ObjectsDb.objectsDb(hashedDb());
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
      hashedDbFileSystem = new MemoryFileSystem();
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
      tempManagerFileSystem = new MemoryFileSystem();
    }
    return tempManagerFileSystem;
  }

  public FileSystem fullFileSystem() {
    if (fullFileSystem == null) {
      fullFileSystem = new MemoryFileSystem();
    }
    return fullFileSystem;
  }

  public TypeType typeType() {
    return objectsDb().typeType();
  }

  public BoolType boolType() {
    return objectsDb().boolType();
  }

  public StringType stringType() {
    return objectsDb().stringType();
  }

  public BlobType blobType() {
    return objectsDb().blobType();
  }

  public NothingType nothingType() {
    return objectsDb().nothingType();
  }

  public ConcreteArrayType arrayType(ConcreteType elementType) {
    return objectsDb().arrayType(elementType);
  }

  public StructType structType(String name, Iterable<Field> fields) {
    return objectsDb().structType(name, fields);
  }

  public StructType personType() {
    ConcreteType string = stringType();
    return structType("Person", list(
        new Field(string, "firstName", unknownLocation()),
        new Field(string, "lastName", unknownLocation())));
  }

  public StructType fileType() {
    return structType("File", list(
        new Field(blobType(), "content", unknownLocation()),
        new Field(stringType(), "path", unknownLocation())));
  }

  public Bool bool(boolean value) {
    return objectsDb().bool(value);
  }

  public SString string(String string) {
    return objectsDb().string(string);
  }

  public BlobBuilder blobBuilder() {
    return objectsDb().blobBuilder();
  }

  public ArrayBuilder arrayBuilder(ConcreteType elemType) {
    return objectsDb().arrayBuilder(elemType);
  }

  public StructBuilder structBuilder(StructType type) {
    return objectsDb().structBuilder(type);
  }

  public Struct person(String firstName, String lastName) {
    return structBuilder(personType())
        .set("firstName", string(firstName))
        .set("lastName", string(lastName))
        .build();
  }

  public Array messageArrayWithOneError() {
    return array(objectFactory().errorMessage("error message"));
  }

  public Array emptyMessageArray() {
    return array(objectFactory().messageType());
  }

  public <T extends SObject> Array array(SObject... elements) {
    return array(elements[0].type(), elements);
  }

  public <T extends SObject> Array array(ConcreteType elementType, SObject... elements) {
    return objectsDb().arrayBuilder(elementType).addAll(list(elements)).build();
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

  public Struct file(Path path) {
    return file(path, ByteString.encodeString(path.value(), CHARSET));
  }

  public Struct file(Path path, ByteString content) {
    SString string = objectFactory().string(path.value());
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

  public static class TestingObjectFactory extends ObjectFactory {
    public TestingObjectFactory(ObjectsDb objectsDb) {
      super(objectsDb);
      structType("File", list(
          new Field(blobType(), "content", unknownLocation()),
          new Field(stringType(), "path", unknownLocation())));
      structType("Message", list(
          new Field(stringType(), "text", unknownLocation()),
          new Field(stringType(), "severity", unknownLocation())));
    }
  }
}
