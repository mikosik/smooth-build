package org.smoothbuild.lang.base.define;

import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public record ModS(
    ModPath path,
    ModFiles files,
    ImmutableList<ModS> referencedMods,
    NList<DefTypeS> types,
    NList<TopEvalS> topEvals) {
}
