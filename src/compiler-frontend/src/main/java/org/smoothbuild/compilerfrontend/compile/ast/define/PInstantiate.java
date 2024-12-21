package org.smoothbuild.compilerfrontend.compile.ast.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Instantiation of polymorphic entity.
 */
public final class PInstantiate extends PExpr {
  private final PPolymorphic polymorphic;
  private List<SType> typeArgs;

  public PInstantiate(PPolymorphic polymorphic, Location location) {
    super(location);
    this.polymorphic = polymorphic;
  }

  public PPolymorphic polymorphic() {
    return polymorphic;
  }

  public void setTypeArgs(List<SType> typeArgs) {
    this.typeArgs = typeArgs;
  }

  public List<SType> typeArgs() {
    return typeArgs;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PInstantiate that
        && Objects.equals(this.polymorphic, that.polymorphic)
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(polymorphic, location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("PInstantiate")
        .addField("polymorphic", polymorphic)
        .addField("location", location())
        .toString();
  }
}
