package net.eclipsetraining.osgi.dirmon;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DirectoryMonitor extends Thread {
	
	private final Set<File> monitoredFiles = new HashSet<File>();

	private final File directory;
	private final DirectoryChangeListener listener;
	private final FileFilter fileFilter;
	
	public DirectoryMonitor(File directory, FileFilter fileFilter, DirectoryChangeListener listener) {
		this.directory = directory;
		this.fileFilter = fileFilter;
		this.listener = listener;
	}

	public DirectoryMonitor(File directory, final FilenameFilter filenameFilter, DirectoryChangeListener listener) {
		this.directory = directory;
		this.listener = listener;
		this.fileFilter = new FileFilter(){
			public boolean accept(File pathname) {
				return filenameFilter.accept(pathname.getParentFile(), pathname.getName());
			}
		};
	}
	
	@Override
	public void run() {
		try {
			while (!Thread.interrupted()) {
				// Sleep
				Thread.sleep(2000);
				
				// Scan the directory
				File[] fileList = directory.listFiles(fileFilter);
				Set<File> currentFileSet = new HashSet<File>();
				for (File file : fileList) {
					currentFileSet.add(file);
				}
				
				// Work out which files are new or deleted
				List<File> newFiles = new ArrayList<File>();
				List<File> deletedFiles = new ArrayList<File>();
				synchronized (this) {
					// Deleted files
					for(Iterator<File> iter = monitoredFiles.iterator(); iter.hasNext(); ) {
						File file = iter.next();
						if(!currentFileSet.contains(file)) {
							deletedFiles.add(file);
							iter.remove();
						}
					}
					// Added files
					for (File file : fileList) {
						boolean isNew = monitoredFiles.add(file);
						if(isNew) {
							newFiles.add(file);
						}
					}
					
					// Notify the listener
					if(!newFiles.isEmpty()) {
						listener.filesAdded((File[]) newFiles
								.toArray(new File[newFiles.size()]));
					}
					if(!deletedFiles.isEmpty()) {
						listener.filesRemoved((File[]) deletedFiles
								.toArray(new File[deletedFiles.size()]));
					}
				}
				
			}
		} catch (InterruptedException e) {
			// Ignore
		} finally {
			//listener.logInfo("----> Directory Repository monitor thread exiting", null);
		}
	}
}