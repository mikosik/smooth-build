package org.smoothbuild.backendcompile.testing;

import static org.smoothbuild.common.collect.Map.map;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.compilerbackend.BsMapping;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;

public class TestingBsMapping {
  public static BsMapping bsMapping() {
    return new BsMapping(map(), map());
  }

  public static BsMapping bsMapping(Hash hash, String name) {
    return new BsMapping(map(hash, name), map());
  }

  public static BsMapping bsMapping(Hash hash, Location location) {
    return new BsMapping(map(), map(hash, location));
  }
}
