package org.smoothbuild.testing.db.values;

import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.util.Lists.list;

import java.io.IOException;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.db.values.TestingValuesDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.runtime.TestingRuntimeTypes;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.lang.value.TestingValueFactory;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.lang.value.ValueFactory;

import okio.ByteString;

public class ValueCreators {
  public static <T extends Value> Array array(HashedDb hashedDb, Value... elements) {
    return array(new TestingValuesDb(hashedDb), elements[0].type(), elements);
  }

  public static <T extends Value> Array array(HashedDb hashedDb, ConcreteType elementType,
      Value... elements) {
    return array(new TestingValuesDb(hashedDb), elementType, elements);
  }

  public static <T extends Value> Array array(ValuesDb valuesDb, ConcreteType elementType,
      Value... elements) {
    return valuesDb.arrayBuilder(elementType).addAll(list(elements)).build();
  }

  public static Array messageArrayWithOneError() {
    TestingHashedDb hashedDb = new TestingHashedDb();
    return array(hashedDb, new TestingValueFactory(hashedDb).errorMessage("error message"));
  }

  public static Array emptyMessageArray() {
    TestingHashedDb hashedDb = new TestingHashedDb();
    TestingRuntimeTypes testingRuntimeTypes = new TestingRuntimeTypes(new ValuesDb(hashedDb));
    return array(hashedDb, testingRuntimeTypes.message());
  }

  public static Value errorMessage(HashedDb hashedDb, String text) {
    return new TestingValueFactory(hashedDb).errorMessage(text);
  }

  public static Value warningMessage(HashedDb hashedDb, String text) {
    return new TestingValueFactory(hashedDb).warningMessage(text);
  }

  public static Value infoMessage(HashedDb hashedDb, String text) {
    return new TestingValueFactory(hashedDb).infoMessage(text);
  }

  public static Struct file(HashedDb hashedDb, Path path) {
    return file(new TestingValueFactory(hashedDb), path);
  }

  public static Struct file(Path path) {
    return file(new TestingValueFactory(), path);
  }

  public static Struct file(ValueFactory valueFactory, Path path) {
    return file(valueFactory, path, ByteString.encodeString(path.value(), CHARSET));
  }

  public static Struct file(Path path, ByteString content) {
    ValueFactory valueFactory = new TestingValueFactory();
    SString string = valueFactory.string(path.value());
    Blob blob = blob(valueFactory, content);
    return valueFactory.file(string, blob);
  }

  public static Struct file(ValueFactory valueFactory, Path path, ByteString content) {
    SString string = valueFactory.string(path.value());
    Blob blob = blob(valueFactory, content);
    return valueFactory.file(string, blob);
  }

  public static Blob blob(ByteString bytes) {
    return blob(new TestingValueFactory(), bytes);
  }

  public static Blob blob(ValueFactory valueFactory, ByteString bytes) {
    try {
      return valueFactory.blob(sink -> sink.write(bytes));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static ByteString trueByteString() {
    return ByteString.of((byte) 1);
  }

  public static ByteString falseByteString() {
    return ByteString.of((byte) 0);
  }
}
