package com.hongsi.martholidayalarm.common.mart.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.hongsi.martholidayalarm.common.mart.domain.Holiday;
import com.hongsi.martholidayalarm.common.mart.domain.Mart;
import com.hongsi.martholidayalarm.common.mart.domain.MartType;
import com.hongsi.martholidayalarm.common.mart.dto.MartDto;
import com.hongsi.martholidayalarm.common.mart.repository.MartRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MartServiceTest {

	@Autowired
	MartService martService;

	@Autowired
	MartRepository martRepository;

	@After
	public void cleanup() {
		martRepository.deleteAll();
	}

	@Test
	public void 마트_전체_저장() {
		List<Mart> marts = new ArrayList<>();
		Mart mart = Mart.builder()
				.martType(MartType.EMART)
				.realId("1")
				.build();
		mart.addHoliday(Holiday.builder()
				.mart(mart)
				.date(LocalDate.of(2018, 3, 31))
				.build());
		marts.add(mart);
		martService.saveAll(marts);

		marts = new ArrayList<>();
		mart = Mart.builder()
				.martType(MartType.EMART)
				.realId("1")
				.branchName("성수점")
				.build();
		mart.addHoliday(Holiday.builder()
				.mart(mart)
				.date(LocalDate.of(2018, 3, 31))
				.build());
		marts.add(mart);
		martService.saveAll(marts);

		marts = martRepository.findAll();
		assertThat(marts).isNotEmpty().hasSize(1);
		assertThat(marts.get(0).getHolidays()).isNotEmpty().hasSize(1);
		assertThat(marts.get(0).getBranchName()).isEqualTo("성수점");
	}

	@Test
	public void 마트타입_조회() {
		List<Mart> marts = new ArrayList<>();
		marts.add(Mart.builder()
				.realId("1")
				.martType(MartType.EMART)
				.region("서울")
				.build());
		marts.add(Mart.builder()
				.realId("2")
				.martType(MartType.EMART)
				.region("경상")
				.build());
		marts.add(Mart.builder()
				.realId("1")
				.martType(MartType.LOTTEMART)
				.region("서울")
				.build());
		marts.add(Mart.builder()
				.realId("2")
				.martType(MartType.LOTTEMART)
				.region("경상")
				.build());
		martRepository.saveAll(marts);

		List<MartType> martTypes = martService.getMartTypes();
		List<MartType> usingMartTypes = Arrays.stream(MartType.values())
				.collect(Collectors.toList());

		assertThat(usingMartTypes).isNotEmpty().hasSize(martTypes.size());
	}

	@Test
	public void 마트타입별_지역_조회() {
		List<Mart> marts = new ArrayList<>();
		marts.add(Mart.builder()
				.realId("1")
				.martType(MartType.EMART)
				.region("서울")
				.build());
		marts.add(Mart.builder()
				.realId("2")
				.martType(MartType.EMART)
				.region("경상")
				.build());
		marts.add(Mart.builder()
				.realId("3")
				.martType(MartType.EMART)
				.region("경기")
				.build());
		martRepository.saveAll(marts);

		List<String> regions = martService.getRegions(MartType.EMART);

		assertThat(regions).isNotEmpty().hasSize(marts.size());
		assertThat(regions).containsOnly("경기", "경상", "서울");
	}

	@Test
	public void 마트타입별_지점_조회() {
		List<Mart> marts = new ArrayList<>();
		marts.add(Mart.builder()
				.realId("1")
				.martType(MartType.EMART)
				.region("서울")
				.branchName("서울점")
				.build());
		marts.add(Mart.builder()
				.realId("2")
				.martType(MartType.EMART)
				.region("서울")
				.branchName("강남점")
				.build());
		marts.add(Mart.builder()
				.realId("3")
				.martType(MartType.EMART)
				.region("경상")
				.branchName("경상점")
				.build());
		marts.add(Mart.builder()
				.realId("1")
				.martType(MartType.LOTTEMART)
				.region("경기")
				.branchName("경기점")
				.build());
		martRepository.saveAll(marts);

		List<String> branches = martService.getBranches(MartType.EMART, "서울");

		assertThat(branches).isNotEmpty().hasSize(2);
		assertThat(branches).containsExactlyInAnyOrder("강남점", "서울점");
	}

	@Test
	public void 마트타입으로_MartDTO_리스트조회() {
		List<Mart> savedMarts = new ArrayList<>();
		Mart mart = Mart.builder()
				.martType(MartType.EMART)
				.realId("1")
				.build();
		mart.addHoliday(Holiday.builder()
				.mart(mart)
				.date(LocalDate.of(2018, 8, 9))
				.build());
		savedMarts.add(mart);
		martService.saveAll(savedMarts);

		String martTypeStr = "emart";
		List<MartDto> marts = martService.getMartsByMartType(martTypeStr);

		assertThat(marts).isNotEmpty().hasSize(1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void 잘못된_마트타입으로_MartDTO_리스트조회() {
		String martTypeStr = "honghong";
		List<MartDto> marts = martService.getMartsByMartType(martTypeStr);

		verify(martService, never()).getMartsByMartType(any(String.class));
	}
}