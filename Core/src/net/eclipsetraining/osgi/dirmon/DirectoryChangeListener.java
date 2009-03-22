package net.eclipsetraining.osgi.dirmon;

import java.io.File;

public interface DirectoryChangeListener {
	void filesAdded(File[] addedFiles);
	void filesRemoved(File[] removedFiles);
}
