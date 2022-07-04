package org.smoothbuild.parse.ast;

public sealed interface TopRefableP extends RefableP
    permits FuncP, MonoTopRefableP {
}
