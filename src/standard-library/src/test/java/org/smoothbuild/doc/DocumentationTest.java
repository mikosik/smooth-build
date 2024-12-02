package org.smoothbuild.doc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.smoothbuild.evaluator.testing.EvaluatorTestContext;

public class DocumentationTest extends EvaluatorTestContext {
  @ParameterizedTest
  @ArgumentsSource(CodeExampleProvider.class)
  public void example_code_can_be_compiled(String code) throws IOException {
    createUserModule(code + "\n my_result = 7;");
    evaluate("my_result");
  }

  private static class CodeExampleProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
      return codeExamples().stream()
          .map(e -> Arguments.arguments(Named.of(e.file.getFileName() + ":" + e.line, e.code)));
    }
  }

  private static List<CodeExample> codeExamples() throws IOException {
    List<CodeExample> examples = new ArrayList<>();
    for (Path mdFile : mdFiles()) {
      examples.addAll(readCodeExamples(mdFile));
    }
    return examples;
  }

  private static List<Path> mdFiles() throws IOException {
    Path documentationDir = Path.of("../../doc").toAbsolutePath();
    try (Stream<Path> stream = Files.walk(documentationDir)) {
      return stream.filter(Files::isRegularFile).collect(Collectors.toList());
    }
  }

  private static List<CodeExample> readCodeExamples(Path mdFile) throws IOException {
    List<CodeExample> codeExamples = new ArrayList<>();
    boolean insideExample = false;
    StringBuilder code = new StringBuilder();
    int currentLine = 0;
    int firstLine = 0;
    try (Stream<String> stream = Files.lines(mdFile)) {
      for (String line : stream.toList()) {
        currentLine++;
        var trimmed = line.trim();
        if (trimmed.equals("```")) {
          insideExample = !insideExample;
          if (insideExample) {
            code = new StringBuilder();
            firstLine = currentLine;
          } else {
            codeExamples.add(new CodeExample(mdFile, firstLine, code.toString()));
          }
        } else {
          code.append(line);
          code.append("\n");
        }
      }
    }
    return codeExamples;
  }

  private record CodeExample(Path file, int line, String code) {}
}
