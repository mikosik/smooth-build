package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.like.TopRefable;

public sealed interface TopRefableP extends RefableP, TopRefable
    permits FuncP, MonoTopRefableP {
}
