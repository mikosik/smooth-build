package org.smoothbuild.compile.lang.define;

import java.math.BigInteger;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.IntTS;

public record IntS(IntTS type, BigInteger bigInteger, Loc loc) implements InstS {
}
