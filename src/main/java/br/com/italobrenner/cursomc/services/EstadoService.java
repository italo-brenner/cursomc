package br.com.italobrenner.cursomc.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.italobrenner.cursomc.domain.Estado;
import br.com.italobrenner.cursomc.repositories.EstadoRepository;

@Service
public class EstadoService {

	@Autowired
	private EstadoRepository estadoRepository;
	
	public List<Estado> findAllByOrderByNome() {
		return estadoRepository.findAll();
	}
	
}
