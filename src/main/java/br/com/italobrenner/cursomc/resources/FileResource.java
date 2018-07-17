package br.com.italobrenner.cursomc.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.italobrenner.cursomc.services.FileService;

@RestController
@RequestMapping(value="/files")
public class FileResource {
	
	@Autowired
	private FileService fileService;

	@RequestMapping(value="/{fileName}", method=RequestMethod.GET)
	public HttpEntity<byte[]> showFile(@PathVariable String fileName) {
		byte[] image = fileService.downloadFile(fileName);

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.IMAGE_JPEG); 
	    headers.setContentLength(image.length);
	    return new HttpEntity<byte[]>(image, headers);
	}
	
}
