package org.smoothbuild.run.err;

import java.util.Collection;

import org.smoothbuild.function.base.QualifiedName;
import org.smoothbuild.problem.Error;

public class UnknownFunctionError extends Error {

  public UnknownFunctionError(QualifiedName name, Collection<QualifiedName> availableNames) {
    super(null, "Unknown function " + name + "\nOnly following function are available: "
        + availableNames.toString());
  }
}
