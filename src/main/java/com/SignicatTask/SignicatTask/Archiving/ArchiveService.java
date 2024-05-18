package com.SignicatTask.SignicatTask.Archiving;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;


@Service
//TODO
public class ArchiveService {

    public CompletableFuture<byte[]> archiveFiles(List<File> files) {
        return new CompletableFuture<byte[]>();
    }

}
