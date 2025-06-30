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
import lombok.Data;

/**
 * Main test pojo class
 */
@Data
@Record
@Filler(length = 5, value = '_')
public class RootPojo implements RecordVisitable<PojoVisitor> {

	@Fragment(length = 5, padder = '0')
	private long id;
	@Filler(length = 3, value = '0')
	@Filler(length = 2, value = '#')
	@Record
	private ChildPojo child;
	@Fragment(length = 3)
	private int counter;
	@Filler(length = 3)
	@Fragment(length = 5, padder = '0')
	@Occurences(value = 3)
	private long[] values;
	@Converter(value = MyLocalDateTypeHandler.class)
	@Fragment(length = 6, format = "yyyyMM")
	private LocalDate date;

	@Override
	public void accept(PojoVisitor visitor) {
		visitor.visit(this);
	}

}
