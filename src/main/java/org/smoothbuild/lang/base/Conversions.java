package org.smoothbuild.lang.base;

import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.NIL;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;

import com.google.common.collect.ImmutableMultimap;

public class Conversions {
  private static final ImmutableMultimap<SType<?>, SType<?>> CONVERSIONS = createConversions();

  public static boolean canConvert(SType<?> from, SType<?> to) {
    return from == to || CONVERSIONS.containsEntry(from, to);
  }

  private static ImmutableMultimap<SType<?>, SType<?>> createConversions() {
    ImmutableMultimap.Builder<SType<?>, SType<?>> builder = ImmutableMultimap.builder();

    builder.put(FILE, BLOB);
    builder.put(FILE_ARRAY, BLOB_ARRAY);
    builder.put(NIL, STRING_ARRAY);
    builder.put(NIL, BLOB_ARRAY);
    builder.put(NIL, FILE_ARRAY);

    return builder.build();
  }
}
