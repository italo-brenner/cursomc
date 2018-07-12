package br.com.italobrenner.cursomc.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.italobrenner.cursomc.services.exception.FileException;

@Service
public class FileService {
	
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

	public URI uploadFile(InputStream is, String fileName, String contentType) {
		try {
			String filePath = "images" ;
			File file = new File(filePath);
			if (! file.exists() ) {
				file.mkdirs();
			}
			FileOutputStream outputStream = new FileOutputStream(new File(filePath + File.separator + fileName));
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = is.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
			outputStream.close();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			throw new FileException("IOException");
		}

	}

}
