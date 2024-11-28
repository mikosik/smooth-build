package org.smoothbuild.common.log.report;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.log.location.Location;

public record BExprAttributes(Map<Hash, String> names, Map<Hash, Location> locations) {
  public BExprAttributes() {
    this(Map.map(), Map.map());
  }
}
