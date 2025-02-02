package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Instantiation of polymorphic entity.
 */
public record SInstantiate(
    List<SType> typeArgs, SPolymorphic sPolymorphic, SType evaluationType, Location location)
    implements SExpr {

  public SInstantiate(SPolymorphic sPolymorphic, Location location) {
    this(list(), sPolymorphic, location);
    checkArgument(sPolymorphic.schema().quantifiedVars().isEmpty());
  }

  public SInstantiate(List<SType> typeArgs, SPolymorphic sPolymorphic, Location location) {
    this(typeArgs, sPolymorphic, sPolymorphic.schema().instantiate(typeArgs), location);
  }

  @Override
  public String toSourceCode() {
    return sPolymorphic.toSourceCode() + typeArgs.map(SType::specifier).toString("<", ", ", ">");
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SInstantiate")
        .addField("typeArgs", typeArgs.toString("<", ",", ">"))
        .addField("polymorphic", sPolymorphic)
        .addField("evaluationType", evaluationType)
        .addField("location", location)
        .toString();
  }
}
