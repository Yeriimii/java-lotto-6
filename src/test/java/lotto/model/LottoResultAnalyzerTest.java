package lotto.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class LottoResultAnalyzerTest {
    @DisplayName("당첨 번호와 몇 개 일치하는지 확인")
    @ParameterizedTest(name = "당첨 번호: {0}, 구매한 로또 번호: {1}, 예상 일치 개수: {2}")
    @MethodSource("matchCase")
    public void 당첨_번호_일치_확인(List<Integer> winningNumbers, List<Integer> purchasedNumbers, int expectedMatchCount) {
        // given
        LottoResultAnalyzer lottoResultAnalyzer = new LottoResultAnalyzer();
        LottoPaper purchasedLottoPaper = new LottoPaper(purchasedNumbers);

        // when
        int matchCount = lottoResultAnalyzer.calculateMatchCount(purchasedLottoPaper, winningNumbers);

        // then
        assertEquals(expectedMatchCount, matchCount);
    }

    @DisplayName("당첨 번호 일치 개수를 로또 용지에 단일 입력 확인")
    @ParameterizedTest(name = "당첨 번호: {0}, 구매한 로또 번호: {1}, 예상 일치 개수: {2}")
    @MethodSource("matchCase")
    public void 당첨_번호_일치_개수_단일_입력(List<Integer> winningNumbers, List<Integer> purchasedNumbers, int expectedMatchCount) {
        // given
        LottoResultAnalyzer lottoResultAnalyzer = new LottoResultAnalyzer();
        LottoPaper purchasedLottoPaper = new LottoPaper(purchasedNumbers);

        // when
        lottoResultAnalyzer.writeResultToLottoPaper(purchasedLottoPaper, winningNumbers);

        // then
        assertEquals(expectedMatchCount, purchasedLottoPaper.getMatchingCount());
    }

    @DisplayName("당첨 번호 일치 개수를 로또 용지에 복수 입력 확인")
    @ParameterizedTest(name = "당첨 번호: {0}, 구매한 로또 번호: {1}, 예상 일치 개수: {2}")
    @MethodSource("matchCase")
    public void 당첨_번호_일치_개수_복수_입력(List<Integer> winningNumbers, List<Integer> purchasedNumbers, int expectedMatchCount) {
        // given
        List<LottoPaper> purchasedLottoPapers = new ArrayList<>();
        LottoResultAnalyzer lottoResultAnalyzer = new LottoResultAnalyzer();
        purchasedLottoPapers.add(new LottoPaper(purchasedNumbers));
        purchasedLottoPapers.add(new LottoPaper(purchasedNumbers));

        // when
        lottoResultAnalyzer.writeResultToLottoPapers(purchasedLottoPapers, winningNumbers);

        // then
        assertThat(purchasedLottoPapers)
                .allMatch(paper -> paper.getMatchingCount() == expectedMatchCount);
    }

    @DisplayName("당첨 번호 일치 개수에 따라 등수 확인")
    @ParameterizedTest(name = "보너스 번호: {1}")
    @MethodSource("matchCase2")
    public void 등수_판별(List<List<Integer>> lottoNumberArgument, int bonusNumber, PrizeCategory prizeCategory) {
        // given
        List<Integer> winningNumbers = lottoNumberArgument.get(0);
        List<Integer> purchasedNumbers = lottoNumberArgument.get(1);

        LottoResultAnalyzer lottoResultAnalyzer = new LottoResultAnalyzer();
        LottoPaper lottoPaper = new LottoPaper(purchasedNumbers);

        // when
        lottoResultAnalyzer.writeResultToLottoPaper(lottoPaper, winningNumbers);

        lottoResultAnalyzer.matchByLottoPaper(lottoPaper, bonusNumber);

        EnumMap<PrizeCategory, Integer> matchResults = lottoResultAnalyzer.getMatchResults();

        // then
        assertThat(matchResults.keySet())
                .containsExactly(prizeCategory);
    }

    static Stream<Arguments> matchCase() {
        return Stream.of(
                Arguments.of(
                        List.of(1, 2, 3, 4, 5, 6),
                        List.of(4, 5, 6, 7, 8, 9),
                        3
                ),
                Arguments.of(
                        List.of(1, 2, 3, 4, 5, 6),
                        List.of(10, 11, 12, 13, 14, 15),
                        0
                )
        );
    }

    static Stream<Arguments> matchCase2() {
        List<List<Integer>> lottoNumberArgumentFirst = new ArrayList<>();
        lottoNumberArgumentFirst.add(List.of(1, 2, 3, 4, 5, 6));
        lottoNumberArgumentFirst.add(List.of(1, 2, 3, 4, 5, 7));

        return Stream.of(
                Arguments.of(
                        lottoNumberArgumentFirst,
                        7,
                        PrizeCategory.FIVE_MATCH_WITH_BONUS
                ),
                Arguments.of(
                        lottoNumberArgumentFirst,
                        10,
                        PrizeCategory.FIVE_MATCH_NO_BONUS
                )
        );
    }

    @DisplayName("당첨 금액 총액 계산 확인")
    @ParameterizedTest(name = "{1} 일 때 {2}원")
    @MethodSource("totalWinningPrize")
    public void 당첨_금액_계산(EnumMap<PrizeCategory, Integer> matchResult, String Status, long expectedTotalPrize) {
        // given
        LottoResultAnalyzer lottoResultAnalyzer = new LottoResultAnalyzer();

        // when
        long actualTotalPrize = lottoResultAnalyzer.calculateTotalWinningPrize(matchResult);

        // then
        assertEquals(expectedTotalPrize, actualTotalPrize);
    }

    static Stream<Arguments> totalWinningPrize() {
        EnumMap<PrizeCategory, Integer> matchResultFirstCase = new EnumMap<>(PrizeCategory.class);
        matchResultFirstCase.put(PrizeCategory.THREE_MATCH, 5);
        matchResultFirstCase.put(PrizeCategory.FIVE_MATCH_WITH_BONUS, 2);

        EnumMap<PrizeCategory, Integer> matchResultSecondCase = new EnumMap<>(PrizeCategory.class);
        matchResultSecondCase.put(PrizeCategory.FOUR_MATCH, 2);
        matchResultSecondCase.put(PrizeCategory.SIX_MATCH, 2);

        return Stream.of(
                Arguments.of(
                        matchResultFirstCase,
                        "5등: 5개, 2등: 2개",
                        60025000L
                ),
                Arguments.of(
                        matchResultSecondCase,
                        "4등: 2개, 1등: 2개",
                        4000100000L
                )
        );
    }
}
