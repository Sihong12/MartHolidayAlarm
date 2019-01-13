package com.hongsi.martholidayalarm.api.bot.kakao.service;

import com.hongsi.martholidayalarm.api.bot.kakao.domain.Button;
import com.hongsi.martholidayalarm.api.bot.kakao.domain.UserRequest;
import com.hongsi.martholidayalarm.api.bot.kakao.dto.BotResponse;
import com.hongsi.martholidayalarm.api.bot.kakao.dto.Keyboard;
import com.hongsi.martholidayalarm.api.bot.kakao.dto.Message;
import com.hongsi.martholidayalarm.api.bot.kakao.repository.KakaoBotRepository;
import com.hongsi.martholidayalarm.mart.domain.MartType;
import com.hongsi.martholidayalarm.mart.dto.MartResponse;
import com.hongsi.martholidayalarm.mart.dto.MartTypeResponse;
import com.hongsi.martholidayalarm.mart.service.MartService;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class KakaoBotService {

	private final MartService martService;

	private final KakaoBotRepository kakaoBotRepository;

	public BotResponse parse(UserRequest userRequest) {
		UserRequest beforeRequest = kakaoBotRepository.findByUserKey(userRequest.getUserKey());
		if (userRequest.isSame(beforeRequest)) {
			throw new IllegalStateException("중복된 요청입니다.");
		}
		userRequest.readyToUpdate(beforeRequest);
		kakaoBotRepository.save(userRequest);
		return new BotResponse(getMessageForResponse(userRequest),
				getKeyboardForResponse(userRequest));
	}

	private Message getMessageForResponse(UserRequest userRequest) {
		Button selectedButton = userRequest.getButton();
		if (selectedButton == Button.BRANCH) {
			MartType martType = MartType
					.of(userRequest.getBeforeRequest(Button.MARTTYPE));
			String branchName = userRequest.getBeforeRequest(Button.BRANCH);
			MartResponse mart = martService.findMartByMartTypeAndBranchName(martType, branchName);
			return new Message(mart);
		}
		return new Message(selectedButton.getMessage());
	}

	private Keyboard getKeyboardForResponse(UserRequest userRequest) {
		return new Keyboard(getButtons(userRequest));
	}

	private List<String> getButtons(UserRequest userRequest) {
		try {
			switch (userRequest.getButton()) {
				case DEFAULT:
					return martService.findMartTypes().stream()
							.map(MartTypeResponse::getDisplayName)
							.collect(Collectors.toList());
				case MARTTYPE:
					String beforeRequest = userRequest.getBeforeRequest(Button.MARTTYPE);
					return martService.findRegionsByMartType(MartType.of(beforeRequest));
				case REGION:
					MartType martType = MartType.of(userRequest.getBeforeRequest(Button.MARTTYPE));
					String region = userRequest.getBeforeRequest(Button.REGION);
					return martService.findBranchNamesByMartTypeAndRegion(martType, region);
			}
		} catch (IllegalArgumentException e) {
			log.error("Can not create response : " + e.getMessage());
		}
		return Keyboard.getDefaultKeyboardToList();
	}

	@Transactional
	public BotResponse reset(String userKey) {
		deleteUserRequest(userKey);
		Message wrongMessage = new Message("잘못된 요청입니다 다시 선택해주세요");
		Keyboard defaultKeyboard = new Keyboard(Keyboard.DEFAULT_KEYBOARD);
		return new BotResponse(wrongMessage, defaultKeyboard);
	}

	@Transactional
	public void deleteUserRequest(String userKey) {
		kakaoBotRepository.deleteByUserKey(userKey);
	}
}
