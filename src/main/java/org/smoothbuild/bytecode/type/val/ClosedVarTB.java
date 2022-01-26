package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.base.CatKindB.CLOSED_VARIABLE;

import org.smoothbuild.db.Hash;
import org.smoothbuild.lang.base.type.api.ClosedVarT;

import com.google.common.collect.ImmutableSet;

public class ClosedVarTB extends VarTB implements ClosedVarT {
  public ClosedVarTB(Hash hash, String name) {
    super(hash, name, CLOSED_VARIABLE, ImmutableSet.of(), true);
  }
}
