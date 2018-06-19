package br.com.italobrenner.cursomc.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.italobrenner.cursomc.domain.Cliente;
import br.com.italobrenner.cursomc.repositories.ClienteRepository;
import br.com.italobrenner.cursomc.security.UserSS;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private ClienteRepository clienteRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<Cliente> optCliente = clienteRepository.findByEmail(email);
		if (! optCliente.isPresent()) {
			throw new UsernameNotFoundException(email);
		}
		Cliente cliente = optCliente.get();
		return new UserSS(cliente.getId() , cliente.getEmail(), cliente.getSenha(), cliente.getPerfis());
	}

}
