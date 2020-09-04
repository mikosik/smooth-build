package org.smoothbuild.db.object.base;

import static org.smoothbuild.util.Lists.map;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.db.object.db.CannotDecodeObjectException;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.spec.ArraySpec;
import org.smoothbuild.db.object.spec.Spec;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Array extends Obj {
  private final ObjectDb objectDb;

  public Array(MerkleRoot merkleRoot, ObjectDb objectDb, HashedDb hashedDb) {
    super(merkleRoot, hashedDb);
    this.objectDb = objectDb;
  }

  @Override
  public ArraySpec spec() {
    return (ArraySpec) super.spec();
  }

  public <T extends Obj> Iterable<T> asIterable(Class<T> clazz) {
    assertIsIterableAs(clazz);
    ImmutableList<Obj> elements = elements();
    for (Obj object : elements) {
      if (!object.spec().equals(spec().elemSpec())) {
        throw new CannotDecodeObjectException(hash(), "It is array which spec == " + spec().name()
            + " but one of its elements has spec == " + object.spec().name());
      }
    }
    @SuppressWarnings("unchecked")
    ImmutableList<T> result = (ImmutableList<T>) elements;
    return result;
  }

  private <T extends Obj> void assertIsIterableAs(Class<T> clazz) {
    Spec elemSpec = spec().elemSpec();
    if (!(elemSpec.isNothing() || clazz.isAssignableFrom(elemSpec.jType()))) {
      throw new IllegalArgumentException(spec().name() + " cannot be viewed as Iterable of "
          + clazz.getCanonicalName() + ".");
    }
  }

  private ImmutableList<Obj> elements() {
    try {
      return map(hashedDb.readHashes(dataHash()), objectDb::get);
    } catch (HashedDbException e) {
      throw new CannotDecodeObjectException(hash(), e);
    }
  }

  @Override
  public String valueToString() {
    return "[" + elementsToStringValues(elements()) + ']';
  }
}
