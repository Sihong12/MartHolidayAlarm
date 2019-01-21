package com.hongsi.martholidayalarm.domain.crawler.mart;

import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.databind.JsonNode;
import com.hongsi.martholidayalarm.client.location.converter.LocationConvertClient;
import com.hongsi.martholidayalarm.domain.crawler.AbstractMartCrawler;
import com.hongsi.martholidayalarm.domain.crawler.CrawlerMartData;
import com.hongsi.martholidayalarm.domain.crawler.CrawlerMartType;
import com.hongsi.martholidayalarm.utils.HtmlParser;
import com.hongsi.martholidayalarm.utils.JsonParser;
import java.util.List;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class LotteMartCrawler extends AbstractMartCrawler {

	private static final String REGION_URL = CrawlerMartType.LOTTEMART
			.appendUrl("/bc/branch/holidaystore.do?SITELOC=DC009");
	private static final String REAL_ID_URL = CrawlerMartType.LOTTEMART
			.appendUrl("/bc/branch/regnstorelist.json?regionCode=");
	private static final String DATA_URL = CrawlerMartType.LOTTEMART
			.appendUrl("/bc/branch/storeinfo.json?brnchCd=");
	private static final String REGION_BUTTON_SELECTOR = "div.wrap-location-inner > button";
	private static final String REAL_ID_KEY = "brnchCd";
	private static final String DATA_KEY = "data";

	public LotteMartCrawler(LocationConvertClient locationConvertClient) {
		super(locationConvertClient);
	}

	@Override
	public List<CrawlerMartData> crawl() {
		return parseRegionCode().stream()
				.flatMap(regionCode -> parseRealId(regionCode).stream())
				.distinct()
				.map(this::parseData)
				.map(super::parse)
				.collect(toList());
	}

	private List<String> parseRegionCode() {
		return HtmlParser.get(REGION_URL)
				.select(REGION_BUTTON_SELECTOR).stream()
				.map(Element::val)
				.collect(toList());
	}

	private List<String> parseRealId(String regionCode) {
		String url = REAL_ID_URL + regionCode;
		JsonNode dataNode = JsonParser.parse(url).get(DATA_KEY);
		return dataNode.findValuesAsText(REAL_ID_KEY);
	}

	private LotteMartData parseData(String realId) {
		String url = DATA_URL + realId;
		JsonNode dataNode = JsonParser.parse(url).get(DATA_KEY);
		return JsonParser.convert(dataNode, LotteMartData.class);
	}
}