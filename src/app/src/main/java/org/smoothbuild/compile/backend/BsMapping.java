package org.smoothbuild.compile.backend;

import org.smoothbuild.common.Hash;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.compile.frontend.lang.base.location.Location;

public record BsMapping(Map<Hash, String> nameMapping, Map<Hash, Location> locMapping) {
  public BsMapping() {
    this(Map.map(), Map.map());
  }
}
