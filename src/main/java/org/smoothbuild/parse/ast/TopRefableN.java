package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.like.TopRefable;

public sealed interface TopRefableN extends RefableN, TopRefable
    permits FuncN, MonoTopRefableN {
}
