package org.smoothbuild.compile.sb;

import java.util.Map;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.compile.lang.base.Loc;

import com.google.common.collect.ImmutableMap;

public record BsMapping(
    ImmutableMap<Hash, String> nameMapping,
    ImmutableMap<Hash, Loc> locMapping) {
  public BsMapping() {
    this(ImmutableMap.of(), ImmutableMap.of());
  }

  public BsMapping(Map<Hash, String> nameMapping, Map<Hash, Loc> locMapping) {
    this(ImmutableMap.copyOf(nameMapping), ImmutableMap.copyOf(locMapping));
  }
}
