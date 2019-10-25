package org.smoothbuild.testing;

import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.util.Lists.list;

import java.io.IOException;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.outputs.OutputsDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.util.TempManager;
import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Types;
import org.smoothbuild.lang.runtime.RuntimeTypes;
import org.smoothbuild.lang.type.BlobType;
import org.smoothbuild.lang.type.BoolType;
import org.smoothbuild.lang.type.ConcreteArrayType;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.NothingType;
import org.smoothbuild.lang.type.StringType;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.TypeType;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.Bool;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.lang.value.StructBuilder;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.lang.value.ValueFactory;
import org.smoothbuild.task.exec.Container;

import okio.ByteString;

public class TestingContext {
  private ValueFactory valueFactory;
  private Container container;
  private TestingRuntimeTypes types;
  private RuntimeTypes runtimeTypes;
  private ValuesDb valuesDb;
  private OutputsDb outputsDb;
  private HashedDb hashedDbValues;
  private HashedDb hashedDbOutputs;
  private FileSystem hashedDbValuesFileSystem;
  private FileSystem hashedDbOutputsFileSystem;
  private MemoryFileSystem fullFileSystem;

  public NativeApi nativeApi() {
    return container();
  }

  public Container container() {
    if (container == null) {
      container = new Container(fullFileSystem(), valueFactory(), types(),
          new TempManager(new MemoryFileSystem()));
    }
    return container;
  }

  public ValueFactory valueFactory() {
    if (valueFactory == null) {
      valueFactory = new ValueFactory(types(), valuesDb());
    }
    return valueFactory;
  }

  public Types types() {
    if (types == null) {
      types = new TestingRuntimeTypes(valuesDb());
    }
    return types;
  }

  public RuntimeTypes runtimeTypes() {
    if (runtimeTypes == null) {
      runtimeTypes = new RuntimeTypes(valuesDb());
    }
    return runtimeTypes;
  }

  public ValuesDb valuesDb() {
    if (valuesDb == null) {
      valuesDb = new ValuesDb(hashedDb());
    }
    return valuesDb;
  }

  public OutputsDb outputsDb() {
    if (outputsDb == null) {
      outputsDb = new OutputsDb(hashedDbOutputs(), valuesDb(), types());
    }
    return outputsDb;
  }

  public ValuesDb valuesDbOther() {
    return new ValuesDb(hashedDb());
  }

  public HashedDb hashedDb() {
    if (hashedDbValues == null) {
      hashedDbValues = new HashedDb(
          hashedDbFileSystem(), Path.root(), new TempManager(new MemoryFileSystem()));
    }
    return hashedDbValues;
  }

  public HashedDb hashedDbOutputs() {
    if (hashedDbOutputs == null) {
      hashedDbOutputs = new HashedDb(
          hashedDbOutputsFileSystem(), Path.root(), new TempManager(new MemoryFileSystem()));
    }
    return hashedDbOutputs;
  }

  private FileSystem hashedDbFileSystem() {
    if (hashedDbValuesFileSystem == null) {
      hashedDbValuesFileSystem = new MemoryFileSystem();
    }
    return hashedDbValuesFileSystem;
  }

  private FileSystem hashedDbOutputsFileSystem() {
    if (hashedDbOutputsFileSystem == null) {
      hashedDbOutputsFileSystem = new MemoryFileSystem();
    }
    return hashedDbOutputsFileSystem;
  }

  public FileSystem fullFileSystem() {
    if (fullFileSystem == null) {
      fullFileSystem = new MemoryFileSystem();
    }
    return fullFileSystem;
  }

  public TypeType typeType() {
    return valuesDb().typeType();
  }

  public BoolType boolType() {
    return valuesDb().boolType();
  }

  public StringType stringType() {
    return valuesDb().stringType();
  }

  public BlobType blobType() {
    return valuesDb().blobType();
  }

  public NothingType nothingType() {
    return valuesDb().nothingType();
  }

  public ConcreteArrayType arrayType(ConcreteType elementType) {
    return valuesDb().arrayType(elementType);
  }

  public StructType structType(String name, Iterable<Field> fields) {
    return valuesDb().structType(name, fields);
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
    return valuesDb().bool(value);
  }

  public SString string(String string) {
    return valuesDb().string(string);
  }

  public BlobBuilder blobBuilder() {
    return valuesDb().blobBuilder();
  }

  public ArrayBuilder arrayBuilder(ConcreteType elemType) {
    return valuesDb().arrayBuilder(elemType);
  }

  public StructBuilder structBuilder(StructType type) {
    return valuesDb().structBuilder(type);
  }

  public Struct person(String firstName, String lastName) {
    return structBuilder(personType())
        .set("firstName", string(firstName))
        .set("lastName", string(lastName))
        .build();
  }

  public Array messageArrayWithOneError() {
    return array(valueFactory().errorMessage("error message"));
  }

  public Array emptyMessageArray() {
    return array(types().message());
  }

  public <T extends Value> Array array(Value... elements) {
    return array(elements[0].type(), elements);
  }

  public <T extends Value> Array array(ConcreteType elementType, Value... elements) {
    return valuesDb().arrayBuilder(elementType).addAll(list(elements)).build();
  }

  public  Value errorMessage(String text) {
    return valueFactory().errorMessage(text);
  }

  public Value warningMessage(String text) {
    return valueFactory().warningMessage(text);
  }

  public Value infoMessage(String text) {
    return valueFactory().infoMessage(text);
  }

  public Struct file(Path path) {
    return file(path, ByteString.encodeString(path.value(), CHARSET));
  }

  public Struct file(Path path, ByteString content) {
    SString string = valueFactory().string(path.value());
    Blob blob = blob(content);
    return valueFactory().file(string, blob);
  }

  public Blob blob(ByteString bytes) {
    try {
      return valueFactory().blob(sink -> sink.write(bytes));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static class TestingRuntimeTypes extends RuntimeTypes {
    public TestingRuntimeTypes(ValuesDb valuesDb) {
      super(valuesDb);
      struct("File", list(
          new Field(blob(), "content", unknownLocation()),
          new Field(string(), "path", unknownLocation())));
      struct("Message", list(
          new Field(string(), "text", unknownLocation()),
          new Field(string(), "severity", unknownLocation())));
    }
  }
}
