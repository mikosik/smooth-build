package org.smoothbuild.bytecode.type.exc;

import static org.smoothbuild.bytecode.obj.base.ObjB.DATA_PATH;

import org.smoothbuild.bytecode.type.base.CatKindB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;

public class DecodeCatWrongEvalTExc extends DecodeCatNodeExc {
  public DecodeCatWrongEvalTExc(Hash hash, CatKindB kind, TypeB evalT) {
    super(hash, kind, DATA_PATH, buildMessage(evalT));
  }

  private static String buildMessage(TypeB evalT) {
    return "It is equal to " + evalT.q() + " but evalT should not contain open-vars.";
  }
}
