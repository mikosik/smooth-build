package org.smoothbuild.db.object.obj.val;

import static java.util.Objects.checkIndex;

import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.exc.UnexpectedObjNodeException;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.val.StructSpec;
import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Struc_ extends Val {
  // Should be accessed only through synchronized items() method.
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

  public synchronized ImmutableList<Val> items() {
    if (items == null) {
      items = instantiateItems();
    }
    return items;
  }

  private ImmutableList<Val> instantiateItems() {
    NamedList<ValSpec> fields = spec().fields();
    var objs = readSequenceObjs(DATA_PATH, dataHash(), fields.size(), Val.class);
    for (int i = 0; i < fields.size(); i++) {
      Spec expectedSpec = fields.getObject(i);
      Spec actualSpec = objs.get(i).spec();
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
    var typeFields = spec().fields().list();
    ImmutableList<Val> values = items();
    for (int i = 0; i < values.size(); i++) {
      builder.append(typeFields.get(i).saneName());
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
