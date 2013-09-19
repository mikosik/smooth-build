package org.smoothbuild.parse.def;

import java.util.Set;

import org.smoothbuild.function.base.Param;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

public class TypedParamsPool {
  private final Set<Param> params;
  private final Set<Param> requiredParams;

  public TypedParamsPool(TypedParamsPool pool1, TypedParamsPool pool2) {
    this.params = Sets.union(pool1.params, pool2.params);
    this.requiredParams = Sets.union(pool1.requiredParams, pool2.requiredParams);
  }

  public TypedParamsPool() {
    this.params = Sets.<Param> newHashSet();
    this.requiredParams = Sets.<Param> newHashSet();
  }

  public void add(Param param) {
    if (param.isRequired()) {
      requiredParams.add(param);
    } else {
      params.add(param);
    }
  }

  public Iterable<Param> requiredParams() {
    return requiredParams;
  }

  public boolean hasCandidate() {
    return requiredParams.size() == 1 || (requiredParams.size() == 0 && params.size() == 1);
  }

  public boolean remove(Param param) {
    if (param.isRequired()) {
      return requiredParams.remove(param);
    } else {
      return params.remove(param);
    }
  }

  public Param candidate() {
    Preconditions.checkState(hasCandidate(), "No candidate available");
    if (requiredParams.isEmpty()) {
      return params.iterator().next();
    } else {
      return requiredParams.iterator().next();
    }
  }

  public String toFormattedString() {
    return Param.paramsToString(Sets.union(requiredParams, params));
  }

  public int size() {
    return params.size() + requiredParams.size();
  }
}
