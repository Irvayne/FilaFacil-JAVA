package br.ufpi.lost.controller;

import java.util.List;

import javax.inject.Inject;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Result;
import br.ufpi.lost.annotation.Permission;
import br.ufpi.lost.dao.FilaDAO;
import br.ufpi.lost.dao.PontoDeAtendimentoDAO;
import br.ufpi.lost.model.Empresa;
import br.ufpi.lost.model.Fila;
import br.ufpi.lost.model.PontoDeAtendimento;
import br.ufpi.lost.model.UsuarioLogado;

@Controller
public class FilaController {
	
	@Inject private UsuarioLogado usuarioLogado;
	@Inject private Result result;
	@Inject private FilaDAO dao;
	@Inject private PontoDeAtendimentoDAO pontoDeAtendimentoDAO;
	
	@Permission
	@Get("/fila/telaInicial")
	/**
	 * Carrega a tela inicial
	 */
	public void telaInicial() {
		Empresa empresa = usuarioLogado.getUsuario().getEmpresa();
		List<Fila> filas = dao.listarFilasPorEmpresa(empresa.getId());
		result.include("filas", filas);
	}
	
	@Permission
	@Get
	public void adicionar() {
		
	}
	
	@Permission
	@Post("/fila/adicionar")
	/**
	 * Adicionar fila a empresa. O metodo recebe a fila como parametro.
	 * @param fila
	 */
	public void adicionar(Fila fila) {
		
		fila.setEmpresa(usuarioLogado.getUsuario().getEmpresa());

		dao.save(fila);
		
		result.include("mensagem", "Fila adicionada com sucesso");
		result.redirectTo(this).telaInicial();
	}
	
	@Permission
	@Get
	/**
	 * Metodo lista as filas por empresas.
	 */
	public void associacoes() {
		List<Fila> filas = dao.listarFilasPorEmpresa(usuarioLogado.getUsuario().getEmpresa().getId());
		
		result.include("filas", filas);
	}
	
	@Permission
	@Get
	/**
	 * Carrega as filas e os pontos de atendimento
	 */
	public void associar() {
		List<Fila> filas = dao.listarFilasPorEmpresa(usuarioLogado.getUsuario().getEmpresa().getId());
		List<PontoDeAtendimento> listarPorEmpresa = pontoDeAtendimentoDAO.listarPorEmpresa(usuarioLogado.getUsuario().getEmpresa().getId());
		
		result.include("pontos", listarPorEmpresa);
		result.include("filas", filas);
	}
	
	@Permission
	@Post
	/**
	 * Metodo associa a uma fila a variso pontos de atendimento. O metodo recebe a fila a ser adicionada e a lista de pontos de atendimento
	 * @param idFila
	 * @param pontoDeAtendimentos
	 */
	public void associar(long idFila, List<Long> pontoDeAtendimentos) {
		Fila findById = dao.findById(idFila);
		
		for (Long ponto : pontoDeAtendimentos) {
			
			PontoDeAtendimento pontoDeAtendimento = pontoDeAtendimentoDAO.findById(ponto);
			pontoDeAtendimento.getFilas().add(findById);
			
			pontoDeAtendimentoDAO.save(pontoDeAtendimento);
		}
		result.redirectTo(this).associacoes();
	}
	
	

}
