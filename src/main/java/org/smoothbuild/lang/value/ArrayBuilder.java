package org.smoothbuild.lang.value;

import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.db.values.ValuesDbException.valuesDbException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.type.ConcreteArrayType;

import com.google.common.hash.HashCode;

public class ArrayBuilder {
  private final ConcreteArrayType type;
  private final HashedDb hashedDb;
  private final List<Value> elements;

  public ArrayBuilder(ConcreteArrayType type, HashedDb hashedDb) {
    this.type = type;
    this.hashedDb = hashedDb;
    this.elements = new ArrayList<>();
  }

  public ArrayBuilder addAll(Iterable<? extends Value> values) {
    stream(values).forEach(this::add);
    return this;
  }

  public ArrayBuilder add(Value elem) {
    if (!type.elemType().equals(elem.type())) {
      throw new IllegalArgumentException("Element type must be " + type.elemType().name()
          + " but was " + elem.type().name() + ".");
    }
    Class<?> required = type.elemType().jType();
    if (!required.equals(elem.getClass())) {
      throw new IllegalArgumentException("Element must be instance of java class "
          + required.getCanonicalName() + " but it is instance of "
          + elem.getClass().getCanonicalName() + ".");
    }
    elements.add(elem);
    return this;
  }

  public Array build() {
    HashCode[] elementHashes = elements
        .stream()
        .map(Value::hash)
        .toArray(HashCode[]::new);
    return type.newValue(writeElements(elementHashes));
  }

  private HashCode writeElements(HashCode[] elementHashes) {
    try {
      return hashedDb.writeHashes(elementHashes);
    } catch (IOException e) {
      throw valuesDbException(e);
    }
  }
}
