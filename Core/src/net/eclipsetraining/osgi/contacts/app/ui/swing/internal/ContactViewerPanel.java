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

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.eclipsetraining.osgi.contacts.api.ContactRepository;
import net.eclipsetraining.osgi.contacts.api.ContactRepositoryListener;

public class ContactViewerPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    
	private final ContactRepositoryTableModel tableModel;

    public ContactViewerPanel(final ContactRepository repository) {
        tableModel = new ContactRepositoryTableModel(repository);
        final JTable table = new JTable(tableModel);
        final JScrollPane scrollPane = new JScrollPane(table);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

	public ContactRepositoryListener getContactRepositoryListener() {
		return tableModel; 
	}
}

