package org.smoothbuild.compile.fs.ps.ast.define;

import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.SchemaS;
import org.smoothbuild.compile.fs.lang.type.TypeS;

import com.google.common.collect.ImmutableList;

/**
 * Expression that can be monomorphized.
 */
public abstract sealed class MonoizableP extends ExprP
    permits AnonymousFuncP, ReferenceP {
  private ImmutableList<TypeS> typeArgs;

  public MonoizableP(Location location) {
    super(location);
  }

  public abstract SchemaS schemaS();

  public void setTypeArgs(ImmutableList<TypeS> typeArgs) {
    this.typeArgs = typeArgs;
  }

  public ImmutableList<TypeS> typeArgs() {
    return typeArgs;
  }
}
