package org.smoothbuild.bytecode.type.inst;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.CategoryKindB;

/**
 * This class is immutable.
 */
public abstract class BaseTB extends TypeB {
  public BaseTB(Hash hash, String name, CategoryKindB kind) {
    super(hash, name, kind);
  }
}
