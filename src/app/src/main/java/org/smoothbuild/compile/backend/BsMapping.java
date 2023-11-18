package org.smoothbuild.compile.backend;

import java.util.Map;

import org.smoothbuild.compile.frontend.lang.base.location.Location;
import org.smoothbuild.vm.bytecode.hashed.Hash;

import com.google.common.collect.ImmutableMap;

public record BsMapping(
    ImmutableMap<Hash, String> nameMapping,
    ImmutableMap<Hash, Location> locMapping) {
  public BsMapping() {
    this(ImmutableMap.of(), ImmutableMap.of());
  }

  public BsMapping(Map<Hash, String> nameMapping, Map<Hash, Location> locationMapping) {
    this(ImmutableMap.copyOf(nameMapping), ImmutableMap.copyOf(locationMapping));
  }
}
