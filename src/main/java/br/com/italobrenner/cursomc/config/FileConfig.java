package br.com.italobrenner.cursomc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.italobrenner.cursomc.services.FileService;
import br.com.italobrenner.cursomc.services.ProjectFileService;
import br.com.italobrenner.cursomc.services.S3Service;

@Configuration
public class FileConfig {
	
	@Value("${file.strategy}")
	private String fileStrategy;
	
	@Bean
	public FileService getFileStrategy() {
		if ("project".equals(fileStrategy)) {
			return new ProjectFileService();
		} else if ("s3".equals(fileStrategy)) {
			return new S3Service();
		} else {
			throw new IllegalArgumentException("Escolha uma estratégia de arquivo válida");
		}
	}
	
}
