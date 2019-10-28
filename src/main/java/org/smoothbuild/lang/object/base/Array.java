package org.smoothbuild.lang.object.base;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.lang.object.db.ObjectsDbException.corruptedObjectException;
import static org.smoothbuild.lang.object.db.ObjectsDbException.objectsDbException;

import java.io.EOFException;
import java.io.IOException;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.lang.object.type.ConcreteArrayType;
import org.smoothbuild.lang.object.type.ConcreteType;

import com.google.common.collect.ImmutableList;

public class Array extends SObjectImpl {
  private final ObjectsDb objectsDb;

  public Array(Hash dataHash, ConcreteArrayType arrayType, ObjectsDb objectsDb,
      HashedDb hashedDb) {
    super(dataHash, arrayType, hashedDb);
    this.objectsDb = objectsDb;
  }

  @Override
  public ConcreteArrayType type() {
    return (ConcreteArrayType) super.type();
  }

  public <T extends SObject> Iterable<T> asIterable(Class<T> clazz) {
    assertIsIterableAs(clazz);
    ImmutableList<SObject> elements = elements();
    for (SObject object : elements) {
      if (!object.type().equals(type().elemType())) {
        throw corruptedObjectException(hash(), "It is array with type " + type().q()
            + " but one of its elements has type " + object.type().q());
      }
    }
    return (ImmutableList<T>) elements;
  }

  private <T extends SObject> void assertIsIterableAs(Class<T> clazz) {
    ConcreteType elemType = type().elemType();
    if (!(elemType.isNothing() || clazz.isAssignableFrom(elemType.jType()))) {
      throw new IllegalArgumentException("Array of type " + type().q() + " cannot be iterated as " +
          Struct.class.getCanonicalName());
    }
  }

  private ImmutableList<SObject> elements() {
    try {
      return hashedDb
          .readHashes(dataHash())
          .stream()
          .map(objectsDb::get)
          .collect(toImmutableList());
    } catch (EOFException e) {
      throw corruptedObjectException(hash(),
          "It is an Array object which stored in ObjectsDb has number of bytes which is not " +
              "multiple of hash size = " + Hash.hashesSize() + ".");
    } catch (IOException e) {
      throw objectsDbException(e);
    }
  }
}
