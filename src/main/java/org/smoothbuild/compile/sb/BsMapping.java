package org.smoothbuild.compile.sb;

import java.util.Map;

import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.compile.lang.base.Loc;

import com.google.common.collect.ImmutableMap;

public record BsMapping(
    ImmutableMap<ExprB, String> nameMapping,
    ImmutableMap<ExprB, Loc> locMapping) {
  public BsMapping() {
    this(ImmutableMap.of(), ImmutableMap.of());
  }

  public BsMapping(Map<ExprB, String> nameMapping, Map<ExprB, Loc> locMapping) {
    this(ImmutableMap.copyOf(nameMapping), ImmutableMap.copyOf(locMapping));
  }
}
