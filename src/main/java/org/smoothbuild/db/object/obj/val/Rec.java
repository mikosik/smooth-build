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
    ImmutableList<Val> items = items();
    checkIndex(index, items.size());
    return items.get(index);
  }

  public Val superObject() {
    ImmutableList<Val> items = items();
    return items.size() == 0 ? null : items.iterator().next();
  }

  private ImmutableList<Val> items() {
    if (elements == null) {
      var itemSpecs = spec().items();
      var itemHashes = getDataSequence(itemSpecs.size());
      var builder = ImmutableList.<Val>builder();
      for (int i = 0; i < itemSpecs.size(); i++) {
        Obj obj = itemAt(itemHashes, i);
        Spec spec = itemSpecs.get(i);
        if (spec.equals(obj.spec())) {
          builder.add((Val) obj);
        } else {
          throw new DecodeObjException(hash(), "Its RECORD spec declares item " + i
              + " to have " + spec.name() + " spec but its data has object with " +
              obj.spec().name() + " spec at that index.");
        }
      }
      elements = builder.build();
    }
    return elements;
  }

  private Obj itemAt(List<Hash> hashSequence, int i) {
    return wrapObjectDbExceptionAsDecodeObjException(
        hash(),
        () -> objectDb().get(hashSequence.get(i)));
  }

  @Override
  public String valueToString() {
    return "{" + elementsToStringValues(items()) + '}';
  }
}
