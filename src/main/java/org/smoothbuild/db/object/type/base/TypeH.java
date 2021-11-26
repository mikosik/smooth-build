package org.smoothbuild.db.object.type.base;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.lang.base.type.api.AbstractType;
import org.smoothbuild.lang.base.type.api.Variable;

import com.google.common.collect.ImmutableSet;

public abstract class TypeH extends AbstractType {
  private final Hash hash;
  private final TypeKindH kind;

  protected TypeH(String name, Hash hash, TypeKindH kind) {
    this(name, hash, kind, ImmutableSet.of());
  }

  protected TypeH(String name, Hash hash, TypeKindH kind,
      ImmutableSet<? extends Variable> variables) {
    super(name, variables);
    this.hash = hash;
    this.kind = kind;
  }

  /**
   * Creates new java instance of Obj represented by merkleRoot.
   */
  public ObjectH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return kind.newInstanceJ(merkleRoot, objectHDb);
  }

  public Hash hash() {
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    return (object instanceof TypeH that) && Objects.equals(hash(), that.hash());
  }

  @Override
  public int hashCode() {
    return hash().hashCode();
  }

  public TypeKindH kind() {
    return kind;
  }

  public Class<? extends ObjectH> jType() {
    return kind.jType();
  }

  public boolean isNothing() {
    return false;
  }
}
