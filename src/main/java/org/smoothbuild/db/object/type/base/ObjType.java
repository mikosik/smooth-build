package org.smoothbuild.db.object.type.base;

import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.lang.base.type.api.AbstractType;
import org.smoothbuild.lang.base.type.api.Variable;

import com.google.common.collect.ImmutableSet;

public abstract class ObjType extends AbstractType {
  private final Hash hash;
  private final ObjKind kind;

  protected ObjType(String name, Hash hash, ObjKind kind) {
    this(name, hash, kind, ImmutableSet.of());
  }

  protected ObjType(String name, Hash hash, ObjKind kind, ImmutableSet<Variable> variables) {
    super(name, variables);
    this.hash = hash;
    this.kind = kind;
  }

  /**
   * Creates new java instance of Obj represented by merkleRoot.
   */
  public abstract Obj newObj(MerkleRoot merkleRoot, ObjDb objDb);

  public Hash hash() {
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    return (object instanceof ObjType that) && Objects.equals(hash(), that.hash());
  }

  @Override
  public int hashCode() {
    return hash().hashCode();
  }

  public Class<? extends Obj> jType() {
    return kind.jType();
  }

  public boolean isNothing() {
    return false;
  }
}
