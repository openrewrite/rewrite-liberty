/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.java.liberty;

import com.google.errorprone.refaster.annotation.AfterTemplate;
import com.google.errorprone.refaster.annotation.BeforeTemplate;
import com.ibm.websphere.runtime.ServerName;

/**
 * Use `getProperty("wlp.server.name")` as `getDisplayName()` is not available in Liberty.
 */
public class ServerNameRefaster {
    @BeforeTemplate
    String getDisplayName() {
        return ServerName.getDisplayName();
    }

    @BeforeTemplate
    String getFullName() {
        return ServerName.getFullName();
    }

    @AfterTemplate
    String getProperty() {
        return java.lang.System.getProperty("wlp.server.name");
    }
}
