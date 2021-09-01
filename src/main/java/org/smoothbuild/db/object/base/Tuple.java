package org.smoothbuild.db.object.base;

import static java.util.Objects.checkIndex;
import static org.smoothbuild.db.object.db.Helpers.wrapObjectDbExceptionAsDecodeObjException;

import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.DecodeObjException;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.db.object.spec.TupleSpec;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Tuple extends Val {
  private ImmutableList<Val> elements;

  public Tuple(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
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
      var elementHashes = getDataSequence(elementSpecs.size());
      var builder = ImmutableList.<Val>builder();
      for (int i = 0; i < elementSpecs.size(); i++) {
        Obj obj = elementAt(elementHashes, i);
        Spec spec = elementSpecs.get(i);
        if (spec.equals(obj.spec())) {
          builder.add((Val) obj);
        } else {
          throw new DecodeObjException(hash(), "Its TUPLE spec declares element " + i
              + " to have " + spec.name() + " spec but its data has object with " +
              obj.spec().name() + " spec at that index.");
        }
      }
      elements = builder.build();
    }
    return elements;
  }

  private Obj elementAt(List<Hash> hashSequence, int i) {
    return wrapObjectDbExceptionAsDecodeObjException(
        hash(),
        () -> objectDb().get(hashSequence.get(i)));
  }

  @Override
  public String valueToString() {
    return "{" + elementsToStringValues(elements()) + '}';
  }
}
