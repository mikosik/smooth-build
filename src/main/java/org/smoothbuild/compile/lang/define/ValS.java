package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.collect.Lists.joinToString;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.Tanal;
import org.smoothbuild.compile.lang.type.TypeS;

/**
 * Named value.
 * This class is immutable.
 */
public sealed abstract class ValS extends Tanal implements NamedEvaluableS
    permits AnnValS, DefValS {
  public ValS(TypeS type, String name, Loc loc) {
    super(type, name, loc);
  }

  protected String valFieldsToString() {
    return joinToString("\n",
        "type = " + type(),
        "name = " + name(),
        "loc = " + loc()
    );
  }
}


