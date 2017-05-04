package com.qy.ftp.support;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by liuzhengqi on 4/5/2017.
 */
@Slf4j
public class TreeCopier implements FileVisitor<Path> {
	private final Path source;
	private final Path target;

	public TreeCopier(Path source, Path target) {
		this.source = source;
		this.target = target;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
		// before visiting entries in a directory we copy the directory
		// (okay if directory already exists).
		CopyOption[] options = new CopyOption[]{COPY_ATTRIBUTES};

		Path newdir = target.resolve(source.relativize(dir));
		try {
			Files.copy(dir, newdir, options);
		} catch (FileAlreadyExistsException x) {
			// ignore
		} catch (IOException x) {
			return SKIP_SUBTREE;
		}
		return CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		copyFile(file, target.resolve(source.relativize(file)));
		return CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
		// fix up modification time of directory when done
		if (exc == null) {
			Path newdir = target.resolve(source.relativize(dir));
			try {
				FileTime time = Files.getLastModifiedTime(dir);
				Files.setLastModifiedTime(newdir, time);
			} catch (IOException x) {
				log.error("Unable to copy all attributes from " + newdir.getFileName(), x);
			}
		}
		return CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) {
		if (exc instanceof FileSystemLoopException) {
			log.error("cycle detected: " + file, exc);
		} else {
			//ignore
		}
		return CONTINUE;
	}

	void copyFile(Path source, Path target) {
		CopyOption[] options = new CopyOption[]{COPY_ATTRIBUTES, REPLACE_EXISTING};
		try {
			Files.copy(source, target, options);
		} catch (IOException x) {
			//log.error("Unable to copy: " + source.getFileName(), x);
		}
		log.info(source.toFile().getAbsoluteFile() + " copied successfully!");
	}
}
