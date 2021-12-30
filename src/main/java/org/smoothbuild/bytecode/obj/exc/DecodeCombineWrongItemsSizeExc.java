package org.smoothbuild.bytecode.obj.exc;

import org.smoothbuild.bytecode.type.expr.CombineCB;
import org.smoothbuild.db.Hash;

public class DecodeCombineWrongItemsSizeExc extends DecodeObjExc {
  public DecodeCombineWrongItemsSizeExc(Hash hash, CombineCB cat, int actual) {
    super(buildMessage(hash, cat, actual));
  }

  private static String buildMessage(Hash hash, CombineCB cat, int actual) {
    return ("Cannot decode %s object at %s. Evaluation type items size (%s)"
        + " is not equal to actual items size (%s).")
        .formatted(
            cat.q(),
            hash,
            cat.evalT().items().size(),
            actual);
  }
}
