package org.smoothbuild.db.object.spec.base;

import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.Variable;

import com.google.common.collect.ImmutableSet;

public abstract class Spec implements Type {
  private final Hash hash;
  private final SpecKind kind;
  private final ImmutableSet<Variable> variables;

  protected Spec(Hash hash, SpecKind kind) {
    this(hash, kind, ImmutableSet.of());
  }

  protected Spec(Hash hash, SpecKind kind, ImmutableSet<Variable> variables) {
    this.hash = hash;
    this.kind = kind;
    this.variables = variables;
  }

  @Override
  public ImmutableSet<Variable> variables() {
    return variables;
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

  @Override
  public String toString() {
    return name() + "@" + hash();
  }

  @Override
  public String name() {
    return kind.name();
  }

  public Class<? extends Obj> jType() {
    return kind.jType();
  }

  public boolean isNothing() {
    return kind == SpecKind.NOTHING;
  }
}
