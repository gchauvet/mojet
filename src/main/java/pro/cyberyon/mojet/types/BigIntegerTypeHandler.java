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

import java.math.BigInteger;

/**
 * Big integer data type handler
 *
 * @author Guillaume CHAUVET
 */
final class BigIntegerTypeHandler extends AbstractTypeHandler<BigInteger> {

	@Override
	protected boolean isAccept(Class<?> type) {
		return BigInteger.class == type;
	}

	@Override
	protected BigInteger doRead(String data, String format) {
		return new BigInteger(data);
	}

	@Override
	protected String doWrite(BigInteger data, String format) {
		return data.toString();
	}

}
