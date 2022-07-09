package net.binis.codegen.spring.actuator;

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

import net.binis.codegen.async.AsyncDispatcher;
import net.binis.codegen.async.monitoring.DispatcherMonitor;
import net.binis.codegen.async.monitoring.ExecutorMonitor;
import net.binis.codegen.factory.CodeFactory;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Endpoint(id = "codegen-async")
public class CodeGenActuatorEndpoint {

    @ReadOperation
    public CodeGenActuatorModel health() {
        var details = new LinkedHashMap<String, Object>();
        var dispatcher = CodeFactory.create(AsyncDispatcher.class);

        if (dispatcher instanceof DispatcherMonitor) {
            var monitor = (DispatcherMonitor) dispatcher;
            details.put("flows", monitor.flows());
        }

        return build(details);
    }

    private CodeGenActuatorModel build(Map<String, Object> details) {
        var health = new CodeGenActuatorModel();
        health.setHealthDetails(details);
        return health;
    }

    @ReadOperation
    public CodeGenActuatorModel flow(@Selector String flow) {
        if ("all".equals(flow)) {
            return all();
        }

        var dispatcher = CodeFactory.create(AsyncDispatcher.class);

        Map<String, Object> details;
        if (dispatcher instanceof DispatcherMonitor) {
            var monitor = (DispatcherMonitor) dispatcher;
            details = buildFlowStats(flow, monitor);
        } else {
            details = Collections.emptyMap();
        }

        return build(details);
    }

    private CodeGenActuatorModel all() {
        var details = new LinkedHashMap<String, Object>();
        var dispatcher = CodeFactory.create(AsyncDispatcher.class);

        if (dispatcher instanceof DispatcherMonitor) {
            var monitor = (DispatcherMonitor) dispatcher;
            for (var flow : monitor.flows()) {
                details.put(flow, buildFlowStats(flow, monitor));
            }
        }

        return build(details);
    }

    private Map<String, Object> buildFlowStats(String name, DispatcherMonitor monitor) {
        var details = new LinkedHashMap<String, Object>();

            var executor = monitor.getExecutor(name);
            if (executor instanceof ExecutorMonitor) {
                var m = (ExecutorMonitor) executor;
                details.put("pool-size", m.getPoolSize());
                details.put("core-pool-size", m.getCorePoolSize());
                details.put("largest-pool-size", m.getLargestPoolSize());
                details.put("maximum-pool-size", m.getMaximumPoolSize());
                details.put("active-count", m.getActiveCount());
                details.put("task-count", m.getTaskCount());
                details.put("completed-task-count", m.getCompletedTaskCount());
                details.put("keep-alive-time", m.getKeepAliveTime(TimeUnit.MILLISECONDS));
                details.put("queue-size", m.getQueueSize());
            }

        return details;
    }

}
