package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.collect.Lists.joinToString;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.Sanal;
import org.smoothbuild.compile.lang.type.SchemaS;

/**
 * Named value.
 * This class is immutable.
 */
public sealed abstract class NamedValueS extends Sanal implements NamedEvaluableS
    permits AnnValueS, DefValueS {
  public NamedValueS(SchemaS schema, String name, Loc loc) {
    super(schema, name, loc);
  }

  protected String fieldsToString() {
    return joinToString("\n",
        "schema = " + schema(),
        "name = " + name(),
        "loc = " + loc()
    );
  }
}


