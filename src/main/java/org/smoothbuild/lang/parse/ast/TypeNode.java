package org.smoothbuild.lang.parse.ast;

import static org.smoothbuild.lang.base.type.Types.isVariableName;
import static org.smoothbuild.lang.base.type.Types.nothingT;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.util.CountersMap;

import com.google.common.collect.ImmutableList;

public class TypeNode extends NamedNode {
  public TypeNode(String name, Location location) {
    super(name, location);
  }

  public boolean isNothing() {
    return name().equals(nothingT().name());
  }

  public boolean isPolytype() {
    return isVariableName(name());
  }

  public ImmutableList<String> variablesUsedOnce() {
    var counters = new CountersMap<String>();
    countVariables(counters);
    return counters.keysWithCounter(1);
  }

  public void countVariables(CountersMap<String> countersMap) {
    if (isVariableName(name())) {
      countersMap.increment(name());
    }
  }
}
