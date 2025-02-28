package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.Collection;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;

/**
 * Reference to {@link SMonoReferenceable}.
 */
public record SMonoReference(SType type, Id referencedId, Location location)
    implements SReference, SExpr {
  @Override
  public String toSourceCode(Collection<STypeVar> localTypeVars) {
    return referencedId.toString();
  }

  @Override
  public SType evaluationType() {
    return type;
  }

  public String toSourceCode() {
    return referencedId.toString();
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SMonoReference")
        .addField("type", type)
        .addField("referencedName", referencedId)
        .addField("location", location)
        .toString();
  }
}
