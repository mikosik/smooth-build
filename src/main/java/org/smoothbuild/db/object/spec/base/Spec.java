package org.smoothbuild.db.object.spec.base;

import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.lang.base.type.api.AbstractType;
import org.smoothbuild.lang.base.type.api.Variable;

import com.google.common.collect.ImmutableSet;

public abstract class Spec extends AbstractType {
  private final Hash hash;
  private final SpecKind kind;

  protected Spec(String name, Hash hash, SpecKind kind) {
    this(name, hash, kind, ImmutableSet.of());
  }

  protected Spec(String name, Hash hash, SpecKind kind, ImmutableSet<Variable> variables) {
    super(name, variables);
    this.hash = hash;
    this.kind = kind;
  }

  /**
   * Creates new java instance of Obj represented by merkleRoot.
   */
  public abstract Obj newObj(MerkleRoot merkleRoot, ObjectDb objectDb);

  public Hash hash() {
    return hash;
  }

  public SpecKind kind() {
    return kind;
  }

  @Override
  public boolean equals(Object object) {
    return (object instanceof Spec that) && Objects.equals(hash(), that.hash());
  }

  @Override
  public int hashCode() {
    return hash().hashCode();
  }

  public Class<? extends Obj> jType() {
    return kind.jType();
  }

  public boolean isNothing() {
    return kind == SpecKind.NOTHING;
  }
}
