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

import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import net.eclipsetraining.osgi.contacts.api.ContactRepository;
import net.eclipsetraining.osgi.contacts.api.ContactRepositoryListener;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class ContactRepositoryTracker extends ServiceTracker {

    private final JTabbedPane tabbedPane;

    public ContactRepositoryTracker(final BundleContext context, final JTabbedPane tabbedPane) {
        super(context, ContactRepository.class.getName(), null);
        this.tabbedPane = tabbedPane;
    }

    @Override
    public Object addingService(final ServiceReference reference) {
        final String repoName = (String) reference.getProperty(ContactRepository.NAME);

        final ContactRepository repository = (ContactRepository) context.getService(reference);

        final Callable<Pair<ContactViewerPanel,ServiceRegistration>> callable = new Callable<Pair<ContactViewerPanel,ServiceRegistration>>() {
            public Pair<ContactViewerPanel,ServiceRegistration> call() {
            	try {
            	Pair<ContactViewerPanel,ServiceRegistration> result;
            	
                final ContactViewerPanel panel = new ContactViewerPanel(repository);
                final String title = (repoName != null) ? repoName : "<unknown>";

                tabbedPane.addTab(title, panel);

                // Publish the ContactRepositoryListener service
                Properties props = new Properties();
                props.put(ContactRepository.NAME, title);
                ServiceRegistration registration = context.registerService(ContactRepositoryListener.class.getName(), panel.getContactRepositoryListener(), props);
                
                result = new Pair<ContactViewerPanel, ServiceRegistration>(panel, registration);

                return result;
            	} catch (Exception e) {
            		e.printStackTrace();
            		return null;
            	}
            }
        };

        final FutureTask<Pair<ContactViewerPanel, ServiceRegistration>> task = new FutureTask<Pair<ContactViewerPanel,ServiceRegistration>>(callable);
        SwingUtilities.invokeLater(task);

        return task;
    }

    @Override
    public void removedService(final ServiceReference reference, final Object service) {
        @SuppressWarnings("unchecked")
        final Future<Pair<ContactViewerPanel,ServiceRegistration>> task = (Future<Pair<ContactViewerPanel, ServiceRegistration>>) service;


        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                	Pair<ContactViewerPanel, ServiceRegistration> pair = task.get();
                	if(pair != null) {
                        tabbedPane.remove(pair.getA());
                        try {
							pair.getB().unregister();
						} catch (IllegalStateException e) {
							// Ignore - service was already unregistered, probably by shutdown of the bundle
						}
                    }
                } catch (final InterruptedException e) {
                    // Restore interruption status
                    Thread.currentThread().interrupt();
                } catch (final ExecutionException e) {
                    // The panel was never successfully created, so doesn't need to be removed
                }
            }
        });

        context.ungetService(reference);
    }
}

