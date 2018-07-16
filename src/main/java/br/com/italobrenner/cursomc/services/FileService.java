package br.com.italobrenner.cursomc.services;

import java.io.InputStream;
import java.net.URI;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
	
	public URI uploadFile(MultipartFile multipartFile);

	public URI uploadFile(InputStream is, String fileName, String contentType);
	
	public byte[] downloadFile(String fileName);

}
