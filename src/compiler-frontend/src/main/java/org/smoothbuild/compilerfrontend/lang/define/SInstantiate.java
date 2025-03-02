package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.Collection;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;

/**
 * Instantiation of polymorphic entity.
 */
public record SInstantiate(
    List<SType> typeArgs, SPolyReference sPolyReference, SType evaluationType, Location location)
    implements SExpr {

  public SInstantiate(SPolyReference sPolyReference, Location location) {
    this(list(), sPolyReference, location);
    checkArgument(sPolyReference.schema().typeParams().isEmpty());
  }

  public SInstantiate(List<SType> typeArgs, SPolyReference sPolyReference, Location location) {
    this(typeArgs, sPolyReference, sPolyReference.schema().instantiate(typeArgs), location);
  }

  @Override
  public String toSourceCode(Collection<STypeVar> localTypeVars) {
    return sPolyReference.toSourceCode()
        + typeArgs.map(type -> type.specifier(localTypeVars)).toString("<", ", ", ">");
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SInstantiate")
        .addField("typeArgs", typeArgs.toString("<", ",", ">"))
        .addField("polymorphic", sPolyReference)
        .addField("evaluationType", evaluationType)
        .addField("location", location)
        .toString();
  }
}
