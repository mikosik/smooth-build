package org.smoothbuild.parse.err;

import org.smoothbuild.function.base.QualifiedName;
import org.smoothbuild.problem.SourceLocation;
import org.smoothbuild.problem.Warning;

public class OverridenImportWarning extends Warning {
  public OverridenImportWarning(SourceLocation sourceLocation, String name, QualifiedName imported) {
    super(sourceLocation, "Function '" + name + "' overrides imported '" + imported.full() + "'");
  }
}
