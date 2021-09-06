package org.smoothbuild.db.object.obj.val;

import static java.util.Objects.checkIndex;

import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.db.UnexpectedNodeException;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
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
  private ImmutableList<Val> items;

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
    if (items == null) {
      items = instantiateItems();
    }
    return items;
  }

  private ImmutableList<Val> instantiateItems() {
    var itemSpecs = spec().items();
    var objs = readSequenceObjs(DATA_PATH, dataHash(), itemSpecs.size(), Val.class);
    for (int i = 0; i < itemSpecs.size(); i++) {
      Val obj = objs.get(i);
      Spec expectedSpec = itemSpecs.get(i);
      Spec actualSpec = obj.spec();
      if (!expectedSpec.equals(actualSpec)) {
        throw new UnexpectedNodeException(hash(), spec(), DATA_PATH, i, expectedSpec, actualSpec);
      }
    }
    return objs;
  }

  @Override
  public String valueToString() {
    return "{" + elementsToStringValues(items()) + '}';
  }
}
