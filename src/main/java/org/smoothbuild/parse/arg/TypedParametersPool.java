package org.smoothbuild.parse.arg;

import java.util.Set;

import org.smoothbuild.lang.function.base.TypedName;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

/**
 * Pool of params that are assignable from argument with given type. For example
 * TypeParamsPool for smooth type 'File' will contain params with type 'File'
 * and 'Blob' as arguments of type 'File' can be assigned to 'Blob' param as
 * well.
 */
public class TypedParametersPool {
  private final Set<TypedName> optionalParameters;
  private final Set<TypedName> requiredParameters;

  public TypedParametersPool(Set<TypedName> optionalParameters, Set<TypedName> requiredParameters) {
    this.optionalParameters = optionalParameters;
    this.requiredParameters = requiredParameters;
  }

  public Set<TypedName> optionalParameters() {
    return optionalParameters;
  }

  public Set<TypedName> requiredParameters() {
    return requiredParameters;
  }

  public boolean hasCandidate() {
    return requiredParameters.size() == 1
        || (requiredParameters.size() == 0 && optionalParameters.size() == 1);
  }

  public TypedName candidate() {
    Preconditions.checkState(hasCandidate(), "No candidate available");
    if (requiredParameters.isEmpty()) {
      return optionalParameters.iterator().next();
    } else {
      return requiredParameters.iterator().next();
    }
  }

  public String toFormattedString() {
    return TypedName.iterableToString(Sets.union(requiredParameters, optionalParameters));
  }

  public boolean isEmpty() {
    return optionalParameters.isEmpty() && requiredParameters.isEmpty();
  }
}
