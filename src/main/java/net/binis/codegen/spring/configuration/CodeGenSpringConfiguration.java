package net.binis.codegen.spring.configuration;

/*-
 * #%L
 * code-generator-spring-configuration
 * %%
 * Copyright (C) 2021 - 2026 Binis Belev
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
import net.binis.codegen.exception.MapperException;
import net.binis.codegen.exception.ValidationFormException;
import net.binis.codegen.factory.CodeFactory;
import net.binis.codegen.jackson.CodeBeanDeserializerModifier;
import net.binis.codegen.jackson.CodeProxyTypeFactory;
import net.binis.codegen.jackson.serialize.CodeEnumStringSerializer;
import net.binis.codegen.map.Mapper;
import net.binis.codegen.spring.configuration.properties.CodeGenProperties;
import net.binis.codegen.spring.mapping.keys.MappingKeys;
import net.binis.codegen.spring.query.QueryProcessor;
import net.binis.codegen.tools.Reflection;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.boot.jackson.autoconfigure.XmlMapperBuilderCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.module.SimpleModule;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Configuration
public class CodeGenSpringConfiguration {

    @SuppressWarnings("unchecked")
    public CodeGenSpringConfiguration(CodeGenProperties properties, ApplicationContext context) {
        if (properties.isShow_hql()) {
            log.info("Query logging enabled!");
            QueryProcessor.logQuery();
            if (properties.isShow_hql_params()) {
                log.info("Query params logging enabled!");
                QueryProcessor.logParams();
            }
        }

        if (isNull(CodeFactory.getProjectionProvider())) {
            var factory = new SpelAwareProxyProjectionFactory();
            CodeFactory.setProjectionProvider((cls, projections) -> obj -> factory.createProjection(projections[0], obj));
        }

        CodeFactory.registerForeignFactory((cls, params) -> nonNull(params) || params.length == 0 ? context.getBean(cls) : context.getBean(cls, params));
        Mapper.map().key(MappingKeys.JSON).source(Object.class).destination(String.class).producer(o -> {
            try {
                return CodeFactory.create(ObjectMapper.class).writeValueAsString(o);
            } catch (Exception e) {
                return "{ \"exception\": \"" + e.getMessage() + "\"}";
            }
        });
        Mapper.registerMapper(String.class, Object.class, MappingKeys.JSON, (source, destination) -> {
            try {
                return CodeFactory.create(ObjectMapper.class).readerForUpdating(destination).readValue(source);
            } catch (ValidationFormException v) {
                throw v;
            } catch (Exception e) {
                throw new MapperException(e);
            }
        });

        var xml = Reflection.loadClass("tools.jackson.dataformat.xml.XmlMapper");
        if (nonNull(xml)) {
            Mapper.map().key(MappingKeys.XML).source(Object.class).destination(String.class).producer(o -> {
                try {
                    return ((ObjectMapper) CodeFactory.create(xml)).writeValueAsString(o);
                } catch (Exception e) {
                    return "<exception>" + e.getMessage() + "</exception>";
                }
            });
            Mapper.registerMapper(String.class, Object.class, MappingKeys.XML, (source, destination) -> {
                try {
                    return ((ObjectMapper) CodeFactory.create(xml)).readerForUpdating(destination).readValue(source);
                } catch (ValidationFormException v) {
                    throw v;
                } catch (Exception e) {
                    throw new MapperException(e);
                }
            });
        }

    }

    @Bean
    public JsonMapperBuilderCustomizer jsonCustomizer() {
        var module = new SimpleModule();
        module.setDeserializerModifier(new CodeBeanDeserializerModifier());
        module.addSerializer(new CodeEnumStringSerializer());
        return builder -> builder.typeFactory(new CodeProxyTypeFactory(builder.typeFactory())).addModule(module);
    }

    /* If XML handling is required add this code to your code.   Potentially
        @Bean
        public XmlMapperBuilderCustomizer xmlCustomizer() {
            var module = new SimpleModule();
            module.setDeserializerModifier(new CodeBeanDeserializerModifier());
            module.addSerializer(new CodeEnumStringSerializer());
            return builder -> builder.typeFactory(new CodeProxyTypeFactory(builder.typeFactory())).addModule(module);
        }
    */

}
