package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Pagamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
public class GeradorDePagamento {

    private PagamentoDao pagamentos;

    @Autowired
    public GeradorDePagamento(PagamentoDao pagamentos) {
        this.pagamentos = pagamentos;
    }

    public void gerarPagamento(Lance lanceVencedor) {
        LocalDate vencimento = LocalDate.now().plusDays(1);
        Pagamento pagamento = new Pagamento(lanceVencedor, proximoDiaUtil(vencimento));
        this.pagamentos.salvar(pagamento);
    }

    private LocalDate proximoDiaUtil(LocalDate dataBase) {
        DayOfWeek diaDaSemana = dataBase.getDayOfWeek();
        if (diaDaSemana == DayOfWeek.SATURDAY) {
            return dataBase.plusDays(2);
        } else if (diaDaSemana == DayOfWeek.SUNDAY) {
            return dataBase.plusDays(1);
        }
        return dataBase;
    }
}