package net.binis.codegen.spring.configuration;

/*-
 * #%L
 * code-generator-spring-configuration
 * %%
 * Copyright (C) 2021 - 2022 Binis Belev
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

import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import net.binis.codegen.jackson.CodeBeanDeserializerModifier;
import net.binis.codegen.jackson.CodeProxyTypeFactory;
import net.binis.codegen.spring.configuration.properties.CodeGenProperties;
import net.binis.codegen.spring.query.QueryProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class CodeGenSpringConfiguration {

    public CodeGenSpringConfiguration(CodeGenProperties properties) {
        if (properties.isShow_hql()) {
            log.info("Query logging enabled!");
            QueryProcessor.logQuery();
            if (properties.isShow_hql_params()) {
                log.info("Query params logging enabled!");
                QueryProcessor.logParams();
            }
        }
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        SimpleModule module = new SimpleModule();
        module.setDeserializerModifier(new CodeBeanDeserializerModifier());
        return builder -> builder.postConfigurer(c -> c.setTypeFactory(new CodeProxyTypeFactory(c.getTypeFactory()))).modulesToInstall(module);
    }

}
