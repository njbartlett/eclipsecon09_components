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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.UIManager;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class Activator implements BundleActivator {

    private ContactViewerFrame frame;

    public void start(final BundleContext context) {
        // Use the system L&F if possible
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception e) {
            // Ignore
        }

        frame = new ContactViewerFrame();
        frame.pack();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                try {
                    context.getBundle().stop();
                } catch (final BundleException e1) {
                    // Ignore
                }
            }
        });

        frame.openTracking(context);
        frame.setVisible(true);
    }

    public void stop(final BundleContext context) {
        frame.setVisible(false);
        frame.closeTracking();
    }

}

