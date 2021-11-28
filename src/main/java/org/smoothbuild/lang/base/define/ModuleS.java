package org.smoothbuild.lang.base.define;

import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public record ModuleS(
    ModulePath path,
    ModuleFiles files,
    ImmutableList<ModuleS> referencedModules,
    NList<DefinedType> types,
    NList<TopEvaluableS> referencables) {
}
