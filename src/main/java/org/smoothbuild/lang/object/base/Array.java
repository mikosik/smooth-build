package org.smoothbuild.lang.object.base;

import static com.google.common.collect.ImmutableList.toImmutableList;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.lang.object.db.ObjectDb;
import org.smoothbuild.lang.object.db.ObjectDbException;
import org.smoothbuild.lang.object.type.ArrayType;
import org.smoothbuild.lang.object.type.BinaryType;

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
  public ArrayType type() {
    return (ArrayType) super.type();
  }

  public <T extends SObject> Iterable<T> asIterable(Class<T> clazz) {
    assertIsIterableAs(clazz);
    ImmutableList<SObject> elements = elements();
    for (SObject object : elements) {
      if (!object.type().equals(type().elemType())) {
        throw new ObjectDbException(hash(), "It is array with type " + type().name()
            + " but one of its elements has type " + object.type().name());
      }
    }
    @SuppressWarnings("unchecked")
    ImmutableList<T> result = (ImmutableList<T>) elements;
    return result;
  }

  private <T extends SObject> void assertIsIterableAs(Class<T> clazz) {
    BinaryType elemType = type().elemType();
    if (!(elemType.isNothing() || clazz.isAssignableFrom(elemType.jType()))) {
      throw new IllegalArgumentException(type().name() + " cannot be viewed as Iterable of "
          + clazz.getCanonicalName() + ".");
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
