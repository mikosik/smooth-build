package org.smoothbuild.lang.define;

import org.smoothbuild.lang.base.Nal;
import org.smoothbuild.lang.like.RefableObj;

/**
 * Top level evaluable.
 */
public sealed interface RefableObjS extends RefableObj, RefableS, ObjS, Nal
    permits MonoRefableObjS, PolyRefableObjS {
}
