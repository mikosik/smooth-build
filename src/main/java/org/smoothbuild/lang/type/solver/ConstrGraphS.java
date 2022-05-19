package org.smoothbuild.lang.type.solver;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.VarS;
import org.smoothbuild.util.type.Bounds;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;

public record ConstrGraphS(
  ImmutableMap<VarS, Bounds<TypeS>> varBounds,
  ImmutableSetMultimap<VarS, VarS> constrs) {

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public String toString() {
    return varBounds.keySet().stream()
        .map(this::varDescription)
        .collect(joining("\n"));
  }

  private String varDescription(VarS var) {
    var bounds = varBounds.get(var);
    String boundsString = bounds.lower().name() + ", " + bounds.upper().name();
    String constrsString = toCommaSeparatedString(constrs.get(var), TypeS::name);
    return "%s (%s) < %s".formatted(var.name(), boundsString, constrsString);
  }

  public static class Builder {
    private final ImmutableMap.Builder<VarS, Bounds<TypeS>> varBounds;
    private final ImmutableSetMultimap.Builder<VarS, VarS> constrs;

    public Builder() {
      this.varBounds = ImmutableMap.builder();
      this.constrs = ImmutableSetMultimap.builder();
    }

    public Builder addVar(VarS var, Iterable<? extends VarS> uppers, Bounds<TypeS> bounds) {
      constrs.putAll(var, uppers);
      addBounds(var, bounds);
      return this;
    }

    public Builder addBounds(VarS var, Bounds<TypeS> bounds) {
      varBounds.put(var, bounds);
      return this;
    }

    public Builder addUpper(VarS lower, VarS upper) {
      constrs.put(lower, upper);
      return this;
    }

    public ConstrGraphS build() {
      return new ConstrGraphS(varBounds.build(), constrs.build());
    }
  }
}
