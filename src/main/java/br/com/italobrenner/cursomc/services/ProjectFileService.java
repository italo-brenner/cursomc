package br.com.italobrenner.cursomc.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.commons.io.FileUtils;

import br.com.italobrenner.cursomc.services.exception.FileException;

public class ProjectFileService extends AbstractFileService {

	@Override
	public URI uploadFile(InputStream is, String fileName, String contentType) {
		try {
			String filePath = "files";
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
			throw new FileException("IOException");
		}
	}
	
	@Override
	public byte[] downloadFile(String fileName) {
		
		try {
			byte[] image = FileUtils.readFileToByteArray(new File("files" + File.separator + fileName));
			return image;
		} catch (IOException e) {
			throw new FileException("IOException");
		}
	}
	
}
