package org.smoothbuild.evaluator;

import static org.smoothbuild.compilerfrontend.lang.base.location.Locations.unknownLocation;

import jakarta.inject.Inject;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.compilerbackend.BsMapping;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.define.STrace;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;

public class BsTranslator {
  private final BsMapping bsMapping;

  @Inject
  public BsTranslator(BsMapping bsMapping) {
    this.bsMapping = bsMapping;
  }

  public STrace translate(BTrace bTrace) {
    return new STrace(translate(bTrace.elements()));
  }

  private STrace.Element translate(BTrace.Element headElement) {
    if (headElement == null) {
      return null;
    } else {
      var name = nameFor(headElement.called());
      var location = locationFor(headElement.call());
      var tailS = translate(headElement.tail());
      return new STrace.Element(name, location, tailS);
    }
  }

  public String nameFor(Hash funcHash) {
    return bsMapping.nameMapping().getOrDefault(funcHash, "???");
  }

  private Location locationFor(Hash hash) {
    return bsMapping.locMapping().getOrDefault(hash, unknownLocation());
  }
}
