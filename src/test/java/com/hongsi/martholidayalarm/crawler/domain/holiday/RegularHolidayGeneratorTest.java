package com.hongsi.martholidayalarm.crawler.domain.holiday;

import static org.assertj.core.api.Assertions.assertThat;

import com.hongsi.martholidayalarm.mart.domain.Holiday;
import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

public class RegularHolidayGeneratorTest {

	private RegularHolidayGenerator regularHolidayGenerator;

	@Before
	public void setUp() {
		regularHolidayGenerator = RegularHolidayGenerator.parse("둘째,넷째주 수요일, 일요일");
	}

	@Test
	public void 텍스트로_객체_생성_확인() {
		RegularHolidayGenerator regularHolidayGenerator1 = RegularHolidayGenerator.parse("둘째주 일요일");
		assertThat(regularHolidayGenerator1.getRegularHolidays()).hasSize(1)
				.containsOnly(RegularHoliday.of(WeekWrapper.SECOND, DayOfWeekWrapper.SUNDAY));

		RegularHolidayGenerator regularHolidayGenerator2 = RegularHolidayGenerator
				.parse("넷 째주 수요일");
		assertThat(regularHolidayGenerator2.getRegularHolidays()).hasSize(1)
				.containsOnly(RegularHoliday.of(WeekWrapper.FOURTH, DayOfWeekWrapper.WEDNESDAY));
	}

	@Test
	public void 두개_이상의_휴일일_때_객체_생성_확인() {
		RegularHolidayGenerator regularHolidayGenerator = RegularHolidayGenerator
				.parse("둘째,넷째주 월요일, 일요일");

		assertThat(regularHolidayGenerator.getRegularHolidays()).hasSize(4)
				.containsExactly(
						RegularHoliday.of(WeekWrapper.SECOND, DayOfWeekWrapper.MONDAY),
						RegularHoliday.of(WeekWrapper.SECOND, DayOfWeekWrapper.SUNDAY),
						RegularHoliday.of(WeekWrapper.FOURTH, DayOfWeekWrapper.MONDAY),
						RegularHoliday.of(WeekWrapper.FOURTH, DayOfWeekWrapper.SUNDAY)
				);
	}

	@Test
	public void 두개_이상의_휴일일_때_객체_생성_확인2() {
		RegularHolidayGenerator regularHolidayGenerator = RegularHolidayGenerator
				.parse("(단, 매월 2,4번째 일요일 휴점)");

		assertThat(regularHolidayGenerator.getRegularHolidays()).hasSize(2)
				.containsExactly(
						RegularHoliday.of(WeekWrapper.SECOND, DayOfWeekWrapper.SUNDAY),
						RegularHoliday.of(WeekWrapper.FOURTH, DayOfWeekWrapper.SUNDAY)
				);
	}

	@Test(expected = IllegalArgumentException.class)
	public void 잘못된_Week_파라미터로_객체_생성시_예외() {
		RegularHolidayGenerator.parse("여섯째주월요일");
	}

	@Test(expected = IllegalArgumentException.class)
	public void 잘못된_DayOfMonth_파라미터로_객체_생성시_예외() {
		RegularHolidayGenerator.parse("첫째주 없는요일");
	}

	@Test
	public void 휴일_생성() {
		LocalDate startDate = LocalDate.of(2018, 5, 30);
		assertThat(regularHolidayGenerator.generate(startDate))
				.containsExactly(
						Holiday.of(2018, 6, 10),
						Holiday.of(2018, 6, 13),
						Holiday.of(2018, 6, 24),
						Holiday.of(2018, 6, 27)
				);
	}
}