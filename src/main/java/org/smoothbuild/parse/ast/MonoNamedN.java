package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.Nal;

public interface NamedN implements AstNode implements Nal {

  @Override
  public default boolean equals(Object object) {
    return object instanceof NamedN that
        && this.name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
