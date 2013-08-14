package org.smoothbuild.parse.err;

import org.smoothbuild.lang.function.CanonicalName;
import org.smoothbuild.problem.SourceLocation;
import org.smoothbuild.problem.Warning;

public class OverridenImportWarning extends Warning {
  public OverridenImportWarning(SourceLocation sourceLocation, String name, CanonicalName imported) {
    super(sourceLocation, "Function '" + name + "' overrides imported '" + imported.full() + "'");
  }
}
