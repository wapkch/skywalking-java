/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.apm.toolkit.activation.micrometer;

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;

import java.net.URI;

import org.apache.skywalking.apm.util.StringUtil;

class SpanHelper {

    static String tryToGetPeer(String remoteAddress, String remoteName, KeyValues allKeyValues) {
        if (remoteAddress != null) {
            return remoteAddress;
        }
        String result = allKeyValues
                               .stream()
                               .filter(keyValue -> "http.url".equalsIgnoreCase(
                                   keyValue.getKey()) || "uri".equalsIgnoreCase(keyValue.getKey())
                                   || keyValue.getKey().contains("uri") || keyValue.getKey().contains("url")
                               )
                               .findFirst()
                               .map(KeyValue::getValue)
                               .orElse("unknown");
        try {
            URI uri = URI.create(result);
            if (uri.getScheme() != null && uri.getHost() != null) {
                return uri.getScheme() + "://" + uri.getHost() + ":" + uri.getPort();
            }
            if (!StringUtil.isEmpty(remoteName)) {
                return remoteName;
            }
            if (uri.getPath() != null) {
                return uri.getPath();
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }
}
