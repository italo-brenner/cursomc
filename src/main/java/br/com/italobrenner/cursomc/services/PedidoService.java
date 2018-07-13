package br.com.italobrenner.cursomc.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.italobrenner.cursomc.domain.Cliente;
import br.com.italobrenner.cursomc.domain.ItemPedido;
import br.com.italobrenner.cursomc.domain.PagamentoComBoleto;
import br.com.italobrenner.cursomc.domain.Pedido;
import br.com.italobrenner.cursomc.domain.enums.EstadoPagamento;
import br.com.italobrenner.cursomc.repositories.ClienteRepository;
import br.com.italobrenner.cursomc.repositories.ItemPedidoRepository;
import br.com.italobrenner.cursomc.repositories.PagamentoRepository;
import br.com.italobrenner.cursomc.repositories.PedidoRepository;
import br.com.italobrenner.cursomc.repositories.ProdutoRepository;
import br.com.italobrenner.cursomc.security.UserSS;
import br.com.italobrenner.cursomc.services.exception.AuthorizationException;
import br.com.italobrenner.cursomc.services.exception.ObjectNotFoundException;

@Service
public class PedidoService {
	
	@Autowired
	private BoletoService boletoService;
	
	@Autowired
	private PedidoRepository pedidoRepository;
	
	@Autowired
	private PagamentoRepository pagamentoRepository;
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private ItemPedidoRepository itemPedidoRepository;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private EmailService emailService;
	
	public Pedido find(Integer id) {
		Optional<Pedido> pedido = pedidoRepository.findById(id);
		return pedido.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Pedido.class.getName()));
	}

	@Transactional
	public Pedido insert(Pedido pedido) {
		pedido.setId(null);
		pedido.setInstante(new Date());
		pedido.setCliente(clienteRepository.findById(pedido.getCliente().getId()).get());
		pedido.getPagamento().setEstadoPagamento(EstadoPagamento.PENDENTE);
		pedido.getPagamento().setPedido(pedido);
		if (pedido.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pagamentoComBoleto = (PagamentoComBoleto) pedido.getPagamento();
			boletoService.preencherPagamentoComBoleto(pagamentoComBoleto, pedido.getInstante());
		}
		Pedido mPedido = pedidoRepository.save(pedido);
		pagamentoRepository.save(mPedido.getPagamento());
		for (ItemPedido ip : mPedido.getItens()) {
			ip.setDesconto(0.);
			ip.setProduto(produtoRepository.findById(ip.getProduto().getId()).get());
			ip.setPreco(produtoRepository.findById(ip.getProduto().getId()).get().getPreco());
			ip.setPedido(mPedido);
		}
		itemPedidoRepository.saveAll(mPedido.getItens());
		emailService.sendOrderConfirmationHtmlEmail(mPedido);
		return mPedido;
	}
	
	public Page<Pedido> findPage(Integer page, Integer linesPerPages, String orderBy, String direction) {
		UserSS user = UserService.authenticated();
		if (user == null) {
			throw new AuthorizationException("Acesso negado");
		}
		PageRequest pageRequest = PageRequest.of(page, linesPerPages, Direction.valueOf(direction), orderBy);
		Optional<Cliente> optCliente = clienteRepository.findById(user.getId());
		Cliente cliente = optCliente.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! Id: " + user.getId() + ", Tipo: " + Cliente.class.getName()));
		return pedidoRepository.findByCliente(cliente, pageRequest);
	}

}
