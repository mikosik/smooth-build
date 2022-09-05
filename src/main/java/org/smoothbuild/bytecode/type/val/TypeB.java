package org.smoothbuild.bytecode.type.val;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.CatB;
import org.smoothbuild.bytecode.type.CatKindB;

public abstract class TypeB extends CatB {
  protected TypeB(Hash hash, String name, CatKindB kind) {
    super(hash, name, kind);
  }
}
