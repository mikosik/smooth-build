package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.ArrayTS;

import com.google.common.collect.ImmutableList;

public record OrderS(ArrayTS evalT, ImmutableList<ExprS> elems, Loc loc) implements OperS {
}
