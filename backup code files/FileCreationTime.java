package com.nishant.code;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public class FileCreationTime {

  public static FileTime getCreationTime(File file) throws IOException {
    Path p = Paths.get(file.getAbsolutePath());
    BasicFileAttributes view
        = Files.getFileAttributeView(p, BasicFileAttributeView.class)
                    .readAttributes();
    FileTime fileTime=view.creationTime();
    //  also available view.lastAccessTine and view.lastModifiedTime
    return fileTime;
  }

}