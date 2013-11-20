package org.smoothbuild.lang.function.def.args;

import java.util.Set;

import org.smoothbuild.lang.function.base.Param;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

public class TypedParamsPool {
  private final Set<Param> optionalParams;
  private final Set<Param> requiredParams;

  public TypedParamsPool(TypedParamsPool pool1, TypedParamsPool pool2, TypedParamsPool pool3) {
    Set<Param> optional1 = pool1.optionalParams;
    Set<Param> optional2 = pool2.optionalParams;
    Set<Param> optional3 = pool3.optionalParams;
    this.optionalParams = Sets.union(optional1, Sets.union(optional2, optional3));

    Set<Param> required1 = pool1.requiredParams;
    Set<Param> required2 = pool2.requiredParams;
    Set<Param> required3 = pool3.requiredParams;
    this.requiredParams = Sets.union(required1, Sets.union(required2, required3));
  }

  public TypedParamsPool() {
    this.optionalParams = Sets.<Param> newHashSet();
    this.requiredParams = Sets.<Param> newHashSet();
  }

  public void add(Param param) {
    if (param.isRequired()) {
      requiredParams.add(param);
    } else {
      optionalParams.add(param);
    }
  }

  public Iterable<Param> optionalParams() {
    return optionalParams;
  }

  public Iterable<Param> requiredParams() {
    return requiredParams;
  }

  public boolean hasCandidate() {
    return requiredParams.size() == 1 || (requiredParams.size() == 0 && optionalParams.size() == 1);
  }

  public boolean remove(Param param) {
    if (param.isRequired()) {
      return requiredParams.remove(param);
    } else {
      return optionalParams.remove(param);
    }
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
