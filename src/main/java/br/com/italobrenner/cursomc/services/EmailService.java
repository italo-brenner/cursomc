package br.com.italobrenner.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import br.com.italobrenner.cursomc.domain.Pedido;

public interface EmailService {
	
	void sendOrderConfirmationEmail(Pedido pedido);
	
	void sendEmail(SimpleMailMessage mailMessage);
	
}
