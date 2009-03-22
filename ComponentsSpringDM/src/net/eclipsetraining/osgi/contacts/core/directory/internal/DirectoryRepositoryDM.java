package net.eclipsetraining.osgi.contacts.core.directory.internal;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.eclipsetraining.osgi.contacts.api.Contact;
import net.eclipsetraining.osgi.contacts.api.ContactRepository;
import net.eclipsetraining.osgi.contacts.api.ContactRepositoryListener;
import net.eclipsetraining.osgi.dirmon.DirectoryChangeListener;
import net.eclipsetraining.osgi.dirmon.DirectoryMonitor;

import org.osgi.service.log.LogService;

public class DirectoryRepositoryDM implements ContactRepository, DirectoryChangeListener {

	private static final String FILENAME_SUFFIX = ".contact";

	// Configured state
	private String dirName;
	private Iterable<ContactRepositoryListener> listeners;
	private LogService log;
	
	// Live state
	private Thread thread;
	private final List<Contact> contacts = new ArrayList<Contact>();
	
	// Setters/Getters
	public String getDirName() {
		return dirName;
	}

	public void setDirName(String dirName) {
		this.dirName = dirName;
	}

	public Iterable<ContactRepositoryListener> getListeners() {
		return listeners;
	}

	public void setListeners(Iterable<ContactRepositoryListener> listeners) {
		this.listeners = listeners;
	}

	public LogService getLog() {
		return log;
	}

	public void setLog(LogService log) {
		this.log = log;
	}

	// Activate/Deactivate
	
	protected void activate() throws Exception {
		// Directory property
		if (dirName == null) {
			throw new Exception("Missing 'directory' property.");
		}
		File directory = new File(dirName);
		if (!directory.isDirectory()) {
			throw new Exception(
					"Specified directory does not exist or is not a directory ("
							+ dirName + ").");
		}
		
		FileFilter filter = new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().toLowerCase().endsWith(FILENAME_SUFFIX);
			}
		};

		// Start the monitor thread
		thread = new DirectoryMonitor(directory, filter, this);
		thread.start();

		log(LogService.LOG_INFO, "----> Started Directory Repository monitoring thread on " + directory);
	}

	protected void deactivate() {
		thread.interrupt();
		
		log(LogService.LOG_INFO, "----> Stopped Directory Repository monitoring thread.");
	}
	
	public synchronized Iterable<Contact> getAll() {
		return Collections.unmodifiableCollection(contacts);
	}

	public void filesAdded(File[] addedFiles) {
		log(LogService.LOG_INFO, "----> Adding " + addedFiles.length + " new contact(s).");
		Contact[] newContacts = contactsFromFiles(addedFiles);
		
		synchronized (this) {
			for (Contact contact : newContacts) {
				contacts.add(contact);
			}
		}
		
		for (ContactRepositoryListener listener : listeners) {
			try { listener.contactsAdded(newContacts); } catch (Exception e) { /* Ignore */ }
		}
	}
	
	public void filesRemoved(File[] removedFiles) {
		log(LogService.LOG_INFO, "----> Removing " + removedFiles.length + " deleted contact(s).");
		Contact[] removedContacts = contactsFromFiles(removedFiles);
		
		synchronized (this) {
			for (Contact contact : removedContacts) {
				contacts.remove(contact);
			}
		}
		
		for (ContactRepositoryListener listener : listeners) {
			try { listener.contactsRemoved(removedContacts); } catch (Exception e) { /* Ignore */ }
		}
	}

	private Contact[] contactsFromFiles(File[] addedFiles) {
		Contact[] contacts = new Contact[addedFiles.length];
		for (int i = 0; i < addedFiles.length; i++) {
			contacts[i] = parseFileName(addedFiles[i].getName());
		}
		return contacts;
	}
	private static Contact parseFileName(String name) {
		Contact result;
		
		String main = name.substring(0, name.length() - FILENAME_SUFFIX.length());
		
		int separator = main.indexOf('_');
		if(separator == -1) {
			result = new Contact("", main);
		} else {
			result = new Contact(main.substring(0, separator), main.substring(separator + 1));
		}
		return result;
	}
	
	private void log(int level, String message) {
		try {
			log.log(level, message);
		} catch (Exception e) {
			// Ignore
		}
	}
}
