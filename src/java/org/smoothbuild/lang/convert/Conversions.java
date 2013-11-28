package org.smoothbuild.lang.convert;

import static org.smoothbuild.lang.type.STypes.BLOB;
import static org.smoothbuild.lang.type.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.type.STypes.EMPTY_ARRAY;
import static org.smoothbuild.lang.type.STypes.FILE;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.smoothbuild.lang.type.STypes.STRING_ARRAY;

import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.lang.type.SType;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

public class Conversions {

  /**
   * Maps smooth type to a map that contains keys being super-types of that type
   * and values being converters to each super-type.
   */
  private static final ImmutableMap<SType<?>, ImmutableMap<SType<?>, Converter<?>>> map =
      createMap();

  public static ImmutableSet<SType<?>> superTypesOf(SType<?> type) {
    return map.get(type).keySet();
  }

  public static boolean canConvert(SType<?> from, SType<?> to) {
    if (from == to) {
      return true;
    }
    return map.get(from).containsKey(to);
  }

  public static Converter<?> converter(SType<?> from, SType<?> to) {
    return map.get(from).get(to);
  }

  private static ImmutableMap<SType<?>, ImmutableMap<SType<?>, Converter<?>>> createMap() {
    Builder<SType<?>, ImmutableMap<SType<?>, Converter<?>>> builder = ImmutableMap.builder();

    builder.put(STRING, Empty.typeToConverterMap());
    builder.put(BLOB, Empty.typeToConverterMap());
    builder.put(FILE, convertersMap(new FileToBlobConverter()));

    builder.put(STRING_ARRAY, Empty.typeToConverterMap());
    builder.put(BLOB_ARRAY, Empty.typeToConverterMap());
    builder.put(FILE_ARRAY, convertersMap(new FileArrayToBlobArrayConverter()));

    Converter<?> nilToStringArray = new EmptyArrayToTypedArrayConverter<SString>(STRING_ARRAY);
    Converter<?> nilToBlobArray = new EmptyArrayToTypedArrayConverter<SBlob>(BLOB_ARRAY);
    Converter<?> nilToFileArray = new EmptyArrayToTypedArrayConverter<SFile>(FILE_ARRAY);

    builder.put(EMPTY_ARRAY, convertersMap(nilToStringArray, nilToBlobArray, nilToFileArray));

    return builder.build();
  }

  private static ImmutableMap<SType<?>, Converter<?>> convertersMap(Converter<?>... converters) {
    Builder<SType<?>, Converter<?>> builder = ImmutableMap.builder();
    for (Converter<?> converter : converters) {
      builder.put(converter.targetType(), converter);
    }
    return builder.build();
  }
}
