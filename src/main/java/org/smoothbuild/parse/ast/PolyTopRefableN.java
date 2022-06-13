package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.like.PolyTopRefable;

public sealed interface PolyTopRefableN extends TopRefableN, PolyTopRefable
    permits FuncN {
}
