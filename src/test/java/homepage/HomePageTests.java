package homepage;

//Importacoes Hamcrest
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import base.BaseTests;
import pages.CarrinhoPage;
import pages.CheckoutPage;
import pages.LoginPage;
import pages.ModalProdutoPage;
import pages.PedidoPage;
import pages.ProdutoPage;
import util.Funcoes;

public class HomePageTests extends BaseTests {

	@Test
	public void testContarProdutos_oitoProdutosDiferentes() {
		carregarPaginaInicial();
		assertThat(homePage.contarProdutos(), is(8));
	}

	@Test
	public void testValidarCarrinhoZerado_ZeroItensNoCarrinho() {
		int produtosNoCarrinho = homePage.obterQuantidadeProdutosNoCarrinho();
		assertThat(produtosNoCarrinho, is(0));
	}

	ProdutoPage produtoPage;
	String nomeProduto_ProdutoPage;

	@Test
	public void testValidarDetalhesDoProduto_DescrioEValorIguais() {
		int indice = 0;
		String nomeProduto_HomePage = homePage.obterNomeProduto(indice);
		String precoProduto_HomePage = homePage.obterPrecoProduto(indice);

		System.out.println(nomeProduto_HomePage);
		System.out.println(precoProduto_HomePage);

		produtoPage = homePage.clicarProduto(indice);

		nomeProduto_ProdutoPage = produtoPage.obterNomeProduto();
		String precoProduto_ProdutoPage = produtoPage.obterPrecoProduto();

		System.out.println(nomeProduto_ProdutoPage);
		System.out.println(precoProduto_ProdutoPage);

		assertThat(nomeProduto_HomePage.toUpperCase(), is(nomeProduto_ProdutoPage.toUpperCase()));
		assertThat(precoProduto_HomePage, is(precoProduto_ProdutoPage));
	}

	LoginPage loginPage;

	@Test
	public void testLoginComSucesso_UsuarioLogado() {
		// Clicar no bot�o Sign In na home page
		loginPage = homePage.clicarBotaoSignIn();

		// Preencher usu�rio e senha
		loginPage.preencherEmail("marcelo@teste.com");
		loginPage.preencherPassword("marcelo");

		// Clicar no bot�o Sign In para logar
		loginPage.clicarBotaoSignIn();

		// Validar se o usu�rio est� logado de fato
		assertThat(homePage.estaLogado("Marcelo Bittencourt"), is(true));

		carregarPaginaInicial();

	}

	@ParameterizedTest
	@CsvFileSource(resources = "/massaTeste_Login.csv", numLinesToSkip = 1, delimiter = ';')
	public void testLogin_UsuarioLogadoComDadosValidos(String nomeTeste, String email, String password,
			String nomeUsuario, String resultado) {
		// Clicar no bot�o Sign In na home page
		loginPage = homePage.clicarBotaoSignIn();

		// Preencher usu�rio e senha
		loginPage.preencherEmail(email);
		loginPage.preencherPassword(password);

		// Clicar no bot�o Sign In para logar
		loginPage.clicarBotaoSignIn();

		boolean esperado_loginOK;
		if (resultado.equals("positivo"))
			esperado_loginOK = true;
		else
			esperado_loginOK = false;

		// Validar se o usu�rio est� logado de fato
		assertThat(homePage.estaLogado(nomeUsuario), is(esperado_loginOK));

		capturarTela(nomeTeste, resultado);

		if (esperado_loginOK)
			homePage.clicarBotaoSignOut();

		carregarPaginaInicial();
	}

	ModalProdutoPage modalProdutoPage;

