package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.TypeDefinition;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.type.SStructType;
import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Type definition.
 */
public record STypeDefinition(SType type, Id id, Location location) implements TypeDefinition {
  public String toSourceCode() {
    return switch (type) {
      case SStructType struct -> structToSourceCode(struct);
      default -> throw new RuntimeException();
    };
  }

  private static String structToSourceCode(SStructType struct) {
    return struct.name() + " {\n  "
        + struct.fields().list().map(SItemSig::toSourceCode).toString(",\n  ") + ",\n}";
  }

  @Override
  public String toString() {
    return new ToStringBuilder("STypeDefinition")
        .addField("type", type())
        .addField("id", id())
        .addField("location", location())
        .toString();
  }
}
