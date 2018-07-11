package br.com.italobrenner.cursomc.services;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.italobrenner.cursomc.domain.Cidade;
import br.com.italobrenner.cursomc.domain.Cliente;
import br.com.italobrenner.cursomc.domain.Endereco;
import br.com.italobrenner.cursomc.domain.enums.Perfil;
import br.com.italobrenner.cursomc.domain.enums.TipoCliente;
import br.com.italobrenner.cursomc.dto.ClienteDTO;
import br.com.italobrenner.cursomc.dto.ClienteNewDTO;
import br.com.italobrenner.cursomc.repositories.ClienteRepository;
import br.com.italobrenner.cursomc.repositories.EnderecoRepository;
import br.com.italobrenner.cursomc.security.UserSS;
import br.com.italobrenner.cursomc.services.exception.AuthorizationException;
import br.com.italobrenner.cursomc.services.exception.DataIntegrityException;
import br.com.italobrenner.cursomc.services.exception.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private EnderecoRepository enderecoRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private S3Service s3service;

	public Cliente find(Integer id) {
		UserSS user = UserService.authenticated();
		if (user == null || !user.hasRole(Perfil.ADMIN) || !id.equals(user.getId())) {
			throw new AuthorizationException("Acesso negado");
		}
		Optional<Cliente> cliente = clienteRepository.findById(id);
		return cliente.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
	}

	@Transactional
	public Cliente insert(Cliente cliente) {
		cliente.setId(null);
		Cliente mCliente = clienteRepository.save(cliente);
		enderecoRepository.saveAll(mCliente.getEnderecos());
		return mCliente;
	}

	public Cliente update(Cliente cliente) {
		Cliente mCliente = find(cliente.getId());
		updateData(mCliente, cliente);
		return clienteRepository.save(mCliente);
	}

	public void delete(Integer id) {
		find(id);
		try {
			clienteRepository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir uma cliente que tem produtos");
		}
	}

	public List<Cliente> findAll() {
		return clienteRepository.findAll();
	}

	public Page<Cliente> findPage(Integer page, Integer linesPerPages, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPages, Direction.valueOf(direction), orderBy);
		return clienteRepository.findAll(pageRequest);
	}

	public Cliente fromDTO(ClienteDTO clienteDTO) {
		return new Cliente(clienteDTO.getId(), clienteDTO.getNome(), clienteDTO.getEmail(), null, null, null);
	}

	public Cliente fromDTO(ClienteNewDTO clienteNewDTO) {
		Cliente cliente = new Cliente(null, clienteNewDTO.getNome(), clienteNewDTO.getEmail(),
				clienteNewDTO.getCpfOuCnpj(), TipoCliente.toEnum(clienteNewDTO.getTipoCliente()),
				passwordEncoder.encode(clienteNewDTO.getSenha()));
		Cidade cidade = new Cidade(clienteNewDTO.getCidadeId(), null, null);
		Endereco endereco = new Endereco(null, clienteNewDTO.getLogradouro(), clienteNewDTO.getNumero(),
				clienteNewDTO.getComplemento(), clienteNewDTO.getBairro(), clienteNewDTO.getCep(), cliente, cidade);
		cliente.getEnderecos().add(endereco);
		cliente.getTelefones().add(clienteNewDTO.getTelefone1());
		if (clienteNewDTO.getTelefone2() != null) {
			cliente.getTelefones().add(clienteNewDTO.getTelefone2());
		}
		if (clienteNewDTO.getTelefone3() != null) {
			cliente.getTelefones().add(clienteNewDTO.getTelefone3());
		}
		return cliente;
	}

	private void updateData(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
	}

	public URI uploadProfilePicture(MultipartFile multipartFile) {
		UserSS user = UserService.authenticated();
		if (user == null) {
			throw new AuthorizationException("Acesso negado");
		}

		URI uri = s3service.uploadFile(multipartFile);

		Optional<Cliente> optCli = clienteRepository.findById(user.getId());

		Cliente cli = optCli.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + user.getId() + ", Tipo: " + Cliente.class.getName()));
		
		cli.setImageUrl(uri.toString());
		clienteRepository.save(cli);
		
		return uri;
	}

}
