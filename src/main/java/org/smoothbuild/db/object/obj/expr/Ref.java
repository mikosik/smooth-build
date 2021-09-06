package org.smoothbuild.db.object.obj.expr;

import java.math.BigInteger;

import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;

public class Ref extends Expr {
  public Ref(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  public BigInteger value() {
    return readData(() -> hashedDb().readBigInteger(dataHash()));
  }

  @Override
  public String valueToString() {
    return "Ref(" + value() + ")";
  }}
