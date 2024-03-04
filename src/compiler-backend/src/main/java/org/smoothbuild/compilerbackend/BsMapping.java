package org.smoothbuild.compilerbackend;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;

public record BsMapping(Map<Hash, String> nameMapping, Map<Hash, Location> locMapping) {
  public BsMapping() {
    this(Map.map(), Map.map());
  }
}
