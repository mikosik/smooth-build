package org.smoothbuild.parse.err;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.problem.SourceLocation;
import org.smoothbuild.problem.Warning;

public class OverridenImportWarning extends Warning {
  public OverridenImportWarning(SourceLocation sourceLocation, String name, Name imported) {
    super(sourceLocation, "Function '" + name + "' overrides imported '" + imported.full() + "'");
  }
}
