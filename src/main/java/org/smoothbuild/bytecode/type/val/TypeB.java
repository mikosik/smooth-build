package org.smoothbuild.bytecode.type.val;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.CategoryB;
import org.smoothbuild.bytecode.type.CategoryKindB;

public abstract class TypeB extends CategoryB {
  protected TypeB(Hash hash, String name, CategoryKindB kind) {
    super(hash, name, kind);
  }
}
