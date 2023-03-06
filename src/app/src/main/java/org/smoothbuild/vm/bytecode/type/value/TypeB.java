package org.smoothbuild.vm.bytecode.type.value;

import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.CategoryB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB;

public abstract class TypeB extends CategoryB {
  protected TypeB(Hash hash, String name, CategoryKindB kind) {
    super(hash, name, kind);
  }
}
