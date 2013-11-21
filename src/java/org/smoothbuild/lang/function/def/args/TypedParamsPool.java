package org.smoothbuild.lang.function.def.args;

import java.util.Set;

import org.smoothbuild.lang.function.base.Param;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

/**
 * Pool of params that are assignable from argument with given type. For example
 * TypeParamsPool for smooth type 'File' will contain params with type 'File'
 * and 'Blob' as arguments of type 'File' can be assigned to 'Blob' param as
 * well.
 */
public class TypedParamsPool {
  private final Set<Param> optionalParams;
  private final Set<Param> requiredParams;

  public TypedParamsPool(Set<Param> optionalParams, Set<Param> requiredParams) {
    this.optionalParams = optionalParams;
    this.requiredParams = requiredParams;
  }

  public Set<Param> optionalParams() {
    return optionalParams;
  }

  public Set<Param> requiredParams() {
    return requiredParams;
  }

  public boolean hasCandidate() {
    return requiredParams.size() == 1 || (requiredParams.size() == 0 && optionalParams.size() == 1);
  }

  public Param candidate() {
    Preconditions.checkState(hasCandidate(), "No candidate available");
    if (requiredParams.isEmpty()) {
      return optionalParams.iterator().next();
    } else {
      return requiredParams.iterator().next();
    }
  }

  public String toFormattedString() {
    return Param.paramsToString(Sets.union(requiredParams, optionalParams));
  }

  public int size() {
    return optionalParams.size() + requiredParams.size();
  }
}
