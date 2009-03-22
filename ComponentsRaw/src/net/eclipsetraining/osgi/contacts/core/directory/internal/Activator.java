package net.eclipsetraining.osgi.contacts.core.directory.internal;

import java.util.Properties;

import net.eclipsetraining.osgi.contacts.api.ContactRepository;
import net.eclipsetraining.osgi.contacts.api.ContactRepositoryListener;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

	// Hard code the name
	private static final String NAME = "Directory";
	
	private LogTracker logTracker;
	private ServiceTracker listenerTracker;
	
	private DirectoryRepositoryRaw repository;
	private ServiceRegistration registration;



	public void start(BundleContext context) throws Exception {
		logTracker = new LogTracker(context);
		logTracker.open();
		
		listenerTracker = new ServiceTracker(context, ContactRepositoryListener.class.getName(), null);
		listenerTracker.open();
		
		repository = new DirectoryRepositoryRaw("contacts", listenerTracker, logTracker);
		repository.activate();
		
		Properties properties = new Properties();
		properties.put(ContactRepository.NAME, NAME);
		registration = context.registerService(ContactRepository.class.getName(), repository, properties);
	}

	public void stop(BundleContext context) throws Exception {
		registration.unregister();
		repository.deactivate();
		listenerTracker.close();
		logTracker.close();
	}

}
