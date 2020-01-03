package org.smoothbuild.lang.object.base;

import static com.google.common.collect.ImmutableList.toImmutableList;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.lang.object.db.ObjectDb;
import org.smoothbuild.lang.object.db.ObjectDbException;
import org.smoothbuild.lang.object.type.ConcreteArrayType;
import org.smoothbuild.lang.object.type.ConcreteType;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Array extends SObjectImpl {
  private final ObjectDb objectDb;

  public Array(MerkleRoot merkleRoot, ObjectDb objectDb, HashedDb hashedDb) {
    super(merkleRoot, hashedDb);
    this.objectDb = objectDb;
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
        throw new ObjectDbException(hash(), "It is array with type " + type().q()
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
          .map(objectDb::get)
          .collect(toImmutableList());
    } catch (HashedDbException e) {
      throw new ObjectDbException(hash(), e);
    }
  }
}
