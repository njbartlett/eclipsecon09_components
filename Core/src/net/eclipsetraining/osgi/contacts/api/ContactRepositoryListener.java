package net.eclipsetraining.osgi.contacts.api;

public interface ContactRepositoryListener {
	void contactsAdded(Contact[] newContacts);
	void contactsRemoved(Contact[] removedContacts);
}
