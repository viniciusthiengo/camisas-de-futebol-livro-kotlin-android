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
import android.widget.TextView
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.activity_shirts.*
import thiengo.com.br.camisasdefutebol.data.Database
import thiengo.com.br.camisasdefutebol.extension.priceBRFormat
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
         * indica que um null pode ser retornado, mesmo como nós
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_shirts, menu)
        return true
    }

    /*
     * Método nativo de seleção de item de menu de topo,
     * aqui com o proósito de invocar a caixa de diálogo
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
                    .onPositive(this)
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
         * uma funcão. Abaixo estamos realizando uma operação
         * com todos os valores em shrts: somando todos os
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
     * apresenta um progresso indeterminado. Assim simulamos
     * que algo de pesado está sendo processado no background
     * do aplicativo.
     * */
    override fun onClick( dialog: MaterialDialog, which: DialogAction ) {
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

    private fun callDialogFinishBuy(){

        /*
         * SpannableStringBuilder e SpannableString estão
         * sendo utilizados para que seja possível a
         * customização de String sem a necessidade de
         * trabalho com CustomView. Mais sobre SpannedString
         * no link a seguir:
         * https://www.thiengo.com.br/como-utilizar-spannable-no-android-para-customizar-strings
         * */
        val spanContent = SpannableStringBuilder(getString(R.string.pay_done_content))
        val spanOrderLabel = SpannableString(getString(R.string.pay_done_order_label))
        val spanOrderNumber = SpannableString( orderCodeGenerator() )

        spanOrderLabel.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            spanOrderLabel.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spanOrderNumber.setSpan(
            ForegroundColorSpan( ContextCompat.getColor(this, R.color.colorPrice) ),
            0,
            spanOrderNumber.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spanContent.append(spanOrderLabel)
        spanContent.append(spanOrderNumber)

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

    /*
     * Simulador de código de compra realizada. O "+ 10000000"
     * é necessário, pois Random().nextInt(10000000) pode gerar
     * qualquer valor entre 0 e 9999999. Com o "+ 10000000"
     * garantimos os 8 digitos de código de compra.
     * */
    private fun orderCodeGenerator(): String{
        val code = Random().nextInt(10000000) + 10000000
        return String.format(" %d", code)
    }
}
