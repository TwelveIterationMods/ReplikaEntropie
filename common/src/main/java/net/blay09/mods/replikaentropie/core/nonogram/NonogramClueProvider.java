package net.blay09.mods.replikaentropie.core.nonogram;

public interface NonogramClueProvider {
    NonogramClues clues();

    boolean validate(NonogramState nonogramState);
}
