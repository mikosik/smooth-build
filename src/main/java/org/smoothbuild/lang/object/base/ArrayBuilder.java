package org.smoothbuild.lang.object.base;

import static com.google.common.collect.Streams.stream;

import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.lang.object.db.ObjectsDbException;
import org.smoothbuild.lang.object.type.ConcreteArrayType;

public class ArrayBuilder {
  private final ConcreteArrayType type;
  private final HashedDb hashedDb;
  private final List<SObject> elements;

  public ArrayBuilder(ConcreteArrayType type, HashedDb hashedDb) {
    this.type = type;
    this.hashedDb = hashedDb;
    this.elements = new ArrayList<>();
  }

  public ArrayBuilder addAll(Iterable<? extends SObject> objects) {
    stream(objects).forEach(this::add);
    return this;
  }

  public ArrayBuilder add(SObject elem) {
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
    Hash[] elementHashes = elements
        .stream()
        .map(SObject::hash)
        .toArray(Hash[]::new);
    return type.newSObject(writeElements(elementHashes));
  }

  private Hash writeElements(Hash[] elementHashes) {
    try {
      return hashedDb.writeHashes(elementHashes);
    } catch (HashedDbException e) {
      throw new ObjectsDbException(e);
    }
  }
}
