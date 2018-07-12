package br.com.italobrenner.cursomc.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.springframework.web.multipart.MultipartFile;

import br.com.italobrenner.cursomc.services.exception.FileException;

public abstract class AbstractFileService implements FileService {
	
	public URI uploadFile(MultipartFile multipartFile) {
		try {
			String fileName = multipartFile.getOriginalFilename();
			InputStream is;
			is = multipartFile.getInputStream();
			String contentType = multipartFile.getContentType();
			return uploadFile(is, fileName, contentType);
		} catch (IOException e) {
			throw new FileException("Erro de IO: " + e.getMessage());
		}
	}

}
