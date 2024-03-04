package org.smoothbuild.virtualmachine.bytecode.type.value;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB;

public abstract class TypeB extends CategoryB {
  protected TypeB(Hash hash, String name, CategoryKindB kind) {
    super(hash, name, kind);
  }
}
