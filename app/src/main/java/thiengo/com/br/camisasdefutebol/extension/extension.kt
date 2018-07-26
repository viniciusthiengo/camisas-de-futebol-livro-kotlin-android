package thiengo.com.br.camisasdefutebol.extension

import android.content.Context
import android.support.v4.content.ContextCompat
import android.widget.EditText
import thiengo.com.br.camisasdefutebol.R
import java.util.*

/*
 * O algoritmo de apresentação de preço em formato
 * brasileiro foi colocado em um método de extensão
 * de Float, pois este algoritmo será utilizado em
 * mais de um trecho de todo o projeto, assim
 * evitamos a repetição de código.
 * */
fun Float.priceBRFormat( moneySign: String ) =
    /*
     * A vírgula em "%s %,.2f" juntamente a Locale.GERMANY
     * permite que tenhamos a separação de "milhares" no
     * valor informado, por exemplo: o valor 1459 será
     * apresentado como 1.459,00.
     * */
    String.format(
        Locale.GERMANY,
        "%s %,.2f",
        moneySign,
        this
    )

/*
 * Permite a validação única de campo, EditText, com base
 * no Lambda, literal de função, passado como argumento.
 * */
fun EditText.validation(
    context: Context,
    fieldVerification: ()->Boolean ): Boolean{

    if( fieldVerification() ){
        this.setErrorBorder(context)
        return false
    }
    else{
        this.setNormalBorder(context)
        return true
    }
}

/*
 * Define o drawable edittext_border_and_background (borda
 * cinza) no EditText para indicar que o valor no campo é
 * válido.
 * */
private fun EditText.setNormalBorder( context: Context ){
    this.setBorder(
        context,
        R.drawable.edittext_border_and_background
    )
}

/*
 * Define o drawable edittext_border_and_background_error
 * (borda vermelha) no EditText para indicar que o valor no
 * campo é inválido.
 * */
private fun EditText.setErrorBorder( context: Context ){
    this.setBorder(
        context,
        R.drawable.edittext_border_and_background_error
    )
}

/*
 * Para evitar repetição de código, o método abaixo é o que
 * realmente modifica a borda do EditText alvo. Ele é
 * utilizado nos dois métodos anteriores de configuração de
 * borda.
 * */
private fun EditText.setBorder( context: Context, drawableId: Int ){
    this.background = ContextCompat
        .getDrawable(
            context,
            drawableId
        )
}