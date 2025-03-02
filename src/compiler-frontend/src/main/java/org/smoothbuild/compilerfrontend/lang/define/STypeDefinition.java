package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.TypeDefinition;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.name.Name;
import org.smoothbuild.compilerfrontend.lang.type.SStructType;
import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Type definition.
 */
public record STypeDefinition(SType type, Fqn fqn, Location location) implements TypeDefinition {
  @Override
  public Name name() {
    return fqn.parts().getLast();
  }

  public String toSourceCode() {
    return switch (type) {
      case SStructType struct -> structToSourceCode(struct);
      default -> throw new RuntimeException();
    };
  }

  private static String structToSourceCode(SStructType struct) {
    return struct.fqn() + " {\n  "
        + struct.fields().list().map(SItemSig::toSourceCode).toString(",\n  ") + ",\n}";
  }

  @Override
  public String toString() {
    return new ToStringBuilder("STypeDefinition")
        .addField("type", type())
        .addField("fqn", fqn())
        .addField("location", location())
        .toString();
  }
}
