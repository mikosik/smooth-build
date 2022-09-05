package org.smoothbuild.compile.lang.define;

public sealed interface MonoRefableS extends RefableS, ValS
    permits FuncS, NamedValS {
}
