package org.smoothbuild.db.object.type.base;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.lang.base.type.api.AbstractType;
import org.smoothbuild.lang.base.type.api.Var;

import com.google.common.collect.ImmutableSet;

public abstract class SpecH extends AbstractType {
  private final Hash hash;
  private final SpecKindH kind;

  protected SpecH(String name, Hash hash, SpecKindH kind) {
    this(name, hash, kind, ImmutableSet.of());
  }

  protected SpecH(String name, Hash hash, SpecKindH kind,
      ImmutableSet<? extends Var> vars) {
    super(name, vars);
    this.hash = hash;
    this.kind = kind;
  }

  /**
   * Creates new java instance of Obj represented by merkleRoot.
   */
  public ObjectH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    checkArgument(this.equals(merkleRoot.spec()));
    return kind.newInstanceJ(merkleRoot, objectHDb);
  }

  public Hash hash() {
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    return (object instanceof SpecH that) && Objects.equals(hash(), that.hash());
  }

  @Override
  public int hashCode() {
    return hash().hashCode();
  }

  public SpecKindH kind() {
    return kind;
  }

  public Class<? extends ObjectH> typeJ() {
    return kind.typeJ();
  }

  public boolean isNothing() {
    return false;
  }
}
