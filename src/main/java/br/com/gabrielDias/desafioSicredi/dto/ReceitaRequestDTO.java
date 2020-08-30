package br.com.gabrielDias.desafioSicredi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceitaRequestDTO {
	
	private String agencia;
	
	private String conta;
	
	private Double saldo;
	
	private String status;

}