	@Test
	public void testIncluirProdutoNoCarrinho_ProdutoIncluidoComSucesso() {
		
		String tamanhoProduto = "M";
		String corProduto = "Black";
		int quantidadeProduto = 2;
		
		//--Pr�-condi��o
		//usu�rio logado
		if(!homePage.estaLogado("Marcelo Bittencourt")) {
			testLoginComSucesso_UsuarioLogado();
		}
		
		//--Teste
		//Selecionando produto
		testValidarDetalhesDoProduto_DescrioEValorIguais();
		
		//Selecionar tamanho
		List<String> listaOpcoes  = produtoPage.obterOpcoesSelecionadas();
		
		System.out.println(listaOpcoes.get(0));
		System.out.println("Tamanho da lista: " + listaOpcoes.size());
		
		produtoPage.selecionarOpcaoDropDown(tamanhoProduto);
		
		listaOpcoes  = produtoPage.obterOpcoesSelecionadas();
		
		System.out.println(listaOpcoes.get(0));
		System.out.println("Tamanho da lista: " + listaOpcoes.size());
		
		//Selecionar cor
		produtoPage.selecionarCorPreta();
		
		//Selecionar quantidade
		produtoPage.alterarQuantidade(quantidadeProduto);
		
		//Adicionar no carrinho
		modalProdutoPage = produtoPage.clicarBotaoAddToCart();
		
		//valida��es
		assertThat(modalProdutoPage.obterMensagemProdutoAdicionado()
				.endsWith("Product successfully added to your shopping cart"), is(true));
		
		System.out.println();

		assertThat(modalProdutoPage.obterDescricaoProduto().toUpperCase(), is(nomeProduto_ProdutoPage.toUpperCase()));
//		assertEquals((nomeProduto_ProdutoPage.toUpperCase()), modalProdutoPage.obterDescricaoProduto().toUpperCase());

		/*
		String precoProdutoString = modalProdutoPage.obterPrecoProduto();
		precoProdutoString = precoProdutoString.replace("$", "");
		Double precoProduto = Double.parseDouble(precoProdutoString);
		 */
//		String precoProdutoString = modalProdutoPage.obterPrecoProduto();
//		precoProdutoString = precoProdutoString.replace("$", "");
		Double precoProduto = Double.parseDouble(modalProdutoPage.obterPrecoProduto().replace("$", ""));


		assertThat(modalProdutoPage.obterTamanhoProduto(), is(tamanhoProduto));
		assertThat(modalProdutoPage.obterCorProduto(), is(corProduto));
		assertThat(modalProdutoPage.obterQuantidadeProduto(), is(Integer.toString(quantidadeProduto)));

//		assertEquals(tamanhoProduto, modalProdutoPage.obterTamanhoProduto());
//		assertEquals(corProduto, modalProdutoPage.obterCorProduto());
//		assertEquals(Integer.toString(quantidadeProduto), modalProdutoPage.obterQuantidadeProduto());

		String subtotalString = modalProdutoPage.obterSubtotal();
		subtotalString = subtotalString.replace("$", "");
		Double subtotal = Double.parseDouble(subtotalString);

		Double subtotalCalculado = quantidadeProduto * precoProduto;

		assertThat(subtotal, is(subtotalCalculado));
//		assertEquals(subtotalCalculado, subtotal);

	}


	// Valores esperados

	String esperado_nomeProduto = "Hummingbird printed t-shirt";
	Double esperado_precoProduto = 19.12;
	String esperado_tamanhoProduto = "M";
	String esperado_corProduto = "Black";
	int esperado_input_quantidadeProduto = 2;
	Double esperado_subtotalProduto = esperado_precoProduto * esperado_input_quantidadeProduto;

	int esperado_numeroItensTotal = esperado_input_quantidadeProduto;
	Double esperado_subtotalTotal = esperado_subtotalProduto;
	Double esperado_shippingTotal = 7.00;
	Double esperado_totalTaxExclTotal = esperado_subtotalProduto + esperado_shippingTotal;
	Double esperado_totalTaxIncTotal = esperado_totalTaxExclTotal;
	Double esperado_taxesTotal = 0.00;

	String esperado_nomeCliente = "Marcelo Bittencourt";

	CarrinhoPage carrinhoPage;

