package net.binis.codegen.spring;

/*-
 * #%L
 * code-generator-hibernate
 * %%
 * Copyright (C) 2021 - 2024 Binis Belev
 * %%
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
 * #L%
 */

import lombok.extern.slf4j.Slf4j;
import net.binis.codegen.map.Mapper;
import net.binis.codegen.spring.mapping.keys.MappingKeys;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@SpringBootTest(classes = TestApplication.class)
public class ConfigIntegrationTest {

    @Test
    void test() {

        var list = List.of();
        Mapper.map(list, String.class, MappingKeys.JSON);

        var set = Set.of();
        Mapper.map(set, String.class, MappingKeys.JSON);

        var map = Map.of();
        Mapper.map(map, String.class, MappingKeys.JSON);

        list = List.of(5);
        Mapper.map(list, String.class, MappingKeys.JSON);

        set = Set.of(5);
        Mapper.map(set, String.class, MappingKeys.JSON);

        map = Map.of("5", 5);
        Mapper.map(map, String.class, MappingKeys.JSON);


    }

}
