package org.smoothbuild.lang.define;

/**
 * Top level refable.
 */
public sealed interface TopRefableS extends RefableS
    permits FuncS, MonoTopRefableS, PolyTopRefableS {
}
