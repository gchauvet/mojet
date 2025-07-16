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
package pro.cyberyon.mojet.types;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang3.StringUtils;

/**
 * local date data type handler
 *
 * @author Guillaume CHAUVET
 */
final class LocalDateTypeHandler extends AbstractTypeHandler<LocalDate> {

	@Override
	protected boolean isAccept(Class<?> type) {
		return LocalDate.class == type;
	}

	@Override
	public LocalDate read(String data, String format) {
		return StringUtils.isNotBlank(data) ? LocalDate.parse(data, getFormatter(format)) : null;
	}

	@Override
	public String write(LocalDate data, String format) {
		return data != null ? getFormatter(format).format(data) : "";
	}

	private static DateTimeFormatter getFormatter(String format) {
		return StringUtils.isEmpty(format) ? DateTimeFormatter.ISO_DATE : DateTimeFormatter.ofPattern(format);
	}

}
