package net.eclipsetraining.osgi.contacts.api;
/*******************************************************************************
 * Copyright (c) 2008 WeigleWilczek and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Heiko Seeberger - initial implementation
 ******************************************************************************/



public interface ContactRepository {

    static final String NAME = "name";

    Iterable<Contact> getAll();
}
