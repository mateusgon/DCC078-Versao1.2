package action;

import PadraoComposite.Bebida;
import PadraoComposite.Combo;
import PadraoComposite.ItemDeVenda;
import PadraoComposite.PratoDeEntrada;
import PadraoComposite.PratoPrincipal;
import PadraoComposite.Sobremesa;
import controller.Action;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Produto;
import persistence.ComboDAO;
import persistence.ProdutoDAO;

public class CadastrarComboPostAction implements Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Integer idRestaurante = Integer.parseInt(request.getParameter("idRest"));
        String produtos[] = request.getParameterValues("produtos");
        String combos[] = request.getParameterValues("combos");
        String nome = request.getParameter("nome");
        Double valor = Double.parseDouble(request.getParameter("valor"));

        Boolean cadastrouItem = false;
        List<ItemDeVenda> itens = new ArrayList<>();
        ItemDeVenda combo = new Combo();
        combo = combo.setCodigo(-1).setNome(nome).setValor(valor).setRestaurantecod(idRestaurante).setDificuldade(0);

        if (produtos != null && produtos.length > 0) {
            for (int i = 0; i < produtos.length; i = i + 2) {
                if (Integer.parseInt(produtos[i + 1]) != 0) {
                    cadastrouItem = true;
                    Produto produto = ProdutoDAO.getInstance().listProduto(Integer.parseInt(produtos[i]));
                    switch (produto.getTipoItem()) {
                        case 1:
                            ItemDeVenda entrada = new PratoDeEntrada();
                            entrada = entrada.setCodigo(produto.getProdutocod()).setNome(produto.getNome()).setValor(produto.getValor()).setDificuldade(produto.getDificuldade()).setRestaurantecod(idRestaurante).setAtivado(1).setQuantidade(Integer.parseInt(produtos[i + 1]));
                            combo.adicionar(entrada);
                            break;
                        case 2:
                            ItemDeVenda principal = new PratoPrincipal();
                            principal = principal.setCodigo(produto.getProdutocod()).setNome(produto.getNome()).setValor(produto.getValor()).setDificuldade(produto.getDificuldade()).setRestaurantecod(idRestaurante).setAtivado(1).setQuantidade(Integer.parseInt(produtos[i + 1]));
                            combo.adicionar(principal);
                            break;
                        case 3:
                            ItemDeVenda bebida = new Bebida();
                            bebida = bebida.setCodigo(produto.getProdutocod()).setNome(produto.getNome()).setValor(produto.getValor()).setDificuldade(produto.getDificuldade()).setRestaurantecod(idRestaurante).setAtivado(1).setQuantidade(Integer.parseInt(produtos[i + 1]));
                            combo.adicionar(bebida);
                            break;
                        case 4:
                            ItemDeVenda sobremesa = new Sobremesa();
                            sobremesa = sobremesa.setCodigo(produto.getProdutocod()).setNome(produto.getNome()).setValor(produto.getValor()).setDificuldade(produto.getDificuldade()).setRestaurantecod(idRestaurante).setAtivado(1).setQuantidade(Integer.parseInt(produtos[i + 1]));
                            combo.adicionar(sobremesa);
                            break;
                        default:
                            break;
                    }
                    if (produto.getDificuldade() > combo.getDificuldade()) {
                        combo.setDificuldade(produto.getDificuldade());
                    }
                }
            }
        }
        if (cadastrouItem) {
            ComboDAO.getInstance().saveCombo(combo);
            ComboDAO.getInstance().saveComboProduto(combo);
            combo.setItens(new ArrayList<ItemDeVenda>());
        }

        Boolean cadastrouCombo = false;
        Boolean trocouDificuldade = false;

        if (combos != null && combos.length > 0) {
            for (int i = 0; i < combos.length - 1; i = i + 1) {
                ItemDeVenda c = new Combo();

                if (Integer.parseInt(combos[i + 1]) != 0) {
                    cadastrouCombo = true;
                    ComboDAO.getInstance().searchComboEspecifico(Integer.parseInt(combos[i]), c);
                    c.setQuantidade(Integer.parseInt(combos[i + 1]));
                    combo.adicionar(c);
                }

            }
        }

        if (trocouDificuldade) {
            ComboDAO.getInstance().updateDificuldade(combo.getCodigo(), combo.getDificuldade());
        }

        if (cadastrouCombo && !cadastrouItem) {
            ComboDAO.getInstance().saveCombo(combo);
            ComboDAO.getInstance().saveComboDeCombo(combo);
        } else if (cadastrouCombo) {
            ComboDAO.getInstance().saveComboDeCombo(combo);
        }

        if (cadastrouItem || cadastrouCombo) {
            request.setAttribute("idRest", idRestaurante);
            RequestDispatcher dispatcher = request.getRequestDispatcher("acesso-restrito-superusuario-restaurante.jsp");
            dispatcher.forward(request, response);
        } else {

        }
    }
}
