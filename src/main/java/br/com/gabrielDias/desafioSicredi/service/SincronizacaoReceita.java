package br.com.gabrielDias.desafioSicredi.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import br.com.gabrielDias.desafioSicredi.dto.ReceitaRequestDTO;
import br.com.gabrielDias.desafioSicredi.sincronizacaoreceita.ReceitaService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SincronizacaoReceita {

	private static final String NOVA_LINHA = "\n";
	private static final String AGENCIA = "agencia";
	private static final String SPLIT = ";";

	public static void main(String[] args) throws InterruptedException, IOException {
		for (String string : args) {
			verificarArquivo(string);
		}
	}

	public static void verificarArquivo(String arquivo) throws InterruptedException, IOException {
		
		log.info("SincronizacaoReceita.verificarArquivo - Start");
		ReceitaService receitaService = new ReceitaService();
		StringBuffer csvReport = new StringBuffer();
		csvReport.append("agencia;conta;saldo;status;atualizado\n");

		try {
			FileReader fileReader = new FileReader(arquivo);
			BufferedReader reader = new BufferedReader(fileReader);
			Stream<String> linhas = reader.lines();

			linhas.forEach(linha -> {
				try {
					String[] split = linha.split(SPLIT);
					if(!AGENCIA.equals(split[0])) {
		
						ReceitaRequestDTO receitaRequest = criarReceitaRequest(split);
						boolean atualizarConta = receitaService.atualizarConta(receitaRequest.getAgencia(),
								receitaRequest.getConta(), receitaRequest.getSaldo(), receitaRequest.getStatus());
	
						csvReport.append(receitaRequest.getAgencia() + SPLIT + receitaRequest.getConta() + SPLIT
							+ receitaRequest.getSaldo() + SPLIT + receitaRequest.getStatus() + SPLIT + atualizarConta + NOVA_LINHA);
					}
				} catch (RuntimeException | InterruptedException e) {
					log.error("SincronizacaoReceita.verificarArquivo - Falha ao atualizar conta - Error: {}",e.getMessage(), e);
				}
			});
			gerarCSV(csvReport);
			fileReader.close();
			reader.close();
		} catch (FileNotFoundException e) {
			log.error("SincronizacaoReceita.verificarArquivo - Arquivo nao encontrado - Error: {}", e.getMessage(), e);
		}
		log.info("SincronizacaoReceita.verificarArquivo - End");

	}

	private static ReceitaRequestDTO criarReceitaRequest(String[] split) {
		ReceitaRequestDTO response = new ReceitaRequestDTO();
		response.setAgencia(split[0]);
		response.setConta(split[1].replace("-", ""));
		response.setSaldo(Double.parseDouble(split[2].replace(",", ".")));
		response.setStatus(split[3]);

		return response;
	}

	public static void gerarCSV(StringBuffer result) {
		try {
			FileOutputStream arquivo = new FileOutputStream("Resposta.csv");
			byte[] contentInBytes = result.toString().getBytes();
			arquivo.write(contentInBytes);
			arquivo.flush();
			if (arquivo != null) {
				arquivo.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
