package tohear.hearo.archive.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class DiseaseTypeTest {

    @ParameterizedTest
    @EnumSource(DiseaseType.class)
    void mapsExactEnglishEnumNameToCategory(DiseaseType expected) {
        assertThat(DiseaseType.fromAiWord(expected.name())).isEqualTo(expected);
        assertThat(DiseaseType.fromAiWord(" " + expected.name() + " ")).isEqualTo(expected);
    }

    @ParameterizedTest
    @EnumSource(DiseaseType.class)
    void mapsKoreanLabelToCategory(DiseaseType expected) {
        assertThat(DiseaseType.fromAiWord(expected.getLabel())).isEqualTo(expected);
        assertThat(DiseaseType.fromAiWord(" " + expected.getLabel() + " ")).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"암", "장염", "없음", "미분류", "cold", "C OLD", "UNDEFINED_9", "INVALID"})
    void valuesOutsideEnumNamesAndLabelsFallBackToOther(String word) {
        assertThat(DiseaseType.fromAiWord(word)).isEqualTo(DiseaseType.OTHER);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void missingClassificationFallsBackToOther(String word) {
        assertThat(DiseaseType.fromAiWord(word)).isEqualTo(DiseaseType.OTHER);
    }
}
