/*******************************************************************************
 * Copyright (c) 2008 WeigleWilczek and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Neil Bartlett - initial implementation
 ******************************************************************************/

package net.eclipsetraining.osgi.contacts.app.ui.swing.internal;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import net.eclipsetraining.osgi.contacts.api.Contact;
import net.eclipsetraining.osgi.contacts.api.ContactRepository;
import net.eclipsetraining.osgi.contacts.api.ContactRepositoryListener;

public class ContactRepositoryTableModel extends AbstractTableModel implements ContactRepositoryListener {

    private static final long serialVersionUID = 1L;

    private final List<Contact> contacts = new ArrayList<Contact>();

    public ContactRepositoryTableModel(final ContactRepository repository) {
        for (final Contact contact : repository.getAll()) {
        	contacts.add(contact);
        }
    }

    public int getColumnCount() {
        return 2;
    }

    public synchronized int getRowCount() {
        return contacts.size();
    }

    public Object getValueAt(final int rowIndex, final int columnIndex) {
        Contact contact;
        synchronized(this) {
            contact = contacts.get(rowIndex);
        }
        switch (columnIndex) {
        case 0:
            return contact.getFirstName();
        case 1:
            return contact.getLastName();
        default:
            return "ERROR";
        }
    }

    @Override
    public String getColumnName(final int column) {
        switch (column) {
        case 0:
            return "First Name";
        case 1:
            return "Last Name";
        default:
            return "ERROR";
        }
    }

    public void contactsAdded(Contact[] newContacts) {
    	final int firstNewRow;
    	final int lastNewRow;
    	
    	synchronized (this) {
	    	firstNewRow = contacts.size();
	    	for (Contact contact : newContacts) {
				contacts.add(contact);
			}
	    	lastNewRow = contacts.size() - 1;
    	}
    	
    	SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				fireTableRowsInserted(firstNewRow, lastNewRow);
			}
    	});
    }

	public void contactsRemoved(final Contact[] removedContacts) {
		synchronized (this) {
			for (Contact contact : removedContacts) {
				contacts.remove(contact);
			}
		}
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				fireTableDataChanged();
			}
		});
	}
}

