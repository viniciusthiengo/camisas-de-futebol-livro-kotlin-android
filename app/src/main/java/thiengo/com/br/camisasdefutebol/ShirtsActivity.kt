package thiengo.com.br.camisasdefutebol

import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.activity_shirts.*
import thiengo.com.br.camisasdefutebol.data.Database
import thiengo.com.br.camisasdefutebol.extension.priceBRFormat
import thiengo.com.br.camisasdefutebol.extension.validation
import thiengo.com.br.camisasdefutebol.util.luhnAlgorithm
import thiengo.com.br.camisasdefutebol.util.orderCodeGenerator
import java.util.*


class ShirtsActivity : AppCompatActivity(),
        DialogInterface.OnShowListener,
        MaterialDialog.SingleButtonCallback {

    private val shirts = Database.getShirts()
    lateinit var dialog: MaterialDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shirts)
        setSupportActionBar(toolbar)

        /*
         * Para a apresentação da "seta de voltar" no
         * topo da atividade, isso para que possamos
         * simular que antes de estar na tela de carrinho
         * de compra o usuário estava em alguma outra
         * tela do aplicativo.
         * */
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initRecycler()
    }

    /*
     * Método responsável por trabalhar a configuração
     * e inicialização do RecyclerView.
     * */
    private fun initRecycler(){
        rv_shirts.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(this)
        rv_shirts.layoutManager = layoutManager

        /*
         * Para que seja possível customizar o divisor do
         * RecyclerView temos de definir um drawable e
         * então carrega-lo via método setDrawable().
         * */
        val divider = DividerItemDecoration(this, layoutManager.orientation )

        /*
         * O método setDrawable() não aceita null como
         * argumento e a assinatura de ContextCompat.getDrawable()
         * indica que um null pode ser retornado, mesmo com nós
         * desenvolvedores sabendo que isso é improvável para
         * a configuração que estamos trabalhando. Devido a
         * exigência de um "não null" em setDrawable() o
         * operador que força a geração de um NullPointerException
         * (o operador !!) caso um valor null seja retornado.
         * */
        divider.setDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.recyclerview_divider
            )!!
        )
        rv_shirts.addItemDecoration( divider )

        rv_shirts.adapter = ShirtsAdapter(this, shirts )
    }

    /*
	 * Permitindo que o menu de topo, com o botão de ícone referente a
	 * "comprar", seja apresentado.
	 * */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_shirts, menu)
        return true
    }

    /*
     * Método nativo de seleção de item de menu de topo,
     * aqui com o propósito de invocar a caixa de diálogo
     * de pagamento.
     * */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if( item!!.itemId == R.id.it_buy ){
            dialog = MaterialDialog
                .Builder(this)
                    .title(R.string.pay_title)
                    .titleColorRes(R.color.colorToolbarItensColor)
                    .customView(R.layout.dialog_payment, true)
                    .positiveText(R.string.pay_positive_button_label)
                    .neutralText(R.string.pay_neutral_button_label)
                    .neutralColorRes(R.color.colorToolbarItensColor)
                    .showListener(this)
                    .onAny(this)
                    .autoDismiss(false)
                    .show()
        }

        return super.onOptionsItemSelected(item)
    }

    /*
     * Atualizando o preço total da compra somente depois
     * de já ter em tela a caixa de diálogo de pagamento.
     * */
    override fun onShow( dialogInterface: DialogInterface? ) {
        var totalPrice = 0.0F

        /*
		 * map é uma função de alto nível no Kotlin que
		 * também nos permite passar por todos os valores
		 * de alguma coleção e então atualiza-los ou apenas
		 * realizar algo que inclua eles, isso utilizando
		 * uma função. Abaixo estamos realizando uma operação
		 * com todos os valores em shirts: somando todos os
		 * preços de camisas em totalPrice.
		 * */
        shirts.map{
            totalPrice += it.price
        }

        val tvTotalPrice= dialog.customView!!.findViewById<TextView>(R.id.tv_total_price)

        tvTotalPrice.text = totalPrice.priceBRFormat(
            getString(R.string.money_sign)
        )
    }

    /*
     * Método responsável por invocar a caixa de diálogo que
     * apresenta um progresso indeterminado (simulando um
     * processamento pesado em background), isso se os campos
     * tiverem sido todos preenchidos corretamente, pois
     * caso contrário os campos com dados inválidos serão
     * destacados em vermelho. Se o neutralButton for
     * acionado, a caixa de diálogo de pagamento será apenas
     * fechada.
     * */
    override fun onClick( dialog: MaterialDialog, which: DialogAction ) {

        if( which == DialogAction.POSITIVE
            && validation( dialog ) ){

            dialog.dismiss()

            val materialDialog = MaterialDialog
                .Builder(this)
                    .title(R.string.pay_process_title)
                    .titleColorRes(R.color.colorToolbarItensColor)
                    .content(R.string.pay_process_content)
                    .progress(true, 0)
                    .cancelable(false)
                    .show()

            callHeavyJobSimulator( materialDialog )
        }
        else if( which == DialogAction.NEUTRAL ){
            dialog.dismiss()
        }
    }

    /*
     * Método que contém o acesso e invocação de validação dos
     * campos de texto, EditTexts, presentes na caixa de diálogo
     * de pagamento.
     * */
    private fun validation( dialog: MaterialDialog ): Boolean{
        val etNumber= dialog.customView!!.findViewById<EditText>(R.id.et_card_number)
        val etCvv= dialog.customView!!.findViewById<EditText>(R.id.et_card_cvv)
        val etName= dialog.customView!!.findViewById<EditText>(R.id.et_card_name)

        val isNumber = etNumber.validation {
            etNumber.text.isEmpty()
            || !luhnAlgorithm( etNumber.text.toString() )
        }
        val isCvv = etCvv.validation { etCvv.text.isEmpty() }
        val isName = etName.validation { etName.text.isEmpty() }

        return isNumber && isCvv && isName
    }

    /*
     * Método que realmente contém o delay (em milissegundos)
     * que indica um pesado processamento em background. Note
     * a importância de se trabalhar com uma Thread de background
     * e também com a volta a Thread UI, isso, pois o
     * MaterialDialog é um componente visual e somente pode ter
     * suas entidades visuais atualizadas quando na Thread
     * principal.
     * */
    private fun callHeavyJobSimulator( materialDialog: MaterialDialog ){
        Thread{
            kotlin.run {
                /*
                 * Parando a execução nesta linha por 2 segundos
                 * (2000 milissegundos).
                 * */
                SystemClock.sleep(2000)

                runOnUiThread {
                    /*
                     * Voltando a Thread principal (UI) para
                     * poder trabalhar com o MaterialDialog.
                     * */
                    materialDialog.dismiss()
                    callDialogFinishBuy()
                }
            }
        }.start()
    }

    /*
     * Responsável por invocar a última caixa de diálogo,
     * com a resposta de "pagamento realizado com sucesso".
     * Em um domínio do problema real, no qual o app irá a
     * produção, teria de haver a validação da resposta do
     * back-end para assim mostrar se o pagamento foi ou
     * não aprovado.
     * */
    private fun callDialogFinishBuy(){
        /*
         * SpannableStringBuilder e SpannableString estão
         * sendo utilizados para que seja possível a
         * customização de String sem a necessidade de
         * trabalho com CustomView. Mais sobre SpannedString
         * no link a seguir:
         *
         * https://www.thiengo.com.br/como-utilizar-spannable-no-android-para-customizar-strings
         * */
        val spanContent = SpannableStringBuilder( getString( R.string.pay_done_content ) )
        val spanOrderLabel = SpannableString( getString( R.string.pay_done_order_label ) )
        val spanOrderNumber = SpannableString( orderCodeGenerator() )

        spanOrderLabel.setSpan(
            StyleSpan( Typeface.BOLD ),
            0,
            spanOrderLabel.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spanOrderNumber.setSpan(
            ForegroundColorSpan( ContextCompat.getColor( this, R.color.colorPrice ) ),
            0,
            spanOrderNumber.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spanContent.append( spanOrderLabel )
        spanContent.append( spanOrderNumber )

        /*
         * Apresentando a caixa de diálogo que informa que o
         * pagamento foi processado com sucesso. Note que a
         * API MaterialDialog aceita SpannedString para
         * configuração visual, ou seja, até imagem pode ser
         * apresentada como conteúdo sem a necessidade de
         * uso de ImageView como customView.
         * */
        MaterialDialog
            .Builder(this)
                .title( R.string.pay_done_title )
                .titleColorRes(R.color.colorToolbarItensColor)
                .content( spanContent )
                .positiveText(R.string.pay_done_button_label)
                .show()
    }
}
