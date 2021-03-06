package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

class FinalizarLeilaoServiceTest {

    private FinalizarLeilaoService service;
    @Mock
    private LeilaoDao leilaoDao;

    @Mock
    private EnviadorDeEmails enviadorDeEmails;

    @BeforeEach
    public void beforEach() {
        MockitoAnnotations.initMocks(this); // ira iniciar todos @Mock
        this.service = new FinalizarLeilaoService(leilaoDao, enviadorDeEmails);
    }

    @Test
    void naoDeveriaEnviarEmailParaVencedorDoLeilaoEmCasoDeErroAoEncerrarOLeilao() {

        List<Leilao> leiloes = leiloes();

        //Quando for chamado o método buscarLeiloesExpirados retornar leiloes
        Mockito.when(leilaoDao.buscarLeiloesExpirados())
                .thenReturn(leiloes);

        // forçando o lançamento de uma exception quando o método salvar for chamado
        Mockito.when(leilaoDao.salvar(Mockito.any()))
                .thenThrow(RuntimeException.class);

        try {
            // capturando a exception
            service.finalizarLeiloesExpirados();

            // verificar se não houve uma interação com enviadorDeEmails
            Mockito.verifyNoInteractions(enviadorDeEmails);
        }catch (RuntimeException ex){}

    }

    @Test
    void deveriaEnviarEmailParaVencedorDoLeilao() {

        List<Leilao> leiloes = leiloes();

        //Quando for chamado o método buscarLeiloesExpirados retornar leiloes
        Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);

        service.finalizarLeiloesExpirados();

        Leilao leilao = leiloes.get(0);
        Lance lanceVencedor = leilao.getLanceVencedor();

        Mockito.verify(enviadorDeEmails).enviarEmailVencedorLeilao(lanceVencedor);
    }

    @Test
    void test() {
        // LeilaoDao dao = Mockito.mock(LeilaoDao.class);
        //service = new FinalizarLeilaoService(dao);
        List<Leilao> leiloes = leiloes();
        //Quando for chamado o método buscarLeiloesExpirados retornar leiloes
        Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);
        service.finalizarLeiloesExpirados();
        Leilao leilao = leiloes.get(0);
        Assert.assertTrue(leilao.isFechado());

        Mockito.verify(leilaoDao).salvar(leilao);
    }

    private List<Leilao> leiloes() {
        List<Leilao> leiloes = new ArrayList<Leilao>();

        Leilao leilao = new Leilao("Celular",
                new BigDecimal("500"),
                new Usuario("Fulano"));

        Lance primeiro = new Lance(new Usuario("Beltrano"),
                new BigDecimal("600"));

        Lance segundo = new Lance(new Usuario("Ciclano"),
                new BigDecimal("900"));
        leilao.propoe(primeiro);
        leilao.propoe(segundo);
        leiloes.add(leilao);

        return leiloes;
    }
}