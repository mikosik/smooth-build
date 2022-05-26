package org.smoothbuild.lang.define;

import org.smoothbuild.util.collect.NList;

public record ModS(
    ModPath path,
    ModFiles files,
    NList<DefTypeS> types,
    NList<TopRefableS> topRefables) {
}
