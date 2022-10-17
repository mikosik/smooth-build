package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.TagLoc;
import org.smoothbuild.compile.lang.base.Trace;

/**
 * Smooth stack trace.
 */
public class TraceS extends Trace<TagLoc> {
  public TraceS(String tag, Loc loc) {
    this(tag, loc, null);
  }

  public TraceS(String tag, Loc loc, TraceS chain) {
    super(new TagLoc(tag, loc), chain);
  }
}
