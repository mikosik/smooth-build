package org.smoothbuild.lang.define;

public sealed interface MonoRefableS extends RefableS, ExprS
    permits FuncS, NamedValS {
}
