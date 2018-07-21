package com.hongsi.martholidayalarm.crawler.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.hongsi.martholidayalarm.mart.domain.Mart;
import org.junit.Test;

public class EmartPageTest {

	@Test
	public void getInfo() throws Exception {
		String realId = "1038";
		EmartPage page = new EmartPage(EmartPage.BASE_URL + "/branch/view.do?id=" + realId);
		Mart mart = page.getInfo();
		assertThat(mart).isNotNull();
		assertThat(mart.getRealId()).isEqualTo(realId);
		assertThat(mart.getBranchName()).isNotBlank();
		assertThat(mart.getAddress()).isNotBlank();
		assertThat(mart.getPhoneNumber()).isNotBlank();
		assertThat(mart.getRegion()).isNotEmpty();
	}
}