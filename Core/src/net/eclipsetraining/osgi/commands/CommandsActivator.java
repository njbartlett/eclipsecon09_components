package net.eclipsetraining.osgi.commands;

import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;

public class CommandsActivator implements BundleActivator {

	private ServiceTracker pkgAdminTracker;
	private ServiceRegistration registration;

	public void start(BundleContext context) throws Exception {
		pkgAdminTracker = new ServiceTracker(context, PackageAdmin.class.getName(), null);
		pkgAdminTracker.open();
		registration = context.registerService(CommandProvider.class.getName(), new InstallCommands(context, pkgAdminTracker), null);
	}

	public void stop(BundleContext context) throws Exception {
		registration.unregister();
		pkgAdminTracker.close();
	}

}
