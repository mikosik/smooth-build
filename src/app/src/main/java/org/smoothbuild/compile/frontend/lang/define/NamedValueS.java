package org.smoothbuild.compile.frontend.lang.define;

import static org.smoothbuild.common.collect.Iterables.joinToString;

import org.smoothbuild.compile.frontend.lang.base.location.Location;
import org.smoothbuild.compile.frontend.lang.type.SchemaS;

/**
 * Named value.
 * This class is immutable.
 */
public sealed abstract class NamedValueS
    extends NamedEvaluableS
    permits AnnotatedValueS, NamedExprValueS {
  public NamedValueS(SchemaS schema, String name, Location location) {
    super(schema, name, location);
  }

  protected String fieldsToString() {
    return joinToString("\n",
        "schema = " + schema(),
        "name = " + name(),
        "location = " + location()
    );
  }
}


