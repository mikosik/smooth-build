package org.smoothbuild.db.object.base;

import static java.util.Objects.checkIndex;

import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.db.object.db.CannotDecodeObjectException;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.db.object.spec.TupleSpec;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Tuple extends Val {
  private ImmutableList<Val> elements;
  private final ObjectDb objectDb;

  public Tuple(MerkleRoot merkleRoot, ObjectDb objectDb, HashedDb hashedDb) {
    super(merkleRoot, hashedDb);
    this.objectDb = objectDb;
  }

  @Override
  public TupleSpec spec() {
    return (TupleSpec) super.spec();
  }

  public Val get(int index) {
    ImmutableList<Val> elements = elements();
    checkIndex(index, elements.size());
    return elements.get(index);
  }

  public Val superObject() {
    ImmutableList<Val> elements = elements();
    return elements.size() == 0 ? null : elements.iterator().next();
  }

  private ImmutableList<Val> elements() {
    if (elements == null) {
      var elementSpecs = spec().elementSpecs();
      var elementHashes = readElementHashes(elementSpecs);
      if (elementSpecs.size() != elementHashes.size()) {
        throw new CannotDecodeObjectException(
            hash(), "Its TUPLE spec declares " + elementSpecs.size()
            + " elements but its data points to" + elementHashes.size() + "  elements.");
      }
      var builder = ImmutableList.<Val>builder();
      for (int i = 0; i < elementSpecs.size(); i++) {
        Obj obj = objectDb.get(elementHashes.get(i));
        Spec spec = elementSpecs.get(i);
        if (spec.equals(obj.spec())) {
          builder.add((Val) obj);
        } else {
          throw new CannotDecodeObjectException(hash(), "Its TUPLE spec declares element " + i
              + " to have " + spec.name() + " spec but its data has object with " +
              obj.spec().name() + " spec at that index.");
        }
      }
      elements = builder.build();
    }
    return elements;
  }

  private List<Hash> readElementHashes(ImmutableList<Spec> elementSpecs) {
    try {
      return hashedDb.readHashes(dataHash(), elementSpecs.size());
    } catch (HashedDbException e) {
      throw new CannotDecodeObjectException(hash(), "Error reading element hashes.", e);
    }
  }

  @Override
  public String valueToString() {
    return "{" + elementsToStringValues(elements()) + '}';
  }
}
