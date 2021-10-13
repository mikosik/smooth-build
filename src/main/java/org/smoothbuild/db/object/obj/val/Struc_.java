package org.smoothbuild.db.object.obj.val;

import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Val;

/**
 * This class is immutable.
 */
public class Struc_ extends Val {
  public Struc_(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  @Override
  public String valueToString() {
    return "TODO"; // TODO
  }
}
