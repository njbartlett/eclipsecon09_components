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

package net.eclipsetraining.osgi.contacts.core.inmemory.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import net.eclipsetraining.osgi.contacts.api.Contact;
import net.eclipsetraining.osgi.contacts.api.ContactRepository;

public class InMemoryContactRepository implements ContactRepository {

    private final Collection<Contact> contacts;

    public InMemoryContactRepository(final Contact[] contacts) {
        if (contacts == null) this.contacts = new ArrayList<Contact>();
        else this.contacts = new ArrayList<Contact>(Arrays.asList(contacts));
    }

    public Iterable<Contact> getAll() {
        // Prevent modifications through casting to (Array)List
        return Collections.unmodifiableCollection(contacts);
    }
}
