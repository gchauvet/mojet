/*
 * Copyright 2025 Guillaume CHAUVET.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pro.cyberyon.mojet;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import pro.cyberyon.mojet.types.AbstractTypeHandler;

/**
 *
 * @author Guillaume CHAUVET
 */
final class MyLocalDateTypeHandler extends AbstractTypeHandler<LocalDate> {

	@Override
	protected boolean isAccept(Class<?> type) {
		return LocalDate.class == type;
	}

	@Override
	protected LocalDate doRead(String data, String format) {
		final DateTimeFormatter sfd = DateTimeFormatter.ofPattern("dd" + format, Locale.FRENCH);
		return LocalDate.parse("01" + data, sfd);
	}

	@Override
	protected String doWrite(LocalDate data, String format) {
		final DateTimeFormatter sfd = DateTimeFormatter.ofPattern("dd" + format, Locale.FRENCH);
		return data.format(sfd).substring(2);
	}

}
