package org.smoothbuild.compile.fs;

import static org.smoothbuild.compile.fs.lang.define.LoadInternalMod.loadInternalModule;
import static org.smoothbuild.compile.fs.ps.PsTranslator.translatePs;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.compile.fs.fp.FpTranslator;
import org.smoothbuild.compile.fs.lang.define.DefinitionsS;
import org.smoothbuild.compile.fs.lang.define.ModFiles;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.out.log.MaybeProcessor;

/**
 * F->S Translator.
 * Translates source code F-ile to S-objects.
 */
public class FsTranslator {
  private final FpTranslator fpTranslator;

  @Inject
  public FsTranslator(FpTranslator fpTranslator) {
    this.fpTranslator = fpTranslator;
  }

  public Maybe<DefinitionsS> translateFs(List<ModFiles> files) {
    return new Translator(fpTranslator, files)
        .process();
  }

  private static class Translator extends MaybeProcessor<DefinitionsS> {
    private final FpTranslator fpTranslator;
    private final List<ModFiles> files;

    public Translator(FpTranslator fpTranslator, List<ModFiles> files) {
      this.fpTranslator = fpTranslator;
      this.files = files;
    }

    @Override
    protected DefinitionsS processImpl() throws FailedException {
      var internalModule = loadInternalModule();
      var definitionsS = DefinitionsS.empty().withModule(internalModule);
      for (ModFiles modFiles : files) {
        var moduleP = addLogsAndGetValue(fpTranslator.translateFp(modFiles));
        var moduleS = addLogsAndGetValue(translatePs(moduleP, definitionsS));
        definitionsS = definitionsS.withModule(moduleS);
      }
      return definitionsS;
    }
  }
}
