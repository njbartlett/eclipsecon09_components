package net.eclipsetraining.osgi.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;

public class InstallCommands implements CommandProvider {
	
	private static final String HELP_INSTALL_DIR = "installDir <dir> - Install all bundles found in the specified directory.";
	
	private final BundleContext context;
	private final ServiceTracker pkgAdminTracker;

	public InstallCommands(BundleContext context, ServiceTracker pkgAdminTracker) {
		this.context = context;
		this.pkgAdminTracker = pkgAdminTracker;
	}

	public String getHelp() {
		StringBuilder buf = new StringBuilder();
		
		buf.append("---Bulk Bundle Management Commands---\n");
		buf.append('\t').append(HELP_INSTALL_DIR).append('\n');
		
		return buf.toString();
	}
	
	public void _installDir(CommandInterpreter ci) {
		String dirName = ci.nextArgument();
		if(dirName == null) {
			ci.println("Usage: " + HELP_INSTALL_DIR);
			return;
		}
		
		File dir = new File(dirName);
		if(!dir.isDirectory()) {
			ci.println("Directory does not exist, or is not a directory");
			return;
		}

		List<File> toInstall = new LinkedList<File>();
		File[] files = dir.listFiles(new FilenameFilter(){
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".jar");
			}
		});
		for (File file : files) {
			try {
				JarFile jar = new JarFile(file);
				Attributes attributes = jar.getManifest().getMainAttributes();
				String symbolicName = (String) attributes.getValue(Constants.BUNDLE_SYMBOLICNAME);
				if(symbolicName != null) {
					ci.println("Adding bundle " + symbolicName);
					toInstall.add(file);
				}
			} catch (IOException e) {
				ci.println("Failed to read file " + file.getName());
			}
		}
		
		List<Bundle> installed = new LinkedList<Bundle>();
		if(toInstall.isEmpty()) {
			ci.println("Nothing to install");
		} else {
			ci.println(String.format("Attempting to install %d bundle(s).", toInstall.size()));
			
			for (File file : toInstall) {
				try {
					Bundle bundle = context.installBundle(file.toURI().toString(), new FileInputStream(file));
					installed.add(bundle);
				} catch (FileNotFoundException e) {
					// File deleted just as we were about to install it
				} catch (BundleException e) {
					ci.println("Failed to install '" + file.getName() + "'");
				}
			}
			ci.println(String.format("Successfully installed %d bundle(s).", installed.size()));
		}
		
		PackageAdmin pkgAdmin = (PackageAdmin) pkgAdminTracker.getService();
		if(pkgAdmin != null) {
			pkgAdmin.refreshPackages((Bundle[]) installed.toArray(new Bundle[installed.size()]));
		}
	}

}
