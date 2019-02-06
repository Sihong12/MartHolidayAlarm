package com.hongsi.martholidayalarm.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PhoneValidatorTest {

	@Test
	public void isValidPhoneNumber() {
		assertValidation("02-1234-4560", true);
		assertValidation("051-1234-4560", true);
		assertValidation("051-123-4560", true);
		assertValidation("+82-1899-9900", true);
	}

	@Test
	public void isInvalidPhoneNumber() {
		assertValidation("02-12-4560", false);
		assertValidation("02-123-456", false);
		assertValidation("0233-123-4567", false);
	}

	private void assertValidation(String phoneNumber, boolean isValid) {
		assertThat(PhoneValidator.isValid(phoneNumber)).isEqualTo(isValid);
	}
}