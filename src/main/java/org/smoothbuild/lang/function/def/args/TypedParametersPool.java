package org.smoothbuild.lang.function.def.args;

import java.util.Set;

import org.smoothbuild.lang.function.base.Parameter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

/**
 * Pool of params that are assignable from argument with given type. For example
 * TypeParamsPool for smooth type 'File' will contain params with type 'File'
 * and 'Blob' as arguments of type 'File' can be assigned to 'Blob' param as
 * well.
 */
public class TypedParametersPool {
  private final Set<Parameter> optionalParameters;
  private final Set<Parameter> requiredParameters;

  public TypedParametersPool(Set<Parameter> optionalParameters, Set<Parameter> requiredParameters) {
    this.optionalParameters = optionalParameters;
    this.requiredParameters = requiredParameters;
  }

  public Set<Parameter> optionalParams() {
    return optionalParameters;
  }

  public Set<Parameter> requiredParams() {
    return requiredParameters;
  }

  public boolean hasCandidate() {
    return requiredParameters.size() == 1 || (requiredParameters.size() == 0 && optionalParameters.size() == 1);
  }

  public Parameter candidate() {
    Preconditions.checkState(hasCandidate(), "No candidate available");
    if (requiredParameters.isEmpty()) {
      return optionalParameters.iterator().next();
    } else {
      return requiredParameters.iterator().next();
    }
  }

  public String toFormattedString() {
    return Parameter.parametersToString(Sets.union(requiredParameters, optionalParameters));
  }

  public int size() {
    return optionalParameters.size() + requiredParameters.size();
  }
}
