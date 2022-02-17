package org.smoothbuild.bytecode.type.base;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Comparator.comparing;

import java.util.Collection;

import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.exc.DecodeObjIllegalPolymorphicTypeExc;
import org.smoothbuild.bytecode.type.val.OpenVarTB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.lang.type.api.Type;
import org.smoothbuild.lang.type.api.TypeBBridge;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public abstract class TypeB extends CatB implements TypeBBridge {
  private final ImmutableSet<OpenVarTB> openVars;
  private final boolean hasClosedVars;

  protected TypeB(Hash hash, String name, CatKindB kind) {
    this(hash, name, kind, ImmutableSet.of(), false);
  }

  protected TypeB(Hash hash, String name, CatKindB kind, ImmutableSet<OpenVarTB> openVars,
      boolean hasClosedVars) {
    super(hash, name, kind);
    this.openVars = openVars;
    this.hasClosedVars = hasClosedVars;
  }

  public static ImmutableSet<OpenVarTB> calculateOpenVars(ImmutableList<TypeB> types) {
    return types.stream()
        .map(TypeB::openVars)
        .flatMap(Collection::stream)
        .sorted(comparing(Type::name))
        .collect(toImmutableSet());
  }

  @Override
  public boolean hasClosedVars() {
    return hasClosedVars;
  }

  @Override
  public ImmutableSet<OpenVarTB> openVars() {
    return openVars;
  }

  @Override
  public String toString() {
    return "TypeB(`" + name() + "`)";
  }

  protected static void validateNotPolymorphic(MerkleRoot merkleRoot) {
    if (((TypeB) merkleRoot.cat()).isPolytype()) {
      throw new DecodeObjIllegalPolymorphicTypeExc(merkleRoot.hash(), merkleRoot.cat());
    }
  }
}
