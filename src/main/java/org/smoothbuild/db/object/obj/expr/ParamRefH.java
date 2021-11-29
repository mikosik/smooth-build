package org.smoothbuild.db.object.obj.expr;

import java.math.BigInteger;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;

public class ParamRefH extends ExprH {
  public ParamRefH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb);
  }

  public BigInteger value() {
    return readData(() -> hashedDb().readBigInteger(dataHash()));
  }

  @Override
  public String valToString() {
    return "Ref(" + value() + ")";
  }}
