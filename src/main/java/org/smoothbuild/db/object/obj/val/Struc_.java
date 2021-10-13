package org.smoothbuild.db.object.obj.val;

import java.util.Objects;

import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.exc.DecodeStructWrongTupleSpecException;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.spec.val.StructSpec;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Struc_ extends Val {
  public Struc_(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  @Override
  public StructSpec spec() {
    return (StructSpec) super.spec();
  }

  public Rec rec() {
    Rec rec = readObj(DATA_PATH, dataHash(), Rec.class);
    if (Objects.equals(spec().rec(), rec.spec())) {
      return rec;
    } else {
      throw new DecodeStructWrongTupleSpecException(hash(), spec(), rec.spec());
    }
  }

  @Override
  public String valueToString() {
    StringBuilder builder = new StringBuilder("{");
    ImmutableList<String> names = spec().names();
    ImmutableList<Val> values = rec().items();
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
