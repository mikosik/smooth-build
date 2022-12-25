package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.collect.Iterables.joinToString;

import org.smoothbuild.compile.lang.base.Sanal;
import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.compile.lang.type.SchemaS;

/**
 * Named value.
 * This class is immutable.
 */
public sealed abstract class NamedValueS
    extends Sanal
    implements NamedEvaluableS
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