	@Test
	public void TestIrParaCarrinho_InformacoesPersistidas() {
		// --Pr�-condi��es
		// Produto inclu�do na tela ModalProdutoPage
		testIncluirProdutoNoCarrinho_ProdutoIncluidoComSucesso();

		carrinhoPage = modalProdutoPage.clicarBotaoProceedToCheckout();

		// Teste

		// validar todos elementos da tela
		System.out.println("***TELA DO CARRINHO***");

		System.out.println(carrinhoPage.obter_nomeProduto());
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_precoProduto()));
		System.out.println(carrinhoPage.obter_tamanhoProduto());
		System.out.println(carrinhoPage.obter_corProduto());
		System.out.println(carrinhoPage.obter_input_quantidadeProduto());
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalProduto()));

		System.out.println("***ITENS DE TOTAIS***");

		System.out.println(Funcoes.removeTextoItemsDevolveInt(carrinhoPage.obter_numeroItensTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_shippingTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxExclTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxIncTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_taxesTotal()));

		// Asser��es Hamcrest
		assertThat(carrinhoPage.obter_nomeProduto(), is(esperado_nomeProduto));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_precoProduto()), is(esperado_precoProduto));
		assertThat(carrinhoPage.obter_tamanhoProduto(), is(esperado_tamanhoProduto));
		assertThat(carrinhoPage.obter_corProduto(), is(esperado_corProduto));
		assertThat(Integer.parseInt(carrinhoPage.obter_input_quantidadeProduto()),
				is(esperado_input_quantidadeProduto));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalProduto()),
				is(esperado_subtotalProduto));

		assertThat(Funcoes.removeTextoItemsDevolveInt(carrinhoPage.obter_numeroItensTotal()),
				is(esperado_numeroItensTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalTotal()), is(esperado_subtotalTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_shippingTotal()), is(esperado_shippingTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxExclTotal()),
				is(esperado_totalTaxExclTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxIncTotal()),
				is(esperado_totalTaxIncTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_taxesTotal()), is(esperado_taxesTotal));

		// Asser��e JUnit
		/*
		 * assertEquals(carrinhoPage.obter_nomeProduto(), is(esperado_nomeProduto));
		 * assertEquals(Funcoes.removeCifraoDevolveDouble(carrinhoPage.
		 * obter_precoProduto()), is(esperado_precoProduto));
		 * assertEquals(carrinhoPage.obter_tamanhoProduto(),
		 * is(esperado_tamanhoProduto)); assertEquals(carrinhoPage.obter_corProduto(),
		 * is(esperado_corProduto));
		 * assertEquals(Integer.parseInt(carrinhoPage.obter_input_quantidadeProduto()),
		 * is(esperado_input_quantidadeProduto));
		 * assertEquals(Funcoes.removeCifraoDevolveDouble(carrinhoPage.
		 * obter_subtotalProduto()), is(esperado_subtotalProduto));
		 * 
		 * assertEquals(Funcoes.removeTextoItemsDevolveInt(carrinhoPage.
		 * obter_numeroItensTotal()), is(esperado_numeroItensTotal));
		 * assertEquals(Funcoes.removeCifraoDevolveDouble(carrinhoPage.
		 * obter_subtotalTotal()), is(esperado_subtotalTotal));
		 * assertEquals(Funcoes.removeCifraoDevolveDouble(carrinhoPage.
		 * obter_shippingTotal()), is(esperado_shippingTotal));
		 * assertEquals(Funcoes.removeCifraoDevolveDouble(carrinhoPage.
		 * obter_totalTaxExclTotal()), is(esperado_totalTaxExclTotal));
		 * assertEquals(Funcoes.removeCifraoDevolveDouble(carrinhoPage.
		 * obter_totalTaxIncTotal()), is(esperado_totalTaxIncTotal));
		 * assertEquals(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_taxesTotal(
		 * )), is(esperado_taxesTotal));
		 */
	}

	CheckoutPage checkoutPage;

	@Test
	public void TestIrParaCheckout_FreteMeioPagamentoEnderecoListadosOk() {
		// Pr�-condi��es

		// Produto dispon�vel no carrinho de compras
		TestIrParaCarrinho_InformacoesPersistidas();

		// Teste

		// Clicar no bot�o
		checkoutPage = carrinhoPage.clicarBotaoProceedToCheckout();

		// Preencher Informa��es

		// Validar Informa��es na Tela
		assertThat(Funcoes.removeCifraoDevolveDouble(checkoutPage.obter_totalTaxIncTotal()),
				is(esperado_totalTaxIncTotal));
		// assertThat(checkoutPage.obter_nomeCliente(), is(esperado_nomeCliente));
		assertTrue(checkoutPage.obter_nomeCliente().startsWith(esperado_nomeCliente));

		checkoutPage.clicarBotaoContinueAddress();

		String encontrado_shippingValor = checkoutPage.obter_shippingValor();
		encontrado_shippingValor = Funcoes.removeTexto(encontrado_shippingValor, " tax excl.");
		Double encontrado_shippingValor_Double = Funcoes.removeCifraoDevolveDouble(encontrado_shippingValor);

		assertThat(encontrado_shippingValor_Double, is(esperado_shippingTotal));

		checkoutPage.clicarBotaoContinueShipping();

		// Selecionar op��es "Pay by check"
		checkoutPage.selecionarRadioPayByCheck();

		// Validar valor do cheque(amount)
		String encontrado_amountPayByCheck = checkoutPage.obter_amountPayByCheck();
		encontrado_amountPayByCheck = Funcoes.removeTexto(encontrado_amountPayByCheck, " (tax incl.)");
		Double encontrado_amountPayByCheck_Double = Funcoes.removeCifraoDevolveDouble(encontrado_amountPayByCheck);

		assertThat(encontrado_amountPayByCheck_Double, is(esperado_totalTaxIncTotal));

		// Clicar na op��o "I agree"
		checkoutPage.selecionarCheckboxIAgree();

		assertTrue(checkoutPage.estaSelecionadoCheckboxIAgree());
	}

	@Test
	public void TestFinalizarPedido_pedidoFinalizadoComSucesso() {
		// Pr�-condi��es
		// Checkout completamente concluido
		TestIrParaCheckout_FreteMeioPagamentoEnderecoListadosOk();

		// Teste
		// Clicar no bot�o para confirmar o pedido
		PedidoPage pedidoPage = checkoutPage.clicarBotaoConfirmaPedido();

		// Validar valores da tela
		assertTrue(pedidoPage.obter_textoPedidoConfirmado().endsWith("YOUR ORDER IS CONFIRMED"));
		// assertThat(pedidoPage.obter_textoPedidoConfirmado().toUpperCase(), is("YOUR
		// ORDER IS CONFIRMED"));

		assertThat(pedidoPage.obter_email(), is("marcelo@teste.com"));

		assertThat(pedidoPage.obter_totalProdutos(), is(esperado_subtotalTotal));

		assertThat(pedidoPage.obter_totalTaxIncl(), is(esperado_totalTaxIncTotal));

		assertThat(pedidoPage.obter_metodoPagamento(), is("check"));

	}

}
