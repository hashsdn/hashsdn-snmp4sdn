/*
 * Copyright (c) 2015 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn.internal;

import java.util.Collection;

//import org.opendaylight.controller.sal.binding.api.AbstractBindingAwareProvider;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ConsumerContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.switchdb.rev150901.SwitchDbService;
import org.opendaylight.yangtools.yang.binding.RpcService;
import org.osgi.framework.BundleContext;

import org.opendaylight.snmp4sdn.core.IController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchDbProvider implements BindingAwareProvider, AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(SwitchDbProvider.class);

    private BindingAwareBroker broker;
    private BundleContext context;

    SwitchDbImpl swdbImpl;

    IController controller = null;

    public SwitchDbProvider() {
        swdbImpl = new SwitchDbImpl();
        if(swdbImpl == null){
            logger.debug("ERROR: SwitchDbProvider: SwitchDbImpl() creation fail");
        }
        logger.debug("SwitchDbProvider creation complete");
    }

    //The following three functions (getImplementations(), getFunctionality(), onSessionInitialized()), seems to be deprecated in yangtools ver. 0.7.0-SNAPSHOT

    /*@Override//seems deprecated in yangtools 0.7.0-SNAPSHOT
    public Collection<? extends RpcService> getImplementations() {
        return null;
    }

    @Override//seems deprecated in yangtools 0.7.0-SNAPSHOT
    public Collection<? extends ProviderFunctionality> getFunctionality() {
        return null;
    }

    @Override//seems deprecated in yangtools 0.7.0-SNAPSHOT
    public void onSessionInitialized(ConsumerContext session) {
        // NOOP
    }*/

    @Override
    public void onSessionInitiated(ProviderContext session) {
        swdbImpl.init();
        session.addRpcImplementation(SwitchDbService.class, swdbImpl);
        logger.debug("SwitchDbProvider: onSessionInitiated(): done");
    }

    /*
    @Override
    protected void startImpl(BundleContext context) {
    }
    */

    @Override
    public void close() {
    }

    public BundleContext getContext() {
        return context;
    }

    public void setContext(BundleContext context) {
        this.context = context;
    }

    public BindingAwareBroker getBroker() {
        return broker;
    }

    public void setBroker(BindingAwareBroker broker) {
        this.broker = broker;
        registerProvider();
    }

    public void unsetBroker(BindingAwareBroker broker) {
        this.broker = null;
    }

    private boolean hasAllDependencies(){
        if(this.broker != null/* && this.switchConnectionProvider != null*/) {
            return true;
        }
        return false;
    }

    private void registerProvider() {
        if(hasAllDependencies()) {
            this.broker.registerProvider(this,context);
        }
    }

    public void setController(IController core) {
        this.controller = (IController)core;
        swdbImpl.setController(controller);
    }

    public void unsetController(IController core) {
        if (this.controller == (IController)core) {
            this.controller = null;
        }
    }

}

