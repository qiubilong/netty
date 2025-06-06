/*
 * Copyright 2012 The Netty Project
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
package io.netty.testsuite.transport.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketProtocolFamily;
import io.netty.testsuite.transport.AbstractComboTestsuiteTest;
import io.netty.testsuite.transport.TestsuitePermutation;
import io.netty.util.NetUtil;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

public abstract class AbstractDatagramTest extends AbstractComboTestsuiteTest<Bootstrap, Bootstrap> {
    @Override
    protected List<TestsuitePermutation.BootstrapComboFactory<Bootstrap, Bootstrap>> newFactories() {
        return SocketTestPermutation.INSTANCE.datagram(socketInternetProtocalFamily());
    }

    @Override
    protected void configure(Bootstrap bootstrap, Bootstrap bootstrap2, ByteBufAllocator allocator) {
        bootstrap.option(ChannelOption.ALLOCATOR, allocator);
        bootstrap2.option(ChannelOption.ALLOCATOR, allocator);
    }

    protected SocketAddress newSocketAddress() {
        switch (socketInternetProtocalFamily()) {
            case INET:
                return new InetSocketAddress(NetUtil.LOCALHOST4, 0);
            case INET6:
                return new InetSocketAddress(NetUtil.LOCALHOST6, 0);
            default:
                throw new AssertionError();
        }
    }

    protected boolean isSupported(SocketProtocolFamily family, InetAddress address) {
        switch (family) {
            case INET:
                return address instanceof Inet4Address;
            case INET6:
                return address instanceof Inet6Address;
            default:
                return false;
        }
    }

    protected SocketProtocolFamily socketProtocolFamily() {
        return SocketProtocolFamily.INET;
    }

    protected SocketProtocolFamily groupInternetProtocalFamily() {
        return socketProtocolFamily();
    }

    protected SocketProtocolFamily socketInternetProtocalFamily() {
        return socketProtocolFamily();
    }
}
