package org.smoothbuild.lang.base.define;

import org.smoothbuild.util.collect.NList;

public record ModS(
    ModPath path,
    ModFiles files,
    NList<DefTypeS> types,
    NList<TopEvalS> topEvals) {
}
