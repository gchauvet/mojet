/*
 * Copyright 2025 Guillaume CHAUVET.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pro.cyberyon.mojet;

import lombok.Data;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test
 *
 * @author Guillaume CHAUVET
 */
class MojetLineMapperTest {

    /**
     * Main test pojo class
     */
    @Data
    @Record
    @Filler(length = 5, value = '_')
    public static class RootPojo {

        @Fragment(length = 5)
        private long id;
        @Filler(length = 3, value = '0')
        @Filler(length = 2, value = '#')
        @Record
        private ChildPojo child;
        @Fragment(length = 3)
        int counter;
        @Filler(length = 3)
        @Fragment(length = 5, padder = '0')
        @Occurences(3)
        long[] values;
    }

    /**
     * Child pojo test class.
     */
    @Data
    @Record
    public static class ChildPojo {

        @Fragment(length = 6)
        private long total;
        @Fragment(length = 3)
        private String label;
    }

    @Test
    void testSimpleReadLineToRootPojo() throws Exception {
        final MojetLineMapper<RootPojo> mapper = new MojetLineMapper(RootPojo.class);
        final RootPojo result = mapper.mapLine("01985000##114273EUR567   100011000210003_____", 1);
        assertEquals(1985, result.getId());
        assertEquals(114273, result.getChild().getTotal());
        assertEquals("EUR", result.getChild().getLabel());
        assertEquals(567, result.getCounter());
        assertArrayEquals(new long[]{10001, 10002, 10003}, result.getValues());
    }

    @Data
    @Record
    public static class BadFragmentPojo {

        @Fragment(length = -1)
        private int value1;
        @Fragment(length = 0)
        private int value2;
    }

    @Data
    @Record
    public static class BadPaddingPojo {

        @Filler(length = -1)
        private int value1;
        @Filler(length = 0)
        private int value2;
    }

    @Data
    @Record
    public static class BadRecordPojo {

        private ChildPojo value2;
    }

    @Test
    void testBadPojoSetting() {
        assertThrows(MojetRuntimeException.class, () -> new MojetLineMapper(BadFragmentPojo.class));
        assertThrows(MojetRuntimeException.class, () -> new MojetLineMapper(BadPaddingPojo.class));
        assertThrows(MojetRuntimeException.class, () -> new MojetLineMapper(BadRecordPojo.class));
    }

}
