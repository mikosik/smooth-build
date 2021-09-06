package org.smoothbuild.db.object.obj.val;

import static java.util.Objects.checkIndex;
import static org.smoothbuild.db.object.db.Helpers.wrapObjectDbExceptionAsDecodeObjException;

import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.DecodeObjException;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.val.RecSpec;

import com.google.common.collect.ImmutableList;

/**
 * Record.
 *
 * This class is immutable.
 */
public class Rec extends Val {
  private ImmutableList<Val> elements;

  public Rec(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  @Override
  public RecSpec spec() {
    return (RecSpec) super.spec();
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
          throw new DecodeObjException(hash(), "Its RECORD spec declares element " + i
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