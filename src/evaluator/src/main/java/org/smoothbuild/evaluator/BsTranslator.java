package org.smoothbuild.evaluator;

import static org.smoothbuild.compilerfrontend.lang.base.location.Locations.unknownLocation;

import jakarta.inject.Inject;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.compilerbackend.BsMapping;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.define.STrace;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace.Line;

public class BsTranslator {
  private final BsMapping bsMapping;

  @Inject
  public BsTranslator(BsMapping bsMapping) {
    this.bsMapping = bsMapping;
  }

  public STrace translate(BTrace bTrace) {
    return new STrace(translate(bTrace.topLine()));
  }

  private STrace.Line translate(Line headLine) {
    if (headLine == null) {
      return null;
    } else {
      var name = nameFor(headLine.called());
      var location = locationFor(headLine.call());
      var next = translate(headLine.next());
      return new STrace.Line(name, location, next);
    }
  }

  public String nameFor(Hash bExprHash) {
    return bsMapping.nameMapping().getOrDefault(bExprHash, "???");
  }

  private Location locationFor(Hash hash) {
    return bsMapping.locMapping().getOrDefault(hash, unknownLocation());
  }
}
