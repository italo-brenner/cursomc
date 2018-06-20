package br.com.italobrenner.cursomc.services;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;

import br.com.italobrenner.cursomc.domain.Cliente;
import br.com.italobrenner.cursomc.domain.Pedido;

public interface EmailService {
	
	void sendOrderConfirmationEmail(Pedido pedido);
	
	void sendEmail(SimpleMailMessage mailMessage);
	
	void sendOrderConfirmationHtmlEmail(Pedido pedido);
	
	void sendHtmlEmail(MimeMessage mimeMessage);

	void sendNewPasswordEmail(Cliente cliente, String newPass);
	
}
