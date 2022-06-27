package org.smoothbuild.lang.type.solver;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;
import static org.smoothbuild.util.type.Side.LOWER;
import static org.smoothbuild.util.type.Side.UPPER;

import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.lang.type.VarS;
import org.smoothbuild.util.type.Bounds;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;

public record ConstrGraphS(
  ImmutableMap<VarS, Bounds<MonoTS>> varBounds,
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
    var boundsString = bounds.lower().name() + ", " + bounds.upper().name();
    var lowerNeighbours = toCommaSeparatedString(neighbours(var, LOWER), MonoTS::name);
    var upperNeighbours = toCommaSeparatedString(neighbours(var, UPPER), MonoTS::name);
    return "%s < %s (%s) < %s".formatted(
        lowerNeighbours, var.name(), boundsString, upperNeighbours);
  }

  public static class Builder {
    private final ImmutableMap.Builder<VarS, Bounds<MonoTS>> varBounds;
    private final ImmutableSetMultimap.Builder<VarS, VarS> constrs;

    public Builder() {
      this.varBounds = ImmutableMap.builder();
      this.constrs = ImmutableSetMultimap.builder();
    }

    public Builder addVar(VarS var, Iterable<? extends VarS> uppers, Bounds<MonoTS> bounds) {
      constrs.putAll(var, uppers);
      addBounds(var, bounds);
      return this;
    }

    public Builder addBounds(VarS var, Bounds<MonoTS> bounds) {
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
