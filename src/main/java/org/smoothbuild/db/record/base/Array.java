package org.smoothbuild.db.record.base;

import static org.smoothbuild.util.Lists.map;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.db.record.db.CannotDecodeRecordException;
import org.smoothbuild.db.record.db.RecordDb;
import org.smoothbuild.db.record.spec.ArraySpec;
import org.smoothbuild.db.record.spec.Spec;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Array extends RecordImpl {
  private final RecordDb recordDb;

  public Array(MerkleRoot merkleRoot, RecordDb recordDb, HashedDb hashedDb) {
    super(merkleRoot, hashedDb);
    this.recordDb = recordDb;
  }

  @Override
  public ArraySpec spec() {
    return (ArraySpec) super.spec();
  }

  public <T extends Record> Iterable<T> asIterable(Class<T> clazz) {
    assertIsIterableAs(clazz);
    ImmutableList<Record> elements = elements();
    for (Record record : elements) {
      if (!record.spec().equals(spec().elemSpec())) {
        throw new CannotDecodeRecordException(hash(), "It is array which spec == " + spec().name()
            + " but one of its elements has spec == " + record.spec().name());
      }
    }
    @SuppressWarnings("unchecked")
    ImmutableList<T> result = (ImmutableList<T>) elements;
    return result;
  }

  private <T extends Record> void assertIsIterableAs(Class<T> clazz) {
    Spec elemSpec = spec().elemSpec();
    if (!(elemSpec.isNothing() || clazz.isAssignableFrom(elemSpec.jType()))) {
      throw new IllegalArgumentException(spec().name() + " cannot be viewed as Iterable of "
          + clazz.getCanonicalName() + ".");
    }
  }

  private ImmutableList<Record> elements() {
    try {
      return map(hashedDb.readHashes(dataHash()), recordDb::get);
    } catch (HashedDbException e) {
      throw new CannotDecodeRecordException(hash(), e);
    }
  }

  @Override
  public String valueToString() {
    return "[" + elementsToStringValues(elements()) + ']';
  }
}
