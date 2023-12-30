package org.smoothbuild.compile.backend;

import org.smoothbuild.common.collect.Map;
import org.smoothbuild.compile.frontend.lang.base.location.Location;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public record BsMapping(Map<Hash, String> nameMapping, Map<Hash, Location> locMapping) {
  public BsMapping() {
    this(Map.map(), Map.map());
  }
}
