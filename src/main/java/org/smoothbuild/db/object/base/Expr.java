package org.smoothbuild.db.object.base;

import org.smoothbuild.db.object.db.CannotDecodeObjectException;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.spec.ExprSpec;

import com.google.common.collect.ImmutableList;

public abstract class Expr extends Obj {
  public Expr(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  protected ImmutableList<Expr> verifyExprList(ImmutableList<Obj> elements) {
    for (Obj element : elements) {
      if (!(element.spec() instanceof ExprSpec)) {
        throw new CannotDecodeObjectException(hash(), "It is " + spec().name()
            + " but one of its elements is " + element.spec().name() + " instead of Expr.");
      }
    }
    @SuppressWarnings("unchecked")
    ImmutableList<Expr> result = (ImmutableList<Expr>) (Object) elements;
    return result;
  }
}
