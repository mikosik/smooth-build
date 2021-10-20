package org.smoothbuild.db.object.obj.val;

import static java.util.Objects.checkIndex;

import org.smoothbuild.db.object.exc.UnexpectedObjNodeException;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.val.StructSpec;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Struc_ extends Val {
  private ImmutableList<Val> items;

  public Struc_(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  @Override
  public StructSpec spec() {
    return (StructSpec) super.spec();
  }

  public Val get(int index) {
    ImmutableList<Val> items = items();
    checkIndex(index, items.size());
    return items.get(index);
  }

  public ImmutableList<Val> items() {
    if (items == null) {
      items = instantiateItems();
    }
    return items;
  }

  private ImmutableList<Val> instantiateItems() {
    var itemSpecs = spec().fields();
    var objs = readSequenceObjs(DATA_PATH, dataHash(), itemSpecs.size(), Val.class);
    for (int i = 0; i < itemSpecs.size(); i++) {
      Val obj = objs.get(i);
      Spec expectedSpec = itemSpecs.get(i);
      Spec actualSpec = obj.spec();
      if (!expectedSpec.equals(actualSpec)) {
        throw new UnexpectedObjNodeException(
            hash(), spec(), DATA_PATH, i, expectedSpec, actualSpec);
      }
    }
    return objs;
  }

  @Override
  public String valueToString() {
    StringBuilder builder = new StringBuilder("{");
    ImmutableList<String> names = spec().names();
    ImmutableList<Val> values = items();
    for (int i = 0; i < values.size(); i++) {
      builder.append(names.get(i));
      builder.append("=");
      builder.append(values.get(i).valueToString());
      if (i != values.size() - 1) {
        builder.append(",");
      }
    }
    builder.append("}");
    return builder.toString();
  }
}
