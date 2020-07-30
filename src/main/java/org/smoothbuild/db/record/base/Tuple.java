package org.smoothbuild.db.record.base;

import static java.util.Objects.checkIndex;

import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.db.record.db.CannotDecodeRecordException;
import org.smoothbuild.db.record.db.RecordDb;
import org.smoothbuild.db.record.spec.Spec;
import org.smoothbuild.db.record.spec.TupleSpec;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Tuple extends RecordImpl {
  private ImmutableList<Record> elements;
  private final RecordDb recordDb;

  public Tuple(MerkleRoot merkleRoot, RecordDb recordDb, HashedDb hashedDb) {
    super(merkleRoot, hashedDb);
    this.recordDb = recordDb;
  }

  @Override
  public TupleSpec spec() {
    return (TupleSpec) super.spec();
  }

  public Record get(int index) {
    ImmutableList<Record> elements = elements();
    checkIndex(index, elements.size());
    return elements.get(index);
  }

  public Record superObject() {
    ImmutableList<Record> elements = elements();
    return elements.size() == 0 ? null : elements.iterator().next();
  }

  private ImmutableList<Record> elements() {
    if (elements == null) {
      var elementSpecs = spec().elementSpecs();
      var elementHashes = readElementHashes(elementSpecs);
      if (elementSpecs.size() != elementHashes.size()) {
        throw new CannotDecodeRecordException(
            hash(), "Its TUPLE spec declares " + elementSpecs.size()
            + " elements but its data points to" + elementHashes.size() + "  elements.");
      }
      var builder = ImmutableList.<Record>builder();
      for (int i = 0; i < elementSpecs.size(); i++) {
        Record record = recordDb.get(elementHashes.get(i));
        Spec spec = elementSpecs.get(i);
        if (spec.equals(record.spec())) {
          builder.add(record);
        } else {
          throw new CannotDecodeRecordException(hash(), "Its TUPLE spec declares element " + i
              + " to have " + spec.name() + " spec but its data has record with " +
              record.spec().name() + " spec at that index.");
        }
      }
      elements = builder.build();
    }
    return elements;
  }

  private List<Hash> readElementHashes(final ImmutableList<Spec> elementSpecs) {
    try {
      return hashedDb.readHashes(dataHash(), elementSpecs.size());
    } catch (HashedDbException e) {
      throw new CannotDecodeRecordException(hash(), "Error reading element hashes.", e);
    }
  }

  @Override
  public String valueToString() {
    return "{" + elementsToStringValues(elements()) + '}';
  }
}
