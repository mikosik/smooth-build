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
public class Tuple extends Obj {
  private ImmutableList<Obj> elements;
  private final ObjectDb objectDb;

  public Tuple(MerkleRoot merkleRoot, ObjectDb objectDb, HashedDb hashedDb) {
    super(merkleRoot, hashedDb);
    this.objectDb = objectDb;
  }

  @Override
  public TupleSpec spec() {
    return (TupleSpec) super.spec();
  }

  public Obj get(int index) {
    ImmutableList<Obj> elements = elements();
    checkIndex(index, elements.size());
    return elements.get(index);
  }

  public Obj superObject() {
    ImmutableList<Obj> elements = elements();
    return elements.size() == 0 ? null : elements.iterator().next();
  }

  private ImmutableList<Obj> elements() {
    if (elements == null) {
      var elementSpecs = spec().elementSpecs();
      var elementHashes = readElementHashes(elementSpecs);
      if (elementSpecs.size() != elementHashes.size()) {
        throw new CannotDecodeObjectException(
            hash(), "Its TUPLE spec declares " + elementSpecs.size()
            + " elements but its data points to" + elementHashes.size() + "  elements.");
      }
      var builder = ImmutableList.<Obj>builder();
      for (int i = 0; i < elementSpecs.size(); i++) {
        Obj object = objectDb.get(elementHashes.get(i));
        Spec spec = elementSpecs.get(i);
        if (spec.equals(object.spec())) {
          builder.add(object);
        } else {
          throw new CannotDecodeObjectException(hash(), "Its TUPLE spec declares element " + i
              + " to have " + spec.name() + " spec but its data has object with " +
              object.spec().name() + " spec at that index.");
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
      throw new CannotDecodeObjectException(hash(), "Error reading element hashes.", e);
    }
  }

  @Override
  public String valueToString() {
    return "{" + elementsToStringValues(elements()) + '}';
  }
}
