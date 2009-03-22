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
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.osgi.framework.BundleContext;

public class ContactViewerFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final JTabbedPane tabbedPane;
    private ContactRepositoryTracker tracker;

    public ContactViewerFrame() {
        super("Contact Viewer");

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Welcome", createWelcomePanel());
        tabbedPane.setPreferredSize(new Dimension(500, 500));

        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }

    private Component createWelcomePanel() {
        final JLabel label = new JLabel("Welcome to the contact viewer demo. Please select a contacts folder.");

        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    protected void openTracking(final BundleContext context) {
        tracker = new ContactRepositoryTracker(context, tabbedPane);
        tracker.open();
    }

    protected void closeTracking() {
        tracker.close();
    }
}

