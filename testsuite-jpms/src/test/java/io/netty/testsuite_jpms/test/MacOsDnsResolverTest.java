/*
 * Copyright 2024 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.netty.testsuite_jpms.test;

import io.netty.resolver.dns.DnsServerAddressStream;
import io.netty.resolver.dns.DnsServerAddressStreamProvider;
import io.netty.resolver.dns.macos.MacOSDnsServerAddressStreamProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@EnabledOnOs(OS.MAC)
public class MacOsDnsResolverTest {

    @Test
    public void testSimple() {
        MacOSDnsServerAddressStreamProvider.ensureAvailability();
        DnsServerAddressStreamProvider provider = new MacOSDnsServerAddressStreamProvider();
        DnsServerAddressStream stream = provider.nameServerAddressStream("netty.io");
        assertNotNull(stream);
        assertNotEquals(0, stream.size());

        for (int i = 0; i < stream.size(); i++) {
            assertNotEquals(0, stream.next().getPort());
        }
    }
}
