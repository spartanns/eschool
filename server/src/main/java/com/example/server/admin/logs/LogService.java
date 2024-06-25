package com.example.server.admin.logs;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service @RequiredArgsConstructor
public class LogService {
    private final LogRepository repository;

    public Log saveLog(MultipartFile file) throws Exception {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (filename.contains("..")) {
                throw new Exception("File name contains invalid path sequence " + filename);
            }

            Log log = new Log(filename, file.getContentType(), file.getBytes());

            return repository.save(log);
        } catch (Exception e) {
            throw new Exception("Could not save file: " + filename);
        }
    }

    public Log readLog(Long id) {
        return repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("File not found."));
    }
}
