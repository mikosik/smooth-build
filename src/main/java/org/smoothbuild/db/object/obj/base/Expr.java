package org.smoothbuild.db.object.obj.base;

import org.smoothbuild.db.object.db.ObjectDb;

public abstract class Expr extends Obj {
  public Expr(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }
}
