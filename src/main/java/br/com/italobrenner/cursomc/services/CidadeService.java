package br.com.italobrenner.cursomc.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.italobrenner.cursomc.domain.Cidade;
import br.com.italobrenner.cursomc.repositories.CidadeRepository;

@Service
public class CidadeService {
	
	@Autowired
	private CidadeRepository cidadeRepository;
	
	public List<Cidade> findCidades(Integer estadoId) {
		return cidadeRepository.findCidades(estadoId);
	}
	
}
