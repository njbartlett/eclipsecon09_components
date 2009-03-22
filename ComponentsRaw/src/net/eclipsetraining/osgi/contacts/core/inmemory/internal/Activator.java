package net.eclipsetraining.osgi.contacts.core.inmemory.internal;

import java.util.Properties;

import net.eclipsetraining.osgi.contacts.api.Contact;
import net.eclipsetraining.osgi.contacts.api.ContactRepository;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

	private ServiceRegistration dummiesReg;
	private ServiceRegistration nerdsReg;

	public void start(BundleContext context) throws Exception {
		InMemoryContactRepository repository;
		Properties props;
		
		// Dummies
		repository = new InMemoryContactRepository(new Contact[] {
				new Contact("Joe", "Bloggs"),
				new Contact("Max", "Mustermann")
		});
		
		props = new Properties();
		props.put("name", "Some Dummies");
		
		dummiesReg = context.registerService(ContactRepository.class.getName(), repository, props);
		
		// Nerds
		repository = new InMemoryContactRepository(new Contact[] {
				new Contact("Neil", "Bartlett"),
				new Contact("Heiko", "Seeberger")
		});
		
		props = new Properties();
		props.put("name", "OSGi Nerds");
		
		nerdsReg = context.registerService(ContactRepository.class.getName(), repository, props);
	}
	

	public void stop(BundleContext context) throws Exception {
		dummiesReg.unregister();
		nerdsReg.unregister();
	}

}
