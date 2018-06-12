package br.com.italobrenner.cursomc.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.italobrenner.cursomc.domain.ItemPedido;
import br.com.italobrenner.cursomc.domain.PagamentoComBoleto;
import br.com.italobrenner.cursomc.domain.Pedido;
import br.com.italobrenner.cursomc.domain.enums.EstadoPagamento;
import br.com.italobrenner.cursomc.repositories.ItemPedidoRepository;
import br.com.italobrenner.cursomc.repositories.PagamentoRepository;
import br.com.italobrenner.cursomc.repositories.PedidoRepository;
import br.com.italobrenner.cursomc.repositories.ProdutoRepository;
import br.com.italobrenner.cursomc.resources.BoletoService;
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
		
	public Pedido find(Integer id) {
		Optional<Pedido> pedido = pedidoRepository.findById(id);
		return pedido.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Pedido.class.getName()));
	}

	@Transactional
	public Pedido insert(Pedido pedido) {
		pedido.setId(null);
		pedido.setInstante(new Date());
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
			ip.setPreco(produtoRepository.findById(ip.getProduto().getId()).get().getPreco());
			ip.setPedido(mPedido);
		}
		itemPedidoRepository.saveAll(pedido.getItens());
		return mPedido;
	}

}
