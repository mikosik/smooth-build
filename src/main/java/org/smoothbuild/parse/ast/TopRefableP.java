package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.like.common.TopRefableC;

public sealed interface TopRefableP extends RefableP, TopRefableC
    permits FuncP, MonoTopRefableP {
}
