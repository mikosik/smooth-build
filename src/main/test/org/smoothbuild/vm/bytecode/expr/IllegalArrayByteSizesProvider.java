package org.smoothbuild.vm.bytecode.expr;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public class IllegalArrayByteSizesProvider implements ArgumentsProvider {
  @Override
  public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
    return IntStream.rangeClosed(1, Hash.lengthInBytes() * 3 + 1)
        .filter(i -> i % Hash.lengthInBytes() != 0)
        .mapToObj(Arguments::of);
  }
}
