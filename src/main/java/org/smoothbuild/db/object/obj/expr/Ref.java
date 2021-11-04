package org.smoothbuild.db.object.obj.expr;

import java.math.BigInteger;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;

public class Ref extends Expr {
  public Ref(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
  }

  public BigInteger value() {
    return readData(() -> hashedDb().readBigInteger(dataHash()));
  }

  @Override
  public String valueToString() {
    return "Ref(" + value() + ")";
  }}
